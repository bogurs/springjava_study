package kr.go.kofiu.ctr.actions;


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
public class RecvMailReportActionSet implements Action  {

	/**
	 * 보고서 파일(Excel CSV 포맷) 생성 Action
	 */
	ReportAction reportAction;
	
	/**
	 * 관리자에게 오류 내역을 발송하는 Action
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
