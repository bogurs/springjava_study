package kr.go.kofiu.ctr.util;

import java.io.IOException;

//import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.AccountType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.CounterPartPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.EnterpriseOrganizationType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.ReportAdministrationPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.RepresentativePersonType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.SecuritiesDocumentType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.TransactionPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.JobType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.OpenAgentType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.TransactionType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.AgentType;
import krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportDocument;
import krGovKofiuDataRootSchemaModule11.CurrencyTransactionReportType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule11.AddressType;
/*******************************************************
 * <pre> 
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : FlatFile�� �о XML�� ǥ���ϴ� �ڹ� ��ü�� ��ȯ 
 * ��   ��   ��  : �̼���
 * ��   ��   ��  : 2007. 11. 01
 * <pre>
 *******************************************************/
public class FlatFileToXML089 {
 
	/**
	 * FlatFile ����Ÿ
	 */
	private String flatdata;
	//private String filename;

	/**
	 * 
	 * @param flatdata
	 */
	public FlatFileToXML089(String flatdata, String filename)
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
	 * FlatFile���� XML�� ����� ��ü�� �̿��Ͽ� �� condition���� conditional mandatory �� mandatory�� check�Ѵ�. 
	 * @param document
	 * @throws FlatFileConvertingException
	 */
	public void checkConditionalMandatory(CurrencyTransactionReportDocument document) throws FlatFileConvertingException {
		CurrencyTransactionReportType ctr = document.getCurrencyTransactionReport();
		// Mandatory 01 : �޽���Ÿ��(Field 01)
		if ( ctr.getMessageCode() == null ){ 
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01)�� ���� �����ϴ�.");
		}
		
		// Mandatory 02 : ������ȣ(Field 02)
		if ( ctr.getDocumentID() == null ){ 
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ������ȣ(Field 02)�� ���� �����ϴ�.");
		}
		
		// Mandatory 03 : ��������(Field 03)
		if ( ctr.getDate() == null ){ 
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ��������(Field 03)�� ���� �����ϴ�.");
		}
		
		if (   ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND) 
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			// Conditional Mandatory 01 : ���/���������� ��쿡�� ����������ȣ(Field 04)�� �ݵ�� �ʿ���.
			if ( ctr.getFormerDocumentID() == null ){
				throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �޽���Ÿ��(Field 01) ����  02 �Ǵ� 03�̸� ���� �Ǵ� ��Һ����̹Ƿ� ����������ȣ(Field 04)�� �ݵ�� �ʿ��մϴ�.");
			}
			// Conditional Mandatory 50 : ���/���������� ��쿡�� ��� �Ǵ� ���� ����(Field 51)�� �ݵ�� �ʿ���.
			if ( ctr.getCancellationReason() == null ){
				throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �޽���Ÿ��(Field 01) ����  02 �Ǵ� 03�̸� ���� �Ǵ� ��Һ����̹Ƿ� ��� �Ǵ� ���� ����(Field 82)��  �ݵ�� �ʿ��մϴ�.");
			}
		}
		else {
			if ( ctr.getFormerDocumentID() != null ){
				// Conditional Mandatory 02 : �űԺ����� ��쿡�� ����������ȣ(Field 04)�� ���� ����� ��.
				throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �޽���Ÿ��(Field 01) ����  01�̸� �űԺ����̹Ƿ� ����������ȣ(Field 04)�� ���� ������ �� �����ϴ�.");
			}
			
			if ( ctr.getCancellationReason() != null ){
				// Conditional Mandatory 02 : �űԺ����� ��쿡�� ��� �Ǵ� ���� ����(Field 51)�� ���� ����� ��.
				throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �޽���Ÿ��(Field 01) ����  01�̸� �űԺ����̹Ƿ� ��� �Ǵ� ���� ����(Field 82)�� ���� ������ �� �����ϴ�.");
			}
		}
		
		// Mandatory 04 : �ŷ�����(Field 05)
		if ( ctr.getOrder() == null ){ 
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ�����(Field 05)�� ���� �����ϴ�.");
		}
		
		// Mandatory 05 : �⺻������ȣ(Field 06)
		if ( ctr.getMainDocumentID() == null ){ 
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �⺻������ȣ(Field 06)�� ���� �����ϴ�.");
		}

		
		
		// �ű� �Ǵ� ���� ������ ��
		if(    ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.SND) 
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_SND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)){
			
			if ( ctr.getReportAdministrationParty() != null ){
				// Mandatory 06 : ��������(Field 07)
				if (ctr.getReportAdministrationParty().getName() == null) {
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ��������(Field 07)�� ���� �����ϴ�.");
				}
				
				// Mandatory 07 : �������ڵ�(Field 08)
				if (ctr.getReportAdministrationParty().getCode() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڵ�(Field 08)�� ���� �����ϴ�.");
				}
				
				// Mandatory 10 : ����������ȭ��ȣ(Field 11)
				if (ctr.getReportAdministrationParty().getContact() != null){
					if (ctr.getReportAdministrationParty().getContact().getTelephoneID() == null){
						throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����������ȭ��ȣ(Field 11)�� ���� �����ϴ�.");
					}
				}
				else {
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����������ȭ��ȣ(Field 11)�� ���� �����ϴ�.");
				}
			}
			else {
				// ���������� ��ü�� ����. Mandatory 06 �������� ���� �޼����� ��ü��.
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ��������(Field 07)�� ���� �����ϴ�.");
			}
			
			// Mandatory 08 : ����å���ڸ�(Field 09)
			if (ctr.getPersonName() == null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����å���ڸ�(Field 09)�� ���� �����ϴ�.");
			}
			
			if (ctr.getTransaction() != null){
				if (ctr.getTransaction().getPersonName() == null){
					// Mandatory 09 : �������ڸ�(Field 10)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڸ�(Field 10)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getDateTime() == null){
					// Mandatory 14 : �ŷ��߻��Ͻ�(Field 29)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ��߻��Ͻ�(Field 42)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getBranchFinancialUnitName() == null){
					// Mandatory 15 : �ŷ���������(Field 30)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ���������(Field 43)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getChannelCode() == null){
					// Mandatory 16 : �ŷ�ä�� �ڵ�(Field 32)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ�ä�� �ڵ�(Field 45)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getMeansCode() == null){
					// Mandatory 17 : �ŷ����� �ڵ�(Field 33)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ����� �ڵ�(Field 46)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getAmount() == null){
					// Mandatory 18 : �ŷ��ݾ�(Field 34)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ��ݾ�(Field 47)�� ���� �����ϴ�.");
				}
				
				if (ctr.getTransaction().getTypeCode() == null){
					// Mandatory 19 : �ŷ����� �ڵ�(Field 48)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)�� ���� �����ϴ�.");
				}
				else {
					// ���°ŷ��� ���
					if (   "11".equals(ctr.getTransaction().getTypeCode().getStringValue())
						|| "12".equals(ctr.getTransaction().getTypeCode().getStringValue())){
	
						if (ctr.getTransactionParty().getAccount() != null) {
							// Conditional Mandatory 02 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� �ݵ�� ���ð��¹�ȣ(Field 58)���� �ʿ���.
							if(ctr.getTransactionParty().getAccount().getID() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� ���ð��¹�ȣ(Field 58)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 03 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� �ݵ�� ���°�����������(Field 59)���� �ʿ���.
							if(ctr.getTransactionParty().getAccount().getBranchFinancialUnitName() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� ���°�����������(Field 59)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 04 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� �ݵ�� ���°��������� �����ȣ(Field 60)���� �ʿ���.
							if(ctr.getTransactionParty().getAccount().getBranchAddress() != null){
								if (ctr.getTransactionParty().getAccount().getBranchAddress().getPostCodeID() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� �����°��������� �����ȣ(Field 60)����  �ݵ�� �ʿ��մϴ�.");
								}
							} else {
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� �����°��������� �����ȣ(Field 60)����  �ݵ�� �ʿ��մϴ�.");
							}
													
							// Conditional Mandatory 05 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� �ݵ�� ���°�������(Field 61)���� �ʿ���.
							if(ctr.getTransactionParty().getAccount().getOpenDate() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� ���°�������(Field 61)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 40 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� �ݵ�� ���°����븮�� ���翩��(Field 62)���� �ʿ���.
							if(ctr.getTransactionParty().getAccount().getOpenAgentYN() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� ���°����븮�� ���翩��(Field 62)����  �ݵ�� �ʿ��մϴ�.");
							}
							else if("1".equals(ctr.getTransactionParty().getAccount().getOpenAgentYN().getStringValue())){// ���°����븮���� ������ ����� ������� üũ
								if (ctr.getTransactionParty().getAccount().getOpenAgent() != null){
									// Conditional Mandatory 41 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�θ�(Field 63)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getName() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�θ�(Field 63)���� �ݵ�� �ʿ��մϴ�.");
									}
									
									// Conditional Mandatory 42 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �Ǹ��ȣ����(Field 64)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getTypeCode() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �Ǹ��ȣ����(Field 64)���� �ݵ�� �ʿ��մϴ�.");
									}
	
									// Conditional Mandatory 43 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �Ǹ��ȣ(Field 65)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getID() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �Ǹ��ȣ(Field 65)���� �ݵ�� �ʿ��մϴ�.");
									}
									
									// Conditional Mandatory 44 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �����ڵ�(Field 67)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationalityCode() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �����°����븮�� �����ڵ�(Field 67)���� �ݵ�� �ʿ��մϴ�.");
									}
	
									// Conditional Mandatory 45 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� ������(Field 68)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationality() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� ������(Field 68)���� �ݵ�� �ʿ��մϴ�.");
									}
	
									// Conditional Mandatory 46 : ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �����ڵ�(Field 69)���� �ݵ�� �ʿ���.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�� �����ڵ�(Field 69)���� �ݵ�� �ʿ��մϴ�.");
									}
									else {
										if ("99".equals(ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode().getStringValue())){
											// Conditional Mandatory 47 : ���°����븮�� �����ڵ�(Field 69)�� ���� 99(��Ÿ)�� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 81)���� �ݵ�� �ʿ���.
											if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() == null){
												throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� �����ڵ�(Field 69)�� ���� 99(��Ÿ)�� ��� ���°����븮�� �����(Field 70)���� �ݵ�� �ʿ��մϴ�.");
											}
										}
										else {
											// Conditional Mandatory 48 : ���°����븮�� �����ڵ�(Field 69)�� ���� 99(��Ÿ)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 81)���� ������ �� ����.
											if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() != null){
												throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� �����ڵ�(Field 69)�� ���� 99(��Ÿ)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 70)���� ������ �� �����ϴ�.");
											}
										}
									}
								}
								else {
									// ���°����븮�� ���翩��(Field 62)�� 1(����)�� ���������� ���°����븮�� ���� ��ü�� ����. Conditional Mandatory 41�� ���� �޼����� ��ü.
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� ��� ���°����븮�θ�(Field 63)���� �ݵ�� �ʿ��մϴ�.");
								}
							}
							else {
								// Conditional Mandatory 49 : ���°����븮�� ���翩��(Field 62)�� 2(����) �Ǵ� 3(�ľ��� �� ����)�� ��� ���°����븮�� ���� �����Ͱ� ����� ��.
								if (ctr.getTransactionParty().getAccount().getOpenAgent() != null){
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getName() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�θ�(Field 63)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getTypeCode() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� �Ǹ��ȣ����(Field 64)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getID() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� �Ǹ��ȣ(Field 65)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getContact() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� ��ȭ��ȣ(Field 66)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationalityCode() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� �����ڵ�(Field 67)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationality() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� ������(Field 68)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� �����ڵ�(Field 69)���� ������ �� �����ϴ�.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] ���°����븮�� ���翩��(Field 62)���� 1(����)�� �ƴ� ��� ���°����븮�� �����(Field 70)���� ������ �� �����ϴ�.");
									}
								}
							}
						}
						else {
							// �������� ��ü�� ����. Conditional Mandatory 02�� ���� �޽����� ��ü.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� ��� ���ð��¹�ȣ(Field 58)����  �ݵ�� �ʿ��մϴ�.");
						}
					}
					else {
						if (ctr.getTransactionParty().getAccount() != null) {
							// Conditional Mandatory 06 : �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� �ƴ�  ��� ���ð��� �����Ͱ� ����� ��.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 11 �Ǵ� 12(���°ŷ�)�� �ƴ� ��� ���¿� ���õ� �ʵ�(Field 58,59,60,61,62,63,64,65,66,67,68,69,70)�� ���� ������ �� �����ϴ�.");
						}
					}
					
					// �������� �ŷ��� ���
					if (   "25".equals(ctr.getTransaction().getTypeCode().getStringValue())
						|| "26".equals(ctr.getTransaction().getTypeCode().getStringValue())){
						if (ctr.getSecuritiesDocument() != null){
							// Conditional Mandatory 07 : �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� �ݵ�� �������������� ����(Field 71)���� �ʿ���.
							if(ctr.getSecuritiesDocument().getTypeCode() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� �������������� ����(Field 71)����  �ݵ�� �ʿ��մϴ�.");
							}
							// �������� - �ڱ�ռ�ǥ �ŷ��� ���
							else if ("05".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())){
								// Conditional Mandatory 11 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������ޱ��������(Field 74)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitName() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �������ޱ��������(Field 74)����  �ݵ�� �ʿ��մϴ�.");
								}
								
								// Conditional Mandatory 12 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������ޱ�������ڵ�(Field 75)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �������ޱ�������ڵ�(Field 75)����  �ݵ�� �ʿ��մϴ�.");
								}		
								
								// Conditional Mandatory 13 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������޿�������(Field 76)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �������޿�������(Field 76)����  �ݵ�� �ʿ��մϴ�.");
								}
								
								// Conditional Mandatory 14 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������޿������� �����ȣ(Field 77)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �������޿������� �����ȣ(Field 77)����  �ݵ�� �ʿ��մϴ�.");
								}
							}
							else {
								/*
								// Conditional Mandatory 15 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ �����Ͱ� ����� ��.
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ ������ ���õ� �ʵ�(Field 74,75,76,77)�� ���� ������ �� �����ϴ�.");
								*/
								
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitName() != null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ ������ ���õ� �ʵ�(Field 74)�� ���� ������ �� �����ϴ�.");
								}
								
								// Conditional Mandatory 12 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������ޱ�������ڵ�(Field 75)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ ������ ���õ� �ʵ�(Field 75)�� ���� ������ �� �����ϴ�.");
								}		
								
								// Conditional Mandatory 13 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������޿�������(Field 76)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ ������ ���õ� �ʵ�(Field 76)�� ���� ������ �� �����ϴ�.");
								}
								
								// Conditional Mandatory 14 : �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� ��� �ݵ�� �������޿������� �����ȣ(Field 77)���� �ʿ���.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �������������� ����(Field 71)���� 05(�ڱ�ռ�ǥ �ŷ�)�� �ƴ� ��� �ڱ�ռ�ǥ ������ ���õ� �ʵ�(Field 77)�� ���� ������ �� �����ϴ�.");
								}
								
								
							}
							
							// Conditional Mandatory 08 : �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� �ݵ�� ������������ ���۹�ȣ(Field 72)���� �ʿ���.
							 if(!"11".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())&
								!"99".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue()))
									{
								      if(ctr.getSecuritiesDocument().getStartID() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� ������������ ���۹�ȣ(Field 72)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 09 : �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� �ݵ�� ������������ ����ȣ(Field 73)���� �ʿ���.
							// if(ctr.getSecuritiesDocument().getEndID() == null){
							//	throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� ������������ ����ȣ(Field 73)����  �ݵ�� �ʿ��մϴ�.");
							//}
						}
							 }
						else {
							// ������������ ��ü�� ����. Conditional Mandatory 07�� ���� �޽����� ��ü.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� ��� �������������� ����(Field 71)����  �ݵ�� �ʿ��մϴ�.");
						}
					}
					else {
						//if(!"11".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())||
						//		ctr.getSecuritiesDocument() != null){
						if(ctr.getSecuritiesDocument() != null){
						// Conditional Mandatory 10 : �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� �ƴ� ��� �������� �����Ͱ� ����� ��.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 25 �Ǵ� 26(�������� �ŷ�)�� �ƴ� ��� �������������� ���õ� �ʵ�(Field 71,72,73,74,75,76,77)�� ���� ������ �� �����ϴ�.");
						}
					}
					
					// �������Աݿ� ���� �۱ݰŷ��� ��
					if ("21".equals(ctr.getTransaction().getTypeCode().getStringValue())||
							"22".equals(ctr.getTransaction().getTypeCode().getStringValue())){
						if (ctr.getCounterPartParty() != null){
							// Conditional Mandatory 16 : �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ��� �ݵ�� ������ ���¹�ȣ(Field 78)���� �ʿ���.
							if (ctr.getCounterPartParty().getAccount() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ���  ������ ���¹�ȣ(Field 78)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 17 : �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ��� �ݵ�� ������������(Field 79)���� �ʿ���.
							if (ctr.getCounterPartParty().getReceiptOrganizationName() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ���  ������������(Field 79)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 18 : �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ��� �ݵ�� �����������ڵ�(Field 80)���� �ʿ���.
							if (ctr.getCounterPartParty().getReceiptOrganizationCode() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ���  �����������ڵ�(Field 80)����  �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 19 : �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ��� �ݵ�� ������(���������)����(Field 81)���� �ʿ���.
							if (ctr.getCounterPartParty().getName() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ���  ������(���������)����(Field 81)����  �ݵ�� �ʿ��մϴ�.");
							}
						}
						else {
							// �������Աݿ� ���� ������ ���� ��ü�� ����. Conditional Mandatory 16�� ���� �޽����� ��ü.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� ���  ������ ���¹�ȣ(Field 78)����  �ݵ�� �ʿ��մϴ�.");
						}
					}
					else {
						if (ctr.getCounterPartParty() != null){
							// Conditional Mandatory 20 : �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� �ƴ� ��� �������Աݿ� ���� ������ ���� �����Ͱ� ����� ��.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ����� �ڵ�(Field 48)���� 21 �Ǵ� 22(�������Աݿ� ���� �۱ݰŷ�)�� �ƴ� ��� ������������ ���õ� �ʵ�(Field 78,79,80,81)�� ���� ������ �� �����ϴ�.");
						}
					}
				}
				
				if (ctr.getTransaction().getTypeCode() == null){
					// Mandatory 20 : �ŷ��븮�� ���翩��(Field 49)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� �����ϴ�.");
				}
				else { // �ŷ��븮���� ������ ����� ������� üũ
					if ("1".equals(ctr.getTransaction().getAgentExistYN().getStringValue())){
						if (ctr.getTransaction().getAgent() != null){
							// Conditional Mandatory 33 : �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�θ�(Field 50)���� �ݵ�� �ʿ���.
							if (ctr.getTransaction().getAgent().getName() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�θ�(Field 50)���� �ݵ�� �ʿ��մϴ�.");
							}
	
							// Conditional Mandatory 34 : �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�� ���� �ڵ�(Field 54)���� �ݵ�� �ʿ���.
							if (ctr.getTransaction().getAgent().getNationalityCode() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�� ���� �ڵ�(Field 54)���� �ݵ�� �ʿ��մϴ�.");
							}
	
							//if(!"KR".equals(ctr.getTransactionParty().getNationalityCode().getStringValue())){
							// Conditional Mandatory 35 : �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�� ������(Field 55)���� �ݵ�� �ʿ���.
							if (ctr.getTransaction().getAgent().getNationality() == null ){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ���  �ŷ��븮�� ������(Field 55)���� �ݵ�� �ʿ��մϴ�.");
							}
							
							// Conditional Mandatory 36 : �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮���� �ŷ��ڿ��� �����ڵ�(Field 56)���� �ݵ�� �ʿ���.
							if (ctr.getTransaction().getAgent().getRelationshipCode() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��ڿ��� �����ڵ�(Field 56)���� �ݵ�� �ʿ��մϴ�.");
							}
							else {
								if ("99".equals(ctr.getTransaction().getAgent().getRelationshipCode().getStringValue())){
									// Conditional Mandatory 37 : �ŷ��븮�� �����ڵ�(Field 56)�� ���� 99(��Ÿ)�� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 57)���� �ݵ�� �ʿ���.
									if (ctr.getTransaction().getAgent().getRelationship() == null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� �����ڵ�(Field 56)�� ���� 99(��Ÿ)�� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 57)���� �ݵ�� �ʿ��մϴ�.");
									}
								}
								else {
									// Conditional Mandatory 38 : �ŷ��븮�� �����ڵ�(Field 56)�� ���� 99(��Ÿ)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 81)���� ������ �� ����.
									if (ctr.getTransaction().getAgent().getRelationship() != null){
										throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� �����ڵ�(Field 56)�� ���� 99(��Ÿ)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 57)���� ������ �� �����ϴ�.");
									}
								}
							}
						}
						else {
							// �ŷ��븮�� ���� ��ü�� ����. Conditional Mandatory 33 �������� ���� �޼����� ��ü��.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� ��� �ŷ��븮�θ�(Field 50)���� �ݵ�� �ʿ��մϴ�.");
						}
					}
					else { // Conditional Mandatory 39 : �ŷ��븮�� ���� ���ΰ� 1�� �ƴ� ���� �Էµ��� �ʾƾ� �ϴ� �ʵ� üũ
						if (ctr.getTransaction().getAgent() != null){
							if (ctr.getTransaction().getAgent().getName() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�θ�(Field 50)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getTypeCode() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�� �Ǹ��ȣ����(Field 51)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getID() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�� �Ǹ��ȣ(Field 52)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getNationalityCode() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�� ���� �ڵ�(Field 54)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getNationality() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�� ������(Field 55)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getContact() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮�� ��ȭ��ȣ(Field 53)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getRelationshipCode() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����ڵ�(Field 56)���� ������ �� �����ϴ�.");
							}
							if (ctr.getTransaction().getAgent().getRelationship() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��븮�� ���翩��(Field 49)�� ���� 1(����)�� �ƴ� ��� �ŷ��븮���� �ŷ��ڿ��� �����(Field 57)���� ������ �� �����ϴ�.");
							}
						}
					}
				}
			}
			else {
				// �ŷ����� ��ü�� ����. Mandatory 09 �������� ���� �޼����� ��ü��.
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڸ�(Field 10)�� ���� �����ϴ�.");
			}
	
			// �ŷ���(�����) ���� �κ��� �ʼ� �� ������ �ʼ� �׸� üũ
			if (ctr.getTransactionParty() != null){
				// Mandatory 11 : �ŷ���(�����)��(Field 12)
				if (ctr.getTransactionParty().getName() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ���(�����)��(Field 12)�� ���� �����ϴ�.");
				}
				
				// Mandatory 12 : �ŷ���(�����) �Ǹ��ȣ����(Field 13)
				if (ctr.getTransactionParty().getTypeCode() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ���(�����) �Ǹ��ȣ����(Field 13)�� ���� �����ϴ�.");
				}
				
				// Mandatory 13 : �ŷ���(�����) �Ǹ��ȣ(Field 14)
				if (ctr.getTransactionParty().getID() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ���(�����) �Ǹ��ȣ(Field 14)�� ���� �����ϴ�.");
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
						
					// Conditional Mandatory : �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 03 ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 10�ڸ��̾�� �� .
					if (ctr.getTransactionParty().getID().getStringValue().length() != 10){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� '03'�� ��� �ŷ��� �Ǹ��ȣ(Field 14) �ڸ����� 10�ڸ��̾�� �մϴ�.");
					}
						
				} else {
					//Conditional Mandatory : �ŷ��� �Ǹ��ȣ�� 15�ڸ� ����.
					if (ctr.getTransactionParty().getID().getStringValue().length() > 15){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���  �Ǹ��ȣ(Field 14) �ڸ����� 15�ڸ� �����Դϴ�.");
					}
				}
				
				//�ŷ��ڰ� ������ ���
				if (   "01".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "04".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "06".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "07".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "11".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "99".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
					
					// Conditional Mandatory 21 : �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,99�� ��� �ŷ��� ������(Field 16)�� �ݵ�� �Է��ؾ� ��.
					if (ctr.getTransactionParty().getNationality() == null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,11,99)�� ��� �ŷ��� ������(Field 16)���� �ݵ�� �ʿ��մϴ�.");
					}
					
					// Conditional Mandatory 22 : �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,99�� ��� �ŷ��� �����ڵ�(Field 15)�� �ݵ�� �Է��ؾ� ��.
					if (ctr.getTransactionParty().getNationalityCode() == null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����  01,04,06,07,11,99)�� ��� �ŷ��� �����ڵ�(Field 15)���� �ݵ�� �ʿ��մϴ�.");
					}
					
					if (!"KR".equals(ctr.getTransactionParty().getNationalityCode().getStringValue())){
						/*
						if (ctr.getTransactionParty().getRealAddress() != null){
							// Conditional Mandatory 23 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�� ��� �ŷ����� ���������� ���� �ż� �����ȣ(Field 52)���� �ʿ���.
							if (ctr.getTransactionParty().getRealAddress().getPostCodeID() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�� ��� �ŷ����� ���������� ���� �ż� �����ȣ(Field 52)����  �ݵ�� �ʿ��մϴ�.");
							}
							// Conditional Mandatory 24 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�� ��� �ŷ����� ���������� ���� �ż� �ּ�(Field 53)���� �ʿ���.
							if (ctr.getTransactionParty().getRealAddress().getLineOne() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�� ��� �ŷ����� ���������� ���� �ż� �ּ�(Field 53)����  �ݵ�� �ʿ��մϴ�.");
							}
						}
						else {
							// �ŷ����� ���������� ���� �ż�  ���� ��ü�� ����. Conditional Mandatory 21�� ���� �޽����� ��ü.
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�� ��� �ŷ����� ���������� ���� �ż� �����ȣ(Field 52)����  �ݵ�� �ʿ��մϴ�.");
						}
						*/
						if(ctr.getTransactionParty().getAddress() == null) {
							// Conditional Mandatory 25 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��� �ŷ��� �������(Field 19)���� �ʿ���.
							if (ctr.getTransactionParty().getBirthDate() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��� �ŷ��� �������(Field 19)����  �ݵ�� �ʿ��մϴ�.");
							}
							// Conditional Mandatory 26 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��� �ŷ��� ����(Field 20)���� �ʿ���.
							if (ctr.getTransactionParty().getGender() == null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��� �ŷ��� ����(Field 20)����  �ݵ�� �ʿ��մϴ�.");
							}
						}/*2009.01.06 ���İ����� ���ʿ���(�ּ�ó��)-jto
						else {
							// Conditional Mandatory 27 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��쿡�� �ŷ��� �������(Field 19)���� �Է��� �� ����.
							if (ctr.getTransactionParty().getBirthDate() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰�  �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� �Է��߱� ������ �ŷ��� �������(Field 19)����  ������ �� �����ϴ�.");
							}
							// Conditional Mandatory 28 : �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰� �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� ��쿡�� �ŷ��� ����(Field 20)���� �Է��� �� ����.
							if (ctr.getTransactionParty().getGender() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ��ڰ� �ܱ���(Field 15(�ŷ��� �����ڵ�)�� ���� KR�� �ƴ� ���)�̰�  �ŷ��� �����ȣ/�ּ�(Field 17,18)�� ���� �Է��߱� ������ �ŷ��� ����(Field 20)�� ���� ������ �� �����ϴ�.");
							}
						}*/
					}
				}
				else { // Conditional Mandatory 29 : �ŷ��ڰ� ������ �ƴ� ��쿡 �Էµ��� �ʾƾ� �ϴ� �ʵ� üũ
					if (ctr.getTransactionParty().getAddress() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����  01,04,06,07,11,99)�� �ƴ� ��쿡 �ŷ��� �ּ�(Field 17 �� Field 18)���� ������ �� �����ϴ�.");
					}
					if (ctr.getTransactionParty().getMixedRealNameID() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����  01,04,06,07,11,99)�� �ƴ� ��쿡 �ŷ��� �Ǹ����չ�ȣ(Field 22)���� ������ �� �����ϴ�.");
					}
					/*if (ctr.getTransactionParty().getNationality() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,99)�� �ƴ� ��쿡 �ŷ��� ������(Field 16)���� ������ �� �����ϴ�.");
					}
					if (ctr.getTransactionParty().getNationalityCode() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,99)�� �ƴ� ��쿡 �ŷ��� �����ڵ�(Field 15)���� ������ �� �����ϴ�.");
					}*/
					/*
					if (ctr.getTransactionParty().getRealAddress() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,99)�� �ƴ� ��쿡 �ŷ��� �ŷ����� ���������� ���� �ż� ����(Field 52,53)���� ������ �� �����ϴ�.");
					}*/
					if (ctr.getTransactionParty().getPassportID() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����  01,04,06,07,11,99)�� �ƴ� ��쿡 ���ǹ�ȣ(Field 23)���� ������ �� �����ϴ�.");
					}
					if (ctr.getTransactionParty().getBirthDate() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����  01,04,06,07,11,99)�� �ƴ� ��쿡 �ŷ��� �������(Field 19)���� ������ �� �����ϴ�.");
					}
					if (ctr.getTransactionParty().getGender() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,11,99)�� �ƴ� ��쿡 ����(Field 20)���� ������ �� �����ϴ�.");
					}
					if (ctr.getTransactionParty().getJob() != null){
						throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 01,04,06,07,11,99)�� �ƴ� ��쿡 �ŷ��� ��������(Field 24,25,26,27)���� ������ �� �����ϴ�.");
					}
				}
				
				// �ŷ��ڰ� ���� �Ǵ� ��ü�� ���(090317��Ÿ�ڵ��߰�jto)
				if (   "02".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "03".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "08".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "09".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "05".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "11".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "12".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "99".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
					
					if (ctr.getEnterpriseOrganization() != null) {
						if (ctr.getEnterpriseOrganization().getRepresentativePerson() != null){
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getName() != null){
								// Conditional Mandatory 30 : �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�̰� ��ǥ�ڸ�(Field 28)�� �Է��Ͽ��� ���� ��ǥ�� ������(Field 32)���� �ݵ�� �ʿ���.
								if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationality() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�̰� ��ǥ�ڸ�(Field 28)�� �Է��Ͽ��� ���� ��ǥ�� ������(Field 32)���� �ݵ�� �ʿ��մϴ�.");
								}
								// Conditional Mandatory 31 : �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�̰� ��ǥ�ڸ�(Field 28)�� �Է��Ͽ��� ���� ��ǥ�� �����ڵ�(Field 31)���� �ݵ�� �ʿ���.
								if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationalityCode() == null){
									throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�̰� ��ǥ�ڸ�(Field 28)�� �Է��Ͽ��� ���� ��ǥ�� �����ڵ�(Field 31)���� �ݵ�� �ʿ��մϴ�.");
								}
							}
						}
					}
				}
				else {// Conditional Mandatory 32 : �ŷ��ڰ� ���� �Ǵ� ��ü�� �ƴ� ��쿡 �Էµ��� �ʾƾ� �ϴ� �ʵ� üũ
					if (ctr.getEnterpriseOrganization() != null){
						if (ctr.getEnterpriseOrganization().getRepresentativePerson()!= null){
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getName() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ��ǥ�ڸ�(Field 28)���� ������ �� �����ϴ�.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getTypeCode() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ��ǥ�� �Ǹ��ȣ����(Field 29)���� ������ �� �����ϴ�.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getID() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ��ǥ�� �Ǹ��ȣ(Field 30)���� ������ �� �����ϴ�.");
							}
							/*
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getAddress() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�� �ƴ� ��쿡 ��ǥ�� ���� �ּ�����(Field 64,65)���� ������ �� �����ϴ�.");
							}
							*/
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationality() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ��ǥ�� ������(Field 32)���� ������ �� �����ϴ�.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationalityCode() != null){
								throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ��ǥ�� �����ڵ�(Field 31)���� ������ �� �����ϴ�.");
							}
						}
						if (ctr.getEnterpriseOrganization().getEstablishmentDate() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ü(��ü) ������(Field 33)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getKSICCode() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 �����ڵ�(Field 34)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getAddress() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ü(��ü)���� �ּ� ����(Field 36,38)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getBusinessSiteAddress()!=null){
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ü(��ü)����� �ּ� ����(Field 37,39)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getContact() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ü(��ü)���� ��ȭ��ȣ(Field 40)���� ������ �� �����ϴ�.");
						}					
						if (ctr.getEnterpriseOrganization().getBusinessSiteContact() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)����02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ü(��ü)����� ��ȭ��ȣ(Field 41)���� ������ �� �����ϴ�.");
						}					
						if (ctr.getEnterpriseOrganization().getCorporationID() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,05,08,09,11,12,99)�� �ƴ� ��쿡 ���ε�Ϲ�ȣ(Field 35)���� ������ �� �����ϴ�.");
						}
						/*
						if (ctr.getEnterpriseOrganization().getScaleType() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�� �ƴ� ��쿡 ����Ը� �ڵ�(Field 69)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getFinancialInstitutionYN() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�� �ƴ� ��쿡 ������� ����(Field 70)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getNonprofitYN() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�� �ƴ� ��쿡 �񿵸���ü ����(Field 71)���� ������ �� �����ϴ�.");
						}
						if (ctr.getEnterpriseOrganization().getListedStocksYN() != null) {
							throw new FlatFileConvertingException("##[������ �ʼ� �׸� ����] �ŷ���(�����)�� ����(Field 13(�ŷ���(�����)�Ǹ��ȣ����)���� 02,03,08,09)�� �ƴ� ��쿡 ���� ����(Field 72)���� ������ �� �����ϴ�.");
						}
						*/
					}
				}
			}
			else {
				// �ŷ����� ��ü�� ����. Mandatory 11 �������� ���� �޼����� ��ü��.
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �ŷ���(�����)��(Field 12)�� ���� �����ϴ�.");
			}
		}
		// ��Һ����� ��
		else if (  ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL)){
			
			if ( ctr.getReportAdministrationParty() != null ){
				// Mandatory 06 : ��������(Field 07)
				if (ctr.getReportAdministrationParty().getName() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ��������(Field 07)�� ���� �����ϴ�.");
				}
				
				// Mandatory 07 : �������ڵ�(Field 08)
				if (ctr.getReportAdministrationParty().getCode() == null){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڵ�(Field 08)�� ���� �����ϴ�.");
				}
				
				// Mandatory 10 : ����������ȭ��ȣ(Field 11)
				if (ctr.getReportAdministrationParty().getContact() != null){
					if (ctr.getReportAdministrationParty().getContact().getTelephoneID() == null){
						throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����������ȭ��ȣ(Field 11)�� ���� �����ϴ�.");
					}
				}
				else {
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����������ȭ��ȣ(Field 11)�� ���� �����ϴ�.");
				}
			}
			else {
				// ���������� ��ü�� ����. Mandatory 06 �������� ���� �޼����� ��ü��.
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ��������(Field 07)�� ���� �����ϴ�.");
			}
			
			// Mandatory 08 : ����å���ڸ�(Field 09)
			if (ctr.getPersonName() == null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] ����å���ڸ�(Field 09)�� ���� �����ϴ�.");
			}
			
			if (ctr.getTransaction() != null){
				if (ctr.getTransaction().getPersonName() == null){
					// Mandatory 09 : �������ڸ�(Field 10)
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڸ�(Field 10)�� ���� �����ϴ�.");
				}
				
				// ��Ÿ ���� �ŷ����� ������Ʈ���� �Էµ��� ���ƾ� �ϴ� �ʵ� üũ
				if (ctr.getTransaction().getAgent() != null
				 || ctr.getTransaction().getAgentExistYN() != null
				 || ctr.getTransaction().getAmount() != null
				 || ctr.getTransaction().getBranchAddress() != null
				 || ctr.getTransaction().getBranchFinancialUnitName() != null
				 || ctr.getTransaction().getChannelCode() != null
				 || ctr.getTransaction().getDateTime() != null
				 || ctr.getTransaction().getMeansCode() != null
				 || ctr.getTransaction().getTypeCode() != null ){
					throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01) ���� 03�̸� ��Һ����̹Ƿ� �ŷ����� ������ �Է��� �� �����ϴ�.");
				}
			}
			else {
				// �ŷ����� ��ü�� ����. Mandatory 09 �������� ���� �޼����� ��ü��.
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �������ڸ�(Field 10)�� ���� �����ϴ�.");
			}
			
			// ��Ÿ ���� �Էµ��� ���ƾ� �ϴ� �ʵ� üũ
			if(ctr.getCounterPartParty() != null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01) ���� 03�̸� ��Һ����̹Ƿ� ������ ������ �Է��� �� �����ϴ�.");
			}
			if (ctr.getEnterpriseOrganization() != null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01) ���� 03�̸� ��Һ����̹Ƿ� ��� �Ǵ� ��ü ������ �Է��� �� �����ϴ�.");
			}
			if (ctr.getTransactionParty() != null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01) ���� 03�̸� ��Һ����̹Ƿ� �ŷ��� ������ �Է��� �� �����ϴ�.");
			}
			if (ctr.getSecuritiesDocument() != null){
				throw new FlatFileConvertingException("##[�ʼ� �׸� ����] �޽���Ÿ��(Field 01) ���� 03�̸� ��Һ����̹Ƿ� �������� �� �ڱ�ռ�ǥ ������ �Է��� �� �����ϴ�.");
			}
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
		
		  CurrencyTransactionReportDocument poDoc =	CurrencyTransactionReportDocument.Factory.newInstance();
		  CurrencyTransactionReportType newCTR = poDoc.addNewCurrencyTransactionReport();
		 
		  int XMLIndex = 0; // 0 index is CTRSTART
		  String tempVal = null;
		  
		  //xmlSeq=0
		  //CurrencyTransactionReport/DocumentID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewDocumentID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=1
		  //CurrencyTransactionReport/MessageCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewMessageCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=2
		  //CurrencyTransactionReport/Date
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.setDate(tempVal);
		  }

		  //xmlSeq=3		  
		  //CurrencyTransactionReport/FormerDocumentID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewFormerDocumentID().setStringValue(tempVal);
		  }

		  //xmlSeq=4
		  //CurrencyTransactionReport/Order
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewOrder().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=5
		  //CurrencyTransactionReport/MainDocumentID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewMainDocumentID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=6
		  //CurrencyTransactionReport/PersonName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewPersonName().setStringValue(tempVal);
		  }
		  //CurrencyTransactionReport/ReportAdministrationParty
		  ReportAdministrationPartyType newRAP = newCTR.addNewReportAdministrationParty();
		  
		  //xmlSeq=7
		  // CurrencyTransactionReport/ReportAdministrationParty/Code
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=8
		  // CurrencyTransactionReport/ReportAdministrationParty/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=9
		  // CurrencyTransactionReport/ReportAdministrationParty/Contact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newRAP.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  //CurrencyTransactionReport/TransactionParty
		  TransactionPartyType newTP = newCTR.addNewTransactionParty();
		  boolean isTrnPartyNull = true;

		  //xmlSeq=10
		  // CurrencyTransactionReport/TransactionParty/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=11
		  // CurrencyTransactionReport/TransactionParty/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=12
		  // CurrencyTransactionReport/TransactionParty/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewName().setStringValue(tempVal);
		  }
//##################################12.24
		  /*//xmlSeq=13
		  // CurrencyTransactionReport/TransactionParty/Address/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewAddress().addNewLineOne().setStringValue(tempVal);
		  }
		  //xmlSeq=14
		  // CurrencyTransactionReport/TransactionParty/Address/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewAddress().addNewPostCodeID().setStringValue(tempVal);
		  }*/
   		  boolean isTranAddressNull = true;
		  AddressType newTRNSADDR = newTP.addNewAddress();
		  //xmlSeq=13
		  // CurrencyTransactionReport/TransactionParty/Address/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAddressNull = false;
			  newTRNSADDR.addNewLineOne().setStringValue(tempVal);
		  }
		  //xmlSeq=14
		  // CurrencyTransactionReport/TransactionParty/Address/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAddressNull = false;
			  newTRNSADDR.addNewPostCodeID().setStringValue(tempVal);
		  }
		  if ( isTranAddressNull ) newTP.unsetAddress();
		  else 	isTranAddressNull = false;
		  
//#################################################		 
		  //xmlSeq=15
		  // CurrencyTransactionReport/TransactionParty/Contact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=16
		  // CurrencyTransactionReport/TransactionParty/MixedRealNameID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewMixedRealNameID().setStringValue(tempVal);
		  }

		  //xmlSeq=17
		  // CurrencyTransactionReport/TransactionParty/PassportID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewPassportID().setStringValue(tempVal);
		  }
		  //xmlSeq=18
		  // CurrencyTransactionReport/TransactionParty/NationalityCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewNationalityCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=19
		  // CurrencyTransactionReport/TransactionParty/Nationality
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewNationality().setStringValue(tempVal);
		  }	  
		  
		
		  //xmlSeq=20
		  // CurrencyTransactionReport/TransactionParty/BirthDate
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.setBirthDate(tempVal);
		  }
		  
		  //xmlSeq=21
		  // CurrencyTransactionReport/TransactionParty/Gender
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTrnPartyNull = false;
			  newTP.addNewGender().setStringValue(tempVal);
		  }

		  // CurrencyTransactionReport/TransactionParty/Job
		  JobType newJob = newTP.addNewJob();
		  
		  boolean isJobNull = true;
		  
		  //xmlSeq=22
		  // CurrencyTransactionReport/TransactionParty/Job/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isJobNull = false;
			  newJob.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=23
		  // CurrencyTransactionReport/TransactionParty/Job/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isJobNull = false;
			  newJob.addNewName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=24
		  // CurrencyTransactionReport/TransactionParty/Job/BusinessTypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isJobNull = false;
			  newJob.addNewBusinessTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=25
		  // CurrencyTransactionReport/TransactionParty/Job/BusinessID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isJobNull = false;
			  newJob.addNewBusinessID().setStringValue(tempVal);
		  }
		  
		  //JobType�� �� �ƹ��� ���� ������ unset
		  if (isJobNull){
			  newTP.unsetJob();
		  }

		  // ������ �ʼ�/optional �׸� 
		  // TransactionParty/Account/ID
		  AccountType newAcc = newTP.addNewAccount();
		  
		  boolean isAccountNull = true;
		  
		  //xmlSeq=26
		  // CurrencyTransactionReport/TransactionParty/Account/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAccountNull = false;
			  newAcc.addNewID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=27
		  // CurrencyTransactionReport/TransactionParty/Account/OpenDate
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAccountNull = false;
			  newAcc.setOpenDate(tempVal);
		  }
		  
		  //xmlSeq=28
		  // CurrencyTransactionReport/TransactionParty/Account/BranchFinancialUnitName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAccountNull = false;
			  newAcc.addNewBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  //xmlSeq=29
		  // CurrencyTransactionReport/TransactionParty/Account/BranchAddress/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAccountNull = false;
			  newAcc.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=30
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgentYN
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAccountNull = false;
			  newAcc.addNewOpenAgentYN().setStringValue(tempVal);
		  }

		  // TransactionParty/Account/OpenAgentType
		  OpenAgentType newOAT = newAcc.addNewOpenAgent();
		  
		  boolean isAgentNull = true;
		  
		  //xmlSeq=31
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewName().setStringValue(tempVal);
		  }

		  //xmlSeq=32
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=33
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=34
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/Contact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=35
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/NationalityCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewNationalityCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=36
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/Nationality
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewNationality().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=37
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/RelationshipCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewRelationshipCode().setStringValue(tempVal);
		  }
		  //xmlSeq=38
		  // CurrencyTransactionReport/TransactionParty/Account/OpenAgent/Relationship
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isAgentNull = false;
			  newOAT.addNewRelationship().setStringValue(tempVal);
		  }
		  
		  // OpenAgent�� Null���� üũ
		  if (isAgentNull){
			  newAcc.unsetOpenAgent();
		  }
		  
		  // Account�� Null���� üũ
		  if ( isAccountNull ) newTP.unsetAccount();
		  else 	isTrnPartyNull = false;
		  
		  		  
		  // TransactionParty�� Null���� üũ		  
		  if ( isTrnPartyNull ) newCTR.unsetTransactionParty();
		  
		  // CurrencyTransactionReport/EnterpriseOrganization 
		  EnterpriseOrganizationType newEnterOrg = newCTR.addNewEnterpriseOrganization();
		  
		  boolean isEntOrgNull = true;
		  
		  //xmlSeq=39
		  // CurrencyTransactionReport/EnterpriseOrganization/EstablishmentDate
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.setEstablishmentDate(tempVal);
		  }
		  
		  //xmlSeq=40
		  // CurrencyTransactionReport/EnterpriseOrganization/KSICCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewKSICCode().setStringValue(tempVal);
		  }
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson
		  RepresentativePersonType newRP = newEnterOrg.addNewRepresentativePerson();
		  
		  boolean isRPTNull = true;
		  
		  //xmlSeq=41
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isRPTNull = false;
			  newRP.addNewTypeCode().setStringValue(tempVal);
		  }
		  //xmlSeq=42
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isRPTNull = false;
			  newRP.addNewID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=43
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isRPTNull = false;
			  newRP.addNewName().setStringValue(tempVal);
		  }
		 
		  
		  //xmlSeq=44
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson/NationalityCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isRPTNull = false;
			  newRP.addNewNationalityCode().setStringValue(tempVal);
		  }
		  //xmlSeq=45
		  // CurrencyTransactionReport/EnterpriseOrganization/RepresentativePerson/Nationality
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isRPTNull = false;
			  newRP.addNewNationality().setStringValue(tempVal);
		  }

		  
		  
		  if ( isRPTNull ) newEnterOrg.unsetRepresentativePerson();
		  else 	isEntOrgNull = false;
/*
		  //xmlSeq=46
		  // CurrencyTransactionReport/EnterpriseOrganization/Address/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewAddress().addNewLineOne().setStringValue(tempVal);
		  }
		 
		  
		  //xmlSeq=47
		  // CurrencyTransactionReport/EnterpriseOrganization/Address/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewAddress().addNewPostCodeID().setStringValue(tempVal);
		  }*/
		  
		  //1224�� ����
   		  boolean isEnterAddressNull = true;
		  
		  AddressType newADDR = newEnterOrg.addNewAddress();
		  
		  //xmlSeq=46
		  // CurrencyTransactionReport/EnterpriseOrganization/Address/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEnterAddressNull = false;
			  newADDR.addNewLineOne().setStringValue(tempVal);
		  }
		 
		  
		  //xmlSeq=47
		  // CurrencyTransactionReport/EnterpriseOrganization/Address/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEnterAddressNull = false;
			  newADDR.addNewPostCodeID().setStringValue(tempVal);
		  }
		  if ( isEnterAddressNull ) newEnterOrg.unsetAddress();
		  else 	isEnterAddressNull = false;
		  
		  
		  
		  
//########################
		 /* //xmlSeq=48
		  // CurrencyTransactionReport/EnterpriseOrganization/BusinessSiteAddress/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewBusinessSiteAddress().addNewLineOne().setStringValue(tempVal);
		  }
		 
		  
		  //xmlSeq=49
		  // CurrencyTransactionReport/EnterpriseOrganization/BusinessSiteAddress/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewBusinessSiteAddress().addNewPostCodeID().setStringValue(tempVal);
		  }*/
		  
         boolean isEnterBusinessAddressNull = true;
		  
         AddressType newBZADDR = newEnterOrg.addNewBusinessSiteAddress();

		  //xmlSeq=48
		  // CurrencyTransactionReport/EnterpriseOrganization/BusinessSiteAddress/LineOne
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEnterBusinessAddressNull = false;
			  newBZADDR.addNewLineOne().setStringValue(tempVal);
		  }
		 
		  
		  //xmlSeq=49
		  // CurrencyTransactionReport/EnterpriseOrganization/BusinessSiteAddress/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEnterBusinessAddressNull = false;
			  newBZADDR.addNewPostCodeID().setStringValue(tempVal);
		  } 
		  if ( isEnterBusinessAddressNull ) newEnterOrg.unsetBusinessSiteAddress();
		  else 	isEnterBusinessAddressNull = false;
		  
		  
//##########################		  
		  //xmlSeq=50
		  // CurrencyTransactionReport/EnterpriseOrganization/Contact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
//		##########################
		  //xmlSeq=51
		  // CurrencyTransactionReport/EnterpriseOrganization/BusinessSiteContact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewBusinessSiteContact().addNewTelephoneID().setStringValue(tempVal);
		  } 
//		##########################		  
		  //xmlSeq=52
		  // CurrencyTransactionReport/EnterpriseOrganization/CorporationID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isEntOrgNull = false;
			  newEnterOrg.addNewCorporationID().setStringValue(tempVal);
		  }
		 
		  // EnterpriseOrganization Null üũ
		  if ( isEntOrgNull )  newCTR.unsetEnterpriseOrganization();

		  // CurrencyTransactionReport/Transaction
		  TransactionType newTran = newCTR.addNewTransaction();
		  
		  //xmlSeq=53
		  // CurrencyTransactionReport/Transaction/DateTime
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.setDateTime(tempVal);
		  }
		  
		  //xmlSeq=54
		  // CurrencyTransactionReport/Transaction/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=55
		  // CurrencyTransactionReport/Transaction/MeansCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewMeansCode().setStringValue(tempVal);
		  }
		  //xmlSeq=56
		  // CurrencyTransactionReport/Transaction/ChannelCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewChannelCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=57
		  // CurrencyTransactionReport/Transaction/Amount
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewAmount().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=58
		  // CurrencyTransactionReport/Transaction/BranchFinancialUnitName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=59
		  // CurrencyTransactionReport/Transaction/PersonName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewPersonName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=68
		  // CurrencyTransactionReport/Transaction/BranchAddress/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=69
		  // CurrencyTransactionReport/Transaction/AgentExistYN
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newTran.addNewAgentExistYN().setStringValue(tempVal);
		  }
		  
		  // CurrencyTransactionReport/Transaction/Agent
		  AgentType newTranAgent = newTran.addNewAgent();
		  
		  boolean isTranAgentNull = true;
		  
		  //xmlSeq=70
		  // CurrencyTransactionReport/Transaction/Agent/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewName().setStringValue(tempVal);
		  }
		  //xmlSeq=71
		  // CurrencyTransactionReport/Transaction/Agent/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=72
		  // CurrencyTransactionReport/Transaction/Agent/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=73
		  // CurrencyTransactionReport/Transaction/Agent/NationalityCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewNationalityCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=74
		  // CurrencyTransactionReport/Transaction/Agent/Nationality
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewNationality().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=75
		  // CurrencyTransactionReport/TransactionParty/Agent/Contact/TelephoneID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewContact().addNewTelephoneID().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=76
		  // CurrencyTransactionReport/TransactionParty/Agent/RelationshipCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewRelationshipCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=77
		  // CurrencyTransactionReport/TransactionParty/Agent/Relationship
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isTranAgentNull = false;
			  newTranAgent.addNewRelationship().setStringValue(tempVal);
		  }
		  
		  // Agent Null üũ
		  if (isTranAgentNull){
			  newTran.unsetAgent();
		  }

		  // CurrencyTransactionReport/SecuritiesDocument
		  SecuritiesDocumentType newSD = newCTR.addNewSecuritiesDocument();
		  
		  boolean isSDNull = true;
		  
		  //xmlSeq=78
		  // CurrencyTransactionReport/SecuritiesDocument/TypeCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewTypeCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=79
		  // CurrencyTransactionReport/SecuritiesDocument/StartID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewStartID().setStringValue(tempVal);
		  }
		  //xmlSeq=80
		  // CurrencyTransactionReport/SecuritiesDocument/EndID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewEndID().setStringValue(tempVal);
		  }
		  //xmlSeq=81
		  // CurrencyTransactionReport/SecuritiesDocument/PaymentMainFinancialUnitName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewPaymentMainFinancialUnitName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=82
		  // CurrencyTransactionReport/SecuritiesDocument/PaymentMainFinancialUnitCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewPaymentMainFinancialUnitCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=83
		  // CurrencyTransactionReport/SecuritiesDocument/PaymentBranchFinancialUnitName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewPaymentBranchFinancialUnitName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=84
		  // CurrencyTransactionReport/SecuritiesDocument/BranchAddress/PostCodeID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  isSDNull = false;
			  newSD.addNewBranchAddress().addNewPostCodeID().setStringValue(tempVal);
		  }
		  
		  // SecuritiesDocument Null üũ
		  if ( isSDNull ) newCTR.unsetSecuritiesDocument();
		  
		  // CurrencyTransactionReport/CounterPartParty
		  CounterPartPartyType newCPP = null;
		  
		  //xmlSeq=85
		  // CurrencyTransactionReport/CounterPartParty/Name
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();
			  
			  newCPP.addNewName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=86
		  // CurrencyTransactionReport/CounterPartParty/ReceiptOrganizationName
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewReceiptOrganizationName().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=87
		  // CurrencyTransactionReport/CounterPartParty/ReceiptOrganizationCode
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewReceiptOrganizationCode().setStringValue(tempVal);
		  }
		  
		  //xmlSeq=88
		  // CurrencyTransactionReport/CounterPartParty/Account/ID
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  if ( newCPP == null )
				  newCPP = newCTR.addNewCounterPartParty();

			  newCPP.addNewAccount().addNewID().setStringValue(tempVal);
		  }
		  //xmlSeq=89
		  // CurrencyTransactionReport/CancellationReason
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewCancellationReason().setStringValue(tempVal);
		  }
		  
		//FileTool.writeToFile("./../Messages/temp/" + filename, poDoc.toString());
		//System.out.println("�ű� ������ flat������ ó���Ͽ� xml�� ������ > ./../Messages/temp/" + filename);
		  
		  return poDoc;
	}
	
	/**
	 * �ش� Index(Ű ��)�� property ���� �о�´�. 
	 * @param tokens
	 * @param index
	 * @return
	 */
	public String getValue(String[] tokens, int index){
		Message message = FlatFileChecker.getMessageById("101");
		Field field = message.getFieldByXmlSeq(Integer.toString(index));
		String val = tokens[field.getId()];
		if ( val != null && val.length() > 0 )
			return val;
		else
			return null;
	}

	public static void main(String[] agrs) throws IOException, FlatFileConvertingException{
		  
		  
		String flatfile = "CTRSTART||99||2009-00059448||20090617||||1/1||2009-00059448||�뱸����||1331||�Ŵ���||������||T6982/2152||����Ʈ��ũ���֣�||03||0001078643003||||||||||||||||||||||||||||�ڳ���||01||5611041079910||KR||�ѱ�||||||1101112910415||150010||700442||���� �������� ���ǵ��� �������Ǻ���������||�뱸 �߱� ����2�� ���������������������£��ģ�������ȣ||0803302114||0534210810||20090615093228||������||706712||01||01||700000000||11||2||||||||||||||||||002040031883||������||706712||20031211||9||||||||||||||||||||||||||||||||||||||||||CTREND";
		//String flatfile = "CTRSTART||01||2008-11111111||20081018||||0000001/0000001||2008-11111111||���������м���||9400||�����||�����||02-3479-2193||ȫ�浿||04||111111111111111||660250||�泲 ���ֽ� ������   111111||20081001||3||1111-1111-1111-1111||111111111111111||�ѱ�||KR||111111111111111||01||��õ||17124||1111111111||||||||||||||||||||||||||||||20081001121212||����||999999||01||01||3000000000||21||2||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||CTREND";
		String[] token = flatfile.split("\\|\\|");
		FlatFileToXML089 flatFileToXML089 = new FlatFileToXML089(flatfile,"test.SND");
		CurrencyTransactionReportDocument ctr = flatFileToXML089.convertToXML(token);
		System.out.println(ctr.toString());
		flatFileToXML089.checkConditionalMandatory(ctr);
		
	}
}
