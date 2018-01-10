package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.Utility;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 접수 증서 수신 후 복호화 및 인증서 오류 발생시 해당 파일을 
 * 				archive/rcv_error 폴더로 이동한다.  
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MoveArchiveRcverrorAction implements Action {
	
	/**
	 * 수신 파일 명 
	 */
	private String rcvFileName;
	
	/**
	 * 암호화된 메세지 
	 */
	private byte[] encryptedMsg;
	
	/**
	 * 생성자
	 * @param rcvFileName	수신 파일 명 
	 * @param encryptedMsg	암호화된 메세지 
	 */
	public MoveArchiveRcverrorAction(String rcvFileName, byte[] encryptedMsg){
		this.rcvFileName = rcvFileName;
		this.encryptedMsg = encryptedMsg;
	}
	
	/**
	 * 암호화 데이타를 PROC/RCV 폴더에 저장한다. 
	 * 이동된 데이타는 Agent의 RetryInboxErrorJob에 의해 처리된다. 
	 */
	public void doAct() {
		try {
			String arc_rcvErr_dir = CtrAgentEnvInfo.ARCHIVE_RECEIVE_ERROR_DIR_NAME;
			FileTool.writeToFile(arc_rcvErr_dir + File.separator + rcvFileName, encryptedMsg, false);
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full 상황 발생 
				// Disk Full 상황 발생 
				new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
						, "DISK FULL로 인해 접수 증서 "	+ rcvFileName + " 파일을 " + CtrAgentEnvInfo.PROC_RECEIVE_DIR_NAME + "에 생성하지 못하였습니다.", e).doAct();
				new ShutdownAction().doAct();
			}
			throw new ActionException(e);
		}
	}

}
