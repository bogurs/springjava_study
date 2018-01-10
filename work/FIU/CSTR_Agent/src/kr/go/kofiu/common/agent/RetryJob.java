package kr.go.kofiu.common.agent;

import kr.go.kofiu.common.agent.AgentException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Retry Job�� ��� �ʿ�� �ϴ� param���� �������� �ʱ�ȭ �ϱ� ���� Ŭ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 14
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public abstract class RetryJob extends ScheduleJob  {
	
	/**
	 * ��õ� Ƚ�� 
	 */
	protected int numOfRetries = 3;
	
	/**
	 * ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	protected long timeToWait = 3;
	
	/**
	 * param ���� numOfRetries, timeToWait ���� ������ �ʱ�ȭ 
	 * doRetryJob�� ȣ���Ѵ�.
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
	 * ������ ���ϴ� �׼��� �� �޼ҵ忡 �����Ѵ�.
	 * @param context
	 * @throws AgentException
	 */
	public abstract void doRetryJob(JobExecutionContext context) throws AgentException ;
}
