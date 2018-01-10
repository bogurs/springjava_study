package kr.go.kofiu.common.agent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.common.agent.Agent;
import kr.go.kofiu.common.agent.AgentException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 스케줄 Job. Agent의 모든 서비스는 이 클래스를 extends한다. 
 * 				Exception 처리와 Race Conditon 처리를 수행한다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public abstract class ScheduleJob implements Job 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ScheduleJob.class);

	/**
	 * 현재 수행 중인 job의 class 명을 저장한다. 
	 * 복수개의 동일한 서비스가 실행 되는 것을 방지한다. 
	 */
	private static Set RuntimeJob = Collections.synchronizedSet(new HashSet());
	
	/**
	 * Quartz 프레임워크에서 스케줄링 Job을 위해 사용되는 메소드
	 * @param context JobExecutionContext
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobClazz = context.getJobDetail().getJobClass().getName();
		try {
			// 이미 실행 중인 지를 체크한다. Job 별로 체크하여야 하므로 해당 class의 전역 변수를 참조하여야 한다.
			logger.debug(jobClazz + " ScheduleJob.isJobRun() - " + RuntimeJob);
			if ( RuntimeJob.add(jobClazz) ) {
				doJob(context);
			} else {
				return ;
			}
		} catch (AgentException e) {
			logger.error(jobClazz + " 실행 중에 오류가 발생하였습니다.", e);
			e.fireAction();
		} catch(Exception e){
			//오류 처리.
			logger.error(jobClazz + " 실행 중에 알 수 없는 오류가 발생하였습니다.", e);
		} catch(Throwable t ){
			logger.fatal(jobClazz + " 실행 중에 알 수 없는 Error가  발생하였습니다.", t);
			EmailAction emailAction = new EmailAction(jobClazz + " 실행 중에 알 수 없는 Error가  발생하였습니다."	, t);
			emailAction.doAct();
			CTRAgent.getInstance().shutdown();
		} 
		// never do that finally.	
		RuntimeJob.remove(jobClazz);
	}

	/**
	 * 수행을 원하는 액션을 이 메소드에 구현한다.
	 * @param context  JobExecutionContext
	 * @throws AgentException
	 */
	public abstract void doJob(JobExecutionContext context) throws AgentException;
	
}