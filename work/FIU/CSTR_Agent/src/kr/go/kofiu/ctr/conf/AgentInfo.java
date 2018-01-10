package kr.go.kofiu.ctr.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * 업무   그룹명 : CTR 시스템
 * 서브   업무명 : 보고 기관 Agent
 * 설        명 : Agent의 정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentInfo 
{
	//2014 CtrAgentEnvInfo 추가
    private CtrAgentEnvInfo ctrAgentEnvInfo;
    
    public CtrAgentEnvInfo getCtrAgentEnvInfo() {
        return ctrAgentEnvInfo;
    }
    public void setCtrAgentEnvInfo(CtrAgentEnvInfo ctrAgentEnvInfo) {
        this.ctrAgentEnvInfo = ctrAgentEnvInfo;
    }
    
    //JMS 환경설정 Info 추가
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
	 * INBOX 저장 여부 
	 */
	private boolean InboxEnabled = true;
	
	/**
	 * ARCHIVE 저장 여부 
	 */
	private boolean ArchiveEnabled = true;
	
	/**
	 * Inbox에 데이타 저장을 날짜별로
	 */
	private boolean SaveInboxPerDate = false;	

	/**
	 * Agent Self Test 모드 여부  
	 */
	private boolean test = false;
	
	/**
	 * CSV파일에 이력 저장 시 보고 일자별 파일로 저장할지 여부
	 */
	private boolean csvPerRptDate = false;
	
	/**
	 * 보고 기관 등록시 사용한 IP 값 
	 */
	private String ip = "";
	
	/**
	 * Archive 폴더 세팅
	 */
	private String archivefolderType = "type0";
	
	/**
	 * Inbox 폴더 세팅
	 */
	private String inboxfolderType = "type0";
	
	/**
	 * 연계 모듈 설치 시 NAT를 통해 중계 기관 IP를 변형하는 곳에서는 
	 * 웹서비스를 호출하기 위해서 로컬 wsdl 파일을 사용하여야한다.
	 * 이 때 로컬에 저장된 wsdl 파일에서 address 정보를 내부에서 보는 ip값으로 
	 * 수정하여 준 후 사용한다.
	 */
	private boolean useLocalWSDL = false;
	
	/**
	 * 메시지 폴더를 기관별로 관리 할것인지 아니면 통째로 하나로 관리할것인 여부 
	 */
	private MessageBoxEnv messageBoxEnv;

	/**
	 * Mail 정보 
	 */
	private MailInfo mailInfo = null;

	/**
	 * 멀티 스레딩 정보
	 */
	private ThreadExecutorInfo threadExecutorInfo;

	/**
	 * 집중 기관 서버와 관련된 정보를 담고 있는 객체 
	 */
	private CgServer cgServer = null;
	
	/**
	 * 문서 서명 및 암호화 처리 여부 
	 */
	private EncryptionInfo encryptionInfo;

	/**
	 * 디스크 풀 발생시 메세지 목록. OS별로 다르다.
	 */
	private Collection diskFullMessage;
	
	/**
	 * 스케줄링 JobInfo를 담아둘 Collection 객체
	 */
	private Collection schedule = null;
	
	/**
	 * 스케줄링 JobInfo 정보를 담아 둘 Collection 객체를 초기화한다.
	 * 여기서는 그 중 java.util.ArrayList를 사용하였다. 
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
	 * 멀티 스레드 정보 get
	 * @return
	 */
	public ThreadExecutorInfo getThreadExecutorInfo() {
		return threadExecutorInfo;
	}

	/**
	 * 멀티 스레드 정보 set
	 * @param threadExecutorInfo
	 */
	public void setThreadExecutorInfo(ThreadExecutorInfo threadExecutorInfo) {
		this.threadExecutorInfo = threadExecutorInfo;
	}

	/**
	 * 집중 기관 접속 정보 get
	 * @return 집중 기관 접속 정보. 웹서비스 WSDL URL 정보를 담고 있다.  
	 */
	public CgServer getCgServer()
	{
		return cgServer;
	}

	/**
	 * 집중 기관 접속 정보 set
	 * @param cgServer 집중 기관 접속 정보.
	 */
	public void setCgServer(CgServer cgServer)
	{
		this.cgServer = cgServer;
	}

	/**
	 * JobInfo가 담긴 Collection 객체 get
	 * @return JobInfo가 담긴 Collection 객체
	 */
	public Collection getSchedule() 
	{
		return schedule;
	}

	/**
	 * JobInfo 추가
	 * @param info JobInfo 
	 */
	public void addJobInfo(JobInfo info)
	{
		schedule.add(info);
	}
	
	
	/**
	 * DiskFullMessage 담긴 Collection 객체 get
	 * @return DiskFullMessage 담긴 Collection 객체
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
	 * 암호화 및 복호화. 전자 서명을 위한 인증서 정보를 담고 있는 객체 get
	 * @return EncryptionInfo
	 */
	public EncryptionInfo getEncryptionInfo() {
		return encryptionInfo;
	}

	/**
	 * 암호화 및 복호화. 전자 서명을 위한 인증서 정보 set
	 * @param encryptionInfo EncryptionInfo
	 */
	public void setEncryptionInfo(EncryptionInfo encryptionInfo) {
		this.encryptionInfo = encryptionInfo;
	}
	
	/**
	 * 메일 정보 get
	 * @return  MailInfo
	 */
	public MailInfo getMailInfo() {
		return mailInfo;
	}

	/**
	 * 메일 정보 set
	 * @param mailInfo MailInfo
	 */
	public void setMailInfo(MailInfo mailInfo) {
		this.mailInfo = mailInfo;
	}

	/**
	 * 4자리의 Agent ID 값 get
	 * @return Agent ID 값 
	 */
	public String getId() {
		return id;
	}

	/**
	 *  4자리의 Agent ID 값 set
	 * @param id Agent ID 값 
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Self 테스트 여부 값 get
	 * @return
	 */
	public boolean isTest() {
		return test;
	}

	/**
	 *  Self 테스트 여부 값 set
	 * @param test
	 */
	public void setTest(boolean test) {
		this.test = test;
	}

	/**
	 * Inbox에 발송 증서/접수 증서 저장 여부 값 get
	 * @return
	 */
	public boolean isInboxEnabled() {
		return InboxEnabled;
	}

	/**
	 * Inbox에 발송 증서/접수 증서 저장 여부 값 set
	 * @param inboxEnabled
	 */
	public void setInboxEnabled(boolean inboxEnabled) {
		InboxEnabled = inboxEnabled;
	}

	/**
	 * Archive에 보고문서 백업 여부 값 get
	 * @return
	 */
	public boolean isArchiveEnabled() {
		return ArchiveEnabled;
	}
	
	/**
	 * Archive에 보고문서 백업 여부 값 set
	 * @param archiveEnabled
	 */
	public void setArchiveEnabled(boolean archiveEnabled) {
		ArchiveEnabled = archiveEnabled;
	}
	
	/**
	 * 보고 모듈 설치 기관 IP 값 get
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 보고 모듈 설치 기관 IP 값 set
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
