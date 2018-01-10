/**
 * 
 */
package kr.go.kofiu.ctr.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import kr.co.kofiu.www.ctr.encodedTypes.DocumentObject;
import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.JmsMQLib;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.DocumentToSoap;
import kr.go.kofiu.ctr.util.SoapToDocument;
import kr.go.kofiu.str.util.CurrentTimeGetter;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.EnterpriseOrganizationType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.TransactionPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.TransactionType;
import krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportType;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ������ ���� �Ⱓ�� �����Ѵ�.
 * ��   ��   ��  : ����ȣ 
 * �� �� �� ��  : �������� processRequestDocument�� ���� �����ϴ� ����
 * 				 -> ����(JMS���)�� ���� ESB ť�κ��� �����ϴ� �������� ����
 * 				����, ���� ������ ���� ebXML �Ľ��� ���� DocumentToSoap Ŭ���� �߰�
 * ��   ��   ��  : 2005. 8. 25
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageSenderCommand089 {
	
	/**
	 * ���� ���� ���� ����
	 */
	private MessageContext089 context;
	
	public MessageSenderCommand089(MessageContext089 ctx){
		this.context = ctx;
	}
	
	/**
	 * ���� ������ �����Ѵ�.
	 */
	public String sendSndfileToEsb() throws Exception {

		String result = null;
				
			DocumentObject document = new DocumentObject();
			
			ItemAnonType[] headers = makeHeader();
			document.setHeaders(headers);

			ItemAnonType[] bodys = makeBodys();
			document.setBodys(bodys);
	        
	        // file attach
	        document.setAttachment(context.getEncryptedMsg());
	        String xmlStr = DocumentToSoap.conventer(document, context.getEncryptedMsg());
			
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();
			String GUID = getGUID_FIUF9999();
	        result = SoapToDocument.sndResultParser(jmssoapService.processRequestDocumemt(xmlStr, GUID));
		
		return result;
	}
	
	public String getGUID_FIUF9999(){ //Main Flow�� GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); �̶�� �� �� �ִ�.         
        int num = r.nextInt(10000);
        String randomValue = String.format("%04d", num);
        CurrencyTransactionReportType ctrRpt = context.getXmlDoc().getCurrencyTransactionReport();
		String orgCd = ctrRpt.getReportAdministrationParty().getCode().getStringValue();
		String agentID = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentID+orgCd+"9400"+"FIUF9999"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}


	/**
	 * ���� ���� ��� ���� ���� ���� 
	 * @return ItemAnonType[] ���� ���� ��� ����
	 * @throws AgentException 
	 */
	private ItemAnonType[] makeHeader() throws AgentException {
		int idx = 0;
		
		//IF_ID(8)+����ڵ�(4)+YYYYMMDDHHMMSSFF3+random(4)
		//IIB InterfaceID ���� ����
		String InterfaceID="FIUF9999";
		
		CurrencyTransactionReportType ctrRpt = context.getXmlDoc().getCurrencyTransactionReport();
		String fclty_cd = ctrRpt.getReportAdministrationParty().getCode().getStringValue();
		String now = DateUtil.getDateTime(DateUtil.yyyyMMddHHmmssSSS);		
		Random random = new Random();
		int randomInt=random.nextInt(10000);
		String randomString = String.format("%04d", randomInt);
		String GlobalUniqueID = InterfaceID+fclty_cd+now+randomString;
		
		boolean isEncryption = Configure.getInstance().getAgentInfo().getEncryptionInfo().isEnabled();
		boolean isSigning = Configure.getInstance().getAgentInfo().getEncryptionInfo().getSigningInfo().isEnabled();
		String securityCode;
		if(isEncryption){
			if(isSigning){
				securityCode = "03";
			}else{
				securityCode = "01";
			}
		}else{
			if(isSigning){
				securityCode = "02";
			}else{
				securityCode = "00";
			}
		}
		
		ItemAnonType[] headers = new ItemAnonType[10];		
		
		headers[idx++] = new ItemAnonType("GlobalUniqueID", GlobalUniqueID);		
		
		headers[idx++] = new ItemAnonType("InterfaceID", InterfaceID);
		
		headers[idx++] = new ItemAnonType("SecurityCode", securityCode);
		
		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.MessageCreationDateTime, 
				DateUtil.getDateTime(DateUtil.yyyyMMddHHmmss));
		
		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportFormVersionID
				, kr.go.kofiu.ctr.util.DocumentObject.DOCUMENT_VERSION_NEW);
		
		String module_version = Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR);
		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ModuleVersionID, module_version);

		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationPartyIPName, 
				Configure.getInstance().getAgentInfo().getIp());
		
		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.MessageCode, 
				ctrRpt.getMessageCode().getStringValue());

		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationEncryptionTypeCode,
				context.getEncryptionTypeCode() );
		
		headers[idx++] = new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.AgentID, 
				context.getAgentId());
		return headers;
	}
	
	/**
	 * ���� ���� body ���� ����
	 * @return ItemAnonType[] ���� ���� body ����
	 */
	private ItemAnonType[] makeBodys() {
		// XML For Body Data
		CurrencyTransactionReportType ctrRpt = context.getXmlDoc().getCurrencyTransactionReport();
		ArrayList list = new ArrayList();
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationPartyCode, 
				ctrRpt.getReportAdministrationParty().getCode().getStringValue())); // ���� ����ڵ�
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationPartyName, 
				ctrRpt.getReportAdministrationParty().getName().getStringValue())); // ���� ��� ��
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDocumentID, 
				ctrRpt.getDocumentID().getStringValue())); //���� ��ȣ 
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDate, 
				ctrRpt.getDate())); // ���� ���� 
		
		//	 ����  ���� ���� ��ȣ. ���� �� �� �ְ� ���� ���� �ִ�.
		if( ctrRpt.getFormerDocumentID() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.FormerDocumentID, 
				ctrRpt.getFormerDocumentID().getStringValue())); // ���� ���� ��ȣ
		}

		//	 2006/01/17 �� ������ ���� �� �߰� 
		if( ctrRpt.getMainDocumentID() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.MainDocumentID, 
					ctrRpt.getMainDocumentID().getStringValue())); // �⺻���� ��ȣ(4-1)
		}
		if( ctrRpt.getOrder() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionOrder, 
					ctrRpt.getOrder().getStringValue())); // �ŷ�����
		}
		
		// 2006/01/11 - ��Һ��� �߰��� ���� �ڵ� ���� 
		TransactionPartyType trnPartyType = ctrRpt.getTransactionParty(); 
		if ( trnPartyType != null ) {
			list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyTypeCode, 
					trnPartyType.getTypeCode().getStringValue())); //  �ŷ��� �Ǹ� ��ȣ ���� 
	
			// ������ �ʼ� - 2005/10/10 �� ���� ���� �ݿ� 
			if ( trnPartyType.getNationalityCode() != null ){
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyNationalityCode, 
						trnPartyType.getNationalityCode().getStringValue())); // �ŷ����� ���� 
			}
			
			// 11/23 �߰� 
			if ( trnPartyType.getAddress() != null 
					&& trnPartyType.getAddress().getPostCodeID() != null ) {
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyAdressPostCodeID
						, trnPartyType.getAddress().getPostCodeID().getStringValue())); //���� ���� ��ȣ
			}

			// ���� �� �ʼ�. ���� �ȳ��� �� �ִ�.
			if (trnPartyType.getAccount() != null 
				 && trnPartyType.getAccount().getBranchAddress() != null
				 && trnPartyType.getAccount().getBranchAddress().getPostCodeID() != null ) { 
				
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.AccountOpenPlaceAddressPostCodeID, 
						trnPartyType.getAccount().getBranchAddress().getPostCodeID().getStringValue())); // ���� ������ ���� ��ȣ
			}
		}
		
		// �� ���� ������ �ʼ��� ���� �� ���� �� �ִ�.
		EnterpriseOrganizationType entOrgType = ctrRpt.getEnterpriseOrganization();
		if ( entOrgType != null ){
			// 11/23 �߰� 
			if (entOrgType.getRepresentativePerson() != null  
					&& entOrgType.getRepresentativePerson().getTypeCode() != null){
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.EnterpriseOraganizationRepresentativePersonTypeCode
					, entOrgType.getRepresentativePerson().getTypeCode().getStringValue()));
			}
			
			//	 ǥ�� ��� �з� �ڵ� 
			if ( entOrgType.getKSICCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.EnterpriseOraganizationKSICCode, 
					entOrgType.getKSICCode().getStringValue())); 
			}
			
			// ����� ��ü ���� ��ȣ
			if ( entOrgType.getAddress() != null 
					&& entOrgType.getAddress().getPostCodeID() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.EnterpriseOraganizationAddressPostCodeID, 
					entOrgType.getAddress().getPostCodeID().getStringValue())); 
			}
		} 
		
		TransactionType trnType = ctrRpt.getTransaction();
		if ( trnType != null ){
			if ( trnType.getBranchAddress() != null) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionBranchOfficeAddressPostCodeID, 
					trnType.getBranchAddress().getPostCodeID().getStringValue())); // �ŷ� ������ ���� ��ȣ
			}
			
			if ( trnType.getChannelCode()  != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionChannelCode, 
					trnType.getChannelCode().getStringValue())); // �ŷ� ���
			}
			
			if (trnType.getMeansCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionMeansCode, 
					trnType.getMeansCode().getStringValue())); // �ŷ� ����
			}
			
			if ( trnType.getTypeCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionTypeCode, 
					trnType.getTypeCode().getStringValue())); // �ŷ� ����
			}
			
			// 2006/02/07 �߰� 
			if ( trnType.getDateTime() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionDateTime, 
						trnType.getDateTime()) ); // �ŷ� �߻� �Ͻ�
			}
		}
		
		
		//	 �� ���� ������ �ʼ��� ���� �� ���� �� �ִ�.
		if ( ctrRpt.getSecuritiesDocument() != null ) {
			if ( ctrRpt.getSecuritiesDocument().getTypeCode() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.SecuritiesDocumentTypeCode, 
					ctrRpt.getSecuritiesDocument().getTypeCode().getStringValue())); //���� ���� ���� �ڵ�

			if ( ctrRpt.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.SecuritesDocumentPaymentMainFinancialUnitCode, 
					ctrRpt.getSecuritiesDocument().getPaymentMainFinancialUnitCode().getStringValue())); // ���� ���� �ڵ�
			
			if ( ctrRpt.getSecuritiesDocument().getBranchAddress() != null 
					&& ctrRpt.getSecuritiesDocument().getBranchAddress().getPostCodeID() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.PaymentBranchOfficeAddressPostCodeID, 
					ctrRpt.getSecuritiesDocument().getBranchAddress().getPostCodeID().getStringValue())); //���ÿ����� �����ȣ
		}
		
		//	 �� ���� ������ �ʼ��� ���� �� ���� �� �ִ�.
		if( ctrRpt.getCounterPartParty() != null
				&& ctrRpt.getCounterPartParty().getReceiptOrganizationCode() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CounterPartPartyReceiptOrganizationCode, 
				ctrRpt.getCounterPartParty().getReceiptOrganizationCode().getStringValue())); // ���� ���� ��� �ڵ�
		}
		
		list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.AttachmentFileNameText, 
				context.getFilename())); // ÷�� ���� �� 


		ItemAnonType[] bodys = new ItemAnonType[list.size()];
		Iterator iter = list.iterator();
		for ( int j = 0 ; iter.hasNext() ; j++) {
			bodys[j] = (ItemAnonType)iter.next();
		}
		return bodys;
	}

	
}
