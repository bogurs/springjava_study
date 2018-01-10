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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 전송 후 해야 할 Action Set 모음
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SendActionSet implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SendActionSet.class);

	/**
	 * 보고서 파일(Excel CSV 포맷) 생성 Action
	 */
	ReportAction reportAction;
	
	/**
	 * 파일을 destination directory로 move 하는 Action 
	 */
	MoveFileAction moveFileAction;
	
	/**
	 * SNR 파일 생성 Action
	 */
	SNRAction snrAction;

	/**
	 * 생성자 
	 * @param sndFile
	 */
	public SendActionSet(File sndFile ) {
		this( sndFile , TypeCode.ErrorCode.OK , null );
	}
	
	/**
	 * 생성자 
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
			logger.error("보고 문서 전송 후 리포트기록에 필요한 문서 번호를 CTR 보고파일에서  얻어오지 못했습니다. '2005-UNKNOWN'이라는 임의의 문서 번호를 사용합니다.", e);
			documentId = "2005-UNKNOWN";
			
			// error 처리
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
		// 필히 report Action을 먼저 한다. DISK FULL을 report Action에서 잡을 수 있다. 
		reportAction.doAct();
		moveFileAction.doAct();
		snrAction.doAct();
	}
	
	public void doErrorAct() {
		// 필히 report Action을 먼저 한다. DISK FULL을 report Action에서 잡을 수 있다. 
		reportAction.doAct();
		snrAction.doAct();
	}

}
