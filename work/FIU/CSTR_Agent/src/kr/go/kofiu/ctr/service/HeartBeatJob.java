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
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : �߰� ����� �ֱ������� ��ȣ�� �����Ѵ�. �̸� ���� �߰� ����� ���� ����� 
 * ��� ������ �� �� �ִ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class HeartBeatJob extends ScheduleJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HeartBeatJob.class);

	/**
	 * 1 �� milli second ���� �� 
	 */
	private static final long MINUTE = 1000 * 60; 
	
	/**
	 * �߰� ����� �ֱ������� ��ȣ�� �����Ѵ�.
	 * ��ȣ�� ������ �ǹ̰� ���� ���̴�.
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		// �׽�Ʈ ���� 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}

		try {
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();	

			// check interval
			JobDataMap map = context.getJobDetail().getJobDataMap();
			Long period = (Long)map.get("period");
			String mdl_stts_chk_intvl = Long.toString(period.longValue()/1000);
			//logger.info("HeartBeat ���� �ֱ�  : " + mdl_stts_chk_intvl);

			String GUID = getGUID_FIUF0001();
			EsbCheckServiceInfo heartBeatResult = XmlParser.getHeartBeatResult((String) jmssoapService.EsbSoapService(Controller.HEARTBEAT, GUID));
			if(heartBeatResult != null){
				//logger.info("HeartBeat Signal ���� ����\n" + heartBeatResult.getCheck());
				logger.info("CTR HEARTBEAT "+mdl_stts_chk_intvl+"�� �ֱ�  ���� ó�� (�ڵ� : "+ heartBeatResult.getResultCode() + " | ��� : " + heartBeatResult.getResultMessage()+")");
				new ReportAction(Controller.HEARTBEAT, heartBeatResult.getResultCode(), heartBeatResult.getResultMessage()).doAct();
			}
		} catch (RetryException e) {
			// AgentWaiter WebService lookup, method ȣ�� ���� 
			// 10 �а� ���
			JobDataMap map = context.getJobDetail().getJobDataMap();
			Long period = (Long)map.get("period");
			String mdl_stts_chk_intvl = Long.toString(period.longValue()/1000);
			logger.info("CTR HEARTBEAT "+mdl_stts_chk_intvl+"�� �ֱ�  ���� ó�� (�ڵ� : 99 | ��� :  ESB Server Fail)");
			new ReportAction(Controller.HEARTBEAT, "99", "ESB Server Fail").doAct();
			int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
			logger.error("���� �߰� ����� ���� ���� ���� ����(HeartBeat) ������ ������ �ֽ��ϴ�. �������� "+sleepTime+"�ʰ� �����մϴ�.");
			CTRAgent.getInstance().sleepAgent(sleepTime);
		} catch (Exception e) {
			//logger.error("", e);
			throw new AgentException(e);
		}

	}
	
	public String getGUID_FIUF0001(){ //Check Agent�� GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); �̶�� �� �� �ִ�.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

}
