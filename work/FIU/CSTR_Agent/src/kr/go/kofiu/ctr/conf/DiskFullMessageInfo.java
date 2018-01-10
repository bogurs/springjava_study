package kr.go.kofiu.ctr.conf;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 12. 21
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class DiskFullMessageInfo {

	/**
	 * 
	 */
	private String message;
	
	/**
	 * 
	 */
	private String description;
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		String xmlText = "\t\t<Message message=\""
			+ this.getMessage() + "\" description=\"" + this.getDescription() + "\" />\n";
		return xmlText;
	}

}
