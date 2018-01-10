package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : STR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 전송시 전자 서명을 위한 인증서 정보.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 선만주 
 * 작   성   일  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class SigningInfo {
	/**
	 * 전자 서명 인증서 파일명
	 */
	private String certificate;

	/**
	 * 전자 서명 인증서 key 파일명
	 */
	private String key;

	/**
	 * 전자 서명 인증서 pin 번호
	 */
	private String pin;

	/**
	 * 전자 서명 유무
	 */
	private boolean enabled = true;

	public String toString() {
		String xmlText = "            <signing enabled=\""
				+ Boolean.toString(this.isEnabled()) + "\">\n";
		xmlText += "                <certificate>" + this.getCertificate()
				+ "</certificate>\n";

		if (null != this.getKey()) {
			xmlText += "                <key>" + this.getKey() + "</key>\n";
		}

		if (null != this.getPin()) {
			xmlText += "                <pin>" + this.getPin() + "</pin>\n";
		}

		xmlText += "            </signing>\n";

		return xmlText;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
