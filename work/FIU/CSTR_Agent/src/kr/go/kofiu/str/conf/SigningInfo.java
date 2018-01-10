package kr.go.kofiu.str.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : STR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���۽� ���� ������ ���� ������ ����.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ������ 
 * ��   ��   ��  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class SigningInfo {
	/**
	 * ���� ���� ������ ���ϸ�
	 */
	private String certificate;

	/**
	 * ���� ���� ������ key ���ϸ�
	 */
	private String key;

	/**
	 * ���� ���� ������ pin ��ȣ
	 */
	private String pin;

	/**
	 * ���� ���� ����
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
