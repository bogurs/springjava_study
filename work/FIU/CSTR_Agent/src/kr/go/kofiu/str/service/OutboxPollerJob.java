package kr.go.kofiu.str.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.str.conf.FilePathInfo;
import kr.go.kofiu.str.conf.STRConfigure;

import org.quartz.JobExecutionContext;


import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.validation.str.StrFileNamePatternMatcher;

/**
 * /*******************************************************
 * 
 * ���� �׷�� : STR �ý��� ���� 
 * ������ : ���� ��� Agent 
 * �� �� : ���������� OUTBOX�� �Ѿ�� XML������ ������ proc�� �������ش�. 
 * �� �� �� : ������ 
 * �� �� �� : 2014. 09. 23  
 * 
 */

public class OutboxPollerJob extends ScheduleJob {
	public static long IOWaitTime = 0;; 	// ���ϻ��� �� ���ð�
	private long now = System.currentTimeMillis();			// ����ð�
	private static boolean isDebug = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Schedule ���� ( OUTBOX Polling )
	 * @throws AgentException 
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		try {
			
			//logger.info("[STR]OUTBOXPolling ���� ");
			FilePathInfo filePath = STRConfigure.getInstance().getAgentInfo()
					.getFilePathInfo();
			// ���� ���
			String outboxPath = filePath.getOUTBOX();
			String procPath = filePath.getPROC();
			String successLogPath = filePath.getREPORT_SEND();
			String errorArchPath = filePath.getARCHIVE_SEND_ERR();
			String errorLogPath = filePath.getREPORT_SEND();
			
			if(isDebug){
				// ���� Path ���
				System.out.println("outboxPath = " + outboxPath);
				System.out.println("procPath = " + procPath);
				System.out.println("successLogPath = " + successLogPath);
				System.out.println("errorArchPath = " + errorArchPath);
				System.out.println("errorLogPath = " + errorLogPath);
				
			}
			
			File strFile = new File(outboxPath);
			File[] strFileListTemp = strFile.listFiles();

			
			IOWaitTime = 1000 * 60 * filePath.getOutboxIOWaitMinutes();
			ArrayList<File> strFileList = new ArrayList<File>();
			for(int i = 0 ; i < strFileListTemp.length ; i++){
				if (strFileListTemp[i].isFile()) {
					if ((now - strFileListTemp[i].lastModified()) < IOWaitTime) {
						continue;
					}
					strFileList.add(strFileListTemp[i]);
				}
			}
			
			// ���ŵ� ���� ������ 0���� ���� �����Ѵ�.
			if (strFileList.size() == 0) {
				return;
			}

			if(isDebug){
				logger.info("���ϰټ� - " + (strFileList.size()));
			}
			
			
			// ���ϸ� üũ
			StrFileNamePatternMatcher fnChker = new StrFileNamePatternMatcher(); // ���ϸ� ����üũ (xml)
			for (int i = 0; i < strFileList.size(); i++) {
				File fileTemp = strFileList.get(i);
				try{
					String strFileNm = fileTemp.getName();
					
					fnChker.match(strFileNm);
					File procFile = new File(procPath + "/" + strFileNm);
					fileTemp.renameTo(procFile);
					logger.info("���� ���丮�� " + procFile.getName() + " ���� �̵�.");
				}catch(Throwable t){
					
					String strFileNm = fileTemp.getName();
					
					// ���ϸ� currentTime �߰�
					String time = CurrentTimeGetter.formatDateTime();
					System.out.println(strFileNm.substring(0, strFileNm.indexOf(".xml")));
					strFileNm = strFileNm.substring(0, strFileNm.indexOf(".xml")).concat("_"+time).concat(".xml");
					
					File errorFile = new File(errorArchPath + "/" + strFileNm);
					fileTemp.renameTo(errorFile);
					logger.info("���� ���丮�� " + errorFile.getName() + " ���� �̵�.");
					//WriteRcvFile.createLogfile(errorLogPath , errorFile.getName() );
					FileTool.writeToFile( errorLogPath + File.separator + strFileNm, "", false );
					
				}
			
			}

			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
		}

	}
	
	

}
