package kr.go.kofiu.str.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.FileAttach;
import kr.go.kofiu.str.conf.STRAgentInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.security.GpkiUtil;
import kr.go.kofiu.str.summary.StrSummaryGenerator;
import kr.go.kofiu.str.summary.UtilSummary;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.AttachedXlsValidation;
import kr.go.kofiu.str.validation.attach.AttachedXmlValidation;
import kr.go.kofiu.str.validation.str.StrValidationException;
import kr.go.kofiu.str.validation.str.StrValidator;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.gpki.gpkiapi.exception.GpkiApiException;


public class StrSendTest  {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	InputStream strInstream;
//	String strFileNm;
	String sendResult;
	/**
	 * STR �� ÷������ ����ȭ ���� �� ��� ���� ���� �� ���ڼ���, ��ȣȭ
	 * @param actuals base64Encoding�� ���� STR ByteArray Data. 
	 * @throws AgentException 
	 * @throws STRConfigureException 
	 * @throws Exception 
	 */
	

	public synchronized String action(InputStream strInstream, String strFileNm) throws RetryException, AgentException, STRConfigureException {
		logger.info("STR ���� ������ �˻� ����");
		this.strInstream = strInstream;
		//this.strFileNm = strFileNm;
		byte[] actuals = null;
		byte[] base64DecodedAddiFile = null;

		// Validation Check
		StrValidator sv = null;
		try{
			sv = new StrValidator(strInstream);
			sv.getStrDocument().validate();
			sv.validate();
		}catch(ValidationException e){
			//�޽��� Ÿ���ڵ� ����
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(java.lang.IllegalArgumentException e){
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(org.xml.sax.SAXException e){
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(Exception e){
			//15.03.17 ���� 
			// ���� SYSTEM_ERROR�� ��쿡�� �߼������� ���� ������ ������ �ʰ� �־��µ� ���� ������ e.getMessage�� ����
			return "SYSTEM_ERROR("+e.getMessage()+")";
		}

		
		String orgCd = sv.getOrgCd();
		
		/*
		 *  �ߺ����� üũ
		 *  
		 *  ������ STR�� ������ ������ /Message/ARCHIVE/send �� �̹� �����ϴ��� ����.
		 */
		
		logger.info("�ߺ� ���ϸ� üũ");
		// ����üũ�� ������ �����´�.
		
		STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
		String fPath = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_SEND();
		String date = CurrentTimeGetter.formatDate(); //
		fPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), fPath, orgCd, date);
		
		
		// Archive ���� üũ
		STRDocumentSenderUtil.checkDir(fPath);
		
		// �ߺ����� ���� ����
		File archiveSendStr = new File(fPath);
		
		for (File strFile : archiveSendStr.listFiles()) {
			if (!strFile.isDirectory()
					&& strFileNm.substring(0, 26).equals(strFile.getName().substring(0, 26))) {
				// �ߺ����� ó�� ����ó��
				
				return "DUPLICATE_FILE_ERROR"; // �ߺ����� ó��
			}
		}		
		
		/*
		 * ����� ����
		 * 
		 * ����ڵ�� �������̵�� ����� �����Ѵ�.
		 */
		logger.info("����� ���� ����");
		/*try{
			String userID = sv.getStrDocument().getSTR().getOrganization().getMainAuthor().getUserid();
			String xmlData = XmlParserData.getUserCheckXML(userID, orgCd);
			
			
			UtilSummary us = MessageHandler.getInstance().sendUserCheck(xmlData, GUIDUtil.getGUID_FIUF0000(orgCd));
			 String csvText = CSVUtil.genUtilCSV(us.getErrorCd(), us.getErrorMsg());
			 logger.info("STR USERCHECK ���� ó�� (�ڵ� : "+ us.getErrorCd() + " | ��� : " + us.getErrorMsg()+")");
			 CSVLogger csv = new CSVLogger();
			 csv.writeCsv(csvText.getBytes(), Controller.USERCHECK , STRConfigure.getInstance().getAgentInfo().getId(), true , null);
			if(!us.getErrorCd().equals("00")){
				// ����üũ ���� ����
				return "VALIDATION_ERROR_FALLUSER";
			}
			
		}catch (RetryException e) {
			throw new RetryException(e);
		}*/
		
		
		logger.info("STR ÷������ ���� ����");
		// ÷������ Validation Check
		String fileName = null;
		STRDocument strDoc = sv.getStrDocument();
		
		// ascii �˻�
		if(!checkAscii(strDoc)){
			// �ƽ�Ű�ڵ� ����
			return "VALIDATION_ERROR_ASCII";
		}
		
		
		/*
		 * ÷������ ����
		 * 
		 * ÷������ �������� �ʼ����� �����Ѵ�.
		 * 
		 */
		FileAttach[] fileAttaches = strDoc.getSTR().getFileAttachArray();
		AttachedXlsValidation attachedXlsValidation = new AttachedXlsValidation(orgCd);
		AttachedXmlValidation attachedXmlValidation = new AttachedXmlValidation(orgCd);

		
		try{
			// ÷������ ���翩�� üũ
			//if (fileAttaches != null && !fileAttaches.isEmpty()) {
			if (fileAttaches != null && fileAttaches.length > 0) {
	
				for (FileAttach attachedFile : fileAttaches) {
	
					fileName = attachedFile.getFileName().getStringValue();
	
					// ÷������ base64Decoding
					base64DecodedAddiFile = Base64.decodeBase64(attachedFile.getBody());
					// ÷������ ���� üũ
					if (fileName.endsWith(".xml")) {
						// ÷������ xml Validation Check
						attachedXmlValidation.xmlValidation(base64DecodedAddiFile);
	
					} else if (fileName.endsWith(".xls")) {
						// ÷������ xls Validation Check
						attachedXlsValidation.xlsValidation(base64DecodedAddiFile);
					}
				}
			}
		}
		catch(Exception e){
			// STR ������ ����ó��
			return "VALIDATION_ATTACHMENT_ERROR("+e.getMessage()+")";
		}
		logger.info("STR ���� ������ �˻� ���� �Ϸ�");
		logger.info("�۽� ���μ��� ebXML ���� ����");
		
		// ������� ����
		try{
			String GUID = GUIDUtil.getGUID_FIUF9998(orgCd);
			//int cutSize = 30*1024*1024; 
			// 15.04.10 �캻 �ӵ����Ϸ� ���Ͽ� 1�ް��� ������ ����  
			int cutSize = 1*1024*1024;
			//int cutSize = 1*1024;
			
			/*
			 * ebXML ���� �� ����
			 * 
			 * ����ebxml �뷮�� 30m �̻��ΰ�� �����͸� 30m �� �߶� �����մϴ�.
			 */			
			if(strDoc.toString().length() > cutSize){  // Over 30m
				logger.info("STR ������ 30M�� �ʰ��Ͽ� ���� ������ �����մϴ�.");
				String procPath = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getPROC();
				String filePath = procPath+"/"+GUID;
				File splitterFile = new File(filePath);
				
				//proc/guid ���� ����
				STRDocumentSenderUtil.checkDir(splitterFile.getAbsolutePath());
				
				// ���� ���� �� ���ڼ��� ó��
				byte[] data = makeSign(orgCd, strDoc.toString());
				
				// ���ڼ��� ó�� ���� ����
				saveFile(procPath+"/"+strFileNm+"_", data);
				
				// ���� ����
				FileSplitter fs = new FileSplitter(new File(procPath+"/"+strFileNm+"_") , splitterFile.getAbsolutePath()+"/");
				String splitterResult = fs.split(cutSize);
				if(!splitterResult.equals("OK")){
					logger.error(splitterResult);
				}
				int maxseq = fs.getMaxseq();
				
				// ���ϻ��� Ȯ��
				while(true){
					if(splitterFile.list().length==(maxseq+1)){
						break;
					}
				}
				
				// ���ڼ��� ó�� ���� ����
				//new File(filePath+strFileNm+"_").delete();
				
				System.out.println(new File(procPath+"/"+strFileNm+"_").delete());
				
				// ������ ���Ϻ� ����
				for(int i = 0 ; i <= maxseq ; i++){
					String fileNm = filePath+"/"+strFileNm+"_.part"+i;
					//String fileNm = strFileNm+"_.part"+i;
					StrSummaryGenerator strSummaryGen = new StrSummaryGenerator(strDoc, orgCd , fileNm, GUID, (i+1), (maxseq+1));
					actuals = (byte[]) strSummaryGen.action();
					String sendResultXML = MessageHandler.getInstance().sendProcess(new String(actuals),orgCd);
					sendResult = STRDocumentSenderUtil.pollingRespParser(sendResultXML);
					if(sendResult.equals("OK")){
						logger.info("�������� "+(maxseq+1)+"�� �� "+(i+1)+"�� ó�� ����");
						continue;
					}
					
				}
				
				// ���� ���ϻ���
				int fileNum = splitterFile.list().length;
				for(int i = 0 ; i < fileNum ; i++){
					String splitterfilepath[] = splitterFile.list();
					new File(filePath+"/"+splitterfilepath[0]).delete();
					//System.out.println(new File(filePath+"/"+splitterfilepath[0]).delete());
				}
				// ���� ����
				//System.out.println(new File(filePath).delete());
				new File(filePath).delete();
				
				
				
			}else{ // Under 30M 
				logger.info("STR ebXML ���� ����");
				StrSummaryGenerator strSummaryGen = new StrSummaryGenerator(strDoc, orgCd ,strFileNm, GUID, 1, 1);
				//actuals = strSummaryGen.generate();
				actuals = (byte[]) strSummaryGen.action();
				String sendResultXML = MessageHandler.getInstance().sendProcess(new String(actuals),GUID);
				
				logger.info("�߼����� ���ż���");
				
				sendResult = STRDocumentSenderUtil.pollingRespParser(sendResultXML);
			}
			if(!sendResult.equals("OK")){
				return sendResult;
			}

		}
		
		catch(RetryException e){
			// ��õ� ó��
			throw new RetryException(e);
		}
		catch(AgentException e){
			// ��õ� ó��
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}
		catch(java.lang.NullPointerException e){
			// ��õ� ó��
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(GpkiApiException e){
			return "GPKI_API_ERROR("+e.getMessage()+")";
		}
		catch(Exception e){
			// ebXML ���� �� ���۽� ����ó��
			return "VALIDATION_ERROR_CREATE_STR("+e.getMessage()+")";
		}
		
		return sendResult;
		
	}
	
	
	
	
	public boolean checkAscii(STRDocument strDoc){
		String checkValue[] = new String[4];
		checkValue[0] = strDoc.getSTR().getOrganization().getOrgName().getCode(); // ����ڵ�
		checkValue[1]  = strDoc.getSTR().getOrganization().getOrgName().getStringValue(); // �����
		checkValue[2]  = strDoc.getSTR().getOrganization().getMainAuthor().getUserid(); // ���� ����ڸ�
		checkValue[3]  = strDoc.getSTR().getOrganization().getManager(); // ���� å���ڸ�
		
		for(int i = 0 ; i < checkValue.length ; i++ ){
			if(!specialPhraseCheck(checkValue[i])){
				return false;
			}
		}
		
		return true;
	}
	
	//��������, ����å���ڸ�, �������ڸ�

		/* 20100210 ����ȯ CSR2010-0035*/
		/* ? ���ڿ� Ȯ��ƽ�Ű �ڵ尪�� ���� ���ڴ� false�� �����Ѵ�. */
		public boolean specialPhraseCheck(String s) {
		   if(s != null && !s.equals("")) {
		      int strLen = s.length();
		      for (int i = 0 ; i < strLen ; i++) {
		      int j = (int)s.charAt(i);
		         // 63 : ? , 
		         if(j == 63 || (j>=128 && j <= 255) ){
		            return false;
		         }
		      }
		   }
		return true;
		}

	private byte[] makeSign(String orgCd, String data) throws Exception{
		STRAgentInfo ai = STRConfigure.getInstance().getAgentInfo();
		String securityCode = "";
		byte[] signedData = null;
		boolean encryptYN = ai.getFiuInfo().getEncryptionInfo().getKeyManageInfo().isEnabled();
		boolean signedYN = ai.getReportOrgInfo(orgCd).getEncryptionInfo().getSigningInfo().isEnabled();
		
		if(!encryptYN && !signedYN){
			securityCode = "00";
		}else if(encryptYN && !signedYN){
			securityCode = "01";
		}else if(!encryptYN && signedYN){
			securityCode = "02";
		}else if(encryptYN && signedYN){
			securityCode = "03";
		}
		
		if(securityCode.equals("00")){
			signedData = Base64.encodeBase64(data.getBytes());
		}else if(securityCode.equals("01")){
			signedData = Base64.encodeBase64(GpkiUtil.getInstance().encrypt(data.getBytes(), orgCd));
		}else if(securityCode.equals("02")){
			signedData = GpkiUtil.getInstance().makeSignature(data.getBytes());
		}else if(securityCode.equals("03")){
			byte[] encryptData = GpkiUtil.getInstance().encrypt(data.getBytes(), orgCd);
			signedData = GpkiUtil.getInstance().makeSignature(encryptData);
		}
		
		return signedData;
	}
	private void saveFile(String fileFath, byte[] data) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fileFath));
		out.write(new String(data));
		out.close();
	}
	
	


	
}
