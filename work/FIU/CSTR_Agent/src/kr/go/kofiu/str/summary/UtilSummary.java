package kr.go.kofiu.str.summary;

public class UtilSummary {
	
	/*
	 * ESB Util ���� ȣ�� �� ������� ��Ƴ������� VO
	 */

	/*
	 * �����ڵ�
	 */
	private String errorCd;
	
	/*
	 * ���� �޽���
	 */
	private String errorMsg;
	
	/*
	 * heartbeat �����
	 */
	private String heartBeatResult;
	
	/*
	 * ������
	 */
	private String moduleVersion;
	
	/*
	 * ������üũ �����
	 */
	private String moduleVersionResult;
	
	public String getErrorCd() {
		return errorCd;
	}
	public void setErrorCd(String errorCd) {
		this.errorCd = errorCd;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getHeartBeatResult() {
		return heartBeatResult;
	}
	public void setHeartBeatResult(String heartBeatResult) {
		this.heartBeatResult = heartBeatResult;
	}
	public String getModuleVersion() {
		return moduleVersion;
	}
	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}
	public String getModuleVersionResult() {
		return moduleVersionResult;
	}
	public void setModuleVersionResult(String moduleVersionResult) {
		this.moduleVersionResult = moduleVersionResult;
	}

}
