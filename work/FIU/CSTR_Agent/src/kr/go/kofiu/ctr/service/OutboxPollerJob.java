package kr.go.kofiu.ctr.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.actions.EmailAction;
import kr.go.kofiu.ctr.actions.NullFileSendActionSet;
import kr.go.kofiu.ctr.actions.SendActionSet;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.util.CTRFileFilter;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.FlatFileChecker;
import kr.go.kofiu.ctr.util.FlatFileChecker089;
import kr.go.kofiu.ctr.util.FlatFileDataException;
import kr.go.kofiu.ctr.util.TypeCode;
import kr.go.kofiu.ctr.util.Utility;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;


/*******************************************************
 * <pre>
 * ���� �׷�� : CTR �ý���
 * ���� ������ : ���� ��� Agent
 * ��          �� : OUTBOX�� �ִ� SND ������ �о� ������ ������ �� �ֵ��� PROC DIR�� �ű�
 * ��   ��   ��  : ykyu 
 * ��   ��   ��  : 2005. 8. 2
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class OutboxPollerJob extends ScheduleJob
{
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(OutboxPollerJob.class);

	/**
	 * OUTBOX�� �ִ� SND ������ �о� ������ ������ �� �ֵ��� PROC DIR�� �ű�
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		// ��� �ڵ� ������ üũ 
		if ( Configure.getInstance().getAgentInfo().getMessageBoxEnv().isFolderPerFcltyCode() ) {
			Collection col = Configure.getInstance().getAgentInfo().getMessageBoxEnv().getFcltyCodes();
			Iterator iter = col.iterator();
			while( iter.hasNext() ) {
				String fclty_cd = (String)iter.next();
				//
				processInner(CtrAgentEnvInfo.OUTBOX_DIR_NAME + File.separator + fclty_cd);
			}
		} else {
			processInner(CtrAgentEnvInfo.OUTBOX_DIR_NAME);
		}

	}
	
	/**
	 * 
	 * @param outDir
	 * @throws AgentException
	 */
	public void processInner(String outDir) throws AgentException {
		
		File[] files = null;
		// ���� ���� ���� ���
		long IOWaitTime = 1000 * 60 * Configure.getInstance().getAgentInfo().getMessageBoxEnv().getOutboxIOWaitMinutes();
		files = new File(outDir).listFiles(new CTRFileFilter( IOWaitTime ));

		
		//logger.info("OutboxPollerJob Processing... (File List = " + files.length + ")");
		if(files.length==0){
			return;
		}
		for(int j=0; j < files.length; j++) {
			processFileCheck(files[j]);
			
		}// end of for-loop
	}
	
	public synchronized void processFileCheck(File file) throws AgentException{
		String procDir = CtrAgentEnvInfo.PROC_SEND_DIR_NAME;
		
		logger.info(file.getName() + "���� OUTBOX���� �˻� ����");
		try {
			String[] token = FileTool.getFileString(file.getPath()).split("\\|\\|");
			// V0.88 �� V0.99�� ��� ó���ϱ� ���� ���� �б�
			if ( token != null && token.length == 53 ){
				// V0.88(������)�� ��
				FlatFileChecker checker = new FlatFileChecker(file);
				int result = checker.isValid();
				if( FlatFileChecker.OK == result ){
					logger.info(file + " CTR ������ ���� ���� ���� ����");
					String filename = procDir + File.separator + file.getName();
					File f = new File(filename);
					if (  f.exists() ) {
						// ���� ���ϸ��� �����Ѵ�. 
						logger.error( file.getName() + " ������ �̹� �����մϴ�." );
						new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
								, file.getName() + " ������ �̹� �����մϴ�." ).doAct();
					} else { 
						// flatfile ���� üũ 
						// ��� �ڵ尡 ���ϸ�� ��ġ�ϴ��� �˻� 
						checker.checkMessageFormat();
						FileTool.move(file,  filename);
					}
					logger.info(file + "�� " + procDir + "�� �̵��մϴ�.");						
				} else if ( FlatFileChecker.CTREND_INVALID == result ){
					// just skip
					logger.info(file + " ��  ������ ó���մϴ�. Because "  + checker);
					return;
				} else {
					// File���� ���˿� ���� �ʰų� ,  CTREND�� ����, ������ �ۼ� ���ڰ� ����+1�� �̻��� �� 
					logger.error( checker );
					new SendActionSet(file, TypeCode.ErrorCode.FILE_FORMAT_ERROR 
							, checker.toString()).doAct();
				} 
			}
			else if ( token != null && token.length == 84){
			//else  {
				// V0.89(�űԼ���)�̰ų� ������ �ִ� FlatFile�� ��(token �迭�� ���� ������ FlatFileCheker089���� ó����)
				FlatFileChecker089 checker = new FlatFileChecker089(file);
				int result = checker.isValid();
				if( FlatFileChecker.OK == result ){
					logger.info(file + " CTR ������ ���� ���� ���� ����");
					String filename = procDir + File.separator + file.getName();
					File f = new File(filename);
					if (  f.exists() ) {
						// ���� ���ϸ��� �����Ѵ�. 
						logger.error( file.getName() + " ������ �̹� �����մϴ�." );
						new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
								, file.getName() + " ������ �̹� �����մϴ�." ).doAct();
					} else { 
						// flatfile ���� üũ 
						// ��� �ڵ尡 ���ϸ�� ��ġ�ϴ��� �˻� 
						checker.checkMessageFormat();
						FileTool.move(file,  filename);
						logger.info(file + "�� " + procDir + "�� �̵��մϴ�.");	
						}
										
				} else if ( FlatFileChecker.CTREND_INVALID == result ){
					// just skip
					
					logger.debug(file + " ��  ������ ó���մϴ�. Because "  + checker);
					return;
				} else {
					// File���� ���˿� ���� �ʰų� ,  CTREND�� ���� ������ �Ϸ簡 ������. 
					logger.error( checker );
					new SendActionSet(file, TypeCode.ErrorCode.FILE_FORMAT_ERROR 
							, checker.toString()).doAct();
				} 
			}
			else {
				logger.error(file + " ������ v0.88(53���׸�)�Ǵ� v0.89(84�׸�)�� ���� �ʴ� �����Դϴ�.");
				throw new FlatFileDataException(file + " ������ v0.88(53���׸�)�Ǵ� v0.89(84�׸�)�� ���� �ʴ� �����Դϴ�.");
			}
			
		} catch (FlatFileDataException e) {
			logger.error(file.getName() + " CTR ������ ���� ���� ���� ����", e);
			new SendActionSet(file, TypeCode.ErrorCode.FILE_DATA_ERROR 
					, e.getMessage() ).doAct();
		} catch (FileNotFoundException e ){
			// error ó�� 
			String msg = "������ �������� �ʽ��ϴ�. CTR ���� ������ OUTBOX ������ ��� �Ͻ� ��, ������ ������ ���� ���ϰ� ������ ������ �����˴ϴ�. �ٽ� ��������." + e.getMessage();
			logger.error(msg, e);
			new NullFileSendActionSet(file.getName(), msg).doAct();
		} catch (IOException e) {
			AgentException ae = new AgentException(e);
			if ( Utility.isDiskFull(e) ) {
				// Disk Full ��Ȳ �߻� 
				ae.addAction(new EmailAction("[���]CTR ���� ����� DISK FULL�� ���� ���� �Ͽ����ϴ�."
						, "DISK FULL�� ���� "	+ file.getName() + " ������ " + procDir + "�� move���� ���Ͽ����ϴ�.", e));
				ae.addAction(new ShutdownAction());
			} else {
				ae.addAction(new EmailAction(e.getMessage(), e));
			}
			throw ae;
		} catch (Exception e) {
			logger.error(file.getName() + " CTR ������ ���� ���� ���� ����", e);
			new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
					, e.getMessage() ).doAct();
		}
	}
}