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
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Agent ������Ʈ�� �����Ѵ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateCommand extends RetryCommand {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateCommand.class);

	/**
	 * ������
	 * @param numOfRetries ��õ� Ƚ�� 
	 * @param timeToWait ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	public AgentUpdateCommand( int numOfRetries, long timeToWait) {
		super(numOfRetries, timeToWait);
	}

	/**
	 * ���� ����� ������Ʈ ��û�� �����Ѵ�.
	 * @throws AgentException 
	 */
	protected Object action() throws AgentException {
		UpdateResponse response = new UpdateResponse(UpdateResponse.UPDATE);
		try { 
			// http�� ��� �߰� �ٿ�ε� 
			//JMSSOAPService jmssoapService = JMSSOAPService.getInstance();			
			
			logger.info("���� ��� ���̺귯�� ���� �ٿ�ε� ����");
			
			// GUID ���� �����´�
			String GUID = GUIDUtil.getGUID_FIUF0001();

			// JMS�� ȣ���Ͽ� ������ ���� UpdateResponse�� ��´�.
	        response = (UpdateResponse) MessageHandler.getInstance().sendModuleUpdate(XmlParserData.getModuleUpdateXML(),GUID);
	        
			logger.info("���� ��� ���̺귯�� ���� ������ �ٿ�ε� �Ϸ�. " + response);
			
		}catch (Exception e) {
			logger.info("STR ������Ʈ�� �����Ͽ����ϴ�. Agent�� �����մϴ�.");
			STRAgent.getInstance().shutdown();
			//throw new AgentException(e.getMessage(), e);
		}
		
		return response;
		
	}
	
	
	
}
