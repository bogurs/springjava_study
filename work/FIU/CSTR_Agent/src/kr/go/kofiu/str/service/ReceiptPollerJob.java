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
		//logger.info("[STR]ReceiptPollerJob Polling ���� ");
				
		try{
			STRResponseDocument strdoc;
			STRAgentInfo agentInfo = STRConfigure.getInstance().getAgentInfo();
			
			// ���� ���縦 ���� ������ġ ����
			FilePathInfo filePath = agentInfo.getFilePathInfo(); 
			String acceptInboxCertPath = filePath.getINBOX_ACCEPT();
			String arriveInboxCertPath = filePath.getINBOX_ARRIVE();
			String preacceptInboxPath = filePath.getINBOX_PREACCEPT();
			String acceptArchiveCertPath = filePath.getARCHIVE_RCV_ACCEPT();
			String arriveArchiveCertPath = filePath.getARCHIVE_RCV_ARRIVE();
			String preacceptArchivePath = filePath.getARCHIVE_RCV_PREACCEPT();
			
			
			// JMS SOAP ��� �߼�,����,����,������ ���� ����
			ArrayList<String> responseData = MessageHandler.getInstance().pollingProcess();
			if(responseData.equals(null) || responseData.size()==0){
				return;
			}
			
			
			for(int i = 0 ; i < responseData.size() ; i++){
				// JMS SOAP ���� ������ ���� DocumentObject�� ��ȯ
				try{
					String responseString = responseData.get(i);
					strdoc = WriteRcvFile.parseDocumentMessage(responseString);
				}catch(AgentException e){
					logger.error("���ŵ� ���� �м� �� ������ �߻��Ͽ����ϴ�.");
					throw new AgentException(e);
				}
				// ��ī�̺� ���� ���� �� �α� ����
				//boolean getResult = WriteRcvFile.WriteToFile(strdoc,false,"RCV");
				
				// Attachment 
				String attachment = strdoc.getAttachment();
				
				String error_cd = getErrorCd(attachment);
				String error_msg = getErrorMsg(attachment);
				String flag = attachment.substring(10, 12);
				String flag_filetag = "";
				String service = "";
				
				
				// flag(MessageTypeCode) ���� ���� ���� ���ϸ� ���� ������ �����Ѵ�.
				String strFileNm = strdoc.getAttachFileNm();
				if(flag.equals("06")){
					flag_filetag = "01";
					strFileNm = strFileNm.replace(".xml","_01.RCV");
					service = "����";
				}else if(flag.equals("07")){
					flag_filetag = "02";
					strFileNm = strFileNm.replace(".xml","_02.RCV");
					service = "������";
				}else if(flag.equals("08")){
					flag_filetag = "03";
					strFileNm = strFileNm.replace(".xml","_03.RCV");
					service = "����";
				}
				
				//
				logger.info(service + "�������� = " + attachment);
				// ����ڵ尪 ����
				String orgCd = CSVUtil.getOrgCode(strdoc);
				
				//CSV Logging
				String csvText = CSVUtil.genCSV(strdoc,error_cd,error_msg);
				CSVLogger csv = new CSVLogger();
				csv.writeCsv(csvText.getBytes(),"", orgCd , false, strdoc.getDocSendDt());
				
				
				// �߼����� ���� -> ����ڵ�(4)_��¥(8)_�ϷĹ�ȣ(8).SNR
				//String strFileNm = "STR_"+ orgCd +"_"+strdoc.getDocSendDt()+"_"+strdoc.getOrgDocNum().substring(0, 8)+"_"+flag_filetag+".RCV";
				
				
				// ����Paht ����
				String destArchivePath = null;
				String destInboxPath = null;
				
				
				// ����,����, �������� �°� ������ġ ����
				if(flag.equals("06")){ // Arrive ��������
					destArchivePath = arriveArchiveCertPath;
					destInboxPath = arriveInboxCertPath;
				}else if(flag.equals("07")){ //PreAccept ����������
					destArchivePath = preacceptArchivePath;
					destInboxPath = preacceptInboxPath;
				}else if(flag.equals("08")){ //Accept ��������
					destArchivePath = acceptArchiveCertPath;
					destInboxPath = acceptInboxCertPath;
				}
				
				// ���� ��������(����,������,����)
				String date = CurrentTimeGetter.formatDate();
				
				destInboxPath = STRDocumentSenderUtil.setFilePath(agentInfo.getInboxfolderType(), destInboxPath, orgCd, date);
				destArchivePath = STRDocumentSenderUtil.setFilePath(agentInfo.getArchivefolderType(), destArchivePath, orgCd, date);
				
				FileTool.writeToFile( destInboxPath + File.separator + strFileNm, attachment, false );
				FileTool.writeToFile( destArchivePath  + File.separator + strFileNm, attachment, false );
				
			}
			
			}catch (RetryException e){
				// ���� ���� ��� �� �߰��� ���� , CTRService lookup, method ȣ�� ����
				// Internal Server Error(FIURemoteException)
				// 10 �а� ���
				//e.printStackTrace();
				int sleepTime = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo().getSLEEP_TIME();
				logger.error("���� �߰� �����  ���� ���� ���� ����(MessagePolling) ������ ������ �ֽ��ϴ�. �������� "+sleepTime+"�ʰ� �����մϴ�.");
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
