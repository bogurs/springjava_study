package kr.go.kofiu.ctr.service;

import kr.go.kofiu.ctr.util.TypeCode;
import krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportDocument;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���۽� �ʿ��� ��� Context ������ ��� �ִ�.  �� ���� CTR ����.
 * ��   ��   ��  : �̼���
 * ��   ��   ��  : 2007. 9. 27
 * copyright @ LG CNS. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageContext089 {

	/**
	 * ���� ���� XML�� ��ü Ÿ��
	 */
	private CurrencyTransactionReportDocument xmlDoc;
	
	/**
	 * ��ȣȭ�� ���� ����
	 */
	private byte[] encryptedMsg;
	
	/**
	 * ���� ���� ���� ��
	 */
	private String filename;
	
	/**
	 * Agent ID
	 */
	private String agentId;
	
	/**
	 * ��ȣȭ ����
	 */
	private boolean isEncryption;
	
	/**
	 * ���� ���� ����
	 */
	private boolean isSigning;

	/**
	 * ������
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
	 * ��ȣȭ Ÿ�� �ڵ� �� ����
	 * ��ȣȭ + ���� ����
	 * ��ȣȭ + ���� ���� ���� 
	 * ��ȭȭ ���� + ���� ���� 
	 * ��ȣȭ + ���� ���� ��� ���� 
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
