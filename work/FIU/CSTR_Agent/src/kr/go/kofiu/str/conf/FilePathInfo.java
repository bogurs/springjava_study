package kr.go.kofiu.str.conf;

public class FilePathInfo {
	
	/**
	 * 2분 대기 설정
	 */
	private int outboxIOWaitMinutes = 0;
	
	/**
	 * 메인 디렉토리
	 */
	public String HOME;
	
	/**
	 * message 디렉토리
	 */
	public String MESSAGE;
	
	/**
	 * 송신함 OUTBOX
	 */
	public String OUTBOX;
	
	/**
	 * 송신함 PROC
	 */
	public String PROC;
	
	/**
	 * 수신함 INBOX
	 */
	public String INBOX;
	
	/**
	 * 보관함 ARCHIVE
	 */
	public String ARCHIVE;
	
	/**
	 * 로그함 REPORT (CSV)
	 */
	public String REPORT;
	
	/**
	 * 상태로그함 STATUS (CSV)
	 */
	public String STATUS;
	
	/**
	 * GPKI 라이센스 파일
	 */
	public String GPKICONF;
	
	
	/**
	 * STR 도착 증서
	 */
	private final String SEND_CERT = "/send_cert";
	
	/**
	 * STR 발송 증서
	 */
	private final String ARRIVE_CERT = "/arrive_cert";
	
	/**
	 * STR 가접수 증서
	 */
	private final String PREACCEPT_CERT = "/preaccept_cert";
	
	/**
	 * STR 접수 증서
	 */
	private final String ACCEPT_CERT = "/accept_cert";
	
	/**
	 * STR 문서 (발송성공) / 로그 송신 정보 내역
	 */
	private final String SEND = "/send";
	
	/**
	 * STR 문서 (발송실패)
	 */
	private final String SEND_ERR = "/send_err";
	
	/**
	 * 로그 수신 정보 내역
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
	 * 수신함 STR 발송증서 PATH
	 * @return
	 */
	public String getINBOX_SEND(){
		return INBOX + SEND_CERT;
	}
	
	/**
	 * 수신함 STR 도착증서 PATH
	 * @return
	 */
	public String getINBOX_ARRIVE(){
		return INBOX + ARRIVE_CERT;
	}
	
	/**
	 * 수신함 STR 가접수증서 PATH
	 * @return
	 */
	public String getINBOX_PREACCEPT(){
		return INBOX + PREACCEPT_CERT;
	}

	/**
	 * 수신함 STR 접수증서 PATH
	 * @return
	 */
	public String getINBOX_ACCEPT(){
		return INBOX + ACCEPT_CERT;
	}

	/**
	 * 보관함 STR 문서 발송 성공
	 * @return
	 */
	public String getARCHIVE_SEND(){
		return ARCHIVE + SEND;
	}
	
	/**
	 * 보관함 STR 문서 발송 실패
	 * @return
	 */
	public String getARCHIVE_SEND_ERR(){
		return ARCHIVE + SEND_ERR;
	}
	
	/**
	 * 보관함 STR 문서 수신 성공
	 * @return
	 */
	public String getARCHIVE_RCV(){
		return ARCHIVE + RCV;
	}
	
	/**
	 * 보관함 STR 발송증서 PATH
	 * @return
	 */
	public String getARCHIVE_RCV_SEND(){
		return ARCHIVE + RCV + SEND_CERT;
	}
	
	/**
	 * 보관함 STR 도착증서 PATH
	 * @return
	 */
	public String getARCHIVE_RCV_ARRIVE(){
		return ARCHIVE + RCV + ARRIVE_CERT;
	}
	
	/**
	 * 보관함 STR 가접수증서 PATH
	 * @return
	 */
	public String getARCHIVE_RCV_PREACCEPT(){
		return ARCHIVE + RCV + PREACCEPT_CERT;
	}

	/**
	 * 보관함 STR 접수증서 PATH
	 * @return
	 */
	public String getARCHIVE_RCV_ACCEPT(){
		return ARCHIVE + RCV + ACCEPT_CERT;
	}
	
	/**
	 * 로그함 STR 송신 내역
	 * @return
	 */
	public String getREPORT_SEND(){
		return REPORT + SEND;
	}

	/**
	 * 로그함 STR 수신 내역
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
