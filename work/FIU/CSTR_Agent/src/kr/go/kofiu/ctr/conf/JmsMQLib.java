package kr.go.kofiu.ctr.conf;


import java.util.ArrayList;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.util.RetryException;

import org.apache.log4j.Logger;

import com.ibm.mq.MQException;

public class JmsMQLib {
	
	public Properties p = new Properties();		
	public Context ctx;
	public TextMessage textMsg;
	public TextMessage srcTextMsg;
	public TextMessage trgTextMsg;
	
	public ObjectMessage trgObjMsg; // 10/11 추가
	
	public TextMessage admTextMsg;
	public TextMessage errTextMsg;
	public Message msg;
	
	public static String POLICY="";
	public float  GET_WAIT_TIME=1;

	/**-------------------- 변수 선언 ------------------------------------*/
	static Logger logger = Logger.getLogger(JmsMQLib.class);
	
	private QueueConnectionFactory qcf;
	private QueueConnection qCon;
	private QueueSession qSes;
	private String state = "Start";
	
	// Receiver: SourceQ, AdminQ(메시지를 받음,get)
	// Sender: TargetQ, ErrorQ, AdminQ(메시지를 보냄, put)
	private QueueSender REQUSET_QUEUE_SENDER; 
	private QueueReceiver RESPONSE_QUEUE_RECEIVER; 
	private QueueReceiver REPLY_QUEUE_RECEIVER; 
	private QueueSender USR_REQUSER_QUEUE_SENDER; 
	private QueueReceiver USR_RESPONSE_QUEUE_RECEIVER;
	private QueueSender CHK_REQUEST_QUEUE_SENDER; 
	private QueueReceiver CHK_RESPONSE_QUEUE_RECEIVER;
	
	/*private QueueReceiver srcQRCV; 			// SourceQ에서 메시지를 get
	private QueueReceiver admQRCV; 			// AdminQ에서 메시지를 get
	private QueueSender admQSND; 			// AdminQ에 메시지를 put
	private QueueSender trgQSND; 				// TargetQ에 메시지를 put
	private QueueSender errQSND; 				// ErrorQ에 메시지를 put
*/	private Queue REQUSET_QUEUE;
	private Queue RESPONSE_QUEUE;
	private Queue REPLY_QUEUE;
	private Queue USR_REQUSER_QUEUE;
	private Queue USR_RESPONSE_QUEUE;
	private Queue CHK_REQUEST_QUEUE;
	private Queue CHK_RESPONSE_QUEUE;
	
	private String INITIAL_CONTEXT_FACTORY;
	private String QMANAGER;
	private String INITIAL_CONTEXT_URL;
	private String REQUSET_Q_NAME;
	private String RESPONSE_Q_NAME;
	private String REPLY_Q_NAME;
	private String CHK_REQUEST_Q_NAME;
	private String CHK_RESPONSE_Q_NAME;
	private String TARGET_SERVICE;
	
	/*private static JmsMQLib instance = null;
	
	public static JmsMQLib getInstance() throws Exception{
		if(instance == null){
			instance = new JmsMQLib();
		}
		return instance;
	}
	
	private JmsMQLib() throws Exception{
		init();
	}*/
	/**
	 * Connection을 맺고, 연결 성공 여부를 반환한다.
	 * 반환값: true - connection 성공, false - connection 실패
	 */
	public boolean init() throws AgentException {		
		String INITIAL_CONTEXT_FACTORY = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getINITIAL_CONTEXT_FACTORY(); 
		String QMANAGER=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getQMANAGER();
		String INITIAL_CONTEXT_URL = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getINITIAL_CONTEXT_URL();  
		String TARGET_SERVICE=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getTARGET_SERVICE();
		
		String REQUSET_Q_NAME=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getREQUSET_Q_NAME();
		String RESPONSE_Q_NAME=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRESPONSE_Q_NAME();
		String REPLY_Q_NAME = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getREPLY_Q_NAME();	
		String CHK_REQUEST_Q_NAME=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getESBCHECKSERVICE_REQUEST_Q_NAME();
		String CHK_RESPONSE_Q_NAME=Configure.getInstance().getAgentInfo().getJmsSoapInfo().getESBCHECKSERVICE_RESPONSE_Q_NAME();
		
		this.INITIAL_CONTEXT_FACTORY = INITIAL_CONTEXT_FACTORY;
		this.QMANAGER = QMANAGER;
		this.INITIAL_CONTEXT_URL = INITIAL_CONTEXT_URL;
		this.REQUSET_Q_NAME = REQUSET_Q_NAME;
		this.RESPONSE_Q_NAME = RESPONSE_Q_NAME;
		this.REPLY_Q_NAME = REPLY_Q_NAME;
		this.CHK_REQUEST_Q_NAME = CHK_REQUEST_Q_NAME;
		this.CHK_RESPONSE_Q_NAME = CHK_RESPONSE_Q_NAME;
		this.TARGET_SERVICE = TARGET_SERVICE;
		
		boolean sucCon = false;
		try {
			logger.debug("\t[JMS Connection " + state + "---------------------]");

			logger.debug("\t 1) getInitialContext");
			ctx = getInitialContext();

			logger.debug("\t 2) getConnectionFactory");
			qcf = getConnectionFactory();// CF lookup
			
			logger.debug("\t 3) getQueue");
//			srcQ = getSrcQueue();			 // Queue lookup
			REQUSET_QUEUE = getREQUSET_Queue();
			RESPONSE_QUEUE = getRESPONSE_Queue();
			REPLY_QUEUE = getREPLY_Queue();
			CHK_REQUEST_QUEUE = getCHK_REQUEST_Queue();
			CHK_RESPONSE_QUEUE = getCHK_RESPONSE_Queue();
//			USR_REQUSER_QUEUE = getUSR_REQUSER_Queue();
//			USR_RESPONSE_QUEUE = getUSR_RESPONSE_Queue();

			logger.debug("\t 4) getQueueSession");
			qSes = getQSession();			 // Create session
			
			logger.debug("\t[JMS Connection Success!-----------------]\n");
			sucCon = true;

		} catch (Exception e) {
			logger.debug("\t[JMS Connection Fail!----------------------]");    
			throw new AgentException(e);
		}
		return sucCon;
	} 

	
	/**
	 * 현재의 QueueSession을 반환한다.
	 * @return qSes
	 */
	public QueueSession getQSes(){
		return qSes;
	}
	

	/**
	 * 모든 자원을 release 한다.
	 */
	public void release() {
		try {
			if (REQUSET_QUEUE_SENDER != null)
				REQUSET_QUEUE_SENDER.close();
			if (RESPONSE_QUEUE_RECEIVER != null)
				RESPONSE_QUEUE_RECEIVER.close();
			if (REPLY_QUEUE_RECEIVER != null)
				REPLY_QUEUE_RECEIVER.close();
			if (USR_REQUSER_QUEUE_SENDER != null)
				USR_REQUSER_QUEUE_SENDER.close();
			if (USR_RESPONSE_QUEUE_RECEIVER != null)
				USR_RESPONSE_QUEUE_RECEIVER.close();
			if (CHK_REQUEST_QUEUE_SENDER != null)
				CHK_REQUEST_QUEUE_SENDER.close();
			if (CHK_RESPONSE_QUEUE_RECEIVER != null)
				CHK_RESPONSE_QUEUE_RECEIVER.close();
			if (qSes != null)
				qSes.close();
			if (qCon != null)
				qCon.close();
			if (ctx != null)
				ctx.close();
			
		} catch (Exception e) {
			logger.fatal("[Fail] Resource Release!" + e.getMessage());
			logger.fatal(new AgentException(e));
		}
	}
	
	
	/**
	 * 현재의 QueueSession을 rollback 한다.
	 * @throws Exception
	 */
	public void rollback() throws AgentException{
		try {
			if(qSes != null){
				qSes.rollback();
			} else {
				throw new AgentException("QueueSession이 null이어서 rollback 할 수 없습니다.");
			}
		} catch (JMSException e) {
			logger.info("[Fail] Rollback!");
			new AgentException(e);
		}
	}
	
	
	/**
	 * 현재의 QueueSession을 commit 한다.
	 * @throws Exception
	 */
	public void commit() throws Exception{
		try{
			if(qSes != null){
				qSes.commit();
			} else {
				throw new AgentException("QueueSession이 null이어서 commit 할 수 없습니다.");
			}
		} catch (JMSException e) {
			logger.info("[Fail] Commit!");
			logger.fatal(new AgentException(e));
			throw e;
		}
	}

	
	
	/**-------------------- Message Put 메소드------------------------------------*/
	
		/**
	 * Message 형식의 메시지를 받아 TargetQ에 put한다. String 형식의 msgdata는 로그 파일에 데이터 내용을 로깅할 때 사용된다.
	 * 수신 서버와의 연결에 오류가 발생했을 시엔 연결이 될때까지 재시도 후 메시지를 재처리한다.
	 * @param msg
	 * @param msgdata
		 * @throws AgentException 
	 * @throws Exception
	 */
	public Message putMsg(Message msg, String msgdata, String GUID) throws AgentException {
		try {
			
			if (REQUSET_QUEUE_SENDER == null){
				REQUSET_QUEUE_SENDER = getREQUSET_QUEUE_SENDER(); // TargetQ에 메시지 넣을 Sender 객체 얻어옴
			}
			//if (RESPONSE_QUEUE_RECEIVER == null){
				RESPONSE_QUEUE_RECEIVER = getRESPONSE_QUEUE_RECEIVER(GUID);
			//}
				
			String getFcltyCdFromGUID = GUID.substring(0, 4);
			
			msg.setStringProperty("SOAPJMS_contentType", "SOAP11-MTOM");	
			msg.setStringProperty("SOAPJMS_bindingVersion", "1.0");
			msg.setJMSReplyTo(RESPONSE_QUEUE);
			msg.setJMSCorrelationID(GUID);
			msg.setStringProperty("fcltyCd", getFcltyCdFromGUID);
			String setStringPropertyString = "jms:jndi:" + REQUSET_QUEUE
					+ "?jndiConnectionFactoryName=" + QMANAGER
					+ "&amp;jndiInitialContextFactory=" + INITIAL_CONTEXT_FACTORY
					+ "&amp;jndiURL=" + INITIAL_CONTEXT_URL
					+ "&amp;replyToName=" + RESPONSE_QUEUE
					+ "&amp;targetService=" + TARGET_SERVICE;
			msg.setStringProperty("SOAPJMS_requestURI", setStringPropertyString);
			REQUSET_QUEUE_SENDER.send(msg);
			
			commit();
			Message getMessage = null;
			
			boolean retry = true;
			long GET_WAIT_TIME = Long.parseLong(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getGET_WAIT_TIME());
			int RETRY_WAIT_TIME = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_WAIT_TIME());
			int RETRY_COUNT = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_COUNT());
			while(retry){				
				getMessage = RESPONSE_QUEUE_RECEIVER.receive(GET_WAIT_TIME*1000);
				if(getMessage == null){
					Thread.sleep(RETRY_WAIT_TIME*1000);
					if(RETRY_COUNT == 0){
						throw new RetryException("발송증서 수신에 실패했습니다. Agent를 중지합니다. ESB 관리자에게 문의하십시오.");
					}else{
						logger.info("[발송증서 수신 재시도 알림] 남은 재시도 횟수 : "+RETRY_COUNT +", "+RETRY_WAIT_TIME + "초 후에 재시도 합니다.");
						RETRY_COUNT--;
					}
				}else{
					break;
				}
			}
			
			commit();
			logger.debug("[Commit] Put Message: \n" + msgdata);
			
			RESPONSE_QUEUE_RECEIVER.close();
						
			return getMessage;
			
		}
		catch (JMSException je) {
			
			//에러코드 JMSCC0005인 경우에만
			if(je.getErrorCode() != null){
				if(je.getErrorCode().equals("JMSCC0005")){
					try{
						commit();
						logger.debug("[Commit] Put Message: \n" + msgdata);
						return null;
					}catch (Exception e) {
						rollback();
						//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
						logger.debug("[Rollback] Put Message: \n" + msgdata);
						throw new RetryException(e);
					}
				}
			}
			rollback();
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			
			Throwable t = je.getLinkedException();
			if (t != null) {
				if (t instanceof JMSException) {
					JMSException je1 = (JMSException) t;
					logger.fatal(new AgentException(je1));
					throw new RetryException(je1.getErrorCode(), je1);
					
				} else if (t instanceof MQException) {
					MQException mqe = (MQException) t;
					logger.fatal(new AgentException(mqe));
					throw new RetryException(mqe);
				}
			}
			
			throw new RetryException(je); 
			
		} catch (RetryException e) {
			throw new RetryException(e);
		}catch (Exception e) {
			rollback();
			//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			throw new RetryException(e);
		}
	}
	
	/**
	 * Message 형식의 메시지를 받아 TargetQ에 put한다. String 형식의 msgdata는 로그 파일에 데이터 내용을 로깅할 때 사용된다.
	 * 수신 서버와의 연결에 오류가 발생했을 시엔 연결이 될때까지 재시도 후 메시지를 재처리한다.
	 * @param msg
	 * @param msgdata
	 * @throws Exception
	 */
	public Message putCheckMsg(Message msg, String msgdata, String GUID, String serviceMode) throws AgentException {
		try {
			if (CHK_REQUEST_QUEUE_SENDER == null){
				CHK_REQUEST_QUEUE_SENDER = getCHK_REQUEST_QUEUE_SENDER(); // TargetQ에 메시지 넣을 Sender 객체 얻어옴
			}
			//if (CHK_RESPONSE_QUEUE_RECEIVER == null){
				CHK_RESPONSE_QUEUE_RECEIVER = getCHK_RESPONSE_QUEUE_RECEIVER(GUID);
			//}
			
			msg.setStringProperty("SOAPJMS_contentType", "SOAP11-MTOM");
			msg.setStringProperty("SOAPJMS_bindingVersion", "1.0");
			msg.setJMSReplyTo(CHK_RESPONSE_QUEUE);
			msg.setJMSCorrelationID(GUID);
			String setStringPropertyString = "jms:jndi:" + CHK_REQUEST_QUEUE
					+ "?jndiConnectionFactoryName=" + QMANAGER
					+ "&amp;jndiInitialContextFactory=" + INITIAL_CONTEXT_FACTORY
					+ "&amp;jndiURL=" + INITIAL_CONTEXT_URL
					+ "&amp;replyToName=" + CHK_RESPONSE_QUEUE
					+ "&amp;targetService=" + TARGET_SERVICE;
			msg.setStringProperty("SOAPJMS_requestURI", setStringPropertyString);
			CHK_REQUEST_QUEUE_SENDER.send(msg);
			commit();
			Message getMessage = null;
			
			boolean retry = true;
			long GET_WAIT_TIME = Long.parseLong(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getGET_WAIT_TIME());
			int RETRY_WAIT_TIME = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_WAIT_TIME());
			int RETRY_COUNT = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_COUNT());
			while(retry){				
				getMessage = CHK_RESPONSE_QUEUE_RECEIVER.receive(GET_WAIT_TIME*1000);
				if(getMessage == null){
					Thread.sleep(RETRY_WAIT_TIME*1000);
					if(RETRY_COUNT == 0){
						String errorMsg = serviceMode + " 요청 수신에 실패했습니다. Agent를 일시 중지합니다. ESB 관리자에게 문의하십시오.";		
						new ReportAction(serviceMode, errorMsg).doAct();
						throw new AgentException(errorMsg);
					}else{
						logger.info("["+ serviceMode + " 요청 응답 수신 재시도 알림] 남은 재시도 횟수 : "+RETRY_COUNT +", "+RETRY_WAIT_TIME + "초 후에 재시도 합니다.");
						RETRY_COUNT--;
					}
				}else{
					break;
				}
			}
			
			commit();
			logger.debug("[Commit] Put Message: \n" + msgdata);
			
			CHK_RESPONSE_QUEUE_RECEIVER.close();
						
			return getMessage;
			
		}
		catch (JMSException je) {
			
			//에러코드 JMSCC0005인 경우에만
			if(je.getErrorCode() != null){
				if(je.getErrorCode().equals("JMSCC0005")){
					try{
						commit();
						logger.debug("[Commit] Put Message: \n" + msgdata);
						return null;
					}catch (Exception e) {
						rollback();
						//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
						logger.debug("[Rollback] Put Message: \n" + msgdata);
						throw new RetryException(e);
					}
				}
			}
			rollback();
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			
			Throwable t = je.getLinkedException();
			if (t != null) {
				if (t instanceof JMSException) {
					JMSException je1 = (JMSException) t;
					logger.fatal(new AgentException(je1));
					throw new RetryException(je1.getErrorCode(), je1);
					
				} else if (t instanceof MQException) {
					MQException mqe = (MQException) t;
					logger.fatal(new AgentException(mqe));
					throw new RetryException(mqe);
				}
			}
			
			throw new RetryException(je); 
			
		} catch (RetryException e) {
			throw new RetryException(e);
		}catch (Exception e) {
			rollback();
			//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			throw new RetryException(e);
		}
	}
	
	

	/**-------------------- Message Get 메소드------------------------------------*/

	
	/**
	 * SourceQ에서 Message 타입의 메시지를 얻어온다.(get)
	 * @return msg
	 * @throws Exception
	 */
	public synchronized ArrayList<String> getMsg() throws Exception {
		TextMessage msg = null;
		String fcltyCd = Configure.getInstance().getAgentInfo().getId();
		ArrayList<String> receiveList = new ArrayList<String>();
					
		initREPLY_QUEUE_RECEIVER(fcltyCd); // SourceQ의 메시지 얻어올 Receive 객체 얻어옴
		do {
			try {
				msg = null;
				msg = (TextMessage) REPLY_QUEUE_RECEIVER.receive((long) (GET_WAIT_TIME * 1000));
				
				if (msg != null) {
					String jMSCorrelationID = msg.getJMSCorrelationID();
					String getAgentId = jMSCorrelationID.substring(0, 4);
					if (getAgentId.equals(fcltyCd)) { // 기관코드 검사 ( 제거해도될듯 )
						receiveList.add(msg.getText()); // 리스트 추가.
					}

				}
			} catch (JMSException je) {
				if (je.getErrorCode() != null) {
					if (je.getErrorCode().equals("JMSWMQ2002")
							|| je.getErrorCode().equals("JMSCMQ0001")) {
						try { // get 하다가 connection broken

							// connection 재시도
							state = "Retry";
							release();
							while (!init()) {
								Thread.sleep(1000);
							}
							// initSrcQRCV();
						} catch (Exception e) {
							e.printStackTrace();
							throw new AgentException("connection을 재시도 하였으나 연결되지 않아 어댑터가 종료합니다.",	e);
						}
					}
				}
			}
		} while (msg != null && receiveList.size() != 500); 		//지정횟수만큼 반복수행	
			REPLY_QUEUE_RECEIVER.close();
			commit();	// 지정된 횟수만큼 수행 이후 커밋
		
		return receiveList;
	}
	
//	/**
//	 * SourceQ에서 Message 타입의 메시지를 얻어온다.(get)
//	 * @return msg
//	 * @throws Exception
//	 */
//	public Message getMsg() throws Exception {
//		Message msg = null;
//		String fcltyCd = Configure.getInstance().getAgentInfo().getId();
//		try{			
//			initREPLY_QUEUE_RECEIVER(fcltyCd); // SourceQ의 메시지 얻어올 Receive 객체 얻어옴
//			
//			msg = REPLY_QUEUE_RECEIVER.receive((long) (GET_WAIT_TIME * 1000));
//			REPLY_QUEUE_RECEIVER.close();
//			if(msg != null){
//				String jMSCorrelationID = msg.getJMSCorrelationID();
//				String getAgentId = jMSCorrelationID.substring(0, 4);
//				
//				if(getAgentId.equals(fcltyCd)){
//					commit();
//				}else{
//					rollback();
//					return null;
//				}
//			}
//			
//		}catch (JMSException je){
//			if(je.getErrorCode() != null){
//				if(je.getErrorCode().equals("JMSWMQ2002")||je.getErrorCode().equals("JMSCMQ0001")){
//					try{										//get 하다가 connection broken 
//						
//						//connection 재시도
//						state = "Retry";
//						release();
//						while(!init()){
//							Thread.sleep(1000);
//						}
////						initSrcQRCV();
//					}catch (Exception e){
//						e.printStackTrace();
//						throw new AgentException("connection을 재시도 하였으나 연결되지 않아 어댑터가 종료합니다." ,e );
//					}
//				}
//			}
//		}
//		return msg;
//	}
		

	/**
	 * AdminQ에 메시지가 있는지 확인하고, 메시지가 존재할 경우 그 값이 Y인지 체크한다.
	 * @return AdminQ에 메시지 존재시 true, 아니면 false
	 * @throws Exception
	 *//*
	public boolean getMsgAdmQ() throws Exception {
		boolean isStop = false;
		if (admQRCV == null)
			initAdmQRCV(); // AdminQ의 메시지 얻어올 Receive 객체 얻어옴

		if (getTextMsg() != null) { // 큐에서 메시지 얻어옴, 메시지가 있을 경우
			String stopcheck = admTextMsg.getText();
			if (stopcheck != null && !stopcheck.trim().equals("")) {
				if (stopcheck.equals("Y")) {
					//logger.info("Get Stop Message From " + ADM_QUEUE + "(AdminQ)!");
					isStop = true;
				} else {
				}
			}
		}
		return isStop;
	}

	
	*//**
	 * QBridge 기동시 AdminQ의 메시지를 모두 삭제한다.
	 * @throws Exception
	 *//*
	public void clearMsgAdmQ() throws Exception{
		try{
			if (admQRCV == null)
				initAdmQRCV(); // AdminQ의 메시지 얻어올 Receive 객체 얻어옴
			
			while (admQRCV.receiveNoWait() != null) {
				//do nothing
			}
			commit();
			
		} catch (Exception e) {
			throw new AgentException("AdminQ를 초기화할 수 없습니다. RAdapter가 종료됩니다." , e);
		}
	}
	
	
	*//**
	 * Message 타입의 메시지가 TextMessage 타입으로 형변환이 가능한 경우 변환하여 반환한다.
	 * @return
	 * @throws Exception
	 *//*
	public TextMessage getTextMsg() throws Exception {
		try{
			if (state == "Retry"){
				initAdmQRCV();
			}
			Message msg = admQRCV.receive(1000);// GET_WAIT_TIME의 주기로
			
			if (msg != null) {
				if (msg instanceof TextMessage) {
					admTextMsg = (TextMessage) msg;
					qSes.commit();
				}
			}
		}catch (JMSException je){									
			if(je.getErrorCode() != null){
				if(je.getErrorCode().equals("JMSWMQ2002")||je.getErrorCode().equals("JMSCMQ0001")){
					try{										//get 하다가 connection broken 
						
						state = "Retry";
						release();
						while(!init()){
							//connection 연결되면 빠져나감
							Thread.sleep(1000);
						}
//						initAdmQRCV();
					}catch (Exception e){
						e.printStackTrace();
						throw new AgentException("connection을 재시도 하였으나 연결되지 않아 어댑터가 종료합니다.",e);
					}
				}
			}
		}

		return admTextMsg;
	}
	
	*/
	
	public Context getInitialContext() throws Exception {
		try {
			p.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
			p.put(Context.PROVIDER_URL, INITIAL_CONTEXT_URL);
		} catch (Exception e) {
			throw e;
		}
		return new InitialContext(p);
	}

	// Get Session
	public QueueSession getQSession() throws Exception {
		if (qcf == null)
			throw new AgentException("Queue Connection Factory is null");
		qCon = qcf.createQueueConnection();
		qCon.start();
		return (QueueSession) qCon
				.createSession(true, Session.AUTO_ACKNOWLEDGE);
	}

	// Get ConnectionFactory
	public QueueConnectionFactory getConnectionFactory() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		try {
			Object obj = ctx.lookup(QMANAGER);
			return (QueueConnectionFactory) obj;
		} catch (Exception e) {
			throw e;
		}

	}

	/**-------------------- init Sender & Receiver ------------------------------------*/
	// SourceQ의 메시지 얻어올 Receive 객체 얻어옴
/*	public void initSrcQRCV() throws Exception {
		srcQRCV = getSrcQReceiver();
	}

	// AdminQ의 메시지 얻어올 Receive 객체 얻어옴
	public void initAdmQRCV() throws Exception {
		admQRCV = getAdmQReceiver();
	}

	// AdminQ에 메시지를 넣을 Sender, TextMessage 객체 얻어옴
	public void initAdmQSND() throws Exception {
		admQSND = getAdmQSender();
		admTextMsg = qSes.createTextMessage();
	}

	// ErrorQ에 메시지를 넣을 Sender, TextMessage 객체 얻어옴
	public void initErrQSND() throws Exception {
		errQSND = getErrQSender();
		errTextMsg = qSes.createTextMessage();
	}*/

	// 송신프로세스 송신 Q
	public void initREQUSET_QUEUE_SENDER() throws Exception {
		REQUSET_QUEUE_SENDER = getREQUSET_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
	
	// 송신프로세스 수신 Q
	public void initRESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		RESPONSE_QUEUE_RECEIVER = getRESPONSE_QUEUE_RECEIVER(GUID);
	}
	
	// 체크에이전트 송신 Q
	public void initCHK_REQUEST_QUEUE_SENDER() throws Exception {
		CHK_REQUEST_QUEUE_SENDER = getCHK_REQUEST_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
		
	// 체크에이전트 수신 Q
	public void initCHK_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		CHK_RESPONSE_QUEUE_RECEIVER = getCHK_RESPONSE_QUEUE_RECEIVER(GUID);
	}
	
	// 유저체크 송신 Q
	public void initUSR_REQUSER_QUEUE_SENDER() throws Exception {
		USR_REQUSER_QUEUE_SENDER = getUSR_REQUSER_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
	
	// 유저체크 수신 Q
	public void initUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		USR_RESPONSE_QUEUE_RECEIVER = getUSR_RESPONSE_QUEUE_RECEIVER(GUID);
	}
		
	// 수신 프로세스 수신 Q
	public void initREPLY_QUEUE_RECEIVER(String fcltyCd) throws Exception {
		REPLY_QUEUE_RECEIVER = getREPLY_QUEUE_RECEIVER(fcltyCd);
	}


	/**-------------------- Get Sender & Receiver ------------------------------------*/

	// TargetQ에 메시지를 put하기 위한 Sender 객체를 얻어옴
	public QueueSender getREQUSET_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (REQUSET_QUEUE == null)
			return null;
		return qSes.createSender(REQUSET_QUEUE);
	}

	// SourceQ의 메시지를 get하기 위한 Receiver 객체를 얻어옴
	public QueueReceiver getRESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}
	
	// TargetQ에 메시지를 put하기 위한 Sender 객체를 얻어옴
	public QueueSender getCHK_REQUEST_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (CHK_REQUEST_QUEUE == null)
			return null;
		return qSes.createSender(CHK_REQUEST_QUEUE);
	}

	// SourceQ의 메시지를 get하기 위한 Receiver 객체를 얻어옴
	public QueueReceiver getCHK_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (CHK_RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(CHK_RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}
	
	// TargetQ에 메시지를 put하기 위한 Sender 객체를 얻어옴
	public QueueSender getUSR_REQUSER_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_REQUSER_QUEUE == null)
			return null;
		return qSes.createSender(USR_REQUSER_QUEUE);
	}

	// SourceQ의 메시지를 get하기 위한 Receiver 객체를 얻어옴
	public QueueReceiver getUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_RESPONSE_QUEUE == null)
			return null;
		
		return qSes.createReceiver(USR_RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}

	// AdminQ의 메시지를 get하기 위한 Receiver 객체를 얻어옴
	public QueueReceiver getREPLY_QUEUE_RECEIVER(String fcltyCd) throws Exception {
		String selector = "fcltyCd = '" + fcltyCd + "'";
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (REPLY_QUEUE == null)
			return null;
		return qSes.createReceiver(REPLY_QUEUE, selector);
	}

	/**-------------------- Get Queue ------------------------------------*/
	public Queue getREQUSET_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (REQUSET_Q_NAME == null || REQUSET_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(REQUSET_Q_NAME);
	}

	public Queue getRESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (RESPONSE_Q_NAME == null || RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(RESPONSE_Q_NAME);
	}
	
	public Queue getCHK_REQUEST_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (CHK_REQUEST_Q_NAME == null || CHK_REQUEST_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(CHK_REQUEST_Q_NAME);
	}

	public Queue getCHK_RESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (CHK_RESPONSE_Q_NAME == null || CHK_RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(CHK_RESPONSE_Q_NAME);
	}

	public Queue getREPLY_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (REPLY_Q_NAME == null || REPLY_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(REPLY_Q_NAME);
	}

	/*-------------------- Get Message -----------------------------------------*/
	 
	
	public Message getMessage(String xmldata) throws AgentException{
		try {
			return qSes.createTextMessage(xmldata);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
		}
	}
	
	public static void main(String[] args){
		try{
//		JmsMQLib j = new JmsMQLib();
//		j.init();
		String xmlData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:p=\"http://www.kofiu.go.kr/kofics\"><soapenv:Header/><soapenv:Body>	<p:KOFICS>		<Header>			<Transaction>			    <UuId/>			    <IfId/>			</Transaction>			<Document>			    <ReqFcltNm></ReqFcltNm>				<ReqFcltCd></ReqFcltCd>				<KoficsUserId></KoficsUserId>			    <ReqDate></ReqDate>			    <AttachmentCnt></AttachmentCnt>			</Document>		</Header>		<Data>	    	<Master>				<UuId>testuser1</UuId>				<OrgCode>9400</OrgCode>			    <UserYn></UserYn>				<ResultCode></ResultCode>				<ErrDetail></ErrDetail>	  			<Attachment>			    	<SvFileNm></SvFileNm>				</Attachment>		</Master>		</Data>	</p:KOFICS></soapenv:Body></soapenv:Envelope>";
//		Message msg = j.qSes.createTextMessage(xmlData);
//		System.out.println(j.putMsg(msg,"xmlData"));
		/*while(true){
		System.out.println(j.getMsg());
		}*/
		}catch(Exception e){
			//e.printStackTrace();
		}
	}

}

