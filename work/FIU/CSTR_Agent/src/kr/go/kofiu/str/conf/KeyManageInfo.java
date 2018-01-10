package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : STR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ����� ���� ���� ���� ��ȣȭ�� ���� ���� ������ ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ������ 
 * ��   ��   ��  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class KeyManageInfo {
	/**
	 * Ű ����
	 */
	private boolean enabled = true;

	/**
	 * ���ڼ��� �� ��ȣȭ ����
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
