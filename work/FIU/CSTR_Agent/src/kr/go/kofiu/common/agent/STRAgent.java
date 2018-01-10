package kr.go.kofiu.common.agent;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.helpers.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;

import kr.go.kofiu.str.conf.FilePathInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.JobInfo;
import kr.go.kofiu.str.service.AgentUpdateJob;
import kr.go.kofiu.str.summary.UtilSummary;
import kr.go.kofiu.str.util.CSVLogger;
import kr.go.kofiu.str.util.CSVUtil;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import kr.go.kofiu.str.util.GUIDUtil;
import kr.go.kofiu.str.util.MessageHandler;
import kr.go.kofiu.str.util.XmlParserData;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.ThreadExecutorInfo;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class STRAgent {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(STRAgent.class);
	
	/**
	 *  controller
	 */
	Controller controller = null;
	
	/**
	 *  instance
	 */
	private static STRAgent instance;
	
	/**
	 * STR ���� ���� ���� ������ Ǯ 
	 */
	private ThreadPoolExecutor executor;
	
	/**
	 * scheduler
	 */
	private Scheduler scheduler;
	
	/**
	 * single ton Class
	 * @return
	 */
	public static STRAgent getInstance() {
		return instance;
	}
	
	/**
	 * �ý����� Shutdown�Ѵ�. 
	 */
	public void shutdown()  {		
		shutdown(false);
	}
	
	private String checkSystemFolder() {
		// ������ ����ó��
		ArrayList list = new ArrayList();
		list.add(Controller.BOOT_PATH);
		FilePathInfo filePathInfo = STRConfigure.getInstance().getAgentInfo().getFilePathInfo();
		list.add("./str_cert");
		list.add("./str_config");
		
		
		// MessageBox check
		list.add(filePathInfo.getMESSAGE());
		list.add(filePathInfo.getOUTBOX());
		list.add(filePathInfo.getPROC());
		list.add(filePathInfo.getINBOX());
		list.add(filePathInfo.getINBOX_ACCEPT());
		list.add(filePathInfo.getINBOX_ARRIVE());
		list.add(filePathInfo.getINBOX_PREACCEPT());
		list.add(filePathInfo.getINBOX_SEND());
		list.add(filePathInfo.getARCHIVE());
		list.add(filePathInfo.getARCHIVE_RCV());
		list.add(filePathInfo.getARCHIVE_RCV_ACCEPT());
		list.add(filePathInfo.getARCHIVE_RCV_ARRIVE());
		list.add(filePathInfo.getARCHIVE_RCV_PREACCEPT());
		list.add(filePathInfo.getARCHIVE_RCV_SEND());
		list.add(filePathInfo.getARCHIVE_SEND());
		list.add(filePathInfo.getARCHIVE_SEND_ERR());
		list.add(filePathInfo.getREPORT());
		list.add(filePathInfo.getREPORT_RCV());
		list.add(filePathInfo.getREPORT_SEND());

		Iterator iter = list.iterator();
		while(iter.hasNext()){
			String path = (String)iter.next();
			File f = new File(path);
			if ( !f.exists() || !f.isDirectory() ) 
				return path;
		}

		
		return "ok";
	}
	
	/**
	 * BOOT_UPDATE_PHASE3 �� ��� .bak ���� ���� 
	 * @throws AgentException
	 */
	private void checkBootCtrl() throws AgentException {
		String bootType = controller.doControl(Controller.BOOT_GET, null, Controller.SERVICE_STR);
		if ( Controller.BOOT_UPDATE_PHASE3.equals(bootType) ) {
			
			controller.doControl(Controller.BOOT_STARTUP, null, Controller.SERVICE_STR);

//			logger.info("finish cleaning.....");
			// send update info to cg system 
		} else if (Controller.BOOT_UPDATE_PHASE1.equals(bootType)
				|| Controller.BOOT_UPDATE_PHASE2.equals(bootType) ){
			String msg = "STR ���� ��� ������Ʈ�� �����Ͽ����ϴ�.\n"
				+ "FIU ����ܿ� ���� �ϼż� �������� ������Ʈ �Ͻñ� �ٶ��ϴ�.\n" ;
			throw new AgentException(msg);
		} else if ( Controller.BOOT_STARTUP.equals(bootType) ) {
			String msg = "STR ���� ����� �۵��ϰ� �ְų� ������������ ���� �Ǿ����ϴ�.\n"
					+ "���� ���� ���� ���μ����� �ִ��� üũ�Ͻʽÿ�.\n" 
					+ "���μ����� ���ٸ� force�� ���ڷ� �Ѱܼ� ����ó�� �����Ͻñ� �ٶ��ϴ�. \"start.sh STR force\"";
			throw new AgentException(msg);
		}
	}
	
//	public void chmodDir_STR(){
//		// ���� ���� ���� ����
//		String osName = System.getProperty("os.name").toLowerCase();
//		if(osName.indexOf("win") >=0){
//			
//		}
//		// �ü���� UNIX or LINUX �� ��� Message ������ ���ϱ����� �����Ѵ�.
//		else{
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
//				STRAgent.getInstance().shutdown();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				STRAgent.getInstance().shutdown();
//			}		
//			logger.info("������ ���丮 ���� ����Ϸ�");
//		}		
//	}
	
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
				logger.info("*  STR ���� ���  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") restart " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else 
				logger.info("*  STR ���� ���  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") stop " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) {
				logger.info("STR ���� ����� �� ���� �մϴ�.");
			} else {
				logger.info("STR ���� ����� �����մϴ�.");
			}
				
			if ( scheduler != null ) scheduler.shutdown();
			
			if ( !executor.isShutdown() ) executor.shutdown();
			
			if ( !restart ) {
				controller.doControl(Controller.BOOT_SHUTDOWN,null,Controller.SERVICE_STR);
			}
		} catch (Exception e) {
			logger.error("STR ���� ��� ���� ���μ��� ���� �߿� ������ �߻��Ͽ����ϴ�. �����ϰ�  ������ ���� �˴ϴ�.", e); 
		}
		
		
		if ( restart ) {
			System.exit(100);
		} else	{
			logger.info("STR ���� ����� ���������� �����Ͽ����ϴ�.");
			System.exit(0);
		}
	}

	
	/**
	 * ������
	 * @throws AgentException 
	 **/
	@SuppressWarnings("static-access")
	public STRAgent() throws AgentException 
	{
		// controller
 		controller = new Controller();
 		ThreadExecutorInfo info;
		try {
			info = STRConfigure.getInstance().getAgentInfo().getThreadExecutorInfo();
			executor = new ThreadPoolExecutor(info.getCorePoolSize(), info.getMaximumPoolSize()
	 				, info.getKeepAliveTime(), TimeUnit.SECONDS , new LinkedBlockingQueue());
	 		
			instance = this;
			
		} 
 		
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			shutdown();
			throw new AgentException(e);
		} 
		
	}
	
	/**
	 * Agent ������ ȯ�漳���� ����
	 * @throws AgentException 
	 */
	public void checkSystem() throws AgentException{
		checkBootCtrl();
		try{
			logger.info("***	JMS ����� �ʱ�ȭ �մϴ�.                 ****");
			//MessageHandler.getInstance();
			
			logger.info("***	STR ���丮�� üũ�մϴ�.                 ****");
			String result = checkSystemFolder();
			if ( !"ok".equals(result) )
				throw new AgentException(result + "������  �������� �ʽ��ϴ�."); 
			
			logger.info("***	STR ��� ������ üũ�մϴ�(�������:"+controller.doControl(Controller.VERSION_GET,"STR")+")             ****");
			
			UtilSummary us = MessageHandler.getInstance().sendChechAgent(XmlParserData.getCheckAgentXML(controller.doControl(Controller.VERSION_GET, "STR")), GUIDUtil.getGUID_FIUF0001());
			
			if(us.getModuleVersionResult().trim().equals("TRUE")){
				logger.info("***	STR ��� ������ üũ�� �Ϸ��߽��ϴ�...        ****");
				
			}else{
				logger.info("***	STR ��� ������ ��ġ�����ʽ��ϴ�. ������Ʈ�� �����մϴ�.        ****");
				
				logger.info("STR ���� ��� ������Ʈ ���� ");
				AgentUpdateJob job = new AgentUpdateJob();
				job.doRetryJob(null);
				logger.info("STR ���� ��� ������Ʈ ����");
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			shutdown();
			throw new AgentException(e);
		} 
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
		//System.setProperty( "javax.xml.rpc.ServiceFactory", "weblogic.webservice.core.rpc.ServiceFactoryImpl" );

		// scheduler
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		scheduler = schedFact.getScheduler();

		// load jobs
		loadJobs(scheduler);

		// set control file data to startup state
		controller.doControl(Controller.BOOT_STARTUP, null, Controller.SERVICE_STR);

		// start schedule
		scheduler.start();		

		// start up msg
		logger.info("STR ���� ����� ���������� ���� �Ǿ����ϴ�.");
		
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
				/*new EmailAction("[STR���� ���:���]�߰������� ��Ʈ���� �����۵��ϴ��� Ȯ���Ͻñ� �ٶ��ϴ�."
						, "���� �߰����� STR���� ����/���� ������ ��/�������� ���ϰ� �ֽ��ϴ�.\n��Ʈ���� Ȯ���ϰ� �߰����� �����ڿ��� �����Ͻñ� �ٶ��ϴ�.", null);*/
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
	 * Job�� �ε��Ѵ�.
	 * @param sched Quartz Scheduler
	 * @throws AgentException Agent �ý��� ���� üũ �߿� ���� �߻� 
	 * �߻� �� Agent�� �������� ������ ������ �� ���� �����̹Ƿ� Shutdown�Ѵ�.
	 */
	public void loadJobs(Scheduler sched) throws AgentException  
	{
		Collection col = STRConfigure.getInstance().getAgentInfo().getSchedule();
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


}
