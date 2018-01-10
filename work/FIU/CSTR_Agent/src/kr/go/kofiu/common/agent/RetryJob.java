package kr.go.kofiu.common.agent;

import kr.go.kofiu.common.agent.AgentException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Retry Job일 경우 필요로 하는 param값을 공통으로 초기화 하기 위한 클래스
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 14
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public abstract class RetryJob extends ScheduleJob  {
	
	/**
	 * 재시도 횟수 
	 */
	protected int numOfRetries = 3;
	
	/**
	 * 실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	protected long timeToWait = 3;
	
	/**
	 * param 변수 numOfRetries, timeToWait 로컬 변수로 초기화 
	 * doRetryJob을 호출한다.
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		String param = (String)map.get("param");
		String[] params = param.split(",");
		numOfRetries = Integer.parseInt(params[0].trim());
		timeToWait = Long.parseLong(params[1].trim());
		doRetryJob(context);
	}

	/**
	 * 수행을 원하는 액션을 이 메소드에 구현한다.
	 * @param context
	 * @throws AgentException
	 */
	public abstract void doRetryJob(JobExecutionContext context) throws AgentException ;
}
