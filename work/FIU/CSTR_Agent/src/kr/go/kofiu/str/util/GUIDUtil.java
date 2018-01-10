package kr.go.kofiu.str.util;

import kr.go.kofiu.str.conf.STRConfigure;

public class GUIDUtil {
	
	/**
	 * FIUF0000 GUID 값 생성 로직
	 * 
	 * @return GUID
	 */
	public static String getGUID_FIUF0000(String orgCd){
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
        int num = r.nextInt(10000);
        String randomValue = String.format("%04d", num);
		//String GUID = "FIUF0000"+orgCd+"9400"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
        String GUID = STRConfigure.getInstance().getAgentInfo().getId()+orgCd+"9400"+"FIUF0000"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}
	
	/**
	 * FIUF0001 GUID 값 생성 로직
	 * 
	 * @return GUID
	 */
	public static String getGUID_FIUF0001(){ //Check Agent용 GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
        int num = r.nextInt(10000);
        String randomValue = String.format("%04d", num);
        String agentId = STRConfigure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"STR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}
	
	
	/**
	 * FIUF9998 GUID 값 생성 로직
	 * 
	 * @return GUID
	 */
	public static String getGUID_FIUF9998(String orgCd){
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
        int num = r.nextInt(10000);
        String randomValue = String.format("%04d", num);
		//String GUID = "FIUF0000"+orgCd+"9400"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
        String GUID = STRConfigure.getInstance().getAgentInfo().getId()+orgCd+"9400"+"FIUF0000"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}
}
