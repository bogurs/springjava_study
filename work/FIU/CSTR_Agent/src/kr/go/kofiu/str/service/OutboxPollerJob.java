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
 * 업무 그룹명 : STR 시스템 서브 
 * 업무명 : 보고 기관 Agent 
 * 설 명 : 보고기관에서 OUTBOX로 넘어온 XML파일을 가져와 proc로 전송해준다. 
 * 작 성 자 : 김현수 
 * 작 성 일 : 2014. 09. 23  
 * 
 */

public class OutboxPollerJob extends ScheduleJob {
	public static long IOWaitTime = 0;; 	// 파일생성 후 대기시간
	private long now = System.currentTimeMillis();			// 현재시간
	private static boolean isDebug = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Schedule 수행 ( OUTBOX Polling )
	 * @throws AgentException 
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		try {
			
			//logger.info("[STR]OUTBOXPolling 시작 ");
			FilePathInfo filePath = STRConfigure.getInstance().getAgentInfo()
					.getFilePathInfo();
			// 폴더 목록
			String outboxPath = filePath.getOUTBOX();
			String procPath = filePath.getPROC();
			String successLogPath = filePath.getREPORT_SEND();
			String errorArchPath = filePath.getARCHIVE_SEND_ERR();
			String errorLogPath = filePath.getREPORT_SEND();
			
			if(isDebug){
				// 폴더 Path 출력
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
			
			// 수신된 파일 갯수가 0개일 경우는 리턴한다.
			if (strFileList.size() == 0) {
				return;
			}

			if(isDebug){
				logger.info("파일겟수 - " + (strFileList.size()));
			}
			
			
			// 파일명 체크
			StrFileNamePatternMatcher fnChker = new StrFileNamePatternMatcher(); // 파일명 패턴체크 (xml)
			for (int i = 0; i < strFileList.size(); i++) {
				File fileTemp = strFileList.get(i);
				try{
					String strFileNm = fileTemp.getName();
					
					fnChker.match(strFileNm);
					File procFile = new File(procPath + "/" + strFileNm);
					fileTemp.renameTo(procFile);
					logger.info("진행 디렉토리에 " + procFile.getName() + " 파일 이동.");
				}catch(Throwable t){
					
					String strFileNm = fileTemp.getName();
					
					// 파일명에 currentTime 추가
					String time = CurrentTimeGetter.formatDateTime();
					System.out.println(strFileNm.substring(0, strFileNm.indexOf(".xml")));
					strFileNm = strFileNm.substring(0, strFileNm.indexOf(".xml")).concat("_"+time).concat(".xml");
					
					File errorFile = new File(errorArchPath + "/" + strFileNm);
					fileTemp.renameTo(errorFile);
					logger.info("실패 디렉토리에 " + errorFile.getName() + " 파일 이동.");
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
