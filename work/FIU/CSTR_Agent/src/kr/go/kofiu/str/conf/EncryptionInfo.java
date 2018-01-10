package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : STR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ��ȣȭ  ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ������ 
 * ��   ��   ��  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class EncryptionInfo {
	/**
	 * ���� ���� ������  ����
	 */
	private SigningInfo signingInfo;

	/**
	 * Ű ���� ������ ����
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
