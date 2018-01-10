package kr.go.kofiu.ctr.conf;


/*******************************************************
 * <pre>
 * ���� �׷�� : CTR �ý���
 * ���� ������ : ���� ��� Agent
 * ��          �� : config.xml���� �о�� �����ٸ� Job������ ��� �ִ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 7. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class JobInfo 
{

	/**
	 * Job Ŭ���� �� 
	 */
	private String clazzname;
	
	/**
	 * ���� �ð� Time ���� 
	 */
	private String timeformat;
	
	/**
	 * ���� 
	 */
	private String desc;

	/**
	 * �߰������� �ʿ��� parameter�� ��
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
