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
	 * CTR ���� ���� ���� ������ Ǯ 
	 */
	private ThreadPoolExecutor executor;
	
	/**
	 * ������
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
	 * ���� �ʵ��� Agent�� �����մϴ�. �ַ� �߰� ����� ���� ���� ���� ��
	 * sleep �մϴ�. 
	 * @param seconds
	 */
	public void sleepAgent(long seconds) {
		try{
			if ( scheduler != null && !scheduler.isPaused()) {
				logger.info("���� ��� �����ٷ��� Sleep ���� ���ϴ�.");
     			scheduler.pauseAll();
				logger.info("���� ��� �����ٷ��� ����������  Sleep ���� �Ǿ����ϴ�.");
				logger.info(seconds + "�� ���� ���� ����� ������ �Ͻ� ���� �մϴ�.");
				/*new EmailAction("[CTR���� ���:���]�߰������� ��Ʈ���� �����۵��ϴ��� Ȯ���Ͻñ� �ٶ��ϴ�."
						, "���� �߰����� CTR���� ����/���� ������ ��/�������� ���ϰ� �ֽ��ϴ�.\n��Ʈ���� Ȯ���ϰ� �߰����� �����ڿ��� �����Ͻñ� �ٶ��ϴ�.", null);*/
				scheduler.pauseAll();
				Thread.sleep(seconds * 1000);
				
				logger.info("���� ����� Sleep ��忡�� ����ϴ�.");
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
				logger.fatal("���� ��� �����ٷ��� ���۵����� �ʽ��ϴ�.", e1);
				try { startup(); } catch (Exception e) { logger.fatal("����� ���� �����ٷ� ����� ���� ���Ͽ����ϴ�. �������� ����� �մϴ�.", e); shutdown(true); }
			} // end of try for resume job
		} // end of finally
	}
	
	/**
	 * Agent ���� ���� �ý����� ���� ������ üũ�Ѵ�. 
	 * ���� ��� ���� ���� ����, ���� ���� ����, ������ ���� ����
	 * @throws AgentException Agent �ý��� ���� ������ üũ �߿� ���� �߻� 
	 * �߻� �� Agent�� �������� ������ ������ �� ���� �����̹Ƿ� Shutdown�Ѵ�.
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
			throw new AgentException("config.xml ���� ���Ͽ� Agent/ID ���� �����ϴ�.");
		}
		
		// check and set ip
		if ( agentInfo.getIp() == null ||
				"".equals(agentInfo.getIp()) ) {
			throw new AgentException("config.xml ���� ���Ͽ� Agent/ip ���� �����ϴ�.");
		}
		
		// �߰� ��� ���� �׽�Ʈ
		if ( ! agentInfo.isTest() ) {
			checkAccessToEsb();
//			checkAccessToCg();
		}
		
		// ��ȣȭ �׽�Ʈ 
		checkEncryption(agentInfo);
		
		// ���� ���� ���� ����	
		//chmodDir_CTR();		
	}
	
//	private void chmodDir_CTR() {
//		String osName = System.getProperty("os.name").toLowerCase();
//		if(osName.indexOf("win") >=0){
//					
//		}else{
//			logger.info("������ ���丮 ������ �����մϴ�.");
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
//			logger.info("������ ���丮 ���� ����Ϸ�");
//		}
//	}

	public String getGUID_FIUF0001(){ //Check Agent�� GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); �̶�� �� �� �ִ�.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

	@SuppressWarnings("unchecked")
	private void checkAccessToEsb() throws AgentException {
		// ESB ���� ���� �׽�Ʈ
		logger.info("CTR Agent ȯ�� �������Դϴ�. ��ø� ��ٷ��ּ���...");	
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
		
		//Agent Check Agent �α�
		//new ReportAction(Controller.CHECKAGENT, agentCheckResultMap.get("resultCode"), agentCheckResultMap.get("resultMessage")).doAct();
		
		String result = agentCheckResultMap.get("MemoryData");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			logger.info("�� ���� ��� ��ġ ����� ��� ���� ���� ID(" + Configure.getInstance().getAgentInfo().getId() + ")�Դϴ�. ���� FIU ����ܿ� ���� ��� ��ġ ������� ����� �Ͻñ� �ٶ��ϴ�." );
			throw new AgentException("�� ���� ��� ��ġ ����� ��� ���� ���� ID(" + Configure.getInstance().getAgentInfo().getId() + ")�Դϴ�. ���� FIU ����ܿ� ���� ��� ��ġ ������� ����� �Ͻñ� �ٶ��ϴ�.");
		}
		
		result = agentCheckResultMap.get("IPResult");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			logger.info("��ġ�� ����� CTR ���� ��� IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " ������ ������� ���� IP�Դϴ�. " );
			throw new AgentException("��ġ�� ����� CTR ���� ��� IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " ������ ������� ���� IP�Դϴ�. " );
		}

		result = agentCheckResultMap.get("TimeResult");
		logger.info("Time : "+agentCheckResultMap.get("TimeValue"));//�߰�
		if ( "FALSE".equalsIgnoreCase(result) ) {
			String time = agentCheckResultMap.get("TimeValue");
			logger.info("��ġ�� ����� CTR ���� ���� ���� ����� ���� �л��� ����  " + time + " ~ 24:00 �ð����� ���� �����ϰ� ���� �Ǿ� �ֽ��ϴ�.");
		}

		result = agentCheckResultMap.get("ModuleVersionResult");
		if ( "FALSE".equalsIgnoreCase(result) ) {
			String moduleVersion = agentCheckResultMap.get("moduleVersion");
			logger.info("��ġ�� ����� CTR ���� ����� ������  " + Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR)
					+ "���� ���� ���� ������ ���� " + moduleVersion + "�� �ٸ��ϴ�.");
			
			logger.info("CTR ���� ��� ������Ʈ ���� ");
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
			logger.info("CTR ���� ��� ������Ʈ ����");
		}
		logger.info("ESB �ý��� ���� ���� �׽�Ʈ ����");		
	}

	/**
	 * �߰� ����� ���� �׽�Ʈ�� �����Ѵ�. 
	 * @throws AgentException
	 */
	private void checkAccessToCg() throws AgentException {
		/*// ��Ʈ�� �׽�Ʈ �� Agent check
		CgServer cgServer = Configure.getInstance().getCgServer();
		if ( cgServer.getIp() == null ||  cgServer.getIp().length() ==  0 )
			throw new AgentException("CTR ������ ���� ����(config/config.xml)����  CgServer IP ���� �����ϴ�.");
		
		// NAT�� �̿��Ͽ� �߰��� IP�� �����Ͽ� ����ϴ� ���. �߰� ������� wsdl�� �������� �ʰ� 
		// ���õǾ� �ִ� ���� ����Ѵ�.
		if ( !Configure.getInstance().getAgentInfo().isUseLocalWSDL() ) { 
			try {
				String remoteWSDLUrl = CtrAgentEnvInfo.getRemoteWSDLUrl();
				// wsdl ���� ���ÿ� ���� 
				logger.info("�߰��� �ý��ۿ��� WSDL������ �����ɴϴ�. URL : " + remoteWSDLUrl);
				URL url = new URL(remoteWSDLUrl);
				logger.info("url : " + url); //�߰�
				URLConnection conn  = url.openConnection();
				logger.info("conn : " + conn); //�߰�
				BufferedReader in  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				logger.info("in : " + in); //�߰�
				
				
				StringBuffer buf = new StringBuffer();
				String temp = in.readLine();
				while( temp != null ) {
					buf.append(temp).append("\r\n");
					temp = in.readLine();
				}
				
				FileTool.writeToFile(CtrAgentEnvInfo.CONFIG_DIR + File.separator + "CGWebService.wsdl", buf.toString());
				logger.info("CtrAgentEnvInfo.CONFIG_DIR : " + CtrAgentEnvInfo.CONFIG_DIR); //�߰�
				logger.info("File.separator : " + File.separator);//�߰�
				logger.info("CGWebService.wsdl");//�߰�
				logger.info("buf.toString() : " + buf.toString());//�߰�
			} catch (Exception e) {
				throw new AgentException("�߰����� WSDL ������ �����ϴµ� �����Ͽ����ϴ�.", e);
			}
		}
		
		logger.info("�߰��� �ý��� ���� ���� �׽�Ʈ - " + CtrAgentEnvInfo.getLocalWSDLUrl());

		ItemAnonType[]  map = null;
		try {
			CGWebService cgSvc = new CGWebService_Impl(CtrAgentEnvInfo.getLocalWSDLUrl());
			CGWebServiceSoap soap = cgSvc.getCGWebServiceSoap();
			map = soap.checkAgent(Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET),
							Configure.getInstance().getAgentInfo().getIp(),
							Configure.getInstance().getAgentInfo().getId());
			logger.info("Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET) : " + Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET));//�߰�
			logger.info("Configure.getInstance().getAgentInfo().getIp() : " + Configure.getInstance().getAgentInfo().getIp());//�߰�
			logger.info("Configure.getInstance().getAgentInfo().getId() : " + Configure.getInstance().getAgentInfo().getId());//�߰�
		} catch (Exception e) {
			throw new AgentException("�߰� ���  �ý��� ���� ���ӿ� �����Ͽ����ϴ�. ��Ʈ���� ������ �ִ� ������ �����˴ϴ�. ��Ʈ���� Ȯ���� ���ñ� �ٶ��ϴ�.",e);
		}
			
		String result = findItemAnonTypeArr(map, "MemoryData");
		if ( "false".equalsIgnoreCase(result) ) {
			logger.info("�� ���� ��� ��ġ ����� ��� ���� ���� ID(" + Configure.getInstance().getAgentInfo().getId() + ")�Դϴ�. ���� FIU ����ܿ� ���� ��� ��ġ ������� ����� �Ͻñ� �ٶ��ϴ�." );
			throw new AgentException("�� ���� ��� ��ġ ����� ��� ���� ���� ID(" + Configure.getInstance().getAgentInfo().getId() + ")�Դϴ�. ���� FIU ����ܿ� ���� ��� ��ġ ������� ����� �Ͻñ� �ٶ��ϴ�.");
		}
		
		result = findItemAnonTypeArr(map, "IPResult");
		if ( "false".equalsIgnoreCase(result) ) {
			logger.info("��ġ�� ����� CTR ���� ��� IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " ������ ������� ���� IP�Դϴ�. " );
			throw new AgentException("��ġ�� ����� CTR ���� ��� IP Address : " + Configure.getInstance().getAgentInfo().getIp() + " ������ ������� ���� IP�Դϴ�. " );
		}

		result = findItemAnonTypeArr(map, "TimeResult");
		logger.info("Time : "+findItemAnonTypeArr(map, "Time"));//�߰�
		if ( "false".equalsIgnoreCase(result) ) {
			String time = findItemAnonTypeArr(map, "Time");
			logger.info("��ġ�� ����� CTR ���� ���� ���� ����� ���� �л��� ����  " + time + " ~ 24:00 �ð����� ���� �����ϰ� ���� �Ǿ� �ֽ��ϴ�.");
		}

		result = findItemAnonTypeArr(map, "ModuleVersionResult");
		if ( "false".equalsIgnoreCase(result) ) {
			String moduleVersion = findItemAnonTypeArr(map, "ModuleVersion");
			logger.info("��ġ�� ����� CTR ���� ����� ������  " + Controller.getInstance().doControl(CtrAgentEnvInfo.VERSION_GET)
					+ "���� ���� ���� ������ ���� " + moduleVersion + "�� �ٸ��ϴ�.");
			
			logger.info("CTR ���� ��� ������Ʈ ���� ");
			AgentUpdateJob job = new AgentUpdateJob();
			job.doRetryJob(null);
			logger.info("CTR ���� ��� ������Ʈ ����");
		}
		logger.info("�߰��� �ý��� ���� ���� �׽�Ʈ  ����");*/
	}

	/**
	 * ��ȣȭ �� ���ڼ��� �׽�Ʈ 
	 * @throws AgentException 
	 * @throws GpkiApiException 
	 *
	 */
	private void checkEncryption(AgentInfo agentInfo) throws AgentException, GpkiApiException {
		// folder check
		String result = checkSystemFolder();
		if ( !"ok".equals(result) )
			throw new AgentException(result + "������  �������� �ʽ��ϴ�."); 
		
		// ������ ���� üũ 
		if ( agentInfo.getEncryptionInfo().getSigningInfo().isEnabled() ) {
			String certfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator 
							+ Configure.getInstance().getEncryptionSigningInfo().getCertificate();
			String keyfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator 
							+ Configure.getInstance().getEncryptionSigningInfo().getKey();
			
			File f = new File(certfile);
			if ( !f.exists() )
				throw new AgentException("���� ���� ������ ���� '" + certfile + "' �� �������� �ʽ��ϴ�.");
			
			File f2 = new File(keyfile);
			if ( !f2.exists() )
				throw new AgentException("���� ���� ������ Ű ���� '" + keyfile + "' �� �������� �ʽ��ϴ�." );
		}
		
		// ��ȣȭ�� ��� ���� üũ 
		if ( Configure.getInstance().getEncryptionInfo().isEnabled() ) {
			//String certfile = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator	+ Configure.getInstance().getEncryptionInfo().getPrivateCertInfo().getCertificate();
			String certfile = CtrAgentEnvInfo.getFIUPublicKeyFile();
			File f = new File(certfile);
			if ( !f.exists() )
				throw new AgentException("���� ���� ��ȣȭ ����Ű  ����'" + certfile + "' �� �������� �ʽ��ϴ�.");
		}
		
		try {
			if ( Configure.getInstance().getEncryptionInfo().isEnabled() 
					|| agentInfo.getEncryptionInfo().getSigningInfo().isEnabled() ) {
				String message = "Hello, World!";
				SecurityUtil.encrypt(message.getBytes());
				logger.info("��ȣȭ �� ���� ���� �׽�Ʈ�� �����Ͽ����ϴ�.");
				
			}
		} catch (UnsatisfiedLinkError e) {
			logger.error("��ȣȭ  Native ���̺귯���� �ùٸ��� ������ �� �����ϴ�.\n" 
						+ "���̺귯 ������ /usr/lib ������ �ùٸ���  ��ũ�� �ɷ��ִ��� Ȯ���ϰ�, gcc�� ��ġ �Ǿ� �ִ��� Ȯ�� �ٶ��ϴ�.", e);
			shutdown();
		/*} catch (GpkiSDKException e) {
			if ( e.returnedValue() == 70018 ){
				logger.error("������ ��й�ȣ ����, ������ ��й�ȣ�� ��Ȯ���� Ȯ���Ͻʽÿ�.");
			} else {
				logger.error("���� ���� ������ �����Ͽ����ϴ�. �����ڵ� : " + e.returnedValue(), e);
			}
			shutdown();
		}*/
		} catch (DecryptionException e) {
			logger.error("���� ���� ������ �����Ͽ����ϴ�. �����ڵ� : ", e);
			shutdown();
		}
	}

	
	/**
	 * Agent ���� �� �ʿ��� ������ ���ǵǾ� �ִ��� üũ�Ѵ�. 
	 * @return "OK" �̸� �����̰�, �׷��� ������ ������ ���� �޼����� �����Ѵ�.
	 */
	private String checkSystemFolder() {
		// ������ ���� 
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
			logger.info("ARCHIVE�� ����/���� �������� �������� �ʽ��ϴ�.");
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

		// ��� �ڵ� , ���� ������ ���� 
		// ������ ���� 
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
				logger.info("�ش� ������ �������� �ʾƼ� ���� ������ �����մϴ� - " + path );
				f.mkdir();
			}
		}
		
		return "ok";
	}


	/**
	 * BOOT_UPDATE_PHASE3 �� ��� .bak ���� ���� 
	 * @throws AgentException
	 */
	private void checkBootCtrl() throws AgentException {
		String bootType = controller.doControl(Controller.BOOT_GET, null, Controller.SERVICE_CTR);
		if ( Controller.BOOT_UPDATE_PHASE3.equals(bootType) ) {
			
			// reset config.xml with old file when exist.
			renewConfig();
			
			//logger.info("���� ��� ������Ʈ �������� ���� ��� ������ �����մϴ�.");
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
			String msg = "CTR ���� ��� ������Ʈ�� �����Ͽ����ϴ�.\n"
				+ "FIU ����ܿ� ���� �ϼż� �������� ������Ʈ �Ͻñ� �ٶ��ϴ�.\n" ;
			throw new AgentException(msg);
		} else if ( Controller.BOOT_STARTUP.equals(bootType) ) {
			String msg = "CTR ���� ����� �۵��ϰ� �ְų� ������������ ���� �Ǿ����ϴ�.\n"
					+ "���� ���� ���� ���μ����� �ִ��� üũ�Ͻʽÿ�.\n" 
					+ "���μ����� ���ٸ� force�� ���ڷ� �Ѱܼ� ����ó�� �����Ͻñ� �ٶ��ϴ�. \"start.sh CTR force\"";
			throw new AgentException(msg);
		}
	}
	
	/**
	 * config file update �Ŀ� ���� ���� ���� 
	 * ���ο� config.xml �� �ڵ����� �Է��Ѵ�. 
	 * ���нÿ� ġ������ ������ �ƴϹǷ� ���۾����� ���� �Է��Ѵ�.
	 * @throws AgentException 
	 */
	private void renewConfig () throws AgentException {
		try {
			File oldConfig = new File(CtrAgentEnvInfo.CONFIG_DIR + "/config.xml.bak");
			long term = System.currentTimeMillis() - oldConfig.lastModified();
			// 10 �� ���� ������ �ֽ� ������ �Ǵ��ϰ� ����
			if ( oldConfig.exists() && term < 30 * 60 * 1000 ) {
				logger.info("���� ��� ������Ʈ �� ����  config.xml.bak ������ ������ ���ο� config.xml�� �����մϴ�.");
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
				
				logger.info("���� ������ ���� ���� ���� �մϴ�.\n" + newAgentInfo.toString());
			}
		} catch (Exception e) {
			String msg = "���� ��� ������Ʈ �� Agent/config/config.xml ������ ������ �����ϴµ� �����Ͽ����ϴ�.\n���� ���� ����Ÿ�� config.xml.bak�� �̿��Ͽ� ���� �����Ͽ� �ּ���.";
			throw new AgentException(msg, e);
		}
	}
	
	/**
	 * �� ���� ���·� �Ѿ�� key/value �� ��ü ItemAnonType�� Array���� �μ��� �־����� 
	 * key���� �ش��ϴ� value���� ���´�. 
	 * @param arr
	 * @param key
	 * @return
	 */
	public static String findItemAnonTypeArr(ItemAnonType[] arr, String key) {
		String value = null;
		for ( int i = 0; i < arr.length ; i++) {
			if ( arr[i] == null ) { continue; } //15.02.26 (null �� ��� �������� ã��)
			else if ( key.equals(arr[i].getKey()) )	{
				value = (String)arr[i].getValue();
				return value;
			}
		} 
		return value;
	}

	/**
	 * �� ���� ���·� �Ѿ�� key/value �� ��ü ItemAnonType�� Array���� �μ��� �־����� 
	 * key���� �ش��ϴ� value���� ���´�. 
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
	 * Agent�� �����Ѵ�. ������ ��ϵ� job�� �����ٷ��� ����ϰ� Start�Ѵ�. 
	 * @throws AgentException Agent �ý��� �ʱ�ȭ �� üũ �߿� ���� �߻� 
	 * �߻� �� Agent�� �������� ������ ������ �� ���� �����̹Ƿ� Shutdown�Ѵ�.
	 * @throws SchedulerException ���񽺸� Quartz �����ٷ��� ��� �� ���� �߻�.
	 * �߻� �� Agent�� �������� ������ ������ �� ���� �����̹Ƿ� Shutdown�Ѵ�.
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
		logger.info("CTR ���� ����� ���������� ���� �Ǿ����ϴ�.");
	}
	
	/**
	 * Job�� �ε��Ѵ�.
	 * @param sched Quartz Scheduler
	 * @throws AgentException Agent �ý��� ���� üũ �߿� ���� �߻� 
	 * �߻� �� Agent�� �������� ������ ������ �� ���� �����̹Ƿ� Shutdown�Ѵ�.
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
				throw new AgentException("�����ٷ� ����� �����Ͽ����ϴ�.", e);
			} catch (ClassNotFoundException e) {
				throw new AgentException("�����ٷ� ���� Ŭ������ ������ �ʽ��ϴ�.", e);
			} catch (ParseException e) {
				throw new AgentException("�����ٷ� Timeformat�� �ùٸ��� �ʽ��ϴ�.\nPlease, ���� ���� (config.xml)�� Ȯ���� ���ñ� �ٶ��ϴ�.", e);
			}
		}
	}
	
	
	/**
	 * �ý����� Shutdown�Ѵ�. 
	 */
	public void shutdown()  {		
		shutdown(false);
	}
		
	/**
	 * �ý����� Shutdown�Ѵ�. restart true�̸� Error Level 100�� ����� Shutdown�Ѵ�.
	 * ���� ��ũ��Ʈ�� trap�� �ɷ��� 100���� ��� ������ϰ� �ȴ�.
	 * @param restart true�� ��� 100�� error level�� shutdown�Ѵ�. 
	 */
	public void shutdown(boolean restart)
	{
		try {
			// scheduler shutdown msg
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) 
				logger.info("*  CTR ���� ���  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") restart " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else 
				logger.info("*  CTR ���� ���  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_CTR) + ") stop " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) {
				logger.info("CTR ���� ����� �� ���� �մϴ�.");
			} else {
				logger.info("CTR ���� ����� �����մϴ�.");
			}
				
			if ( scheduler != null ) scheduler.shutdown();
			
			if ( !executor.isShutdown() ) executor.shutdown();
			
			if ( !restart ) {
				controller.doControl(Controller.BOOT_SHUTDOWN, null, Controller.SERVICE_CTR);
			}
		} catch (Exception e) {
			logger.error("CTR ���� ��� ���� ���μ��� ���� �߿� ������ �߻��Ͽ����ϴ�. �����ϰ�  ������ ���� �˴ϴ�.", e); 
		}
		
		
		if ( restart ) {
			System.exit(100);
		} else	{
			logger.info("CTR ���� ����� ���������� �����Ͽ����ϴ�.");
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
