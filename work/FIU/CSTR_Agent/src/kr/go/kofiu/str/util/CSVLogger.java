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
	 * STR Remote Adapter�� ����� �α� ������ CSV ������ ������ �����Ѵ�.
	 * @param orgCd ����ڵ�(CSV ���ϸ��� �Ϻη�  ����)
	 * @param sendCert �߼� ���� �α׸� ����� ������ ��� true
	 * @param svrType � ���񽺸� ȣ���Ͽ����� ���
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
			
			// CsvPerRptDate ���� true�� ���ϸ� ��¥�� �ִ´�
			if(STRConfigure.getInstance().getAgentInfo().isCsvPerRptDate()){
				csvFileNm = csvFileNm + orgCd + "_"	+ date + "_";
			}else{
				csvFileNm = csvFileNm + orgCd + "_"	+ CurrentTimeGetter.formatDate() + "_";
			}
		}
		
		
		// ���Ͽ뷮�� 2.5M �̻��϶� ������ȣ�� ���ϸ��� ����
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
		//logger.info("csv ���� ���� - " + csvString);
		pw.close();
	}

	/**
	 * data�� CSV ���Ͽ� ����Ѵ�. �ʿ� �� ���ο� CSV�� �����Ѵ�.
	 * 
	 * @param data
	 *            CSV ����
	 * @param path
	 *            CSV ���� ���
	 * @param orgCd
	 *            ��� �ڵ�
	 * @param sendCert
	 *            STR ���� I/F ����. true�� ���� ���->FIU�� STR ���� ����/���п� ���� ������
	 * @throws IOException
	 *             ���� ����, CSV ���� I/O ���� �� ���� �߻�
	 * @throws SAXException
	 *             Configuration�� �д� ���� �߻�
	 * @throws GpkiApiException
	 *             GPKI API���� ���� �߻�
	 */
	public void writeCsv(byte[] data, String svrType, String orgcd, boolean sendCert, String date)  {
		File file;
		try {
			file = this.createCsv(orgcd, sendCert, svrType, date);
			this.write(file, data);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				logger.info("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�." + "���� ���� ���� �� CSV ����Ʈ �α׸� ��� �߿� DISK FULL�� ���� �ŷ� ����� ���� ���Ͽ����ϴ�.\n" + new String(data) );
				new ShutdownAction().doAct();
			}
			logger.error("���� ���� �۽� ���� CSV ���� ��� ����. �޼��� : " +  new String(data), e);
		}
	}

	/**
	 * java.io.File ��ü�� createNewFile()�� �θ� ���丮�� ���� ��� IOException�� �߻���Ų��. ��
	 * �޼ҵ�� �θ� ���丮�� ���� ��� �θ� ���丮�� ������ ���� csv ������ �����.
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
