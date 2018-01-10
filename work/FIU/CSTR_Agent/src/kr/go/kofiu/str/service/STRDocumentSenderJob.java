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
			String error_msg ="�߼��������ż���";
			
			STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
			
			FilePathInfo filePath = agentInfo.getFilePathInfo(); //FilePath Info
			// ���� ���
			String procPath = filePath.getPROC();
			String successArchRcvSendPath= filePath.getARCHIVE_RCV_SEND();
			String inboxSendPath = filePath.getINBOX_SEND();
			String successArchPath = filePath.getARCHIVE_SEND();
			String errorArchPath = filePath.getARCHIVE_SEND_ERR();

			
			// ���ϸ�� �� ���� ���� ����
			File strFile = new File(procPath);
			File[] strFileListTemp = strFile.listFiles();
			ArrayList<File> strFileList = new ArrayList<File>();
			for(int i = 0 ; i < strFileListTemp.length ; i++){
				if(strFileListTemp[i].isFile()){
					strFileList.add(strFileListTemp[i]);
				}
			}
			// ���ϸ�� �� ���� ���� ��
			
			// ���� ����� 0���� ��� �����Ѵ�.
			if (strFileList.size() == 0) {
				return;
			}
			
			// ������ �˻縦 ���� �ν��Ͻ� ����
			StrSendTest strSend = new StrSendTest();
			for (int i = 0; i < strFileList.size(); i++) {
				
				File fileTemp = strFileList.get(i);
				InputStream is = new FileInputStream(fileTemp.getAbsolutePath());
				
				// �ʿ��� �� ����
				
				try{
					//�������� ����
					strDest = WriteRcvFile.parseSTRDocument(is);
				}catch(Exception e){
					is.close();
					logger.error(fileTemp.getName() +" �������� Parsing �� ������ �߻��߽��ϴ�. XML ������ �ùٸ��� Ȯ���Ͻʽÿ�. ������ ARCHIVE�� �����մϴ�.");
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
				
				// ������ �˻� ����, JMS SOAP ��� �� strDoc �� ����
				FileInputStream strInputStream = new FileInputStream(fileTemp.getAbsolutePath());
				String strDocResult = strSend.action(strInputStream, fileTemp.getName());
				strInputStream.close();
				
				//���ϰ��� OK�� �ƴѰ�� ����ó�� 
				if(!strDocResult.equals("OK")){
					STRDocumentSenderUtil.error(strDocResult, strDest);
					continue;
				}
				
				// CSV, ���� ������ ���� ȯ�漼��
				String orgCd = strDest.getOrgCode();
				String time = CurrentTimeGetter.formatDateTime();
				String date = CurrentTimeGetter.formatDate();
				String orgFileNm = fileTemp.getName();
				String moveFileNm = orgFileNm.substring(0, orgFileNm.indexOf(".xml")).concat("_"+time).concat(".xml");
				
				// �߼����� ���ϸ� ����Ģ -> ����ڵ�(4)_��¥(8)_�ϷĹ�ȣ(8).SNR
				String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";
				if(isDebug){
					logger.info("�߼� ���� ���ϸ� = " + strFileNm);
				}
				
				// CSV �α�
				//CSV Logging
				String csvText = CSVUtil.genCSV(strDest,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(), ".", orgCd, true, strDest.getDocSendDate());
				
				
				String certPath = "";		// INBOX �߼����� ������
				String certBackPath = "";	// ARCHIVE/rcv �߼����� ������
				String archivePath = "";	// ARCHIVE/send STR���� �̵� ���
				
				
				// �߼����� ���� ������ ��� CSV,��������,STR ���� Archive ���� ��ġ����
				if(error_cd.equals("00")){
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, orgCd, date);
					archivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchPath, orgCd, date);
				}else{
					certPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), inboxSendPath, orgCd, date);
					certBackPath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), successArchRcvSendPath, orgCd, date);
					archivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), errorArchPath, orgCd, date);
				}
				
				// �߼� ���� ����
				String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg);
				logger.info("�߼��������� = " + snr);

				FileTool.writeToFile( certPath + File.separator + strFileNm, snr, false );
				FileTool.writeToFile( certBackPath + File.separator + strFileNm, snr, false );
				
				/*
				 * ������ �����̵�
				 * 
				 * STR������ ARCHIVE/send �� �̵� �Ǵ� �����Ѵ�.
				 * isArchiveEnabled() �� true �ϰ�� - �̵�
				 * isArchiveEnabled() �� false �ϰ�� - ����
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
						logger.info("Proc �����̵� ����");
					}else{
						logger.info("�����̵��� �����Ͽ����ϴ�. ������ ��������� Ȯ�� �ٶ��ϴ�.");
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
						logger.info("Proc �������� ����");
					}else{
						logger.info("�������ſ� �����Ͽ����ϴ�. ������ ��������� Ȯ�� �ٶ��ϴ�.");
					}
				}
			}
				
		}catch (RetryException e){
			// ���� ���� ��� �� �߰��� ���� , CTRService lookup, method ȣ�� ����
			// Internal Server Error(FIURemoteException)
			// 10 �а� ���
			try{
				
				int sleepTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getSLEEP_TIME();
				int retryTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getRETRY_COUNT();
				int waitTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getRETRY_WAIT_TIME();
				
				
				String error_cd = "99";
				String error_msg = "���� ���� ���� ����(MessageSending) "+waitTime+"�ʰ� "+retryTime+"ȸ ��õ��� �Ͽ����� ������� ���Ͽ� �������� "+sleepTime+"�ʰ� �����մϴ�.";
				
				logger.error("���� �߰� �����  ���� ���� ���� ����(MessageSending) ������ ������ �ֽ��ϴ�. �������� "+sleepTime+"�ʰ� �����մϴ�.");
				
				/*
				 * ��õ� ���� CSV ����
				 */
				String csvText = CSVUtil.genCSV(strDest,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(), ".", strDest.getOrgCode(), true, strDest.getDocSendDate());
				
				/*
				 * ��õ� ���� ���� ����
				 */
				String snr = GenReceiptDoc.generate(strDest, error_cd, error_msg);
				logger.info("�߼��������� = " + snr);
				
				STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
				FilePathInfo filePath = agentInfo.getFilePathInfo(); 
				
				// ���� ���
				String successArchRcvSendPath= filePath.getARCHIVE_RCV_SEND();
				String inboxSendPath = filePath.getINBOX_SEND();
	
				String certPath = "";		// INBOX �߼����� ������
				String certBackPath = "";	// ARCHIVE/rcv �߼����� ������
				String date = CurrentTimeGetter.formatDate();
				String strFileNm = "STR_"+strDest.getOrgCode()+"_"+strDest.getFileName().substring(9, 26)+".SNR";
				
				// �߼����� ���� ������ ��� CSV,��������,STR ���� Archive ���� ��ġ����
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