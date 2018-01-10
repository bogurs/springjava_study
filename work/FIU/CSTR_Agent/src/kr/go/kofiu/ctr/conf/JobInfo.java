package kr.go.kofiu.ctr.conf;


/*******************************************************
 * <pre>
 * 업무 그룹명 : CTR 시스템
 * 서브 업무명 : 보고 기관 Agent
 * 설          명 : config.xml에서 읽어온 스케줄링 Job정보를 담고 있다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class JobInfo 
{

	/**
	 * Job 클래스 명 
	 */
	private String clazzname;
	
	/**
	 * 실행 시간 Time 포맷 
	 */
	private String timeformat;
	
	/**
	 * 설명 
	 */
	private String desc;

	/**
	 * 추가적으로 필요한 parameter를 값
	 */
	private String param;
	
	/**
	 * 
	 * @return
	 */
	public String getParam() {
		return param;
	}
	
	/**
	 * 
	 * @param param
	 */
	public void setParam(String param) {
		this.param = param;
	}

	/**
	 * @return
	 */
	public String getClazzname()
	{
		return clazzname;
	}

	/**
	 * @param clazzname
	 */
	public void setClazzname(String clazzname)
	{
		this.clazzname = clazzname;
	}

	/**
	 * @return
	 */
	public String getDesc() 
	{
		return desc;
	}

	/**
	 * @param desc
	 */
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	/**
	 * @return
	 */
	public String getTimeformat() 
	{
		return timeformat;
	}

	/**
	 * @param timeformat
	 */
	public void setTimeformat(String timeformat) 
	{
		this.timeformat = timeformat;
	}

	public String toString() {
		String xmlText = "\t\t<job>\n";
		xmlText += "\t\t\t<clazzname>" + getClazzname() + "</clazzname>\n" ;
		xmlText += "\t\t\t<timeformat>" + getTimeformat() + "</timeformat>\n" ;
		if ( getParam() != null )
			xmlText += "\t\t\t<param>" + getParam() + "</param>\n" ;
		
		if ( getDesc() != null )
			xmlText += "\t\t\t<desc>" + getDesc() + "</desc>\n" ;
		xmlText += "\t\t</job>\n";
		return xmlText;
	}

}
