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
	 * STR 및 첨부파일 정형화 수행 후 요약 정보 생성 및 전자서명, 암호화
	 * @param actuals base64Encoding된 최종 STR ByteArray Data. 
	 * @throws AgentException 
	 * @throws STRConfigureException 
	 * @throws Exception 
	 */
	

	public synchronized String action(InputStream strInstream, String strFileNm) throws RetryException, AgentException, STRConfigureException {
		logger.info("STR 문서 정형성 검사 수행");
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
			//메시지 타입코드 에러
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(java.lang.IllegalArgumentException e){
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(org.xml.sax.SAXException e){
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(Exception e){
			//15.03.17 수정 
			// 기존 SYSTEM_ERROR의 경우에는 발송증서에 에러 내역을 남기지 않고 있었는데 에러 내역을 e.getMessage로 남김
			return "SYSTEM_ERROR("+e.getMessage()+")";
		}

		
		String orgCd = sv.getOrgCd();
		
		/*
		 *  중복파일 체크
		 *  
		 *  폴링한 STR과 동일한 파일이 /Message/ARCHIVE/send 에 이미 존재하는지 검증.
		 */
		
		logger.info("중복 파일명 체크");
		// 파일체크할 폴더를 가져온다.
		
		STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
		String fPath = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getARCHIVE_SEND();
		String date = CurrentTimeGetter.formatDate(); //
		fPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), fPath, orgCd, date);
		
		
		// Archive 폴더 체크
		STRDocumentSenderUtil.checkDir(fPath);
		
		// 중복파일 검증 시작
		File archiveSendStr = new File(fPath);
		
		for (File strFile : archiveSendStr.listFiles()) {
			if (!strFile.isDirectory()
					&& strFileNm.substring(0, 26).equals(strFile.getName().substring(0, 26))) {
				// 중복파일 처리 예외처리
				
				return "DUPLICATE_FILE_ERROR"; // 중복파일 처리
			}
		}		
		
		/*
		 * 사용자 검증
		 * 
		 * 기관코드와 유저아이디로 사용자 검증한다.
		 */
		logger.info("사용자 검증 수행");
		/*try{
			String userID = sv.getStrDocument().getSTR().getOrganization().getMainAuthor().getUserid();
			String xmlData = XmlParserData.getUserCheckXML(userID, orgCd);
			
			
			UtilSummary us = MessageHandler.getInstance().sendUserCheck(xmlData, GUIDUtil.getGUID_FIUF0000(orgCd));
			 String csvText = CSVUtil.genUtilCSV(us.getErrorCd(), us.getErrorMsg());
			 logger.info("STR USERCHECK 서비스 처리 (코드 : "+ us.getErrorCd() + " | 결과 : " + us.getErrorMsg()+")");
			 CSVLogger csv = new CSVLogger();
			 csv.writeCsv(csvText.getBytes(), Controller.USERCHECK , STRConfigure.getInstance().getAgentInfo().getId(), true , null);
			if(!us.getErrorCd().equals("00")){
				// 유저체크 실패 에러
				return "VALIDATION_ERROR_FALLUSER";
			}
			
		}catch (RetryException e) {
			throw new RetryException(e);
		}*/
		
		
		logger.info("STR 첨부파일 검증 수행");
		// 첨부파일 Validation Check
		String fileName = null;
		STRDocument strDoc = sv.getStrDocument();
		
		// ascii 검사
		if(!checkAscii(strDoc)){
			// 아스키코드 에러
			return "VALIDATION_ERROR_ASCII";
		}
		
		
		/*
		 * 첨부파일 검증
		 * 
		 * 첨부파일 문서내의 필수값을 검증한다.
		 * 
		 */
		FileAttach[] fileAttaches = strDoc.getSTR().getFileAttachArray();
		AttachedXlsValidation attachedXlsValidation = new AttachedXlsValidation(orgCd);
		AttachedXmlValidation attachedXmlValidation = new AttachedXmlValidation(orgCd);

		
		try{
			// 첨부파일 존재여부 체크
			//if (fileAttaches != null && !fileAttaches.isEmpty()) {
			if (fileAttaches != null && fileAttaches.length > 0) {
	
				for (FileAttach attachedFile : fileAttaches) {
	
					fileName = attachedFile.getFileName().getStringValue();
	
					// 첨부파일 base64Decoding
					base64DecodedAddiFile = Base64.decodeBase64(attachedFile.getBody());
					// 첨부파일 형식 체크
					if (fileName.endsWith(".xml")) {
						// 첨부파일 xml Validation Check
						attachedXmlValidation.xmlValidation(base64DecodedAddiFile);
	
					} else if (fileName.endsWith(".xls")) {
						// 첨부파일 xls Validation Check
						attachedXlsValidation.xlsValidation(base64DecodedAddiFile);
					}
				}
			}
		}
		catch(Exception e){
			// STR 검증시 예외처리
			return "VALIDATION_ATTACHMENT_ERROR("+e.getMessage()+")";
		}
		logger.info("STR 문서 정형성 검사 수행 완료");
		logger.info("송신 프로세스 ebXML 생성 시작");
		
		// 요약정보 생성
		try{
			String GUID = GUIDUtil.getGUID_FIUF9998(orgCd);
			//int cutSize = 30*1024*1024; 
			// 15.04.10 우본 속도저하로 인하여 1메가로 사이즈 변경  
			int cutSize = 1*1024*1024;
			//int cutSize = 1*1024;
			
			/*
			 * ebXML 생성 및 전송
			 * 
			 * 전송ebxml 용량이 30m 이상인경우 데이터를 30m 씩 잘라서 전송합니다.
			 */			
			if(strDoc.toString().length() > cutSize){  // Over 30m
				logger.info("STR 문서가 30M이 초과하여 분할 전송을 시행합니다.");
				String procPath = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getPROC();
				String filePath = procPath+"/"+GUID;
				File splitterFile = new File(filePath);
				
				//proc/guid 폴더 생성
				STRDocumentSenderUtil.checkDir(splitterFile.getAbsolutePath());
				
				// 파일 분할 전 전자서명 처리
				byte[] data = makeSign(orgCd, strDoc.toString());
				
				// 전자서명 처리 파일 저장
				saveFile(procPath+"/"+strFileNm+"_", data);
				
				// 파일 분할
				FileSplitter fs = new FileSplitter(new File(procPath+"/"+strFileNm+"_") , splitterFile.getAbsolutePath()+"/");
				String splitterResult = fs.split(cutSize);
				if(!splitterResult.equals("OK")){
					logger.error(splitterResult);
				}
				int maxseq = fs.getMaxseq();
				
				// 파일생성 확인
				while(true){
					if(splitterFile.list().length==(maxseq+1)){
						break;
					}
				}
				
				// 전자서명 처리 파일 삭제
				//new File(filePath+strFileNm+"_").delete();
				
				System.out.println(new File(procPath+"/"+strFileNm+"_").delete());
				
				// 생성된 파일별 전송
				for(int i = 0 ; i <= maxseq ; i++){
					String fileNm = filePath+"/"+strFileNm+"_.part"+i;
					//String fileNm = strFileNm+"_.part"+i;
					StrSummaryGenerator strSummaryGen = new StrSummaryGenerator(strDoc, orgCd , fileNm, GUID, (i+1), (maxseq+1));
					actuals = (byte[]) strSummaryGen.action();
					String sendResultXML = MessageHandler.getInstance().sendProcess(new String(actuals),orgCd);
					sendResult = STRDocumentSenderUtil.pollingRespParser(sendResultXML);
					if(sendResult.equals("OK")){
						logger.info("분할파일 "+(maxseq+1)+"건 중 "+(i+1)+"건 처리 성공");
						continue;
					}
					
				}
				
				// 분할 파일삭제
				int fileNum = splitterFile.list().length;
				for(int i = 0 ; i < fileNum ; i++){
					String splitterfilepath[] = splitterFile.list();
					new File(filePath+"/"+splitterfilepath[0]).delete();
					//System.out.println(new File(filePath+"/"+splitterfilepath[0]).delete());
				}
				// 폴더 삭제
				//System.out.println(new File(filePath).delete());
				new File(filePath).delete();
				
				
				
			}else{ // Under 30M 
				logger.info("STR ebXML 전송 수행");
				StrSummaryGenerator strSummaryGen = new StrSummaryGenerator(strDoc, orgCd ,strFileNm, GUID, 1, 1);
				//actuals = strSummaryGen.generate();
				actuals = (byte[]) strSummaryGen.action();
				String sendResultXML = MessageHandler.getInstance().sendProcess(new String(actuals),GUID);
				
				logger.info("발송증서 수신성공");
				
				sendResult = STRDocumentSenderUtil.pollingRespParser(sendResultXML);
			}
			if(!sendResult.equals("OK")){
				return sendResult;
			}

		}
		
		catch(RetryException e){
			// 재시도 처리
			throw new RetryException(e);
		}
		catch(AgentException e){
			// 재시도 처리
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}
		catch(java.lang.NullPointerException e){
			// 재시도 처리
			return "VALIDATION_ERROR("+e.getMessage()+")";
		}catch(GpkiApiException e){
			return "GPKI_API_ERROR("+e.getMessage()+")";
		}
		catch(Exception e){
			// ebXML 생성 및 전송시 예외처리
			return "VALIDATION_ERROR_CREATE_STR("+e.getMessage()+")";
		}
		
		return sendResult;
		
	}
	
	
	
	
	public boolean checkAscii(STRDocument strDoc){
		String checkValue[] = new String[4];
		checkValue[0] = strDoc.getSTR().getOrganization().getOrgName().getCode(); // 기관코드
		checkValue[1]  = strDoc.getSTR().getOrganization().getOrgName().getStringValue(); // 기관명
		checkValue[2]  = strDoc.getSTR().getOrganization().getMainAuthor().getUserid(); // 보고 담당자명
		checkValue[3]  = strDoc.getSTR().getOrganization().getManager(); // 보고 책임자명
		
		for(int i = 0 ; i < checkValue.length ; i++ ){
			if(!specialPhraseCheck(checkValue[i])){
				return false;
			}
		}
		
		return true;
	}
	
	//보고기관명, 보고책임자명, 보고담당자명

		/* 20100210 진정환 CSR2010-0035*/
		/* ? 문자와 확장아스키 코드값을 가진 문자는 false를 리턴한다. */
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
