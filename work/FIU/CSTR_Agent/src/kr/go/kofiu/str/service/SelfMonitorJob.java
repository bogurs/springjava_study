package kr.go.kofiu.str.service;



import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.common.agent.STRAgent;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : �ֱ������� boot.ctl���ϰ��� üũ�Ѵ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SelfMonitorJob extends ScheduleJob {
	
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SelfMonitorJob.class);

	/**
	 * Control ���� ������ boot.ctl������ ����͸��Ѵ�. 
	 * shutdown , restart �� ���� �ش� Action�� ���� �Ѵ�.
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		
		Controller controller = Controller.getInstance();
		String bootValue = controller.doControl(Controller.BOOT_GET,null,"STR");
	
		if(bootValue.equals(Controller.BOOT_SHUTDOWN)) {
			STRAgent.getInstance().shutdown();
		} else if(bootValue.equals(Controller.BOOT_RESTART) 
				|| bootValue.equals(Controller.BOOT_UPDATE_PHASE3))	{
			STRAgent.getInstance().shutdown(true);
		}
	}

}