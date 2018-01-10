package kr.go.kofiu.ctr.util;

import kr.go.kofiu.common.agent.AgentException;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileDataException extends AgentException {

	/**
	 * ������ 
	 * @param msg
	 * @param t
	 */
	public FlatFileDataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	/**
	 * ������ 
	 * @param msg
	 */
	public FlatFileDataException(String msg) {
		super(msg);
	}
	
	/**
	 * ������ 
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
