package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Self-Test ���� ������¿��� ���� ���� ���� �� �ؾ� �� Action Set ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class TestSendReceiveActionSet implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestSendReceiveActionSet.class);

	/**
	 * ���� ����(Excel CSV ����) ���� Action
	 */
	private ReportAction reportAction;
	
	/**
	 * ������ destination directory�� move �ϴ� Action 
	 */
	private MoveFileAction moveFileAction;
	
	/**
	 * SNR ���� ���� Action
	 */
	private SNRAction snrAction;

	/**
	 * ���� ���� ���� �� 
	 */
	private String rcvFile = null;
	
	/**
	 * ���� ���� ���� 
	 */
	private String flatfileText = null;
	
	/**
	 * ���� ��� �ڵ� 
	 */
	private String ReportAdministrationPartyCode = null;
	
	/**
	 * ���� ����(Excel CSV ����) ���� Action
	 */
	private ReportAction reportAction2;
	
	
	/**
	 * ������ 
	 * @param sndFile
	 */
	public TestSendReceiveActionSet(File sndFile ) {
		this( sndFile , TypeCode.ErrorCode.OK , null );
	}
	
	/**
	 * ������ 
	 * @param sndFile
	 * @param errorType
	 * @param errorMsg
	 */
	public TestSendReceiveActionSet(File sndFile, String typeCode, String errorMsg) {
		String filename = sndFile.getName();
		String msgTypeCode = null;
		String documentId = null;
		String Date = null;

		try {
			String flatdata = FileTool.getFileString(sndFile);
			String[] tokens = flatdata.split("\\|\\|"); //parseTextByDelimiter("||");
			msgTypeCode = tokens[1];
			documentId = tokens[2];
			if ( ReportAdministrationPartyCode == null ) ReportAdministrationPartyCode = tokens[8];
			if ( Date == null ) Date = tokens[3];
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
			//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType,CtrAgentEnvInfo.getSendDirName(), ReportAdministrationPartyCode,folderDate);
			moveFileAction = new MoveFileAction( sndFile, CtrAgentEnvInfo.getSendDirName() ,ReportAdministrationPartyCode);
			snrAction = new SNRAction(filename, msgTypeCode, ReportAdministrationPartyCode, documentId, Date );
		
			// ���������� ���� ���� ���� �� ���� ó�� 
			rcvFile = FileTool.replaceSuffix( filename, "RCV");
			flatfileText = mimicFlatFile(msgTypeCode, ReportAdministrationPartyCode, documentId, Date);
			reportAction2 = new ReportAction(ReportAction.RESP_REPORT, rcvFile , documentId, Date);
			
		} else {
			reportAction = new ReportAction(ReportAction.SEND_REPORT, filename, documentId, Date, ReportAction.RESULT_ERROR, errorMsg);
			//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType,CtrAgentEnvInfo.getSendErrorDirName()	, ReportAdministrationPartyCode,folderDate);
			moveFileAction = new MoveFileAction( sndFile, CtrAgentEnvInfo.getSendErrorDirName() ,ReportAdministrationPartyCode);
			snrAction = new SNRAction(filename, msgTypeCode, ReportAdministrationPartyCode, documentId, 
					Date , typeCode, errorMsg );
		}

	}
	
	/**
	 * 
	 * @param ResponseDoc
	 * @return
	 */
	public String mimicFlatFile(String msgTypeCode , String ReportAdministrationPartyCode
				, String documentId, String Date)  {
        String flatfile = "CTRSTART||";
        flatfile += TypeCode.MessageType.RCV_RESULT + "||"; 
        flatfile += msgTypeCode + "||";
		flatfile += ReportAdministrationPartyCode + "||";
		flatfile += documentId + "||";
		flatfile += Date + "||";
		flatfile += DateUtil.getDateTime(DateUtil.yyyyMMddHHmmss)+ "||";
		flatfile += "00||";
		flatfile += "��������||";
		return flatfile + "CTREND";
	}

	/*
	 *  (non-Javadoc)
	 * @see kr.go.kofiu.ctr.actions.Action#doAct()
	 */
	public void doAct() {
		reportAction.doAct();
		moveFileAction.doAct();
		snrAction.doAct();
	
		String date = CurrentTimeGetter.formatDate();
		String inboxType = Configure.getInstance().getAgentInfo().getInboxfolderType();
		try {
			if ( Configure.getInstance().getAgentInfo().isInboxEnabled() ) { 
				String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(inboxType ,CtrAgentEnvInfo.getInboxDirName() , ReportAdministrationPartyCode, date);
				//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(CtrAgentEnvInfo.getInboxDirName(), ReportAdministrationPartyCode);
				FileTool.writeToFile(dir + File.separator + rcvFile
							, flatfileText, false);
			}
			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(inboxType, CtrAgentEnvInfo.getReceiveDirName(), ReportAdministrationPartyCode, date);
			//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(CtrAgentEnvInfo.getReceiveDirName(), ReportAdministrationPartyCode);
			FileTool.writeToFile(dir + File.separator + rcvFile, flatfileText, false);
		} catch (IOException e) {
			logger.error(e, e);
		}
		reportAction2.doAct();
	}

}
