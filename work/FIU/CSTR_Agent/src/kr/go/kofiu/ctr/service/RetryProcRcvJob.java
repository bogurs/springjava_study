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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : INBOX_ERROR 폴더에 있는 파일을 처리한다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * 수   정   일  : 2014. 11. 8 
 * 수 정 내 용   : FIU 11차에서 접수증서는 암호화되어 들어오지 않습니다.
 * 				따라서 복호화과정에서 재처리를 해야하는 작업이 불필요 하므로 사용하지 않습니다.
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class RetryProcRcvJob extends ScheduleJob  {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RetryProcRcvJob.class);

	/**
	 * FileIOException이 발생하는 데, 특별히 에러 처리 하지 않는다. 
	 * 즉, 이 job은 실행을 중지한다.
	 * 다음 스케줄링 이벤트가 발생할 경우에 다시 실행된다.
	 * 성공적으로 실행 되면 마지막 코드 라인에서 해당 파일을 삭제하게 되므로 
	 * 다음 실행에서는 해당 파일이 처리 되지 않는다.
	 * @throws  
	 * @throws IOException
	 */
	public void doJob(JobExecutionContext context) throws AgentException {

		// 보고 문서 파일 목록 
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
				
			/* 2011 보안모듈 고도화
			   GpkiSDKException은 사용하지 않기로 한다.
			   DecryptionException로 대체 한다.
			}catch (GpkiSDKException e) {
				logger.error("returned value : " + e.returnedValue(), e);
			} */
			}catch (DecryptionException e) {
				logger.error("returned value : ", e);
			}catch (XmlException e) {
				logger.error("RetryInbox 접수 증서 " + files[j].getName() + " XML 변환 과정에서 오류 발생", e);
				continue;
			} catch (IOException e) {
				logger.error("RetryInbox 접수 증서 " + files[j].getName() + " XML 변환 과정에서 오류 발생", e);
				continue;
			} catch (GpkiApiException e) {
				logger.error("returned value : " + e.getMessage(), e);
				continue;
			}
		    
		    String rcvFile = files[j].getName();
			new ReportAction(ReportAction.RESP_REPORT, rcvFile, documentId, reportDate, 
					ReportAction.RESULT_OK, "복호화 오류 발생으로 재처리").doAct();
			
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
