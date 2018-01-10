package kr.go.kofiu.str.conf;


import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.util.RetryException;

import com.ibm.mq.MQException;

import org.apache.log4j.Logger;

public class JmsMQLib extends MQLib {

	/**-------------------- ���� ���� ------------------------------------*/
	static Logger logger = Logger.getLogger(JmsMQLib.class);
	
	private QueueConnectionFactory qcf;
	private QueueConnection qCon;
	private QueueSession qSes;
	private String state = "Start";
	
	// Receiver: SourceQ, AdminQ(�޽����� ����,get)
	// Sender: TargetQ, ErrorQ, AdminQ(�޽����� ����, put)
	private QueueSender SND_REQUEST_QUEUE_SENDER; 
	private QueueReceiver SND_RESPONSE_QUEUE_RECEIVER; 
	private QueueReceiver RCV_RESPONSE_QUEUE_RECEIVER; 
	private QueueSender USR_REQUEST_QUEUE_SENDER; 
	private QueueReceiver USR_RESPONSE_QUEUE_RECEIVER;
	private QueueSender HEART_REQUEST_QUEUE_SENDER; 
	private QueueReceiver HEART_RESPONSE_QUEUE_RECEIVER;
	
	private Queue SND_REQUEST_QUEUE;
	private Queue SND_RESPONSE_QUEUE;
	private Queue RCV_RESPONSE_QUEUE;
	private Queue USR_REQUEST_QUEUE;
	private Queue USR_RESPONSE_QUEUE;
	private Queue HEART_REQUEST_QUEUE;
	private Queue HEART_RESPONSE_QUEUE;
	
	
	/**
	 * Connection�� �ΰ�, ���� ���� ���θ� ��ȯ�Ѵ�.
	 * ��ȯ��: true - connection ����, false - connection ����
	 */
	public boolean init() throws AgentException {
		
		boolean sucCon = false;
		try {
			logger.debug("\t[JMS Connection " + state + "---------------------]");

			logger.debug("\t 0) setQueueName");
			initQueue();
			
			logger.debug("\t 1) getInitialContext");
			ctx = getInitialContext();

			logger.debug("\t 2) getConnectionFactory");
			qcf = getConnectionFactory();// CF lookup
			
			logger.debug("\t 3) getQueue");
			SND_REQUEST_QUEUE = getSND_REQUEST_Queue();
			SND_RESPONSE_QUEUE = getSND_RESPONSE_Queue();
			RCV_RESPONSE_QUEUE = getRCV_RESPONSE_Queue();
			USR_REQUEST_QUEUE = getUSR_REQUEST_Queue();
			USR_RESPONSE_QUEUE = getUSR_RESPONSE_Queue();
			HEART_REQUEST_QUEUE = getHEART_REQUEST_Queue();
			HEART_RESPONSE_QUEUE = getHEART_RESPONSE_Queue();
			logger.debug("\t 4) getQueueSession");
			qSes = getQSession();			 // Create session
			logger.debug("\t[JMS Connection Success!-----------------]\n");
			sucCon = true;
		}
		/*} catch(JMSException e){
			String jmsErrorCd = e.getErrorCode();
			logger.error("JMS | JMS Error - " + jmsErrorCd);
		} catch(MQException e){
			String jmsErrorCd = e.getErrorCode();
			logger.error("JMS | JMS Error - " + jmsErrorCd);
		}*/catch (Exception e) {
			logger.debug("\t[JMS Connection Fail!----------------------]");   
			throw new AgentException(e.toString());
		}
		return sucCon;
	} 

	public void initQueue(){
		STRJMSInfo info = STRConfigure.getInstance().getAgentInfo().getStrJMSInfo();
		this.INITIAL_CONTEXT_FACTORY = info.getINITIAL_CONTEXT_FACTORY();
		this.CONNECTION_FACTORY = info.getCONNECTION_FACTORY();
		this.GET_WAIT_TIME = info.getGET_WAIT_TIME();
		this.RETRY_COUNT = info.getRETRY_COUNT();
		this.RETRY_WAIT_TIME = info.getRETRY_WAIT_TIME();
		this.PROVIDER_URL = info.getPROVIDER_URL();  
		this.SND_REQUEST_Q_NAME = info.getSND_REQUEST_Q_NAME();
		this.SND_RESPONSE_Q_NAME = info.getSND_RESPONSE_Q_NAME();
		this.RCV_RESPONSE_Q_NAME = info.getRCV_RESPONSE_Q_NAME();
		this.HEART_REQUEST_Q_NAME = info.getHEART_REQUEST_Q_NAME();
		this.HEART_RESPONSE_Q_NAME = info.getHEART_RESPONSE_Q_NAME();
		this.SERVICE_OPERATION = info.getSERVICE_OPERATION();
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
			if (SND_REQUEST_QUEUE_SENDER != null)
				SND_REQUEST_QUEUE_SENDER.close();
			if (SND_RESPONSE_QUEUE_RECEIVER != null)
				SND_RESPONSE_QUEUE_RECEIVER.close();
			if (RCV_RESPONSE_QUEUE_RECEIVER != null)
				RCV_RESPONSE_QUEUE_RECEIVER.close();
			if (USR_REQUEST_QUEUE_SENDER != null)
				USR_REQUEST_QUEUE_SENDER.close();
			if (USR_RESPONSE_QUEUE_RECEIVER != null)
				USR_RESPONSE_QUEUE_RECEIVER.close();
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
	 * @throws Exception
	 */
	public synchronized Message putMsg(Message msg, String msgdata, String GUID) throws AgentException {
		try {
			if (SND_REQUEST_QUEUE_SENDER == null){
				SND_REQUEST_QUEUE_SENDER = getSND_REQUEST_QUEUE_SENDER(); // TargetQ�� �޽��� ���� Sender ��ü ����
			}
	//		if (SND_RESPONSE_QUEUE_RECEIVER == null){
				SND_RESPONSE_QUEUE_RECEIVER = getSND_RESPONSE_QUEUE_RECEIVER(GUID);
	//		}
			//String getFcltyCdFromGUID = GUID.substring(8, 12);
			msg.setStringProperty("SOAPJMS_contentType", "SOAP11-MTOM");	
			msg.setStringProperty("SOAPJMS_bindingVersion", "1.0");
			msg.setJMSReplyTo(SND_RESPONSE_QUEUE);
			msg.setStringProperty("fcltyCd", STRConfigure.getInstance().getAgentInfo().getId());
			msg.setJMSCorrelationID(GUID);
			msg.setStringProperty("SOAPJMS_requestURI", "jms:jndi:"+getSND_REQUEST_Q_NAME()+
					"?jndiConnectionFactoryName="+ getCONNECTION_FACTORY() +
					"&amp;jndiInitialContextFactory="+getINITIAL_CONTEXT_FACTORY() +
					"&amp;jndiURL="+getPROVIDER_URL() +
					"&amp;replyToName="+ getSND_RESPONSE_Q_NAME() +
					"&amp;targetService="+getSERVICE_OPERATION());
			// ���ۺ�
			SND_REQUEST_QUEUE_SENDER.send(msg);
			//logger.info("JMS | SEND PROCESS SEND OK");
			commit();
			
			// ���ź�
			Message getMessage = null;
			int retrySeq=0;
			while(retrySeq != RETRY_COUNT){
				getMessage = SND_RESPONSE_QUEUE_RECEIVER.receive(GET_WAIT_TIME * 1000);
				if(getMessage==null){
					logger.info("JMS | �۽� ���μ��� ���ſ� �����Ͽ����ϴ�. "+ RETRY_WAIT_TIME +"���� ������ ��õ� �մϴ�. ( �õ� ȸ�� : "+ (retrySeq + 1) +" , �� ȸ�� : " + RETRY_COUNT + " )");
					retrySeq++ ;
					Thread.sleep(RETRY_WAIT_TIME * 1000);
				}else{
					break;
				}
			}
			if(getMessage==null){
				throw new AgentException("JMS | ESB ���� ���� | Agent�� �۵��� 10�а� �����մϴ�."); 
			}
			//logger.info("JMS | SEND PROCESS RECEIVE OK");
			commit();
			logger.debug("[Commit] Put Message: \n" + msgdata);
			
			SND_RESPONSE_QUEUE_RECEIVER.close();
			
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
						throw new AgentException(e);
					}
				}
			}
			rollback();
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			
			Throwable t = je.getLinkedException();
			if (t != null) {
				if (t instanceof JMSException) {
					JMSException je1 = (JMSException) t;
					throw new AgentException(je1.getErrorCode(), je1);
					
				} else if (t instanceof MQException) {
					MQException mqe = (MQException) t;
					throw new AgentException(mqe);
				}
			}
			
			throw new AgentException(je); 
			
		} catch (Exception e) {
			rollback();
			//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			throw new AgentException(e);
		}
	}

	/**
	 * Message ������ �޽����� �޾� TargetQ�� put�Ѵ�. String ������ msgdata�� �α� ���Ͽ� ������ ������ �α��� �� ���ȴ�.
	 * ���� �������� ���ῡ ������ �߻����� �ÿ� ������ �ɶ����� ��õ� �� �޽����� ��ó���Ѵ�.
	 * @param msg
	 * @param msgdata
	 * @throws RetryException 
	 * @throws Exception
	 */
	public synchronized Message putUserCheckMsg(Message msg, String msgdata, String GUID) throws AgentException {
		try {
			if (USR_REQUEST_QUEUE_SENDER== null){
				USR_REQUEST_QUEUE_SENDER = getUSR_REQUEST_QUEUE_SENDER(); // TargetQ�� �޽��� ���� Sender ��ü ����
			}
	//		if (USR_RESPONSE_QUEUE_RECEIVER == null){
				USR_RESPONSE_QUEUE_RECEIVER = getUSR_RESPONSE_QUEUE_RECEIVER(GUID);
	//		}
			
			msg.setStringProperty("SOAPJMS_contentType", "SOAP11-MTOM");	
			msg.setStringProperty("SOAPJMS_bindingVersion", "1.0");
			msg.setJMSReplyTo(USR_RESPONSE_QUEUE);
			msg.setJMSCorrelationID(GUID);
			msg.setStringProperty("SOAPJMS_requestURI", "jms:jndi:"+getUSR_REQUEST_Q_NAME()+
					"?jndiConnectionFactoryName="+ getCONNECTION_FACTORY() +
					"&amp;jndiInitialContextFactory="+getINITIAL_CONTEXT_FACTORY() +
					"&amp;jndiURL="+getPROVIDER_URL() +
					"&amp;replyToName="+ getUSR_RESPONSE_Q_NAME() +
					"&amp;targetService="+getSERVICE_OPERATION());
			USR_REQUEST_QUEUE_SENDER.send(msg);
			//logger.info("JMS | USERCHECK SEND OK");
			commit();
			
			Message getMessage = null;
			int retrySeq=0;
			while(retrySeq != RETRY_COUNT){
				getMessage = USR_RESPONSE_QUEUE_RECEIVER.receive(GET_WAIT_TIME * 1000);
				if(getMessage==null){
					logger.info("JMS | USERCHECK ���ſ� �����Ͽ����ϴ�. "+ RETRY_WAIT_TIME +"���� ������ ��õ� �մϴ�. ( �õ� ȸ�� : "+ (retrySeq + 1) +" , �� ȸ�� : " + RETRY_COUNT + " )");
					
					retrySeq++ ;
					Thread.sleep(RETRY_WAIT_TIME * 1000);
				}else{
					break;
				}
			}
			if(getMessage==null){
				
				throw new AgentException("JMS | ESB ���� ���� | Agent�� �۵��� 10�а� �����մϴ�."); 
				
			}
			//logger.info("JMS | USERCHECK RECEIVE OK");
			commit();
			logger.debug("[Commit] Put Message: \n" + msgdata);
			
			USR_RESPONSE_QUEUE_RECEIVER.close();
			
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
						throw new AgentException(e);
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
					throw new AgentException(je1.getErrorCode(), je1);
					
				} else if (t instanceof MQException) {
					MQException mqe = (MQException) t;
					logger.fatal(new AgentException(mqe));
					throw new AgentException(mqe);
				}
			}
			
			//logger.fatal(new AgentException(je));
			throw new AgentException(je); 
			
		} catch (Exception e) {
			rollback();
			//logger.info("[Rollback] Put Message in the " + TRG_QUEUE + "(TargetQ)!");
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			
			throw new AgentException(e);
		}
	}
	
	public synchronized Message putUtilMsg(Message msg, String msgdata, String GUID) throws AgentException {
		try {
			if (HEART_REQUEST_QUEUE_SENDER== null){
				HEART_REQUEST_QUEUE_SENDER = getHEART_REQUEST_QUEUE_SENDER(); // TargetQ�� �޽��� ���� Sender ��ü ����
			}
			//if (HEART_RESPONSE_QUEUE_RECEIVER == null){
				HEART_RESPONSE_QUEUE_RECEIVER = getHEART_RESPONSE_QUEUE_RECEIVER(GUID);
			//}
			
			msg.setStringProperty("SOAPJMS_contentType", "SOAP11-MTOM");	
			msg.setStringProperty("SOAPJMS_bindingVersion", "1.0");
			msg.setJMSReplyTo(HEART_RESPONSE_QUEUE);
			msg.setJMSCorrelationID(GUID);
			msg.setStringProperty("SOAPJMS_requestURI", "jms:jndi:"+getHEART_REQUEST_Q_NAME()+
					"?jndiConnectionFactoryName="+ getCONNECTION_FACTORY() +
					"&amp;jndiInitialContextFactory="+getINITIAL_CONTEXT_FACTORY() +
					"&amp;jndiURL="+getPROVIDER_URL() +
					"&amp;replyToName="+ getHEART_RESPONSE_Q_NAME() +
					"&amp;targetService="+getSERVICE_OPERATION());
			HEART_REQUEST_QUEUE_SENDER.send(msg);
			commit();
			//logger.info("JMS | STR UTIL SEND OK");
			
			Message getMessage = null;
			int retrySeq= 0;
			while(retrySeq != RETRY_COUNT){
				getMessage = HEART_RESPONSE_QUEUE_RECEIVER.receive(GET_WAIT_TIME * 1000);
				if(getMessage==null){
					logger.info("JMS | HEARTBEAT ���ſ� �����Ͽ����ϴ�. "+ RETRY_WAIT_TIME +"���� ������ ��õ� �մϴ�. ( �õ� ȸ�� : "+ (retrySeq + 1) +" , �� ȸ�� : " + RETRY_COUNT + " )");
					retrySeq++ ;
					Thread.sleep(RETRY_WAIT_TIME * 1000);
				}else{
					break;
				}
			}
			if(getMessage==null){
				throw new AgentException("AGENT �⵿ ���� | ESB �����ڿ��� �����Ͻñ� �ٶ��ϴ�."); 
			}
			//logger.info("JMS | STR UTIL RECEIVE OK");
			HEART_RESPONSE_QUEUE_RECEIVER.close();
			commit();
			
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
						throw new AgentException(e);
					}
				}
			}
			rollback();
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			
			Throwable t = je.getLinkedException();
			if (t != null) {
				if (t instanceof JMSException) {
					JMSException je1 = (JMSException) t;
					//logger.fatal(new AgentException(je1));
					throw new AgentException(je1.getErrorCode(), je1);
					
				} else if (t instanceof MQException) {
					MQException mqe = (MQException) t;
					//logger.fatal(new AgentException(mqe));
					throw new AgentException(mqe);
				}
			}	
			//logger.fatal(new AgentException(je));
			throw new AgentException(je); 
			
		} catch (Exception e) {
			rollback();
			logger.debug("[Rollback] Put Message: \n" + msgdata);
			//logger.fatal(new AgentException(e));
			throw new AgentException(e);
		}
	}
	
	
	

	/**-------------------- Message Get �޼ҵ�------------------------------------*/

	
	
	
	/**
	 * SourceQ���� Message Ÿ���� �޽����� ���´�.(get)
	 * @return msg
	 * @throws Exception
	 */
	public synchronized Message /*ArrayList<Message>*/ getMsg(String agentId) throws Exception {
		ArrayList<Message> msgArray = new ArrayList<Message>();
		Message msg = null ;
		
		try{
			initRCV_RESPONSE_QUEUE_RECEIVER(agentId); // SourceQ�� �޽��� ���� Receive ��ü ����
			msg = RCV_RESPONSE_QUEUE_RECEIVER.receive((long) (GET_WAIT_TIME * 1000));	//km
			RCV_RESPONSE_QUEUE_RECEIVER.close();
			commit();
		}catch (JMSException je){
			if(je.getErrorCode() != null){
				if(je.getErrorCode().equals("JMSWMQ2002")||je.getErrorCode().equals("JMSCMQ0001")){
					try{										//get �ϴٰ� connection broken 
						//connection ��õ�
						state = "Retry";
						release();
						while(!init()){
							Thread.sleep(1000);
						}
//						initSrcQRCV();
					}catch (Exception e){
						//e.printStackTrace();
						throw new AgentException("connection�� ��õ� �Ͽ����� ������� �ʾ� ����Ͱ� �����մϴ�." ,e );
					}
				}
			}
		}
		return msg;
		
		/*do
		{
			msg = null;
			try{
				initRCV_RESPONSE_QUEUE_RECEIVER(agentId); // SourceQ�� �޽��� ���� Receive ��ü ����
				msg = RCV_RESPONSE_QUEUE_RECEIVER.receive((long) (GET_WAIT_TIME * 1000));	//km
				RCV_RESPONSE_QUEUE_RECEIVER.close();
				commit();
			}catch (JMSException je){
				if(je.getErrorCode() != null){
					if(je.getErrorCode().equals("JMSWMQ2002")||je.getErrorCode().equals("JMSCMQ0001")){
						try{										//get �ϴٰ� connection broken 
							
							//connection ��õ�
							state = "Retry";
							release();
							while(!init()){
								Thread.sleep(1000);
							}
	//						initSrcQRCV();
						}catch (Exception e){
							e.printStackTrace();
							throw new AgentException("connection�� ��õ� �Ͽ����� ������� �ʾ� ����Ͱ� �����մϴ�." ,e );
						}
					}
				}
			}
		}
		while(!msg.equals(null));
		
		return msgArray;*/
		
	}
		

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
			p.put(Context.PROVIDER_URL, PROVIDER_URL);
		} catch (Exception e) {
			throw e;
		}
		return new InitialContext(p);
	}

	// Get Session
	public QueueSession getQSession() throws  AgentException, JMSException {
		QueueSession qSession = null ;
		if (qcf == null)
			throw new AgentException("Queue Connection Factory is null");
		qCon = qcf.createQueueConnection();
		qCon.start();
		qSession = (QueueSession) qCon.createSession(true, Session.AUTO_ACKNOWLEDGE);
		//while(qSession == null){}
		return qSession;
	}

	// Get ConnectionFactory
	public QueueConnectionFactory getConnectionFactory() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		try {
			Object obj = ctx.lookup(CONNECTION_FACTORY);
			return (QueueConnectionFactory) obj;
		} catch (Exception e) {
			throw e;
		}

	}

	/**-------------------- init Sender & Receiver ------------------------------------*/
	// �۽����μ��� �۽� Q
	public void initSND_REQUEST_QUEUE_SENDER() throws Exception {
		SND_REQUEST_QUEUE_SENDER = getSND_REQUEST_QUEUE_SENDER();
	}
	
	// �۽����μ��� ���� Q
	public void initSND_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		SND_RESPONSE_QUEUE_RECEIVER = getSND_RESPONSE_QUEUE_RECEIVER(GUID);
	}
	
	// ����üũ �۽� Q
	public void initUSR_REQUEST_QUEUE_SENDER() throws Exception {
		USR_REQUEST_QUEUE_SENDER = getUSR_REQUEST_QUEUE_SENDER();
	}
	
	// ����üũ ���� Q
	public void initUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		USR_RESPONSE_QUEUE_RECEIVER = getUSR_RESPONSE_QUEUE_RECEIVER(GUID);
	}
		
	// ���� ���μ��� ���� Q
	public void initRCV_RESPONSE_QUEUE_RECEIVER(String agentId) throws Exception {
		RCV_RESPONSE_QUEUE_RECEIVER = getRCV_RESPONSE_QUEUE_RECEIVER(agentId);
	}


	/**-------------------- Get Sender & Receiver ------------------------------------*/

	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
	public QueueSender getSND_REQUEST_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (SND_REQUEST_QUEUE == null)
			return null;
		return qSes.createSender(SND_REQUEST_QUEUE);
	}

	// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getSND_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (SND_RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(SND_RESPONSE_QUEUE,"JMSCorrelationID='"+GUID+"'");
	}
	
	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
	public QueueSender getUSR_REQUEST_QUEUE_SENDER() throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_REQUEST_QUEUE == null)
			return null;
		return qSes.createSender(USR_REQUEST_QUEUE);
	}

	// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getUSR_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (USR_RESPONSE_QUEUE == null)
			return null;
		
		return qSes.createReceiver(USR_RESPONSE_QUEUE,"JMSCorrelationID='"+GUID+"'");
	}

	// AdminQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
	public QueueReceiver getRCV_RESPONSE_QUEUE_RECEIVER(String agentId) throws Exception {
		if (qSes == null)
			throw new AgentException("Queue Session is null");
		if (RCV_RESPONSE_QUEUE == null)
			return null;
		return qSes.createReceiver(RCV_RESPONSE_QUEUE,"JMSCorrelationID='"+agentId+"'");
	}
	
	// TargetQ�� �޽����� put�ϱ� ���� Sender ��ü�� ����
		public QueueSender getHEART_REQUEST_QUEUE_SENDER() throws Exception {
			if (qSes == null)
				throw new AgentException("Queue Session is null");
			if (HEART_REQUEST_QUEUE == null)
				return null;
			return qSes.createSender(HEART_REQUEST_QUEUE);
		}

		// SourceQ�� �޽����� get�ϱ� ���� Receiver ��ü�� ����
		public QueueReceiver getHEART_RESPONSE_QUEUE_RECEIVER(String GUID) throws Exception {
			if (qSes == null)
				throw new AgentException("Queue Session is null");
			if (HEART_RESPONSE_QUEUE == null)
				return null;
			
			return qSes.createReceiver(HEART_RESPONSE_QUEUE,"JMSCorrelationID='"+GUID+"'");
		}

	/**-------------------- Get Queue ------------------------------------*/
	public Queue getSND_REQUEST_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (SND_REQUEST_Q_NAME == null || SND_REQUEST_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(SND_REQUEST_Q_NAME);
	}

	public Queue getSND_RESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (SND_RESPONSE_Q_NAME == null || SND_RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(SND_RESPONSE_Q_NAME);
	}

	public Queue getRCV_RESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (RCV_RESPONSE_Q_NAME == null || RCV_RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(RCV_RESPONSE_Q_NAME);
	}
	
	public Queue getUSR_REQUEST_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (USR_REQUEST_Q_NAME == null || USR_REQUEST_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(USR_REQUEST_Q_NAME);
	}

	public Queue getUSR_RESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (USR_RESPONSE_Q_NAME == null || USR_RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(USR_RESPONSE_Q_NAME);
	}
	
	public Queue getHEART_REQUEST_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (HEART_REQUEST_Q_NAME == null || HEART_REQUEST_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(HEART_REQUEST_Q_NAME);
	}

	public Queue getHEART_RESPONSE_Queue() throws Exception {
		if (ctx == null)
			throw new AgentException("Context is null");
		if (HEART_RESPONSE_Q_NAME == null || HEART_RESPONSE_Q_NAME.trim().equals(""))
			return null;
		return (Queue) ctx.lookup(HEART_RESPONSE_Q_NAME);
	}

	/*-------------------- Get Message -----------------------------------------*/
	 
	
	public Message getMessage(String xmldata) throws AgentException{
		try {
			while(qSes==null){
				Thread.sleep(1000);
			}
			return qSes.createTextMessage(xmldata);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
		} catch (Exception e){
			throw new AgentException(e);
		}
	}
	
	public static void main(String[] args){
		try{
		JmsMQLib j = new JmsMQLib();
		j.init();
		String xmlData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:p=\"http://www.kofiu.go.kr/kofics\"><soapenv:Header/><soapenv:Body>	<p:KOFICS>		<Header>			<Transaction>			    <UuId/>			    <IfId/>			</Transaction>			<Document>			    <ReqFcltNm></ReqFcltNm>				<ReqFcltCd></ReqFcltCd>				<KoficsUserId></KoficsUserId>			    <ReqDate></ReqDate>			    <AttachmentCnt></AttachmentCnt>			</Document>		</Header>		<Data>	    	<Master>				<UuId>testuser1</UuId>				<OrgCode>9400</OrgCode>			    <UserYn></UserYn>				<ResultCode></ResultCode>				<ErrDetail></ErrDetail>	  			<Attachment>			    	<SvFileNm></SvFileNm>				</Attachment>		</Master>		</Data>	</p:KOFICS></soapenv:Body></soapenv:Envelope>";
		Message msg = j.qSes.createTextMessage(xmlData);
		System.out.println(j.putMsg(msg,"xmlData","1"));
		/*while(true){
		System.out.println(j.getMsg());
		}*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

