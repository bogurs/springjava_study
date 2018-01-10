package kr.go.kofiu.str.service;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.str.conf.JmsMQLib;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.summary.UtilSummary;
import kr.go.kofiu.str.util.CSVLogger;
import kr.go.kofiu.str.util.CSVUtil;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.util.GUIDUtil;
import kr.go.kofiu.str.util.MessageHandler;
import kr.go.kofiu.str.util.XmlParserData;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;


public class HeartBeatJob extends ScheduleJob{
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void doJob(JobExecutionContext context)
		    throws AgentException
		  {
		    UtilSummary us = null;
		    String xmlData = XmlParserData.getHeartBeatXML();
		    String GUID = GUIDUtil.getGUID_FIUF0001();
		    CSVLogger csv = new CSVLogger();
		    try {
		      us = MessageHandler.getInstance().sendHeartBeat(xmlData, GUID);
		      JobDataMap map = context.getJobDetail().getJobDataMap();
		      Long period = (Long)map.get("period");
		      String mdl_stts_chk_intvl = Long.toString(period.longValue() / 1000L);
		      this.logger.info("STR HEARTBEAT " + mdl_stts_chk_intvl + "�� �ֱ� ���� ó�� (�ڵ� : " + us.getErrorCd() + " | ��� : " + us.getErrorMsg() + ")");
		      String csvText = CSVUtil.genUtilCSV(us.getErrorCd(), us.getErrorMsg());
		      csv.writeCsv(csvText.getBytes(), "HEARTBEAT", STRConfigure.getInstance().getAgentInfo().getId(), true, null);
		    }
		    catch (RetryException e)
		    {
		      JobDataMap map = context.getJobDetail().getJobDataMap();
		      Long period = (Long)map.get("period");
		      String mdl_stts_chk_intvl = Long.toString(period.longValue() / 1000L);
		      this.logger.info("STR HEARTBEAT " + mdl_stts_chk_intvl + "�� �ֱ� ���� ó�� (�ڵ� : 99 | ��� : ESB Server Fail)");
		      String csvText = CSVUtil.genUtilCSV("99", "ESB Server Fail");
		      csv.writeCsv(csvText.getBytes(), "HEARTBEAT", STRConfigure.getInstance().getAgentInfo().getId(), true, null);
		      int sleepTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getSLEEP_TIME();
		      this.logger.error("���� �߰� �����  ���� ���� ���� ����(HeartBeat) ������ ������ �ֽ��ϴ�. �������� " + sleepTime + "�ʰ� �����մϴ�.");
		      STRAgent.getInstance().sleepAgent(sleepTime);
		    }
	}
}
