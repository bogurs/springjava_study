package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;
import kr.go.kofiu.ctr.util.Utility;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.log4j.Logger;

/**
 * *****************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 ******************************************************
 */
public class SNRAction implements Action {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SNRAction.class);

	/**
	 * 
	 */
	private String SNDFilename;
	
	/**
	 * Message Type Code
	 */
	private String  msgTypeCode;
	
	/**
	 * 보고 기관 코드 (4)
	 */
	private String ReportAdministrationPartyCode;
	
	/**
	 * 문서 번호 (13) exam> 2005-00000001 
	 */
	private String DocumentID;
	
	/**
	 * 보고 일자 (8)
	 */
	private String Date;
	
	/**
	 * 접수 일시 ( 14 )
	 */
	private String ReceiptDateTime;
	
	/**
	 * 오류 코드  00.성공 ,	01.보고문서형식오류, 02.보고문서항목오류,
	 * 03.XML메시지구성오류, 04.보고문서코드검증오류 , 05.인증오류,	99.기타
	 */
	private String ErrorTypeCode;
	
	/**
	 * 오류 메세지 
	 */
	private String ErrorType;

	
	/**
	 * 생성자 
	 * @param SNDFilename
	 * @param ReportAdministrationPartyCode
	 * @param DocumentID
	 * @param Date
	 */
	public SNRAction(String SNDFilename, String msgTypeCode, String ReportAdministrationPartyCode,
			String DocumentID,	String Date ) {
		this(SNDFilename, msgTypeCode,  ReportAdministrationPartyCode, DocumentID, Date,
				TypeCode.ErrorCode.OK, "발송성공");	
	}
	
	/**
	 * 생성자 
	 * @param SNDFilename
	 * @param ReportAdministrationPartyCode
	 * @param DocumentID
	 * @param Date
	 * @param ErrorTypeCode
	 * @param ErrorType
	 */
	public SNRAction(String SNDFilename, String msgTypeCode, String ReportAdministrationPartyCode, String DocumentID, 
			String Date , String ErrorTypeCode , String ErrorType) {
		this(SNDFilename, msgTypeCode, ReportAdministrationPartyCode, DocumentID, Date
				, DateUtil.getDateTime( DateUtil.yyyyMMddHHmmss ) , ErrorTypeCode, ErrorType);	
	}

	/**
	 * 생성자 
	 * @param SNDFilename
	 * @param ReportAdministrationPartyCode
	 * @param DocumentID
	 * @param Date
	 * @param ReceiptDateTime
	 * @param ErrorTypeCode
	 * @param ErrorType
	 */
	public SNRAction(String SNDFilename, String msgTypeCode, String ReportAdministrationPartyCode, String DocumentID, 
			String Date , String ReceiptDateTime , 
			String ErrorTypeCode , String ErrorType) {
		this.SNDFilename = SNDFilename;
		this.msgTypeCode = msgTypeCode;
		this.ReportAdministrationPartyCode = ReportAdministrationPartyCode;
		this.DocumentID = DocumentID;
		this.Date = Date;
		this.ReceiptDateTime = ReceiptDateTime;
		this.ErrorTypeCode = ErrorTypeCode;
		this.ErrorType = ErrorType;		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see kr.go.kofiu.ctr.actions.Action#doAct()
	 */
	public void doAct() {
		try {
			// replace SND to SNR
			String snrFilename = FileTool.replaceSuffix(SNDFilename, "SNR");	
			String snr = generateSNR();
			String date = CurrentTimeGetter.formatDate();
			String inboxType = Configure.getInstance().getAgentInfo().getInboxfolderType();
			String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
			
			// check inbox save 
			if ( Configure.getInstance().getAgentInfo().isInboxEnabled()) { 
				
				String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(inboxType ,CtrAgentEnvInfo.getInboxDirName() , ReportAdministrationPartyCode, date);
				//String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(CtrAgentEnvInfo.getInboxDirName(), ReportAdministrationPartyCode);
				FileTool.writeToFile( dir + File.separator 	+ snrFilename, snr, false );
			}

			String dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, CtrAgentEnvInfo.getReceiveDirName(), ReportAdministrationPartyCode, date);
			//String dir =CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(CtrAgentEnvInfo.getReceiveDirName(), ReportAdministrationPartyCode);
			FileTool.writeToFile(dir + File.separator + snrFilename, snr , false);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full 상황 발생 
				/*new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
						, "DISK FULL로 인해 보고 문서 " + SNDFilename + "에 대한 발송 증서 파일 '" + generateSNR() + "' 을 생성하지 못하였습니다.", e).doAct();*/
				new ShutdownAction().doAct();
			}
			logger.error("", e);
		}
	}

	/**
	 * SNR 파일 데이타를 생성한다.
	 * @return
	 */
	public String generateSNR(){
		String message = "CTRSTART||";
		message += TypeCode.MessageType.SNR_RESULT + "||";
       	message += msgTypeCode + "||";
		message += ReportAdministrationPartyCode + "||"; 
		message += DocumentID + "||"; 
		message += Date + "||"; 
		message += ReceiptDateTime + "||"; 
		message += ErrorTypeCode + "||";  
		message += ErrorType ; 
		message += "||CTREND";
		return message;
	}
}
