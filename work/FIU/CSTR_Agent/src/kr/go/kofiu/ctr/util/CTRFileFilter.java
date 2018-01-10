package kr.go.kofiu.ctr.util;

import java.io.File;
import java.io.FileFilter;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 디렉토리의 파일 목록을 얻어올 때, 
 * 			파일 확장자 'SND'로 필터링하고, 메모리 상한을 위해 
 * 			파일 갯수를 1000개 단위로 끊어 읽는다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CTRFileFilter implements FileFilter {

	/**
	 * 한 번에 읽어 오는 파일 갯수의 상한선
	 */
	public static final int LIMIT_FILE_COUNT = 1000;
	
	/**
	 * 
	 */
	public static final long TWO_MINUTE = 1000 * 60 * 2;

	/**
	 * 현재 시간
	 */
	private long now = System.currentTimeMillis();
	
	/**
	 * 최소 파일 변경 시간 - 현재 시간 보다 최소한 checkTime 이전에 생성된 것이어야 한다. 
	 * rftp 로 파일 등록 시 파일 기록 하다 실패시 오류 발생 가능 성 존재.
	 */
	private long checkTime = 0;

	/**
	 * 현재 까지 읽은 파일 개수
	 */
	public int fileCount = 0;
	
	
	/**
	 * 생성자 
	 *
	 */
	public CTRFileFilter() {
		
	}
	
	/**
	 * 생성자 
	 * @param checkTime
	 */
	public CTRFileFilter(long checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 * 파일 필터링 
	 */
	public boolean accept(File pathname) {
		// directory 여부 
		if(pathname.isDirectory()) { return false; }

		// check prefix
		if(!pathname.getName().startsWith("CTR")) {return false;}
		
		// check extension
		if(!pathname.getName().endsWith("SND") && !pathname.getName().endsWith("RCV")) {return false;}
	
		fileCount++;
		if ( fileCount > LIMIT_FILE_COUNT ) 
			return false;
		
		if ( checkTime != 0 
				&& (now - pathname.lastModified()) < checkTime ) {
			return false;
		}
		
		return true;
	}

}
