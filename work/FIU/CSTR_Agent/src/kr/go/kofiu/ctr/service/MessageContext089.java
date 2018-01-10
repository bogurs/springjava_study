package kr.go.kofiu.ctr.service;

import kr.go.kofiu.ctr.util.TypeCode;
import krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportDocument;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 전송시 필요한 모든 Context 정보를 담고 있다.  신 버전 CTR 적용.
 * 작   성   자  : 이수영
 * 작   성   일  : 2007. 9. 27
 * copyright @ LG CNS. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageContext089 {

	/**
	 * 보고 문서 XML의 객체 타입
	 */
	private CurrencyTransactionReportDocument xmlDoc;
	
	/**
	 * 암호화된 보고 문서
	 */
	private byte[] encryptedMsg;
	
	/**
	 * 보고 문서 파일 명
	 */
	private String filename;
	
	/**
	 * Agent ID
	 */
	private String agentId;
	
	/**
	 * 암호화 여부
	 */
	private boolean isEncryption;
	
	/**
	 * 전자 서명 여부
	 */
	private boolean isSigning;

	/**
	 * 생성자
	 * @param xmlDoc
	 * @param encryptedMsg
	 * @param filename
	 * @param agentId
	 * @param isEncryption
	 * @param isSigning
	 */
	public MessageContext089(CurrencyTransactionReportDocument xmlDoc, byte[] encryptedMsg, 
			String filename , String agentId, boolean isEncryption, boolean isSigning) 
	{
		this.xmlDoc = xmlDoc;
		this.encryptedMsg = encryptedMsg;
		this.filename = filename;
		this.agentId = agentId;
		this.isEncryption = isEncryption;
		this.isSigning = isSigning;
	}

	/**
	 * 
	 * @return
	 */
	public CurrencyTransactionReportDocument getXmlDoc() {
		return xmlDoc;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getEncryptedMsg()
	{
		return encryptedMsg;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEncryption() {
		return isEncryption;
	}

	/**
	 * 암호화 타입 코드 값 리턴
	 * 암호화 + 전자 서명
	 * 암호화 + 전자 서명 안함 
	 * 암화화 안함 + 전자 서명 
	 * 암호화 + 전자 서명 모두 안함 
	 * @return
	 */
	public String getEncryptionTypeCode(){
		if (isEncryption &&  isSigning)
			return TypeCode.EncryptionTypeCode.ENC_AND_SIGN;
		else if ( isEncryption &&  !isSigning)
			return TypeCode.EncryptionTypeCode.ENC_ONLY;
		else if ( !isEncryption &&  isSigning)
			return TypeCode.EncryptionTypeCode.SIGN_ONLY;
		else 
			return TypeCode.EncryptionTypeCode.NOTHING;
	}

	/**
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @return
	 */
	public String getAgentId() {
		return agentId;
	}
}
