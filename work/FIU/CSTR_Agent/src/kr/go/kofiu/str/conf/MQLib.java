package kr.go.kofiu.str.conf;


import java.util.Properties;
import javax.naming.Context;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.QueueSender;
import javax.jms.TextMessage;


public abstract class MQLib {

	public Properties p = new Properties();		
	public Context ctx;
	public TextMessage textMsg;
	public TextMessage srcTextMsg;
	public TextMessage trgTextMsg;
	
	public ObjectMessage trgObjMsg; // 10/11 Ãß°¡
	
	public TextMessage admTextMsg;
	public TextMessage errTextMsg;
	public Message msg;
/*	public String INITIAL_CONTEXT_FACTORY = "com.ibm.mq.jms.context.WMQInitialContextFactory"; 
	public String CONNECTION_FACTORY="CSTRQM";
	public String PROVIDER_URL = "192.168.2.33:1414/SYSTEM.ADMIN.SVRCONN";  
	public String SND_REQUEST_Q_NAME="SND_AP_FIU_STRF9999_R01_SEND";
	public String SND_RESPONSE_Q_NAME="SND_AP_FIU_STRF9999_R01_RECV";
	public String RCV_RESPONSE_Q_NAME="SND_AP_FIU_STRF9999_R01_RESP";
	public String USR_REQUEST_Q_NAME="CHK_AP_FIU_STRF0000_R01_SEND";
	public String USR_RESPONSE_Q_NAME="CHK_AP_FIU_STRF0000_R01_RECV";
	public String HEART_REQUEST_Q_NAME="CHK_AP_FIU_STRF0000_R01_SEND";
	public String HEART_RESPONSE_Q_NAME="CHK_AP_FIU_STRF0000_R01_RECV";*/
	
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
	
	public abstract boolean init() throws Exception;						//MQ Init
	
	public abstract void release() throws Exception;					//Con release	
	
	public abstract Context getInitialContext() throws Exception; 		// get InitialContext
	
	
	public String getCONNECTION_FACTORY() {
		return CONNECTION_FACTORY;
	}

	public void setCONNECTION_FACTORY(String connection_factory) {
		CONNECTION_FACTORY = connection_factory;
	}
	
	public String getINITIAL_CONTEXT_FACTORY() {
		return INITIAL_CONTEXT_FACTORY;
	}

	public void setINITIAL_CONTEXT_FACTORY(String initial_context_factory) {
		INITIAL_CONTEXT_FACTORY = initial_context_factory;
	}

	public String getPROVIDER_URL() {
		return PROVIDER_URL;
	}

	public void setPROVIDER_URL(String provider_url) {
		PROVIDER_URL = provider_url;
	}
	 
	public int getGET_WAIT_TIME() {
		return GET_WAIT_TIME;
	}

	public void setGET_WAIT_TIME(int  get_wait_time) {
		GET_WAIT_TIME = get_wait_time;
	}
	
	public String getPOLICY() {
		return POLICY;
	}

	public void setPOLICY(String Policy) {
		POLICY = Policy;
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

	public String getSERVICE_OPERATION() {
		return SERVICE_OPERATION;
	}

	public void setSERVICE_OPERATION(String sERVICE_OPERATION) {
		SERVICE_OPERATION = sERVICE_OPERATION;
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
	
	

} //class ended

