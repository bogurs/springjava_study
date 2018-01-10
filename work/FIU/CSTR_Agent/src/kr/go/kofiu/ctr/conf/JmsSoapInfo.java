package kr.go.kofiu.ctr.conf;


public class JmsSoapInfo {
	
	public static final String SNR_ERROR_MSG = "ESB에서 수신 처리 에러 발생. 관리자에게 문의하십시오.";
	public static final String ESB_CHECK_OK = "OK";
	public static final String ESB_CHECK_ERROR = "ERROR";
	
	public String INITIAL_CONTEXT_FACTORY; 
	public String QMANAGER;
	public String INITIAL_CONTEXT_URL;  
	public String REQUSET_Q_NAME;
	public String RESPONSE_Q_NAME;
	public String REPLY_Q_NAME;	
	public String TARGET_SERVICE;
	
	public String ESBCHECKSERVICE_REQUEST_Q_NAME;
	public String ESBCHECKSERVICE_RESPONSE_Q_NAME;
	
	public String GET_WAIT_TIME;
	public String RETRY_WAIT_TIME;
	public String RETRY_COUNT;
	public String SLEEP_TIME;
	
	public String getINITIAL_CONTEXT_FACTORY() {
		return INITIAL_CONTEXT_FACTORY;
	}

	public void setINITIAL_CONTEXT_FACTORY(String initial_context_factory) {
		INITIAL_CONTEXT_FACTORY = initial_context_factory;
	}	

	public String getQMANAGER() {
		return QMANAGER;
	}

	public void setQMANAGER(String qMANAGER) {
		QMANAGER = qMANAGER;
	}

	public String getINITIAL_CONTEXT_URL() {
		return INITIAL_CONTEXT_URL;
	}

	public void setINITIAL_CONTEXT_URL(String iNITIAL_CONTEXT_URL) {
		INITIAL_CONTEXT_URL = iNITIAL_CONTEXT_URL;
	}

	public String getREQUSET_Q_NAME() {
		return REQUSET_Q_NAME;
	}

	public void setREQUSET_Q_NAME(String rEQUSET_Q_NAME) {
		REQUSET_Q_NAME = rEQUSET_Q_NAME;
	}

	public String getRESPONSE_Q_NAME() {
		return RESPONSE_Q_NAME;
	}

	public void setRESPONSE_Q_NAME(String rESPONSE_Q_NAME) {
		RESPONSE_Q_NAME = rESPONSE_Q_NAME;
	}

	public String getREPLY_Q_NAME() {
		return REPLY_Q_NAME;
	}

	public void setREPLY_Q_NAME(String rEPLY_Q_NAME) {
		REPLY_Q_NAME = rEPLY_Q_NAME;
	}

	public String getTARGET_SERVICE() {
		return TARGET_SERVICE;
	}
	public void setTARGET_SERVICE(String tARGET_SERVICE) {
		TARGET_SERVICE = tARGET_SERVICE;
	}

	public String getESBCHECKSERVICE_REQUEST_Q_NAME() {
		return ESBCHECKSERVICE_REQUEST_Q_NAME;
	}

	public void setESBCHECKSERVICE_REQUEST_Q_NAME(
			String eSBCHECKSERVICE_REQUEST_Q_NAME) {
		ESBCHECKSERVICE_REQUEST_Q_NAME = eSBCHECKSERVICE_REQUEST_Q_NAME;
	}

	public String getESBCHECKSERVICE_RESPONSE_Q_NAME() {
		return ESBCHECKSERVICE_RESPONSE_Q_NAME;
	}

	public void setESBCHECKSERVICE_RESPONSE_Q_NAME(
			String eSBCHECKSERVICE_RESPONSE_Q_NAME) {
		ESBCHECKSERVICE_RESPONSE_Q_NAME = eSBCHECKSERVICE_RESPONSE_Q_NAME;
	}

	public String getGET_WAIT_TIME() {
		return GET_WAIT_TIME;
	}

	public void setGET_WAIT_TIME(String gET_WAIT_TIME) {
		GET_WAIT_TIME = gET_WAIT_TIME;
	}

	public String getRETRY_WAIT_TIME() {
		return RETRY_WAIT_TIME;
	}

	public void setRETRY_WAIT_TIME(String rETRY_WAIT_TIME) {
		RETRY_WAIT_TIME = rETRY_WAIT_TIME;
	}

	public String getRETRY_COUNT() {
		return RETRY_COUNT;
	}

	public void setRETRY_COUNT(String rETRY_COUNT) {
		RETRY_COUNT = rETRY_COUNT;
	}

	public String getSLEEP_TIME() {
		return SLEEP_TIME;
	}

	public void setSLEEP_TIME(String sLEEP_TIME) {
		SLEEP_TIME = sLEEP_TIME;
	}	
	
	
	
}
