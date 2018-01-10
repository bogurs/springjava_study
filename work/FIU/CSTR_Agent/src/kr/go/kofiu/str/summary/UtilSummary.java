package kr.go.kofiu.str.summary;

public class UtilSummary {
	
	/*
	 * ESB Util 서비스 호출 후 결과값을 담아놓기위한 VO
	 */

	/*
	 * 에러코드
	 */
	private String errorCd;
	
	/*
	 * 에러 메시지
	 */
	private String errorMsg;
	
	/*
	 * heartbeat 결과값
	 */
	private String heartBeatResult;
	
	/*
	 * 모듈버전
	 */
	private String moduleVersion;
	
	/*
	 * 모듈버전체크 결과값
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
