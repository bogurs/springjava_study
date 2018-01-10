package kr.go.kofiu.str.conf;

public class ReportOrgInfo {
	/**
	 * ���� ��� �ڵ�
	 */
	private String orgCd = "";

	/**
	 * ���� ��� �̸�
	 */
	private String orgName = "";
	
	/**
	 * ��ǥ ��� �ڵ�
	 */
	private String repOrgCd = "";
	
	/**
	 * ARCHIVE/INBOX ������ �ڵ� ��������
	 */
	private boolean folderPerFcltyCode ;

	
	/**
	 * STR, ÷�� ���� ����ȭ ������ ������ ���� ���ϸ�
	 */
	private ValidationInfo validationInfo = null;

	/**
	 * ���� ����� Ű ������ ������ ����
	 */
	private EncryptionInfo encryptionInfo = null;

	public ValidationInfo getValidationInfo() {
		return validationInfo;
	}

	public void setValidationInfo(ValidationInfo validationInfo) {
		this.validationInfo = validationInfo;
	}

	public EncryptionInfo getEncryptionInfo() {
		return encryptionInfo;
	}

	public void setEncryptionInfo(EncryptionInfo encryptionInfo) {
		this.encryptionInfo = encryptionInfo;
	}

	public String getOrgCd() {
		return this.orgCd;
	}

	public void setOrgCd(String orgCd) {
		this.orgCd = orgCd;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getRepOrgCd() {
		return repOrgCd;
	}

	public void setRepOrgCd(String repOrgCd) {
		this.repOrgCd = repOrgCd;
	}

	
	public boolean isFolderPerFcltyCode() {
		return folderPerFcltyCode;
	}

	public void setFolderPerFcltyCode(boolean folderPerFcltyCode) {
		this.folderPerFcltyCode = folderPerFcltyCode;
	}

	public String toString() {
		String temp = "";
		StringBuilder xmlContent = new StringBuilder("    <ReportOrg");
		
		temp = this.getOrgCd();
		
		if (null != temp && !"".equals(temp)) {
			xmlContent.append(" orgCd=\"").append(temp).append("\"");
		}
		
		temp = this.getOrgName();
		
		if (null != temp && !"".equals(temp)) {
			xmlContent.append(" orgName=\"").append(temp).append("\"");
		}
		
		temp = this.getRepOrgCd();
		
		if (null != temp && !"".equals(temp)) {
			xmlContent.append(" repOrgCd=\"").append(temp).append("\"");
		}
		
		xmlContent.append(">\n");
		xmlContent.append(this.getValidationInfo().toString());
		xmlContent.append(this.getEncryptionInfo().toString()).append("    </ReportOrg>\n");
		
		
		
		return xmlContent.toString();
	}
}