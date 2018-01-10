package kr.go.kofiu.str.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kr.go.kofiu.common.agent.AgentException;


//import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/*******************************************************
 * <pre>
 * ����   �׷��  : STR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ������ Job. Agent�� ��� ���񽺴� �� Ŭ������ extends�Ѵ�. 
 * 				Exception ó���� Race Conditon ó���� �����Ѵ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public abstract class ScheduleJob implements Job 
{
	/**
	 * Logger for this class
	 */
	//private static final Logger logger = Logger.getLogger(ScheduleJob.class);

	/**
	 * ���� ���� ���� job�� class ���� �����Ѵ�. 
	 * �������� ������ ���񽺰� ���� �Ǵ� ���� �����Ѵ�. 
	 */
	private static Set RuntimeJob = Collections.synchronizedSet(new HashSet());
	
	/**
	 * Quartz �����ӿ�ũ���� �����ٸ� Job�� ���� ���Ǵ� �޼ҵ�
	 * @param context JobExecutionContext
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobClazz = context.getJobDetail().getJobClass().getName();
		try {
			// �̹� ���� ���� ���� üũ�Ѵ�. Job ���� üũ�Ͽ��� �ϹǷ� �ش� class�� ���� ������ �����Ͽ��� �Ѵ�.
			//logger.debug(jobClazz + " ScheduleJob.isJobRun() - " + RuntimeJob);
			if ( RuntimeJob.add(jobClazz) ) {
				doJob(context);
			} else {
				return ;
			}
		} /*catch (AgentException e) {
			logger.error(jobClazz + " ���� �߿� ������ �߻��Ͽ����ϴ�.", e);
			e.fireAction();
		}*/ catch(Exception e){
			//���� ó��.
			//logger.error(jobClazz + " ���� �߿� �� �� ���� ������ �߻��Ͽ����ϴ�.", e);
		} /*catch(Throwable t ){
			logger.fatal(jobClazz + " ���� �߿� �� �� ���� Error��  �߻��Ͽ����ϴ�.", t);
			EmailAction emailAction = new EmailAction(jobClazz + " ���� �߿� �� �� ���� Error��  �߻��Ͽ����ϴ�."	, t);
			emailAction.doAct();
			Agent.getInstance().shutdown();
		} */
		// never do that finally.	
		RuntimeJob.remove(jobClazz);
	}

	/**
	 * ������ ���ϴ� �׼��� �� �޼ҵ忡 �����Ѵ�.
	 * @param context  JobExecutionContext
	 * @throws AgentException
	 */
	public abstract void doJob(JobExecutionContext context) throws AgentException ;
	
}