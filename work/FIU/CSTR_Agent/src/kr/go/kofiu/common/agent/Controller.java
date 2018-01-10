package kr.go.kofiu.common.agent;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent의 Control 파일을 조작한다. boot.ctl, version.ctl 파일
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Controller {
	
	public static final String SERVICE_STR = "STR";
	public static final String SERVICE_CTR = "CTR";
	
	public static final String BOOT_STR_FILENAME = "boot_str.ctl";
	public static final String BOOT_CTR_FILENAME = "boot_ctr.ctl";
	public static final String BOOT_PATH = "ctl";
	
	
	private final String VERSION_STR_FILENAME = "version_str.ctl";
	private final String VERSION_CTR_FILENAME = "version_ctr.ctl";
//	public static final String VERSION_FILENAME = "version.ctl";
	public static final String VERSION_PATH = "ctl";
	
	public static final String BOOT_GET = "bootget";
	public static final String BOOT_STARTUP = "startup";
	public static final String BOOT_SHUTDOWN = "shutdown";
	public static final String BOOT_RESTART = "restart";
	public static final String BOOT_UPDATE_PHASE1 = "update_phase1";
	public static final String BOOT_UPDATE_PHASE2 = "update_phase2";
	public static final String BOOT_UPDATE_PHASE3 = "update_phase3";
	public static final String VERSION_SET = "versionset";
	public static final String VERSION_GET = "versionget";
	
	public static final String LOG_PATH = "logs";
	
	/**
	 * HEARTBEAT
	 */
	public static final String HEARTBEAT = "HEARTBEAT";
	
	/**
	 * VERSIONCHECK
	 */
	public static final String CHECKAGENT = "CHECKAGENT";
	
	/**
	 * MODULEUPDATE (CTR)
	 */
	public static final String CTRMODULEUPDATE = "CTRMODULEUPDATE";
	
	/**
	 * MODULEUPDATE (STR)
	 */
	public static final String STRMODULEUPDATE = "STRMODULEUPDATE";
	
	/**
	 * USERCHECK
	 */
	public static final String USERCHECK = "USERCHECK";
	
	/**
	 * instance
	 */
	private static Controller instance;

	/**
	 * 생성자
	 *
	 **/
	public Controller() {
		instance = this;
	}

	/**
	 * doControl(cmd, null)을 호출한다.
	 * @param cmd
	 * @return
	 * @throws AgentException
	 */
	public String doControl(String cmd, String service) throws AgentException {
		return doControl(cmd, null, service);
	}

	/**
	 * Control 정보를 cmd값에 따라서 조회/수정한다. 
	 * @param cmd  BOOT_GET, BOOT_STARTUP, BOOT_SHUTDOWN, BOOT_RESTART, VERSION_GET, VERSION_SET
	 * @param arg VERSION_SET일 경우에 사용되면 이외에는 null 값을 주면 된다. 
	 * @return
	 * @throws AgentException
	 */
	/*public String doControl(String cmd, String arg) throws AgentException 
	{
		String value = null;
		try {
		
			if( cmd.equals(VERSION_GET)) {
				value = FileTool.getFileString(VERSION_PATH + File.separator + VERSION_CTR_FILENAME);
				value = value.trim();
			} else if( cmd.equals(Controller.VERSION_SET)) {
				FileTool.writeToFile(VERSION_PATH + File.separator + VERSION_FILENAME, arg );
				value = arg;
			} else {
				throw new AgentException(cmd + " : Controller invalid command !");
			}
		} catch (IOException e) {
			throw new AgentException("Controller File I/O Failed!", e);
		}
		
		return value;
		
	}*/
	
	public String doControl(String cmd, String arg, String service) throws AgentException 
	{
		String value = null;
		try {
		
			if(	cmd.equals(BOOT_GET)) {
				if(service.equals("STR")){
					value = FileTool.getFileString(BOOT_PATH + File.separator + BOOT_STR_FILENAME);
				}else if(service.equals("CTR")){
					value = FileTool.getFileString(BOOT_PATH + File.separator + BOOT_CTR_FILENAME);
				}
				
			}
			else if( cmd.equals(BOOT_STARTUP)
					|| cmd.equals(BOOT_SHUTDOWN)
					|| cmd.equals(BOOT_RESTART) 
					|| cmd.equals(BOOT_UPDATE_PHASE1) 
					|| cmd.equals(BOOT_UPDATE_PHASE2) 
					|| cmd.equals(BOOT_UPDATE_PHASE3) ) 
			{
				if(service.equals("STR")){
					FileTool.writeToFile(BOOT_PATH + File.separator + BOOT_STR_FILENAME, cmd);
				}else if(service.equals("CTR")){
					FileTool.writeToFile(BOOT_PATH + File.separator + BOOT_CTR_FILENAME, cmd);
				}
				
				
				value = cmd;
			}
			else if( cmd.equals(VERSION_GET)) {
				if(service.equals("CTR")){
					value = FileTool.getFileString(VERSION_PATH + File.separator + VERSION_CTR_FILENAME);
				}else if(service.equals("STR")){
					value = FileTool.getFileString(VERSION_PATH + File.separator + VERSION_STR_FILENAME);
				}
				value = value.trim();
			} else if( cmd.equals(Controller.VERSION_SET)) {
				if(service.equals("CTR")){
					FileTool.writeToFile(VERSION_PATH + File.separator + VERSION_CTR_FILENAME, arg );
				}else if(service.equals("STR")){
					FileTool.writeToFile(VERSION_PATH + File.separator + VERSION_STR_FILENAME, arg );
				}
				value = arg;
			} else {
				throw new AgentException(cmd + " : Controller invalid command !");
			}
		} catch (IOException e) {
			throw new AgentException("Controller File I/O Failed!", e);
		}
		
		return value;
		
	}

	/**
	 * 싱글 톤 Class
	 * @return
	 */
	public static Controller getInstance()
	{
		return instance;
	}
	
	/**
	 * Controller main 메소드 
	 * @param args
	 * @throws AgentException
	 */
	public static void main(String[] args) throws AgentException 
	{
		if(args.length < 2 || args.length > 3 ) 
		{
			System.out.println("Usage : java kr.go.kofiu.ctr.agent.Controller [control command] [command option]");
			return;
		}
		
		String command = args[0];
		String service = args[1];
		String opt = null;
		if(args.length > 1)
		{ 
			opt = args[1];
		}
			
		Controller control = new Controller();
		
		System.out.println(control.doControl(command,null,service));
	}
	
}
