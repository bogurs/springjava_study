package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���۽� ���� ������ ���� ������ ����.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SigningInfo {
	/**
	 * ���� ���� ���� ���� ������
	 */
	private String certificate;
	
	/**
	 * ������ key ����
	 */
	private String key;
	
	/**
	 * ������ pin
	 */
	private String pin;
	
	/**
	 * ���� ���� ���� 
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
