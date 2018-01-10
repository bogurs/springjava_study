package kr.go.kofiu.str.conf;

import java.util.ArrayList;
import java.util.Collection;

import kr.go.kofiu.str.conf.JobInfo;
import kr.go.kofiu.ctr.conf.ThreadExecutorInfo;

/*******************************************************
 * <pre>
 * ����   �׷�� : STR �ý���
 * ����   ������ : ���� ��� Agent
 * ��        �� : Agent�� ������ ��� �ִ� value object.
 *    apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.
 * ��   ��   ��  : ������
 * ��   ��   ��  : 2008.09.01
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
	 * ������
	 */
	public STRAgentInfo() 
	{
		schedule = new ArrayList();
		//diskFullMessage = new ArrayList();
	}
	
	/**
	 * ������ Agent Id ��
	 */
	private String id = "";

	/**
	 * ���� ��� ��Ͻ� ����� IP ��
	 */
	private String ip = "";
	
	/**
	 * ���� ��� ��Ͻ� ����� IP ��
	 */
	private boolean csvPerRptDate;

	/**
	 * Archive ���� ����
	 */
	private String archivefolderType = "type0";
	
	/**
	 * Inbox ���� ����
	 */
	private String inboxfolderType = "type0";
	
	/**
	 * ���� ������ �ڵ����� ���� �� ����
	 */
	private boolean autoReload = false;

	/**
	 * ���� ������ �ڵ����� ���� �� ����
	 */
	private boolean archiveEnabled;

// �����췯 ���� �߰��� ����
	/**
	 * ��Ƽ ������ ����
	 */
	private ThreadExecutorInfo threadExecutorInfo;
	
	/**
	 * �����ٸ� JobInfo�� ��Ƶ� Collection ��ü
	 */
	private Collection schedule = null;

// �����췯 ���� �߰��� ��
	
	/**
	 * Remote Adapter�� ���� ������ ���� control File��
	 */
	private String controlFile = "";

	/**
	 * ���� ��� ��� ������. �ϳ��� Remote Adapter�� ���� ���� ����� ó���� �� �ִ� (���� �����̳� �ѱ����������� ����
	 * �ý����� ��Ź ��� ���� �߼� ���ǻ��).
	 */
	private Collection<ReportOrgInfo> reportOrgInfos = new ArrayList<ReportOrgInfo>();

	/**
	 * ���������м��� ���� ����
	 */
	private FiuInfo fiuInfo = null;
	
	/**
	 * JMS ���� ���� 
	 */
	private STRJMSInfo strJMSInfo;
	
	

	/**
	 * ���� ���� ���� ���� 
	 */
	private FilePathInfo filePathInfo;
	
	/**
	 * JobInfo
	 */

	private JobInfo jobInfo;
	
	

	/**
	 * ���� ��� ������ ��ü�� �Ѳ����� ���´�.
	 * 
	 * @return ���� ��� ������
	 */
	public Collection<ReportOrgInfo> getReportOrgInfos() {
		return this.reportOrgInfos;
	}

	/**
	 * ���� ��� ������ ��ü�� �Ѳ����� �ٲ۴�.
	 * 
	 * @param reportOrgInfos
	 *            ������ ������ ���� ��� ������
	 */
	public void setReportOrgs(Collection<ReportOrgInfo> reportOrgInfos) {
		this.reportOrgInfos = reportOrgInfos;
	}

	/**
	 * ���� ��� ���� ���� �ϳ��� �߰��Ѵ�.
	 * 
	 * @param reportOrgInfo
	 *            ������ �߰��� ���� ��� ����
	 */
	public void addReportOrgInfo(ReportOrgInfo reportOrgInfo) {
		this.reportOrgInfos.add(reportOrgInfo);
	}

	/**
	 * ���� ��� ���� ���� �ϳ��� ���´�.
	 * 
	 * @param orgCd
	 *            ���� ��� �ڵ�
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
			throw new STRConfigureException("����ڵ� [" + orgCd
					+ "]��(��) config.xml�� �����ϴ�. ���� ��� ������ config.xml�� �����ߴ��� Ȯ���Ͻʽÿ�.");
		}

		return returnVal;
	}

	/**
	 * ���� ��� ��ġ ��� IP �� get
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * ���� ��� ��ġ ��� IP �� set
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
	 * JobInfo �߰�
	 * @param info JobInfo 
	 */
	public void addJobInfo(JobInfo info)
	{
		schedule.add(info);
	}
	
	/**
	 * JobInfo�� ��� Collection ��ü get
	 * @return JobInfo�� ��� Collection ��ü
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
