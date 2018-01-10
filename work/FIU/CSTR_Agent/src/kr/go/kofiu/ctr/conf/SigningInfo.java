package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 전송시 전자 서명을 위한 인증서 정보.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SigningInfo {
	/**
	 * 보고 문서 전자 서명 인증서
	 */
	private String certificate;
	
	/**
	 * 인증서 key 파일
	 */
	private String key;
	
	/**
	 * 인증서 pin
	 */
	private String pin;
	
	/**
	 * 전자 서명 유무 
	 */
	private boolean enabled = true;
	
	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * 
	 * @param isSigning
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	public String getKey() {
		return key;
	}
	
	/**
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
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
		this.pin = pin;
	}

	/**
	 * 
	 * @param signingInfo
	 */
	public void accept(SigningInfo signingInfo) {
		setEnabled(signingInfo.isEnabled());
		setKey(signingInfo.getKey());
		setCertificate(signingInfo.getCertificate());
		setPin(signingInfo.getPin());
	}

	
	
	public String toString() {
		String xmlText = "\t\t<signing enabled=\"" + Boolean.toString(this.isEnabled()) + "\">\n";
		xmlText += "\t\t\t<certificate>" + this.getCertificate() + "</certificate>\n" ;
		xmlText += "\t\t\t<key>" + this.getKey() + "</key>\n" ;
		xmlText += "\t\t\t<pin>" + this.getPin() + "</pin>\n" ;
		xmlText += "\t\t</signing>\n";
		return xmlText;
	}
	
}
