package kr.go.kofiu.str.conf;

public class FilePathInfo {
	
	/**
	 * 2�� ��� ����
	 */
	private int outboxIOWaitMinutes = 0;
	
	/**
	 * ���� ���丮
	 */
	public String HOME;
	
	/**
	 * message ���丮
	 */
	public String MESSAGE;
	
	/**
	 * �۽��� OUTBOX
	 */
	public String OUTBOX;
	
	/**
	 * �۽��� PROC
	 */
	public String PROC;
	
	/**
	 * ������ INBOX
	 */
	public String INBOX;
	
	/**
	 * ������ ARCHIVE
	 */
	public String ARCHIVE;
	
	/**
	 * �α��� REPORT (CSV)
	 */
	public String REPORT;
	
	/**
	 * ���·α��� STATUS (CSV)
	 */
	public String STATUS;
	
	/**
	 * GPKI ���̼��� ����
	 */
	public String GPKICONF;
	
	
	/**
	 * STR ���� ����
	 */
	private final String SEND_CERT = "/send_cert";
	
	/**
	 * STR �߼� ����
	 */
	private final String ARRIVE_CERT = "/arrive_cert";
	
	/**
	 * STR ������ ����
	 */
	private final String PREACCEPT_CERT = "/preaccept_cert";
	
	/**
	 * STR ���� ����
	 */
	private final String ACCEPT_CERT = "/accept_cert";
	
	/**
	 * STR ���� (�߼ۼ���) / �α� �۽� ���� ����
	 */
	private final String SEND = "/send";
	
	/**
	 * STR ���� (�߼۽���)
	 */
	private final String SEND_ERR = "/send_err";
	
	/**
	 * �α� ���� ���� ����
	 */
	private final String RCV = "/rcv";

	public int getOutboxIOWaitMinutes() {
		return outboxIOWaitMinutes;
	}

	public void setOutboxIOWaitMinutes(int outboxIOWaitMinutes) {
		this.outboxIOWaitMinutes = outboxIOWaitMinutes;
	}

	public String getHOME() {
		return HOME;
	}

	public void setHOME(String mESSAGEHOME) {
		this.HOME = mESSAGEHOME;
		
	}
	
	public String getMESSAGE() {
		return MESSAGE;
	}

	public void setMESSAGE(String mESSAGEHOME) {
		this.MESSAGE = HOME+mESSAGEHOME;
		
	}

	public String getOUTBOX() {
		return OUTBOX;
	}

	public void setOUTBOX(String oUTBOX) {
		OUTBOX = MESSAGE + oUTBOX;
	}

	public String getPROC() {
		return PROC;
	}

	public void setPROC(String pROC) {
		PROC = MESSAGE + pROC;
	}

	public String getINBOX() {
		return INBOX;
	}

	public void setINBOX(String iNBOX) {
		INBOX = MESSAGE + iNBOX;
	}

	public String getARCHIVE() {
		return ARCHIVE;
	}

	public void setARCHIVE(String aRCHIVE) {
		ARCHIVE = MESSAGE + aRCHIVE;
	}

	public String getREPORT() {
		return REPORT;
	}

	public void setREPORT(String rEPORT) {
		REPORT = MESSAGE + rEPORT;
	}
	
	public String getSTATUS() {
		return STATUS;
	}
	
	public void setSTATUS(String sTATUS) {
		STATUS = MESSAGE + sTATUS;
	}

	/**
	 * ������ STR �߼����� PATH
	 * @return
	 */
	public String getINBOX_SEND(){
		return INBOX + SEND_CERT;
	}
	
	/**
	 * ������ STR �������� PATH
	 * @return
	 */
	public String getINBOX_ARRIVE(){
		return INBOX + ARRIVE_CERT;
	}
	
	/**
	 * ������ STR ���������� PATH
	 * @return
	 */
	public String getINBOX_PREACCEPT(){
		return INBOX + PREACCEPT_CERT;
	}

	/**
	 * ������ STR �������� PATH
	 * @return
	 */
	public String getINBOX_ACCEPT(){
		return INBOX + ACCEPT_CERT;
	}

	/**
	 * ������ STR ���� �߼� ����
	 * @return
	 */
	public String getARCHIVE_SEND(){
		return ARCHIVE + SEND;
	}
	
	/**
	 * ������ STR ���� �߼� ����
	 * @return
	 */
	public String getARCHIVE_SEND_ERR(){
		return ARCHIVE + SEND_ERR;
	}
	
	/**
	 * ������ STR ���� ���� ����
	 * @return
	 */
	public String getARCHIVE_RCV(){
		return ARCHIVE + RCV;
	}
	
	/**
	 * ������ STR �߼����� PATH
	 * @return
	 */
	public String getARCHIVE_RCV_SEND(){
		return ARCHIVE + RCV + SEND_CERT;
	}
	
	/**
	 * ������ STR �������� PATH
	 * @return
	 */
	public String getARCHIVE_RCV_ARRIVE(){
		return ARCHIVE + RCV + ARRIVE_CERT;
	}
	
	/**
	 * ������ STR ���������� PATH
	 * @return
	 */
	public String getARCHIVE_RCV_PREACCEPT(){
		return ARCHIVE + RCV + PREACCEPT_CERT;
	}

	/**
	 * ������ STR �������� PATH
	 * @return
	 */
	public String getARCHIVE_RCV_ACCEPT(){
		return ARCHIVE + RCV + ACCEPT_CERT;
	}
	
	/**
	 * �α��� STR �۽� ����
	 * @return
	 */
	public String getREPORT_SEND(){
		return REPORT + SEND;
	}

	/**
	 * �α��� STR ���� ����
	 * @return
	 */
	public String getREPORT_RCV(){
		return REPORT + RCV;
	}

	public String getGPKICONF() {
		return GPKICONF;
	}

	public void setGPKICONF(String gPKICONF) {
		GPKICONF = gPKICONF;
	}

	@Override
	public String toString() {
		return "FilePathInfo [HOME=" + HOME + ", MESSAGE=" + MESSAGE
				+ ", OUTBOX=" + OUTBOX + ", PROC=" + PROC + ", INBOX=" + INBOX
				+ ", ARCHIVE=" + ARCHIVE + ", REPORT=" + REPORT + ", STATUS="
				+ STATUS + ", SEND_CERT=" + SEND_CERT + ", ARRIVE_CERT="
				+ ARRIVE_CERT + ", PREACCEPT_CERT=" + PREACCEPT_CERT
				+ ", ACCEPT_CERT=" + ACCEPT_CERT + ", SEND=" + SEND
				+ ", SEND_ERR=" + SEND_ERR + ", RCV=" + RCV + "]";
	}		

}
