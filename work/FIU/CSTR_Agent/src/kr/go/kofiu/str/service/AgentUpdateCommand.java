package kr.go.kofiu.str.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.JmsMQLib;
import kr.go.kofiu.ctr.util.RetryCommand;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.UpdateResponse;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.util.CSVUtil;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.util.GUIDUtil;
import kr.go.kofiu.str.util.MessageHandler;
import kr.go.kofiu.str.util.XmlParserData;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent 업데이트를 수행한다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateCommand extends RetryCommand {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateCommand.class);

	/**
	 * 생성자
	 * @param numOfRetries 재시도 횟수 
	 * @param timeToWait 실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	public AgentUpdateCommand( int numOfRetries, long timeToWait) {
		super(numOfRetries, timeToWait);
	}

	/**
	 * 집중 기관에 업데이트 요청을 수행한다.
	 * @throws AgentException 
	 */
	protected Object action() throws AgentException {
		UpdateResponse response = new UpdateResponse(UpdateResponse.UPDATE);
		try { 
			// http로 모듈 추가 다운로드 
			//JMSSOAPService jmssoapService = JMSSOAPService.getInstance();			
			
			logger.info("연계 모듈 라이브러리 파일 다운로드 시작");
			
			// GUID 값을 가져온다
			String GUID = GUIDUtil.getGUID_FIUF0001();

			// JMS를 호출하여 가져온 값을 UpdateResponse에 담는다.
	        response = (UpdateResponse) MessageHandler.getInstance().sendModuleUpdate(XmlParserData.getModuleUpdateXML(),GUID);
	        
			logger.info("연계 모듈 라이브러리 파일 웹으로 다운로드 완료. " + response);
			
		}catch (Exception e) {
			logger.info("STR 업데이트에 실패하였습니다. Agent를 종료합니다.");
			STRAgent.getInstance().shutdown();
			//throw new AgentException(e.getMessage(), e);
		}
		
		return response;
		
	}
	
	
	
}
