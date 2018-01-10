package kr.go.kofiu.ctr.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.DiskFullMessageInfo;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 12. 21
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Utility {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Utility.class);

	
	public static boolean isDiskFull(IOException e) {
		if (e == null ) return false;
		
		Collection col = Configure.getInstance().getAgentInfo().getDiskFullMessages();
		Iterator iter = col.iterator();
		while(iter.hasNext()) {
			DiskFullMessageInfo info = (DiskFullMessageInfo)iter.next();
			if ( e.getMessage().indexOf(info.getMessage()) > -1 ) {
				// disk full
				logger.fatal("*****************************************");
				logger.fatal("**** DISK FULL, ���� ��� Shutdown ********");
				logger.fatal("*****************************************\n", e);
				// for debug
				e.printStackTrace();
				return true;
			}
		}
		return false;
	}
}
