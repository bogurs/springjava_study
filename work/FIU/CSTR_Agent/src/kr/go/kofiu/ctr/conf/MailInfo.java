package kr.go.kofiu.ctr.conf;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Mail 설정 정보를 담고 있는 value object.
 * 		apache-digester를 이용하여 Agent 설정 파일의 내용을 Java 객체에 초기화 한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MailInfo {
	
	/**
	 * 메일 사용 여부
	 */
	private boolean enabled = true;
	
	/**
	 * mail host
	 */
	private String host;
	
	/**
	 * mail id
	 */
	private String id;
	
	/**
	 * mail password
	 */
	private String password;
	
	/**
	 * mail to
	 */
	private String to;
	
	/**
	 * mail cc
	 */
	private String cc;

	
	/**
	 * 
	 * @return
	 */
	public String getCc() {
		return cc;
	}
	
	/**
	 * 
	 * @param cc
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTo() {
		return to;
	}
	
	/**
	 * 
	 * @param to
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 
	 * @param mailInfo
	 */
	public void accept(MailInfo mailInfo) {
		setEnabled(mailInfo.isEnabled());
		setHost(mailInfo.getHost());
		setId(mailInfo.getId());
		setPassword(mailInfo.getPassword());
		setTo(mailInfo.getTo());
		setCc(mailInfo.getCc());
	}

	public String toString() {
		String xmlText = "\t<MailInfo enabled=\"" + Boolean.toString(this.isEnabled()) + "\">\n";
		xmlText += "\t\t<host>" + this.getHost() + "</host>\n" ;
		xmlText += "\t\t<id>" + this.getId() + "</id>\n" ;
		xmlText += "\t\t<password>" + this.getPassword() + "</password>\n" ;
		xmlText += "\t\t<to>" + this.getTo() + "</to>\n" ;
		xmlText += "\t\t<cc>" + this.getCc() + "</cc>\n" ;
		xmlText += "\t</MailInfo>\n";
		return xmlText;
	}
	
}
