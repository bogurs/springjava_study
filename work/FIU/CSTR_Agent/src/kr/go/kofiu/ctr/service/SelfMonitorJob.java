package kr.go.kofiu.ctr.service;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.conf.AgentInfo;
import kr.go.kofiu.ctr.conf.Configure;
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
		AgentInfo agentInfo = Configure.getInstance().getAgentInfo();
		if ( agentInfo.isTest() ){
			logger.info("���� �׽�Ʈ ���� ���� �� �Դϴ�. ������ �߰����� �������� �ʰ� �������� ó���մϴ�.");
		}
		
		Controller controller = Controller.getInstance();
		String bootValue = controller.doControl(Controller.BOOT_GET, null, Controller.SERVICE_CTR);
	
		if(bootValue.equals(Controller.BOOT_SHUTDOWN)) {
			CTRAgent.getInstance().shutdown();
		} else if(bootValue.equals(Controller.BOOT_RESTART) 
				|| bootValue.equals(Controller.BOOT_UPDATE_PHASE3))	{
			CTRAgent.getInstance().shutdown(true);
		}
	}

}