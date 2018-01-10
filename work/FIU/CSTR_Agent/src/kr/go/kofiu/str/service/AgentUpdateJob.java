package kr.go.kofiu.str.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.common.agent.RetryJob;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.UpdateResponse;
import kr.go.kofiu.str.conf.STRConfigure;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : �ֱ������� Agent ������Ʈ�� �ʿ������� üũ�ϰ�
 * 		������Ʈ�� �ʿ��ϸ� �����̸� �����Ѵ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 14
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateJob extends RetryJob {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateJob.class);

	/**
	 * ���� ����� ������Ʈ ��û�� �����ϰ�, 
	 * ������Ʈ ����� �����ϸ�, ������Ʈ �ϰ� Agent�� restart �Ѵ�. 
	 */
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// �׽�Ʈ ���� 
		if ( Configure.getInstance().getAgentInfo().isTest() ) {
			return ;
		}
		
		logger.info("#######################################################");
		logger.info("#### STR ������ ������Ʈ�� �����ϴ� ���Դϴ�.    ####");
		logger.info("#### ��ø� ��ٷ��ּ���.                          ####");
		logger.info("#######################################################");
		try {
			logger.info("STR ���� ��� ������Ʈ ���� �ٿ�ε� ����.");
			// ������Ʈ JMS ��� ȣ��
			AgentUpdateCommand command = new AgentUpdateCommand(this.numOfRetries, this.timeToWait);
			UpdateResponse response = (UpdateResponse) command.invoke();
						
			// start update
			if ( kr.go.kofiu.ctr.util.UpdateResponse.UPDATE == response.getStatus() ) {
				Scheduler scheduler = null;
				if ( context != null )
					scheduler = context.getScheduler();
				
				updateModule(scheduler, response);
			} else if (kr.go.kofiu.ctr.util.UpdateResponse.FAIL == response.getStatus() ){
				logger.info("STR ���� ��� ������Ʈ�� �����Ͽ����ϴ�. ������ �õ��Ͽ� �ֽʽÿ�.");
			} else {
				logger.info("���� ��ġ �Ǿ��ִ� STR ���� ����� �ֽ� �����Դϴ�.");
			} 
		} catch (AgentException e ) {
			STRAgent.getInstance().shutdown();
			// controller setting �� ���� 
			//e.addAction(new EmailAction("STR ������ ������Ʈ ���", e));
			//e.addAction(new ShutdownAction());
			//throw e;
		} 
	} // end of method
	
	/**
	 * 
	 * @param scheduler
	 * @param response
	 * @throws AgentException
	 */
	private void updateModule( Scheduler scheduler , UpdateResponse response  ) throws AgentException {
		// pause other trigger
		try {
			if ( scheduler != null ) {
				scheduler.pauseAll();
				logger.info("��� ������Ʈ�� ���� ����  ���μ����� �Ͻ������� �����Ͽ����ϴ�.");
			}
		
			logger.debug(response);
			String nowVer = "";
			String moduleVersionPath = STRConfigure.getInstance().getAgentInfo().getControlFile();
			BufferedReader br = new BufferedReader(new FileReader(moduleVersionPath));
			nowVer = br.readLine();
			br.close();
			String newVer = response.getNewModuleVersion();
			if(nowVer.equals(newVer)){
				logger.info("��ġ�� ��� ����("+nowVer+")�� �ֽ� ������("+newVer+")�� ��ġ�մϴ�. ������Ʈ�� �����մϴ�.");
				return ;
			}
			logger.info("STR ���� ��� ���� ����("+nowVer+")�� �� ����("+newVer+")���� ������Ʈ �մϴ�. ");
			
			//Map reponseMap = response.getFiles();
			/*ItemAnonType[] responseItem = response.getFiles();
			if ( responseItem == null ) {
				logger.warn("������Ʈ �������� ���� ���� ����Ÿ�� �����ϴ�.");
				return ;
			}
			*/
			Map files = new HashMap();
			
			// LIB ���� ������Ʈ
			if (response.getMdlLib()  != null ) {
				byte[] agent_module = response.getMdlLib();
				// use jar lib
				logger.info("LIB ���� ������Ʈ ��� ");
				Map temp = FileTool.unzip(agent_module, "./lib/");
				files.putAll(temp);
				logger.debug("LIB ���� ������Ʈ ��� : " + temp);
			}
			
			// Config.xml ���� ������Ʈ (�̻��)
			/*	
			if (response.getConfig()  != null ) {
				byte[] agent_config = response.getConfig();
				logger.info("���� ���� ������Ʈ ��� ");
				Map temp = FileTool.unzip(agent_config, "./str_config/");
				files.putAll(temp);
				logger.debug("���� ���� ������Ʈ ��� : " + temp);
				// config.xml, CGWebService.wsdl, log4j.xml
				// at restart up, set config value with current value
				// because of config-rules.xml
			}
			*/
	
			// ����Ű ������ ������Ʈ
			if (response.getPblKey()  != null ) {
				byte[] public_key = response.getPblKey() ;
				logger.info("��ȣȭ ����Ű ������Ʈ ");
				files.put(STRConfigure.getInstance().getAgentInfo().getFiuInfo().getEncryptionInfo().getKeyManageInfo().getCertificate(), public_key);
			}

			// file bakup and update.
			innerProcess(files);
			
			// module version update
			logger.info("#### STR ���� ��� ������ " + newVer + " ������ �����մϴ�.");
			Controller.getInstance().doControl(Controller.VERSION_SET, newVer, Controller.SERVICE_STR);
	
			logger.info("#### STR ���� ��� ������Ʈ�� ���������� �Ϸ� �Ǿ����ϴ�.");
			 
			logger.info("#### STR ���� ����� ������մϴ�.");
			Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE3, null, Controller.SERVICE_STR);
			STRAgent.getInstance().shutdown(true);
		} catch (Exception e) {
			// SchedulerException - fail to pauseAll trigger, ignore, next time do that
			// IOException - ��� unzip error
			AgentException ae = new AgentException("STR ���� ��� ������Ʈ ��� - " + e.getMessage(), e);
			throw ae;
		} finally {
			try { 
				if ( scheduler != null ) 
					scheduler.resumeAll();
			} catch (SchedulerException e1) {
				// fatal error, all trigger is not operated. so restart
				logger.fatal("Shutdown Agent... and restart.", e1);
				STRAgent.getInstance().shutdown(true);
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
//		 �� �� ��� start backup
		Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE1, null, Controller.SERVICE_STR);
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
		  String element = (String) iter.next();
		  File f = new File(element);
		  if ( f.exists() )
			  FileTool.copyFile(f, new File(element + ".bak"));
		}
//		 end backup 
//		 and start  overwrite original file with new file data
//		 if update fail , �� �� ���Ϸ� ���� 
		Controller.getInstance().doControl(Controller.BOOT_UPDATE_PHASE2, null, Controller.SERVICE_STR);

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
