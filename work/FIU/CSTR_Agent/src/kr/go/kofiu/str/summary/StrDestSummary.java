package kr.go.kofiu.str.summary;

/**
 * 
 * StrDestSummary
 * 
 * �߼۹���, CSV ������ ���� �����͸� ��Ƶδ� VO Ŭ����
 *
 */
public class StrDestSummary {
	
	/*
	 * ����ڵ�
	 */
	String orgCode;
	
	/*
	 * �޽��� Ÿ�� �ڵ�
	 */
	String messageTypeCode;
	
	/*
	 * ��ǥ����ڵ�
	 */
	String orgDocNum;
	
	/*
	 * ������ȣ
	 */
	String fiuDocNum;
	
	/*
	 * �߼�����
	 */
	String docSendDate;
	
	/*
	 * ���ϸ�
	 */
	String fileName;
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getMessageTypeCode() {
		return messageTypeCode;
	}
	public void setMessageTypeCode(String messageTypeCode) {
		this.messageTypeCode = messageTypeCode;
	}
	public String getOrgDocNum() {
		return orgDocNum;
	}
	public void setOrgDocNum(String orgDocNum) {
		this.orgDocNum = orgDocNum;
	}
	public String getDocSendDate() {
		return docSendDate;
	}
	public void setDocSendDate(String docSendDate) {
		this.docSendDate = docSendDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFiuDocNum() {
		return fiuDocNum;
	}
	public void setFiuDocNum(String fiuDocNum) {
		this.fiuDocNum = fiuDocNum;
	}
	
}
