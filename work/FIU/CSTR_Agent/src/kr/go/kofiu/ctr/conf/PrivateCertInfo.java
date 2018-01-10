package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ��ȣȭ�� ���� ���� ������ ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class PrivateCertInfo {
	
	/**
	 * ���� ������ ���� �� 
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
