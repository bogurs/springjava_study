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
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
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
	 * ���� ��� �ڵ� (4)
	 */
	private String ReportAdministrationPartyCode;
	
	/**
	 * ���� ��ȣ (13) exam> 2005-00000001 
	 */
	private String DocumentID;
	
	/**
	 * ���� ���� (8)
	 */
	private String Date;
	
	/**
	 * ���� �Ͻ� ( 14 )
	 */
	private String ReceiptDateTime;
	
	/**
	 * ���� �ڵ�  00.���� ,	01.���������Ŀ���, 02.�������׸����,
	 * 03.XML�޽�����������, 04.�������ڵ�������� , 05.��������,	99.��Ÿ
	 */
	private String ErrorTypeCode;
	
	/**
	 * ���� �޼��� 
	 */
	private String ErrorType;

	
	/**
	 * ������ 
	 * @param SNDFilename
	 * @param ReportAdministrationPartyCode
	 * @param DocumentID
	 * @param Date
	 */
	public SNRAction(String SNDFilename, String msgTypeCode, String ReportAdministrationPartyCode,
			String DocumentID,	String Date ) {
		this(SNDFilename, msgTypeCode,  ReportAdministrationPartyCode, DocumentID, Date,
				TypeCode.ErrorCode.OK, "�߼ۼ���");	
	}
	
	/**
	 * ������ 
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
	 * ������ 
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
				// Disk Full ��Ȳ �߻� 
				/*new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
						, "DISK FULL�� ���� ���� ���� " + SNDFilename + "�� ���� �߼� ���� ���� '" + generateSNR() + "' �� �������� ���Ͽ����ϴ�.", e).doAct();*/
				new ShutdownAction().doAct();
			}
			logger.error("", e);
		}
	}

	/**
	 * SNR ���� ����Ÿ�� �����Ѵ�.
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
