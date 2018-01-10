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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 관리자가 손쉽게 확인 할 목적으로  보고 문서 송신 및 접수 증서 수신 시
 * 			 성공 및 오류 내역을 파일에 기록한다. 
 * 			보고서는 매일 날짜 별로 기록하며, 엑셀 서식으로 View가 가능한 ',' delimiter의
 * 			 Text 파일을 형태로 생성한다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ReportAction implements Action {
	/**
	 * Logger for this class
	 */
	static final Logger logger = Logger.getLogger(ReportAction.class);
	
	/**
	 * 파일 write 시에 Race Condition을 제어하기 위한 Lock 
	 */
	static Object lock = new Object();

	/**
	 * 접수 증서 보고 내역 생성 
	 */
	public static final String RESP_REPORT = "resp";
	
	/**
	 * 보고 문서 보고 내역 생성 
	 */
	public static final String SEND_REPORT = "send";

	/**
	 * 접수 증서 항목 리스트 
	 */
	public static final String RESP_TAG = "Time, FileName, DocumentID, Result, Description";
	
	/**
	 * 보고 문서 항목 리스트  
	 */
	public static final String SEND_TAG = "Time, FileName, DocumentID, Result, Description";
	
	/**
	 * Esb Check 항목 리스트
	 */
	public static final String STATUSLOG_TAG = "Time, Result, Description";
	
	/**
	 * 결과 OK
	 */
	public static final String RESULT_OK = "OK";
	
	/**
	 * 결과 ERROR
	 */
	public static final String RESULT_ERROR = "ERROR";
	
	/**
	 * 접수 증서에 대한 보고인지 보고 문서에 대한 보고인지에 대한 필드 값
	 */
	private String reportType;
	
	/**
	 * 보고 문서/접수 증서 파일 명.
	 * 파일 이름으을 통해서 보고기관 코드를 획득한다.
	 */
	private String filename;
	
	/**
	 * 보고 문서/접수 증서 문서 번호
	 */
	private String documentId;
	
	/**
	 * 보고 일자별 이력 관리를 위해 필요하다.
	 */
	private String reportDate;

	/**
	 * 결과 내역 'OK'/'ERROR'
	 */
	private String result;
	
	/**
	 * 메세지 
	 */
	private String description;


	/**
	 *  생성자. 
	 *  보고 문서 송신 및 접수 증서 수신에 대한 보고 내역을 엑셀에서 view가 가능한 
	 *  CSV 파일 형태로 저장한다.
	 *  filename으로 sid 추출 
	 * @param reportType 보고 문서인지 접수 증서인지에 대한 구별 값 - RESP_REPORT/SEND_REPORT
	 * @param errorMsg	오류 내용
	 */
	public ReportAction(String reportType,  String errorMsg) {
		this(reportType, CtrAgentEnvInfo.RCV_FILE_SAMPLE, CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE
				, ReportAction.RESULT_ERROR , errorMsg);		
	}
	
	/**
	 *  생성자. 
	 *  보고 문서 송신 및 접수 증서 수신에 대한 보고 내역을 엑셀에서 view가 가능한 
	 *  CSV 파일 형태로 저장한다.
	 *  filename으로 sid 추출 
	 * @param reportType 보고 문서인지 접수 증서인지에 대한 구별 값 - RESP_REPORT/SEND_REPORT
	 * @param errorMsg	오류 내용
	 */
	public ReportAction(String reportType,  String resultCode, String resultMessage) {
		this(reportType, CtrAgentEnvInfo.RCV_FILE_SAMPLE, CtrAgentEnvInfo.DOCUMENT_ID_SAMPLE
				, resultCode , resultMessage);		
	}

	/**
	 *  생성자. 
	 *  보고 문서 송신 및 접수 증서 수신에 대한 보고 내역을 엑셀에서 view가 가능한 
	 *  CSV 파일 형태로 저장한다.
	 *  filename으로 sid 추출 
	 * @param reportType 보고 문서인지 접수 증서인지에 대한 구별 값 - RESP_REPORT/SEND_REPORT
	 * @param filename 보고 문서/접수 증서 파일 명
	 * @param documentId 보고 문서/접수 증서 문서 번호 
	 * @param result	송/수신 결과 - RESULT_OK/RESULT_ERROR
	 */
	public ReportAction(String reportType, String filename, String documentId, String reportDate) {
		this(reportType, filename, documentId, reportDate , ReportAction.RESULT_OK , "");
	}

	/**
	 *  생성자. 
	 *  보고 문서 송신 및 접수 증서 수신에 대한 보고 내역을 엑셀에서 view가 가능한 
	 *  CSV 파일 형태로 저장한다.
	 *  filename으로 sid 추출 
	 * @param reportType 보고 문서인지 접수 증서인지에 대한 구별 값 - RESP_REPORT/SEND_REPORT
	 * @param filename 보고 문서/접수 증서 파일 명
	 * @param documentId 보고 문서/접수 증서 문서 번호 
	 * @param result	송/수신 결과 - RESULT_OK/RESULT_ERROR
	 * @param description 결과 메세지 
	 */
	public ReportAction(String reportType, String filename, String documentId, String result, String description) {
		this(reportType , filename , documentId , "", result , description );
	}

	/**
	 * 생성자.  
	 * 보고 일자별 리포트 파일 생성을 위해서 추가됨. 2006/01/06
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
	 * 보고 내역을 기록한다. 
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
	 * 보고 내역을 기록한다. 
	 * @param msg 파일에 쓸 텍스트 데이타
	 */
	private void write(String msg) {
		synchronized(lock) {
			//15.03.17 변경 사항 (log Time 을 As-IS로 변경).
			String logMsg = "\"" + DateUtil.getDateTime(DateUtil.HH_mm_ss) + "\" ," + msg + "\r\n";
			try {
				File f = getFile();
				FileOutputStream fout = new FileOutputStream(f, true);
				fout.write(logMsg.getBytes());
				fout.close();
			} catch (IOException e) {
				// Disk Full 상황 발생 
				if ( Utility.isDiskFull(e) ) {
					new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
							, "보고 문서 전송 후 CSV 리포트 로그를 기록 중에 DISK FULL로 인해 거래 기록을 쓰지 못하였습니다.\n" 
							 + logMsg , e).doAct();
					new ShutdownAction().doAct();
				}
				logger.error("보고 문서 송신 내역 CSV 파일 기록 실패. 메세지 : " + logMsg, e);
			}
		}
	}

	/**
	 * 보고 내역을 기록할 파일을 얻어온다. 날짜를 체크하여 매일 새로운 파일에 기록되도록 한다.  
	 * @return 보고 내역을 기록할 File을 리턴한다. 
	 * @throws IOException
	 */
	private File getFile() throws IOException {
		String timeFormat = null;
		if ( Configure.getInstance().getAgentInfo().isCsvPerRptDate() ) {
			// 문서 이력을 보고 일자별루 저장 
			timeFormat = this.reportDate;
		} else {
			// 시스템 날짜별로 저장 
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
		// 파일 사이즈 체크 - 2.5M 
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
