package kr.go.kofiu.ctr.actions;

import java.io.File;
import java.io.IOException;

import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.Utility;
import kr.go.kofiu.str.util.CurrentTimeGetter;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 오류 발생 시 해당 파일을 ARCH 폴더의 send/senderror/receive/receiveerror
 * 			폴더 중의 정해진 곳으로 Move하는 Action 정의 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MoveFileAction implements Action {
	
	/**
	 * 이동할 원본 파일
	 */
	private File original;
	

	/**
	 * 파일을 Move할 대상 폴더 
	 */
	private String destination;
	
	/**
	 * 파일을 Move할 기관코드
	 */
	private String orgCd;
	
	/**
	 * 생성자 
	 * @param original 이동할 원본 파일
	 */
	public MoveFileAction(File original, String destination, String orgCd){
		this.original = original;
		this.destination = destination;
		this.orgCd = orgCd;
	}

	/**
	 * 원본 파일을 ERROR 폴더에 옮기고 오류 내역 파일을 생성한다.
	 */
	public void doAct() {
		try {
			String date = CurrentTimeGetter.formatDate();
			String archiveType = Configure.getInstance().getAgentInfo().getArchivefolderType();
			//destination = CtrAgentEnvInfo.checkFcltyAndGetDirSeamless(archiveType, destination, orgCd, date);
			boolean isSaveArchive = kr.go.kofiu.ctr.conf.Configure.getInstance().getAgentInfo().isArchiveEnabled();
			if(isSaveArchive){
				FileTool.move(original, destination + File.separator + original.getName());
				
			}else{
				if(!destination.contains("ARCHIVE")){
					FileTool.move(original, destination + File.separator + original.getName());
				}else{
					original.delete();
				}
			}
		} catch (IOException e) {
			if ( Utility.isDiskFull(e) ) {
				// Disk Full 상황 발생 
				new EmailAction("[긴급]CTR 보고 모듈이 DISK FULL로 인해 정지 하였습니다."
						, "DISK FULL로 인해 "	+ original + " 파일을 " + destination + "로 move하지 못하였습니다.", e).doAct();
				new ShutdownAction().doAct();
			} else {
				throw new ActionException(e);
			}
		}
	}
	
}
