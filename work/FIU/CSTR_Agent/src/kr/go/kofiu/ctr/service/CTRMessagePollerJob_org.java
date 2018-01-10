package kr.go.kofiu.ctr.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import kr.co.kofiu.www.ctr.encodedTypes.DocumentObject;
import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.MoveArchiveRcverrorAction;
import kr.go.kofiu.ctr.actions.RecvMailReportActionSet;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
//import kr.go.kofiu.ctr.util.XMLtoFlatFile089;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.SecuKit;
import kr.go.kofiu.ctr.util.SecurityUtil;
import kr.go.kofiu.ctr.util.TypeCode;
import kr.go.kofiu.ctr.util.Utility;
import kr.go.kofiu.ctr.util.excption.DecryptionException;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import signgate.crypto.util.PKCS7Util;


/*******************************************************
 * <pre>
 * 업무   그룹명 : CTR 시스템
 * 서브   업무명 : 보고 기관 Agent
 * 설        명 : 주기적으로 보고기관에서 RCV 화일을 집중기관으로 부터 수신하는 기능을 수행함. 
 * 작   성   자 : ykyu 
 * 작   성   일 : 2005. 8. 2
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CTRMessagePollerJob_org extends RetryJob
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CTRMessagePollerJob_org.class);

	
	/**
	 * 접수 증서 파일명 
	 */
	private String rcvFile ;
	
	/**
	 * 접수 증서 문서 번호
	 */
	private String documentId;
	
	/**
	 * 보고 일자 
	 */
	private String reportDate;

	/**
	 * 보고서 버전 ("V0.88" 또는 "V0.89")
	 */
	private String reportVersion;
	
	/**
	 * 접수 증서 파일 폴링. 파일 수신 후 복호화 시에 오류 발생 시 INBOX_ERROR 폴더에 
	 * 파일을 저장한다. 
	 * 인증서 읽기 오류가 발생할 경우 암호화된 메세지 저장하고 보고 기록을 남기며, 
	 * 관리자에게  mail을 전달하고  shutdown한다.
	 * 
	 * 2014. 9. 10 수정사항
	 * 복호화시 수신한 접수 증서는 Base64 암호화된 형식으로 수신된다.
	 * 따라서, 복호화시 base64에 대한 복호화 과정으로 바뀜.
	 * 
	 * @throws AgentException 
	 **/
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// 테스트일 경우 CGMessagePollerJob은 실행되지 않음. 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}
		
		DocumentObject documentObj = null;
	    
		try {
//			logger.info("CGMessagePollerJob Processing...");
			CTRMessagePollerCommand command = new CTRMessagePollerCommand(numOfRetries, timeToWait);
			
			//while( (documentObj = (DocumentObject)command.invoke()) != null ) {//WLI를 이용한 call
				//rcvFile은 base64로 암호화 되어 있음
			ArrayList<DocumentObject> resultList = (ArrayList<DocumentObject>) command.invoke();
			
			if(resultList==null || resultList.size()==0){
				return;
			}
			
			for(int i = 0 ; i < resultList.size() ; i++){
				documentObj = resultList.get(i);
				rcvFile = getDocumentBodyAttribute( documentObj , kr.go.kofiu.ctr.util.DocumentObject.AttachmentFileNameText);	

				logger.info(rcvFile + "접수 증서 수신 " );
				if ( rcvFile == null ) {
					logger.warn("접수 증서 파일 명이 NULL입니다. 대신 " + CtrAgentEnvInfo.SND_FILE_SAMPLE + "파일명을 사용합니다.");
					rcvFile = CtrAgentEnvInfo.SND_FILE_SAMPLE;
				}
				
				rcvFile = FileTool.replaceSuffix(rcvFile, "RCV");
				
				// 문서 번호 
				documentId = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDocumentID);
				if ( documentId == null )
					documentId = CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE;
					
				// 보고 일자
				reportDate = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDate);
				if ( reportDate == null )
					reportDate = DateUtil.getDateTime(DateUtil.yyyyMMdd);
				
				// 보고서 버전
				reportVersion = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.ReportFormVersionID);
				if ( reportVersion == null )
					reportVersion = kr.go.kofiu.ctr.util.DocumentObject.DOCUMENT_VERSION_NEW;
				
				/**
				 * 2014. 9. 12 추가 사항
				 * 복호화 전 Base64 Decoding 단계
				 */
				byte[] documentObjAtcmt = documentObj.getAttachment();
				byte[] originalDcuObjAtcmt;				
				
				/**
				 * 15.02.26 수정
				 * 전자서명을 메시지를 통해 체크하여 전자서명 되어 있었으면 처리하고
				 * 아닐 시 base64 decoding 으로 처리
				 */
				int p7Type = 0; 
				
				PKCS7Util p7VrfUtil = new PKCS7Util();
				p7Type = p7VrfUtil.getPKCS7Type(documentObjAtcmt);
				
				if (p7Type != 1 && p7Type != 2 && p7Type != 3) {
					originalDcuObjAtcmt = Base64.decodeBase64(documentObjAtcmt);
				} else{
					originalDcuObjAtcmt = SecuKit.getInstance().decryptSigned(documentObjAtcmt);
				}
				
				documentObj.setAttachment(originalDcuObjAtcmt);
				
				// 복호화
				//2014. 9. 10 base64 복호화과정으로 수정
				byte[] original = originalDcuObjAtcmt;
				logger.info("접수 증서 수신 결과: " + new String(original));
//			    ResponseCurrencyTransactionReportDocument ResponseDoc = null;
//			    try {
//			    	ResponseDoc = XMLtoFlatFile.generateResponseDoc(original);
//				} catch (Exception e) {
//					AgentException ae = new AgentException(e);
//					ae.addAction(new FatalActionSet("접수 증서 XML을  FlatFile로 변환 과정에서 오류 발생 : " + new String(original) , e));
//					throw ae;
//				}
				
			    if ( !WriteToFile(new String(original)) ) {
			    	continue;
			    }
				logger.info(rcvFile + " 접수 증서 저장 성공");

			} // end of for
		} catch (Exception e) {
			throw new AgentException("CGWebService lookup 실패", e);
		}
	}
	
	
	
	/**
	 * key 값에 해당하는 문서 body 값을 얻어온다. 
	 * 접수 증서 파일 명, 접수 증서 문서 번호를 추출
	 * @param documentObj 접수 증서 문서 정보 객체 
	 * @param key body 값 key
	 * @return 접수 증서 파일 명
	 */
	private String getDocumentBodyAttribute(DocumentObject documentObj, String key){
		ItemAnonType[] arr = documentObj.getBodys();
		return CTRAgent.findItemAnonTypeArr(arr, key);
	}

	/**
	 * key 값에 해당하는 문서 body 값을 얻어온다. 
	 * 접수 증서 파일 명, 접수 증서 문서 번호를 추출
	 * @param documentObj 접수 증서 문서 정보 객체 
	 * @param key body 값 key
	 * @return 접수 증서 파일 명
	 */
	private String getDocumentHeaderAttribute(DocumentObject documentObj, String key){
		ItemAnonType[] arr = documentObj.getHeaders();
		return CTRAgent.findItemAnonTypeArr(arr, key);
	}


	/**
	 * 접수 증서 문서 복호화 
	 * @param documentObj 접수 증서 문서 정보 객체 
	 * @return 복호화 된 접수 증서
	 * @throws AgentException
	 */
	private byte[] decryptMsg(DocumentObject documentObj ) throws AgentException {
		// 복호화 
		
		byte[] result = documentObj.getAttachment();
		
		String encryptionType = getDocumentHeaderAttribute(documentObj
				, kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationEncryptionTypeCode);
		if (encryptionType != null 
				&& ( encryptionType.equals(TypeCode.EncryptionTypeCode.ENC_ONLY)
				|| encryptionType.equals(TypeCode.EncryptionTypeCode.ENC_AND_SIGN)) ) {
			try {
				result  =  SecurityUtil.decrypt( result );
			} catch (IOException e) {
				// 인증서 읽기 오류 - 암호화된 메세지 저장, report, mail, shutdown
				AgentException ae = new AgentException(e);
				ae.addAction(new MoveArchiveRcverrorAction(rcvFile, documentObj.getAttachment()));
				ae.addAction(new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId , reportDate, ReportAction.RESULT_ERROR, e.getMessage()));
				throw ae;
			} catch (Exception e) {
				// 복호화 시 오류 - 암호화된 메세지 저장, report, mail, skip
				logger.error("접수 증서 복호화 시 오류  발생 : " , e);
				new MoveArchiveRcverrorAction(rcvFile, documentObj.getAttachment()).doAct();
				new RecvMailReportActionSet(rcvFile, documentId, reportDate, "CTR 연계모듈에서 접수 증서 복호화 시 오류 ", e ).doAct();
			}
		} 
	    logger.debug("접수 증서 - " + new String(result));
	    return result;
	}

	/**
	 * 접수 증서를 Inbox 폴더에 저장하고, 보고 내역을 기록한다.
	 * @param ResponseDoc 접수 증서 Java 객체 타입
	 * @return
	 */
	private boolean WriteToFile(String originalMsg) {
		String[] token = originalMsg.split("\\|\\|");
		String fclty_cd = token[3];
	    String documentId = token[4];
	    String reportDate = token[5];
	    String typeCd = token[7];
	    String errMsg = token[8];
	    
	    // 적제 폴더 세팅
	    String date = CurrentTimeGetter.formatDate();
	    String inboxType = Configure.getInstance().getAgentInfo().getInboxfolderType();
	    String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
	    
	    try {	    	
			if ( Configure.getInstance().getAgentInfo().isInboxEnabled()) { 
				// 기관별 (INBOX) 저장
				
				String inbox_dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(inboxType ,CtrAgentEnvInfo.getInboxDirName() , fclty_cd, date);
				FileTool.writeToFile(inbox_dir + File.separator + rcvFile, originalMsg, false);
			}
			// ARCHIVE/RCV 파일 보관 
			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, CtrAgentEnvInfo.getReceiveDirName(), fclty_cd, date);
			FileTool.writeToFile(dir + File.separator + rcvFile, originalMsg, false);
		    
			// CSV 파일 기록 			
			if (typeCd != null && TypeCode.ErrorCode.OK.equals(typeCd)){ 
				new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, reportDate).doAct();
			} else {
				new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, reportDate, 
						ReportAction.RESULT_ERROR, errMsg+ "(" +typeCd+ ")").doAct();
			}
			
		    logger.debug("flatfile : " + originalMsg);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full 상황 발생 
				/*new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
						, "접수 증서 수신 후 데이타  기록 중에 DISK FULL로 인해 거래 기록을 쓰지 못하였습니다.\n" 
						 + "보고 기관 코드 : " + fclty_cd 
						 + "\n문서 번호 : " + documentId 
						 + "\n접수 증서 내용 : " + originalMsg, e).doAct();*/
				new ShutdownAction().doAct();
			} 
			// mail , log , continue
			logger.error("접수 증서를 파일에 쓰기 할 때 오류 발생, 파일명 : " + rcvFile, e);
			new RecvMailReportActionSet(rcvFile, documentId, reportDate, 
					"CTR 연계모듈에서 접수 증서를 파일에 쓰기 할 때 오류 발생 ", e ).doAct();
			return false;
		} 
		return true;
	}
	
}