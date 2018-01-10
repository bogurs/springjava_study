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
 * ����   �׷�� : CTR �ý���
 * ����   ������ : ���� ��� Agent
 * ��        �� : Agent ������ ���� Main Ŭ����. 
 * ��   ��   �� : ����ȣ 
 * ��   ��   �� : 2005. 7. 29
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
	 * CTR ���� ���� ���� ������ Ǯ 
	 */
	private ThreadPoolExecutor executor;
	

	
	/**
	 * Agent Main ���� �޼ҵ� 
	 * @param args
	 */
	public static void main(String[] args) {
		// initailize Log4j
		
		if(args.length ==0){
			printError("���񽺰��� �������� �ʾҽ��ϴ�.");
			System.exit(0);
		} else{
			String service = args[0]; // ��û ���񽺰� �������� ( value : CTR / STR )
			if(service.equalsIgnoreCase("CTR")){ // ��û ���񽺰� CTR�� ���
				DOMConfigurator.configure(new File("ctr_config/log4j.xml").getAbsolutePath());
				try {	
					// initailize Agent
					CTRAgent ctrAgent = new CTRAgent();
					
					if ( args.length == 2 ){
						
						if ("force".equals(args[1])) { // $java Agent CTR force
							logger.warn("���� ����. �ٸ� CTR ���� ��� ���μ����� ����ǰ� ���� �� �ֽ��ϴ�.");
							ctrAgent.controller.doControl(Controller.BOOT_SHUTDOWN, null, Controller.SERVICE_CTR);
						} else if ( "version".equals(args[1])) { // $java Agent CTR version
							logger.info("*----------------------------------------------------------*");
							logger.info("*   CTR Agent (Ver : " + ctrAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ")");
							logger.info("*----------------------------------------------------------*");
							System.exit(0);
						}
					}
					
					logger.info("*---------------------------------------------------------------------------*");
					logger.info("*   CTR ���� ���  (Ver : " + ctrAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") init  " + new java.util.Date());
					logger.info("*---------------------------------------------------------------------------*");
					
					
					logger.info("CTR Agent�� �⵿�մϴ�.");
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
							logger.warn("���� ����. �ٸ� STR ���� ��� ���μ����� ����ǰ� ���� �� �ֽ��ϴ�.");
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
					logger.info("*   STR ���� ���  (Ver : " + strAgent.controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") init  " + new java.util.Date());
					logger.info("*---------------------------------------------------------------------------*");
					
					logger.info("STR Agent�� �⵿�մϴ�.");
					strAgent.startup();
					
				}catch ( Throwable t ) {
					printError(t);
					System.exit(0);
				}	
			} else {
				printError("���� ���� �߸������Ǿ����ϴ�. (���ɿɼ�:STR,CTR)");
				System.exit(0);
			}
		}
		
	}
	
	public static void printError(String errorDoc){
		logger.error("AGENT ���� ����. ������ �����մϴ�. : " + errorDoc);
		logger.info("*---------------------------------------------------*"); 
		logger.info("*      AGENT ���� ���  " + new java.util.Date()); 
		logger.info("*---------------------------------------------------*"); 
		logger.info("shutdown ......");
	}
	
	public static void printError(Throwable t){
		logger.error("AGENT ���� ����. ������ �����մϴ�. : "+ t.getMessage());
		//t.printStackTrace();
		logger.info("*---------------------------------------------------*"); 
		logger.info("*      AGENT ���� ���  " + new java.util.Date()); 
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
