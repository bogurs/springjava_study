package kr.go.kofiu.str.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.RetryJob;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.str.conf.FilePathInfo;
import kr.go.kofiu.str.conf.STRAgentInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.summary.StrDestSummary;
import kr.go.kofiu.str.util.CSVLogger;
import kr.go.kofiu.str.util.CSVUtil;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.util.GenReceiptDoc;
import kr.go.kofiu.str.util.STRDocumentSenderUtil;
import kr.go.kofiu.str.util.StrSendTest;
import kr.go.kofiu.str.util.WriteRcvFile;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

public class STRDocumentSenderJob extends RetryJob {
	private static boolean isDebug = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// TODO Auto-generated method stub
		
		StrDestSummary strDest = null;
		
		try{
			String error_cd = "00";
			String error_msg ="발송증서수신성공";
			
			STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
			
			FilePathInfo filePath = agentInfo.getFilePathInfo(); //FilePath Info
			// 폴더 목록
			String procPath = filePath.getPROC();
			String successArchRcvSendPath= filePath.getARCHIVE_RCV_SEND();
			String inboxSendPath = filePath.getINBOX_SEND();
			String successArchPath = filePath.getARCHIVE_SEND();
			String errorArchPath = filePath.getARCHIVE_SEND_ERR();

			
			// 파일목록 중 폴더 제거 시작
			File strFile = new File(procPath);
			File[] strFileListTemp = strFile.listFiles();
			ArrayList<File> strFileList = new ArrayList<File>();
			for(int i = 0 ; i < strFileListTemp.length ; i++){
				if(strFileListTemp[i].isFile()){
					strFileList.add(strFileListTemp[i]);
				}
			}
			// 파일목록 중 폴더 제거 끝
			
			// 파일 목록이 0개일 경우 리턴한다.
			if (strFileList.size() == 0) {
				return;
			}
			
			// 정형성 검사를 위한 인스턴스 생성
			StrSendTest strSend = new StrSendTest();
			for (int i = 0; i < strFileList.size(); i++) {
				
				File fileTemp = strFileList.get(i);
				InputStream is = new FileInputStream(fileTemp.getAbsolutePath());
				
				// 필요한 값 추출
				
				try{
					//기준정보 추출
					strDest = WriteRcvFile.parseSTRDocument(is);
				}catch(Exception e){
					is.close();
					logger.error(fileTemp.getName() +" 기준정보 Parsing 중 에러가 발생했습니다. XML 형식이 올바른지 확인하십시오. 파일을 ARCHIVE로 적재합니다.");
					logger.error("ERROR MESSAGE : " + e.getMessage());
					String date = CurrentTimeGetter.formatDate();
					String orgCd = fileTemp.getName().substring(5, 8);
					String time = CurrentTimeGetter.formatDateTime();
					String moveFileNm = fileTemp.getName().substring(0, fileTemp.getName().indexOf(".xml")).concat("_"+time).concat(".xml");
					String archivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), errorArchPath, orgCd, date);
					STRDocumentSenderUtil.checkDir(archivePath);			
					fileTemp.renameTo(new File(archivePath+"/"+moveFileNm));			
					return;
				}
					
				is.close();
				strDest.setFileName(fileTemp.getName());
				
				// 정형성 검사 수행, JMS SOAP 통신 후 strDoc 값 리턴
				FileInputStream strInputStream = new FileInputStream(fileTemp.getAbsolutePath());
				String strDocResult = strSend.action(strInputStream, fileTemp.getName());
				strInputStream.close();
				
				//리턴값이 OK가 아닌경우 에러처리 
				if(!strDocResult.equals("OK")){
					STRDocumentSenderUtil.error(strDocResult, strDest);
					continue;
				}
				
				// CSV, 증서 저장을 위한 환경세팅
				String orgCd = strDest.getOrgCode();
				String time = CurrentTimeGetter.formatDateTime();
				String date = CurrentTimeGetter.formatDate();
				String orgFileNm = fileTemp.getName();
				String moveFileNm = orgFileNm.substring(0, orgFileNm.indexOf(".xml")).concat("_"+time).concat(".xml");
				
				// 발송증서 파일명 명명규칙 -> 기관코드(4)_날짜(8)_일렬번호(8).SNR
				String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";
				if(isDebug){
					logger.info("발송 증서 파일명 = " + strFileNm);
				}
				
				// CSV 로깅
				//CSV Logging
				String csvText = CSVUtil.genCSV(strDest,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(), ".", orgCd, true, strDest.getDocSendDate());
				
				
				String certPath = "";		// INBOX 발송증서 저장경로
				String certBackPath = "";	// ARCHIVE/rcv 발송증서 백업경로
				String archivePath = "";	// ARCHIVE/send STR문서 이동 경로
				
				
				// 발송증서 값이 성공일 경우 CSV,증서적재,STR 문서 Archive 적재 위치지정
				if(error_cd.equals("00")){
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, orgCd, date);
					archivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchPath, orgCd, date);
				}else{
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, orgCd, date);
					archivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), errorArchPath, orgCd, date);
				}
				
				// 발송 증서 적제
				String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg);
				logger.info("발송증서수신 = " + snr);

				FileTool.writeToFile( certPath + File.separator + strFileNm, snr, false );
				FileTool.writeToFile( certBackPath + File.separator + strFileNm, snr, false );
				
				/*
				 * 보고문서 파일이동
				 * 
				 * STR문서를 ARCHIVE/send 로 이동 또는 삭제한다.
				 * isArchiveEnabled() 가 true 일경우 - 이동
				 * isArchiveEnabled() 가 false 일경우 - 삭제
				 * 
				 */
				
				if(STRConfigure.getInstance().getAgentInfo().isArchiveEnabled()){
					//MOVE STR DOCUMENT TO SUCCESS ARCHIVE
					STRDocumentSenderUtil.checkDir(archivePath);
					boolean isSuccess = false;
					int retryCount = 10;
					while(retryCount-- > 0){
						if( isSuccess = fileTemp.renameTo(new File(archivePath+"/"+moveFileNm))){
							break;
						}
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){
							throw new AgentException(e);
						}
					}if(isSuccess){
						logger.info("Proc 파일이동 성공");
					}else{
						logger.info("파일이동에 실패하였습니다. 파일이 사용중인지 확인 바랍니다.");
					}
				}else{
					boolean isSuccess = false;
					int retryCount = 10;
					while(retryCount-- > 0){
						if( isSuccess = fileTemp.delete()){
							break;
						}
						try{
							Thread.sleep(100);
						}catch(InterruptedException e){
							throw new AgentException(e);
						}
					}if(isSuccess){
						logger.info("Proc 파일제거 성공");
					}else{
						logger.info("파일제거에 실패하였습니다. 파일이 사용중인지 확인 바랍니다.");
					}
				}
			}
				
		}catch (RetryException e){
			// 전송 문서 헤더 값 추가시 오류 , CTRService lookup, method 호출 실패
			// Internal Server Error(FIURemoteException)
			// 10 분간 대기
			try{
				
				int sleepTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getSLEEP_TIME();
				int retryTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getRETRY_COUNT();
				int waitTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getRETRY_WAIT_TIME();
				
				
				String error_cd = "99";
				String error_msg = "보고 문서 접수 서비스(MessageSending) "+waitTime+"초간 "+retryTime+"회 재시도를 하였으나 응답받지 못하여 연계모듈을 "+sleepTime+"초간 중지합니다.";
				
				logger.error("현재 중계 기관의  보고 문서 접수 서비스(MessageSending) 제공에 문제가 있습니다. 연계모듈을 "+sleepTime+"초간 중지합니다.");
				
				/*
				 * 재시도 실패 CSV 적재
				 */
				String csvText = CSVUtil.genCSV(strDest,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(), ".", strDest.getOrgCode(), true, strDest.getDocSendDate());
				
				/*
				 * 재시도 실패 증서 적재
				 */
				String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg);
				logger.info("발송증서수신 = " + snr);
				
				STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
				FilePathInfo filePath = agentInfo.getFilePathInfo(); 
				
				// 폴더 목록
				String successArchRcvSendPath= filePath.getARCHIVE_RCV_SEND();
				String inboxSendPath = filePath.getINBOX_SEND();
	
				String certPath = "";		// INBOX 발송증서 저장경로
				String certBackPath = "";	// ARCHIVE/rcv 발송증서 백업경로
				String date = CurrentTimeGetter.formatDate();
				String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";
				
				// 발송증서 값이 성공일 경우 CSV,증서적재,STR 문서 Archive 적재 위치지정
				if(error_cd.equals("00")){
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, strDest.getOrgCode(), date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, strDest.getOrgCode(), date);
					
				}else{
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, strDest.getOrgCode(), date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, strDest.getOrgCode(), date);		
				}
				
				FileTool.writeToFile( certPath + File.separator + strFileNm, snr, false );
				FileTool.writeToFile( certBackPath + File.separator + strFileNm, snr, false );
				STRAgent.getInstance().sleepAgent(sleepTime);
			}catch(Exception e1){
				throw new AgentException(e1);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new AgentException(e);
		}
	}

	
	public String createCSV(StrDestSummary strDest , String error_cd , String error_msg){
		String msg = "";
		String time = CurrentTimeGetter.formatTime();
		String file_nm = strDest.getFileName();
		String document_id = strDest.getFiuDocNum();
		if (time != null && time.length() > 0 ) {
			msg += "\"" + time + "\"";
		}
		msg +=",";
		if (file_nm != null && file_nm.length() > 0 ) {
			msg += "\"" + file_nm + "\"";
		}
		msg +=",";
		if (document_id != null && document_id.length() > 0 ) {
			msg += "\"" + document_id + "\"";
		}
		msg +=",";
		if (error_cd != null && error_cd.length() > 0 ) {
			msg += "\"" + error_cd + "\"";
		}
		msg +=",";
		if (error_msg != null && error_msg.length() > 0 ) {
			msg += "\"" + error_msg + "\"";
		}
		
		msg+="\r\n";
		return msg;
	}
	
}