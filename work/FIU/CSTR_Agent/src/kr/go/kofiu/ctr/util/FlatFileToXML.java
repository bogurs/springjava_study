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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : FlatFile을 읽어서 XML을 표현하는 자바 객체로 변환 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileToXML {

	/**
	 * FlatFile 데이타
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
	 * FlatFile데이타를 이용하여 XML을 생성할 수 있는 CurrencyTransactionReportDocument 객체를 
	 * 생성한다.
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
	 * 조건적 필수 값을 체크한다. 
	 * @param document
	 * @throws FlatFileConvertingException
	 */
	public void checkConditionalMandatory(CurrencyTransactionReportDocument document) throws FlatFileConvertingException {
		CurrencyTransactionReportType ctr = document.getCurrencyTransactionReport();
		if ( ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND) 
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			// 취소/정정 보고
			if ( ctr.getFormerDocumentID() == null || ctr.getFormerDocumentID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("메세지타입(01) 값이  " + ctr.getMessageCode().getStringValue() 
						+ "입니다. 따라서, 취소/정정보고시 이전 문서 번호 정보가 필요합니다.");
			}
		}
		
		// 취소 보고 시  조건 체크 없음. 2006/01/11자 추가 
		if ( ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			return ;
		}

		// 12/29일 반영. 11 : 거래자 실명 번호 , 10 : 거래자 실명 번호 구분 값이 없을 경우 
		// 실명조합번호(14)값과 거래자국적코드(15-1)값이 있어야 된다. 단 KR일 경우 오류 
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
				throw new FlatFileConvertingException("거래자국적코드(15-1)가 존재하지 않습니다. 따라서, 거래자(사업자) 실명번호구분(10) 값과 거래자(사업자)실명번호(11)값이 필요합니다.");
			} else {
				String nationalityCode = tpt.getNationalityCode().getStringValue();
				if ( "KR".equalsIgnoreCase(nationalityCode)){
					// error 실명 조합 번호가 존재하면 안됨
					throw new FlatFileConvertingException("거래자(사업자)실명번호구분(10), 거래자(사업자)실명 번호(11) 값이 " 
							+" 존재하지 않습니다. 이 경우 거래자국적코드(15-1)값과 실명조합 번호(재외동포/외국인)(14) 값이 " 
							+ " 필요한데 입력하신 거래자국적코드(15-1)값이 '" + nationalityCode 
							+ "' 입니다. 따라서 실명조합 번호(재외동포/외국인)(14)가 존재하면 안됩니다. 데이타의 정합성을 체크하세요.");
				} else {
					// 국적 코드 가 KR이 아니다. 실면조합 번호 체크 
					if ( tpt.getMixedRealNameID() == null 
							|| tpt.getMixedRealNameID().getStringValue() == null
							|| "".equals(tpt.getMixedRealNameID().getStringValue()) ) {
						throw new FlatFileConvertingException("실명조합 번호(재외동포/외국인)(14)가 존재하지 않습니다. 따라서, 거래자(사업자) 실명번호구분(10) 값과 거래자(사업자)실명번호(11)값이 필요합니다.");
					}
				}
			}
		}

	
		if ( tpt.getTypeCode() != null && !"".equals(tpt.getTypeCode().getStringValue())){
			String TPTypeCode = tpt.getTypeCode().getStringValue(); // 거래자 실명 번호 구분 
			
			if (  !"01".equals(TPTypeCode)      // 주민 등록 번호(개인)
					&& !"04".equals(TPTypeCode)  // 여권 번호 
					&& !"06".equals(TPTypeCode)  // 외국인 등록 번호 
					&& !"07".equals(TPTypeCode) // 국내  거소 신고번호 
					&& !"99".equals(TPTypeCode)) // 기타
			{
				if (  tpt.getAddress() != null 
						&& tpt.getAddress().getLineOne() != null) {
					String address = tpt.getAddress().getLineOne().getStringValue();
					if ( address != null && address.length() > 0 ) {
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '01','04','06','07','99' 이 아니고 '" + TPTypeCode + "'입니다. 따라서, 거래자 주소(12-1) 값이  '" + address + "' 존재하면 안됩니다.");
					}
				}
				
				if ( tpt.getAddress() != null && tpt.getAddress().getPostCodeID() != null ){
					String postCodeID = tpt.getAddress().getPostCodeID().getStringValue();
					if ( postCodeID != null && postCodeID.length() > 0 ) 
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '01','04','06','07','99' 이 아니고 '" + TPTypeCode + "'입니다. 따라서, 거래자 우편번호(12) 값이 '" + postCodeID + "' 존재하면 안됩니다.");
				}
	
				if ( tpt.getContact() != null &&  tpt.getContact().getTelephoneID() != null ) {
					String telephoneID = tpt.getContact().getTelephoneID().getStringValue();
					if ( telephoneID != null && telephoneID.length() > 0 ) 
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '01','04','06','07','99' 이 아니고 '" + TPTypeCode + "'입니다. 따라서, 거래자 전화번호(13) 값이 '" + telephoneID + "' 존재하면 안됩니다.");
				}
				
				// 10월 14d일 서식 변경 반영 
				
				if ( tpt.getNationalityCode() != null ) { 
					String nationalityCode = tpt.getNationalityCode().getStringValue();
					if ( nationalityCode != null && nationalityCode.length() > 0 ) 
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '01','04','06','07','99' 이 아니고 '" + TPTypeCode + "'입니다. 따라서, 거래자국적코드(15-1) 값이 '" + nationalityCode + "'존재하면 안됩니다.");
				}
				
				if ( tpt.getNationality() != null ) {
					String nationality = tpt.getNationality().getStringValue();
					if (nationality != null && nationality.length() > 0 ) 
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '01','04','06','07','99' 이 아니고 '" + TPTypeCode + "'입니다. 따라서, 거래자국적명이(15) '" + nationality + "' 존재하면 안됩니다.");
				}
			}
			
			// 법인/단체 거래자 정보는 사업자 등록 번호 , 투자 등록 번호, 주민번호 단체일경우 만 기재. 이 경우가 아닐 경우 기재되면 오류  
			if ( !"02".equals(TPTypeCode)      // 주민 등록 번호 (단체 )
				  && !"03".equals(TPTypeCode)  // 사업자 등록 번호 
				  && !"08".equals(TPTypeCode) // 투자 등록 번호 
				  && !"10".equals(TPTypeCode) // 고유번호
				  && !"09".equals(TPTypeCode) // 납세번호 
				  && !"99".equals(TPTypeCode)) // 기타
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
						throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '02','03','08','10','09','99' 이 아니고  '" + TPTypeCode + "'입니다. 따라서, 사업체(단체)설립일(19) 값이 '" + establishDate + "' 존재하면 안됩니다.");
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
			
			//20140311 강동원 실명번호 체크 로직 수정
			//validation check 추가 : 거래자(사업자) 실명번호구분(Field 13) 값에 따른 실명번호 검증
			/* 주민등록번호(01), 주민등록번호(기타단체)(02),법인등록번호(05), 외국인등록번호(06), 국내거소신고번호(07) : 13자리
			 * 사업자 등록번호(03) : 10자리
			 * 이외 : 15자리 이내
			 * */
			if (   "01".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "02".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "05".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "06".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
				|| "07".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
				
				// Conditional Mandatory : 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,02,05,06,07일 경우 거래자 실명번호(Field 14) 자리수는 13자리이어야 함 .
				if (ctr.getTransactionParty().getID().getStringValue().length() != 13){
					throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 '01','02','05','06','07'일 경우 거래자 실명번호(Field 14) 자리수는 13자리이어야 합니다.");
				}
				
			} else if ( "03".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
					
				// Conditional Mandatory : 거래자가 사업자 (Field 13(거래자(사업자)실명번호구분)값이 03일 경우 거래자 실명번호(Field 14) 자리수는 10자리이어야 함 .
				if (ctr.getTransactionParty().getID().getStringValue().length() != 10){
					throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 사업자(Field 13(거래자(사업자)실명번호구분)값이 '03'일 경우 거래자 실명번호(Field 14) 자리수는 10자리이어야 합니다.");
				}
					
			} else {
				//Conditional Mandatory : 거래자 실명번호는 15자리 이하.
				if (ctr.getTransactionParty().getID().getStringValue().length() > 15){
					throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자  실명번호(Field 14) 자리수는 15자리 이하입니다.");
				}
			}
			
		} // end of if TypeCode : 거래자 실명번호 구분 


		TransactionType tranType = ctr.getTransaction();
		String TTypeCode = tranType.getTypeCode().getStringValue(); // 거래 유형(=거래 종류) 
		if ( "11".equalsIgnoreCase(TTypeCode)       // 계좌 + 지급 
			  || "12".equalsIgnoreCase(TTypeCode) ) // 계좌 + 영수
		{
			// 계좌 개설점 , 계좌 개설점 우편번호 , 계좌 개설일, 관련계좌 번호 모두 필수 
			AccountType accType = tpt.getAccount();
			if ( accType == null ) {
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 관련계좌번호(29),계좌개설점(30),계좌개설점우편번호(30-1),계좌 개설일(31) 정보가 필요합니다.");
			}

			if ( accType.getBranchFinancialUnitName() == null || accType.getBranchFinancialUnitName().getStringValue().length() < 1) {
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 계좌개설점(30) 정보가 필요합니다.");
			}

			if (accType.getBranchAddress() == null 
					|| accType.getBranchAddress().getPostCodeID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 계좌개설점우편번호(30-1) 정보가 필요합니다.");
			}
			
			String openDate = accType.getOpenDate();
			if ( openDate == null || openDate.length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 계좌 개설일(31)이 필요합니다. 현재 입력 값은 '" + openDate +"' 입니다.");
			}
			
			if ( accType.getID() == null || accType.getID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 관련계좌번호(29)가 필요합니다.");
			}			
		}
		
		// 10/14 서식 변경 반영 
		if ( "25".equalsIgnoreCase(TTypeCode)      // 비계좌(유가 증권 )+ 지급 
				 || "26".equalsIgnoreCase(TTypeCode) ) // 비계좌(유가 증권 ) + 영수
		{
			SecuritiesDocumentType securities = ctr.getSecuritiesDocument();
			// 유가 증권 종류 , 관련 유가 증권 번호 필수 
			if ( securities == null )
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 관련유가증권 정보가 필요합니다.");				
			
			if ( securities.getTypeCode() == null
					|| securities.getTypeCode().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 관련유가증권종류(32) 값이 필요합니다.");
			}			
			
			//11. 선불카드일 경우 관련유가증권시작번호(33),관련유가증권끝번호(33-1) 은 선택항목 
			if ( !"11".equals(securities.getTypeCode().getStringValue()) ){
				if ( securities.getStartID() == null || securities.getStartID().getStringValue().length() < 1 ){
					throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 이고 관련유가증권종류(32)값이 '" + securities.getTypeCode().getStringValue()+ "' 입니다. 따라서 관련유가증권시작번호(33)가 필요합니다.");
				}		
				// 종료 번호는 안 올 수 있다.
				//if ( securities.getEndID() == null || securities.getEndID().getStringValue().length() < 1 ){
					//throw new FlatFileConvertingException("거래종류 코드 값이 '" + TTypeCode + "' 입니다. 따라서 관련 유가증권종료번호가 필요합니다.");
				//}			
			}
			
			// 05. 자기앞 수표일 경우 기입 (선택항목)
			if ( !"05".equals(securities.getTypeCode().getStringValue()) ){
				if (  securities.getPaymentMainFinancialUnitName() != null ) {
					String value = securities.getPaymentMainFinancialUnitName().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("관련유가증권종류(32) 값이 '05.자기앞 수표'가 아니고 '" + securities.getTypeCode().getStringValue() + "'입니다. 따라서, 제시은행명(34) '" + value + "' 값이  존재하면 안됩니다.");
					}
				}
				if (  securities.getPaymentMainFinancialUnitCode() != null ) {
					String value = securities.getPaymentMainFinancialUnitCode().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("관련유가증권종류(32) 값이 '05.자기앞 수표'가 아니고 '" + securities.getTypeCode().getStringValue() + "'입니다. 따라서, 제시은행기관코드(34-1) '" + value + "' 값이  존재하면 안됩니다.");
					}
				}
				if (  securities.getPaymentBranchFinancialUnitName() != null ) {
					String value = securities.getPaymentBranchFinancialUnitName().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("관련유가증권종류(32) 값이 '05.자기앞 수표'가 아니고 '" + securities.getTypeCode().getStringValue() + "'입니다. 따라서, 제시영업점 명(35) '" + value + "'이  존재하면 안됩니다.");
					}
				}
				if (  securities.getBranchAddress() != null 
						&& securities.getBranchAddress().getPostCodeID() != null ) {
					String value = securities.getBranchAddress().getPostCodeID().getStringValue();
					if ( value != null && value.length() > 0 ) {
						throw new FlatFileConvertingException("관련유가증권종류(32) 값이 '05.자기앞 수표'가 아니고 '" + securities.getTypeCode().getStringValue() + "'입니다. 따라서, 제시영업점우편번호(35-1) '" + value + "'이  존재하면 안됩니다.");
					}
				}
			}
			
		}
	
		if ( "22".equalsIgnoreCase(TTypeCode) )     // 비계좌 (송금) + 영수  
		{
			CounterPartPartyType cppType = ctr.getCounterPartParty();
			// 수취금융 기관명 , 수취금융 기관 코드 , 수취인(수취계좌)성명, 수취인 계좌번호  필수 
			if ( cppType == null )
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취 금융 기관 정보가 필요합니다.");
				
			if ( cppType.getReceiptOrganizationName() == null 
					|| cppType.getReceiptOrganizationName().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취금융기관명(37) 정보가 필요합니다.");
			}		

			if ( cppType.getReceiptOrganizationCode() == null 
					|| cppType.getReceiptOrganizationCode().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취금융기관코드(37-1) 정보가 필요합니다.");
			}		

			// 'ZZZZ' 해외 송금 일 경우 코드 값.
//			if ( !"ZZZZ".equals(cppType.getReceiptOrganizationCode().getStringValue()) ) { 
	//		}
			
			if ( cppType.getName() == null || cppType.getName().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취인(수취계좌주)성명(38) 정보가 필요합니다.");
			}		
			
			if ( cppType.getAccount() == null )
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취인계좌번호(36) 정보가 필요합니다.");
				
			if (  cppType.getAccount().getID() == null
					||  cppType.getAccount().getID().getStringValue().length() < 1 ){
				throw new FlatFileConvertingException("거래종류(28) 값이 '" + TTypeCode + "' 입니다. 따라서 수취인 계좌 번호  정보가 필요합니다.");
			}		
		}
	}
	
	/**
	 * TransactionPartyTypeCode를 체크한다.
	 * @param TPTypeCode
	 * @param value
	 * @throws FlatFileConvertingException
	 */
	private void CheckTransactionPartyTypeCode(String TPTypeCode, XmlAnySimpleType value) throws FlatFileConvertingException{
		// 값이 존재할 경우 오류 처리.
		if ( value != null  ) {
			throw new FlatFileConvertingException("거래자(사업자) 실명번호구분(10) 값이 '02','03','08','10','09','99'가 아니고  '" + TPTypeCode + "' 입니다. 따라서 법인 단체 거래자 정보 값  '" + value + "' 이 존재하면 안됩니다.");
		}
	}
	
	/**
	 * FlatFile 데이타를 CurrencyTransactionReportDocument 타입의 자바 객체로 변환 
	 * @param vec FlatFile 데이타를 Strng[] 형태로 읽어 들인 값
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

		  // 조건적 필수/optional 항목 
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
		  // EnterpriseOrganization 선택적 필수 항목 
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
		  // EnterpriseOrganization 선택적 필수 항목 끝 

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
		  // 조건적 필수/optional 항목 
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
		  // 조건적 필수/optional 항목 
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
		  
		  // 2006/01/11 추가 - 취소/정정 사유
		  if ( (tempVal = getValue(vec, XMLIndex++)) != null ) {
			  newCTR.addNewCancellationReason().setStringValue(tempVal);
		  }
		  return poDoc;
	}
	
	/**
	 * 해당 Index(키 값)의 프러퍼티 값을 읽어온다. 
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
		FlatFileToXML test = new FlatFileToXML("CTRSTART||01||2005-00000001||20050831||||01/99||2005-00000001||외환은행||1234||보고책임자||김갑수||02-2116-2680||홍길동||710222456123||01||KR||대한민국||145251||과천구 별양동||02-789-4781||||||||||||||||||20051201121212||서소문지점||145754||18||11||50000000||11||과천지점||125456||20030228||11-00-56-874||01||1452477||과천||1104||시청점||145754||||||||||CTREND","testfile.snd");
 		 		
		
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
