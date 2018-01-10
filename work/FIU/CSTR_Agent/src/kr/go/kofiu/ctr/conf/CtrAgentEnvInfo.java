package kr.go.kofiu.ctr.conf;

import java.io.File;

import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Agent���� ���̴� ���� ����� �����ϴ� Helper Ŭ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CtrAgentEnvInfo {

	/**
	 * ������ ���� 
	 */
	public static String CERTIFICATE_DIR;

	/**
	 * FIU ����Ű ������ ���� 
	 */
	public static String FIU_PUBLIC_KEY_DIR;

	/**
	 * ����� ������ ���� 
	 */
	public static String USER_CERTIFICATE_DIR;
	
	/**
	 * �ѱ��������� GPKI ���̼�Ʈ ���丮 �߰�
	 * �űԷ� �߰��� 
	 */
	public static String GPKICONF_DIR;

	/**
	 * ��ȣȭ�� ���� FIU ����Ű ( CTR -> FIU ) 
	 */
	// 
	public static String FIU_PUBLIC_KEY;

	/**
	 * ���� ���� ���� 
	 */
	public static final String CONFIG_DIR = "./ctr_config";
	
	/**
	 * Message ���� 
	 */
	//public static String MESSAGE_DIR = "./Messages";
	public static String HOME;
	
	/**
	 * Message ���� 
	 */
	//public static String MESSAGE_DIR = "./Messages";
	public static String MESSAGE_DIR;
	
	/**
	 *  Message Box Folder structure - INBOX ���� �� 
	 */
	public static String INBOX_DIR_NAME;

	/**
	 *  Message Box Folder structure - OUTBOX ���� �� 
	 */
	public static String OUTBOX_DIR_NAME;
	/**
	 *  Message Box Folder structure - ARCH ���� �� 
	 */
	public static String ARCHIVE_DIR_NAME;
	public static String ARCHIVE_SEND_DIR_NAME;
	public static String ARCHIVE_SEND_ERROR_DIR_NAME;
	public static String ARCHIVE_RECEIVE_DIR_NAME;
	public static String ARCHIVE_RECEIVE_ERROR_DIR_NAME;
	
	/**
	 *  Message Box Folder structure - PROC ���� �� 
	 */
	public static String PROC_DIR_NAME;
	/**
	 * Agent �޼��� �ڽ��� Send PROC ���丮
	 */
	public static String PROC_SEND_DIR_NAME;
	/**
	 * Agent �޼��� �ڽ��� Receive PROC ���丮
	 */
	public static String PROC_RECEIVE_DIR_NAME;

	
	/**
	 *  Message Box Folder structure - REPORT ���� �� 
	 */
	public static String REPORT_DIR_NAME;
	/**
	 *  Message Box Folder structure - REPORT ���� �Ʒ��� RESP ���� �� 
	 */
	public static String REPORT_RESP_DIR_NAME;
	/**
	 *  Message Box Folder structure - REPORT ���� �Ʒ��� SEND ���� �� 
	 */
	public static String REPORT_SEND_DIR_NAME;
	
	/**
	 * ���� ���� ���� �� sample
	 */
	public static String SND_FILE_SAMPLE;
	/**
	 * ���� ���� ���� �� sample
	 */
	public static String RCV_FILE_SAMPLE;
	
	/**
	 * DOCUMENT ID SAMPLE
	 */
	public static String DOCUMENT_ID_SAMPLE;
	
	/**
	 * ���� ���� ������ nick name
	 */
	public static String FIUCTRAgentMailFrom;
	
	/**
	 * 20140922 �߰�����
	 * CtrAgentEnvInfo Xmlȭ �۾��� �ʿ��� getter, setter
	 */
	
	public String getCERTIFICATE_DIR() {
		return CERTIFICATE_DIR;
	}

	public void setCERTIFICATE_DIR(String cERTIFICATE_DIR) {
		CERTIFICATE_DIR = cERTIFICATE_DIR;
	}


	public String getFIU_PUBLIC_KEY_DIR() {
		return FIU_PUBLIC_KEY_DIR;
	}


	public void setFIU_PUBLIC_KEY_DIR(String fIU_PUBLIC_KEY_DIR) {
		FIU_PUBLIC_KEY_DIR = CERTIFICATE_DIR + fIU_PUBLIC_KEY_DIR;
	}


	public String getUSER_CERTIFICATE_DIR() {
		return USER_CERTIFICATE_DIR;
	}


	public void setUSER_CERTIFICATE_DIR(String uSER_CERTIFICATE_DIR) {
		USER_CERTIFICATE_DIR = CERTIFICATE_DIR + uSER_CERTIFICATE_DIR;
	}


	public String getGPKICONF_DIR() {
		return GPKICONF_DIR;
	}


	public void setGPKICONF_DIR(String gPKICONF_DIR) {
		GPKICONF_DIR = gPKICONF_DIR;
	}


	public String getFIU_PUBLIC_KEY() {
		return FIU_PUBLIC_KEY;
	}


	public void setFIU_PUBLIC_KEY(String fIU_PUBLIC_KEY) {
		FIU_PUBLIC_KEY = fIU_PUBLIC_KEY;
	}

	public String getHOME() {
		return HOME;
	}

	public void setHOME(String hOME) {
		HOME = hOME;
	}

	public String getMESSAGE_DIR() {
		return MESSAGE_DIR;
	}


	public void setMESSAGE_DIR(String mESSAGE_DIR) {
		MESSAGE_DIR = HOME + mESSAGE_DIR;
	}

	public String getINBOX_DIR_NAME() {
		return INBOX_DIR_NAME;
	}


	public void setINBOX_DIR_NAME(String iNBOX_DIR_NAME) {
		INBOX_DIR_NAME = MESSAGE_DIR + iNBOX_DIR_NAME;
	}


	public String getOUTBOX_DIR_NAME() {
		return OUTBOX_DIR_NAME;
	}


	public void setOUTBOX_DIR_NAME(String oUTBOX_DIR_NAME) {
		OUTBOX_DIR_NAME = MESSAGE_DIR + oUTBOX_DIR_NAME;
	}


	public String getARCHIVE_DIR_NAME() {
		return ARCHIVE_DIR_NAME;
	}


	public void setARCHIVE_DIR_NAME(String aRCHIVE_DIR_NAME) {
		ARCHIVE_DIR_NAME = MESSAGE_DIR + aRCHIVE_DIR_NAME;
	}


	public String getARCHIVE_SEND_DIR_NAME() {
		return ARCHIVE_SEND_DIR_NAME;
	}


	public void setARCHIVE_SEND_DIR_NAME(String aRCHIVE_SEND_DIR_NAME) {
		ARCHIVE_SEND_DIR_NAME = ARCHIVE_DIR_NAME + aRCHIVE_SEND_DIR_NAME;
	}


	public String getARCHIVE_SEND_ERROR_DIR_NAME() {
		return ARCHIVE_SEND_ERROR_DIR_NAME;
	}


	public void setARCHIVE_SEND_ERROR_DIR_NAME(
			String aRCHIVE_SEND_ERROR_DIR_NAME) {
		ARCHIVE_SEND_ERROR_DIR_NAME = ARCHIVE_DIR_NAME + aRCHIVE_SEND_ERROR_DIR_NAME;
	}


	public String getARCHIVE_RECEIVE_DIR_NAME() {
		return ARCHIVE_RECEIVE_DIR_NAME;
	}


	public void setARCHIVE_RECEIVE_DIR_NAME(String aRCHIVE_RECEIVE_DIR_NAME) {
		ARCHIVE_RECEIVE_DIR_NAME = ARCHIVE_DIR_NAME + aRCHIVE_RECEIVE_DIR_NAME;
	}

	public String getARCHIVE_RECEIVE_ERROR_DIR_NAME() {
		return ARCHIVE_RECEIVE_ERROR_DIR_NAME;
	}


	public void setARCHIVE_RECEIVE_ERROR_DIR_NAME(
			String aRCHIVE_RECEIVE_ERROR_DIR_NAME) {
		ARCHIVE_RECEIVE_ERROR_DIR_NAME = ARCHIVE_DIR_NAME + aRCHIVE_RECEIVE_ERROR_DIR_NAME;
	}


	public String getPROC_DIR_NAME() {
		return PROC_DIR_NAME;
	}


	public void setPROC_DIR_NAME(String pROC_DIR_NAME) {
		PROC_DIR_NAME = MESSAGE_DIR + pROC_DIR_NAME;
	}


	public String getPROC_SEND_DIR_NAME() {
		return PROC_SEND_DIR_NAME;
	}


	public void setPROC_SEND_DIR_NAME(String pROC_SEND_DIR_NAME) {
		PROC_SEND_DIR_NAME = PROC_DIR_NAME + pROC_SEND_DIR_NAME;
	}


	public String getPROC_RECEIVE_DIR_NAME() {
		return PROC_RECEIVE_DIR_NAME;
	}


	public void setPROC_RECEIVE_DIR_NAME(String pROC_RECEIVE_DIR_NAME) {
		PROC_RECEIVE_DIR_NAME = PROC_DIR_NAME + pROC_RECEIVE_DIR_NAME;
	}


	public String getREPORT_DIR_NAME() {
		return REPORT_DIR_NAME;
	}


	public void setREPORT_DIR_NAME(String rEPORT_DIR_NAME) {
		REPORT_DIR_NAME = MESSAGE_DIR + rEPORT_DIR_NAME;
	}


	public String getREPORT_RESP_DIR_NAME() {
		return REPORT_RESP_DIR_NAME;
	}


	public void setREPORT_RESP_DIR_NAME(String rEPORT_RESP_DIR_NAME) {
		REPORT_RESP_DIR_NAME = REPORT_DIR_NAME + rEPORT_RESP_DIR_NAME;
	}


	public String getREPORT_SEND_DIR_NAME() {
		return REPORT_SEND_DIR_NAME;
	}


	public void setREPORT_SEND_DIR_NAME(String rEPORT_SEND_DIR_NAME) {
		REPORT_SEND_DIR_NAME = REPORT_DIR_NAME + rEPORT_SEND_DIR_NAME;
	}


	public String getSND_FILE_SAMPLE() {
		return SND_FILE_SAMPLE;
	}


	public void setSND_FILE_SAMPLE(String sND_FILE_SAMPLE) {
		SND_FILE_SAMPLE = sND_FILE_SAMPLE;
	}


	public String getRCV_FILE_SAMPLE() {
		return RCV_FILE_SAMPLE;
	}


	public void setRCV_FILE_SAMPLE(String rCV_FILE_SAMPLE) {
		RCV_FILE_SAMPLE = rCV_FILE_SAMPLE;
	}


	public String getDOCUMENT_ID_SAMPLE() {
		return DOCUMENT_ID_SAMPLE;
	}


	public void setDOCUMENT_ID_SAMPLE(String dOCUMENT_ID_SAMPLE) {
		DOCUMENT_ID_SAMPLE = dOCUMENT_ID_SAMPLE;
	}


	public String getFIUCTRAgentMailFrom() {
		return FIUCTRAgentMailFrom;
	}


	public void setFIUCTRAgentMailFrom(String fIUCTRAgentMailFrom) {
		FIUCTRAgentMailFrom = fIUCTRAgentMailFrom;
	}
	
	/**
	 * Agent�� IP Address ��. ���� ������� ������ ���� ���ȴ�. 
	public static String IPStr ;
	 */
	
	/**
	 * Agent ������ ���丮
	 * @return Agent ������ ���丮
	 */
	public static String getFIUPublicKeyFile()
	{
		return FIU_PUBLIC_KEY_DIR + File.separator +  FIU_PUBLIC_KEY;
	}

	/**
	 * Agent �޼��� �ڽ� ARCH�� Send ERROR ���丮
	 * @return Agent �޼��� �ڽ� ARCH�� Send ERROR ���丮
	 */
	public static String getSendErrorDirName()
	{
		//String dirName = ARCHIVE_SEND_ERROR_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_SEND_ERROR_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	

	/**
	 * Agent �޼��� �ڽ� ARCH�� Send ���丮
	 * @return Agent �޼��� �ڽ� ARCH�� Send ���丮
	 */
	public static String getSendDirName() 
	{
		//String dirName = ARCHIVE_SEND_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_SEND_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	

	/**
	 * Agent �޼��� �ڽ� ARCH�� Receive ���丮
	 * @return Agent �޼��� �ڽ� ARCH�� Receive ���丮
	 */
	public static String getReceiveDirName() {
		//String dirName = ARCHIVE_RECEIVE_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_RECEIVE_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	
	
	/**
	 * Agent �޼��� �ڽ� INBOX ���丮
	 * @return Agent �޼��� �ڽ� INBOX ���丮
	 */
	public static String getInboxDirName() {
		String dirName = INBOX_DIR_NAME;		
		/*if ( Configure.getInstance().getAgentInfo().isSaveInboxPerDate() ) {
			dirName += File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
			FileTool.checkDirectory(dirName);
		}*/
		return  dirName;
	}	
	
	/**
	 * ������� ������ �����Ͽ� MessageBox���� ó���ϴ� �� ���θ� �˻��Ͽ� ������ �������� �����Ѵ�.
	 * �̶� �ش� ������ �����ϴ��� üũ�ϰ� ������ �����Ѵ�. 
	 * ��¥���� ARCHIVE�� �����Ǳ� ������ �Ź� üũ�Ѵ�.
	 * @param originDir
	 * @param fclty_cd
	 * @return
	 */
	public static String checkFcltyAndGetDirSeamless(String type, String path, String orgCd, String date)  {		
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
		new File(resultPath).mkdirs();
		FileTool.checkDirectory(resultPath);
		
		
		return resultPath;
		// ����� 
		/*if ( Configure.getInstance().getAgentInfo().getMessageBoxEnv().isFolderPerFcltyCode() ) {
			String dir = originDir + File.separator + fclty_cd;
			FileTool.checkDirectory(dir);
			return dir;
		}	
		
		return originDir;*/
	}
	
	public static String getRemoteWSDLUrl()  {
		CgServer cgServer = Configure.getInstance().getCgServer();
		String wsdlURL = "http://" + cgServer.getIp() + ":" + cgServer.getPort() 
			+ "/cgservice/CGWebService.jws?WSDL=";
		return wsdlURL;
	}

	public static String getLocalWSDLUrl()  {
		File f = new File(CtrAgentEnvInfo.CONFIG_DIR + File.separator + "CTR_Moudle_Check.wsdl");
		String wsdlURL = "file:///" + f.getAbsolutePath();
		return wsdlURL;
	}

}
