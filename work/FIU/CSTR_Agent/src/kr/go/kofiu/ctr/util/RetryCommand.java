package kr.go.kofiu.ctr.util;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ��õ� command
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
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
	 * ��õ� Ƚ��
	 */
	private int NumberOfTriesLeft;
	
	/**
	 *  ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	private long timeToWait;

	/**
	 * ������
	 *
	 */
	public RetryCommand()	{
		this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_TIME_TO_WAIT);
	}

	/**
	 * ������
	 * @param numberOfRetries ��õ� Ƚ��
	 */
	public RetryCommand(int numberOfRetries)	{
		this(numberOfRetries, DEFAULT_TIME_TO_WAIT);
	}

	/**
	 * ������
	 * @param NumberOfTriesLeft ��õ� Ƚ��
	 * @param timeToWait ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	public RetryCommand(int numberOfRetries, long timeToWait)	{
		this.NumberOfTriesLeft = numberOfRetries;
		this.timeToWait = timeToWait;
	}

	/**
	 * true �� ��� ��õ��� �����ϰ�, false�� ��� ��õ��� �������� �ʴ´�.
	 * @return true �� ��� ��õ��� �����ϰ�, false�� ��� ��õ��� �������� �ʴ´�.
	 */
	public boolean shouldRetry() {
		return (0 < NumberOfTriesLeft);
	}

	/**
	 * ���� �߻� �� ��õ� Ƚ���� ���� �ִ����� üũ�ϰ�,
	 * ��õ� Ƚ���� �ʰ� ���� �ʾ��� ��쿡�� ���� �õ� ���� ��� sleep�� �ڿ� 
	 * �ٽ� �õ��Ѵ�. 
	 * ��õ� Ƚ���� �ʰ� ���� ��쿡�� RetryException�� Throw�Ѵ�.
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
	 * ���� ��õ��� ���� �Ŀ� ������ ���� �ð� �� ��ȯ. ���� ��
	 * @return ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	protected long getTimeToWait()	{
		return timeToWait;
	}
	
	/**
	 * ���� ��õ� �ð� ���� Wait �Ѵ�.
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
	 * ���� ���� ��õ� Ƚ��
	 * @return ���� ���� ��õ� Ƚ��
	 */
	public int getNumberOfTriesLeft() {
		return NumberOfTriesLeft;
	}

	
	/**
	 * Command pattern���� Command�� �����ϴ� Proxy �޼ҵ� 
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
				logger.info(this.getClass().getName() + " , ���� ��õ� Ƚ�� : " + getNumberOfTriesLeft() + " " + getTimeToWait()  + "�� �ڿ� �ٽ� �õ��մϴ�.", e );
				ExceptionOccurred(e);
			}
		}		
		return null;
	}


	/**
	 * �� Ŭ������ extends�ϴ� Ŭ������ �� �޼ҵ带 �����ؾ� �Ѵ�.
	 * @return
	 * @throws Exception
	 */
	protected abstract Object action() throws Exception; 

}
