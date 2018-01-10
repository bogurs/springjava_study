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
 * ����   �׷�� : CTR �ý���
 * ����   ������ : ���� ��� Agent
 * ��        �� : �ֱ������� ���������� RCV ȭ���� ���߱������ ���� �����ϴ� ����� ������. 
 * ��   ��   �� : ykyu 
 * ��   ��   �� : 2005. 8. 2
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
	 * ���� ���� ���ϸ� 
	 */
	private String rcvFile ;
	
	/**
	 * ���� ���� ���� ��ȣ
	 */
	private String documentId;
	
	/**
	 * ���� ���� 
	 */
	private String reportDate;

	/**
	 * ���� ���� ("V0.88" �Ǵ� "V0.89")
	 */
	private String reportVersion;
	
	/**
	 * ���� ���� ���� ����. ���� ���� �� ��ȣȭ �ÿ� ���� �߻� �� INBOX_ERROR ������ 
	 * ������ �����Ѵ�. 
	 * ������ �б� ������ �߻��� ��� ��ȣȭ�� �޼��� �����ϰ� ���� ����� �����, 
	 * �����ڿ���  mail�� �����ϰ�  shutdown�Ѵ�.
	 * 
	 * 2014. 9. 10 ��������
	 * ��ȣȭ�� ������ ���� ������ Base64 ��ȣȭ�� �������� ���ŵȴ�.
	 * ����, ��ȣȭ�� base64�� ���� ��ȣȭ �������� �ٲ�.
	 * 
	 * @throws AgentException 
	 **/
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// �׽�Ʈ�� ��� CGMessagePollerJob�� ������� ����. 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}
		
		DocumentObject documentObj = null;
	    
		try {
//			logger.info("CGMessagePollerJob Processing...");
			CTRMessagePollerCommand command = new CTRMessagePollerCommand(numOfRetries, timeToWait);
			
			//while( (documentObj = (DocumentObject)command.invoke()) != null ) {//WLI�� �̿��� call
				//rcvFile�� base64�� ��ȣȭ �Ǿ� ����
			ArrayList<DocumentObject> resultList = (ArrayList<DocumentObject>) command.invoke();
			
			if(resultList==null || resultList.size()==0){
				return;
			}
			
			for(int i = 0 ; i < resultList.size() ; i++){
				documentObj = resultList.get(i);
				rcvFile = getDocumentBodyAttribute( documentObj , kr.go.kofiu.ctr.util.DocumentObject.AttachmentFileNameText);	

				logger.info(rcvFile + "���� ���� ���� " );
				if ( rcvFile == null ) {
					logger.warn("���� ���� ���� ���� NULL�Դϴ�. ��� " + CtrAgentEnvInfo.SND_FILE_SAMPLE + "���ϸ��� ����մϴ�.");
					rcvFile = CtrAgentEnvInfo.SND_FILE_SAMPLE;
				}
				
				rcvFile = FileTool.replaceSuffix(rcvFile, "RCV");
				
				// ���� ��ȣ 
				documentId = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDocumentID);
				if ( documentId == null )
					documentId = CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE;
					
				// ���� ����
				reportDate = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDate);
				if ( reportDate == null )
					reportDate = DateUtil.getDateTime(DateUtil.yyyyMMdd);
				
				// ���� ����
				reportVersion = getDocumentBodyAttribute( documentObj 
						, kr.go.kofiu.ctr.util.DocumentObject.ReportFormVersionID);
				if ( reportVersion == null )
					reportVersion = kr.go.kofiu.ctr.util.DocumentObject.DOCUMENT_VERSION_NEW;
				
				/**
				 * 2014. 9. 12 �߰� ����
				 * ��ȣȭ �� Base64 Decoding �ܰ�
				 */
				byte[] documentObjAtcmt = documentObj.getAttachment();
				byte[] originalDcuObjAtcmt;				
				
				/**
				 * 15.02.26 ����
				 * ���ڼ����� �޽����� ���� üũ�Ͽ� ���ڼ��� �Ǿ� �־����� ó���ϰ�
				 * �ƴ� �� base64 decoding ���� ó��
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
				
				// ��ȣȭ
				//2014. 9. 10 base64 ��ȣȭ�������� ����
				byte[] original = originalDcuObjAtcmt;
				logger.info("���� ���� ���� ���: " + new String(original));
//			    ResponseCurrencyTransactionReportDocument ResponseDoc = null;
//			    try {
//			    	ResponseDoc = XMLtoFlatFile.generateResponseDoc(original);
//				} catch (Exception e) {
//					AgentException ae = new AgentException(e);
//					ae.addAction(new FatalActionSet("���� ���� XML��  FlatFile�� ��ȯ �������� ���� �߻� : " + new String(original) , e));
//					throw ae;
//				}
				
			    if ( !WriteToFile(new String(original)) ) {
			    	continue;
			    }
				logger.info(rcvFile + " ���� ���� ���� ����");

			} // end of for
		} catch (Exception e) {
			throw new AgentException("CGWebService lookup ����", e);
		}
	}
	
	
	
	/**
	 * key ���� �ش��ϴ� ���� body ���� ���´�. 
	 * ���� ���� ���� ��, ���� ���� ���� ��ȣ�� ����
	 * @param documentObj ���� ���� ���� ���� ��ü 
	 * @param key body �� key
	 * @return ���� ���� ���� ��
	 */
	private String getDocumentBodyAttribute(DocumentObject documentObj, String key){
		ItemAnonType[] arr = documentObj.getBodys();
		return CTRAgent.findItemAnonTypeArr(arr, key);
	}

	/**
	 * key ���� �ش��ϴ� ���� body ���� ���´�. 
	 * ���� ���� ���� ��, ���� ���� ���� ��ȣ�� ����
	 * @param documentObj ���� ���� ���� ���� ��ü 
	 * @param key body �� key
	 * @return ���� ���� ���� ��
	 */
	private String getDocumentHeaderAttribute(DocumentObject documentObj, String key){
		ItemAnonType[] arr = documentObj.getHeaders();
		return CTRAgent.findItemAnonTypeArr(arr, key);
	}


	/**
	 * ���� ���� ���� ��ȣȭ 
	 * @param documentObj ���� ���� ���� ���� ��ü 
	 * @return ��ȣȭ �� ���� ����
	 * @throws AgentException
	 */
	private byte[] decryptMsg(DocumentObject documentObj ) throws AgentException {
		// ��ȣȭ 
		
		byte[] result = documentObj.getAttachment();
		
		String encryptionType = getDocumentHeaderAttribute(documentObj
				, kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationEncryptionTypeCode);
		if (encryptionType != null 
				&& ( encryptionType.equals(TypeCode.EncryptionTypeCode.ENC_ONLY)
				|| encryptionType.equals(TypeCode.EncryptionTypeCode.ENC_AND_SIGN)) ) {
			try {
				result  =  SecurityUtil.decrypt( result );
			} catch (IOException e) {
				// ������ �б� ���� - ��ȣȭ�� �޼��� ����, report, mail, shutdown
				AgentException ae = new AgentException(e);
				ae.addAction(new MoveArchiveRcverrorAction(rcvFile, documentObj.getAttachment()));
				ae.addAction(new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId , reportDate, ReportAction.RESULT_ERROR, e.getMessage()));
				throw ae;
			} catch (Exception e) {
				// ��ȣȭ �� ���� - ��ȣȭ�� �޼��� ����, report, mail, skip
				logger.error("���� ���� ��ȣȭ �� ����  �߻� : " , e);
				new MoveArchiveRcverrorAction(rcvFile, documentObj.getAttachment()).doAct();
				new RecvMailReportActionSet(rcvFile, documentId, reportDate, "CTR �����⿡�� ���� ���� ��ȣȭ �� ���� ", e ).doAct();
			}
		} 
	    logger.debug("���� ���� - " + new String(result));
	    return result;
	}

	/**
	 * ���� ������ Inbox ������ �����ϰ�, ���� ������ ����Ѵ�.
	 * @param ResponseDoc ���� ���� Java ��ü Ÿ��
	 * @return
	 */
	private boolean WriteToFile(String originalMsg) {
		String[] token = originalMsg.split("\\|\\|");
		String fclty_cd = token[3];
	    String documentId = token[4];
	    String reportDate = token[5];
	    String typeCd = token[7];
	    String errMsg = token[8];
	    
	    // ���� ���� ����
	    String date = CurrentTimeGetter.formatDate();
	    String inboxType = Configure.getInstance().getAgentInfo().getInboxfolderType();
	    String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
	    
	    try {	    	
			if ( Configure.getInstance().getAgentInfo().isInboxEnabled()) { 
				// ����� (INBOX) ����
				
				String inbox_dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(inboxType ,CtrAgentEnvInfo.getInboxDirName() , fclty_cd, date);
				FileTool.writeToFile(inbox_dir + File.separator + rcvFile, originalMsg, false);
			}
			// ARCHIVE/RCV ���� ���� 
			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, CtrAgentEnvInfo.getReceiveDirName(), fclty_cd, date);
			FileTool.writeToFile(dir + File.separator + rcvFile, originalMsg, false);
		    
			// CSV ���� ��� 			
			if (typeCd != null && TypeCode.ErrorCode.OK.equals(typeCd)){ 
				new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, reportDate).doAct();
			} else {
				new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, reportDate, 
						ReportAction.RESULT_ERROR, errMsg+ "(" +typeCd+ ")").doAct();
			}
			
		    logger.debug("flatfile : " + originalMsg);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full ��Ȳ �߻� 
				/*new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
						, "���� ���� ���� �� ����Ÿ  ��� �߿� DISK FULL�� ���� �ŷ� ����� ���� ���Ͽ����ϴ�.\n" 
						 + "���� ��� �ڵ� : " + fclty_cd 
						 + "\n���� ��ȣ : " + documentId 
						 + "\n���� ���� ���� : " + originalMsg, e).doAct();*/
				new ShutdownAction().doAct();
			} 
			// mail , log , continue
			logger.error("���� ������ ���Ͽ� ���� �� �� ���� �߻�, ���ϸ� : " + rcvFile, e);
			new RecvMailReportActionSet(rcvFile, documentId, reportDate, 
					"CTR �����⿡�� ���� ������ ���Ͽ� ���� �� �� ���� �߻� ", e ).doAct();
			return false;
		} 
		return true;
	}
	
}