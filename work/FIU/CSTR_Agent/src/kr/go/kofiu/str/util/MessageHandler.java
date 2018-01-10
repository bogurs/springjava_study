package kr.go.kofiu.str.util;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;




import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.UpdateResponse;
import kr.go.kofiu.str.conf.JmsMQLib;
import kr.go.kofiu.str.conf.ReportOrgInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.summary.UtilSummary;


public class MessageHandler {
	static Logger logger = Logger.getLogger(MessageHandler.class);
	private static MessageHandler instance = null;
	//JmsMQLib jmsLib;
	
	public static MessageHandler getInstance() throws RetryException  {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}
	
	public MessageHandler() throws RetryException{
				
	}
	
	/**
	 * UserChecker 사용자 검증
	 * 
	 * @param xmlData XML전문
	 * @return 결과값
	 * @throws AgentException
	 * @throws RetryException 
	 */
	public synchronized UtilSummary sendUserCheck(String xmlData , String GUID) throws RetryException, AgentException{
		
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			// 데이터 생성부
			
			jmsLib.init();
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
			TextMessage receiveMsg = (TextMessage) jmsLib.putUtilMsg(sendMsg, xmlData,GUID);
					
			UtilSummary us = WriteRcvFile.parseUtilSummary(receiveMsg.getText(), Controller.USERCHECK);
			
			return us;
			

		} catch (AgentException e) {
			throw new RetryException(e);
		}catch (Exception e) {
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}
	
	/**
	 * STR 송신 프로세스
	 * 
	 * @param xmlData STR 송신 ebXML전문
	 * @return 결과값
	 * @throws AgentException
	 * @throws RetryException 
	 */
	public synchronized String sendProcess(String xmlData, String GUID) throws RetryException, AgentException{
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			jmsLib.init();
			// 데이터 생성부
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
			TextMessage receiveMsg = (TextMessage) jmsLib.putMsg(sendMsg, xmlData,GUID);
			String receiveXML = receiveMsg.getText();
			
			return receiveXML;

		} catch (AgentException e) {
			throw new RetryException(e);
		}catch (Exception e) {
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}
	
	/**
	 * HeartBeat 호출
	 * 
	 * @param xmlData XML전문
	 * @return 결과값
	 * @throws AgentException
	 * @throws RetryException 
	 */
	public synchronized UtilSummary sendHeartBeat(String xmlData, String GUID) throws RetryException, AgentException{
		//String receiveData = null;
		UtilSummary us = null;
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			// 데이터 생성부
			jmsLib.init();
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
			TextMessage receiveMsg = (TextMessage) jmsLib.putUtilMsg(sendMsg, xmlData,GUID);
			us = WriteRcvFile.parseUtilSummary(receiveMsg.getText(), Controller.HEARTBEAT);
			
			return us;

		} catch (AgentException e) {
			throw new RetryException(e);
		}catch (Exception e) {
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}
	
	/**
	 * ChechAgent 검증
	 * 
	 * @param xmlData XML전문
	 * @return 결과값
	 * @throws AgentException
	 * @throws RetryException 
	 */
	public synchronized UtilSummary sendChechAgent(String xmlData, String GUID) throws RetryException, AgentException{
		//String receiveData = null;
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			// 데이터 생성부
			jmsLib.init();
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
			TextMessage receiveMsg = (TextMessage) jmsLib.putUtilMsg(sendMsg, xmlData,GUID);
			UtilSummary us = WriteRcvFile.parseUtilSummary(receiveMsg.getText(),Controller.CHECKAGENT);
			logger.info("STR CHECKAGENT 서비스 처리 (코드 : "+ us.getErrorCd() + " | 결과 : " + us.getErrorMsg()+")");
			String csvText = CSVUtil.genUtilCSV(us.getErrorCd(), us.getErrorMsg());
			CSVLogger csv = new CSVLogger();
			csv.writeCsv(csvText.getBytes(), Controller.CHECKAGENT , STRConfigure.getInstance().getAgentInfo().getId(), true, null);
									
			return us;

		} catch (Exception e) {
			logger.info("STR CHECKAGENT 서비스 처리 (코드 : 99 | 결과 : AGENT 기동 실패 | ESB 관리자에게 문의하시기 바랍니다.)");
			String csvText = CSVUtil.genUtilCSV("99", "AGENT 기동 실패 | ESB 관리자에게 문의하시기 바랍니다.");
			CSVLogger csv = new CSVLogger();
			csv.writeCsv(csvText.getBytes(), Controller.CHECKAGENT , STRConfigure.getInstance().getAgentInfo().getId(), true, null);
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}
	
	/**
	 * JMS 업데이트 로직을 호출한다.
	 * 
	 * @param xmlData 전송 XML
	 * @param GUID    JMS CorrelationID
	 * @return UpdateResponse 
	 * @throws RetryException
	 * @throws AgentException
	 */
	
	public synchronized UpdateResponse sendModuleUpdate(String xmlData, String GUID) throws RetryException, AgentException{
		//String receiveData = null;
		JmsMQLib jmsLib = new JmsMQLib();
		try {
			jmsLib.init();
			// 데이터 생성부
			Message sendMsg = jmsLib.getMessage(xmlData);

			// JMS 호출
			TextMessage receiveMsg = (TextMessage) jmsLib.putUtilMsg(sendMsg, xmlData,GUID);
			String receiveXML = receiveMsg.getText();
			UtilSummary us = WriteRcvFile.parseUtilSummary(receiveMsg.getText(), Controller.STRMODULEUPDATE);
			logger.info("STR MODULEUPATE 서비스 처리 (코드 : "+ us.getErrorCd() + " | 결과 : " + us.getErrorMsg()+")");
			// 가져온 값을 UpdateResponse로 Parsing 해준다.
			UpdateResponse updateResponse = getModuleUpdateInfo(receiveXML);
			
			return updateResponse;

		} catch (Exception e) {
			logger.info("STR MODULEUPATE 서비스 처리  (코드 : 99 | 결과 : AGENT 기동 실패 | ESB 관리자에게 문의하시기 바랍니다.)");
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}

	
	/**
	 * 도착, 가접수, 접수 수신 프로세스
	 * 
	 * @return 도착,접수,가접수 증서 ebXML
	 * @throws AgentException
	 * @throws RetryException
	 */
	public synchronized ArrayList<String> pollingProcess() throws AgentException, RetryException{
		//String receiveData = null;
		JmsMQLib jmsLib = new JmsMQLib();
		try{
			jmsLib.init();
			// 데이터 생성부
			//Message sendMsg = jmsLib.getMessage(xmlData);
			Iterator<ReportOrgInfo> orgList = STRConfigure.getInstance().getAgentInfo().getReportOrgInfos().iterator();
			
			HashSet<String> orgSet = new HashSet<String>();			
			
			while(orgList.hasNext()){
				ReportOrgInfo tempOrg = orgList.next();
				orgSet.add(tempOrg.getRepOrgCd());				
			}
			
			Iterator<String> orgIterator = orgSet.iterator();
			
			ArrayList<String> msgList = new ArrayList<String>();
			int count = 0;
			while(orgIterator.hasNext()){
				// JMS 호출
				TextMessage receiveMsg = null;
				String GUID = orgIterator.next();
				do{
					receiveMsg = (TextMessage) jmsLib.getMsg(GUID);
					if(receiveMsg!=null){
						String message = receiveMsg.getText();
						msgList.add(message);
						count = count + 1;
					}
				}while(receiveMsg!=null && count != 10);
			}
			return msgList;
			
		}
		catch(AgentException e){
			throw new RetryException(e);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
		}finally{
			jmsLib.release();
		}
	}
	
	public UpdateResponse getModuleUpdateInfo(String xmlDoc) throws ParserConfigurationException, SAXException, IOException {
		String RPT_MDL_VER;//
		String RPT_MDL_BLOB_MDL_LIB;//
		String RPT_MDL_BLOB_MDL_LIB_NM;
		String RPT_MDL_BLOB_MDL_LIB_SIZE;
		String RPT_MDL_BLOB_CONFIG;//
		String RPT_MDL_BLOB_CONFIG_NM;
		String RPT_MDL_BLOB_CONFIG_SIZE;
		String RPT_MDL_BLOB_PBL_KEY;//
		String RPT_MDL_BLOB_PBL_KEY_NM;
		String RPT_MDL_BLOB_PBL_KEY_SIZE;

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlDoc));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		
		parser = dbf.newDocumentBuilder();

		Document doc = parser.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList RPT_MDL_VERNode = rootElement.getElementsByTagName("RPT_MDL_VER");
		RPT_MDL_VER = RPT_MDL_VERNode.item(0).getTextContent();
		NodeList RPT_MDL_BLOB_MDL_LIBNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_MDL_LIB");
		RPT_MDL_BLOB_MDL_LIB = RPT_MDL_BLOB_MDL_LIBNode.item(0).getTextContent();
		//NodeList RPT_MDL_BLOB_CONFIGNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_CONFIG");
		//RPT_MDL_BLOB_CONFIG = RPT_MDL_BLOB_CONFIGNode.item(0).getTextContent();
		NodeList RPT_MDL_BLOB_PBL_KEYNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_PBL_KEY");
		RPT_MDL_BLOB_PBL_KEY = RPT_MDL_BLOB_PBL_KEYNode.item(0).getTextContent();

		UpdateResponse response = new UpdateResponse(0);
		response.setNewModuleVersion(RPT_MDL_VER);
		//15.03.19 수정
		//기존 외부포탈 기능 중 업데이트에 LIB 또는 KEY 파일은 필수 값이 아니므로 널체크를 진행해야 함.
		if(RPT_MDL_BLOB_MDL_LIB != null || RPT_MDL_BLOB_MDL_LIB != ""){
			response.setMdlLib(Base64.decodeBase64(RPT_MDL_BLOB_MDL_LIB));
		}
//		response.setMdlLib(Base64.decodeBase64(RPT_MDL_BLOB_MDL_LIB));
		//response.setConfig(Base64.decodeBase64(RPT_MDL_BLOB_CONFIG));
		if(RPT_MDL_BLOB_PBL_KEY != null || RPT_MDL_BLOB_PBL_KEY != ""){
			response.setPblKey(Base64.decodeBase64(RPT_MDL_BLOB_PBL_KEY));
		}
//		response.setPblKey(Base64.decodeBase64(RPT_MDL_BLOB_PBL_KEY));

		return response;
	}

}
