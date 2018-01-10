package kr.go.kofiu.ctr.util;

import kr.go.kofiu.common.agent.AgentException;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class RetryException extends AgentException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1188166096265866996L;

	/**
	 * 생성자
	 * @param msg 오류 메세지 
	 */
	public RetryException(String msg) 
	{
		super(msg);
	}

	/**
	 * 생성자
	 * @param msg 오류 메세지 
	 * @param t	Caused Exception
	 */
	public RetryException(String msg, Throwable t)
	{
		super(msg, t);
	}

	/**
	 * 생성자
	 * @param t Caused Exception
	 */
	public RetryException(Throwable t) 
	{
		super(t);
	}

}
