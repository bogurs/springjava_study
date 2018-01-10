package kr.go.kofiu.ctr.conf;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 암호화  정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class EncryptionInfo {
	
	/**
	 * 암호화 수행 여부
	 */
	private boolean enabled = true;
	//private boolean enabled = false;


	/**
	 * 보고 문서 전자 서명을 위한 정보
	 */
	private SigningInfo signingInfo;

	/**
	 * 접수 증서 복호화를 위한 개인키 정보
	 */
	private PrivateCertInfo privateCertInfo;

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 암호화를 수행여부 
	 * @param isEncrypted true이면 암호화를 수행하고 그렇지 않으면 암화화를 하지 않는다.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * SigningInfo 전자 서명을 위한 파일 명이 담긴 객체 get
	 * @return SigningInfo 전자 서명을 위한 파일 명이 담긴 객체 
	 */
	public SigningInfo getSigningInfo() {
		return signingInfo;
	}

	/**
	 * SigningInfo 전자 서명을 위한 파일 명이 담긴 객체 set
	 * @param signingInfo SigningInfo 전자 서명을 위한 파일 명이 담긴 객체
	 */
	public void setSigningInfo(SigningInfo signingInfo) {
		this.signingInfo = signingInfo;
	}


	/**
	 * 접수 증서 복호화를 위한 개인 인증서 정보 get
	 * @return PrivateCertInfo 접수 증서 복호화를 위한 개인 인증서 정보
	 */
	public PrivateCertInfo getPrivateCertInfo() {
		return privateCertInfo;
	}

	/**
	 * 접수 증서 복호화를 위한 개인 인증서 정보 set
	 * @param privateCertInfo 접수 증서 복호화를 위한 개인 인증서 정보
	 */
	public void setPrivateCertInfo(PrivateCertInfo privateCertInfo) {
		this.privateCertInfo = privateCertInfo;
	}

	/**
	 * 
	 * @param encryptionInfo
	 */
	public void accept(EncryptionInfo encryptionInfo) {
		this.setEnabled(encryptionInfo.isEnabled());
		signingInfo.accept(encryptionInfo.getSigningInfo());
		privateCertInfo.accept(encryptionInfo.getPrivateCertInfo());
	}

	public String toString() {
		String xmlText = "\t<Encryption enabled=\"" + Boolean.toString(this.isEnabled()) + "\">\n";
		xmlText += signingInfo.toString();
		xmlText += privateCertInfo.toString();
		xmlText += "\t</Encryption>\n";
		return xmlText;
	}
	
}
