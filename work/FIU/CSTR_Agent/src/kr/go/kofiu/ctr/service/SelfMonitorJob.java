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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 주기적으로 boot.ctl파일값을 체크한다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SelfMonitorJob extends ScheduleJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SelfMonitorJob.class);

	/**
	 * Control 정보 파일인 boot.ctl파일을 모니터링한다. 
	 * shutdown , restart 에 따라 해당 Action을 수행 한다.
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		AgentInfo agentInfo = Configure.getInstance().getAgentInfo();
		if ( agentInfo.isTest() ){
			logger.info("현재 테스트 모드로 실행 중 입니다. 문서를 중계기관에 전송하지 않고 가상으로 처리합니다.");
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