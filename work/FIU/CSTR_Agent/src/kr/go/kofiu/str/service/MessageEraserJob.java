package kr.go.kofiu.str.service;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.util.DateUtil;


import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;


/*******************************************************
 * <pre>
 * ����   �׷��  : str �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ��ȿ�Ⱓ�� ���� ���� ���� ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageEraserJob extends ScheduleJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MessageEraserJob.class);

	/**
	 * ARCH/ERROR ������ �ִ� ��ȿ �Ⱓ�� ���� ������ �����Ѵ�.
	 * �Ⱓ�� config.xml ���Ͽ� ���� ������ ���� �����ϴ�.
	 */
	public void doJob(JobExecutionContext context) throws AgentException 
	{
		logger.debug("Delete ARCH Files Start!");
		JobDataMap map = context.getJobDetail().getJobDataMap();
		int day = Integer.parseInt((String)map.get("param"));
		if ( day < 30 ){
			logger.info("���� ������ �ּ��� 30�� ���� �����Ǿ�� �մϴ�.\n ���� config.xml ���Ͽ� �����Ͻ� �� " + day + "���� �۾Ƽ� ���õ˴ϴ�.");
			day = 30;
		}

		Collection col = new ArrayList();
		col.add(STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_SEND());
		col.add(STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_RCV_ACCEPT());
		col.add(STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_RCV_ARRIVE());
		col.add(STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_RCV_PREACCEPT());
		col.add(STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_RCV_SEND());
		
		try {
			deleteFiles(col, day);
		} catch (ParseException e) {
			logger.error(e,e);
		}
	}

	
	/**
	 * ������ ���� ���� ��¥�� ���� ���� ������ �����Ѵ�. 
	 * @param col
	 * @param day
	 * @throws ParseException 
	 */
	private void deleteFiles(Collection col , int day) throws ParseException {
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		
		Iterator iter = col.iterator();
		while(iter.hasNext()){
			String dir = (String)iter.next();
			File[] dirs_per_days = new File(dir).listFiles();
			for(int i=0; i < dirs_per_days.length; i++) {
				if ( !dirs_per_days[i].isDirectory() ){
					long archiveLastModified = dirs_per_days[i].lastModified();
					Date date = new Date(archiveLastModified);
					cal.setTime(date); 
					cal.add(Calendar.DATE , day);
					if ( today.after(cal)) {
						if ( dirs_per_days[i].delete() ){
							logger.info("���� " + dirs_per_days[i] + "�� �����Ͽ����ϴ�.");
						}
					}
				}
				else {
					Collection subCol = new ArrayList();
					subCol.add(dirs_per_days[i].getAbsolutePath());
					deleteFiles(subCol, day);
				}
			}// end of for
		}
	} // end of method
}
