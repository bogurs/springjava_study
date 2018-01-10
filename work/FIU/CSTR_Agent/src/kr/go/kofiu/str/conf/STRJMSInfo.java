package kr.go.kofiu.str.conf;

public class STRJMSInfo {
	
	public String INITIAL_CONTEXT_FACTORY; 
	public String CONNECTION_FACTORY;
	public String PROVIDER_URL;  
	public String SND_REQUEST_Q_NAME;
	public String SND_RESPONSE_Q_NAME;
	public String RCV_RESPONSE_Q_NAME;
	public String USR_REQUEST_Q_NAME;
	public String USR_RESPONSE_Q_NAME;
	public String HEART_REQUEST_Q_NAME;
	public String HEART_RESPONSE_Q_NAME;
	public String SERVICE_OPERATION;
	public static String POLICY="";	
	public int  GET_WAIT_TIME;
	public int  RETRY_WAIT_TIME;
	public int  RETRY_COUNT;
	public int  SLEEP_TIME;
	
	public String getINITIAL_CONTEXT_FACTORY() {
		return INITIAL_CONTEXT_FACTORY;
	}
	public void setINITIAL_CONTEXT_FACTORY(String iNITIAL_CONTEXT_FACTORY) {
		INITIAL_CONTEXT_FACTORY = iNITIAL_CONTEXT_FACTORY;
	}
	public String getCONNECTION_FACTORY() {
		return CONNECTION_FACTORY;
	}
	public void setCONNECTION_FACTORY(String cONNECTION_FACTORY) {
		CONNECTION_FACTORY = cONNECTION_FACTORY;
	}
	public String getPROVIDER_URL() {
		return PROVIDER_URL;
	}
	public void setPROVIDER_URL(String pROVIDER_URL) {
		PROVIDER_URL = pROVIDER_URL;
	}
	public String getSND_REQUEST_Q_NAME() {
		return SND_REQUEST_Q_NAME;
	}
	public void setSND_REQUEST_Q_NAME(String sND_REQUEST_Q_NAME) {
		SND_REQUEST_Q_NAME = sND_REQUEST_Q_NAME;
	}
	public String getSND_RESPONSE_Q_NAME() {
		return SND_RESPONSE_Q_NAME;
	}
	public void setSND_RESPONSE_Q_NAME(String sND_RESPONSE_Q_NAME) {
		SND_RESPONSE_Q_NAME = sND_RESPONSE_Q_NAME;
	}
	public String getRCV_RESPONSE_Q_NAME() {
		return RCV_RESPONSE_Q_NAME;
	}
	public void setRCV_RESPONSE_Q_NAME(String rCV_RESPONSE_Q_NAME) {
		RCV_RESPONSE_Q_NAME = rCV_RESPONSE_Q_NAME;
	}
	public String getUSR_REQUEST_Q_NAME() {
		return USR_REQUEST_Q_NAME;
	}
	public void setUSR_REQUEST_Q_NAME(String uSR_REQUEST_Q_NAME) {
		USR_REQUEST_Q_NAME = uSR_REQUEST_Q_NAME;
	}
	public String getUSR_RESPONSE_Q_NAME() {
		return USR_RESPONSE_Q_NAME;
	}
	public void setUSR_RESPONSE_Q_NAME(String uSR_RESPONSE_Q_NAME) {
		USR_RESPONSE_Q_NAME = uSR_RESPONSE_Q_NAME;
	}
	public String getHEART_REQUEST_Q_NAME() {
		return HEART_REQUEST_Q_NAME;
	}
	public void setHEART_REQUEST_Q_NAME(String hEART_REQUEST_Q_NAME) {
		HEART_REQUEST_Q_NAME = hEART_REQUEST_Q_NAME;
	}
	public String getHEART_RESPONSE_Q_NAME() {
		return HEART_RESPONSE_Q_NAME;
	}
	public void setHEART_RESPONSE_Q_NAME(String hEART_RESPONSE_Q_NAME) {
		HEART_RESPONSE_Q_NAME = hEART_RESPONSE_Q_NAME;
	}
	public int getGET_WAIT_TIME() {
		return GET_WAIT_TIME;
	}
	public void setGET_WAIT_TIME(int gET_WAIT_TIME) {
		GET_WAIT_TIME = gET_WAIT_TIME;
	}
	
	public int getRETRY_WAIT_TIME() {
		return RETRY_WAIT_TIME;
	}
	public void setRETRY_WAIT_TIME(int rETRY_WAIT_TIME) {
		RETRY_WAIT_TIME = rETRY_WAIT_TIME;
	}
	public int getRETRY_COUNT() {
		return RETRY_COUNT;
	}
	public void setRETRY_COUNT(int rETRY_COUNT) {
		RETRY_COUNT = rETRY_COUNT;
	}
	public void setSERVICE_OPERATION(String sERVICE_OPERATION) {
		SERVICE_OPERATION = sERVICE_OPERATION;
	}
	public String getSERVICE_OPERATION() {
		// TODO Auto-generated method stub
		return SERVICE_OPERATION;
	}
	public int getSLEEP_TIME() {
		return SLEEP_TIME;
	}
	public void setSLEEP_TIME(int sLEEP_TIME) {
		SLEEP_TIME = sLEEP_TIME;
	}
	
	
	/*private String initialContextUrl; // 접속 URL
	private String connectionFactoryFromJndi; // Connection Factory
	private String sndDocumentLQ; // Send Queue
	private String sndDocumentResultLQ; // Receive Queue
	private String targetService; // IIB Target Service Name
	private String rcvDocumentLQ; // 문서 수신 Queue
	private String userCheckReqLQ; // 유저체크 송신 Q
	private String userCheckResLQ; // 유저체크 수신 Q 
	
	public String getInitialContextUrl() {
		return initialContextUrl;
	}
	public void setInitialContextUrl(String initialContextUrl) {
		this.initialContextUrl = initialContextUrl;
	}
	public String getConnectionFactoryFromJndi() {
		return connectionFactoryFromJndi;
	}
	public void setConnectionFactoryFromJndi(String connectionFactoryFromJndi) {
		this.connectionFactoryFromJndi = connectionFactoryFromJndi;
	}
	public String getSndDocumentLQ() {
		return sndDocumentLQ;
	}
	public void setSndDocumentLQ(String sndDocumentLQ) {
		this.sndDocumentLQ = sndDocumentLQ;
	}
	public String getSndDocumentResultLQ() {
		return sndDocumentResultLQ;
	}
	public void setSndDocumentResultLQ(String sndDocumentResultLQ) {
		this.sndDocumentResultLQ = sndDocumentResultLQ;
	}
	public String getTargetService() {
		return targetService;
	}
	public void setTargetService(String targetService) {
		this.targetService = targetService;
	}
	public String getRcvDocumentLQ() {
		return rcvDocumentLQ;
	}
	public void setRcvDocumentLQ(String rcvDocumentLQ) {
		this.rcvDocumentLQ = rcvDocumentLQ;
	}
	public String getUserCheckReqLQ() {
		return userCheckReqLQ;
	}
	public void setUserCheckReqLQ(String userCheckReqLQ) {
		this.userCheckReqLQ = userCheckReqLQ;
	}
	public String getUserCheckResLQ() {
		return userCheckResLQ;
	}
	public void setUserCheckResLQ(String userCheckResLQ) {
		this.userCheckResLQ = userCheckResLQ;
	}*/
	
}
