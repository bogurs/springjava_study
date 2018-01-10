package kr.go.kofiu.ctr.conf;

import java.io.File;

import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent에서 쓰이는 전역 상수를 정의하는 Helper 클래스
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CtrAgentEnvInfo {

	/**
	 * 인증서 폴더 
	 */
	public static String CERTIFICATE_DIR;

	/**
	 * FIU 공개키 인증서 폴더 
	 */
	public static String FIU_PUBLIC_KEY_DIR;

	/**
	 * 사용자 인증서 폴더 
	 */
	public static String USER_CERTIFICATE_DIR;
	
	/**
	 * 한국정보인증 GPKI 라이센트 디렉토리 추가
	 * 신규로 추가됨 
	 */
	public static String GPKICONF_DIR;

	/**
	 * 암호화를 위한 FIU 공개키 ( CTR -> FIU ) 
	 */
	// 
	public static String FIU_PUBLIC_KEY;

	/**
	 * 설정 파일 폴더 
	 */
	public static final String CONFIG_DIR = "./ctr_config";
	
	/**
	 * Message 폴더 
	 */
	//public static String MESSAGE_DIR = "./Messages";
	public static String HOME;
	
	/**
	 * Message 폴더 
	 */
	//public static String MESSAGE_DIR = "./Messages";
	public static String MESSAGE_DIR;
	
	/**
	 *  Message Box Folder structure - INBOX 폴더 명 
	 */
	public static String INBOX_DIR_NAME;

	/**
	 *  Message Box Folder structure - OUTBOX 폴더 명 
	 */
	public static String OUTBOX_DIR_NAME;
	/**
	 *  Message Box Folder structure - ARCH 폴더 명 
	 */
	public static String ARCHIVE_DIR_NAME;
	public static String ARCHIVE_SEND_DIR_NAME;
	public static String ARCHIVE_SEND_ERROR_DIR_NAME;
	public static String ARCHIVE_RECEIVE_DIR_NAME;
	public static String ARCHIVE_RECEIVE_ERROR_DIR_NAME;
	
	/**
	 *  Message Box Folder structure - PROC 폴더 명 
	 */
	public static String PROC_DIR_NAME;
	/**
	 * Agent 메세지 박스의 Send PROC 디렉토리
	 */
	public static String PROC_SEND_DIR_NAME;
	/**
	 * Agent 메세지 박스의 Receive PROC 디렉토리
	 */
	public static String PROC_RECEIVE_DIR_NAME;

	
	/**
	 *  Message Box Folder structure - REPORT 폴더 명 
	 */
	public static String REPORT_DIR_NAME;
	/**
	 *  Message Box Folder structure - REPORT 폴더 아래의 RESP 폴더 명 
	 */
	public static String REPORT_RESP_DIR_NAME;
	/**
	 *  Message Box Folder structure - REPORT 폴더 아래의 SEND 폴더 명 
	 */
	public static String REPORT_SEND_DIR_NAME;
	
	/**
	 * 보고 문서 파일 명 sample
	 */
	public static String SND_FILE_SAMPLE;
	/**
	 * 접수 증서 파일 명 sample
	 */
	public static String RCV_FILE_SAMPLE;
	
	/**
	 * DOCUMENT ID SAMPLE
	 */
	public static String DOCUMENT_ID_SAMPLE;
	
	/**
	 * 전송 메일 보낸이 nick name
	 */
	public static String FIUCTRAgentMailFrom;
	
	/**
	 * 20140922 추가사항
	 * CtrAgentEnvInfo Xml화 작업에 필요한 getter, setter
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
	 * Agent의 IP Address 값. 집중 기관에서 인증을 위해 사용된다. 
	public static String IPStr ;
	 */
	
	/**
	 * Agent 인증서 디렉토리
	 * @return Agent 인증서 디렉토리
	 */
	public static String getFIUPublicKeyFile()
	{
		return FIU_PUBLIC_KEY_DIR + File.separator +  FIU_PUBLIC_KEY;
	}

	/**
	 * Agent 메세지 박스 ARCH의 Send ERROR 디렉토리
	 * @return Agent 메세지 박스 ARCH의 Send ERROR 디렉토리
	 */
	public static String getSendErrorDirName()
	{
		//String dirName = ARCHIVE_SEND_ERROR_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_SEND_ERROR_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	

	/**
	 * Agent 메세지 박스 ARCH의 Send 디렉토리
	 * @return Agent 메세지 박스 ARCH의 Send 디렉토리
	 */
	public static String getSendDirName() 
	{
		//String dirName = ARCHIVE_SEND_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_SEND_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	

	/**
	 * Agent 메세지 박스 ARCH의 Receive 디렉토리
	 * @return Agent 메세지 박스 ARCH의 Receive 디렉토리
	 */
	public static String getReceiveDirName() {
		//String dirName = ARCHIVE_RECEIVE_DIR_NAME + File.separator + DateUtil.getDateTime(DateUtil.yyyyMMdd);
		String dirName = ARCHIVE_RECEIVE_DIR_NAME;
		FileTool.checkDirectory(dirName);
		return  dirName;
	}	
	
	/**
	 * Agent 메세지 박스 INBOX 디렉토리
	 * @return Agent 메세지 박스 INBOX 디렉토리
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
	 * 기관별로 폴더를 생성하여 MessageBox에서 처리하는 지 여부를 검사하여 적절한 폴더값을 리턴한다.
	 * 이때 해당 폴더가 존재하는지 체크하고 없으면 생성한다. 
	 * 날짜별로 ARCHIVE가 생성되기 때문에 매번 체크한다.
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
		// 기관별 
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
