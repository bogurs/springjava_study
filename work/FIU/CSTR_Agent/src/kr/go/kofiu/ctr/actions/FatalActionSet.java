package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.util.FileTool;

import org.apache.log4j.Logger;

public class FatalActionSet implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FatalActionSet.class);

	/**
	 * 보고서 파일(Excel CSV 포맷) 생성 Action
	 */
	ReportAction reportAction;
	
	/**
	 * 관리자에게 오류 내역을 발송하는 Action
	 */
	EmailAction emailAction;
	
	/**
	 * Agent 종료 Action
	 */
	ShutdownAction shutdownAction;

	/**
	 * 접수 증서 수신 시에 치명적인 오류 발생시 사용된다.
	 * @param subject
	 * @param throwable
	 */
	public FatalActionSet (String subject, Throwable throwable ) {
		reportAction = new ReportAction(ReportAction.RESP_REPORT, throwable.getMessage());
		emailAction = new EmailAction(subject, throwable);
		shutdownAction = new ShutdownAction();
	}
	
	/**
	 * 보고 문서 송신 시에 치명적이 오류 발생 시에 사용 된다.
	 * @param sndFile
	 * @param subject
	 * @param throwable
	 */
	public FatalActionSet (File sndFile, String subject, Throwable throwable ) {
		String filename = sndFile.getName();

		String documentId = null;
		String reportDate = null;
		try {
			String flatdata = FileTool.getFileString(sndFile);
			String[] tokens = flatdata.split("\\|\\|"); 
			documentId = tokens[2];
			reportDate = tokens[3];
		} catch (IOException e) {
			logger.error("보고 문서에서 문서번호 추출에 실패하였습니다. 기록을 위해 임의의 문서번호 '2005-UNKNOWN' 가 사용됩니다.", e);
			documentId = "2005-UNKNOWN";
		}

		reportAction = new ReportAction(ReportAction.SEND_REPORT, filename, documentId, reportDate ,
				ReportAction.RESULT_ERROR, throwable.getMessage());
		emailAction = new EmailAction(subject, throwable);
		shutdownAction = new ShutdownAction();
	}
	
	public void doAct() {
		reportAction.doAct();
		emailAction.doAct();
		shutdownAction.doAct();
	}

}
