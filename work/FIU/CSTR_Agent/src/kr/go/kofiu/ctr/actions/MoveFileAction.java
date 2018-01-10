package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.Utility;
import kr.go.kofiu.str.util.CurrentTimeGetter;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���� �߻� �� �ش� ������ ARCH ������ send/senderror/receive/receiveerror
 * 			���� ���� ������ ������ Move�ϴ� Action ���� 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MoveFileAction implements Action {
	
	/**
	 * �̵��� ���� ����
	 */
	private File original;
	

	/**
	 * ������ Move�� ��� ���� 
	 */
	private String destination;
	
	/**
	 * ������ Move�� ����ڵ�
	 */
	private String orgCd;
	
	/**
	 * ������ 
	 * @param original �̵��� ���� ����
	 */
	public MoveFileAction(File original, String destination, String orgCd){
		this.original = original;
		this.destination = destination;
		this.orgCd = orgCd;
	}

	/**
	 * ���� ������ ERROR ������ �ű�� ���� ���� ������ �����Ѵ�.
	 */
	public void doAct() {
		try {
			String date = CurrentTimeGetter.formatDate();
			String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
			//destination = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, destination, orgCd, date);
			boolean isSaveArchive = kr.go.kofiu.ctr.conf.Configure.getInstance().getAgentInfo().isArchiveEnabled();
			if(isSaveArchive){
				FileTool.move(original, destination + File.separator + original.getName());
				
			}else{
				if(!destination.contains("ARCHIVE")){
					FileTool.move(original, destination + File.separator + original.getName());
				}else{
					original.delete();
				}
			}
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full ��Ȳ �߻� 
				new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
						, "DISK FULL�� ���� "	+ original + " ������ " + destination + "�� move���� ���Ͽ����ϴ�.", e).doAct();
				new ShutdownAction().doAct();
			} else {
				throw new ActionException(e);
			}
		}
	}
	
}
