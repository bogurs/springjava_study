package kr.go.kofiu.ctr.actions;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Command pattern�� �̿��� Action ����. Action Registry�� 
 * 				�������� Action�� ����Ͽ� �Ѳ����� Action�� ���� �� �ִ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public interface Action {

	/**
	 * ������ ������ �� �޼ҵ忡 �����Ѵ�.
	 *
	 */
	public void doAct();
}
