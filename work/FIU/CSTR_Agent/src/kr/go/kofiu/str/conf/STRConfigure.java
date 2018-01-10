package kr.go.kofiu.str.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * @author �����
 */
public class STRConfigure {
	private static Logger logger = Logger.getLogger(STRConfigure.class);
	private long lastModified = 0l;
	private File configFile = null;

	private STRAgentInfo agentInfo = null;
	private volatile static STRConfigure instance = null;
	
	
	/**
	 * �� Ŭ������ singleton Ŭ�����̴�. self instance. 
	 */
	private static STRConfigure _instance;
	
	static 
	{
		try 
		{
			_instance = new STRConfigure();
		} 
		catch (IOException e) {
			//ignore
			logger.error("Error while Parsinng config.xml file!",e);
		} 
		catch (SAXException e) {
			logger.error("Error while Parsinng config.xml file!",e);
		}
	}
	

	private STRConfigure() throws IOException, SAXException {
		this.configFile = new File(Constants.RA_CONFIG_PATH);
		this.refresh();
	}
	
	/**
	 * 
	 * @return
	 */
	public static STRConfigure getInstance()
	{
		return _instance;
	}

	/**
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	public final void refresh() throws IOException, SAXException {
		Digester digester = DigesterLoader.createDigester(this.getClass()
				.getResource(Constants.RA_CONFIG_RULE_PATH));

		
			logger.info("configuration file: " + Constants.RA_CONFIG_PATH);

		//this.agentInfo = (AgentInfo) digester.parse(new FileInputStream(configFile));
		this.agentInfo = (STRAgentInfo) digester.parse(new FileInputStream("./str_config/config.xml")); // test
		this.lastModified = configFile.lastModified();
	}

	/**
	 * ���� ���� ���� <agent> element ���� ������ ���� ��ü�� ���´�.
	 * 
	 * @return ���� ���� ���� <agent> element ���� ����
	 */
	public final STRAgentInfo getAgentInfo() {
		return this.agentInfo;
	}

	public long getLastModified() {
		return lastModified;
	}

	public File getConfigFile() {
		return configFile;
	}

	public static void main(String... args) throws Exception {
		System.setProperty("adapter.home", ".");
		System.out.println(STRConfigure.getInstance().getAgentInfo().toString());
	}
}
