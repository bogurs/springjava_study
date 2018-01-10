package kr.go.kofiu.ctr.conf;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;


/*******************************************************
 * <pre>
 * 업무 그룹명 : CTR 시스템
 * 서브 업무명 : 보고 기관 Agent
 * 설          명 : Agent Configuraton 을 초기화 하고. 설정 값을 얻어오기 위한 통로.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자 : 최중호 
 * 작   성   일 : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Configure 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Configure.class);

	/**
	 * Agent 정보 표현 객체
	 */
	private AgentInfo agentInfo = null;
	
	/**
	 * 이 클래스는 singleton 클래스이다. self instance. 
	 */
	private static Configure _instance;
	
	static 
	{
		try 
		{
			_instance = new Configure();
		} 
		catch (IOException e) {
			//ignore
			logger.error("Error while Parsinng config.xml file!",e);
		} 
		catch (SAXException e) {
			logger.error("Error while Parsinng config.xml file!",e);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	private Configure() throws IOException, SAXException
	{
		URL rules = getClass().getResource("/kr/go/kofiu/ctr/conf/config-rules.xml");
		Digester digester = DigesterLoader.createDigester(rules);

		// Parse the XML document
		InputStream input = new FileInputStream(
					CtrAgentEnvInfo.CONFIG_DIR + File.separator + "config.xml" );
		agentInfo = (AgentInfo)digester.parse( input );		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static Configure getInstance()
	{
		return _instance;
	}
	
	/**
	 * 
	 * @return
	 */
	public AgentInfo getAgentInfo() 
	{
		return agentInfo;
	}

	/**
	 * 
	 * @param agentInfo
	 */
	public void setAgentInfo(AgentInfo agentInfo)
	{
		this.agentInfo = agentInfo;
	}

	/**
	 * Helper Methods
	 * @param args
	 */
	public CgServer getCgServer(){
		return this.agentInfo.getCgServer();
	}

	/**
	 * Helper Methods
	 * @param args
	 */
	public SigningInfo getEncryptionSigningInfo(){
		return this.agentInfo.getEncryptionInfo().getSigningInfo();
	}

	/**
	 * 
	 * @return
	 */
	public EncryptionInfo getEncryptionInfo(){
		return this.agentInfo.getEncryptionInfo();
	}
	
	public static void main(String args[])
	{
		//System.out.println(System.currentTimeMillis());
		Configure conf = Configure.getInstance();

		System.out.println("RepresentIP  : "  + conf.getAgentInfo().getIp());
		System.out.println("isSaveInboxPerDate  : "  + conf.getAgentInfo().isSaveInboxPerDate());
		System.out.println("FolderPerFcltyCode  : "  + conf.getAgentInfo().getMessageBoxEnv().isFolderPerFcltyCode());
		System.out.println("isCSVPerRptDate  : "  + conf.getAgentInfo().isCsvPerRptDate());
		Collection col = conf.getAgentInfo().getMessageBoxEnv().getFcltyCodes();
		Iterator iter = col.iterator();
		while(iter.hasNext())
		{
			String fclty_code = (String)iter.next();
			System.out.println("fclty_code : " + fclty_code);
		}
				
		System.out.println("id  : "  + conf.getAgentInfo().getId());
		System.out.println("isTest  : "  + conf.getAgentInfo().isTest());
		System.out.println("inboxenabled  : "  + conf.getAgentInfo().isInboxEnabled());
		System.out.println("MailInfo  : "  + conf.getAgentInfo().getMailInfo().isEnabled());
		System.out.println("MailInfo  : "  + conf.getAgentInfo().getMailInfo().getHost());
		System.out.println("MailInfo  : "  + conf.getAgentInfo().getMailInfo().getPassword());
		
		System.out.println("CG Server  : "  + conf.getCgServer().getIp());
		
	
		//conf.getAgentInfo().getSchedule().getClass();
		Collection col2 = conf.getAgentInfo().getDiskFullMessages();
		Iterator iter2 = col2.iterator();
		while(iter2.hasNext())
		{
			DiskFullMessageInfo info = (DiskFullMessageInfo)iter2.next();
			System.out.println(info.getMessage());
			System.out.println(info.getDescription());
		}
		
		//conf.agentConf();
		System.out.println("Encryption  : "  + conf.getAgentInfo().getEncryptionInfo().isEnabled());
		System.out.println("getPrivateCertInfo  : "  + conf.getAgentInfo().getEncryptionInfo().getPrivateCertInfo().getCertificate());
		System.out.println("isSigning  : "  + conf.getEncryptionSigningInfo().isEnabled());
		System.out.println("getCertificate  : "  + conf.getEncryptionSigningInfo().getCertificate());
		System.out.println("getKey  : "  + conf.getEncryptionSigningInfo().getKey());
		System.out.println("getPin  : "  + conf.getEncryptionSigningInfo().getPin());

	}


}
