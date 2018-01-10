package kr.go.kofiu.ctr.service;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.util.DateUtil;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 저장 유효기간이 지난 파일 삭제 서비스
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageEraserJob extends ScheduleJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MessageEraserJob.class);

	/**
	 * ARCH/ERROR 폴더에 있는 유효 기간이 지난 파일을 삭제한다.
	 * 기간은 config.xml 파일에 설정 값으로 변경 가능하다.
	 */
	public void doJob(JobExecutionContext context) throws AgentException 
	{
		logger.debug("Delete ARCH Files Start!");
		JobDataMap map = context.getJobDetail().getJobDataMap();
		int day = Integer.parseInt((String)map.get("param"));
		if ( day < 30 ){
			logger.info("보고 문서는 최소한 30일 동안 보관되어야 합니다.\n 따라서 config.xml 파일에 설정하신 값 " + day + "일이 작아서 무시됩니다.");
			day = 30;
		}

		Collection col = new ArrayList();
		col.add(CtrAgentEnvInfo.ARCHIVE_SEND_DIR_NAME);
		col.add(CtrAgentEnvInfo.ARCHIVE_SEND_ERROR_DIR_NAME);
		col.add(CtrAgentEnvInfo.ARCHIVE_RECEIVE_DIR_NAME);
		col.add(CtrAgentEnvInfo.ARCHIVE_RECEIVE_ERROR_DIR_NAME);
		
		try {
			deleteFiles(col, day);
		} catch (ParseException e) {
			logger.error(e,e);
		}
	}

	
	/**
	 * 파일의 최종 변경 날짜가 일이 지난 파일을 삭제한다. 
	 * @param col
	 * @param day
	 * @throws ParseException 
	 */
	private void deleteFiles(Collection col , int day) throws ParseException {
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		
		Iterator iter = col.iterator();
		while(iter.hasNext()){
			String dir = (String)iter.next();
			File[] dirs_per_days = new File(dir).listFiles();
			for(int i=0; i < dirs_per_days.length; i++) {
				if ( !dirs_per_days[i].isDirectory() ){
					long archiveLastModified = dirs_per_days[i].lastModified();
					Date date = new Date(archiveLastModified);
					cal.setTime(date); 
					cal.add(Calendar.DATE , day);
					if ( today.after(cal)) {
						if ( dirs_per_days[i].delete() ){
							logger.info("파일 " + dirs_per_days[i] + "를 삭제하였습니다.");
						}
					}
				}
				else {
					Collection subCol = new ArrayList();
					subCol.add(dirs_per_days[i].getAbsolutePath());
					deleteFiles(subCol, day);
				}
			}// end of for
		}
	} // end of method
	
}
