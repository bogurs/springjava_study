package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.Utility;

import org.apache.log4j.Logger;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : �����ڰ� �ս��� Ȯ�� �� ��������  ���� ���� �۽� �� ���� ���� ���� ��
 * 			 ���� �� ���� ������ ���Ͽ� ����Ѵ�. 
 * 			������ ���� ��¥ ���� ����ϸ�, ���� �������� View�� ������ ',' delimiter��
 * 			 Text ������ ���·� �����Ѵ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ReportAction implements Action {
	/**
	 * Logger for this class
	 */
	static final Logger logger = Logger.getLogger(ReportAction.class);
	
	/**
	 * ���� write �ÿ� Race Condition�� �����ϱ� ���� Lock 
	 */
	static Object lock = new Object();

	/**
	 * ���� ���� ���� ���� ���� 
	 */
	public static final String RESP_REPORT = "resp";
	
	/**
	 * ���� ���� ���� ���� ���� 
	 */
	public static final String SEND_REPORT = "send";

	/**
	 * ���� ���� �׸� ����Ʈ 
	 */
	public static final String RESP_TAG = "Time, FileName, DocumentID, Result, Description";
	
	/**
	 * ���� ���� �׸� ����Ʈ  
	 */
	public static final String SEND_TAG = "Time, FileName, DocumentID, Result, Description";
	
	/**
	 * Esb Check �׸� ����Ʈ
	 */
	public static final String STATUSLOG_TAG = "Time, Result, Description";
	
	/**
	 * ��� OK
	 */
	public static final String RESULT_OK = "OK";
	
	/**
	 * ��� ERROR
	 */
	public static final String RESULT_ERROR = "ERROR";
	
	/**
	 * ���� ������ ���� �������� ���� ������ ���� ���������� ���� �ʵ� ��
	 */
	private String reportType;
	
	/**
	 * ���� ����/���� ���� ���� ��.
	 * ���� �̸����� ���ؼ� ������ �ڵ带 ȹ���Ѵ�.
	 */
	private String filename;
	
	/**
	 * ���� ����/���� ���� ���� ��ȣ
	 */
	private String documentId;
	
	/**
	 * ���� ���ں� �̷� ������ ���� �ʿ��ϴ�.
	 */
	private String reportDate;

	/**
	 * ��� ���� 'OK'/'ERROR'
	 */
	private String result;
	
	/**
	 * �޼��� 
	 */
	private String description;


	/**
	 *  ������. 
	 *  ���� ���� �۽� �� ���� ���� ���ſ� ���� ���� ������ �������� view�� ������ 
	 *  CSV ���� ���·� �����Ѵ�.
	 *  filename���� sid ���� 
	 * @param reportType ���� �������� ���� ���������� ���� ���� �� - RESP_REPORT/SEND_REPORT
	 * @param errorMsg	���� ����
	 */
	public ReportAction(String reportType,  String errorMsg) {
		this(reportType, CtrAgentEnvInfo.RCV_FILE_SAMPLE, CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE
				, ReportAction.RESULT_ERROR , errorMsg);		
	}
	
	/**
	 *  ������. 
	 *  ���� ���� �۽� �� ���� ���� ���ſ� ���� ���� ������ �������� view�� ������ 
	 *  CSV ���� ���·� �����Ѵ�.
	 *  filename���� sid ���� 
	 * @param reportType ���� �������� ���� ���������� ���� ���� �� - RESP_REPORT/SEND_REPORT
	 * @param errorMsg	���� ����
	 */
	public ReportAction(String reportType,  String resultCode, String resultMessage) {
		this(reportType, CtrAgentEnvInfo.RCV_FILE_SAMPLE, CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE
				, resultCode , resultMessage);		
	}

	/**
	 *  ������. 
	 *  ���� ���� �۽� �� ���� ���� ���ſ� ���� ���� ������ �������� view�� ������ 
	 *  CSV ���� ���·� �����Ѵ�.
	 *  filename���� sid ���� 
	 * @param reportType ���� �������� ���� ���������� ���� ���� �� - RESP_REPORT/SEND_REPORT
	 * @param filename ���� ����/���� ���� ���� ��
	 * @param documentId ���� ����/���� ���� ���� ��ȣ 
	 * @param result	��/���� ��� - RESULT_OK/RESULT_ERROR
	 */
	public ReportAction(String reportType, String filename, String documentId, String reportDate) {
		this(reportType, filename, documentId, reportDate , ReportAction.RESULT_OK , "");
	}

	/**
	 *  ������. 
	 *  ���� ���� �۽� �� ���� ���� ���ſ� ���� ���� ������ �������� view�� ������ 
	 *  CSV ���� ���·� �����Ѵ�.
	 *  filename���� sid ���� 
	 * @param reportType ���� �������� ���� ���������� ���� ���� �� - RESP_REPORT/SEND_REPORT
	 * @param filename ���� ����/���� ���� ���� ��
	 * @param documentId ���� ����/���� ���� ���� ��ȣ 
	 * @param result	��/���� ��� - RESULT_OK/RESULT_ERROR
	 * @param description ��� �޼��� 
	 */
	public ReportAction(String reportType, String filename, String documentId, String result, String description) {
		this(reportType , filename , documentId , "", result , description );
	}

	/**
	 * ������.  
	 * ���� ���ں� ����Ʈ ���� ������ ���ؼ� �߰���. 2006/01/06
	 * @param reportType
	 * @param filename
	 * @param documentId
	 * @param reportDate
	 * @param result
	 * @param description
	 */
	public ReportAction(String reportType, String filename, String documentId, String reportDate, String result, String description) {
		this.reportType = reportType;
		this.filename = filename;
		this.documentId = documentId;
		this.reportDate = reportDate;
		this.result = result;
		this.description = description;
	}

	/**
	 * ���� ������ ����Ѵ�. 
	 */
	public void doAct() {
		String msg = "";
		if(Controller.CHECKAGENT.equals(reportType) ||	Controller.CTRMODULEUPDATE.equals(reportType)
				|| Controller.HEARTBEAT.equals(reportType)){
			if (result != null && result.length() > 0 ) {
				msg += "\"" + result + "\"";
			}
			msg +=",";
			if (description != null && description.length() > 0 ) {
				msg += "\"" + description + "\"";
			}
		}else{
			if (filename != null && filename.length() > 0 ) {
				msg += "\"" + filename + "\"";
			}
			msg +=",";
			if (documentId != null && documentId.length() > 0 ) {
				msg += "\"" + documentId + "\"";
			}
			msg +=",";
			if (result != null && result.length() > 0 ) {
				msg += "\"" + result + "\"";
			}
			msg +=",";
			if (description != null && description.length() > 0 ) {
				msg += "\"" + description + "\"";
			}
		}
		
		write(msg);
	}

	
	/**
	 * ���� ������ ����Ѵ�. 
	 * @param msg ���Ͽ� �� �ؽ�Ʈ ����Ÿ
	 */
	private void write(String msg) {
		synchronized(lock) {
			//15.03.17 ���� ���� (log Time �� As-IS�� ����).
			String logMsg = "\"" + DateUtil.getDateTime(DateUtil.HH_mm_ss) + "\" ," + msg + "\r\n";
			try {
				File f = getFile();
				FileOutputStream fout = new FileOutputStream(f, true);
				fout.write(logMsg.getBytes());
				fout.close();
			} catch (IOException e) {
				// Disk Full ��Ȳ �߻� 
				if ( Utility.isDiskFull(e) ) {
					new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
							, "���� ���� ���� �� CSV ����Ʈ �α׸� ��� �߿� DISK FULL�� ���� �ŷ� ����� ���� ���Ͽ����ϴ�.\n" 
							 + logMsg , e).doAct();
					new ShutdownAction().doAct();
				}
				logger.error("���� ���� �۽� ���� CSV ���� ��� ����. �޼��� : " + logMsg, e);
			}
		}
	}

	/**
	 * ���� ������ ����� ������ ���´�. ��¥�� üũ�Ͽ� ���� ���ο� ���Ͽ� ��ϵǵ��� �Ѵ�.  
	 * @return ���� ������ ����� File�� �����Ѵ�. 
	 * @throws IOException
	 */
	private File getFile() throws IOException {
		String timeFormat = null;
		if ( Configure.getInstance().getAgentInfo().isCsvPerRptDate() ) {
			// ���� �̷��� ���� ���ں��� ���� 
			timeFormat = this.reportDate;
		} else {
			// �ý��� ��¥���� ���� 
			//timeFormat = DateUtil.getDateTime(DateUtil.yyyyMMdd)+"_";
			timeFormat = DateUtil.getDateTime(DateUtil.yyyyMMdd);
		}
		
		String rptFileName ;
		
		if(Controller.CHECKAGENT.equals(reportType) || Controller.HEARTBEAT.equals(reportType) ||
				Controller.CTRMODULEUPDATE.equals(reportType)){
			rptFileName = Controller.LOG_PATH + File.separator + "ctr_" + timeFormat +reportType.toLowerCase() + "_[1].csv";
			
			File f = createReport(rptFileName);
			
			return f;
		}else{
			String fclty_cd = "0000";
			if ( filename != null )
				fclty_cd = filename.substring(4, 8) ; // from file name
			
			
			if ( SEND_REPORT.equalsIgnoreCase(reportType))
				rptFileName = CtrAgentEnvInfo.REPORT_SEND_DIR_NAME + File.separator + fclty_cd + "_" + timeFormat + "_[1].csv";
			else
				rptFileName = CtrAgentEnvInfo.REPORT_RESP_DIR_NAME + File.separator + fclty_cd + "_" + timeFormat + "_[1].csv";
			
			File f = createReport(rptFileName);
				
			return f;
		}		
	 }
	
	/**
	 * 
	 * @param f
	 * @throws IOException
	 */
	private File createReport( String rptFileName ) throws IOException{
		File f = new File(rptFileName);
		// ���� ������ üũ - 2.5M 
		while( f.exists() && f.length() > 2500*1000) {
			rptFileName = FileTool.incrementFileNumber(rptFileName);
			f = new File(rptFileName);
		}
		
		if ( !f.exists() ) {
			f.createNewFile();

			PrintWriter pw = new PrintWriter(new FileWriter(f, true));
			if ( SEND_REPORT.equalsIgnoreCase(reportType))
				pw.println(SEND_TAG);
			else if ( RESP_REPORT.equalsIgnoreCase(reportType))
				pw.println(RESP_TAG);
			else
				pw.println(STATUSLOG_TAG);
			
			pw.close();
		}

		return f;
	}
	
	
}
