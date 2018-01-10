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
	
	public ObjectMessage trgObjMsg; // 10/11 �߰�
	
	public TextMessage admTextMsg;
	public TextMessage errTextMsg;
	public Message msg;
	
	public static String POLICY="";
	public float  GET_WAIT_TIME=1;

	/**-------------------- ���� ���� ------------------------------------*/
	static Logger logger = Logger.getLogger(JmsMQLib.class);
	
	private QueueConnectionFactory qcf;
	private QueueConnection qCon;
	private QueueSession qSes;
	private String state = "Start";
	
	// Receiver: SourceQ, AdminQ(�޽����� ����,get)
	// Sender: TargetQ, ErrorQ, AdminQ(�޽����� ����, put)
	private QueueSender REQUSET_QUEUE_SENDER; 
	private QueueReceiver RESPONSE_QUEUE_RECEIVER; 
	private QueueReceiver REPLY_QUEUE_RECEIVER; 
	private QueueSender USR_REQUSER_QUEUE_SENDER; 
	private QueueReceiver USR_RESPONSE_QUEUE_RECEIVER;
	private QueueSender CHK_REQUEST_QUEUE_SENDER; 
	private QueueReceiver CHK_RESPONSE_QUEUE_RECEIVER;
	
	/*private QueueReceiver srcQRCV; 			// SourceQ���� �޽����� get
	private QueueReceiver admQRCV; 			// AdminQ���� �޽����� get
	private QueueSender admQSND; 			// AdminQ�� �޽����� put
	private QueueSender trgQSND; 				// TargetQ�� �޽����� put
	private QueueSender errQSND; 				// ErrorQ�� �޽����� put
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
	 * Connection�� �ΰ�, ���� ���� ���θ� ��ȯ�Ѵ�.
	 * ��ȯ��: true - connection ����, false - connection ����
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
	 * ������ QueueSession�� ��ȯ�Ѵ�.
	 * @return qSes
	 */
	public QueueSession getQSes(){
		return qSes;
	}
	

	/**
	 * ��� �ڿ��� release �Ѵ�.
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
	 * ������ QueueSession�� rollback �Ѵ�.
	 * @throws Exception
	 */
	public void rollback() throws AgentException{
		try {
			if(qSes != null){
				qSes.rollback();
			} else {
				throw new AgentException("QueueSession�� null�̾ rollback �� �� �����ϴ�.");
			}
		} catch (JMSException e) {
			logger.info("[Fail] Rollback!");
			new AgentException(e);
		}
	}
	
	
	/**
	 * ������ QueueSession�� commit �Ѵ�.
	 * @throws Exception
	 */
	public void commit() throws Exception{
		try{
			if(qSes != null){
				qSes.commit();
			} else {
				throw new AgentException("QueueSession�� null�̾ commit �� �� �����ϴ�.");
			}
		} catch (JMSException e) {
			logger.info("[Fail] Commit!");
			logger.fatal(new AgentException(e));
			throw e;
		}
	}

	
	
	/**-------------------- Message Put �޼ҵ�------------------------------------*/
	
		/**
	 * Message ������ �޽����� �޾� TargetQ�� put�Ѵ�. String ������ msgdata�� �α� ���Ͽ� ������ ������ �α��� �� ���ȴ�.
	 * ���� �������� ���ῡ ������ �߻����� �ÿ� ������ �ɶ����� ��õ� �� �޽����� ��ó���Ѵ�.
	 * @param msg
	 * @param msgdata
		 * @throws AgentException 
	 * @throws Exception
	 */
	public Message putMsg(Message msg, String msgdata, String GUID) throws AgentException {
		try {
			
			if (REQUSET_QUEUE_SENDER == null){
				REQUSET_QUEUE_SENDER = getREQUSET_QUEUE_SENDER(); // TargetQ�� �޽��� ���� Sender ��ü ����
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
						throw new RetryException("�߼����� ���ſ� �����߽��ϴ�. Agent�� �����մϴ�. ESB �����ڿ��� �����Ͻʽÿ�.");
					}else{
						logger.info("[�߼����� ���� ��õ� �˸�] ���� ��õ� Ƚ�� : "+RETRY_COUNT +", "+RETRY_WAIT_TIME + "�� �Ŀ� ��õ� �մϴ�.");
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
			
			//�����ڵ� JMSCC0005�� ��쿡��
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
	 * Message ������ �޽����� �޾� TargetQ�� put�Ѵ�. String ������ msgdata�� �α� ���Ͽ� ������ ������ �α��� �� ���ȴ�.
	 * ���� �������� ���ῡ ������ �߻����� �ÿ� ������ �ɶ����� ��õ� �� �޽����� ��ó���Ѵ�.
	 * @param msg
	 * @param msgdata
	 * @throws Exception
	 */
	public Message putCheckMsg(Message msg, String msgdata, String GUID, String serviceMode) throws AgentException {
		try {
			if (CHK_REQUEST_QUEUE_SENDER == null){
				CHK_REQUEST_QUEUE_SENDER = getCHK_REQUEST_QUEUE_SENDER(); // TargetQ�� �޽��� ���� Sender ��ü ����
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
						String errorMsg = serviceMode + " ��û ���ſ� �����߽��ϴ�. Agent�� �Ͻ� �����մϴ�. ESB �����ڿ��� �����Ͻʽÿ�.";		
						new ReportAction(serviceMode, errorMsg).doAct();
						throw new AgentException(errorMsg);
					}else{
						logger.info("["+ serviceMode + " ��û ���� ���� ��õ� �˸�] ���� ��õ� Ƚ�� : "+RETRY_COUNT +", "+RETRY_WAIT_TIME + "�� �Ŀ� ��õ� �մϴ�.");
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
			
			//�����ڵ� JMSCC0005�� ��쿡��
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
	
	

	/**-------------------- Message Get �޼ҵ�------------------------------------*/

	
	/**
	 * SourceQ���� Message Ÿ���� �޽����� ���´�.(get)
	 * @return msg
	 * @throws Exception
	 */
	public synchronized ArrayList<String> getMsg() throws Exception {
		TextMessage msg = null;
		String fcltyCd = Configure.getInstance().getAgentInfo().getId();
		ArrayList<String> receiveList = new ArrayList<String>();
					
		initREPLY_QUEUE_RECEIVER(fcltyCd); // SourceQ�� �޽��� ���� Receive ��ü ����
		do {
			try {
				msg = null;
				msg = (TextMessage) REPLY_QUEUE_RECEIVER.receive((long) (GET_WAIT_TIME * 1000));
				
				if (msg != null) {
					String jMSCorrelationID = msg.getJMSCorrelationID();
					String getAgentId = jMSCorrelationID.substring(0, 4);
					if (getAgentId.equals(fcltyCd)) { // ����ڵ� �˻� ( �����ص��ɵ� )
						receiveList.add(msg.getText()); // ����Ʈ �߰�.
					}

				}
			} catch (JMSException je) {
				if (je.getErrorCode() != null) {
					if (je.getErrorCode().equals("JMSWMQ2002")
							|| je.getErrorCode().equals("JMSCMQ0001")) {
						try { // get �ϴٰ� connection broken

							// connection ��õ�
							state = "Retry";
							release();
							while (!init()) {
								Thread.sleep(1000);
							}
							// initSrcQRCV();
						} catch (Exception e) {
							e.printStackTrace();
							throw new AgentException("connection�� ��õ� �Ͽ����� ������� �ʾ� ����Ͱ� �����մϴ�.",	e);
						}
					}
				}
			}
		} while (msg != null && receiveList.size() != 500); 		//����Ƚ����ŭ �ݺ�����	
			REPLY_QUEUE_RECEIVER.close();
			commit();	// ������ Ƚ����ŭ ���� ���� Ŀ��
		
		return receiveList;
	}
	
//	/**
//	 * SourceQ���� Message Ÿ���� �޽����� ���´�.(get)
//	 * @return msg
//	 * @throws Exception
//	 */
//	public Message getMsg() throws Exception {
//		Message msg = null;
//		String fcltyCd = Configure.getInstance().getAgentInfo().getId();
//		try{			
//			initREPLY_QUEUE_RECEIVER(fcltyCd); // SourceQ�� �޽��� ���� Receive ��ü ����
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
//					try{										//get �ϴٰ� connection broken 
//						
//						//connection ��õ�
//						state = "Retry";
//						release();
//						while(!init()){
//							Thread.sleep(1000);
//						}
////						initSrcQRCV();
//					}catch (Exception e){
//						e.printStackTrace();
//						throw new AgentException("connection�� ��õ� �Ͽ����� ������� �ʾ� ����Ͱ� �����մϴ�." ,e );
//					}
//				}
//			}
//		}
//		return msg;
//	}
		

	/**
	 * AdminQ�� �޽����� �ִ��� Ȯ���ϰ�, �޽����� ������ ��� �� ���� Y���� üũ�Ѵ�.
	 * @return AdminQ�� �޽��� ����� true, �ƴϸ� false
	 * @throws Exception
	 *//*
	public boolean getMsgAdmQ() throws Exception {
		boolean isStop = false;
		if (admQRCV == null)
			initAdmQRCV(); // AdminQ�� �޽��� ���� Receive ��ü ����

		if (getTextMsg() != null) { // ť���� �޽��� ����, �޽����� ���� ���
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
	 * QBridge �⵿�� AdminQ�� �޽����� ��� �����Ѵ�.
	 * @throws Exception
	 *//*
	public void clearMsgAdmQ() throws Exception{
		try{
			if (admQRCV == null)
				initAdmQRCV(); // AdminQ�� �޽��� ���� Receive ��ü ����
			
			while (admQRCV.receiveNoWait() != null) {
				//do nothing
			}
			commit();
			
		} catch (Exception e) {
			throw new AgentException("AdminQ�� �ʱ�ȭ�� �� �����ϴ�. RAdapter�� ����˴ϴ�." , e);
		}
	}
	
	
	*//**
	 * Message Ÿ���� �޽����� TextMessage Ÿ������ ����ȯ�� ������ ��� ��ȯ�Ͽ� ��ȯ�Ѵ�.
	 * @return
	 * @throws Exception
	 *//*
	public TextMessage getTextMsg() throws Exception {
		try{
			if (state == "Retry"){
				initAdmQRCV();
			}
			Message msg = admQRCV.receive(1000);// GET_WAIT_TIME�� �ֱ��
			
			if (msg != null) {
				if (msg instanceof TextMessage) {
					admTextMsg = (TextMessage) msg;
					qSes.commit();
				}
			}
		}catch (JMSException je){									
			if(je.getErrorCode() != null){
				if(je.getErrorCode().equals("JMSWMQ2002")||je.getErrorCode().equals("JMSCMQ0001")){
					try{										//get �ϴٰ� connection broken 
						
						state = "Retry";
						release();
						while(!init()){
							//connection ����Ǹ� ��������
							Thread.sleep(1000);
						}
//						initAdmQRCV();
					}catch (Exception e){
						e.printStackTrace();
						throw new AgentException("connection�� ��õ� �Ͽ����� ������� �ʾ� ����Ͱ� �����մϴ�.",e);
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
	// SourceQ�� �޽��� ���� Receive ��ü ����
/*	public void initSrcQRCV() throws Exception {
		srcQRCV = getSrcQReceiver();
	}

	// AdminQ�� �޽��� ���� Receive ��ü ����
	public void initAdmQRCV() throws Exception {
		admQRCV = getAdmQReceiver();
	}

	// AdminQ�� �޽����� ���� Sender, TextMessage ��ü ����
	public void initAdmQSND() throws Exception {
		admQSND = getAdmQSender();
		admTextMsg = qSes.createTextMessage();
	}

	// ErrorQ�� �޽����� ���� Sender, TextMessage ��ü ����
	public void initErrQSND() throws Exception {
		errQSND = getErrQSender();
		errTextMsg = qSes.createTextMessage();
	}*/

	// �۽����μ��� �۽� Q
	public void initREQUSET_QUEUE_SENDER() throws Exception {
		REQUSET_QUEUE_SENDER = getREQUSET_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
	
	// �۽����μ��� ���� Q
	public void initRESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		RESPONSE_QUEUE_RECEIVER = getRESPONSE_QUEUE_RECEIVER(GUID);
	}
	
	// üũ������Ʈ �۽� Q
	public void initCHK_REQUEST_QUEUE_SENDER() throws Exception {
		CHK_REQUEST_QUEUE_SENDER = getCHK_REQUEST_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
		
	// üũ������Ʈ ���� Q
	public void initCHK_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		CHK_RESPONSE_QUEUE_RECEIVER = getCHK_RESPONSE_QUEUE_RECEIVER(GUID);
	}
	
	// ����üũ �۽� Q
	public void initUSR_REQUSER_QUEUE_SENDER() throws Exception {
		USR_REQUSER_QUEUE_SENDER = getUSR_REQUSER_QUEUE_SENDER();
		trgTextMsg = qSes.createTextMessage();
	}
	
	// ����üũ ���� Q
	public void initUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		USR_RESPONSE_QUEUE_RECEIVER = getUSR_RESPONSE_QUEUE_RECEIVER(GUID);
	}
		
	// ���� ���μ��� ���� Q
	public void initREPLY_QUEUE_RECEIVER(String fcltyCd) throws Exception {
		REPLY_QUEUE_RECEIVER = getREPLY_QUEUE_RECEIVER(fcltyCd);
	}


	/**-------------------- Get Sender & Receiver ------------------------------------*/

	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
	public QueueSender getREQUSET_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (REQUSET_QUEUE == null)
			return null;
		return qSes.createSender(REQUSET_QUEUE);
	}

	// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getRESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}
	
	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
	public QueueSender getCHK_REQUEST_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (CHK_REQUEST_QUEUE == null)
			return null;
		return qSes.createSender(CHK_REQUEST_QUEUE);
	}

	// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getCHK_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (CHK_RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(CHK_RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}
	
	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
	public QueueSender getUSR_REQUSER_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_REQUSER_QUEUE == null)
			return null;
		return qSes.createSender(USR_REQUSER_QUEUE);
	}

	// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_RESPONSE_QUEUE == null)
			return null;
		
		return qSes.createReceiver(USR_RESPONSE_QUEUE,"JMSCorrelationID='" + GUID + "'");
	}

	// AdminQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
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

