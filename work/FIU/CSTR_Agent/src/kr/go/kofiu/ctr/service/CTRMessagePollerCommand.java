/**
 * 
 */
package kr.go.kofiu.ctr.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import weblogic.jws.proxies.CGWebService;
import weblogic.jws.proxies.CGWebServiceSoap;
import weblogic.jws.proxies.CGWebService_Impl;
import kr.co.kofiu.www.ctr.encodedTypes.DocumentObject;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.JmsMQLib;
import kr.go.kofiu.ctr.util.FIURemoteException;
import kr.go.kofiu.ctr.util.RetryCommand;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.SoapToDocument;
/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ����� �����Ͽ� ���� ������ �����´�. 
 * ��   ��   ��  : ����ȣ 
 * �� �� �� ��  : ���������� pollingRespDocument�� ���� �����ϴ� ����
 * 				 -> ESB JMSť�κ��� ���� ������ �����ϴ� �������� ����
 * 				����, JMS ť�κ��� ���� ���� ������ ���� SoapToDocument Ŭ���� �߰�
 * ��   ��   ��  : 2005. 8. 25
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CTRMessagePollerCommand extends RetryCommand {
	private static final Logger logger = Logger.getLogger(CTRMessagePollerCommand.class);
	/**
	 * ������
	 * @param numOfRetries ��õ� Ƚ�� 
	 * @param timeToWait ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 * @throws IOException 
	 */
	public CTRMessagePollerCommand( int numOfRetries, long timeToWait) throws IOException {
		super(numOfRetries, timeToWait);
	}

	/**
	 * ���� ����� �����񽺸� ȣ���Ͽ� ���� ������ ���´�.
	 * ������ �߻��ϸ� numOfRetries Ƚ�� ��ŭ ��õ��Ѵ�.
	 * 
	 * 2014. 9. 10 �߰�
	 * ESB JMS ť -> Agent ���� ���� ���� ����
	 */
	protected Object action() throws Exception {	
		DocumentObject result = null;
		
		/**
		 * 2014. 9. 12 �߰� ����
		 * JMS/SOAP�� ���� ESB�� ���� ���� ������ ����(Soap ����)
		 * ebXML���İ� �����ֱ� ���� Soap -> DocumentObject ��ȯ ����
		 */
		try{
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();
			ArrayList<String> pollingResult = jmssoapService.pollingRespDocument();
			if(pollingResult == null || pollingResult.size() == 0 ){
				//JMSť���� �ƹ� ���� �������� ���ϴ� ��� 1�е��� ����Ͽ� ��õ� �ϴ� ���� �����ϱ� ����
				return null;
			}
			
			ArrayList<DocumentObject> resultList = new ArrayList<DocumentObject>();
			for(int i = 0 ; i < pollingResult.size() ; i++){
				result = SoapToDocument.conventer(pollingResult.get(i));
				resultList.add(result);
			}
			return resultList;
		} catch (RetryException e) {
			// AgentWaiter WebService lookup, method ȣ�� ���� 
			// 10 �а� ���
			int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
			logger.error("���� �߰� �����  ���� ���� ���� ����(MessagePolling) ������ ������ �ֽ��ϴ�. �������� "+sleepTime+"�ʰ� �����մϴ�.");
			CTRAgent.getInstance().sleepAgent(sleepTime);
			//e.addAction(new FatalActionSet("���� ���� ���� ����" , e));
			//throw e;
			return null;
		} 
		
	}
	
}
