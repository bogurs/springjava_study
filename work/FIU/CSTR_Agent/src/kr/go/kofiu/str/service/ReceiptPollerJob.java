package kr.go.kofiu.str.service;

import java.io.File;
import java.util.ArrayList;


import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.str.conf.FilePathInfo;
import kr.go.kofiu.str.conf.STRAgentInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRJMSInfo;
import kr.go.kofiu.str.util.CSVLogger;
import kr.go.kofiu.str.util.CSVUtil;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.util.MessageHandler;
import kr.go.kofiu.str.util.STRDocumentSenderUtil;
import kr.go.kofiu.str.util.STRResponseDocument;
import kr.go.kofiu.str.util.WriteRcvFile;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;


public class ReceiptPollerJob extends ScheduleJob{
	private static boolean isDebug = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public String attachment = "";
	
	
	public void doJob(JobExecutionContext context) throws AgentException {
		//logger.info("[STR]ReceiptPollerJob Polling 시작 ");
				
		try{
			STRResponseDocument strdoc;
			STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
			
			// 증서 적재를 위한 파일위치 설정
			FilePathInfo filePath = agentInfo.getFilePathInfo(); 
			String acceptInboxCertPath = filePath.getINBOX_ACCEPT();
			String arriveInboxCertPath = filePath.getINBOX_ARRIVE();
			String preacceptInboxPath = filePath.getINBOX_PREACCEPT();
			String acceptArchiveCertPath = filePath.getARCHIVE_RCV_ACCEPT();
			String arriveArchiveCertPath = filePath.getARCHIVE_RCV_ARRIVE();
			String preacceptArchivePath = filePath.getARCHIVE_RCV_PREACCEPT();
			
			
			// JMS SOAP 통신 발송,도착,접수,가접수 증서 수신
			ArrayList<String> responseData = MessageHandler.getInstance().pollingProcess();
			if(responseData.equals(null) || responseData.size()==0){
				return;
			}
			
			
			for(int i = 0 ; i < responseData.size() ; i++){
				// JMS SOAP 에서 가져온 값을 DocumentObject로 변환
				try{
					String responseString = responseData.get(i);
					strdoc = WriteRcvFile.parseDocumentMessage(responseString);
				}catch(AgentException e){
					logger.error("수신된 증서 분석 중 에러가 발생하였습니다.");
					throw new AgentException(e);
				}
				// 아카이브 파일 저장 및 로그 저장
				//boolean getResult = WriteRcvFile.WriteToFile(strdoc,false,"RCV");
				
				// Attachment 
				String attachment = strdoc.getAttachment();
				
				String error_cd = getErrorCd(attachment);
				String error_msg = getErrorMsg(attachment);
				String flag = attachment.substring(10, 12);
				String flag_filetag = "";
				String service = "";
				
				
				// flag(MessageTypeCode) 값에 따라 증서 파일명 뒤의 차번을 설정한다.
				String strFileNm = strdoc.getAttachFileNm();
				if(flag.equals("06")){
					flag_filetag = "01";
					strFileNm = strFileNm.replace(".xml","_01.RCV");
					service = "도착";
				}else if(flag.equals("07")){
					flag_filetag = "02";
					strFileNm = strFileNm.replace(".xml","_02.RCV");
					service = "가접수";
				}else if(flag.equals("08")){
					flag_filetag = "03";
					strFileNm = strFileNm.replace(".xml","_03.RCV");
					service = "접수";
				}
				
				//
				logger.info(service + "증서수신 = " + attachment);
				// 기관코드값 설정
				String orgCd = CSVUtil.getOrgCode(strdoc);
				
				//CSV Logging
				String csvText = CSVUtil.genCSV(strdoc,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(),"", orgCd , false, strdoc.getDocSendDt());
				
				
				// 발송증서 생성 -> 기관코드(4)_날짜(8)_일렬번호(8).SNR
				//String strFileNm = "STR_"+ orgCd +"_"+strdoc.getDocSendDt()+"_"+strdoc.getOrgDocNum().substring(0, 8)+"_"+flag_filetag+".RCV";
				
				
				// 저장Paht 설정
				String destArchivePath = null;
				String destInboxPath = null;
				
				
				// 도착,접수, 가접수에 맞게 적재위치 설정
				if(flag.equals("06")){ // Arrive 도착증서
					destArchivePath = arriveArchiveCertPath;
					destInboxPath = arriveInboxCertPath;
				}else if(flag.equals("07")){ //PreAccept 가접수증서
					destArchivePath = preacceptArchivePath;
					destInboxPath = preacceptInboxPath;
				}else if(flag.equals("08")){ //Accept 접수증서
					destArchivePath = acceptArchiveCertPath;
					destInboxPath = acceptInboxCertPath;
				}
				
				// 증서 파일저장(도착,가접수,접수)
				String date = CurrentTimeGetter.formatDate();
				
				destInboxPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), destInboxPath, orgCd, date);
				destArchivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), destArchivePath, orgCd, date);
				
				FileTool.writeToFile( destInboxPath + File.separator + strFileNm, attachment, false );
				FileTool.writeToFile( destArchivePath  + File.separator + strFileNm, attachment, false );
				
			}
			
			}catch (RetryException e){
				// 전송 문서 헤더 값 추가시 오류 , CTRService lookup, method 호출 실패
				// Internal Server Error(FIURemoteException)
				// 10 분간 대기
				//e.printStackTrace();
				int sleepTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getSLEEP_TIME();
				logger.error("현재 중계 기관의  보고 문서 접수 서비스(MessagePolling) 제공에 문제가 있습니다. 연계모듈을 "+sleepTime+"초간 중지합니다.");
				STRAgent.getInstance().sleepAgent(sleepTime);
			}catch(Exception e){
				e.printStackTrace();
				throw new AgentException(e);
			}
		
	}
	
	public String getErrorMsg(String attachment){
		
		int error_msg_end = attachment.lastIndexOf("||");
		int error_msg_start = attachment.lastIndexOf("||", error_msg_end-1)+2;

		return attachment.substring(error_msg_start,error_msg_end);	
	}
	
	public String getErrorCd(String attachment){
		
		int error_msg_end = attachment.lastIndexOf("||");
		int error_msg_start = attachment.lastIndexOf("||", error_msg_end-1)+2;
		int error_cd_start =  attachment.lastIndexOf("||", error_msg_start-3)+2;
		int error_cd_end =  error_msg_start-2;
		
		return attachment.substring(error_cd_start,error_cd_end);
	}
	
	public boolean checkDir(String dir){
		File f = new File(dir);
		if ( f.exists() && f.isDirectory() ) 
			return true;
		
		boolean result = f.mkdirs();
		return result;
	}
}
