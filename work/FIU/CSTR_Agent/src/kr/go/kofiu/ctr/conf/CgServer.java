package kr.go.kofiu.ctr.conf;



/*******************************************************
 * <pre>
 * 업무    그룹명 : CTR 시스템
 * 서브    업무명 : 보고 기관 Agent
 * 설         명 : 집중기관 접속 정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CgServer
{
	
	/**
	 * 집중 기관 IP Address
	 */
	private String ip;
	
	/**
	 * 집중 기관 port
	 */
	private String port;
	

	/**
	 * 집중 기관 IP Address get
	 * @return 집중 기관 IP Address
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 집중 기관 IP Address set
	 * @param ip 집중 기관 IP Address
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 집중 기관 port get
	 * @return 집중 기관 port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * 집중 기관 port set
	 * @param port 집중 기관 port
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
