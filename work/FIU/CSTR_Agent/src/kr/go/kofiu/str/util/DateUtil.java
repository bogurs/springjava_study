package kr.go.kofiu.str.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*******************************************************
 * <pre>
 * 업무   그룹명  : STR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 날짜 문자열을 얻어오는 Util 클래스 
 * 작   성   자  : 선만주 
 * 작   성   일  : 2008.09.10
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class DateUtil {
	
	private DateUtil() {}

	/**
	 * yyyyMMddHHmmss 포맷
	 */
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

	/**
	 * yyyyMMddHHmmssSSS 포맷
	 */
	public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

	/**
	 * MMddHHmmssSSS 포맷
	 */
	public static final String MMddHHmmssSSS = "MMddHHmmssSSS";

	/**
	 * MMddHHmmss 포맷
	 */
	public static final String MMddHHmmss = "MMddHHmmss";

	/**
	 * yyyy_MM_dd 포맷
	 */
	public static final String yyyy_MM_dd = "yyyy_MM_dd";

	/**
	 * yyyyMMdd 포맷
	 */
	public static final String yyyyMMdd = "yyyyMMdd";

	/**
	 * yyyyMMddHHmm 포맷
	 */
	public static final String yyyyMMddHH = "yyyyMMddHH";

	/**
	 * HH_mm_ss 포맷
	 */
	public static final String HH_mm_ss = "HH:mm:ss";

	/**
	 * Get Current Time With parameter format
	 * 
	 * @param format
	 * @return
	 */
	public static final String getDateTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	/**
	 * convert value With parameter format
	 * 
	 * @param format
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static final Date parse(String format, String value)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		return sdf.parse(value);
	}

	/**
	 * Get Time In Millis With Parameter Format Date Text
	 * 
	 * @param format
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static final long getTimeInMillis(String format, String value)
			throws ParseException {
		Date last = parse(format, value);
		Calendar cal = Calendar.getInstance();
		cal.setTime(last);
		return cal.getTimeInMillis();
	}
}
