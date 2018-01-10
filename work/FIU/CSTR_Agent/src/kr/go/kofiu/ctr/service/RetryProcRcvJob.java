package kr.go.kofiu.ctr.service;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.util.CTRFileFilter;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.SecurityUtil;
import kr.go.kofiu.ctr.util.XMLtoFlatFile;
//song import com.gpki.sdk.bs.GpkiSDKException;
import kr.go.kofiu.ctr.util.excption.DecryptionException;
import krGovKofiuDataRootSchemaModule10.ResponseCurrencyTransactionReportDocument;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.quartz.JobExecutionContext;

import com.gpki.gpkiapi.exception.GpkiApiException;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : INBOX_ERROR ������ �ִ� ������ ó���Ѵ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * ��   ��   ��  : 2014. 11. 8 
 * �� �� �� ��   : FIU 11������ ���������� ��ȣȭ�Ǿ� ������ �ʽ��ϴ�.
 * 				���� ��ȣȭ�������� ��ó���� �ؾ��ϴ� �۾��� ���ʿ� �ϹǷ� ������� �ʽ��ϴ�.
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class RetryProcRcvJob extends ScheduleJob  {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RetryProcRcvJob.class);

	/**
	 * FileIOException�� �߻��ϴ� ��, Ư���� ���� ó�� ���� �ʴ´�. 
	 * ��, �� job�� ������ �����Ѵ�.
	 * ���� �����ٸ� �̺�Ʈ�� �߻��� ��쿡 �ٽ� ����ȴ�.
	 * ���������� ���� �Ǹ� ������ �ڵ� ���ο��� �ش� ������ �����ϰ� �ǹǷ� 
	 * ���� ���࿡���� �ش� ������ ó�� ���� �ʴ´�.
	 * @throws  
	 * @throws IOException
	 */
	public void doJob(JobExecutionContext context) throws AgentException {

		// ���� ���� ���� ��� 
		File[] files = new File(CtrAgentEnvInfo.PROC_RECEIVE_DIR_NAME).listFiles(new CTRFileFilter());
		
		for(int j=0; j < files.length; j++) {
			logger.debug("Retry file : " + files[j].getName() + " checking....");
			
		    String flatfileText = null;
		    String documentId = null;
		    String reportDate = null; 
			try {
				byte[] encrtypedMsg = FileTool.getFileByte(files[j]);
				
				byte[] original = SecurityUtil.decrypt( encrtypedMsg );
				
				ResponseCurrencyTransactionReportDocument respDoc = XMLtoFlatFile.generateResponseDoc(original);
				documentId = respDoc.getResponseCurrencyTransactionReport().getDocumentID().getStringValue();
				reportDate = respDoc.getResponseCurrencyTransactionReport().getDate();
				flatfileText = XMLtoFlatFile.convertToFlatFile(respDoc);
			    logger.debug("flatfile : " + flatfileText);
				
			/* 2011 ���ȸ�� ��ȭ
			   GpkiSDKException�� ������� �ʱ�� �Ѵ�.
			   DecryptionException�� ��ü �Ѵ�.
			}catch (GpkiSDKException e) {
				logger.error("returned value : " + e.returnedValue(), e);
			} */
			}catch (DecryptionException e) {
				logger.error("returned value : ", e);
			}catch (XmlException e) {
				logger.error("RetryInbox ���� ���� " + files[j].getName() + " XML ��ȯ �������� ���� �߻�", e);
				continue;
			} catch (IOException e) {
				logger.error("RetryInbox ���� ���� " + files[j].getName() + " XML ��ȯ �������� ���� �߻�", e);
				continue;
			} catch (GpkiApiException e) {
				logger.error("returned value : " + e.getMessage(), e);
				continue;
			}
		    
		    String rcvFile = files[j].getName();
			new ReportAction(ReportAction.RESP_REPORT, rcvFile, documentId, reportDate, 
					ReportAction.RESULT_OK, "��ȣȭ ���� �߻����� ��ó��").doAct();
			
			try {
				if ( Configure.getInstance().getAgentInfo().isInboxEnabled()) { 
					FileTool.writeToFile(CtrAgentEnvInfo.INBOX_DIR_NAME + File.separator + rcvFile, flatfileText);
				}
			
				FileTool.writeToFile(CtrAgentEnvInfo.getReceiveDirName() + File.separator + rcvFile, flatfileText);
				files[j].delete();
			} catch (IOException e) {
				throw new AgentException(e);
			}
		}
	}


}
