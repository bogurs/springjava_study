package kr.go.kofiu.ctr.conf;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ��ȣȭ  ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class EncryptionInfo {
	
	/**
	 * ��ȣȭ ���� ����
	 */
	private boolean enabled = true;
	//private boolean enabled = false;


	/**
	 * ���� ���� ���� ������ ���� ����
	 */
	private SigningInfo signingInfo;

	/**
	 * ���� ���� ��ȣȭ�� ���� ����Ű ����
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
	 * ��ȣȭ�� ���࿩�� 
	 * @param isEncrypted true�̸� ��ȣȭ�� �����ϰ� �׷��� ������ ��ȭȭ�� ���� �ʴ´�.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * SigningInfo ���� ������ ���� ���� ���� ��� ��ü get
	 * @return SigningInfo ���� ������ ���� ���� ���� ��� ��ü 
	 */
	public SigningInfo getSigningInfo() {
		return signingInfo;
	}

	/**
	 * SigningInfo ���� ������ ���� ���� ���� ��� ��ü set
	 * @param signingInfo SigningInfo ���� ������ ���� ���� ���� ��� ��ü
	 */
	public void setSigningInfo(SigningInfo signingInfo) {
		this.signingInfo = signingInfo;
	}


	/**
	 * ���� ���� ��ȣȭ�� ���� ���� ������ ���� get
	 * @return PrivateCertInfo ���� ���� ��ȣȭ�� ���� ���� ������ ����
	 */
	public PrivateCertInfo getPrivateCertInfo() {
		return privateCertInfo;
	}

	/**
	 * ���� ���� ��ȣȭ�� ���� ���� ������ ���� set
	 * @param privateCertInfo ���� ���� ��ȣȭ�� ���� ���� ������ ����
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
