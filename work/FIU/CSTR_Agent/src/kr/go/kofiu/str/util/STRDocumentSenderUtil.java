package kr.go.kofiu.str.util;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.str.conf.FilePathInfo;
import kr.go.kofiu.str.conf.STRAgentInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.service.AgentUpdateJob;
import kr.go.kofiu.str.summary.StrDestSummary;

public class STRDocumentSenderUtil {
	private static Logger logger = Logger.getLogger(STRDocumentSenderUtil.class);

	public static void error(String error_doc, StrDestSummary strDest) throws Exception {
//		String temp = error_doc.substring(0,16);
		String error_cd = "99";
		String error_msg = "알수없는 에러가 발생하였습니다.(" + error_doc + ")";
		
		// JMS 통신 결과값에 따른 에러코드 및 에러 메시지 설정
		if (error_doc.equals("SYSTEM_ERROR")) {
			error_cd = "99";
			error_msg = "알수없는 에러가 발생하였습니다.(" + error_doc + ")";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_ASCII")) {
			error_cd = "97";
			error_msg = "Vaildation 오류(특수문자가 검출되었습니다.)";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("DUPLICATE_FILE_ERROR")) {
			error_cd = "99";
			error_msg = "이미 처리된 파일이 존재합니다.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("SPLITTER_ERROR")) {
			error_cd = "99";
			error_msg = "ESB에서 대용량 파일 분할 적제시 에러 발생";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("REBUILD_ERROR")) {
			error_cd = "99";
			error_msg = "ESB에서 대용량 파일 리빌드시 에러 발생";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_CREATE_STR")) {
			error_cd = "97";
			error_msg = "Vaildation 오류(STR 요약정보 생성중 에러가 발생했습니다.)";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_FALLUSER")) {
			error_cd = "98";
			error_msg = "유효하지 않은 기관코드 또는, 사용자ID(UserId) 입니다. 보고문서 유효성 확인 필요.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("MODULE_VERSION_ERROR")) {
			error_cd = "98";
			error_msg = "모듈버전이 일치하지 않습니다.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
		}else if (error_doc.substring(0,16).equals("VALIDATION_ERROR")) {
			error_cd = "97";
			error_msg = "기준정보 Vaildation 에러"+error_doc.substring(16,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.substring(0, 27).equals("VALIDATION_ATTACHMENT_ERROR")) {
			error_cd = "97";
			error_msg = "첨부파일 Vaildation 에러"+error_doc.substring(28,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.substring(0, 14).equals("GPKI_API_ERROR")) {
			error_cd = "99";
			error_msg = "암복호화/전자서명 에러"+error_doc.substring(15,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else {
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}
		
	}

	public static void error(StrDestSummary strDest, String error_cd,
			String error_msg) throws Exception {

		FilePathInfo filePath = STRConfigure.getInstance().getAgentInfo()
				.getFilePathInfo(); // FilePath Info
		
		// 폴더 목록
		String fileNm = strDest.getFileName();
		String orgCd = strDest.getOrgCode();
		String procPath = filePath.getPROC();
		String inboxSendPath = filePath.getINBOX_SEND();
		String certArchPath = filePath.getARCHIVE_RCV_SEND();
		String errorArchPath = filePath.getARCHIVE_SEND_ERR();

		//증서 파일명 세팅
		String moveFileNm = fileNm.substring(0, fileNm.indexOf(".xml"))
				.concat("_" + CurrentTimeGetter.formatDateTime()).concat(".xml");
		
		// 발송증서 생성 -> 기관코드(4)_날짜(8)_일렬번호(8).SNR
	/*	String strFileNm = "STR_" + strDest.getOrgCode() + "_"
				+ CurrentTimeGetter.formatDate() + "_"
				+ strDest.getFileName().substring(18, 26) + ".SNR";*/
		
		String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";

		
		
		STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
		String date = CurrentTimeGetter.formatDate();
		//errorArchPath + "/" + moveFileNm
		/*
		 *  발송증서, Archive SEND_ERR, 저장경로 지정
		 */
		inboxSendPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
		certArchPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), certArchPath, orgCd, date);
		errorArchPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), errorArchPath, orgCd, date);
				
		// CSV Logging
		String csvText = CSVUtil.genCSV(strDest, error_cd, error_msg);
		CSVLogger csv = new CSVLogger();
		csv.writeCsv(csvText.getBytes(), "", strDest.getOrgCode(), true, strDest.getDocSendDate());

		// STR 문서 이동 또는 삭제
		if(!error_msg.equals("모듈버전이 일치하지 않습니다.")){
			File fileTemp = new File(procPath + File.separator + fileNm);
			File errorFile = new File(errorArchPath + File.separator + moveFileNm); 
			//MOVE STR DOCUMENT TO SUCCESS ARCHIVE
			
			boolean isSuccess = false;
			int retryCount = 10;
			while(retryCount-- > 0){
				if( isSuccess = fileTemp.renameTo(errorFile)){
					break;
				}
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){
					throw new AgentException(e);
				}
			}
			if(isSuccess){
				logger.info("Proc 파일이동 성공");
			}else{
				logger.info("파일이동에 실패하였습니다. 파일이 사용중인지 확인 바랍니다.");
			}
		}
		
		// 발송증서 저장
		String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg); // 발송증서 생성
		logger.info("발송증서수신 = " + snr);
		FileTool.writeToFile(inboxSendPath + File.separator + strFileNm, snr, false);
		FileTool.writeToFile(certArchPath + File.separator + strFileNm, snr, false);

	}

	public static boolean checkDir(String dir) {
		File f = new File(dir);
		if (f.exists() && f.isDirectory())
			return true;

		boolean result = f.mkdirs();
		return result;
	}

	public static String printMoveResult(boolean result) {

		if (result) {
			return "STR 문서 이동 성공";
		} else {
			return "STR 문서 이동 실패";
		}
	}
	
	public static String pollingRespParser(String processRequsetDocumemt) {
		// 보고 증서 수신 후 String 필드 값 SuspicousTransactionReportResult 처리 과정
		
		//System.out.println(processRequsetDocumemt);
		String SuspicousTransactionReportResult;
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(processRequsetDocumemt));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;

			parser = dbf.newDocumentBuilder();

			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			NodeList processRequestDocumentResultNode = rootElement.getElementsByTagName("processRequestDocumentResult");
			SuspicousTransactionReportResult = processRequestDocumentResultNode.item(0).getTextContent();
			
			return SuspicousTransactionReportResult;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String setFilePath(String type, String path, String orgCd, String date){
		String resultPath = null;
		if(type.equals("type0")){
			resultPath = path;
		}else if(type.equals("type1")){
			resultPath = path + File.separator + orgCd;
		}else if(type.equals("type2")){
			resultPath = path + File.separator + date;
		}else if(type.equals("type3")){
			resultPath = path + File.separator + orgCd + File.separator + date;
		}else if(type.equals("type4")){
			resultPath = path + File.separator + date + File.separator + orgCd;
		}
		checkDir(resultPath);
		
		
		return resultPath;
	}

}
