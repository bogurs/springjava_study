package kr.go.kofiu.ctr.util;

import java.io.File;
import java.io.FileFilter;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���丮�� ���� ����� ���� ��, 
 * 			���� Ȯ���� 'SND'�� ���͸��ϰ�, �޸� ������ ���� 
 * 			���� ������ 1000�� ������ ���� �д´�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class CTRFileFilter implements FileFilter {

	/**
	 * �� ���� �о� ���� ���� ������ ���Ѽ�
	 */
	public static final int LIMIT_FILE_COUNT = 1000;
	
	/**
	 * 
	 */
	public static final long TWO_MINUTE = 1000 * 60 * 2;

	/**
	 * ���� �ð�
	 */
	private long now = System.currentTimeMillis();
	
	/**
	 * �ּ� ���� ���� �ð� - ���� �ð� ���� �ּ��� checkTime ������ ������ ���̾�� �Ѵ�. 
	 * rftp �� ���� ��� �� ���� ��� �ϴ� ���н� ���� �߻� ���� �� ����.
	 */
	private long checkTime = 0;

	/**
	 * ���� ���� ���� ���� ����
	 */
	public int fileCount = 0;
	
	
	/**
	 * ������ 
	 *
	 */
	public CTRFileFilter() {
		
	}
	
	/**
	 * ������ 
	 * @param checkTime
	 */
	public CTRFileFilter(long checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 * ���� ���͸� 
	 */
	public boolean accept(File pathname) {
		// directory ���� 
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
