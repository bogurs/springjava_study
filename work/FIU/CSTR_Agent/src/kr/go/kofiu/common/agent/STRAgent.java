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
	 * STR 보고 문서 전송 스레드 풀 
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
	 * 시스템을 Shutdown한다. 
	 */
	public void shutdown()  {		
		shutdown(false);
	}
	
	private String checkSystemFolder() {
		// 없으면 에러처리
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
	 * BOOT_UPDATE_PHASE3 일 경우 .bak 파일 삭제 
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
			String msg = "STR 연계 모듈 업데이트가 실패하였습니다.\n"
				+ "FIU 사업단에 문의 하셔서 수동으로 업데이트 하시기 바랍니다.\n" ;
			throw new AgentException(msg);
		} else if ( Controller.BOOT_STARTUP.equals(bootType) ) {
			String msg = "STR 연계 모듈이 작동하고 있거나 비정상적으로 종료 되었습니다.\n"
					+ "먼저 동작 중인 프로세스가 있는지 체크하십시요.\n" 
					+ "프로세스가 없다면 force를 인자로 넘겨서 다음처럼 실행하시기 바랍니다. \"start.sh STR force\"";
			throw new AgentException(msg);
		}
	}
	
//	public void chmodDir_STR(){
//		// 보고 문서 권한 변경
//		String osName = System.getProperty("os.name").toLowerCase();
//		if(osName.indexOf("win") >=0){
//			
//		}
//		// 운영체제가 UNIX or LINUX 일 경우 Message 폴더의 파일권한을 변경한다.
//		else{
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
//				STRAgent.getInstance().shutdown();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				STRAgent.getInstance().shutdown();
//			}		
//			logger.info("보고문서 디렉토리 권한 변경완료");
//		}		
//	}
	
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
				logger.info("*  STR 연계 모듈  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") restart " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else 
				logger.info("*  STR 연계 모듈  (Ver : " + controller.doControl(Controller.VERSION_GET, Controller.SERVICE_STR) + ") stop " + new java.util.Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.info("*----------------------------------------------------------------------------*");
			if ( restart ) {
				logger.info("STR 연계 모듈을 재 시작 합니다.");
			} else {
				logger.info("STR 연계 모듈을 종료합니다.");
			}
				
			if ( scheduler != null ) scheduler.shutdown();
			
			if ( !executor.isShutdown() ) executor.shutdown();
			
			if ( !restart ) {
				controller.doControl(Controller.BOOT_SHUTDOWN,null,Controller.SERVICE_STR);
			}
		} catch (Exception e) {
			logger.error("STR 연계 모듈 종료 프로세스 실행 중에 오류가 발생하였습니다. 무시하고  강제로 종료 됩니다.", e); 
		}
		
		
		if ( restart ) {
			System.exit(100);
		} else	{
			logger.info("STR 연계 모듈을 성공적으로 종료하였습니다.");
			System.exit(0);
		}
	}

	
	/**
	 * 생성자
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
	 * Agent 구동시 환경설정을 점검
	 * @throws AgentException 
	 */
	public void checkSystem() throws AgentException{
		checkBootCtrl();
		try{
			logger.info("***	JMS 모듈을 초기화 합니다.                 ****");
			//MessageHandler.getInstance();
			
			logger.info("***	STR 디렉토리를 체크합니다.                 ****");
			String result = checkSystemFolder();
			if ( !"ok".equals(result) )
				throw new AgentException(result + "폴더가  존재하지 않습니다."); 
			
			logger.info("***	STR 모듈 버전을 체크합니다(현재버전:"+controller.doControl(Controller.VERSION_GET,"STR")+")             ****");
			
			UtilSummary us = MessageHandler.getInstance().sendChechAgent(XmlParserData.getCheckAgentXML(controller.doControl(Controller.VERSION_GET, "STR")), GUIDUtil.getGUID_FIUF0001());
			
			if(us.getModuleVersionResult().trim().equals("TRUE")){
				logger.info("***	STR 모듈 버전을 체크를 완료했습니다...        ****");
				
			}else{
				logger.info("***	STR 모듈 버전이 일치하지않습니다. 업데이트를 시행합니다.        ****");
				
				logger.info("STR 연계 모듈 업데이트 시작 ");
				AgentUpdateJob job = new AgentUpdateJob();
				job.doRetryJob(null);
				logger.info("STR 연계 모듈 업데이트 종료");
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			shutdown();
			throw new AgentException(e);
		} 
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
		logger.info("STR 연계 모듈이 성공적으로 시작 되었습니다.");
		
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
				/*new EmailAction("[STR연계 모듈:경고]중계기관과의 네트웍이 정상작동하는지 확인하시기 바랍니다."
						, "현재 중계기관에 STR보고 문서/접수 증서를 송/수신하지 못하고 있습니다.\n네트웍을 확인하고 중계기관이 관리자에게 문의하시기 바랍니다.", null);*/
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
	 * Job을 로드한다.
	 * @param sched Quartz Scheduler
	 * @throws AgentException Agent 시스템 서비스 체크 중에 오류 발생 
	 * 발생 시 Agent는 정상적인 동작을 수행할 수 없는 상태이므로 Shutdown한다.
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
				throw new AgentException("스케줄러 등록이 실패하였습니다.", e);
			} catch (ClassNotFoundException e) {
				throw new AgentException("스케줄러 구현 클래스가 보이지 않습니다.", e);
			} catch (ParseException e) {
				throw new AgentException("스케줄러 Timeformat이 올바르지 않습니다.\nPlease, 설정 파일 (config.xml)을 확인해 보시기 바랍니다.", e);
			}
		}
	}


}
