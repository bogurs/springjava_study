package kr.go.kofiu.ctr.conf;



/*******************************************************
 * <pre>
 * ����    �׷�� : CTR �ý���
 * ����    ������ : ���� ��� Agent
 * ��         �� : ���߱�� ���� ������ ��� �ִ� value object.
 * 		apache-digester�� �̿��Ͽ� Agent ���� ������ ������ Java ��ü�� �ʱ�ȭ �Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CgServer
{
	
	/**
	 * ���� ��� IP Address
	 */
	private String ip;
	
	/**
	 * ���� ��� port
	 */
	private String port;
	

	/**
	 * ���� ��� IP Address get
	 * @return ���� ��� IP Address
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * ���� ��� IP Address set
	 * @param ip ���� ��� IP Address
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * ���� ��� port get
	 * @return ���� ��� port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * ���� ��� port set
	 * @param port ���� ��� port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 * @param cgServer
	 */
	public void accept(CgServer cgServer) {
		setIp(cgServer.getIp());
		setPort(cgServer.getPort());
	}

	public String toString() {
		String xmlText = "\t<CgServer>\n";
		xmlText += "\t\t<ip>" + getIp() + "</ip>\n" ;
		xmlText += "\t\t<port>" + getPort() + "</port>\n" ;
		xmlText += "\t</CgServer>\n";
		return xmlText;
	}

}
