package kr.go.kofiu.str.util;

import java.text.SimpleDateFormat;

public class CurrentTimeGetter {
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * CurrentTimeGetter formatDateTime 
	 * 
	 *  - format : yyyyMMddHHmmss
	 *  
	 * @return dateTime
	 */
	public static String formatDateTime() { 
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		//System.out.println (formatter.format(new java.util.Date()));
		return formatter.format(new java.util.Date()); 
    }
	
	/**
	 * CurrentTimeGetter formatTime 
	 * 
	 * - format : HH:mm:ss
	 * 
	 * @return time
	 */
	public static String formatTime() { 
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss");
			//System.out.println (formatter.format(new java.util.Date()));
			return formatter.format(new java.util.Date()); 
	    }
	
	/**
	 * CurrentTimeGetter formatDate 
	 * 
	 * - format : yyyyMMdd
	 * 
	 * @return date
	 */
	public static String formatDate() { 
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
			//System.out.println (formatter.format(new java.util.Date()));
			return formatter.format(new java.util.Date()); 
	    }
	
	/**
	 * CurrentTimeGetter formatDate 
	 * 
	 * @return customdateTime
	 */
	public static String formatCustom(String pattern) { 
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
			//System.out.println (formatter.format(new java.util.Date()));
			return formatter.format(new java.util.Date()); 
	    }
}
