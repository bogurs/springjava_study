package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.Utility;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���� �� ��ȣȭ �� ������ ���� �߻��� �ش� ������ 
 * 				PROC/receive ������ �̵��Ѵ�.  
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MoveRecevieProcAction implements Action {
	
	/**
	 * ���� ���� �� 
	 */
	private String rcvFileName;
	
	/**
	 * ��ȣȭ�� �޼��� 
	 */
	private byte[] encryptedMsg;
	
	/**
	 * ������
	 * @param rcvFileName	���� ���� �� 
	 * @param encryptedMsg	��ȣȭ�� �޼��� 
	 */
	public MoveRecevieProcAction(String rcvFileName, byte[] encryptedMsg){
		this.rcvFileName = rcvFileName;
		this.encryptedMsg = encryptedMsg;
	}
	
	/**
	 * ��ȣȭ ����Ÿ�� PROC/RCV ������ �����Ѵ�. 
	 * �̵��� ����Ÿ�� Agent�� RetryInboxErrorJob�� ���� ó���ȴ�. 
	 */
	public void doAct() {
		try {
			String proc_dir = CtrAgentEnvInfo.PROC_RECEIVE_DIR_NAME;
			FileTool.writeToFile(proc_dir + File.separator + rcvFileName, encryptedMsg, false);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full ��Ȳ �߻� 
				// Disk Full ��Ȳ �߻� 
				new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
						, "DISK FULL�� ���� ���� ���� "	+ rcvFileName + " ������ " + CtrAgentEnvInfo.PROC_RECEIVE_DIR_NAME + "�� �������� ���Ͽ����ϴ�.", e).doAct();
				new ShutdownAction().doAct();
			}
			throw new ActionException(e);
		}
	}

}
