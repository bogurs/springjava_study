package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : STR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 암호화  정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 선만주 
 * 작   성   일  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class EncryptionInfo {
	/**
	 * 전자 서명 인증서  정보
	 */
	private SigningInfo signingInfo;

	/**
	 * 키 관리 인증서 정보
	 */
	private KeyManageInfo keyManageInfo;

	public SigningInfo getSigningInfo() {
		return signingInfo;
	}

	public void setSigningInfo(SigningInfo signingInfo) {
		this.signingInfo = signingInfo;
	}

	public KeyManageInfo getKeyManageInfo() {
		return keyManageInfo;
	}

	public void setKeyManageInfo(KeyManageInfo keyManageInfo) {
		this.keyManageInfo = keyManageInfo;
	}
	
	public String toString() {
		StringBuilder xml = new StringBuilder("        <Encryption>\n");
		
		if (null != this.getSigningInfo()) {
			xml.append(this.getSigningInfo().toString());
		}
		
		if (null != this.getKeyManageInfo()) {
			xml.append(this.getKeyManageInfo().toString());
		}
		
		xml.append("        </Encryption>\n");
		
		return xml.toString();
	}
}
