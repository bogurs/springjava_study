package kr.go.kofiu.ctr.util;

import kr.go.kofiu.common.agent.AgentException;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class RetryException extends AgentException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1188166096265866996L;

	/**
	 * ������
	 * @param msg ���� �޼��� 
	 */
	public RetryException(String msg) 
	{
		super(msg);
	}

	/**
	 * ������
	 * @param msg ���� �޼��� 
	 * @param t	Caused Exception
	 */
	public RetryException(String msg, Throwable t)
	{
		super(msg, t);
	}

	/**
	 * ������
	 * @param t Caused Exception
	 */
	public RetryException(Throwable t) 
	{
		super(t);
	}

}
