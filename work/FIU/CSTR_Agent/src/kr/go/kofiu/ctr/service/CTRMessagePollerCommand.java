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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 집중 기관에 접속하여 접수 증서를 가져온다. 
 * 작   성   자  : 최중호 
 * 수 정 이 력  : 접수증서를 pollingRespDocument를 통해 수신하던 형태
 * 				 -> ESB JMS큐로부터 접수 증서를 폴링하는 형식으로 변경
 * 				또한, JMS 큐로부터 접수 증서 수신을 위한 SoapToDocument 클래스 추가
 * 작   성   일  : 2005. 8. 25
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CTRMessagePollerCommand extends RetryCommand {
	private static final Logger logger = Logger.getLogger(CTRMessagePollerCommand.class);
	/**
	 * 생성자
	 * @param numOfRetries 재시도 횟수 
	 * @param timeToWait 실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 * @throws IOException 
	 */
	public CTRMessagePollerCommand( int numOfRetries, long timeToWait) throws IOException {
		super(numOfRetries, timeToWait);
	}

	/**
	 * 집중 기관의 웹서비스를 호출하여 접수 증서를 얻어온다.
	 * 오류가 발생하면 numOfRetries 횟수 만큼 재시도한다.
	 * 
	 * 2014. 9. 10 추가
	 * ESB JMS 큐 -> Agent 접수 증서 수신 과정
	 */
	protected Object action() throws Exception {	
		DocumentObject result = null;
		
		/**
		 * 2014. 9. 12 추가 내용
		 * JMS/SOAP을 통해 ESB로 부터 접수 증서를 수신(Soap 형식)
		 * ebXML형식과 맞춰주기 위해 Soap -> DocumentObject 변환 진행
		 */
		try{
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();
			ArrayList<String> pollingResult = jmssoapService.pollingRespDocument();
			if(pollingResult == null || pollingResult.size() == 0 ){
				//JMS큐에서 아무 값도 폴링하지 못하는 경우 1분동안 대기하여 재시도 하는 것을 방지하기 위함
				return null;
			}
			
			ArrayList<DocumentObject> resultList = new ArrayList<DocumentObject>();
			for(int i = 0 ; i < pollingResult.size() ; i++){
				result = SoapToDocument.conventer(pollingResult.get(i));
				resultList.add(result);
			}
			return resultList;
		} catch (RetryException e) {
			// AgentWaiter WebService lookup, method 호출 실패 
			// 10 분간 대기
			int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
			logger.error("현재 중계 기관의  보고 문서 접수 서비스(MessagePolling) 제공에 문제가 있습니다. 연계모듈을 "+sleepTime+"초간 중지합니다.");
			CTRAgent.getInstance().sleepAgent(sleepTime);
			//e.addAction(new FatalActionSet("접수 증서 수신 오류" , e));
			//throw e;
			return null;
		} 
		
	}
	
}
