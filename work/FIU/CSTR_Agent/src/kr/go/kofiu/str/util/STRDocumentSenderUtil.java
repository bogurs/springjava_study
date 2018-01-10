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
		String error_msg = "�˼����� ������ �߻��Ͽ����ϴ�.(" + error_doc + ")";
		
		// JMS ��� ������� ���� �����ڵ� �� ���� �޽��� ����
		if (error_doc.equals("SYSTEM_ERROR")) {
			error_cd = "99";
			error_msg = "�˼����� ������ �߻��Ͽ����ϴ�.(" + error_doc + ")";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_ASCII")) {
			error_cd = "97";
			error_msg = "Vaildation ����(Ư�����ڰ� ����Ǿ����ϴ�.)";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("DUPLICATE_FILE_ERROR")) {
			error_cd = "99";
			error_msg = "�̹� ó���� ������ �����մϴ�.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("SPLITTER_ERROR")) {
			error_cd = "99";
			error_msg = "ESB���� ��뷮 ���� ���� ������ ���� �߻�";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else if (error_doc.equals("REBUILD_ERROR")) {
			error_cd = "99";
			error_msg = "ESB���� ��뷮 ���� ������� ���� �߻�";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_CREATE_STR")) {
			error_cd = "97";
			error_msg = "Vaildation ����(STR ������� ������ ������ �߻��߽��ϴ�.)";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("VALIDATION_ERROR_FALLUSER")) {
			error_cd = "98";
			error_msg = "��ȿ���� ���� ����ڵ� �Ǵ�, �����ID(UserId) �Դϴ�. ������ ��ȿ�� Ȯ�� �ʿ�.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.equals("MODULE_VERSION_ERROR")) {
			error_cd = "98";
			error_msg = "�������� ��ġ���� �ʽ��ϴ�.";
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
		}else if (error_doc.substring(0,16).equals("VALIDATION_ERROR")) {
			error_cd = "97";
			error_msg = "�������� Vaildation ����"+error_doc.substring(16,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.substring(0, 27).equals("VALIDATION_ATTACHMENT_ERROR")) {
			error_cd = "97";
			error_msg = "÷������ Vaildation ����"+error_doc.substring(28,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		} else if (error_doc.substring(0, 14).equals("GPKI_API_ERROR")) {
			error_cd = "99";
			error_msg = "�Ϻ�ȣȭ/���ڼ��� ����"+error_doc.substring(15,error_doc.length());
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}else {
			STRDocumentSenderUtil.error(strDest, error_cd, error_msg);
		}
		
	}

	public static void error(StrDestSummary strDest, String error_cd,
			String error_msg) throws Exception {

		FilePathInfo filePath = STRConfigure.getInstance().getAgentInfo()
				.getFilePathInfo(); // FilePath Info
		
		// ���� ���
		String fileNm = strDest.getFileName();
		String orgCd = strDest.getOrgCode();
		String procPath = filePath.getPROC();
		String inboxSendPath = filePath.getINBOX_SEND();
		String certArchPath = filePath.getARCHIVE_RCV_SEND();
		String errorArchPath = filePath.getARCHIVE_SEND_ERR();

		//���� ���ϸ� ����
		String moveFileNm = fileNm.substring(0, fileNm.indexOf(".xml"))
				.concat("_" + CurrentTimeGetter.formatDateTime()).concat(".xml");
		
		// �߼����� ���� -> ����ڵ�(4)_��¥(8)_�ϷĹ�ȣ(8).SNR
	/*	String strFileNm = "STR_" + strDest.getOrgCode() + "_"
				+ CurrentTimeGetter.formatDate() + "_"
				+ strDest.getFileName().substring(18, 26) + ".SNR";*/
		
		String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";

		
		
		STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
		String date = CurrentTimeGetter.formatDate();
		//errorArchPath + "/" + moveFileNm
		/*
		 *  �߼�����, Archive SEND_ERR, ������ ����
		 */
		inboxSendPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
		certArchPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), certArchPath, orgCd, date);
		errorArchPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), errorArchPath, orgCd, date);
				
		// CSV Logging
		String csvText = CSVUtil.genCSV(strDest, error_cd, error_msg);
		CSVLogger csv = new CSVLogger();
		csv.writeCsv(csvText.getBytes(), "", strDest.getOrgCode(), true, strDest.getDocSendDate());

		// STR ���� �̵� �Ǵ� ����
		if(!error_msg.equals("�������� ��ġ���� �ʽ��ϴ�.")){
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
				logger.info("Proc �����̵� ����");
			}else{
				logger.info("�����̵��� �����Ͽ����ϴ�. ������ ��������� Ȯ�� �ٶ��ϴ�.");
			}
		}
		
		// �߼����� ����
		String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg); // �߼����� ����
		logger.info("�߼��������� = " + snr);
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
			return "STR ���� �̵� ����";
		} else {
			return "STR ���� �̵� ����";
		}
	}
	
	public static String pollingRespParser(String processRequsetDocumemt) {
		// ���� ���� ���� �� String �ʵ� �� SuspicousTransactionReportResult ó�� ����
		
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
