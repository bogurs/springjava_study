package kr.go.kofiu.ctr.actions;


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
public class RecvMailReportActionSet implements Action  {

	/**
	 * ���� ����(Excel CSV ����) ���� Action
	 */
	ReportAction reportAction;
	
	/**
	 * �����ڿ��� ���� ������ �߼��ϴ� Action
	 */
	EmailAction emailAction;

	
	public RecvMailReportActionSet (String rcvFile, String documentId, String reportDate,
			String subject, Throwable throwable ) {
		reportAction = new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, reportDate,
				ReportAction.RESULT_ERROR, throwable.getMessage());
		emailAction = new EmailAction(subject, throwable);
	}

	public void doAct() {
		reportAction.doAct();
		emailAction.doAct();
	}
	
}
