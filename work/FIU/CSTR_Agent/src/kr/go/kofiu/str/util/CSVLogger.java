package kr.go.kofiu.str.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.util.Utility;
import kr.go.kofiu.str.conf.STRConfigure;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.gpki.gpkiapi.exception.GpkiApiException;

//import fiuft.str.userclass.common.util.DateUtil;

public class CSVLogger {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public static final String HEARTBEAT_LOG = "HEARTBEAT_LOG";
	public static final String CHECKAGENT_LOG = "CHECKAGENT_LOG";
	public static final String USERCHECK_LOG = "USERCHECK_LOG";
	public static final String PROCESS_LOG = "PROCESS_LOG";
	public static final String MODULEUPDATE_LOG = "MODULEUPDATE_LOG";
	
	
	/**
	 * STR Remote Adapter가 남기는 로그 파일인 CSV 파일을 새로이 생성한다.
	 * @param orgCd 기관코드(CSV 파일명의 일부로  쓰임)
	 * @param sendCert 발송 관련 로그를 남기는 목적일 경우 true
	 * @param svrType 어떤 서비스를 호출하였는지 명시
	 * @return
	 * @throws IOException
	 */
	public File createCsv(String orgCd, boolean sendCert, String svrType, String date)
			throws IOException {
		int cnt = 0;
		File csvFile = null;		
		
		String csvFileNm = "logs/";
		if(svrType.equals(Controller.HEARTBEAT) ){
			csvFileNm = csvFileNm +  "str_" + CurrentTimeGetter.formatDate() + "_" + Controller.HEARTBEAT.toLowerCase() + "_";
		}else if(svrType.equals(Controller.CHECKAGENT) ){
			csvFileNm = csvFileNm +  "str_" + CurrentTimeGetter.formatDate() + "_" + Controller.CHECKAGENT.toLowerCase() + "_";
		}else if(svrType.equals(Controller.STRMODULEUPDATE) ){
			csvFileNm = csvFileNm +  "str_" + CurrentTimeGetter.formatDate() + "_" + Controller.STRMODULEUPDATE.toLowerCase() + "_";
		}else if(svrType.equals(Controller.USERCHECK) ){
			csvFileNm = csvFileNm +  "str_" + CurrentTimeGetter.formatDate() + "_" + Controller.USERCHECK.toLowerCase() + "_";
		}else{
			if (sendCert) {
				csvFileNm = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getREPORT_SEND() + "/";
			} else {
				csvFileNm = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getREPORT_RCV() + "/";
			}
			
			// CsvPerRptDate 값이 true면 파일명에 날짜를 넣는다
			if(STRConfigure.getInstance().getAgentInfo().isCsvPerRptDate()){
				csvFileNm = csvFileNm + orgCd + "_"	+ date + "_";
			}else{
				csvFileNm = csvFileNm + orgCd + "_"	+ CurrentTimeGetter.formatDate() + "_";
			}
		}
		
		
		// 파일용량이 2.5M 이상일때 다음번호로 파일명을 차번
		do {
			cnt++;			
			csvFile = new File(csvFileNm + "[" + cnt + "]" + ".csv");
			
			if (csvFile.exists()) {
				if ((2.5 * 1024 * 1024) > csvFile.length()) {
					break;
				}
			} else {
				csvFile = this.makeCSVFile(csvFile);
				PrintWriter pw = new PrintWriter(new FileWriter(csvFile));
				pw.print("Time,FileName,DocumentID,RESULT,Description\r\n");
				pw.close();
				break;
			}
		} while (true);
		return csvFile;
	}

	
	private synchronized void write(File file, byte[] data) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(file, true));
		String csvString = new String(data);
		pw.print(csvString);
		//logger.info("csv 저장 성공 - " + csvString);
		pw.close();
	}

	/**
	 * data를 CSV 파일에 기록한다. 필요 시 새로운 CSV를 생성한다.
	 * 
	 * @param data
	 *            CSV 내용
	 * @param path
	 *            CSV 파일 경로
	 * @param orgCd
	 *            기관 코드
	 * @param sendCert
	 *            STR 수신 I/F 여부. true면 보고 기관->FIU인 STR 전송 성공/실패에 관한 내용임
	 * @throws IOException
	 *             설정 파일, CSV 파일 I/O 수행 중 오류 발생
	 * @throws SAXException
	 *             Configuration을 읽다 오류 발생
	 * @throws GpkiApiException
	 *             GPKI API에서 오류 발생
	 */
	public void writeCsv(byte[] data, String svrType, String orgcd, boolean sendCert, String date)  {
		File file;
		try {
			file = this.createCsv(orgcd, sendCert, svrType, date);
			this.write(file, data);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				logger.info("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다." + "보고 문서 전송 후 CSV 리포트 로그를 기록 중에 DISK FULL로 인해 거래 기록을 쓰지 못하였습니다.\n" + new String(data) );
				new ShutdownAction().doAct();
			}
			logger.error("보고 문서 송신 내역 CSV 파일 기록 실패. 메세지 : " +  new String(data), e);
		}
	}

	/**
	 * java.io.File 객체의 createNewFile()은 부모 디렉토리가 없을 경우 IOException을 발생시킨다. 이
	 * 메소드는 부모 디렉토리가 없을 경우 부모 디렉토리를 생성한 다음 csv 파일을 만든다.
	 * 
	 * @param csv
	 * @return
	 * @throws IOException
	 */
	private File makeCSVFile(File csv) throws IOException {
		File parentDir = null;

		if (null != csv) {
			parentDir = csv.getParentFile();

			if (null != parentDir && !parentDir.exists() && parentDir.mkdirs()) {
				csv.createNewFile();
			}
		}

		return csv;
	}
}
