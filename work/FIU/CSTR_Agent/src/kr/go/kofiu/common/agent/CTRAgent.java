package kr.go.kofiu.common.agent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.conf.AgentInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.JobInfo;
import kr.go.kofiu.ctr.conf.ThreadExecutorInfo;
import kr.go.kofiu.ctr.service.AgentUpdateJob;
import kr.go.kofiu.ctr.service.JMSSOAPService;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.SecurityUtil;
import kr.go.kofiu.ctr.util.excption.DecryptionException;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.helpers.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;

import com.gpki.gpkiapi.exception.GpkiApiException;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class CTRAgent {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Agent.class);

	/**
	 *  instance
	 */
	private static CTRAgent instance;

	/**
	 *  controller
	 */
	Controller controller = null;
	
	/**
	 * scheduler
	 */
	private Scheduler scheduler;

	/**
	 * CTR 보고 문서 전송 스레드 풀 
	 */
	private ThreadPoolExecutor executor;
	
	/**
	 * 생성자
	 * @throws AgentException 
	 **/
	CTRAgent() throws AgentException 
	{
		// controller
 		controller = new Controller();
 		ThreadExecutorInfo info = Configure.getInstance().getAgentInfo().getThreadExecutorInfo();
 		executor = new ThreadPoolExecutor(info.getCorePoolSize(), info.getMaximumPoolSize()
 				, info.getKeepAliveTime(), TimeUnit.SECONDS , new LinkedBlockingQueue());
 		
		instance = this;
	}

	/**
	 * 단위 초동안 Agent를 정지합니다. 주로 중계 기관의 접속 오류 지속 시
	 * sleep 합니다. 
	 * @param seconds
	 */
	public void sleepAgent(long seconds) {
		try{
			if ( scheduler != null && !scheduler.isPaused()) {
				logger.info("연계 모듈 스케줄러가 Sleep 모드로 들어갑니다.");
     			scheduler.pauseAll();
				logger.info("연계 모듈 스케줄러가 성공적으로  Sleep 모드로 되었습니다.");
				logger.info(seconds + "초 동안 연계 모듈의 동작을 일시 중지 합니다.");
				/*new EmailAction("[CTR연계 모듈:경고]중계기관과의 네트웍이 정상작동하는지 확인하시기 바랍니다."
						, "현재 중계기관에 CTR보고 문서/접수 증서를 송/수신하지 못하고 있습니다.\n네트웍을 확인하고 중계기관이 관리자에게 문의하시기 바랍니다.", null);*/
				scheduler.pauseAll();
				Thread.sleep(seconds * 1000);
				
				logger.info("연계 모듈이 Sleep 모드에서 깨어납니다.");
			}
		} catch (SchedulerException e) {
			logger.warn("",e);
		} catch (InterruptedException e) {
			logger.warn("", e);
		} finally {
			try { 
				if ( scheduler != null  ) {
					scheduler.resumeAll();
				}
			} catch (SchedulerException e1) {
				// fatal error, all trigger is not operated. so restart
				logger.fatal("연계 모듈 스케줄러가 재작동하지 않습니다.", e1);
				try { startup(); } catch (Exception e) { logger.fatal("모듈의 내부 스케줄러 재시작 하지 못하였습니다. 연계모듈을 재시작 합니다.", e); shutdown(true); }
			} // end of try for resume job
		} // end of finally
	}
	
	/**
	 * Agent 구동 전에 시스템의 사전 조건을 체크한다. 
	 * 집중 기관 접속 가능 여부, 폴더 존재 유무, 인증서 파일 유무
	 * @throws AgentException Agent 시스템 사전 조건을 체크 중에 오류 발생 
	 * 발생 시 Agent는 정상적인 동작을 수행할 수 없는 상태이므로 Shutdown한다.
	 * @throws GpkiApiException 
	 * @throws AgentException 
	 */
	public void checkSystem() throws AgentException, GpkiApiException, AgentException {
		// Check boot.ctl file - state action
		checkBootCtrl();
		
		AgentInfo agentInfo = Configure.getInstance().getAgentInfo();
		// check Mandatory configure
		if ( agentInfo.getId() == null
				|| agentInfo.getId().length() ==  0 ) {
			throw new AgentException("config.xml 설정 파일에 Agent/ID 값이 없습니다.");
		}
		
		// check and set ip
		if ( agentInfo.getIp() == null ||
				"".equals(agentInfo.getIp()) ) {
			throw new AgentException("config.xml 설정 파일에 Agent/ip 값이 없습니다.");
		}
		
		// 중계 기관 접근 테스트
		if ( ! agentInfo.isTest() ) {
			checkAccessToEsb();
//			checkAccessToCg();
		}
		
		// 암호화 테스트 
		checkEncryption(agentInfo);
		
		// 보고 문서 권한 변경	
		//chmodDir_CTR();		
	}
	
//	private void chmodDir_CTR() {
//		String osName = System.getProperty("os.name").toLowerCase();
//		if(osName.indexOf("win") >=0){
//					
//		}else{
//			logger.info("보고문서 디렉토리 권한을 변경합니다.");
//			String cmd = "umask 000";
//			Runtime runtime = Runtime.getRuntime();
//			Process process;
//			try {
//				process = runtime.exec(cmd);
//				process.waitFor();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				CTRAgent.getInstance().shutdown();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				CTRAgent.getInstance().shutdown();
//			}	
//			logger.info("보고문서 디렉토리 권한 변경완료");
//		}
//	}

	public String getGUID_FIUF0001(){ //Check Agent용 GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

	@SuppressWarnings("unchecked")
	private void checkAccessToEsb() throws AgentException {
		// ESB 서버 접속 테스트
		logger.info("CTR Agent 환경 구성중입니다. 잠시만 기다려주세요...");	
		String serviceMode = Controller.CHECKAGENT;
		
		JMSSOAPService jmssoapService;
		HashMap<String, String> agentCheckResultMap = new HashMap<String, String>();
		try {
			String GUID = getGUID_FIUF0001();
			jmssoapService = JMSSOAPService.getInstance();
			agentCheckResultMap = (HashMap<String, String>) jmssoapService.EsbSoapService(serviceMode, GUID);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AgentException(e);
			//e.printStackTrace();
		}
		
		//Agent Check Agent 로깅
		//new ReportAction(Controller.CHECKAGENT, agentCheckResultMap.get("resultCode"), agentCheckResultMap.get("resultMessage")).doAct();
		
		String result = agentCheckResultMap.get("MemoryData");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			logger.info("이 보고 모듈 설치 기관은 등록 되지 않은 ID(" + Configure.getInstance().getAgentInfo().getId() + ")입니다. 먼저 FIU 사업단에 보고 모듈 설치 기관으로 등록을 하시기 바랍니다." );
			throw new AgentException("이 보고 모듈 설치 기관은 등록 되지 않은 ID(" + Configure.getInstance().getAgentInfo().getId() + ")입니다. 먼저 FIU 사업단에 보고 모듈 설치 기관으로 등록을 하시기 바랍니다.");
		}
		
		result = agentCheckResultMap.get("IPResult");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			logger.info("설치된 기관의 CTR 연계 모듈 IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " 접속이 허락되지 않은 IP입니다. " );
			throw new AgentException("설치된 기관의 CTR 연계 모듈 IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " 접속이 허락되지 않은 IP입니다. " );
		}

		result = agentCheckResultMap.get("TimeResult");
		logger.info("Time : "+agentCheckResultMap.get("TimeValue"));//추가
		if ( "FALSE".equalsIgnoreCase(result) ) {
			String time = agentCheckResultMap.get("TimeValue");
			logger.info("설치된 기관의 CTR 보고 문서 전송 기능은 부하 분산을 위해  " + time + " ~ 24:00 시간에만 전송 가능하게 제한 되어 있습니다.");
		}

		result = agentCheckResultMap.get("ModuleVersionResult");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			String moduleVersion = agentCheckResultMap.get("moduleVersion");
			logger.info("설치된 기관의 CTR 연계 모듈의 버전이  " + Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR)
					+ "으로 현재 실행 가능한 버젼 " + moduleVersion + "과 다릅니다.");
			
			logger.info("CTR 연계 모듈 업데이트 시작 ");
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
			logger.info("CTR 연계 모듈 업데이트 종료");
		}
		logger.info("ESB 시스템 서비스 접속 테스트 성공");		
	}

	/**
	 * 중계 기관의 접근 테스트를 수행한다. 
	 * @throws AgentException
	 */
	private void checkAccessToCg() throws AgentException {
		/*// 네트워 테스트 및 Agent check
		CgServer cgServer = Configure.getInstance().getCgServer();
		if ( cgServer.getIp() == null ||  cgServer.getIp().length() ==  0 )
			throw new AgentException("CTR 연계모듈 설정 파일(config/config.xml)에서  CgServer IP 값이 없습니다.");
		
		// NAT를 이용하여 중계기관 IP를 변경하여 사용하는 경우. 중계 기관에서 wsdl을 가져오지 않고 
		// 세팅되어 있던 값을 사용한다.
		if ( !Configure.getInstance().getAgentInfo().isUseLocalWSDL() ) { 
			try {
				String remoteWSDLUrl = CtrAgentEnvInfo.getRemoteWSDLUrl();
				// wsdl 파일 로컬에 저장 
				logger.info("중계기관 시스템에서 WSDL파일을 가져옵니다. URL : " + remoteWSDLUrl);
				URL url = new URL(remoteWSDLUrl);
				logger.info("url : " + url); //추가
				URLConnection conn  = url.openConnection();
				logger.info("conn : " + conn); //추가
				BufferedReader in  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				logger.info("in : " + in); //추가
				
				
				StringBuffer buf = new StringBuffer();
				String temp = in.readLine();
				while( temp != null ) {
					buf.append(temp).append("\r\n");
					temp = in.readLine();
				}
				
				FileTool.writeToFile(CtrAgentEnvInfo.CONFIG_DIR + File.separator + "CGWebService.wsdl", buf.toString());
				logger.info("CtrAgentEnvInfo.CONFIG_DIR : " + CtrAgentEnvInfo.CONFIG_DIR); //추가
				logger.info("File.separator : " + File.separator);//추가
				logger.info("CGWebService.wsdl");//추가
				logger.info("buf.toString() : " + buf.toString());//추가
			} catch (Exception e) {
				throw new AgentException("중계기관의 WSDL 파일을 저장하는데 실패하였습니다.", e);
			}
		}
		
		logger.info("중계기관 시스템 서비스 접속 테스트 - " + CtrAgentEnvInfo.getLocalWSDLUrl());

		ItemAnonType[]  map = null;
		try {
			CGWebService cgSvc = new CGWebService_Impl(CtrAgentEnvInfo.getLocalWSDLUrl());
			CGWebServiceSoap soap = cgSvc.getCGWebServiceSoap();
			map = soap.checkAgent(Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET),
							Configure.getInstance().getAgentInfo().getIp(),
							Configure.getInstance().getAgentInfo().getId());
			logger.info("Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET) : " + Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET));//추가
			logger.info("Configure.getInstance().getAgentInfo().getIp() : " + Configure.getInstance().getAgentInfo().getIp());//추가
			logger.info("Configure.getInstance().getAgentInfo().getId() : " + Configure.getInstance().getAgentInfo().getId());//추가
		} catch (Exception e) {
			throw new AgentException("중계 기관  시스템 서비스 접속에 실패하였습니다. 네트웍에 문제가 있는 것으로 추정됩니다. 네트웍을 확인해 보시기 바랍니다.",e);
		}
			
		String result = findItemAnonTypeArr(map, "MemoryData");
		if ( "false".equalsIgnoreCase(result) ) {
			logger.info("이 보고 모듈 설치 기관은 등록 되지 않은 ID(" + Configure.getInstance().getAgentInfo().getId() + ")입니다. 먼저 FIU 사업단에 보고 모듈 설치 기관으로 등록을 하시기 바랍니다." );
			throw new AgentException("이 보고 모듈 설치 기관은 등록 되지 않은 ID(" + Configure.getInstance().getAgentInfo().getId() + ")입니다. 먼저 FIU 사업단에 보고 모듈 설치 기관으로 등록을 하시기 바랍니다.");
		}
		
		result = findItemAnonTypeArr(map, "IPResult");
		if ( "false".equalsIgnoreCase(result) ) {
			logger.info("설치된 기관의 CTR 연계 모듈 IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " 접속이 허락되지 않은 IP입니다. " );
			throw new AgentException("설치된 기관의 CTR 연계 모듈 IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " 접속이 허락되지 않은 IP입니다. " );
		}

		result = findItemAnonTypeArr(map, "TimeResult");
		logger.info("Time : "+findItemAnonTypeArr(map, "Time"));//추가
		if ( "false".equalsIgnoreCase(result) ) {
			String time = findItemAnonTypeArr(map, "Time");
			logger.info("설치된 기관의 CTR 보고 문서 전송 기능은 부하 분산을 위해  " + time + " ~ 24:00 시간에만 전송 가능하게 제한 되어 있습니다.");
		}

		result = findItemAnonTypeArr(map, "ModuleVersionResult");
		if ( "false".equalsIgnoreCase(result) ) {
			String moduleVersion = findItemAnonTypeArr(map, "ModuleVersion");
			logger.info("설치된 기관의 CTR 연계 모듈의 버전이  " + Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET)
					+ "으로 현재 실행 가능한 버젼 " + moduleVersion + "과 다릅니다.");
			
			logger.info("CTR 연계 모듈 업데이트 시작 ");
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
			logger.info("CTR 연계 모듈 업데이트 종료");
		}
		logger.info("중계기관 시스템 서비스 접속 테스트  성공");*/
	}

	/**
	 * 암호화 및 전자서명 테스트 
	 * @throws AgentException 
	 * @throws GpkiApiException 
	 *
	 */
	private void checkEncryption(AgentInfo agentInfo) throws AgentException, GpkiApiException {
		// folder check
		String result = checkSystemFolder();
		if ( !"ok".equals(result) )
			throw new AgentException(result + "폴더가  존재하지 않습니다."); 
		
		// 인증서 파일 체크 
		if ( agentInfo.getEncryptionInfo().getSigningInfo().isEnabled() ) {
			String certfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator 
							+ Configure.getInstance().getEncryptionSigningInfo().getCertificate();
			String keyfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator 
							+ Configure.getInstance().getEncryptionSigningInfo().getKey();
			
			File f = new File(certfile);
			if ( !f.exists() )
				throw new AgentException("전자 서명 인증서 파일 '" + certfile + "' 이 존재하지 않습니다.");
			
			File f2 = new File(keyfile);
			if ( !f2.exists() )
				throw new AgentException("전자 서명 인증서 키 파일 '" + keyfile + "' 이 존재하지 않습니다." );
		}
		
		// 암호화할 경우 파일 체크 
		if ( Configure.getInstance().getEncryptionInfo().isEnabled() ) {
			//String certfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator	+ Configure.getInstance().getEncryptionInfo().getPrivateCertInfo().getCertificate();
			String certfile = CtrAgentEnvInfo.getFIUPublicKeyFile();
			File f = new File(certfile);
			if ( !f.exists() )
				throw new AgentException("보고 문서 암호화 공개키  파일'" + certfile + "' 이 존재하지 않습니다.");
		}
		
		try {
			if ( Configure.getInstance().getEncryptionInfo().isEnabled() 
					|| agentInfo.getEncryptionInfo().getSigningInfo().isEnabled() ) {
				String message = "Hello, World!";
				SecurityUtil.encrypt(message.getBytes());
				logger.info("암호화 및 전자 서명 테스트가 성공하였습니다.");
				
			}
		} catch (UnsatisfiedLinkError e) {
			logger.error("암호화  Native 라이브러리가 올바르게 동작할 수 없습니다.\n" 
						+ "라이브러 파일이 /usr/lib 폴더에 올바르게  링크가 걸려있는지 확인하고, gcc가 설치 되어 있는지 확인 바랍니다.", e);
			shutdown();
		/*} catch (GpkiSDKException e) {
			if ( e.returnedValue() == 70018 ){
				logger.error("인증서 비밀번호 오류, 인증서 비밀번호가 정확한지 확인하십시요.");
			} else {
				logger.error("전자 서명 인증에 실패하였습니다. 오류코드 : " + e.returnedValue(), e);
			}
			shutdown();
		}*/
		} catch (DecryptionException e) {
			logger.error("전자 서명 인증에 실패하였습니다. 오류코드 : ", e);
			shutdown();
		}
	}

	
	/**
	 * Agent 구동 시 필요한 폴더가 정의되어 있는지 체크한다. 
	 * @return "OK" 이면 정상이고, 그렇지 않으면 오류에 대한 메세지를 리턴한다.
	 */
	private String checkSystemFolder() {
		// 없으면 에러 
		ArrayList list = new ArrayList();
		list.add(Controller.BOOT_PATH);
		list.add(Controller.LOG_PATH);
		list.add(CtrAgentEnvInfo.CERTIFICATE_DIR);
		list.add(CtrAgentEnvInfo.CONFIG_DIR);
		list.add(CtrAgentEnvInfo.FIU_PUBLIC_KEY_DIR);
		
		// MessageBox check
		list.add(CtrAgentEnvInfo.OUTBOX_DIR_NAME);
		list.add(CtrAgentEnvInfo.PROC_DIR_NAME);
		list.add(CtrAgentEnvInfo.PROC_SEND_DIR_NAME);
		list.add(CtrAgentEnvInfo.PROC_RECEIVE_DIR_NAME);
		list.add(CtrAgentEnvInfo.INBOX_DIR_NAME);
		list.add(CtrAgentEnvInfo.ARCHIVE_DIR_NAME);
		list.add(CtrAgentEnvInfo.ARCHIVE_RECEIVE_DIR_NAME);
		list.add(CtrAgentEnvInfo.ARCHIVE_RECEIVE_ERROR_DIR_NAME);
		if(Configure.getInstance().getAgentInfo().isArchiveEnabled()){
			list.add(CtrAgentEnvInfo.ARCHIVE_SEND_DIR_NAME);
			list.add(CtrAgentEnvInfo.ARCHIVE_SEND_ERROR_DIR_NAME);
		}else{
			logger.info("ARCHIVE에 성공/실패 보고문서를 적재하지 않습니다.");
		}
		list.add(CtrAgentEnvInfo.REPORT_DIR_NAME);
		list.add(CtrAgentEnvInfo.REPORT_RESP_DIR_NAME);
		list.add(CtrAgentEnvInfo.REPORT_SEND_DIR_NAME);

		Iterator iter = list.iterator();
		while(iter.hasNext()){
			String path = (String)iter.next();
			File f = new File(path);
			if ( !f.exists() || !f.isDirectory() ) 
				return path;
		}

		// 기관 코드 , 폴더 없으면 생성 
		// 없으면 생성 
		ArrayList fcltyLists = new ArrayList();
		if ( Configure.getInstance().getAgentInfo().getMessageBoxEnv().isFolderPerFcltyCode() ) {
			Collection col = Configure.getInstance().getAgentInfo().getMessageBoxEnv().getFcltyCodes();
			Iterator iter2 = col.iterator();
			while( iter2.hasNext() ) {
				String fclty_cd = (String)iter2.next();
				fcltyLists.add(CtrAgentEnvInfo.OUTBOX_DIR_NAME + File.separator + fclty_cd);
				//fcltyLists.add(CtrAgentEnvInfo.INBOX_DIR_NAME + File.separator + fclty_cd);
			}
		}
		
		Iterator iter3 = fcltyLists.iterator();
		while(iter3.hasNext()){
			String path = (String)iter3.next();
			File f = new File(path);
			if ( !f.exists() || !f.isDirectory() ) {
				logger.info("해당 폴더가 존재하지 않아서 다음 폴더를 생성합니다 - " + path );
				f.mkdir();
			}
		}
		
		return "ok";
	}


	/**
	 * BOOT_UPDATE_PHASE3 일 경우 .bak 파일 삭제 
	 * @throws AgentException
	 */
	private void checkBootCtrl() throws AgentException {
		String bootType = controller.doControl(Controller.BOOT_GET, null, Controller.SERVICE_CTR);
		if ( Controller.BOOT_UPDATE_PHASE3.equals(bootType) ) {
			
			// reset config.xml with old file when exist.
			renewConfig();
			
			//logger.info("연계 모듈 업데이트 과정에서 생긴 백업 파일을 삭제합니다.");
			// clear backup files
			//File f = new File("./lib");
			//deleteBakFile(f);
			
			//f = new File(CtrAgentEnvInfo.CONFIG_DIR);
			//deleteBakFile(f);

			controller.doControl(Controller.BOOT_STARTUP, null, Controller.SERVICE_CTR);

//			logger.info("finish cleaning.....");
			// send update info to cg system 
		} else if (Controller.BOOT_UPDATE_PHASE1.equals(bootType)
				|| Controller.BOOT_UPDATE_PHASE2.equals(bootType) ){
			String msg = "CTR 연계 모듈 업데이트가 실패하였습니다.\n"
				+ "FIU 사업단에 문의 하셔서 수동으로 업데이트 하시기 바랍니다.\n" ;
			throw new AgentException(msg);
		} else if ( Controller.BOOT_STARTUP.equals(bootType) ) {
			String msg = "CTR 연계 모듈이 작동하고 있거나 비정상적으로 종료 되었습니다.\n"
					+ "먼저 동작 중인 프로세스가 있는지 체크하십시요.\n" 
					+ "프로세스가 없다면 force를 인자로 넘겨서 다음처럼 실행하시기 바랍니다. \"start.sh CTR force\"";
			throw new AgentException(msg);
		}
	}
	
	/**
	 * config file update 후에 이전 설정 값을 
	 * 새로운 config.xml 에 자동으로 입력한다. 
	 * 실패시에 치명적인 오류는 아니므로 수작업으로 값을 입력한다.
	 * @throws AgentException 
	 */
	private void renewConfig () throws AgentException {
		try {
			File oldConfig = new File(CtrAgentEnvInfo.CONFIG_DIR + "/config.xml.bak");
			long term = System.currentTimeMillis() - oldConfig.lastModified();
			// 10 분 전에 있으면 최신 것으로 판단하고 수정
			if ( oldConfig.exists() && term < 30 * 60 * 1000 ) {
				logger.info("연계 모듈 업데이트 후 이전  config.xml.bak 파일의 내용을 새로운 config.xml에 세팅합니다.");
				// read using digest api
				URL rules = getClass().getResource("/kr/go/kofiu/ctr/conf/config-rules.xml");
				Digester digester = DigesterLoader.createDigester(rules);
		
				// Parse the XML document
				byte[] oldAgentByte = FileTool.getFileByte(oldConfig);
				InputStream input = new ByteArrayInputStream( oldAgentByte );
				AgentInfo  oldAgentInfo = (AgentInfo)digester.parse( input );
	
				// reset some value 
				AgentInfo newAgentInfo = Configure.getInstance().getAgentInfo();
				newAgentInfo.accept(oldAgentInfo);
				FileTool.writeToFile(CtrAgentEnvInfo.CONFIG_DIR + "/config.xml"
						, newAgentInfo.toString().getBytes(), true);
				
				logger.info("설정 내용의 이전 값을 복구 합니다.\n" + newAgentInfo.toString());
			}
		} catch (Exception e) {
			String msg = "연계 모듈 업데이트 후 Agent/config/config.xml 파일의 내용을 수정하는데 실패하였습니다.\n과거 파일 데이타를 config.xml.bak을 이용하여 직접 수정하여 주세요.";
			throw new AgentException(msg, e);
		}
	}
	
	/**
	 * 웹 서비스 형태로 넘어온 key/value 쌍 객체 ItemAnonType의 Array에서 인수로 주어지는 
	 * key값에 해당하는 value값을 얻어온다. 
	 * @param arr
	 * @param key
	 * @return
	 */
	public static String findItemAnonTypeArr(ItemAnonType[] arr, String key) {
		String value = null;
		for ( int i = 0; i < arr.length ; i++) {
			if ( arr[i] == null ) { continue; } //15.02.26 (null 인 경우 다음값을 찾음)
			else if ( key.equals(arr[i].getKey()) )	{
				value = (String)arr[i].getValue();
				return value;
			}
		} 
		return value;
	}

	/**
	 * 웹 서비스 형태로 넘어온 key/value 쌍 객체 ItemAnonType의 Array에서 인수로 주어지는 
	 * key값에 해당하는 value값을 얻어온다. 
	 * @param arr
	 * @param key
	 * @return
	 */
	public static Object findObjectItemAnonType(ItemAnonType[] arr, String key) {
		for ( int i = 0; i < arr.length ; i++) {
			if ( key.equals(arr[i].getKey()) )	{
				return arr[i].getValue();
			}
		} 
		return null;
	}

	/**
	 * Agent를 구동한다. 설정에 등록된 job을 스케줄러에 등록하고 Start한다. 
	 * @throws AgentException Agent 시스템 초기화 및 체크 중에 오류 발생 
	 * 발생 시 Agent는 정상적인 동작을 수행할 수 없는 상태이므로 Shutdown한다.
	 * @throws SchedulerException 서비스를 Quartz 스케줄러에 등록 시 오류 발생.
	 * 발생 시 Agent는 정상적인 동작을 수행할 수 없는 상태이므로 Shutdown한다.
	 */
	public void startup() throws AgentException, SchedulerException {
		//set weblogic ServiceFactory
		System.setProperty( "javax.xml.rpc.ServiceFactory", "weblogic.webservice.core.rpc.ServiceFactoryImpl" );

		// scheduler
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		scheduler = schedFact.getScheduler();

		// load jobs
		loadJobs(scheduler);

		// set control file data to startup state
		controller.doControl(Controller.BOOT_STARTUP, null, Controller.SERVICE_CTR);

		// start schedule
		scheduler.start();		

		// start up msg
		logger.info("CTR 연계 모듈이 성공적으로 시작 되었습니다.");
	}
	
	/**
	 * Job을 로드한다.
	 * @param sched Quartz Scheduler
	 * @throws AgentException Agent 시스템 서비스 체크 중에 오류 발생 
	 * 발생 시 Agent는 정상적인 동작을 수행할 수 없는 상태이므로 Shutdown한다.
	 */
	public void loadJobs(Scheduler sched) throws AgentException  
	{
		Collection col = Configure.getInstance().getAgentInfo().getSchedule();
		Iterator iter = col.iterator();
		while (iter.hasNext()) {
			JobInfo element = (JobInfo) iter.next();
			try {
				CronTrigger cronTrigger = new CronTrigger(element.getClazzname() + ".trigger",
						Scheduler.DEFAULT_GROUP, element.getTimeformat());
				JobDetail jobDetail = new JobDetail(element.getClazzname() + ".job"
						, Scheduler.DEFAULT_GROUP, Class.forName(element.getClazzname()));
				
				List list = TriggerUtils.computeFireTimes(cronTrigger, new BaseCalendar(), 2);
				Date first = (Date)list.get(0);
				Date second = (Date)list.get(1);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(first);
				long firstLong = cal.getTimeInMillis();
				cal.setTime(second);
				long secondLong = cal.getTimeInMillis();
				
				JobDataMap map = new JobDataMap();
				map.put("param", element.getParam());
				map.put("period", (secondLong - firstLong) );
				jobDetail.setJobDataMap(map);
				
				sched.scheduleJob(jobDetail, cronTrigger);
			} catch (SchedulerException e) {
				throw new AgentException("스케줄러 등록이 실패하였습니다.", e);
			} catch (ClassNotFoundException e) {
				throw new AgentException("스케줄러 구현 클래스가 보이지 않습니다.", e);
			} catch (ParseException e) {
				throw new AgentException("스케줄러 Timeformat이 올바르지 않습니다.\nPlease, 설정 파일 (config.xml)을 확인해 보시기 바랍니다.", e);
			}
		}
	}
	
	
	/**
	 * 시스템을 Shutdown한다. 
	 */
	public void shutdown()  {		
		shutdown(false);
	}
		
	/**
	 * 시스템을 Shutdown한다. restart true이면 Error Level 100을 남기고 Shutdown한다.
	 * 시작 스크립트에 trap이 걸려서 100번일 경우 재시작하게 된다.
	 * @param restart true일 경우 100번 error level로 shutdown한다. 
	 */
	public void shutdown(boolean restart)
	{
		try {
			// scheduler shutdown msg
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) 
				logger.info("*  CTR 연계 모듈  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") restart " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else 
				logger.info("*  CTR 연계 모듈  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") stop " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) {
				logger.info("CTR 연계 모듈을 재 시작 합니다.");
			} else {
				logger.info("CTR 연계 모듈을 종료합니다.");
			}
				
			if ( scheduler != null ) scheduler.shutdown();
			
			if ( !executor.isShutdown() ) executor.shutdown();
			
			if ( !restart ) {
				controller.doControl(Controller.BOOT_SHUTDOWN, null, Controller.SERVICE_CTR);
			}
		} catch (Exception e) {
			logger.error("CTR 연계 모듈 종료 프로세스 실행 중에 오류가 발생하였습니다. 무시하고  강제로 종료 됩니다.", e); 
		}
		
		
		if ( restart ) {
			System.exit(100);
		} else	{
			logger.info("CTR 연계 모듈을 성공적으로 종료하였습니다.");
			System.exit(0);
		}
	}
	
	/**
	 * single ton Class
	 * @return
	 */
	public static CTRAgent getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

}
