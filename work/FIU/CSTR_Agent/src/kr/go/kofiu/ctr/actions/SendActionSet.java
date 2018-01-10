package kr.go.kofiu.ctr.actions;

import java.io.File;

import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���� �� �ؾ� �� Action Set ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SendActionSet implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SendActionSet.class);

	/**
	 * ���� ����(Excel CSV ����) ���� Action
	 */
	ReportAction reportAction;
	
	/**
	 * ������ destination directory�� move �ϴ� Action 
	 */
	MoveFileAction moveFileAction;
	
	/**
	 * SNR ���� ���� Action
	 */
	SNRAction snrAction;

	/**
	 * ������ 
	 * @param sndFile
	 */
	public SendActionSet(File sndFile ) {
		this( sndFile , TypeCode.ErrorCode.OK , null );
	}
	
	/**
	 * ������ 
	 * @param sndFile
	 * @param errorType
	 * @param errorMsg
	 */
	public SendActionSet(File sndFile, String typeCode, String errorMsg) {
		String filename = sndFile.getName();

		String msgTypeCode = null;
		String ReportAdministrationPartyCode = null;
		String documentId = null;
		String Date = null;

		try {
			String flatdata = FileTool.getFileString(sndFile);
			String[] tokens = flatdata.split("\\|\\|"); //parseTextByDelimiter("||");
			msgTypeCode = tokens[1];
			documentId = tokens[2];
			ReportAdministrationPartyCode = tokens[8];
			Date = tokens[3];
		} catch (Exception e) {
			msgTypeCode="00";
			logger.error("���� ���� ���� �� ����Ʈ��Ͽ� �ʿ��� ���� ��ȣ�� CTR �������Ͽ���  ������ ���߽��ϴ�. '2005-UNKNOWN'�̶�� ������ ���� ��ȣ�� ����մϴ�.", e);
			documentId = "2005-UNKNOWN";
			
			// error ó��
			if ( !TypeCode.ErrorCode.FILE_FORMAT_ERROR.equals(typeCode) ) {
				if ( filename != null && filename.length() > 8)
					ReportAdministrationPartyCode = filename.substring(4, 8) ; // from file name

				if ( filename != null && filename.length() > 17)
					Date = filename.substring(9,17) ; // from file name
			} else {
				ReportAdministrationPartyCode = "0000";
				Date = DateUtil.getDateTime( DateUtil.yyyyMMdd );
			}
		}
		
		String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
		String folderDate = CurrentTimeGetter.formatDate();
		if ( TypeCode.ErrorCode.OK.equals(typeCode) ) {
			reportAction = new ReportAction(ReportAction.SEND_REPORT, filename, documentId, Date);
			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, CtrAgentEnvInfo.getSendDirName(), ReportAdministrationPartyCode, folderDate);
			//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(CtrAgentEnvInfo.getSendDirName(), ReportAdministrationPartyCode);
			moveFileAction = new MoveFileAction( sndFile, dir, ReportAdministrationPartyCode);
			snrAction = new SNRAction(filename, msgTypeCode, ReportAdministrationPartyCode, documentId, Date );
		} else {
			reportAction = new ReportAction(ReportAction.SEND_REPORT, filename, documentId, Date,
					ReportAction.RESULT_ERROR, errorMsg);
			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, CtrAgentEnvInfo.getSendErrorDirName(), ReportAdministrationPartyCode, folderDate);
			//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(,CtrAgentEnvInfo.getSendErrorDirName()	, ReportAdministrationPartyCode);
			moveFileAction = new MoveFileAction( sndFile, dir, ReportAdministrationPartyCode );
			snrAction = new SNRAction(filename, msgTypeCode, ReportAdministrationPartyCode, documentId, Date , typeCode, errorMsg );
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see kr.go.kofiu.ctr.actions.Action#doAct()
	 */
	public void doAct() {
		// ���� report Action�� ���� �Ѵ�. DISK FULL�� report Action���� ���� �� �ִ�. 
		reportAction.doAct();
		moveFileAction.doAct();
		snrAction.doAct();
	}
	
	public void doErrorAct() {
		// ���� report Action�� ���� �Ѵ�. DISK FULL�� report Action���� ���� �� �ִ�. 
		reportAction.doAct();
		snrAction.doAct();
	}

}
