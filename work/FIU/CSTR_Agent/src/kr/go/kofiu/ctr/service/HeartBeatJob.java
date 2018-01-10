package kr.go.kofiu.ctr.service;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.EsbCheckServiceInfo;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.XmlParser;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 중계 기관에 주기적으로 신호를 전송한다. 이를 통해 중계 기관은 보고 기관이 
 * 살아 있음을 알 수 있다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class HeartBeatJob extends ScheduleJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HeartBeatJob.class);

	/**
	 * 1 분 milli second 단위 값 
	 */
	private static final long MINUTE = 1000 * 60; 
	
	/**
	 * 중계 기관에 주기적으로 신호를 전송한다.
	 * 신호의 내용은 의미가 없는 값이다.
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		// 테스트 여부 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}

		try {
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();	

			// check interval
			JobDataMap map = context.getJobDetail().getJobDataMap();
			Long period = (Long)map.get("period");
			String mdl_stts_chk_intvl = Long.toString(period.longValue()/1000);
			//logger.info("HeartBeat 전송 주기  : " + mdl_stts_chk_intvl);

			String GUID = getGUID_FIUF0001();
			EsbCheckServiceInfo heartBeatResult = XmlParser.getHeartBeatResult((String) jmssoapService.EsbSoapService(Controller.HEARTBEAT, GUID));
			if(heartBeatResult != null){
				//logger.info("HeartBeat Signal 전송 성공\n" + heartBeatResult.getCheck());
				logger.info("CTR HEARTBEAT "+mdl_stts_chk_intvl+"초 주기  서비스 처리 (코드 : "+ heartBeatResult.getResultCode() + " | 결과 : " + heartBeatResult.getResultMessage()+")");
				new ReportAction(Controller.HEARTBEAT, heartBeatResult.getResultCode(), heartBeatResult.getResultMessage()).doAct();
			}
		} catch (RetryException e) {
			// AgentWaiter WebService lookup, method 호출 실패 
			// 10 분간 대기
			JobDataMap map = context.getJobDetail().getJobDataMap();
			Long period = (Long)map.get("period");
			String mdl_stts_chk_intvl = Long.toString(period.longValue()/1000);
			logger.info("CTR HEARTBEAT "+mdl_stts_chk_intvl+"초 주기  서비스 처리 (코드 : 99 | 결과 :  ESB Server Fail)");
			new ReportAction(Controller.HEARTBEAT, "99", "ESB Server Fail").doAct();
			int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
			logger.error("현재 중계 기관의 보고 문서 접수 서비스(HeartBeat) 제공에 문제가 있습니다. 연계모듈을 "+sleepTime+"초간 중지합니다.");
			CTRAgent.getInstance().sleepAgent(sleepTime);
		} catch (Exception e) {
			//logger.error("", e);
			throw new AgentException(e);
		}

	}
	
	public String getGUID_FIUF0001(){ //Check Agent용 GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

}
