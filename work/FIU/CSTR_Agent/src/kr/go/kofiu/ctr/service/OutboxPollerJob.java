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
 * 업무 그룹명 : CTR 시스템
 * 서브 업무명 : 보고 기관 Agent
 * 설          명 : OUTBOX에 있는 SND 파일을 읽어 파일을 전송할 수 있도록 PROC DIR에 옮김
 * 작   성   자  : ykyu 
 * 작   성   일  : 2005. 8. 2
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
	 * OUTBOX에 있는 SND 파일을 읽어 파일을 전송할 수 있도록 PROC DIR에 옮김
	 */
	public void doJob(JobExecutionContext context) throws AgentException {
		// 기관 코드 별인지 체크 
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
		// 보고 문서 파일 목록
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
		
		logger.info(file.getName() + "파일 OUTBOX에서 검사 시작");
		try {
			String[] token = FileTool.getFileString(file.getPath()).split("\\|\\|");
			// V0.88 및 V0.99를 모두 처리하기 위한 버전 분기
			if ( token != null && token.length == 53 ){
				// V0.88(구서식)일 때
				FlatFileChecker checker = new FlatFileChecker(file);
				int result = checker.isValid();
				if( FlatFileChecker.OK == result ){
					logger.info(file + " CTR 보고문서 파일 형식 검증 성공");
					String filename = procDir + File.separator + file.getName();
					File f = new File(filename);
					if (  f.exists() ) {
						// 동일 파일명이 존재한다. 
						logger.error( file.getName() + " 파일이 이미 존재합니다." );
						new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
								, file.getName() + " 파일이 이미 존재합니다." ).doAct();
					} else { 
						// flatfile 내용 체크 
						// 기관 코드가 파일명과 일치하는지 검사 
						checker.checkMessageFormat();
						FileTool.move(file,  filename);
					}
					logger.info(file + "을 " + procDir + "로 이동합니다.");						
				} else if ( FlatFileChecker.CTREND_INVALID == result ){
					// just skip
					logger.info(file + " 은  다음에 처리합니다. Because "  + checker);
					return;
				} else {
					// File명이 포맷에 맞지 않거나 ,  CTREND가 없고, 보고문서 작성 일자가 금일+1일 이상일 때 
					logger.error( checker );
					new SendActionSet(file, TypeCode.ErrorCode.FILE_FORMAT_ERROR 
							, checker.toString()).doAct();
				} 
			}
			else if ( token != null && token.length == 84){
			//else  {
				// V0.89(신규서식)이거나 오류가 있는 FlatFile일 때(token 배열의 숫자 오류는 FlatFileCheker089에서 처리함)
				FlatFileChecker089 checker = new FlatFileChecker089(file);
				int result = checker.isValid();
				if( FlatFileChecker.OK == result ){
					logger.info(file + " CTR 보고문서 파일 형식 검증 성공");
					String filename = procDir + File.separator + file.getName();
					File f = new File(filename);
					if (  f.exists() ) {
						// 동일 파일명이 존재한다. 
						logger.error( file.getName() + " 파일이 이미 존재합니다." );
						new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
								, file.getName() + " 파일이 이미 존재합니다." ).doAct();
					} else { 
						// flatfile 내용 체크 
						// 기관 코드가 파일명과 일치하는지 검사 
						checker.checkMessageFormat();
						FileTool.move(file,  filename);
						logger.info(file + "을 " + procDir + "로 이동합니다.");	
						}
										
				} else if ( FlatFileChecker.CTREND_INVALID == result ){
					// just skip
					
					logger.debug(file + " 은  다음에 처리합니다. Because "  + checker);
					return;
				} else {
					// File명이 포맷에 맞지 않거나 ,  CTREND가 없고 파일이 하루가 지났다. 
					logger.error( checker );
					new SendActionSet(file, TypeCode.ErrorCode.FILE_FORMAT_ERROR 
							, checker.toString()).doAct();
				} 
			}
			else {
				logger.error(file + " 파일은 v0.88(53개항목)또는 v0.89(84항목)에 맞지 않는 파일입니다.");
				throw new FlatFileDataException(file + " 파일은 v0.88(53개항목)또는 v0.89(84항목)에 맞지 않는 파일입니다.");
			}
			
		} catch (FlatFileDataException e) {
			logger.error(file.getName() + " CTR 보고문서 파일 형식 검증 실패", e);
			new SendActionSet(file, TypeCode.ErrorCode.FILE_DATA_ERROR 
					, e.getMessage() ).doAct();
		} catch (FileNotFoundException e ){
			// error 처리 
			String msg = "파일이 존재하지 않습니다. CTR 보고 문서를 OUTBOX 폴더에 등록 하실 때, 파일을 완전히 쓰지 못하고 실패한 것으로 추정됩니다. 다시 보내세요." + e.getMessage();
			logger.error(msg, e);
			new NullFileSendActionSet(file.getName(), msg).doAct();
		} catch (IOException e) {
			AgentException ae = new AgentException(e);
			if ( Utility.isDiskFull(e) ) {
				// Disk Full 상황 발생 
				ae.addAction(new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
						, "DISK FULL로 인해 "	+ file.getName() + " 파일을 " + procDir + "로 move하지 못하였습니다.", e));
				ae.addAction(new ShutdownAction());
			} else {
				ae.addAction(new EmailAction(e.getMessage(), e));
			}
			throw ae;
		} catch (Exception e) {
			logger.error(file.getName() + " CTR 보고문서 파일 형식 검증 실패", e);
			new SendActionSet(file, TypeCode.ErrorCode.ETC_ERROR 
					, e.getMessage() ).doAct();
		}
	}
}