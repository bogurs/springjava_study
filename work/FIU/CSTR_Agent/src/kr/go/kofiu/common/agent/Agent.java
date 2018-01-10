package kr.go.kofiu.common.agent;
 
import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.str.conf.STRConfigure;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.quartz.Scheduler;

import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
// song import com.gpki.sdk.bs.GpkiSDKException;
//import weblogic.jws.proxies.CGWebService;
//import weblogic.jws.proxies.CGWebServiceSoap;
//import weblogic.jws.proxies.CGWebService_Impl;


/*******************************************************
 * <pre>
 * 업무   그룹명 : CTR 시스템
 * 서브   업무명 : 보고 기관 Agent
 * 설        명 : Agent 실행을 위한 Main 클래스. 
 * 작   성   자 : 최중호 
 * 작   성   일 : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Agent {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Agent.class);

	/**
	 *  instance
	 */
	private static Agent instance;

	/**
	 *  controller
	 */
	private Controller controller = null;
	
	/**
	 * scheduler
	 */
	private Scheduler scheduler;

	/**
	 * CTR 보고 문서 전송 스레드 풀 
	 */
	private ThreadPoolExecutor executor;
	

	
	/**
	 * Agent Main 실행 메소드 
	 * @param args
	 */
	public static void main(String[] args) {
		// initailize Log4j
		
		if(args.length ==0){
			printError("서비스값이 설정되지 않았습니다.");
			System.exit(0);
		} else{
			String service = args[0]; // 요청 서비스값 가져오기 ( value : CTR / STR )
			if(service.equalsIgnoreCase("CTR")){ // 요청 서비스가 CTR일 경우
				DOMConfigurator.configure(new File("ctr_config/log4j.xml").getAbsolutePath());
				try {	
					// initailize Agent
					CTRAgent ctrAgent = new CTRAgent();
					
					if ( args.length == 2 ){
						
						if ("force".equals(args[1])) { // $java Agent CTR force
							logger.warn("강제 시작. 다른 CTR 연계 모듈 프로세스가 실행되고 있을 수 있습니다.");
							ctrAgent.controller.doControl(Controller.BOOT_SHUTDOWN, null, Controller.SERVICE_CTR);
						} else if ( "version".equals(args[1])) { // $java Agent CTR version
							logger.info("*----------------------------------------------------------*");
							logger.info("*   CTR Agent (Ver : " + ctrAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ")");
							logger.info("*----------------------------------------------------------*");
							System.exit(0);
						}
					}
					
					logger.info("*---------------------------------------------------------------------------*");
					logger.info("*   CTR 연계 모듈  (Ver : " + ctrAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") init  " + new java.util.Date());
					logger.info("*---------------------------------------------------------------------------*");
					
					
					logger.info("CTR Agent를 기동합니다.");
					ctrAgent.checkSystem();
					ctrAgent.startup();
				} catch ( Throwable t ) {
					printError(t);
					System.exit(0);
				}
			} else if(service.equalsIgnoreCase("STR")){
				DOMConfigurator.configure(new File("str_config/log4j.xml").getAbsolutePath());
				try {	
					// initailize Agent
					STRAgent strAgent = new STRAgent();
					
					if ( args.length == 2 ){
						
						if ("force".equals(args[1])) { // $java Agent STR force
							logger.warn("강제 시작. 다른 STR 연계 모듈 프로세스가 실행되고 있을 수 있습니다.");
							strAgent.controller.doControl(Controller.BOOT_SHUTDOWN, null, Controller.SERVICE_STR);
						} else if ( "version".equals(args[1])) { // $java Agent STR version
							logger.info("*----------------------------------------------------------*");
							logger.info("*   STR Agent (Ver : " + strAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ")");
							logger.info("*----------------------------------------------------------*");
							System.exit(0);
						}
					}
					strAgent.checkSystem();
//					strAgent.chmodDir_STR();
					logger.info("*---------------------------------------------------------------------------*");
					logger.info("*   STR 연계 모듈  (Ver : " + strAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") init  " + new java.util.Date());
					logger.info("*---------------------------------------------------------------------------*");
					
					logger.info("STR Agent를 기동합니다.");
					strAgent.startup();
					
				}catch ( Throwable t ) {
					printError(t);
					System.exit(0);
				}	
			} else {
				printError("서비스 값이 잘못설정되었습니다. (가능옵션:STR,CTR)");
				System.exit(0);
			}
		}
		
	}
	
	public static void printError(String errorDoc){
		logger.error("AGENT 시작 실패. 동작을 정지합니다. : " + errorDoc);
		logger.info("*---------------------------------------------------*"); 
		logger.info("*      AGENT 연계 모듈  " + new java.util.Date()); 
		logger.info("*---------------------------------------------------*"); 
		logger.info("shutdown ......");
	}
	
	public static void printError(Throwable t){
		logger.error("AGENT 시작 실패. 동작을 정지합니다. : "+ t.getMessage());
		//t.printStackTrace();
		logger.info("*---------------------------------------------------*"); 
		logger.info("*      AGENT 연계 모듈  " + new java.util.Date()); 
		logger.info("*---------------------------------------------------*"); 
		logger.info("shutdown ......");
	}

	/**
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

}
