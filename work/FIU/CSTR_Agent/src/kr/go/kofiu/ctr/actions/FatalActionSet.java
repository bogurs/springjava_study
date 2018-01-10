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
	 * ���� ����(Excel CSV ����) ���� Action
	 */
	ReportAction reportAction;
	
	/**
	 * �����ڿ��� ���� ������ �߼��ϴ� Action
	 */
	EmailAction emailAction;
	
	/**
	 * Agent ���� Action
	 */
	ShutdownAction shutdownAction;

	/**
	 * ���� ���� ���� �ÿ� ġ������ ���� �߻��� ���ȴ�.
	 * @param subject
	 * @param throwable
	 */
	public FatalActionSet (String subject, Throwable throwable ) {
		reportAction = new ReportAction(ReportAction.RESP_REPORT, throwable.getMessage());
		emailAction = new EmailAction(subject, throwable);
		shutdownAction = new ShutdownAction();
	}
	
	/**
	 * ���� ���� �۽� �ÿ� ġ������ ���� �߻� �ÿ� ��� �ȴ�.
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
			logger.error("���� �������� ������ȣ ���⿡ �����Ͽ����ϴ�. ����� ���� ������ ������ȣ '2005-UNKNOWN' �� ���˴ϴ�.", e);
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
