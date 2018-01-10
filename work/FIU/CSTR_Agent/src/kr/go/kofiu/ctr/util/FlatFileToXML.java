package kr.go.kofiu.ctr.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.AccountType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.AddressType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.CounterPartPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.EnterpriseOrganizationType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.ReportAdministrationPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.RepresentativePersonType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.SecuritiesDocumentType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.TransactionPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.TransactionType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.EnterpriseOrganizationType.Contact;
import krGovKofiuDataRootSchemaModule10.CurrencyTransactionReportDocument;
import krGovKofiuDataRootSchemaModule10.CurrencyTransactionReportType;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.xml.sax.SAXException;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : FlatFile�� �о XML�� ǥ���ϴ� �ڹ� ��ü�� ��ȯ 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileToXML {

	/**
	 * FlatFile ����Ÿ
	 */
	private String flatdata;
	//private String filename;

	/**
	 * 
	 * @param flatdata
	 */
	public FlatFileToXML(String flatdata, String filename)
	{
		this.flatdata = flatdata;
		//this.filename = filename;
	}

	/**
	 * FlatFile����Ÿ�� �̿��Ͽ� XML�� ������ �� �ִ� CurrencyTransactionReportDocument ��ü�� 
	 * �����Ѵ�.
	 * @return
	 * @throws FlatFileConvertingException
	 */
	public CurrencyTransactionReportDocument ChkAndGetXMLDoc() throws FlatFileConvertingException
	{
		String[] tokens = flatdata.split("\\|\\|"); //parse By Delimiter("||");

		try{
			CurrencyTransactionReportDocument ctrDoc = convertToXML(tokens);
			checkConditionalMandatory(ctrDoc);
			return ctrDoc;
		} catch (FlatFileConvertingException e) {
			throw e;
		} catch (Exception e) {
			throw new FlatFileConvertingException(e);
		}
	}
	
	/**
	 * ������ �ʼ� ���� üũ�Ѵ�. 
	 * @param document
	 * @throws FlatFileConvertingException
	 */
	public void checkConditionalMandatory(CurrencyTransactionReportDocument document) throws FlatFileConvertingException {
		CurrencyTransactionReportType ctr = document.getCurrencyTransactionReport();
		if ( ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND) 
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			// ���/���� ����
			if ( ctr.getFormerDocumentID() == null || ctr.getFormerDocumentID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�޼���Ÿ��(01) ����  " + ctr.getMessageCode().getStringValue() 
						+ "�Դϴ�. ����, ���/��������� ���� ���� ��ȣ ������ �ʿ��մϴ�.");
			}
		}
		
		// ��� ���� ��  ���� üũ ����. 2006/01/11�� �߰� 
		if ( ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			return ;
		}

		// 12/29�� �ݿ�. 11 : �ŷ��� �Ǹ� ��ȣ , 10 : �ŷ��� �Ǹ� ��ȣ ���� ���� ���� ��� 
		// �Ǹ����չ�ȣ(14)���� �ŷ��ڱ����ڵ�(15-1)���� �־�� �ȴ�. �� KR�� ��� ���� 
		TransactionPartyType tpt = ctr.getTransactionParty();
		if ( tpt.getTypeCode() == null 
				|| tpt.getTypeCode().getStringValue() == null 
				|| "".equals(tpt.getTypeCode().getStringValue())
		     || tpt.getID() == null 
				|| tpt.getID().getStringValue() == null 
				|| "".equals(tpt.getID().getStringValue()) ) {
			
			if ( tpt.getNationalityCode() == null 
					|| tpt.getNationalityCode().getStringValue() == null
					|| "".equals(tpt.getNationalityCode().getStringValue())) {
				throw new FlatFileConvertingException("�ŷ��ڱ����ڵ�(15-1)�� �������� �ʽ��ϴ�. ����, �ŷ���(�����) �Ǹ��ȣ����(10) ���� �ŷ���(�����)�Ǹ��ȣ(11)���� �ʿ��մϴ�.");
			} else {
				String nationalityCode = tpt.getNationalityCode().getStringValue();
				if ( "KR".equalsIgnoreCase(nationalityCode)){
					// error �Ǹ� ���� ��ȣ�� �����ϸ� �ȵ�
					throw new FlatFileConvertingException("�ŷ���(�����)�Ǹ��ȣ����(10), �ŷ���(�����)�Ǹ� ��ȣ(11) ���� " 
							+" �������� �ʽ��ϴ�. �� ��� �ŷ��ڱ����ڵ�(15-1)���� �Ǹ����� ��ȣ(��ܵ���/�ܱ���)(14) ���� " 
							+ " �ʿ��ѵ� �Է��Ͻ� �ŷ��ڱ����ڵ�(15-1)���� '" + nationalityCode 
							+ "' �Դϴ�. ���� �Ǹ����� ��ȣ(��ܵ���/�ܱ���)(14)�� �����ϸ� �ȵ˴ϴ�. ����Ÿ�� ���ռ��� üũ�ϼ���.");
				} else {
					// ���� �ڵ� �� KR�� �ƴϴ�. �Ǹ����� ��ȣ üũ 
					if ( tpt.getMixedRealNameID() == null 
							|| tpt.getMixedRealNameID().getStringValue() == null
							|| "".equals(tpt.getMixedRealNameID().getStringValue()) ) {
						throw new FlatFileConvertingException("�Ǹ����� ��ȣ(��ܵ���/�ܱ���)(14)�� �������� �ʽ��ϴ�. ����, �ŷ���(�����) �Ǹ��ȣ����(10) ���� �ŷ���(�����)�Ǹ��ȣ(11)���� �ʿ��մϴ�.");
					}
				}
			}
		}

	
		if ( tpt.getTypeCode() != null && !"".equals(tpt.getTypeCode().getStringValue())){
			String TPTypeCode = tpt.getTypeCode().getStringValue(); // �ŷ��� �Ǹ� ��ȣ ���� 
			
			if (  !"01".equals(TPTypeCode)      // �ֹ� ��� ��ȣ(����)
					&& !"04".equals(TPTypeCode)  // ���� ��ȣ 
					&& !"06".equals(TPTypeCode)  // �ܱ��� ��� ��ȣ 
					&& !"07".equals(TPTypeCode) // ����  �ż� �Ű��ȣ 
					&& !"99".equals(TPTypeCode)) // ��Ÿ
			{
				if (  tpt.getAddress() != null 
						&& tpt.getAddress().getLineOne() != null) {
					String address = tpt.getAddress().getLineOne().getStringValue();
					if ( address != null && address.length() > 0 ) {
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '01','04','06','07','99' �� �ƴϰ� '" + TPTypeCode + "'�Դϴ�. ����, �ŷ��� �ּ�(12-1) ����  '" + address + "' �����ϸ� �ȵ˴ϴ�.");
					}
				}
				
				if ( tpt.getAddress() != null && tpt.getAddress().getPostCodeID() != null ){
					String postCodeID = tpt.getAddress().getPostCodeID().getStringValue();
					if ( postCodeID != null && postCodeID.length() > 0 ) 
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '01','04','06','07','99' �� �ƴϰ� '" + TPTypeCode + "'�Դϴ�. ����, �ŷ��� �����ȣ(12) ���� '" + postCodeID + "' �����ϸ� �ȵ˴ϴ�.");
				}
	
				if ( tpt.getContact() != null &&  tpt.getContact().getTelephoneID() != null ) {
					String telephoneID = tpt.getContact().getTelephoneID().getStringValue();
					if ( telephoneID != null && telephoneID.length() > 0 ) 
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '01','04','06','07','99' �� �ƴϰ� '" + TPTypeCode + "'�Դϴ�. ����, �ŷ��� ��ȭ��ȣ(13) ���� '" + telephoneID + "' �����ϸ� �ȵ˴ϴ�.");
				}
				
				// 10�� 14d�� ���� ���� �ݿ� 
				
				if ( tpt.getNationalityCode() != null ) { 
					String nationalityCode = tpt.getNationalityCode().getStringValue();
					if ( nationalityCode != null && nationalityCode.length() > 0 ) 
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '01','04','06','07','99' �� �ƴϰ� '" + TPTypeCode + "'�Դϴ�. ����, �ŷ��ڱ����ڵ�(15-1) ���� '" + nationalityCode + "'�����ϸ� �ȵ˴ϴ�.");
				}
				
				if ( tpt.getNationality() != null ) {
					String nationality = tpt.getNationality().getStringValue();
					if (nationality != null && nationality.length() > 0 ) 
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '01','04','06','07','99' �� �ƴϰ� '" + TPTypeCode + "'�Դϴ�. ����, �ŷ��ڱ�������(15) '" + nationality + "' �����ϸ� �ȵ˴ϴ�.");
				}
			}
			
			// ����/��ü �ŷ��� ������ ����� ��� ��ȣ , ���� ��� ��ȣ, �ֹι�ȣ ��ü�ϰ�� �� ����. �� ��찡 �ƴ� ��� ����Ǹ� ����  
			if ( !"02".equals(TPTypeCode)      // �ֹ� ��� ��ȣ (��ü )
				  && !"03".equals(TPTypeCode)  // ����� ��� ��ȣ 
				  && !"08".equals(TPTypeCode) // ���� ��� ��ȣ 
				  && !"10".equals(TPTypeCode) // ������ȣ
				  && !"09".equals(TPTypeCode) // ������ȣ 
				  && !"99".equals(TPTypeCode)) // ��Ÿ
			{
				EnterpriseOrganizationType entOrgType = ctr.getEnterpriseOrganization();
				if ( entOrgType != null ) {
					RepresentativePersonType person  = entOrgType.getRepresentativePerson();
					if ( person != null ){
						CheckTransactionPartyTypeCode(TPTypeCode, person.getName());
						CheckTransactionPartyTypeCode(TPTypeCode, person.getID());
					}
					String establishDate = entOrgType.getEstablishmentDate();
					if ( establishDate != null ) {
						throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '02','03','08','10','09','99' �� �ƴϰ�  '" + TPTypeCode + "'�Դϴ�. ����, ���ü(��ü)������(19) ���� '" + establishDate + "' �����ϸ� �ȵ˴ϴ�.");
					}
					
					CheckTransactionPartyTypeCode(TPTypeCode, entOrgType.getKSICCode());
		
					AddressType addressType = entOrgType.getAddress();
					if ( addressType != null ) {
						CheckTransactionPartyTypeCode(TPTypeCode, addressType.getPostCodeID());
						CheckTransactionPartyTypeCode(TPTypeCode, addressType.getLineOne());
					}
					
					Contact contact = entOrgType.getContact();
					if (contact != null )
						CheckTransactionPartyTypeCode(TPTypeCode, contact.getTelephoneID());
				}
			}
			
			//20140311 ������ �Ǹ��ȣ üũ ���� ����
			//validation check �߰� : �ŷ���(�����) �Ǹ��ȣ����(Field 13) ���� ���� �Ǹ��ȣ ����
			/* �ֹε�Ϲ�ȣ(01), �ֹε�Ϲ�ȣ(��Ÿ��ü)(02),���ε�Ϲ�ȣ(05), �ܱ��ε�Ϲ�ȣ(06), �����żҽŰ��ȣ(07) : 13�ڸ�
			 * ����� ��Ϲ�ȣ(03) : 10�ڸ�
			 * �̿� : 15�ڸ� �̳�
			 * */
			if (   "01".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "02".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "05".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "06".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "07".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
				
				// Conditional Mandatory : �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,02,05,06,07�� ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 13�ڸ��̾�� �� .
				if (ctr.getTransactionParty().getID().getStringValue().length() != 13){
					throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� '01','02','05','06','07'�� ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 13�ڸ��̾�� �մϴ�.");
				}
				
			} else if ( "03".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
					
				// Conditional Mandatory : �ŷ��ڰ� ����� (Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 03�� ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 10�ڸ��̾�� �� .
				if (ctr.getTransactionParty().getID().getStringValue().length() != 10){
					throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� '03'�� ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 10�ڸ��̾�� �մϴ�.");
				}
					
			} else {
				//Conditional Mandatory : �ŷ��� �Ǹ��ȣ�� 15�ڸ� ����.
				if (ctr.getTransactionParty().getID().getStringValue().length() > 15){
					throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���  �Ǹ��ȣ(Field 14) �ڸ����� 15�ڸ� �����Դϴ�.");
				}
			}
			
		} // end of if TypeCode : �ŷ��� �Ǹ��ȣ ���� 


		TransactionType tranType = ctr.getTransaction();
		String TTypeCode = tranType.getTypeCode().getStringValue(); // �ŷ� ����(=�ŷ� ����) 
		if ( "11".equalsIgnoreCase(TTypeCode)       // ���� + ���� 
			  || "12".equalsIgnoreCase(TTypeCode) ) // ���� + ����
		{
			// ���� ������ , ���� ������ �����ȣ , ���� ������, ���ð��� ��ȣ ��� �ʼ� 
			AccountType accType = tpt.getAccount();
			if ( accType == null ) {
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���ð��¹�ȣ(29),���°�����(30),���°����������ȣ(30-1),���� ������(31) ������ �ʿ��մϴ�.");
			}

			if ( accType.getBranchFinancialUnitName() == null || accType.getBranchFinancialUnitName().getStringValue().length() < 1) {
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���°�����(30) ������ �ʿ��մϴ�.");
			}

			if (accType.getBranchAddress() == null 
					|| accType.getBranchAddress().getPostCodeID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���°����������ȣ(30-1) ������ �ʿ��մϴ�.");
			}
			
			String openDate = accType.getOpenDate();
			if ( openDate == null || openDate.length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���� ������(31)�� �ʿ��մϴ�. ���� �Է� ���� '" + openDate +"' �Դϴ�.");
			}
			
			if ( accType.getID() == null || accType.getID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���ð��¹�ȣ(29)�� �ʿ��մϴ�.");
			}			
		}
		
		// 10/14 ���� ���� �ݿ� 
		if ( "25".equalsIgnoreCase(TTypeCode)      // �����(���� ���� )+ ���� 
				 || "26".equalsIgnoreCase(TTypeCode) ) // �����(���� ���� ) + ����
		{
			SecuritiesDocumentType securities = ctr.getSecuritiesDocument();
			// ���� ���� ���� , ���� ���� ���� ��ȣ �ʼ� 
			if ( securities == null )
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ������������ ������ �ʿ��մϴ�.");				
			
			if ( securities.getTypeCode() == null
					|| securities.getTypeCode().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ����������������(32) ���� �ʿ��մϴ�.");
			}			
			
			//11. ����ī���� ��� �����������ǽ��۹�ȣ(33),�����������ǳ���ȣ(33-1) �� �����׸� 
			if ( !"11".equals(securities.getTypeCode().getStringValue()) ){
				if ( securities.getStartID() == null || securities.getStartID().getStringValue().length() < 1 ){
					throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �̰� ����������������(32)���� '" + securities.getTypeCode().getStringValue()+ "' �Դϴ�. ���� �����������ǽ��۹�ȣ(33)�� �ʿ��մϴ�.");
				}		
				// ���� ��ȣ�� �� �� �� �ִ�.
				//if ( securities.getEndID() == null || securities.getEndID().getStringValue().length() < 1 ){
					//throw new FlatFileConvertingException("�ŷ����� �ڵ� ���� '" + TTypeCode + "' �Դϴ�. ���� ���� �������������ȣ�� �ʿ��մϴ�.");
				//}			
			}
			
			// 05. �ڱ�� ��ǥ�� ��� ���� (�����׸�)
			if ( !"05".equals(securities.getTypeCode().getStringValue()) ){
				if (  securities.getPaymentMainFinancialUnitName() != null ) {
					String value = securities.getPaymentMainFinancialUnitName().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("����������������(32) ���� '05.�ڱ�� ��ǥ'�� �ƴϰ� '" + securities.getTypeCode().getStringValue() + "'�Դϴ�. ����, ���������(34) '" + value + "' ����  �����ϸ� �ȵ˴ϴ�.");
					}
				}
				if (  securities.getPaymentMainFinancialUnitCode() != null ) {
					String value = securities.getPaymentMainFinancialUnitCode().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("����������������(32) ���� '05.�ڱ�� ��ǥ'�� �ƴϰ� '" + securities.getTypeCode().getStringValue() + "'�Դϴ�. ����, �����������ڵ�(34-1) '" + value + "' ����  �����ϸ� �ȵ˴ϴ�.");
					}
				}
				if (  securities.getPaymentBranchFinancialUnitName() != null ) {
					String value = securities.getPaymentBranchFinancialUnitName().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("����������������(32) ���� '05.�ڱ�� ��ǥ'�� �ƴϰ� '" + securities.getTypeCode().getStringValue() + "'�Դϴ�. ����, ���ÿ����� ��(35) '" + value + "'��  �����ϸ� �ȵ˴ϴ�.");
					}
				}
				if (  securities.getBranchAddress() != null 
						&& securities.getBranchAddress().getPostCodeID() != null ) {
					String value = securities.getBranchAddress().getPostCodeID().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("����������������(32) ���� '05.�ڱ�� ��ǥ'�� �ƴϰ� '" + securities.getTypeCode().getStringValue() + "'�Դϴ�. ����, ���ÿ����������ȣ(35-1) '" + value + "'��  �����ϸ� �ȵ˴ϴ�.");
					}
				}
			}
			
		}
	
		if ( "22".equalsIgnoreCase(TTypeCode) )     // ����� (�۱�) + ����  
		{
			CounterPartPartyType cppType = ctr.getCounterPartParty();
			// ������� ����� , ������� ��� �ڵ� , ������(�������)����, ������ ���¹�ȣ  �ʼ� 
			if ( cppType == null )
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ���� ���� ��� ������ �ʿ��մϴ�.");
				
			if ( cppType.getReceiptOrganizationName() == null 
					|| cppType.getReceiptOrganizationName().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ������������(37) ������ �ʿ��մϴ�.");
			}		

			if ( cppType.getReceiptOrganizationCode() == null 
					|| cppType.getReceiptOrganizationCode().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� �����������ڵ�(37-1) ������ �ʿ��մϴ�.");
			}		

			// 'ZZZZ' �ؿ� �۱� �� ��� �ڵ� ��.
//			if ( !"ZZZZ".equals(cppType.getReceiptOrganizationCode().getStringValue()) ) { 
	//		}
			
			if ( cppType.getName() == null || cppType.getName().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ������(���������)����(38) ������ �ʿ��մϴ�.");
			}		
			
			if ( cppType.getAccount() == null )
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� �����ΰ��¹�ȣ(36) ������ �ʿ��մϴ�.");
				
			if (  cppType.getAccount().getID() == null
					||  cppType.getAccount().getID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("�ŷ�����(28) ���� '" + TTypeCode + "' �Դϴ�. ���� ������ ���� ��ȣ  ������ �ʿ��մϴ�.");
			}		
		}
	}
	
	/**
	 * TransactionPartyTypeCode�� üũ�Ѵ�.
	 * @param TPTypeCode
	 * @param value
	 * @throws FlatFileConvertingException
	 */
	private void CheckTransactionPartyTypeCode(String TPTypeCode, XmlAnySimpleType value) throws FlatFileConvertingException{
		// ���� ������ ��� ���� ó��.
		if ( value != null  ) {
			throw new FlatFileConvertingException("�ŷ���(�����) �Ǹ��ȣ����(10) ���� '02','03','08','10','09','99'�� �ƴϰ�  '" + TPTypeCode + "' �Դϴ�. ���� ���� ��ü �ŷ��� ���� ��  '" + value + "' �� �����ϸ� �ȵ˴ϴ�.");
		}
	}
	
	/**
	 * FlatFile ����Ÿ�� CurrencyTransactionReportDocument Ÿ���� �ڹ� ��ü�� ��ȯ 
	 * @param vec FlatFile ����Ÿ�� Strng[] ���·� �о� ���� ��
	 * @return
	 * @throws IOException 
	 */
	private CurrencyTransactionReportDocument convertToXML(String[] vec) throws IOException
	{
	     // ArrayList validationErrors = new ArrayList();
	     // XmlOptions validationOptions = new XmlOptions();
	     // validationOptions.setValidateOnSet();
	     // validationOptions.setErrorListener(validationErrors);
		
		  CurrencyTransactionReportDocument poDoc =	CurrencyTransactionReportDocument.Factory.newInstance();
		  		//CurrencyTransactionReportDocument.Factory.newInstance(validationOptions);
		  
		  CurrencyTransactionReportType newCTR = poDoc.addNewCurrencyTransactionReport();
		 
		  int XMLIndex = 0; // 0 index is CTRSTART
		  String tempVal = null;
		  
		  //CurrencyTransactionReport/DocumentIdentifier
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewDocumentID().setStringValue(tempVal);
		  }
		  
		  //CurrencyTransactionReport/MessageCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewMessageCode().setStringValue(tempVal);
		  }
		  
		  //CurrencyTransactionReport/Date
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.setDate(tempVal);
		  }

		  //CurrencyTransactionReport/FormerDocumentIdentifier
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewFormerDocumentID().setStringValue(tempVal);
		  }
		  
		  //CurrencyTransactionReport/Order
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewOrder().setStringValue(tempVal);
		  }
		  //CurrencyTransactionReport/MainDocumentIdentifier
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewMainDocumentID().setStringValue(tempVal);
		  }
		  //CurrencyTransactionReport/PersonName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewPersonName().setStringValue(tempVal);
		  }
 
		  // CurrencyTransactionReport/ReportAdministrationParty
		  ReportAdministrationPartyType newRAP = newCTR.addNewReportAdministrationParty();
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  // CurrencyTransactionReport/TransactionParty
		  TransactionPartyType newTP = newCTR.addNewTransactionParty();
		  boolean isTrnPartyNil = true;

		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewTypeCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewID().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewNationalityCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewNationality().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNil = false;
			  newTP.addNewMixedRealNameID().setStringValue(tempVal);
		  }

		  // ������ �ʼ�/optional �׸� 
		  AccountType newAcc = newTP.addNewAccount();
		  boolean isNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAcc.addNewID().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAcc.setOpenDate(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAcc.addNewBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAcc.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }
		  if ( isNil ) newTP.unsetAccount();
		  else 	isTrnPartyNil = false;

		  // ---------- end of Account type
		  
		  AddressType newAddr = newTP.addNewAddress();
		  isNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAddr.addNewLineOne().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAddr.addNewPostCodeID().setStringValue(tempVal);
		  }

		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newTP.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }

		  if ( isNil )  newTP.unsetAddress();
		  else 	isTrnPartyNil = false;

		  if ( isTrnPartyNil ) newCTR.unsetTransactionParty();

		  // CurrencyTransactionReport/EnterpriseOrganization
		  // EnterpriseOrganization ������ �ʼ� �׸� 
		  EnterpriseOrganizationType newEnterOrg = newCTR.addNewEnterpriseOrganization();
		  boolean isEntOrgNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNil = false;
			  newEnterOrg.setEstablishmentDate(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNil = false;
			  newEnterOrg.addNewKSICCode().setStringValue(tempVal);
		  }
		  
		  RepresentativePersonType newRP = newEnterOrg.addNewRepresentativePerson();
		  isNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newRP.addNewTypeCode().setStringValue(tempVal);
		  }

		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newRP.addNewID().setStringValue(tempVal);
		  }
		  
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newRP.addNewName().setStringValue(tempVal);
		  }
		  if ( isNil ) newEnterOrg.unsetRepresentativePerson();
		  else 	isEntOrgNil = false;

		  
		  AddressType newAddr2 = newEnterOrg.addNewAddress();
		  isNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAddr2.addNewLineOne().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newAddr2.addNewPostCodeID().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newEnterOrg.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  if ( isNil )  newEnterOrg.unsetAddress();
		  else 	isEntOrgNil = false;

		  if ( isEntOrgNil )  newCTR.unsetEnterpriseOrganization();
		  // EnterpriseOrganization ������ �ʼ� �׸� �� 

		  // CurrencyTransactionReport/Transaction
		  TransactionType newTran = newCTR.addNewTransaction();
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.setDateTime(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewTypeCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewMeansCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewChannelCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewAmount().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewPersonName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }

		  // CurrencyTransactionReport/SecuritiesDocument
		  // ������ �ʼ�/optional �׸� 
		  SecuritiesDocumentType newSD = newCTR.addNewSecuritiesDocument();
		  isNil = true;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewTypeCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewStartID().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewEndID().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewPaymentMainFinancialUnitName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewPaymentMainFinancialUnitCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewPaymentBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isNil = false;
			  newSD.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }
		  if ( isNil ) newCTR.unsetSecuritiesDocument();
		  
		  // CurrencyTransactionReport/CounterPartParty
		  // ������ �ʼ�/optional �׸� 
		  CounterPartPartyType newCPP = null;
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();
			  
			  newCPP.addNewName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewReceiptOrganizationName().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewReceiptOrganizationCode().setStringValue(tempVal);
		  }
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewAccount().addNewID().setStringValue(tempVal);
		  }
		  
		  // 2006/01/11 �߰� - ���/���� ����
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewCancellationReason().setStringValue(tempVal);
		  }
		  return poDoc;
	}
	
	/**
	 * �ش� Index(Ű ��)�� ������Ƽ ���� �о�´�. 
	 * @param tokens
	 * @param index
	 * @return
	 */
	public String getValue(String[] tokens, int index){
		Message message = FlatFileChecker.getMessageById("100");
		Field field = message.getFieldByXmlSeq(Integer.toString(index));
		
		String val = tokens[field.getId()];
		
		if ( val != null && val.length() > 0 )
			return val;
		else
			return null;
	}
	
	public static void main(String[] args) throws FlatFileConvertingException, SAXException, IOException, ParserConfigurationException{
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		FlatFileToXML test = new FlatFileToXML("CTRSTART||01||2005-00000001||20050831||||01/99||2005-00000001||��ȯ����||1234||����å����||�谩��||02-2116-2680||ȫ�浿||710222456123||01||KR||���ѹα�||145251||��õ�� ���絿||02-789-4781||||||||||||||||||20051201121212||���ҹ�����||145754||18||11||50000000||11||��õ����||125456||20030228||11-00-56-874||01||1452477||��õ||1104||��û��||145754||||||||||CTREND","testfile.snd");
 		 		
		
		// optional / mandantory checking
  		DocumentBuilderFactory factory = 
  							DocumentBuilderFactory.newInstance();
  		
  		factory.setNamespaceAware(true);
  		factory.setValidating(true);
		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
  		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource","file:D:/Projects/FIU/work/Schema/schemas/v2/CurrencyTransactionReport.xsd");
  		DocumentBuilder builder = factory.newDocumentBuilder();
  		
  	//	builder.setErrorHandler( new SimpleErrorHandler() );
  		builder.parse(test.ChkAndGetXMLDoc().newInputStream());
 	}

}
