package kr.go.kofiu.ctr.actions;

import kr.go.kofiu.common.agent.CTRAgent;




/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Agent shutdown Action ���� 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ShutdownAction implements Action {

	/**
	 * Agent�� Shutdown�Ѵ�.
	 */
	public void doAct() {
		CTRAgent.getInstance().shutdown();
	}

}
