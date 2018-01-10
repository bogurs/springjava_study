package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : STR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 기관이 받은 접수 증서 복호화를 위한 개인 인증서 정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 선만주 
 * 작   성   일  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class KeyManageInfo {
	/**
	 * 키 관리
	 */
	private boolean enabled = true;

	/**
	 * 전자서명 및 암호화 집합
	 */
	private String certificate;

	/**
	 * 
	 */
	private String pin;

	/**
	 * 
	 */
	private String key;

	public KeyManageInfo() {
	}

	/**
	 * 
	 * @return
	 */
	public String getCertificate() {
		return certificate;
	}

	/**
	 * 
	 * @param certificate
	 */
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	/**
	 * 
	 * @return
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * 
	 * @param pin
	 */
	public void setPin(String pin) {
		this.pin = (pin == null ? "" : pin);
	}

	/**
	 * 
	 * @param privateCertInfo
	 */
	public void accept(KeyManageInfo privateCertInfo) {
		setCertificate(privateCertInfo.getCertificate());
		setPin(privateCertInfo.getPin());
	}

	public String toString() {
		String xmlText = "            <keyManage enabled=\""
				+ Boolean.toString(this.enabled) + "\">\n";
		xmlText += "                <certificate>" + this.getCertificate()
				+ "</certificate>\n";

		if (null != this.getKey()) {
			xmlText += "                <key>" + this.getKey() + "</key>\n";
		}

		if (null != this.getPin()) {
			xmlText += "                <pin>" + this.getPin() + "</pin>\n";
		}
		
		xmlText += "            </keyManage>\n";
		
		return xmlText;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = (key == null ? "" : key);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
