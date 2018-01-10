package kr.go.kofiu.ctr.service;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.EsbCheckServiceInfo;
import kr.go.kofiu.ctr.conf.JmsMQLib;
import kr.go.kofiu.ctr.conf.JmsSoapInfo;
import kr.go.kofiu.ctr.util.DocumentToSoap;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.UpdateResponse;
import kr.go.kofiu.ctr.util.XmlParser;

import org.apache.log4j.Logger;


public class JMSSOAPService {
	static Logger logger = Logger.getLogger(JMSSOAPService.class);
	private static JMSSOAPService instance = null;
	
	
	public static JMSSOAPService getInstance() throws Exception {	
		/*if (instance == null) {
			instance = new JMSSOAPService();
		}
		return instance;*/
		
		return new JMSSOAPService();
	}	

	public String processRequestDocumemt(String xmlData, String GlobalUniqueID) throws AgentException{
		String receiveData = null;
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			// ������ ������
			jmsLib.init();
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS ȣ��
			TextMessage receiveMsg = (TextMessage) jmsLib.putMsg(sendMsg, xmlData, GlobalUniqueID);
			receiveData = receiveMsg.getText();

			return receiveData;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
		
	}

//	public ArrayList<String> pollingRespDocument() throws RetryException,AgentException {
//		TextMessage receiveData = null;
//		ArrayList<String> receiveList = new ArrayList<String>();
//		
//		JmsMQLib jmsLib = new JmsMQLib();
//		int count = 0 ;
//		try {
//			// JMS ȣ��
//			jmsLib.init();
//			
//			do{
//				receiveData = (TextMessage) jmsLib.getMsg();
//				if(receiveData != null){
//					String receiveString = receiveData.getText();
//					receiveList.add(receiveString);
//					count = count+1;
//				}
//			}while(receiveData != null && count != 100);
//				return receiveList;
//			}catch(AgentException e){
//				throw new RetryException(e);
//			}catch (Exception e) {
//				// TODO Auto-generated catch block
//				throw new AgentException(e);
//			}finally{
//				jmsLib.release();
//			}
//	}
	
	public ArrayList<String> pollingRespDocument() throws RetryException,AgentException {
		TextMessage receiveData = null;
		ArrayList<String> receiveList ;//= new ArrayList<String>();
		
		JmsMQLib jmsLib = new JmsMQLib();
		int count = 0 ;
		try {
			// JMS ȣ��
			jmsLib.init();
			receiveList = jmsLib.getMsg();
			
			return receiveList;
			
			}catch(AgentException e){
				throw new RetryException(e);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				throw new AgentException(e);
			}finally{
				jmsLib.release();
			}
	}

	public Object EsbSoapService(String serviceMode, String GlobalUniqueID) throws AgentException , RetryException{
		// HEARTBEAT, MODULEUPDATE, VERSIONCHECK ���� �б�
		JmsMQLib jmsLib = new JmsMQLib();
		int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
		if(Controller.HEARTBEAT.equals(serviceMode)){
			try {
				jmsLib.init();
				//logger.info("ESB�� �ֱ��� ��ȣ �۽�");			
				
				String ping = DocumentToSoap.setEsbService_Request(serviceMode);
				// ������ ������
				Message sendMsg = jmsLib.getMessage(ping);
				
				// JMS ȣ��
				TextMessage heartBeatResult = (TextMessage) jmsLib.putCheckMsg(sendMsg, ping, GlobalUniqueID, serviceMode);
				return heartBeatResult.getText();
			}catch (AgentException e) {
				throw new RetryException(e);
			}catch (Exception e) {
				throw new AgentException(e);
			}finally{
				jmsLib.release();
			}
		}else if(Controller.CHECKAGENT.equals(serviceMode)){
			try {
				jmsLib.init();
				logger.info("ESB�� ���� Agent ��ȿ���� Check");
				
				String checkerXml = DocumentToSoap.setEsbService_Request(serviceMode);
				
				// ������ ������
				Message sendMsg = jmsLib.getMessage(checkerXml);
	
				// JMS ȣ��
			
				TextMessage checkAgentResult = (TextMessage) jmsLib.putCheckMsg(sendMsg, checkerXml, GlobalUniqueID, serviceMode);
				EsbCheckServiceInfo infoCtx = XmlParser.getCheckAgentInfo(checkAgentResult.getText());
				
				HashMap<String, String> checkAgentResultMap = new HashMap<String, String>();
				checkAgentResultMap.put("resultCode", infoCtx.getResultCode());
				checkAgentResultMap.put("resultMessage", infoCtx.getResultMessage());
				checkAgentResultMap.put("MemoryData", infoCtx.getMemoryData());
				checkAgentResultMap.put("TimeResult", infoCtx.getTimeResult());
				checkAgentResultMap.put("IPResult", infoCtx.getIPResult());
				checkAgentResultMap.put("ModuleVersionResult", infoCtx.getModuleVersionResult());
				checkAgentResultMap.put("TimeValue", infoCtx.getTimeValue());
				checkAgentResultMap.put("moduleVersion", infoCtx.getModuleVersion());
				logger.info("CTR CHECKAGENT ���� ó�� (�ڵ� : "+ infoCtx.getResultCode() + " | ��� : " + infoCtx.getResultMessage()+")");
				new ReportAction(Controller.CHECKAGENT, infoCtx.getResultCode(), infoCtx.getResultMessage()).doAct();
				return checkAgentResultMap;
			} catch (Exception e) {
				logger.info("CTR CHECKAGENT ���� ó�� (�ڵ� : 99 | ��� : ESB ���ſ� ������ �߻��Ͽ����ϴ�. Agent�� ������ �� �����ϴ�. �����ڿ��� �����ϼ���.)");
				new ReportAction(Controller.CHECKAGENT, "99", "ESB ���ſ� ������ �߻��Ͽ����ϴ�. Agent�� ������ �� �����ϴ�. �����ڿ��� �����ϼ���.").doAct();
				logger.info("ESB ���ſ� ������ �߻��Ͽ����ϴ�. Agent�� ������ �� �����ϴ�. �����ڿ��� �����ϼ���.");
				CTRAgent.getInstance().shutdown();
			} finally{
				jmsLib.release();
			}
			
		}else if(Controller.CTRMODULEUPDATE.equals(serviceMode)){
			try {
				jmsLib.init();
				logger.info("ESB�� ���� Agent Update ������ Ȯ��");
				
				String moduleUpdateRequest = DocumentToSoap.setEsbService_Request(serviceMode);
				// ������ ������
				Message sendMsg = jmsLib.getMessage(moduleUpdateRequest);
	
				// JMS ȣ��
				TextMessage moduleUpdateResult = (TextMessage) jmsLib.putCheckMsg(sendMsg, moduleUpdateRequest, GlobalUniqueID, serviceMode);
				
				EsbCheckServiceInfo response = XmlParser.getModuleUpdateInfo(moduleUpdateResult.getText());
				logger.info("CTR MODULEUPATE ���� ó��  (�ڵ� : "+response.getResultCode()+" | ��� : "+response.getResultMessage()+")");
				new ReportAction(Controller.CTRMODULEUPDATE, response.getResultCode(), response.getResultMessage()).doAct();
				return response;
			} catch (Exception e1) {
				logger.info("CTR MODULEUPATE ���� ó��  (�ڵ� : 99 | ��� : AGENT �⵿ ���� | ESB �����ڿ��� �����Ͻñ� �ٶ��ϴ�.)");
				new ReportAction(Controller.CTRMODULEUPDATE, "99", "AGENT �⵿ ���� | ESB �����ڿ��� �����Ͻñ� �ٶ��ϴ�.").doAct();
				logger.info("ESB ���ſ� ������ �߻��Ͽ����ϴ�. Agent�� ������ �� �����ϴ�. �����ڿ��� �����ϼ���.");
				throw new AgentException(e1);
			}finally{
				jmsLib.release();
			}
			
			
		}else{
			logger.error("�ùٸ� Check Agent ��ɾ �Է��ϼ���. Agent�� ��� �����մϴ�...\n\t�Է°��� ��ɾ� : [HEARTBEAT][MODULEUPDATE][CHECKAGENT]");
			CTRAgent.getInstance().shutdown();
		}
		return null;
		
	}

}
