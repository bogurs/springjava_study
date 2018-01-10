package kr.go.kofiu.ctr.util;

import kr.go.kofiu.common.agent.AgentException;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : FlatFile�� XML�� ��ȯ�ÿ� ������ �߻� 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileConvertingException extends AgentException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1188166096265866996L;

	/**
	 * ������ 
	 * @param msg
	 */
	public FlatFileConvertingException(String msg) 
	{
		super(msg);
	}

	/**
	 * ������
	 * @param msg
	 * @param t
	 */
	public FlatFileConvertingException(String msg, Throwable t)
	{
		super(msg, t);
	}

	/**
	 * ������
	 * @param e
	 */
	public FlatFileConvertingException(Throwable e) 
	{
		super(e);
	}

}
