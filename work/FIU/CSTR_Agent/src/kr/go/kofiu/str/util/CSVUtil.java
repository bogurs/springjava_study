package kr.go.kofiu.str.util;

import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.str.summary.StrDestSummary;

public class CSVUtil {
	
	public static String getFileName(STRResponseDocument strdoc){
		String rcvFile = null;
		
		ItemAnonType[] body = strdoc.getBody();
		for(int i = 0 ; i < body.length ; i++){
			if(((String)body[i].getKey()).equalsIgnoreCase("AttachmentfileName")){
				rcvFile = (String)body[i].getValue();
				break;
			}
		}
		return rcvFile;
	}
	
	public static String getOrgCode(STRResponseDocument strdoc){
		String fclty_cd = null;
		
		// Body 에서 필요정보 가져오기
		ItemAnonType[] body = strdoc.getBody();
		for(int i = 0 ; i < body.length ; i++){
			 if(((String)body[i].getKey()).equalsIgnoreCase("OrgCode")){
				fclty_cd = (String)body[i].getValue();
				break;
			 }
		}
		return fclty_cd;
	}
	
	public static String getMsgType(STRResponseDocument strdoc){
		String msg_type = null;
		
		// Body 에서 필요정보 가져오기
		ItemAnonType[] body = strdoc.getBody();
		for(int i = 0 ; i < body.length ; i++){
			 if(((String)body[i].getKey()).equalsIgnoreCase("MessageTypeCode")){
				 msg_type = (String)body[i].getValue();
				break;
			 }
		}
		return msg_type;
	}
	public static String getEndDate(STRResponseDocument strdoc){
		String msg_type = null;
		
		// Body 에서 필요정보 가져오기
		ItemAnonType[] body = strdoc.getBody();
		for(int i = 0 ; i < body.length ; i++){
			 if(((String)body[i].getKey()).equalsIgnoreCase("EndDate")){
				 msg_type = (String)body[i].getValue();
				break;
			 }
		}
		return msg_type;
	}
	
	
	/**
	 * 
	 * @param strdoc
	 * @param error_cd
	 * @param error_msg
	 * @return
	 */
	public static String genCSV(STRResponseDocument strdoc, String error_cd, String error_msg){
		
		String msg = "";
		
		String msg_code = "00";
		String description = null;
		String fclty_cd = null;
		String document_id = null;
		String file_nm = null;
		String seq = null;
		//15.03.17 변경 사항 (log Time 을 As-IS로 변경).
		String time = DateUtil.getDateTime(DateUtil.HH_mm_ss);
		
		// Header 에서 필요정보 가져오기
		ItemAnonType[] header = strdoc.getHeader();
		for(int i = 0 ; i < header.length ; i++){
			
			if(((String)header[i].getKey()).equalsIgnoreCase("MessageTypeCode")){
				msg_code = (String)header[i].getValue();
			}
			
		}
		
		// Body 에서 필요정보 가져오기
		ItemAnonType[] body = strdoc.getBody();
		for(int i = 0 ; i < body.length ; i++){
			if(((String)body[i].getKey()).equalsIgnoreCase("AttachmentfileName")){
				file_nm = (String)body[i].getValue();
			}else if(((String)body[i].getKey()).equalsIgnoreCase("OrgCode")){
				fclty_cd = (String)body[i].getValue();
			}else if(((String)body[i].getKey()).equalsIgnoreCase("OrgDocNumSeq")){
				seq = (String)body[i].getValue();
			}else if(((String)body[i].getKey()).equalsIgnoreCase("FiuDocNum")){
				document_id = (String)body[i].getValue();
			}
		}
		//description = codeToDesc(error_cd ,msg_code);
				
		if (time != null && time.length() > 0 ) {
			msg += "\"" + time + "\"";
		}
		msg +=",";
		if (file_nm != null && file_nm.length() > 0 ) {
			msg += "\"" + file_nm + "\"";
		}
		msg +=",";
		if (document_id != null && document_id.length() > 0 ) {
			msg += "\"" + document_id + "\"";
		}
		msg +=",";
		if (error_cd != null && error_cd.length() > 0 ) {
			if(error_cd.equals("00")){
				msg += "\"" + "OK" + "\"";
			}else{
				msg += "\"" + "ERROR" + "\"";
			}
		}
		msg +=",";
		if (error_msg != null && error_msg.length() > 0 ) {
			msg += "\"" + error_msg + "\"";
		}
		
		msg+="\r\n";
		return msg;
	}
	
	/**
	 * 
	 * @param strdoc
	 * @param error_cd
	 * @param error_msg
	 * @return
	 */
	public static String genCSV(StrDestSummary strDest, String error_cd, String error_msg){
		
		String msg = "";
		
		String msg_code = "00";
		String description = null;
		String fclty_cd = null;
		String document_id = null;
		String file_nm = null;
		//15.03.17 변경 사항 (log Time 을 As-IS로 변경).
		String time = DateUtil.getDateTime(DateUtil.HH_mm_ss);
		
		fclty_cd = strDest.getOrgCode();
		document_id = strDest.getFiuDocNum();
		description = error_msg;
		file_nm = strDest.getFileName();
		msg_code = error_cd;
				
				
		if (time != null && time.length() > 0 ) {
			msg += "\"" + time + "\"";
		}
		msg +=",";
		if (file_nm != null && file_nm.length() > 0 ) {
			msg += "\"" + file_nm + "\"";
		}
		msg +=",";
		if (document_id != null && document_id.length() > 0 ) {
			msg += "\"" + document_id + "\"";
		}
		msg +=",";
		if (error_cd != null && error_cd.length() > 0 ) {
			if(error_cd.equals("00")){
				msg += "\"" + "OK" + "\"";
			}else{
				msg += "\"" + "ERROR" + "\"";
			}
		}
		msg +=",";
		if (error_msg != null && error_msg.length() > 0 ) {
			msg += "\"" + error_msg + "\"";
		}
		
		msg+="\r\n";
		//System.out.println(msg);
		return msg;
	}
	/**
	 * 
	 * 
	 * @param code 메시지 타입 코드
	 * @param seq  보고문서 시퀀스
	 * 
	 * @return desc  
	 */
	
	public static String genUtilCSV(String result, String desc){
		String msg = "";
		
		if (result != null && result.length() > 0 ) {
			msg += "\"" + result + "\"";
		}
		msg +=",";
		if (desc != null && desc.length() > 0 ) {
			msg += "\"" + desc + "\"";
		}
		//15.03.17 변경 사항 (log Time 을 As-IS로 변경).
		msg = "\"" + DateUtil.getDateTime(DateUtil.HH_mm_ss) + "\" ," + msg + "\r\n";
		return msg;
	}
	
	private static String codeToDesc(String code , String seq){
		String desc = "";
		String seqText = "";
		//System.out.println("seq = " + seq);
		if(seq.trim().equals("00")){
			seqText = "발송";
		}else if(seq.trim().equals("01")){
			seqText = "도착";
		}else if(seq.trim().equals("02")){
			seqText = "가접수";
		}else if(seq.trim().equals("03")){
			seqText = "접수";
		}
		
		if(code.trim().equals("00") || code.length() == 0){
			desc = seqText + "증서수신성공";
		}else if(code.trim().equals("97")){
			desc = seqText + "실패 Validation 에러";
		}else if(code.trim().equals("98")){
			desc = seqText + "유효하지 않은 기관코드, 또는 UserID";
		}else if(code.trim().equals("01")){
			desc = seqText + "문서번호(FiuDocNum) 중복";
		}else if(code.trim().equals("11")){
			desc = seqText + "가접수 에러";
		}else if(code.trim().equals("99")){
			desc = seqText + "유효하지 않은 기관코드, 또는 UserID";
		}
		
		return desc;
	}

}
