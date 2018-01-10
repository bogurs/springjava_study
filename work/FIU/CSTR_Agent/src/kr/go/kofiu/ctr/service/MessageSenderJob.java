package kr.go.kofiu.ctr.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.ctr.actions.FatalActionSet;
import kr.go.kofiu.ctr.actions.SendActionSet;
import kr.go.kofiu.ctr.actions.TestSendReceiveActionSet;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.util.CTRFileFilter;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.FlatFileChecker;
import kr.go.kofiu.ctr.util.FlatFileConvertingException;
import kr.go.kofiu.ctr.util.FlatFileToXML;
import kr.go.kofiu.ctr.util.FlatFileToXML089;
import kr.go.kofiu.ctr.util.RetryException;
import kr.go.kofiu.ctr.util.SecuKit;
import kr.go.kofiu.ctr.util.SecurityUtil;
import kr.go.kofiu.ctr.util.TypeCode;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

import com.gpki.gpkiapi.exception.GpkiApiException;


//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
//song import com.gpki.sdk.bs.GpkiSDKException;
import kr.go.kofiu.ctr.util.excption.DecryptionException;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import krGovKofiuDataRootSchemaModule10.CurrencyTransactionReportDocument;

/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서를 집중기관에 전송하는 서비스
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class MessageSenderJob extends RetryJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(MessageSenderJob.class);

	/**
	 * Sending Message Queue
	 */
	private static Set FILE_QUEUE = Collections.synchronizedSet(new HashSet());

	/**
	 * 보고 문서를 집중 기관에 전송한다. FlatFile을 ebXML문서로 변환 오류 시 오류를 기록하고 ERROR 폴더로 이동한다.
	 * 집중 기관에 전송 중 오류 발생 시(RetryException)에는 오류를 기록하고 관리자에 메일을 전송한 후 Shutdown한다.
	 */
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// 보고 문서 파일 목록
//		logger.info("MessageSenderJob Processing...");
		File[] files = new File(CtrAgentEnvInfo.PROC_SEND_DIR_NAME)
				.listFiles(new CTRFileFilter());
		
		for (int j = 0; j < files.length; j++) {
			if (FILE_QUEUE.size() > 2000) {
				logger.warn("CTR 보고 문서 송신 대기 중인 문서가 2000건을 넘었습니다. 기존 파일 처리 전까지 잠시 기다립니다.");
				return;
			}

			if (!FILE_QUEUE.add(files[j].getName())) {
				// logger.debug("이미 처리 대기 중인 파일 - " + files[j].getName());
				continue;
			}
			logger.info(files[j].getName() + " 보고 문서 전송 큐에 등록");
			MessageSenderWorker worker = new MessageSenderWorker(context,
					files[j]);
			
			CTRAgent.getInstance().getExecutor().execute(worker);
		
		} // end of files loop

		// logger.debug("locked File Set : " + FILE_QUEUE);
	}

	/**
	 ******************************************************
	 * <pre>
	 * 업무   그룹명  : CTR 시스템
	 * 서브   업무명  : 보고 기관 Agent
	 * 설        명  : 보고 문서 전송을 위한 Worker Job 스레드
	 * 작   성   자  : 최중호 
	 * 작   성   일  : 2005. 11. 3
	 * copyright @ SK C&C. All Right Reserved
	 * 
	 * <pre>
	 ******************************************************
	 */
	class MessageSenderWorker implements Runnable {

		private JobExecutionContext context;
		private File file;

		public MessageSenderWorker(JobExecutionContext context, File file) {
			this.context = context;
			this.file = file;
		}

		public void run() {
			logger.info(file + " 전송 시작");
			try {
				/*
				 * 경쟁 조건으로 인해 이 부분에서 파일 존재여부 추가 체크. 시나리오. 1. 새로운 MessageSender
				 * 스레드 시작. 파일 목록 읽음. 2. MessageWorker스레드가 작업 끝냄. 파일 목록 작업 큐에서 삭제
				 * 3. MessageSender 스레드 파일 작업 큐에 작업 끝난 파일 명 등록. 4. File 오류 발생
				 */
				if (!file.exists()) {
					return;
				}

				if (!isOK(file.getName().substring(4, 8))) return;
				logger.info(file.getName() + " 파일은 "
						+ FileTool.getFileString(file).split("\\|\\|").length
						+ "개의 필드를 가지고 있습니다.");
				// Flatfile데이타를 XML로 변환하고 암호화
				// v0.88의 경우
				if (FileTool.getFileString(file).split("\\|\\|").length == 53) {
					logger.info("v0.88을 사용한 방법");
					MessageContext ctx = makeContext();
					// 테스트 여부
					if (Configure.getInstance().getAgentInfo().isTest()) {
						// move ARCH/send , Generate SNR, Report, 접수 증서 생성
						new TestSendReceiveActionSet(file).doAct();

						MessageSenderCommand command = new MessageSenderCommand(ctx);
						//command.sendSndfileToEsb();

						logger.info(file + " 보고 문서 전송 성공");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
						}
					} else {
						// sending
						MessageSenderCommand command = new MessageSenderCommand(ctx);
						String result = command.sendSndfileToEsb();
						logger.info("발송증서 처리 결과 : " + result);
						if (TypeCode.CGCommand.MODULE_VER_INVALID
								.equals(result)) {
							// update call
							try {
								logger.info("CTR 연계 모듈 업데이트가 필요합니다. 모듈 업데이트 프로세스를 시작 합니다.");
								context.getScheduler()
										.triggerJob(
												"kr.go.kofiu.ctr.service.AgentUpdateJob.job",
												Scheduler.DEFAULT_GROUP);
							} catch (Exception e) {
								// ignore
								logger.error(e, e);
							}
							return;
						} else if (TypeCode.CGCommand.MODULE_TIME_NOT_YET
								.equals(result)) {
							// not allowed, sleep 20 min
							try {
								logger.info("현재는 해당 보고 모듈에 허락된 전송 시간이 아닙니다. 조금만 기다려 주십시요.");
								Thread.sleep(20 * 60 * 1000);
							} catch (Exception e) {
								// ignore
							}
						} else if (TypeCode.CGCommand.OK.equals(result)) {
							// move ARCH/send , Generate SNR, Report
							new SendActionSet(file).doAct();
							logger.info(file + " 보고 문서 전송 성공");
						} else {
							new SendActionSet(file,	TypeCode.ErrorCode.ETC_ERROR, result).doAct();
							logger.info(file + " 보고 문서 전송 실패, " + result);
						}
					}
				}

				// v0.89의 경우
				else if (FileTool.getFileString(file).split("\\|\\|").length == 84) {
					logger.info("v0.89을 사용한 방법");
					MessageContext089 ctx = makeContext089();
					// 테스트 여부
					if (Configure.getInstance().getAgentInfo().isTest()) {
						// move ARCH/send , Generate SNR, Report, 접수 증서 생성
						new TestSendReceiveActionSet(file).doAct();

						MessageSenderCommand089 command = new MessageSenderCommand089(ctx);
						//command.sendSndfileToEsb();

						logger.info(file + " 보고 문서 전송 성공");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
						}
					} else {
						// sending
						MessageSenderCommand089 command = new MessageSenderCommand089(ctx);
						String result = command.sendSndfileToEsb();
						logger.info("발송증서 처리 결과 : " + result);
						if (TypeCode.CGCommand.MODULE_VER_INVALID
								.equals(result)) {
							// update call
							try {
								logger.info("CTR 연계 모듈 업데이트가 필요합니다. 모듈 업데이트 프로세스를 시작 합니다.");
								context.getScheduler()
										.triggerJob(
												"kr.go.kofiu.ctr.service.AgentUpdateJob.job",
												Scheduler.DEFAULT_GROUP);
							} catch (Exception e) {
								// ignore
								logger.error(e, e);
							}
							return;
						} else if (TypeCode.CGCommand.MODULE_TIME_NOT_YET
								.equals(result)) {
							// not allowed, sleep 20 min
							try {
								logger.info("현재는 해당 보고 모듈에 허락된 전송 시간이 아닙니다. 조금만 기다려 주십시요.");
								Thread.sleep(20 * 60 * 1000);
							} catch (Exception e) {
								// ignore
							}
						} else if (TypeCode.CGCommand.OK.equals(result)) {
							// move ARCH/send , Generate SNR, Report
							new SendActionSet(file).doAct();
							logger.info(file + " 보고 문서 전송 성공");
						} else {
							new SendActionSet(file,
									TypeCode.ErrorCode.ETC_ERROR, result)
									.doAct();
							logger.info(file + " 보고 문서 전송 실패, " + result);
						}
					}
				} else {
					logger.error(file.getName()
							+ " 파일은 v0.88 또는 v0.89 서식에 맞지 않는 파일입니다.");
//					return;
					throw new Exception(file.getName()
							+ " 파일은 v0.88 또는 v0.89 서식에 맞지 않는 파일입니다.");
				}
			} catch (FlatFileConvertingException e) {
				// Error While Converting FlatFile to ebXML
				// ignore, just skip, process next file
				logger.error("보고 문서를 XML형태로 변환하는 과정에서 오류가 발생하였습니다.", e);
				e.addAction(new SendActionSet(file,
						TypeCode.ErrorCode.FILE_DATA_ERROR, e.getMessage()));
				e.fireAction();
			} catch (RetryException e) {
				// 전송 문서 헤더 값 추가시 오류 , CTRService lookup, method 호출 실패
				// Internal Server Error(FIURemoteException)
				// 10 분간 대기
				int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
				String retryTime = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_COUNT();
			    String waitTime = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_WAIT_TIME();
				
				String error_msg = "보고 문서 접수 서비스(MessageSending) "+waitTime+"초간 "+retryTime+"회 재시도를 하였으나 응답받지 못하여 연계모듈을 "+sleepTime+"초간 중지합니다.";
				
				new SendActionSet( file, TypeCode.ErrorCode.ETC_ERROR, error_msg ).doErrorAct();
				
				logger.error("현재 중계 기관의  보고 문서 접수 서비스(MessageSending) 제공에 문제가 있습니다. 연계모듈을 "+sleepTime+"초간 중지합니다.");
				CTRAgent.getInstance().sleepAgent(sleepTime);
				
				
				
				
				
				
			/*
			 * 2011 보안모듈 고도화 GpkiSDKException은 사용하지 않기로 한다.
			 * DecryptionException로 대체 한다. } catch(GpkiSDKException e ){
			 * logger.error("암호화 실행 중 다음과 같은 오류가 발생하였습니다. 에러 코드 - " +
			 * e.returnedValue(), e); new SendActionSet(file,
			 * TypeCode.ErrorCode.ETC_ERROR,
			 * "암호화 실행 중 다음과 같은 오류가 발생하였습니다. 에러 코드 - " + e.returnedValue() +
			 * "\n" + e.getMessage()).doAct();
			 */
				
			} catch (DecryptionException e) {
				logger.error("암호화 실행 중 다음과 같은 오류가 발생하였습니다. - ", e);
				new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR,
						"암호화 실행 중 다음과 같은 오류가 발생하였습니다.\n" + e.getMessage())
						.doAct();
			} catch (UnsatisfiedLinkError e) {
				// Error While Converting FlatFile to ebXML. ignore...
				logger.error(e, e);
				new FatalActionSet(file, "암호화 native 라이브러리가  설치되어 있지 않습니다.", e)
						.doAct();
			} catch (Throwable t) {
				// Error While Converting FlatFile to ebXML. ignore...
				logger.error(t, t);
				new SendActionSet(file, TypeCode.ErrorCode.FILE_DATA_ERROR,
						t.getMessage()).doAct();
			} finally {
				FILE_QUEUE.remove(file.getName());
			}
		}
		
		/*public synchronized void processSend(){
			
		}*/

		/**
		 * Flatfile데이타를 XML로 변환하고 암호화하여 MessageContext에 담아 리턴한다.
		 * 
		 * @param file
		 * @return
		 * @throws IOException
		 * @throws AgentException
		 * @throws FlatFileConvertingException
		 * @throws GpkiApiException
		 */
		private MessageContext makeContext() throws IOException,
				AgentException, FlatFileConvertingException, GpkiApiException {
			// CurrencyTransactionReportDocument 객체 생성시 입력 값에 대해서 자리수 및 포맷 체킹

			FlatFileToXML converter = new FlatFileToXML(FileTool.getFileString(file), file.getName());
			CurrencyTransactionReportDocument xmlDoc = converter.ChkAndGetXMLDoc();

			// 암호화 시작
			String xmlStr = xmlDoc.xmlText();
			xmlStr = "<?xml version=\"1.0\" encoding=\"EUC-KR\"?>" + xmlStr;

			// 암호화 및 서명에 대한 설정을 따름
			byte[] encryptedMsg = SecurityUtil.encrypt(xmlStr.getBytes());
			
			/**
			 * 2014. 9. 12 추가 Base64 Encoding
			 */	
			boolean isSigning = Configure.getInstance().getEncryptionSigningInfo().isEnabled();
			if(!isSigning){ //전자서명이 되었을 때는 base64를 진행하지 않습니다.
				encryptedMsg = Base64.encodeBase64(encryptedMsg);
			}
			
			MessageContext ctx = new MessageContext(xmlDoc, encryptedMsg,
					file.getName(), Configure.getInstance().getAgentInfo()
							.getId(), Configure.getInstance()
							.getEncryptionInfo().isEnabled(), Configure
							.getInstance().getEncryptionSigningInfo()
							.isEnabled());

			return ctx;
		}

		private MessageContext089 makeContext089() throws IOException,
				AgentException, FlatFileConvertingException, GpkiApiException {
			// CurrencyTransactionReportDocument 객체 생성시 입력 값에 대해서 자리수 및 포맷 체킹

			FlatFileToXML089 converter = new FlatFileToXML089(
					FileTool.getFileString(file), file.getName());
			krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportDocument xmlDoc = converter
					.ChkAndGetXMLDoc();

			// 암호화 시작
			String xmlStr = xmlDoc.xmlText();
			xmlStr = "<?xml version=\"1.0\" encoding=\"EUC-KR\"?>" + xmlStr;

			// depends on encrytpion, signing configuration
			byte[] encryptedMsg = SecurityUtil.encrypt(xmlStr.getBytes());

			/**
			 * 2014. 9. 12 추가 Base64 Encoding
			 */
			boolean isSigning = Configure.getInstance().getEncryptionSigningInfo().isEnabled();
			if(!isSigning){ //전자서명이 되었을 때는 base64를 진행하지 않습니다.
				encryptedMsg = Base64.encodeBase64(encryptedMsg);
			}
			String test1 = new String(encryptedMsg);
			MessageContext089 ctx = new MessageContext089(xmlDoc, encryptedMsg,
					file.getName(), Configure.getInstance().getAgentInfo()
							.getId(), Configure.getInstance()
							.getEncryptionInfo().isEnabled(), Configure
							.getInstance().getEncryptionSigningInfo()
							.isEnabled());

			return ctx;
		}

		/**
		 * Flatfile 데이타가 형식적으로 올바른지 체크한다. (파일명 검증 , CTREND 체크)
		 * 
		 * @param file
		 * @return
		 * @throws FlatFileConvertingException
		 */
		private boolean isOK(String orgCd) {
			try {
				FlatFileChecker checker = new FlatFileChecker(file);
				// 파일명이 올바른지 체크함.
				int result = checker.isValid();

				// if invalid
				if (FlatFileChecker.OK != result
						&& FlatFileChecker.CTREND_INVALID != result) {
					// Error While Converting FlatFile to ebXML
					// ignore, just skip, process next file
					logger.error("보고 문서 형식 오류 - " + checker.toString());
					new SendActionSet(file, TypeCode.ErrorCode.FILE_DATA_ERROR,
							checker.toString()).doAct();
					return false;
				}

				// 파일 존재 여부 체크
				String date = CurrentTimeGetter.formatDate();
				String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
				String arch_dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType,CtrAgentEnvInfo.getSendDirName(),orgCd,date);
				String arch_send = arch_dir + File.separator
						+ file.getName();
				File arch_send_file = new File(arch_send);
				boolean ex = arch_send_file.exists();
				if (arch_send_file.exists()) {
					logger.error(file.getName() + " 파일이 이미 처리 되었습니다.");
					new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR,
							file.getName() + " 파일이 이미 처리 되었습니다.").doAct();
					return false;
				}
			} catch (IOException e) {
				logger.error(e);
			}

			return true;
		}

	} // end of MessageSenderWorker class
}
