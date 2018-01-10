package kr.go.kofiu.ctr.util;

import kr.go.kofiu.common.agent.AgentException;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileDataException extends AgentException {

	/**
	 * 생성자 
	 * @param msg
	 * @param t
	 */
	public FlatFileDataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	/**
	 * 생성자 
	 * @param msg
	 */
	public FlatFileDataException(String msg) {
		super(msg);
	}
	
	/**
	 * 생성자 
	 * @param t
	 */
	public FlatFileDataException(Throwable t) {
		super(t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
