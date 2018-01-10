package kr.go.kofiu.ctr.util;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 재시도 command
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public abstract class RetryCommand {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RetryCommand.class);

	/**
	 * DEFAULT NUMBER OF RETRIES is 3
	 */
	public static final int DEFAULT_NUMBER_OF_RETRIES = 3;
	
	/**
	 * DEFAULT TIME TO WAIT is 2 seconds
	 */
	public static final long DEFAULT_TIME_TO_WAIT = 2; // seconds

	/**
	 * 재시도 횟수
	 */
	private int NumberOfTriesLeft;
	
	/**
	 *  실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	private long timeToWait;

	/**
	 * 생성자
	 *
	 */
	public RetryCommand()	{
		this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_TIME_TO_WAIT);
	}

	/**
	 * 생성자
	 * @param numberOfRetries 재시도 횟수
	 */
	public RetryCommand(int numberOfRetries)	{
		this(numberOfRetries, DEFAULT_TIME_TO_WAIT);
	}

	/**
	 * 생성자
	 * @param NumberOfTriesLeft 재시도 횟수
	 * @param timeToWait 실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	public RetryCommand(int numberOfRetries, long timeToWait)	{
		this.NumberOfTriesLeft = numberOfRetries;
		this.timeToWait = timeToWait;
	}

	/**
	 * true 일 경우 재시도를 수행하고, false일 경우 재시도를 수행하지 않는다.
	 * @return true 일 경우 재시도를 수행하고, false일 경우 재시도를 수행하지 않는다.
	 */
	public boolean shouldRetry() {
		return (0 < NumberOfTriesLeft);
	}

	/**
	 * 오류 발생 시 재시도 횟수가 남아 있는지를 체크하고,
	 * 재시도 횟수를 초과 하지 않았을 경우에는 다음 시도 까지 잠시 sleep한 뒤에 
	 * 다시 시도한다. 
	 * 재시도 횟수를 초과 했을 경우에는 RetryException을 Throw한다.
	 * @param ex
	 * @throws RetryException
	 */
	public void ExceptionOccurred(Exception ex) throws RetryException 	{
		NumberOfTriesLeft --;
		if (!shouldRetry())	{
			throw new RetryException(ex);
		}
		waitUntilNextTry();
	}

	/**
	 * 다음 재시도를 몇초 후에 할지에 대한 시간 값 반환. 단위 초
	 * @return 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	protected long getTimeToWait()	{
		return timeToWait;
	}
	
	/**
	 * 다음 재시도 시간 까지 Wait 한다.
	 *
	 */
	private void waitUntilNextTry() {
		long timeToWait = getTimeToWait() * 1000 ;
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			// ignored
		}
	}

	/**
	 * 현재 남은 재시도 횟수
	 * @return 현재 남은 재시도 횟수
	 */
	public int getNumberOfTriesLeft() {
		return NumberOfTriesLeft;
	}

	
	/**
	 * Command pattern에서 Command를 수행하는 Proxy 메소드 
	 * @return
	 * @throws RetryException
	 */
	public Object invoke() throws RetryException
	{
		//RetryStrategy strategy = getRetryStrategy();
		while (shouldRetry()) {
			try	{
				return action();
			} catch (Exception e){
				logger.info(this.getClass().getName() + " , 남은 재시도 횟수 : " + getNumberOfTriesLeft() + " " + getTimeToWait()  + "초 뒤에 다시 시도합니다.", e );
				ExceptionOccurred(e);
			}
		}		
		return null;
	}


	/**
	 * 이 클래스를 extends하는 클래스는 이 메소드를 구현해야 한다.
	 * @return
	 * @throws Exception
	 */
	protected abstract Object action() throws Exception; 

}
