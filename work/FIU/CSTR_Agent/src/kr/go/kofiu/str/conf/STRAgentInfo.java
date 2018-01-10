package kr.go.kofiu.str.conf;

import java.util.ArrayList;
import java.util.Collection;

import kr.go.kofiu.str.conf.JobInfo;
import kr.go.kofiu.ctr.conf.ThreadExecutorInfo;

/*******************************************************
 * <pre>
 * 업무   그룹명 : STR 시스템
 * 서브   업무명 : 보고 기관 Agent
 * 설        명 : Agent의 정보를 담고 있는 value object.
 *    apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.
 * 작   성   자  : 선만주
 * 작   성   일  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class STRAgentInfo {
	public boolean isCsvPerRptDate() {
		return csvPerRptDate;
	}

	public void setCsvPerRptDate(boolean csvPerRptDate) {
		this.csvPerRptDate = csvPerRptDate;
	}

	/**
	 * 생성자
	 */
	public STRAgentInfo() 
	{
		schedule = new ArrayList();
		//diskFullMessage = new ArrayList();
	}
	
	/**
	 * 보고기관 Agent Id 값
	 */
	private String id = "";

	/**
	 * 보고 기관 등록시 사용한 IP 값
	 */
	private String ip = "";
	
	/**
	 * 보고 기관 등록시 사용한 IP 값
	 */
	private boolean csvPerRptDate;

	/**
	 * Archive 폴더 세팅
	 */
	private String archivefolderType = "type0";
	
	/**
	 * Inbox 폴더 세팅
	 */
	private String inboxfolderType = "type0";
	
	/**
	 * 설정 파일을 자동으로 읽을 지 여부
	 */
	private boolean autoReload = false;

	/**
	 * 설정 파일을 자동으로 읽을 지 여부
	 */
	private boolean archiveEnabled;

// 스케쥴러 관련 추가건 시작
	/**
	 * 멀티 스레딩 정보
	 */
	private ThreadExecutorInfo threadExecutorInfo;
	
	/**
	 * 스케줄링 JobInfo를 담아둘 Collection 객체
	 */
	private Collection schedule = null;

// 스케쥴러 관련 추가건 끝
	
	/**
	 * Remote Adapter의 버전 정보를 담은 control File명
	 */
	private String controlFile = "";

	/**
	 * 보고 기관 등록 정보들. 하나의 Remote Adapter가 복수 개의 기관을 처리할 수 있다 (단위 농협이나 한국증권전산의 전산
	 * 시스템을 위탁 사용 중인 중소 증권사들).
	 */
	private Collection<ReportOrgInfo> reportOrgInfos = new ArrayList<ReportOrgInfo>();

	/**
	 * 금융정보분석원 관련 정보
	 */
	private FiuInfo fiuInfo = null;
	
	/**
	 * JMS 접속 정보 
	 */
	private STRJMSInfo strJMSInfo;
	
	

	/**
	 * 문서 저장 폴더 정보 
	 */
	private FilePathInfo filePathInfo;
	
	/**
	 * JobInfo
	 */

	private JobInfo jobInfo;
	
	

	/**
	 * 보고 기관 정보들 전체를 한꺼번에 얻어온다.
	 * 
	 * @return 보고 기관 정보들
	 */
	public Collection<ReportOrgInfo> getReportOrgInfos() {
		return this.reportOrgInfos;
	}

	/**
	 * 보고 기관 정보들 전체를 한꺼번에 바꾼다.
	 * 
	 * @param reportOrgInfos
	 *            새로이 설정할 보고 기관 정보들
	 */
	public void setReportOrgs(Collection<ReportOrgInfo> reportOrgInfos) {
		this.reportOrgInfos = reportOrgInfos;
	}

	/**
	 * 보고 기관 설정 정보 하나를 추가한다.
	 * 
	 * @param reportOrgInfo
	 *            새로이 추가할 보고 기관 정보
	 */
	public void addReportOrgInfo(ReportOrgInfo reportOrgInfo) {
		this.reportOrgInfos.add(reportOrgInfo);
	}

	/**
	 * 보고 기관 설정 정보 하나를 얻어온다.
	 * 
	 * @param orgCd
	 *            보고 기관 코드
	 * @return
	 */
	public ReportOrgInfo getReportOrgInfo(String orgCd) throws STRConfigureException {
		String tempOrgCd = null;
		ReportOrgInfo returnVal = null;

		for (ReportOrgInfo roi : this.getReportOrgInfos()) {
			tempOrgCd = roi.getOrgCd();

			if (null != tempOrgCd && tempOrgCd.equals(orgCd)) {
				returnVal = roi;
				break;
			}
		}

		if (null == returnVal) {
			throw new STRConfigureException("기관코드 [" + orgCd
					+ "]이(가) config.xml에 없습니다. 관련 기관 정보를 config.xml에 설정했는지 확인하십시오.");
		}

		return returnVal;
	}

	/**
	 * 보고 모듈 설치 기관 IP 값 get
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 보고 모듈 설치 기관 IP 값 set
	 * 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 
	 * @param agentInfo
	 */
	public void accept(STRAgentInfo agentInfo) {
		setIp(agentInfo.getIp());
		// encryptionInfo.accept(agentInfo.getEncryptionInfo());
	}

	public String toString() {
		String xmlText = "<?xml version=\"1.0\" encoding=\"euc-kr\"?>\n";
		xmlText += "<agent id=\"" + this.getId() + "\" ip=\"" + getIp()
				+ "\" autoReload=\"" + this.isAutoReload()
				+ "\" controlFile=\"" + this.getControlFile() + "\">\n";

		for (ReportOrgInfo roi : this.reportOrgInfos) {
			xmlText += roi.toString();
		}

		if (null != this.fiuInfo) {
			xmlText += this.fiuInfo.toString();
		}

		xmlText += "</agent>";

		return xmlText;
	}

	public String getControlFile() {
		return this.controlFile;
	}

	public void setControlFile(String controlFile) {
		this.controlFile = controlFile;
	}

	public boolean isAutoReload() {
		return this.autoReload;
	}

	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}

	public FiuInfo getFiuInfo() {
		return fiuInfo;
	}

	public void setFiuInfo(FiuInfo fiuInfo) {
		this.fiuInfo = fiuInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	public STRJMSInfo getStrJMSInfo() {
		return strJMSInfo;
	}

	public void setSTRJMSInfo(STRJMSInfo strJMSInfo) {
		this.strJMSInfo = strJMSInfo;
	}



	public FilePathInfo getFilePathInfo() {
		return filePathInfo;
	}

	public void setFilePathInfo(FilePathInfo filePathInfo) {
		this.filePathInfo = filePathInfo;
	}
	
	public JobInfo getJobInfo() {
		return jobInfo;
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
	 * JobInfo가 담긴 Collection 객체 get
	 * @return JobInfo가 담긴 Collection 객체
	 */
	public Collection getSchedule() 
	{
		return schedule;
	}

	
	public ThreadExecutorInfo getThreadExecutorInfo() {
		return threadExecutorInfo;
	}

	public void setThreadExecutorInfo(ThreadExecutorInfo threadExecutorInfo) {
		this.threadExecutorInfo = threadExecutorInfo;
	}

	public boolean isArchiveEnabled() {
		return archiveEnabled;
	}

	public void setArchiveEnabled(boolean archiveEnabled) {
		this.archiveEnabled = archiveEnabled;
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
