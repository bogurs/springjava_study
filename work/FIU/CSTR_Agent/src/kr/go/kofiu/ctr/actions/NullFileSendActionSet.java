package kr.go.kofiu.ctr.actions;

import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.TypeCode;

public class NullFileSendActionSet implements Action {
	/**
	 * 焊绊辑 颇老(Excel CSV 器杆) 积己 Action
	 */
	ReportAction reportAction;
	

	/**
	 * SNR 颇老 积己 Action
	 */
	SNRAction snrAction;

	/**
	 * 积己磊 
	 * @param sndFile
	 */
	public NullFileSendActionSet(String filename, String errorMsg) {
		this( filename , TypeCode.ErrorCode.OK , errorMsg );
	}
	
	/**
	 * 积己磊 
	 * @param sndFile
	 * @param errorType
	 * @param errorMsg
	 */
	public NullFileSendActionSet(String filename, String typeCode, String errorMsg) {
		String msgTypeCode = null;
		String ReportAdministrationPartyCode = null;
		String documentId = "2005-UNKNOWN";
		String Date = null;

		if ( !TypeCode.ErrorCode.FILE_FORMAT_ERROR.equals(typeCode) ) {
			if ( filename != null && filename.length() > 8)
				ReportAdministrationPartyCode = filename.substring(4, 8) ; // from file name

			if ( filename != null && filename.length() > 17)
				Date = filename.substring(9,17) ; // from file name
		}
		
		if ( ReportAdministrationPartyCode == null ) ReportAdministrationPartyCode = "0000";
		if ( Date == null ) Date = DateUtil.getDateTime( DateUtil.yyyyMMdd );

		reportAction = new ReportAction(ReportAction.SEND_REPORT, filename, documentId, Date,
				ReportAction.RESULT_ERROR, errorMsg);
		snrAction = new SNRAction(filename, msgTypeCode, ReportAdministrationPartyCode, documentId, 
				Date , typeCode, errorMsg );

	}
	
	/*
	 *  (non-Javadoc)
	 * @see kr.go.kofiu.ctr.actions.Action#doAct()
	 */
	public void doAct() {
		reportAction.doAct();
		snrAction.doAct();
	}

}
