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
			// 데이터 생성부
			jmsLib.init();
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
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
//			// JMS 호출
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
			// JMS 호출
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
		// HEARTBEAT, MODULEUPDATE, VERSIONCHECK 별로 분기
		JmsMQLib jmsLib = new JmsMQLib();
		int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
		if(Controller.HEARTBEAT.equals(serviceMode)){
			try {
				jmsLib.init();
				//logger.info("ESB에 주기적 신호 송신");			
				
				String ping = DocumentToSoap.setEsbService_Request(serviceMode);
				// 데이터 생성부
				Message sendMsg = jmsLib.getMessage(ping);
				
				// JMS 호출
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
				logger.info("ESB를 통해 Agent 유효성을 Check");
				
				String checkerXml = DocumentToSoap.setEsbService_Request(serviceMode);
				
				// 데이터 생성부
				Message sendMsg = jmsLib.getMessage(checkerXml);
	
				// JMS 호출
			
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
				logger.info("CTR CHECKAGENT 서비스 처리 (코드 : "+ infoCtx.getResultCode() + " | 결과 : " + infoCtx.getResultMessage()+")");
				new ReportAction(Controller.CHECKAGENT, infoCtx.getResultCode(), infoCtx.getResultMessage()).doAct();
				return checkAgentResultMap;
			} catch (Exception e) {
				logger.info("CTR CHECKAGENT 서비스 처리 (코드 : 99 | 결과 : ESB 수신에 문제가 발생하였습니다. Agent를 구동할 수 없습니다. 관리자에게 문의하세요.)");
				new ReportAction(Controller.CHECKAGENT, "99", "ESB 수신에 문제가 발생하였습니다. Agent를 구동할 수 없습니다. 관리자에게 문의하세요.").doAct();
				logger.info("ESB 수신에 문제가 발생하였습니다. Agent를 구동할 수 없습니다. 관리자에게 문의하세요.");
				CTRAgent.getInstance().shutdown();
			} finally{
				jmsLib.release();
			}
			
		}else if(Controller.CTRMODULEUPDATE.equals(serviceMode)){
			try {
				jmsLib.init();
				logger.info("ESB를 통해 Agent Update 정보를 확인");
				
				String moduleUpdateRequest = DocumentToSoap.setEsbService_Request(serviceMode);
				// 데이터 생성부
				Message sendMsg = jmsLib.getMessage(moduleUpdateRequest);
	
				// JMS 호출
				TextMessage moduleUpdateResult = (TextMessage) jmsLib.putCheckMsg(sendMsg, moduleUpdateRequest, GlobalUniqueID, serviceMode);
				
				EsbCheckServiceInfo response = XmlParser.getModuleUpdateInfo(moduleUpdateResult.getText());
				logger.info("CTR MODULEUPATE 서비스 처리  (코드 : "+response.getResultCode()+" | 결과 : "+response.getResultMessage()+")");
				new ReportAction(Controller.CTRMODULEUPDATE, response.getResultCode(), response.getResultMessage()).doAct();
				return response;
			} catch (Exception e1) {
				logger.info("CTR MODULEUPATE 서비스 처리  (코드 : 99 | 결과 : AGENT 기동 실패 | ESB 관리자에게 문의하시기 바랍니다.)");
				new ReportAction(Controller.CTRMODULEUPDATE, "99", "AGENT 기동 실패 | ESB 관리자에게 문의하시기 바랍니다.").doAct();
				logger.info("ESB 수신에 문제가 발생하였습니다. Agent를 구동할 수 없습니다. 관리자에게 문의하세요.");
				throw new AgentException(e1);
			}finally{
				jmsLib.release();
			}
			
			
		}else{
			logger.error("올바른 Check Agent 명령어를 입력하세요. Agent를 즉시 종료합니다...\n\t입력가능 명령어 : [HEARTBEAT][MODULEUPDATE][CHECKAGENT]");
			CTRAgent.getInstance().shutdown();
		}
		return null;
		
	}

}
