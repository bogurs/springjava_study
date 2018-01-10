package kr.go.kofiu.str.util;

import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;

public class STRResponseDocument {
	private ItemAnonType[] header;
	private ItemAnonType[] body;
	private String attachment;
	private String orgDocNum;
	private String docSendDt;
	private String attachFileNm;
	public ItemAnonType[] getHeader() {
		return header;
	}
	public void setHeader(ItemAnonType[] header) {
		this.header = header;
	}
	public ItemAnonType[] getBody() {
		return body;
	}
	public void setBody(ItemAnonType[] body) {
		this.body = body;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	public String getOrgDocNum() {
		return orgDocNum;
	}
	public void setOrgDocNum(String orgDocNum) {
		this.orgDocNum = orgDocNum;
	}
	public String getDocSendDt() {
		return docSendDt;
	}
	public void setDocSendDt(String docSendDt) {
		this.docSendDt = docSendDt;
	}
	public String getAttachFileNm() {
		return attachFileNm;
	}
	public void setAttachFileNm(String attachFileNm) {
		this.attachFileNm = attachFileNm;
	}
	

}
