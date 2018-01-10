package kr.go.kofiu.str.util;

import kr.go.kofiu.str.summary.StrDestSummary;

public class GenReceiptDoc {
	
	/**
	 * 
	 * 송신 프로세스에서 발송증서를 생성하기위한 메소드
	 * 
	 * @param strDoc 기준정보
	 * @param error_cd 에러코드
	 * @param error_msg 에러메시지
	 * @return
	 */
	public static String generate(StrDestSummary strDest, String error_cd, String error_msg) {
		String document = "";

		String org_cd = strDest.getOrgCode();
		String org_doc_cd = strDest.getOrgDocNum();
		String msg_type = strDest.getMessageTypeCode();
		String org_doc_snd_dat = strDest.getDocSendDate();
		
		document = "05" + "||";
		document += msg_type + "||";
		document += org_cd + "||";
		document += org_doc_cd + "||";
		document += org_doc_snd_dat + "||";
		document += CurrentTimeGetter.formatDateTime() + "||";
		document += error_cd + "||";
		document += error_msg + "||";
		document = "STRSTART||" + document + "STREND";
		return document;
	}

		
}
