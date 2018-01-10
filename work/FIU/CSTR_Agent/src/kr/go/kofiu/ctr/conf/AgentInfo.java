package kr.go.kofiu.ctr.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * ����   �׷�� : CTR �ý���
 * ����   ������ : ���� ��� Agent
 * ��        �� : Agent�� ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentInfo 
{
	//2014 CtrAgentEnvInfo �߰�
    private CtrAgentEnvInfo ctrAgentEnvInfo;
    
    public CtrAgentEnvInfo getCtrAgentEnvInfo() {
        return ctrAgentEnvInfo;
    }
    public void setCtrAgentEnvInfo(CtrAgentEnvInfo ctrAgentEnvInfo) {
        this.ctrAgentEnvInfo = ctrAgentEnvInfo;
    }
    
    //JMS ȯ�漳�� Info �߰�
    private JmsSoapInfo jmsSoapInfo;
	
	public JmsSoapInfo getJmsSoapInfo() {
		return jmsSoapInfo;
	}
	public void setJmsSoapInfo(JmsSoapInfo jmsSoapInfo) {
		this.jmsSoapInfo = jmsSoapInfo;
	}

	/**
	 * Agent ID 
	 */
	private String id = null;
	
	/**
	 * INBOX ���� ���� 
	 */
	private boolean InboxEnabled = true;
	
	/**
	 * ARCHIVE ���� ���� 
	 */
	private boolean ArchiveEnabled = true;
	
	/**
	 * Inbox�� ����Ÿ ������ ��¥����
	 */
	private boolean SaveInboxPerDate = false;	

	/**
	 * Agent Self Test ��� ����  
	 */
	private boolean test = false;
	
	/**
	 * CSV���Ͽ� �̷� ���� �� ���� ���ں� ���Ϸ� �������� ����
	 */
	private boolean csvPerRptDate = false;
	
	/**
	 * ���� ��� ��Ͻ� ����� IP �� 
	 */
	private String ip = "";
	
	/**
	 * Archive ���� ����
	 */
	private String archivefolderType = "type0";
	
	/**
	 * Inbox ���� ����
	 */
	private String inboxfolderType = "type0";
	
	/**
	 * ���� ��� ��ġ �� NAT�� ���� �߰� ��� IP�� �����ϴ� �������� 
	 * �����񽺸� ȣ���ϱ� ���ؼ� ���� wsdl ������ ����Ͽ����Ѵ�.
	 * �� �� ���ÿ� ����� wsdl ���Ͽ��� address ������ ���ο��� ���� ip������ 
	 * �����Ͽ� �� �� ����Ѵ�.
	 */
	private boolean useLocalWSDL = false;
	
	/**
	 * �޽��� ������ ������� ���� �Ұ����� �ƴϸ� ��°�� �ϳ��� �����Ұ��� ���� 
	 */
	private MessageBoxEnv messageBoxEnv;

	/**
	 * Mail ���� 
	 */
	private MailInfo mailInfo = null;

	/**
	 * ��Ƽ ������ ����
	 */
	private ThreadExecutorInfo threadExecutorInfo;

	/**
	 * ���� ��� ������ ���õ� ������ ��� �ִ� ��ü 
	 */
	private CgServer cgServer = null;
	
	/**
	 * ���� ���� �� ��ȣȭ ó�� ���� 
	 */
	private EncryptionInfo encryptionInfo;

	/**
	 * ��ũ Ǯ �߻��� �޼��� ���. OS���� �ٸ���.
	 */
	private Collection diskFullMessage;
	
	/**
	 * �����ٸ� JobInfo�� ��Ƶ� Collection ��ü
	 */
	private Collection schedule = null;
	
	/**
	 * �����ٸ� JobInfo ������ ��� �� Collection ��ü�� �ʱ�ȭ�Ѵ�.
	 * ���⼭�� �� �� java.util.ArrayList�� ����Ͽ���. 
	 *
	 */
	public AgentInfo() 
	{
		schedule = new ArrayList();
		diskFullMessage = new ArrayList();
	}

	/**
	 * 
	 * @param messageBoxEnv
	 */
	public void setMessageBoxEnv(MessageBoxEnv messageBoxEnv) {
		this.messageBoxEnv = messageBoxEnv;
	}
	
	/**
	 * 
	 * @return
	 */
	public MessageBoxEnv getMessageBoxEnv() {
		return this.messageBoxEnv;
	}
	
	/**
	 * ��Ƽ ������ ���� get
	 * @return
	 */
	public ThreadExecutorInfo getThreadExecutorInfo() {
		return threadExecutorInfo;
	}

	/**
	 * ��Ƽ ������ ���� set
	 * @param threadExecutorInfo
	 */
	public void setThreadExecutorInfo(ThreadExecutorInfo threadExecutorInfo) {
		this.threadExecutorInfo = threadExecutorInfo;
	}

	/**
	 * ���� ��� ���� ���� get
	 * @return ���� ��� ���� ����. ������ WSDL URL ������ ��� �ִ�.  
	 */
	public CgServer getCgServer()
	{
		return cgServer;
	}

	/**
	 * ���� ��� ���� ���� set
	 * @param cgServer ���� ��� ���� ����.
	 */
	public void setCgServer(CgServer cgServer)
	{
		this.cgServer = cgServer;
	}

	/**
	 * JobInfo�� ��� Collection ��ü get
	 * @return JobInfo�� ��� Collection ��ü
	 */
	public Collection getSchedule() 
	{
		return schedule;
	}

	/**
	 * JobInfo �߰�
	 * @param info JobInfo 
	 */
	public void addJobInfo(JobInfo info)
	{
		schedule.add(info);
	}
	
	
	/**
	 * DiskFullMessage ��� Collection ��ü get
	 * @return DiskFullMessage ��� Collection ��ü
	 */
	public Collection getDiskFullMessages() 
	{
		return diskFullMessage;
	}

	/**
	 * 
	 * @param info
	 */
	public void addDiskFullMessageInfo(DiskFullMessageInfo info) {
		diskFullMessage.add(info);
	}


	/**
	 * ��ȣȭ �� ��ȣȭ. ���� ������ ���� ������ ������ ��� �ִ� ��ü get
	 * @return EncryptionInfo
	 */
	public EncryptionInfo getEncryptionInfo() {
		return encryptionInfo;
	}

	/**
	 * ��ȣȭ �� ��ȣȭ. ���� ������ ���� ������ ���� set
	 * @param encryptionInfo EncryptionInfo
	 */
	public void setEncryptionInfo(EncryptionInfo encryptionInfo) {
		this.encryptionInfo = encryptionInfo;
	}
	
	/**
	 * ���� ���� get
	 * @return  MailInfo
	 */
	public MailInfo getMailInfo() {
		return mailInfo;
	}

	/**
	 * ���� ���� set
	 * @param mailInfo MailInfo
	 */
	public void setMailInfo(MailInfo mailInfo) {
		this.mailInfo = mailInfo;
	}

	/**
	 * 4�ڸ��� Agent ID �� get
	 * @return Agent ID �� 
	 */
	public String getId() {
		return id;
	}

	/**
	 *  4�ڸ��� Agent ID �� set
	 * @param id Agent ID �� 
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Self �׽�Ʈ ���� �� get
	 * @return
	 */
	public boolean isTest() {
		return test;
	}

	/**
	 *  Self �׽�Ʈ ���� �� set
	 * @param test
	 */
	public void setTest(boolean test) {
		this.test = test;
	}

	/**
	 * Inbox�� �߼� ����/���� ���� ���� ���� �� get
	 * @return
	 */
	public boolean isInboxEnabled() {
		return InboxEnabled;
	}

	/**
	 * Inbox�� �߼� ����/���� ���� ���� ���� �� set
	 * @param inboxEnabled
	 */
	public void setInboxEnabled(boolean inboxEnabled) {
		InboxEnabled = inboxEnabled;
	}

	/**
	 * Archive�� ������ ��� ���� �� get
	 * @return
	 */
	public boolean isArchiveEnabled() {
		return ArchiveEnabled;
	}
	
	/**
	 * Archive�� ������ ��� ���� �� set
	 * @param archiveEnabled
	 */
	public void setArchiveEnabled(boolean archiveEnabled) {
		ArchiveEnabled = archiveEnabled;
	}
	
	/**
	 * ���� ��� ��ġ ��� IP �� get
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * ���� ��� ��ġ ��� IP �� set
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 
	 * @param agentInfo
	 */
	public void accept(AgentInfo agentInfo) {
		setId( agentInfo.getId() );
		setIp(agentInfo.getIp());
		setInboxEnabled(agentInfo.isInboxEnabled());
		setTest(agentInfo.isTest());
		setUseLocalWSDL(agentInfo.isUseLocalWSDL());
		
		messageBoxEnv = agentInfo.getMessageBoxEnv();
		
		mailInfo.accept( agentInfo.getMailInfo() );
		
		threadExecutorInfo.accept(agentInfo.getThreadExecutorInfo());

		cgServer.accept( agentInfo.getCgServer() );
		
		encryptionInfo.accept( agentInfo.getEncryptionInfo() );
		
		schedule = agentInfo.getSchedule(); 
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUseLocalWSDL() {
		return useLocalWSDL;
	}

	/**
	 * 
	 * @param useLocalWSDL
	 */
	public void setUseLocalWSDL(boolean useLocalWSDL) {
		this.useLocalWSDL = useLocalWSDL;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSaveInboxPerDate() {
		return SaveInboxPerDate;
	}

	/**
	 * 
	 * @param saveInboxPerDate
	 */
	public void setSaveInboxPerDate(boolean saveInboxPerDate) {
		SaveInboxPerDate = saveInboxPerDate;
	}	
	

	public String toString() {
		String xmlText = "<?xml version=\"1.0\" encoding=\"euc-kr\"?>\n";
		xmlText += "<agent id=\"" + getId() 
				+ "\" ip=\"" + getIp() 
				+ "\" inboxEnabled=\"" + Boolean.toString(isInboxEnabled()) 
				+ "\" saveInboxPerDate=\"" + Boolean.toString(isSaveInboxPerDate()) 
				+ "\" test=\"" + Boolean.toString(isTest())
				+ "\" csvPerRptDate=\"" + Boolean.toString(isCsvPerRptDate())
				+ "\" useLocalWSDL=\"" + Boolean.toString(isUseLocalWSDL()) + "\">\n";
		xmlText += messageBoxEnv.toString();
		xmlText += mailInfo.toString();
		xmlText += threadExecutorInfo.toString();
		xmlText += cgServer.toString();
		xmlText += encryptionInfo.toString();
		
		// schedule job
		xmlText += "\t<DiskFullMessages>\n";
		Iterator iter = diskFullMessage.iterator();
		while(iter.hasNext() ){
			DiskFullMessageInfo info = (DiskFullMessageInfo)iter.next();
			xmlText += info.toString();
		}
		xmlText += "\t</DiskFullMessages>\n";

		// schedule job
		xmlText += "\t<schedule>\n";
		Iterator iter2 = schedule.iterator();
		while(iter2.hasNext() ){
			JobInfo info = (JobInfo)iter2.next();
			xmlText += info.toString();
		}
		xmlText += "\t</schedule>\n";
		xmlText += "</agent>";
		return xmlText;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCsvPerRptDate() {
		return csvPerRptDate;
	}

	/**
	 * 
	 * @param csvPerRptDate
	 */
	public void setCsvPerRptDate(boolean csvPerRptDate) {
		this.csvPerRptDate = csvPerRptDate;
	}
	public String getArchivefolderType() {
		return archivefolderType;
	}
	public void setArchivefolderType(String archivefolderType) {
		this.archivefolderType = archivefolderType;
	}
	public String getInboxfolderType() {
		return inboxfolderType;
	}
	public void setInboxfolderType(String inboxfolderType) {
		this.inboxfolderType = inboxfolderType;
	}


}
