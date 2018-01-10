package kr.go.kofiu.ctr.actions;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Action�� ���� �߿� �߻��ϴ� Exception
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ActionException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4421877515429163482L;

	/**
	 * ������ 
	 * @param msg ���� �޼��� 
	 */
	public ActionException(String msg) {
		super(msg);
	}

	/**
	 * ������ 
	 * @param msg ���� �޼��� 
	 * @param t Caused Exception
	 */
	public ActionException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * ������
	 * @param t Caused Exception
	 */
	public ActionException(Throwable t) {
		super(t);
	}

}
