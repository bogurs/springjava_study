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
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ������ ���߱���� �����ϴ� ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
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
	 * ���� ������ ���� ����� �����Ѵ�. FlatFile�� ebXML������ ��ȯ ���� �� ������ ����ϰ� ERROR ������ �̵��Ѵ�.
	 * ���� ����� ���� �� ���� �߻� ��(RetryException)���� ������ ����ϰ� �����ڿ� ������ ������ �� Shutdown�Ѵ�.
	 */
	public void doRetryJob(JobExecutionContext context) throws AgentException {
		// ���� ���� ���� ���
//		logger.info("MessageSenderJob Processing...");
		File[] files = new File(CtrAgentEnvInfo.PROC_SEND_DIR_NAME)
				.listFiles(new CTRFileFilter());
		
		for (int j = 0; j < files.length; j++) {
			if (FILE_QUEUE.size() > 2000) {
				logger.warn("CTR ���� ���� �۽� ��� ���� ������ 2000���� �Ѿ����ϴ�. ���� ���� ó�� ������ ��� ��ٸ��ϴ�.");
				return;
			}

			if (!FILE_QUEUE.add(files[j].getName())) {
				// logger.debug("�̹� ó�� ��� ���� ���� - " + files[j].getName());
				continue;
			}
			logger.info(files[j].getName() + " ���� ���� ���� ť�� ���");
			MessageSenderWorker worker = new MessageSenderWorker(context,
					files[j]);
			
			CTRAgent.getInstance().getExecutor().execute(worker);
		
		} // end of files loop

		// logger.debug("locked File Set : " + FILE_QUEUE);
	}

	/**
	 ******************************************************
	 * <pre>
	 * ����   �׷��  : CTR �ý���
	 * ����   ������  : ���� ��� Agent
	 * ��        ��  : ���� ���� ������ ���� Worker Job ������
	 * ��   ��   ��  : ����ȣ 
	 * ��   ��   ��  : 2005. 11. 3
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
			logger.info(file + " ���� ����");
			try {
				/*
				 * ���� �������� ���� �� �κп��� ���� ���翩�� �߰� üũ. �ó�����. 1. ���ο� MessageSender
				 * ������ ����. ���� ��� ����. 2. MessageWorker�����尡 �۾� ����. ���� ��� �۾� ť���� ����
				 * 3. MessageSender ������ ���� �۾� ť�� �۾� ���� ���� �� ���. 4. File ���� �߻�
				 */
				if (!file.exists()) {
					return;
				}

				if (!isOK(file.getName().substring(4, 8))) return;
				logger.info(file.getName() + " ������ "
						+ FileTool.getFileString(file).split("\\|\\|").length
						+ "���� �ʵ带 ������ �ֽ��ϴ�.");
				// Flatfile����Ÿ�� XML�� ��ȯ�ϰ� ��ȣȭ
				// v0.88�� ���
				if (FileTool.getFileString(file).split("\\|\\|").length == 53) {
					logger.info("v0.88�� ����� ���");
					MessageContext ctx = makeContext();
					// �׽�Ʈ ����
					if (Configure.getInstance().getAgentInfo().isTest()) {
						// move ARCH/send , Generate SNR, Report, ���� ���� ����
						new TestSendReceiveActionSet(file).doAct();

						MessageSenderCommand command = new MessageSenderCommand(ctx);
						//command.sendSndfileToEsb();

						logger.info(file + " ���� ���� ���� ����");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
						}
					} else {
						// sending
						MessageSenderCommand command = new MessageSenderCommand(ctx);
						String result = command.sendSndfileToEsb();
						logger.info("�߼����� ó�� ��� : " + result);
						if (TypeCode.CGCommand.MODULE_VER_INVALID
								.equals(result)) {
							// update call
							try {
								logger.info("CTR ���� ��� ������Ʈ�� �ʿ��մϴ�. ��� ������Ʈ ���μ����� ���� �մϴ�.");
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
								logger.info("����� �ش� ���� ��⿡ ����� ���� �ð��� �ƴմϴ�. ���ݸ� ��ٷ� �ֽʽÿ�.");
								Thread.sleep(20 * 60 * 1000);
							} catch (Exception e) {
								// ignore
							}
						} else if (TypeCode.CGCommand.OK.equals(result)) {
							// move ARCH/send , Generate SNR, Report
							new SendActionSet(file).doAct();
							logger.info(file + " ���� ���� ���� ����");
						} else {
							new SendActionSet(file,	TypeCode.ErrorCode.ETC_ERROR, result).doAct();
							logger.info(file + " ���� ���� ���� ����, " + result);
						}
					}
				}

				// v0.89�� ���
				else if (FileTool.getFileString(file).split("\\|\\|").length == 84) {
					logger.info("v0.89�� ����� ���");
					MessageContext089 ctx = makeContext089();
					// �׽�Ʈ ����
					if (Configure.getInstance().getAgentInfo().isTest()) {
						// move ARCH/send , Generate SNR, Report, ���� ���� ����
						new TestSendReceiveActionSet(file).doAct();

						MessageSenderCommand089 command = new MessageSenderCommand089(ctx);
						//command.sendSndfileToEsb();

						logger.info(file + " ���� ���� ���� ����");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
						}
					} else {
						// sending
						MessageSenderCommand089 command = new MessageSenderCommand089(ctx);
						String result = command.sendSndfileToEsb();
						logger.info("�߼����� ó�� ��� : " + result);
						if (TypeCode.CGCommand.MODULE_VER_INVALID
								.equals(result)) {
							// update call
							try {
								logger.info("CTR ���� ��� ������Ʈ�� �ʿ��մϴ�. ��� ������Ʈ ���μ����� ���� �մϴ�.");
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
								logger.info("����� �ش� ���� ��⿡ ����� ���� �ð��� �ƴմϴ�. ���ݸ� ��ٷ� �ֽʽÿ�.");
								Thread.sleep(20 * 60 * 1000);
							} catch (Exception e) {
								// ignore
							}
						} else if (TypeCode.CGCommand.OK.equals(result)) {
							// move ARCH/send , Generate SNR, Report
							new SendActionSet(file).doAct();
							logger.info(file + " ���� ���� ���� ����");
						} else {
							new SendActionSet(file,
									TypeCode.ErrorCode.ETC_ERROR, result)
									.doAct();
							logger.info(file + " ���� ���� ���� ����, " + result);
						}
					}
				} else {
					logger.error(file.getName()
							+ " ������ v0.88 �Ǵ� v0.89 ���Ŀ� ���� �ʴ� �����Դϴ�.");
//					return;
					throw new Exception(file.getName()
							+ " ������ v0.88 �Ǵ� v0.89 ���Ŀ� ���� �ʴ� �����Դϴ�.");
				}
			} catch (FlatFileConvertingException e) {
				// Error While Converting FlatFile to ebXML
				// ignore, just skip, process next file
				logger.error("���� ������ XML���·� ��ȯ�ϴ� �������� ������ �߻��Ͽ����ϴ�.", e);
				e.addAction(new SendActionSet(file,
						TypeCode.ErrorCode.FILE_DATA_ERROR, e.getMessage()));
				e.fireAction();
			} catch (RetryException e) {
				// ���� ���� ��� �� �߰��� ���� , CTRService lookup, method ȣ�� ����
				// Internal Server Error(FIURemoteException)
				// 10 �а� ���
				int sleepTime = Integer.parseInt(Configure.getInstance().getAgentInfo().getJmsSoapInfo().getSLEEP_TIME());
				String retryTime = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_COUNT();
			    String waitTime = Configure.getInstance().getAgentInfo().getJmsSoapInfo().getRETRY_WAIT_TIME();
				
				String error_msg = "���� ���� ���� ����(MessageSending) "+waitTime+"�ʰ� "+retryTime+"ȸ ��õ��� �Ͽ����� ������� ���Ͽ� �������� "+sleepTime+"�ʰ� �����մϴ�.";
				
				new SendActionSet( file, TypeCode.ErrorCode.ETC_ERROR, error_msg ).doErrorAct();
				
				logger.error("���� �߰� �����  ���� ���� ���� ����(MessageSending) ������ ������ �ֽ��ϴ�. �������� "+sleepTime+"�ʰ� �����մϴ�.");
				CTRAgent.getInstance().sleepAgent(sleepTime);
				
				
				
				
				
				
			/*
			 * 2011 ���ȸ�� ��ȭ GpkiSDKException�� ������� �ʱ�� �Ѵ�.
			 * DecryptionException�� ��ü �Ѵ�. } catch(GpkiSDKException e ){
			 * logger.error("��ȣȭ ���� �� ������ ���� ������ �߻��Ͽ����ϴ�. ���� �ڵ� - " +
			 * e.returnedValue(), e); new SendActionSet(file,
			 * TypeCode.ErrorCode.ETC_ERROR,
			 * "��ȣȭ ���� �� ������ ���� ������ �߻��Ͽ����ϴ�. ���� �ڵ� - " + e.returnedValue() +
			 * "\n" + e.getMessage()).doAct();
			 */
				
			} catch (DecryptionException e) {
				logger.error("��ȣȭ ���� �� ������ ���� ������ �߻��Ͽ����ϴ�. - ", e);
				new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR,
						"��ȣȭ ���� �� ������ ���� ������ �߻��Ͽ����ϴ�.\n" + e.getMessage())
						.doAct();
			} catch (UnsatisfiedLinkError e) {
				// Error While Converting FlatFile to ebXML. ignore...
				logger.error(e, e);
				new FatalActionSet(file, "��ȣȭ native ���̺귯����  ��ġ�Ǿ� ���� �ʽ��ϴ�.", e)
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
		 * Flatfile����Ÿ�� XML�� ��ȯ�ϰ� ��ȣȭ�Ͽ� MessageContext�� ��� �����Ѵ�.
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
			// CurrencyTransactionReportDocument ��ü ������ �Է� ���� ���ؼ� �ڸ��� �� ���� üŷ

			FlatFileToXML converter = new FlatFileToXML(FileTool.getFileString(file), file.getName());
			CurrencyTransactionReportDocument xmlDoc = converter.ChkAndGetXMLDoc();

			// ��ȣȭ ����
			String xmlStr = xmlDoc.xmlText();
			xmlStr = "<?xml version=\"1.0\" encoding=\"EUC-KR\"?>" + xmlStr;

			// ��ȣȭ �� ���� ���� ������ ����
			byte[] encryptedMsg = SecurityUtil.encrypt(xmlStr.getBytes());
			
			/**
			 * 2014. 9. 12 �߰� Base64 Encoding
			 */	
			boolean isSigning = Configure.getInstance().getEncryptionSigningInfo().isEnabled();
			if(!isSigning){ //���ڼ����� �Ǿ��� ���� base64�� �������� �ʽ��ϴ�.
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
			// CurrencyTransactionReportDocument ��ü ������ �Է� ���� ���ؼ� �ڸ��� �� ���� üŷ

			FlatFileToXML089 converter = new FlatFileToXML089(
					FileTool.getFileString(file), file.getName());
			krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportDocument xmlDoc = converter
					.ChkAndGetXMLDoc();

			// ��ȣȭ ����
			String xmlStr = xmlDoc.xmlText();
			xmlStr = "<?xml version=\"1.0\" encoding=\"EUC-KR\"?>" + xmlStr;

			// depends on encrytpion, signing configuration
			byte[] encryptedMsg = SecurityUtil.encrypt(xmlStr.getBytes());

			/**
			 * 2014. 9. 12 �߰� Base64 Encoding
			 */
			boolean isSigning = Configure.getInstance().getEncryptionSigningInfo().isEnabled();
			if(!isSigning){ //���ڼ����� �Ǿ��� ���� base64�� �������� �ʽ��ϴ�.
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
		 * Flatfile ����Ÿ�� ���������� �ùٸ��� üũ�Ѵ�. (���ϸ� ���� , CTREND üũ)
		 * 
		 * @param file
		 * @return
		 * @throws FlatFileConvertingException
		 */
		private boolean isOK(String orgCd) {
			try {
				FlatFileChecker checker = new FlatFileChecker(file);
				// ���ϸ��� �ùٸ��� üũ��.
				int result = checker.isValid();

				// if invalid
				if (FlatFileChecker.OK != result
						&& FlatFileChecker.CTREND_INVALID != result) {
					// Error While Converting FlatFile to ebXML
					// ignore, just skip, process next file
					logger.error("���� ���� ���� ���� - " + checker.toString());
					new SendActionSet(file, TypeCode.ErrorCode.FILE_DATA_ERROR,
							checker.toString()).doAct();
					return false;
				}

				// ���� ���� ���� üũ
				String date = CurrentTimeGetter.formatDate();
				String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
				String arch_dir = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType,CtrAgentEnvInfo.getSendDirName(),orgCd,date);
				String arch_send = arch_dir + File.separator
						+ file.getName();
				File arch_send_file = new File(arch_send);
				boolean ex = arch_send_file.exists();
				if (arch_send_file.exists()) {
					logger.error(file.getName() + " ������ �̹� ó�� �Ǿ����ϴ�.");
					new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR,
							file.getName() + " ������ �̹� ó�� �Ǿ����ϴ�.").doAct();
					return false;
				}
			} catch (IOException e) {
				logger.error(e);
			}

			return true;
		}

	} // end of MessageSenderWorker class
}
