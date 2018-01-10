package kr.go.kofiu.ctr.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.UpdateResponse;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 주기적으로 Agent 업데이트가 필요한지를 체크하고
 * 		업데이트가 필요하면 업데이를 수행한다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 14
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateJob extends RetryJob {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateJob.class);

	/**
	 * 집중 기관에 업데이트 요청을 수행하고, 
	 * 업데이트 모듈이 존재하면, 업데이트 하고 Agent를 restart 한다. 
	 */
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// 테스트 여부 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}
		
		logger.info("#######################################################");
		logger.info("#### CTR 연계모듈 업데이트를 시작하는 중입니다.    ####");
		logger.info("#### 잠시만 기다려주세요.                          ####");
		logger.info("#######################################################");
		try {
			AgentUpdateCommand command = new AgentUpdateCommand(this.numOfRetries, this.timeToWait);
			UpdateResponse response = (UpdateResponse) command.invoke();
			
			// start update
			if ( kr.go.kofiu.ctr.util.UpdateResponse.UPDATE == response.getStatus() ) {
				Scheduler scheduler = null;
				if ( context != null )
					scheduler = context.getScheduler();
				
				updateModule(scheduler, response);
			} else if (kr.go.kofiu.ctr.util.UpdateResponse.FAIL == response.getStatus() ){
				logger.info("CTR 연계 모듈 업데이트를 실패하였습니다. 다음에 시도하여 주십시요.");
			} else {
				logger.info("현재 설치 되어있는 CTR 연계 모듈이 최신 버젼입니다.");
			} 
		} catch (AgentException e ) {
			// controller setting 시 오류 
			e.addAction(new EmailAction("CTR 연계모듈 업데이트 장애", e));
			e.addAction(new ShutdownAction());
			throw e;
		}
	} // end of method
	
	/**
	 * 
	 * @param scheduler
	 * @param response
	 * @throws AgentException
	 */
	private void updateModule( Scheduler scheduler , UpdateResponse response  ) throws AgentException {
		logger.info("CTR 연계 모듈 업데이트 파일 다운로드 시작.");

		// pause other trigger
		try {
			if ( scheduler != null ) {
				scheduler.pauseAll();
				logger.info("모듈 업데이트를 위해 기존  프로세스를 일시적으로 중지하였습니다.");
			}
		
			logger.debug(response);
			String nowVer = Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR);
			String newVer = response.getNewModuleVersion();
			logger.info("CTR 연계 모듈 현재 버젼(" + nowVer + ")을 새 버젼(" + newVer + ")으로 업데이트 합니다.");
			
			//Map reponseMap = response.getFiles();
			/*ItemAnonType[] responseItem = response.getFiles();
			if ( responseItem == null ) {
				logger.warn("업데이트 서버에서 받은 파일 데이타가 없습니다.");
				return ;
			}
			*/
			Map files = new HashMap();
			
			//if ( Agent.findObjectItemAnonType(responseItem, "MdlLib") != null ) {
			if (response.getMdlLib()  != null ) {
				byte[] agent_module = response.getMdlLib();
				// use jar lib
				logger.info("LIB 파일 업데이트 목록  ");
				Map temp = FileTool.unzip(agent_module, "./lib/");
				files.putAll(temp);
				logger.debug("LIB 파일 업데이트 목록 : " + temp);
			}
			
			// 미사용
			/*
			//if ( Agent.findObjectItemAnonType(responseItem, "Config") != null ) {
			if (response.getConfig()  != null ) {
				byte[] agent_config = response.getConfig();
				logger.info("설정 파일 업데이트 목록 ");
				Map temp = FileTool.unzip(agent_config, "./config/");
				files.putAll(temp);
				logger.debug("설정 파일 업데이트 목록 : " + temp);
				// config.xml, CGWebService.wsdl, log4j.xml
				// at restart up, set config value with current value
				// because of config-rules.xml
			}
			*/
	
//			if ( Agent.findObjectItemAnonType(responseItem,"PblKey") != null ) {
			if (response.getPblKey()  != null ) {
				byte[] public_key = response.getPblKey() ;
				logger.info("암호화 공개키 업데이트 ");
				files.put(CtrAgentEnvInfo.getFIUPublicKeyFile(), public_key);
			}
		
			
			// file bakup and update.
			innerProcess(files);
			
			// module version update
			logger.info("#### CTR 연계 모듈 버젼을 " + newVer + " 값으로 변경합니다.");
			Controller.getInstance().doControl(Controller.VERSION_SET, newVer, Controller.SERVICE_CTR);
	
			logger.info("#### CTR 연계 모듈 업데이트가 성공적으로 완료 되었습니다.");
			 
			logger.info("#### CTR 연계 모듈을 재시작합니다.");
			Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE3, null, Controller.SERVICE_CTR);
			CTRAgent.getInstance().shutdown(true);
		} catch (Exception e) {
			// SchedulerException - fail to pauseAll trigger, ignore, next time do that
			// IOException - 모듈 unzip error
			AgentException ae = new AgentException("CTR 연계 모듈 업데이트 장애", e);
			throw ae;
		} finally {
			try { 
				if ( scheduler != null ) 
					scheduler.resumeAll();
			} catch (SchedulerException e1) {
				// fatal error, all trigger is not operated. so restart
				logger.fatal("Shutdown Agent... and restart.", e1);
				CTRAgent.getInstance().shutdown(true);
			}
		}
	}
	
	
	/**
	 * 
	 * @param files
	 * @throws AgentException
	 * @throws IOException 
	 */
	private void innerProcess(Map files) throws AgentException, IOException {
		Set set = files.keySet();
//		 원 본 백업 start backup
		Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE1, null, Controller.SERVICE_CTR);
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
		  String element = (String) iter.next();
		  File f = new File(element);
		  if ( f.exists() )
			  FileTool.copyFile(f, new File(element + ".bak"));
		}
//		 end backup 
//		 and start  overwrite original file with new file data
//		 if update fail , 원 본 파일로 복구 
		Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE2, null, Controller.SERVICE_CTR);

		iter = set.iterator();
		while (iter.hasNext()) {
		  String element = (String) iter.next();
		  byte[] filedata = (byte[])files.get(element);
		  
		  FileTool.writeToFile(element, filedata);
		}

//		 ok
//		 end update restart Agent
	}

	
}
