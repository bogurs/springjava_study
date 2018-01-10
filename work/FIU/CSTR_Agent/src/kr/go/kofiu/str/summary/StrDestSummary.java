package kr.go.kofiu.str.summary;

/**
 * 
 * StrDestSummary
 * 
 * 발송문서, CSV 생성을 위한 데이터를 담아두는 VO 클래스
 *
 */
public class StrDestSummary {
	
	/*
	 * 기관코드
	 */
	String orgCode;
	
	/*
	 * 메시지 타입 코드
	 */
	String messageTypeCode;
	
	/*
	 * 대표기관코드
	 */
	String orgDocNum;
	
	/*
	 * 문서번호
	 */
	String fiuDocNum;
	
	/*
	 * 발송일자
	 */
	String docSendDate;
	
	/*
	 * 파일명
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
