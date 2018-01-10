package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 접수 증서 복호화를 위한 개인 인증서 정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class PrivateCertInfo {
	
	/**
	 * 개인 인증서 파일 명 
	 */
	private String certificate;
	
	/**
	 * 
	 */
	private String pin;
	
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
		this.pin = pin;
	}

	/**
	 * 
	 * @param privateCertInfo
	 */
	public void accept(PrivateCertInfo privateCertInfo) {
		setCertificate(privateCertInfo.getCertificate());
		setPin(privateCertInfo.getPin());
	}

	
	public String toString() {
		String xmlText = "\t\t<private>\n";
		xmlText += "\t\t\t<certificate>" + this.getCertificate() + "</certificate>\n" ;
		xmlText += "\t\t\t<pin>" + this.getPin() + "</pin>\n" ;
		xmlText += "\t\t</private>\n";
		return xmlText;
	}
	
	
}
