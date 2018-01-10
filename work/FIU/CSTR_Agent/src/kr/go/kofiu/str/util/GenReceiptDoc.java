package kr.go.kofiu.str.util;

import kr.go.kofiu.str.summary.StrDestSummary;

public class GenReceiptDoc {
	
	/**
	 * 
	 * �۽� ���μ������� �߼������� �����ϱ����� �޼ҵ�
	 * 
	 * @param strDoc ��������
	 * @param error_cd �����ڵ�
	 * @param error_msg �����޽���
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
