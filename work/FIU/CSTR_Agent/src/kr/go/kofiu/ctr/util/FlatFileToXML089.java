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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : FlatFile을 읽어서 XML을 표현하는 자바 객체로 변환 
 * 작   성   자  : 이수영
 * 작   성   일  : 2007. 11. 01
 * <pre>
 *******************************************************/
public class FlatFileToXML089 {
 
	/**
	 * FlatFile 데이타
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
	 * FlatFile에서 XML로 변경된 객체를 이용하여 각 condition별로 conditional mandatory 및 mandatory를 check한다. 
	 * @param document
	 * @throws FlatFileConvertingException
	 */
	public void checkConditionalMandatory(CurrencyTransactionReportDocument document) throws FlatFileConvertingException {
		CurrencyTransactionReportType ctr = document.getCurrencyTransactionReport();
		// Mandatory 01 : 메시지타입(Field 01)
		if ( ctr.getMessageCode() == null ){ 
				throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01)에 값이 없습니다.");
		}
		
		// Mandatory 02 : 문서번호(Field 02)
		if ( ctr.getDocumentID() == null ){ 
				throw new FlatFileConvertingException("##[필수 항목 위반] 문서번호(Field 02)에 값이 없습니다.");
		}
		
		// Mandatory 03 : 보고일자(Field 03)
		if ( ctr.getDate() == null ){ 
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고일자(Field 03)에 값이 없습니다.");
		}
		
		if (   ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND) 
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL) ) {
			// Conditional Mandatory 01 : 취소/정정보고일 경우에는 종전문서번호(Field 04)가 반드시 필요함.
			if ( ctr.getFormerDocumentID() == null ){
				throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 메시지타입(Field 01) 값이  02 또는 03이면 정정 또는 취소보고이므로 종전문서번호(Field 04)가 반드시 필요합니다.");
			}
			// Conditional Mandatory 50 : 취소/정정보고일 경우에는 취소 또는 정정 사유(Field 51)가 반드시 필요함.
			if ( ctr.getCancellationReason() == null ){
				throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 메시지타입(Field 01) 값이  02 또는 03이면 정정 또는 취소보고이므로 취소 또는 정정 사유(Field 82)가  반드시 필요합니다.");
			}
		}
		else {
			if ( ctr.getFormerDocumentID() != null ){
				// Conditional Mandatory 02 : 신규보고일 경우에는 종전문서번호(Field 04)에 값이 없어야 함.
				throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 메시지타입(Field 01) 값이  01이면 신규보고이므로 종전문서번호(Field 04)에 값이 존재할 수 없습니다.");
			}
			
			if ( ctr.getCancellationReason() != null ){
				// Conditional Mandatory 02 : 신규보고일 경우에는 취소 또는 정정 사유(Field 51)에 값이 없어야 함.
				throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 메시지타입(Field 01) 값이  01이면 신규보고이므로 취소 또는 정정 사유(Field 82)에 값이 존재할 수 없습니다.");
			}
		}
		
		// Mandatory 04 : 거래순번(Field 05)
		if ( ctr.getOrder() == null ){ 
				throw new FlatFileConvertingException("##[필수 항목 위반] 거래순번(Field 05)에 값이 없습니다.");
		}
		
		// Mandatory 05 : 기본문서번호(Field 06)
		if ( ctr.getMainDocumentID() == null ){ 
				throw new FlatFileConvertingException("##[필수 항목 위반] 기본문서번호(Field 06)에 값이 없습니다.");
		}

		
		
		// 신규 또는 정정 보고일 때
		if(    ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.SND) 
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.RESND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_SND)
			|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_RESND)){
			
			if ( ctr.getReportAdministrationParty() != null ){
				// Mandatory 06 : 보고기관명(Field 07)
				if (ctr.getReportAdministrationParty().getName() == null) {
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관명(Field 07)에 값이 없습니다.");
				}
				
				// Mandatory 07 : 보고기관코드(Field 08)
				if (ctr.getReportAdministrationParty().getCode() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관코드(Field 08)에 값이 없습니다.");
				}
				
				// Mandatory 10 : 보고담당자전화번호(Field 11)
				if (ctr.getReportAdministrationParty().getContact() != null){
					if (ctr.getReportAdministrationParty().getContact().getTelephoneID() == null){
						throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자전화번호(Field 11)에 값이 없습니다.");
					}
				}
				else {
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자전화번호(Field 11)에 값이 없습니다.");
				}
			}
			else {
				// 보고기관정보 자체가 없음. Mandatory 06 조건으로 에러 메세지를 대체함.
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관명(Field 07)에 값이 없습니다.");
			}
			
			// Mandatory 08 : 보고책임자명(Field 09)
			if (ctr.getPersonName() == null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고책임자명(Field 09)에 값이 없습니다.");
			}
			
			if (ctr.getTransaction() != null){
				if (ctr.getTransaction().getPersonName() == null){
					// Mandatory 09 : 보고담당자명(Field 10)
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자명(Field 10)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getDateTime() == null){
					// Mandatory 14 : 거래발생일시(Field 29)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래발생일시(Field 42)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getBranchFinancialUnitName() == null){
					// Mandatory 15 : 거래영업점명(Field 30)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래영업점명(Field 43)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getChannelCode() == null){
					// Mandatory 16 : 거래채널 코드(Field 32)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래채널 코드(Field 45)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getMeansCode() == null){
					// Mandatory 17 : 거래수단 코드(Field 33)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래수단 코드(Field 46)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getAmount() == null){
					// Mandatory 18 : 거래금액(Field 34)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래금액(Field 47)에 값이 없습니다.");
				}
				
				if (ctr.getTransaction().getTypeCode() == null){
					// Mandatory 19 : 거래종류 코드(Field 48)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래종류 코드(Field 48)에 값이 없습니다.");
				}
				else {
					// 계좌거래일 경우
					if (   "11".equals(ctr.getTransaction().getTypeCode().getStringValue())
						|| "12".equals(ctr.getTransaction().getTypeCode().getStringValue())){
	
						if (ctr.getTransactionParty().getAccount() != null) {
							// Conditional Mandatory 02 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 반드시 관련계좌번호(Field 58)값이 필요함.
							if(ctr.getTransactionParty().getAccount().getID() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 관련계좌번호(Field 58)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 03 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 반드시 계좌개설영업점명(Field 59)값이 필요함.
							if(ctr.getTransactionParty().getAccount().getBranchFinancialUnitName() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 계좌개설영업점명(Field 59)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 04 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 반드시 계좌개설영업점 우편번호(Field 60)값이 필요함.
							if(ctr.getTransactionParty().getAccount().getBranchAddress() != null){
								if (ctr.getTransactionParty().getAccount().getBranchAddress().getPostCodeID() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우계좌개설영업점 우편번호(Field 60)값이  반드시 필요합니다.");
								}
							} else {
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우계좌개설영업점 우편번호(Field 60)값이  반드시 필요합니다.");
							}
													
							// Conditional Mandatory 05 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 반드시 계좌개설일자(Field 61)값이 필요함.
							if(ctr.getTransactionParty().getAccount().getOpenDate() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 계좌개설일자(Field 61)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 40 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 반드시 계좌개설대리인 존재여부(Field 62)값이 필요함.
							if(ctr.getTransactionParty().getAccount().getOpenAgentYN() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 계좌개설대리인 존재여부(Field 62)값이  반드시 필요합니다.");
							}
							else if("1".equals(ctr.getTransactionParty().getAccount().getOpenAgentYN().getStringValue())){// 계좌개설대리인이 존재할 경우의 제약사항 체크
								if (ctr.getTransactionParty().getAccount().getOpenAgent() != null){
									// Conditional Mandatory 41 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인명(Field 63)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getName() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인명(Field 63)값이 반드시 필요합니다.");
									}
									
									// Conditional Mandatory 42 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 실명번호구분(Field 64)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getTypeCode() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 실명번호구분(Field 64)값이 반드시 필요합니다.");
									}
	
									// Conditional Mandatory 43 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 실명번호(Field 65)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getID() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 실명번호(Field 65)값이 반드시 필요합니다.");
									}
									
									// Conditional Mandatory 44 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 국적코드(Field 67)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationalityCode() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우계좌개설대리인 국적코드(Field 67)값이 반드시 필요합니다.");
									}
	
									// Conditional Mandatory 45 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 국적명(Field 68)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationality() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 국적명(Field 68)값이 반드시 필요합니다.");
									}
	
									// Conditional Mandatory 46 : 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 관계코드(Field 69)값이 반드시 필요함.
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인 관계코드(Field 69)값이 반드시 필요합니다.");
									}
									else {
										if ("99".equals(ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode().getStringValue())){
											// Conditional Mandatory 47 : 계좌개설대리인 관계코드(Field 69)의 값이 99(기타)일 경우 거래대리인의 거래자와의 관계명(Field 81)값이 반드시 필요함.
											if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() == null){
												throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 관계코드(Field 69)의 값이 99(기타)일 경우 계좌개설대리인 관계명(Field 70)값이 반드시 필요합니다.");
											}
										}
										else {
											// Conditional Mandatory 48 : 계좌개설대리인 관계코드(Field 69)의 값이 99(기타)가 아닐 경우 거래대리인의 거래자와의 관계명(Field 81)값이 존재할 수 없음.
											if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() != null){
												throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 관계코드(Field 69)의 값이 99(기타)가 아닐 경우 거래대리인의 거래자와의 관계명(Field 70)값이 존재할 수 없습니다.");
											}
										}
									}
								}
								else {
									// 계좌개설대리인 존재여부(Field 62)를 1(존재)로 선택했지만 계좌개설대리인 정보 자체가 없음. Conditional Mandatory 41의 오류 메세지로 대체.
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)일 경우 계좌개설대리인명(Field 63)값이 반드시 필요합니다.");
								}
							}
							else {
								// Conditional Mandatory 49 : 계좌개설대리인 존재여부(Field 62)가 2(없음) 또는 3(파악할 수 없음)일 경우 계좌개설대리인 관련 데이터가 없어야 함.
								if (ctr.getTransactionParty().getAccount().getOpenAgent() != null){
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getName() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인명(Field 63)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getTypeCode() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 실명번호구분(Field 64)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getID() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 실명번호(Field 65)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getContact() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 전화번호(Field 66)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationalityCode() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 국적코드(Field 67)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getNationality() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 국적명(Field 68)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationshipCode() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 관계코드(Field 69)값이 존재할 수 없습니다.");
									}
									if (ctr.getTransactionParty().getAccount().getOpenAgent().getRelationship() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 계좌개설대리인 존재여부(Field 62)값이 1(존재)이 아닌 경우 계좌개설대리인 관계명(Field 70)값이 존재할 수 없습니다.");
									}
								}
							}
						}
						else {
							// 계좌정보 자체가 없음. Conditional Mandatory 02의 에러 메시지로 대체.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)일 경우 관련계좌번호(Field 58)값이  반드시 필요합니다.");
						}
					}
					else {
						if (ctr.getTransactionParty().getAccount() != null) {
							// Conditional Mandatory 06 : 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)이 아닐  경우 관련계좌 데이터가 없어야 함.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 11 또는 12(계좌거래)가 아닐 경우 계좌와 관련된 필드(Field 58,59,60,61,62,63,64,65,66,67,68,69,70)에 값이 존재할 수 없습니다.");
						}
					}
					
					// 유가증권 거래일 경우
					if (   "25".equals(ctr.getTransaction().getTypeCode().getStringValue())
						|| "26".equals(ctr.getTransaction().getTypeCode().getStringValue())){
						if (ctr.getSecuritiesDocument() != null){
							// Conditional Mandatory 07 : 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 반드시 관련유가증권의 종류(Field 71)값이 필요함.
							if(ctr.getSecuritiesDocument().getTypeCode() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 관련유가증권의 종류(Field 71)값이  반드시 필요합니다.");
							}
							// 유가증권 - 자기앞수표 거래일 경우
							else if ("05".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())){
								// Conditional Mandatory 11 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급금융기관명(Field 74)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitName() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 현금지급금융기관명(Field 74)값이  반드시 필요합니다.");
								}
								
								// Conditional Mandatory 12 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급금융기관코드(Field 75)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 현금지급금융기관코드(Field 75)값이  반드시 필요합니다.");
								}		
								
								// Conditional Mandatory 13 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급영업점명(Field 76)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 현금지급영업점명(Field 76)값이  반드시 필요합니다.");
								}
								
								// Conditional Mandatory 14 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급영업점명 우편번호(Field 77)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 현금지급영업점명 우편번호(Field 77)값이  반드시 필요합니다.");
								}
							}
							else {
								/*
								// Conditional Mandatory 15 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)이 아닐 경우 자기앞수표 데이터가 없어야 함.
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)가 아닐 경우 자기앞수표 정보와 관련된 필드(Field 74,75,76,77)에 값이 존재할 수 없습니다.");
								*/
								
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitName() != null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)가 아닐 경우 자기앞수표 정보와 관련된 필드(Field 74)에 값이 존재할 수 없습니다.");
								}
								
								// Conditional Mandatory 12 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급금융기관코드(Field 75)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)가 아닐 경우 자기앞수표 정보와 관련된 필드(Field 75)에 값이 존재할 수 없습니다.");
								}		
								
								// Conditional Mandatory 13 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급영업점명(Field 76)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)가 아닐 경우 자기앞수표 정보와 관련된 필드(Field 76)에 값이 존재할 수 없습니다.");
								}
								
								// Conditional Mandatory 14 : 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)일 경우 반드시 현금지급영업점명 우편번호(Field 77)값이 필요함.
								if(ctr.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 관련유가증권의 종류(Field 71)값이 05(자기앞수표 거래)가 아닐 경우 자기앞수표 정보와 관련된 필드(Field 77)에 값이 존재할 수 없습니다.");
								}
								
								
							}
							
							// Conditional Mandatory 08 : 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 반드시 관련유가증권 시작번호(Field 72)값이 필요함.
							 if(!"11".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())&
								!"99".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue()))
									{
								      if(ctr.getSecuritiesDocument().getStartID() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 관련유가증권 시작번호(Field 72)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 09 : 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 반드시 관련유가증권 끝번호(Field 73)값이 필요함.
							// if(ctr.getSecuritiesDocument().getEndID() == null){
							//	throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 관련유가증권 끝번호(Field 73)값이  반드시 필요합니다.");
							//}
						}
							 }
						else {
							// 유가증권정보 자체가 없음. Conditional Mandatory 07의 에러 메시지로 대체.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)일 경우 관련유가증권의 종류(Field 71)값이  반드시 필요합니다.");
						}
					}
					else {
						//if(!"11".equals(ctr.getSecuritiesDocument().getTypeCode().getStringValue())||
						//		ctr.getSecuritiesDocument() != null){
						if(ctr.getSecuritiesDocument() != null){
						// Conditional Mandatory 10 : 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)이 아닐 경우 유가증권 데이터가 없어야 함.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 25 또는 26(유가증권 거래)이 아닐 경우 유가증권정보와 관련된 필드(Field 71,72,73,74,75,76,77)에 값이 존재할 수 없습니다.");
						}
					}
					
					// 무통장입금에 의한 송금거래일 때
					if ("21".equals(ctr.getTransaction().getTypeCode().getStringValue())||
							"22".equals(ctr.getTransaction().getTypeCode().getStringValue())){
						if (ctr.getCounterPartParty() != null){
							// Conditional Mandatory 16 : 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우 반드시 수취인 계좌번호(Field 78)값이 필요함.
							if (ctr.getCounterPartParty().getAccount() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우  수취인 계좌번호(Field 78)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 17 : 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우 반드시 수취금융기관명(Field 79)값이 필요함.
							if (ctr.getCounterPartParty().getReceiptOrganizationName() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우  수취금융기관명(Field 79)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 18 : 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우 반드시 수취금융기관코드(Field 80)값이 필요함.
							if (ctr.getCounterPartParty().getReceiptOrganizationCode() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우  수취금융기관코드(Field 80)값이  반드시 필요합니다.");
							}
							
							// Conditional Mandatory 19 : 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우 반드시 수취인(수취계좌주)성명(Field 81)값이 필요함.
							if (ctr.getCounterPartParty().getName() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우  수취인(수취계좌주)성명(Field 81)값이  반드시 필요합니다.");
							}
						}
						else {
							// 무통장입금에 의한 상대계좌 정보 자체가 없음. Conditional Mandatory 16의 에러 메시지로 대체.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)일 경우  수취인 계좌번호(Field 78)값이  반드시 필요합니다.");
						}
					}
					else {
						if (ctr.getCounterPartParty() != null){
							// Conditional Mandatory 20 : 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)이 아닐 경우 무통장입금에 의한 상대계좌 정보 데이터가 없어야 함.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래종류 코드(Field 48)값이 21 또는 22(무통장입금에 의한 송금거래)가 아닐 경우 상대계좌정보와 관련된 필드(Field 78,79,80,81)에 값이 존재할 수 없습니다.");
						}
					}
				}
				
				if (ctr.getTransaction().getTypeCode() == null){
					// Mandatory 20 : 거래대리인 존재여부(Field 49)
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래대리인 존재여부(Field 49)에 값이 없습니다.");
				}
				else { // 거래대리인이 존재할 경우의 제약사항 체크
					if ("1".equals(ctr.getTransaction().getAgentExistYN().getStringValue())){
						if (ctr.getTransaction().getAgent() != null){
							// Conditional Mandatory 33 : 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인명(Field 50)값이 반드시 필요함.
							if (ctr.getTransaction().getAgent().getName() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인명(Field 50)값이 반드시 필요합니다.");
							}
	
							// Conditional Mandatory 34 : 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인 국적 코드(Field 54)값이 반드시 필요함.
							if (ctr.getTransaction().getAgent().getNationalityCode() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인 국적 코드(Field 54)값이 반드시 필요합니다.");
							}
	
							//if(!"KR".equals(ctr.getTransactionParty().getNationalityCode().getStringValue())){
							// Conditional Mandatory 35 : 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인 국적명(Field 55)값이 반드시 필요함.
							if (ctr.getTransaction().getAgent().getNationality() == null ){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우  거래대리인 국적명(Field 55)값이 반드시 필요합니다.");
							}
							
							// Conditional Mandatory 36 : 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인의 거래자와의 관계코드(Field 56)값이 반드시 필요함.
							if (ctr.getTransaction().getAgent().getRelationshipCode() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래자와의 관계코드(Field 56)값이 반드시 필요합니다.");
							}
							else {
								if ("99".equals(ctr.getTransaction().getAgent().getRelationshipCode().getStringValue())){
									// Conditional Mandatory 37 : 거래대리인 관계코드(Field 56)의 값이 99(기타)일 경우 거래대리인의 거래자와의 관계명(Field 57)값이 반드시 필요함.
									if (ctr.getTransaction().getAgent().getRelationship() == null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 관계코드(Field 56)의 값이 99(기타)일 경우 거래대리인의 거래자와의 관계명(Field 57)값이 반드시 필요합니다.");
									}
								}
								else {
									// Conditional Mandatory 38 : 거래대리인 관계코드(Field 56)의 값이 99(기타)가 아닐 경우 거래대리인의 거래자와의 관계명(Field 81)값이 존재할 수 없음.
									if (ctr.getTransaction().getAgent().getRelationship() != null){
										throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 관계코드(Field 56)의 값이 99(기타)가 아닐 경우 거래대리인의 거래자와의 관계명(Field 57)값이 존재할 수 없습니다.");
									}
								}
							}
						}
						else {
							// 거래대리인 정보 자체가 없음. Conditional Mandatory 33 조건으로 에러 메세지를 대체함.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)일 경우 거래대리인명(Field 50)값이 반드시 필요합니다.");
						}
					}
					else { // Conditional Mandatory 39 : 거래대리인 존재 여부가 1이 아닐 때에 입력되지 않아야 하는 필드 체크
						if (ctr.getTransaction().getAgent() != null){
							if (ctr.getTransaction().getAgent().getName() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인명(Field 50)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getTypeCode() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인 실명번호구분(Field 51)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getID() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인 실명번호(Field 52)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getNationalityCode() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인 국적 코드(Field 54)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getNationality() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인 국적명(Field 55)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getContact() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인 전화번호(Field 53)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getRelationshipCode() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인의 거래자와의 관계코드(Field 56)값이 존재할 수 없습니다.");
							}
							if (ctr.getTransaction().getAgent().getRelationship() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래대리인 존재여부(Field 49)의 값이 1(존재)이 아닌 경우 거래대리인의 거래자와의 관계명(Field 57)값이 존재할 수 없습니다.");
							}
						}
					}
				}
			}
			else {
				// 거래정보 자체가 없음. Mandatory 09 조건으로 에러 메세지를 대체함.
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자명(Field 10)에 값이 없습니다.");
			}
	
			// 거래자(사업자) 정보 부분의 필수 및 선택적 필수 항목 체크
			if (ctr.getTransactionParty() != null){
				// Mandatory 11 : 거래자(사업자)명(Field 12)
				if (ctr.getTransactionParty().getName() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래자(사업자)명(Field 12)에 값이 없습니다.");
				}
				
				// Mandatory 12 : 거래자(사업자) 실명번호구분(Field 13)
				if (ctr.getTransactionParty().getTypeCode() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래자(사업자) 실명번호구분(Field 13)에 값이 없습니다.");
				}
				
				// Mandatory 13 : 거래자(사업자) 실명번호(Field 14)
				if (ctr.getTransactionParty().getID() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 거래자(사업자) 실명번호(Field 14)에 값이 없습니다.");
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
						
					// Conditional Mandatory : 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 03 경우 거래자 실명번호(Field 14) 자리수는 10자리이어야 함 .
					if (ctr.getTransactionParty().getID().getStringValue().length() != 10){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 사업자(Field 13(거래자(사업자)실명번호구분)값이 '03'일 경우 거래자 실명번호(Field 14) 자리수는 10자리이어야 합니다.");
					}
						
				} else {
					//Conditional Mandatory : 거래자 실명번호는 15자리 이하.
					if (ctr.getTransactionParty().getID().getStringValue().length() > 15){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자  실명번호(Field 14) 자리수는 15자리 이하입니다.");
					}
				}
				
				//거래자가 개인인 경우
				if (   "01".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "04".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "06".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "07".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "11".equals(ctr.getTransactionParty().getTypeCode().getStringValue())
					|| "99".equals(ctr.getTransactionParty().getTypeCode().getStringValue())){
					
					// Conditional Mandatory 21 : 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,99일 경우 거래자 국적명(Field 16)을 반드시 입력해야 함.
					if (ctr.getTransactionParty().getNationality() == null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,11,99)일 경우 거래자 국적명(Field 16)값이 반드시 필요합니다.");
					}
					
					// Conditional Mandatory 22 : 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,99일 경우 거래자 국적코드(Field 15)를 반드시 입력해야 함.
					if (ctr.getTransactionParty().getNationalityCode() == null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 개인(Field 13(거래자(사업자)실명번호구분)값이  01,04,06,07,11,99)일 경우 거래자 국적코드(Field 15)값이 반드시 필요합니다.");
					}
					
					if (!"KR".equals(ctr.getTransactionParty().getNationalityCode().getStringValue())){
						/*
						if (ctr.getTransactionParty().getRealAddress() != null){
							// Conditional Mandatory 23 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)인 경우 거래자의 연락가능한 실제 거소 우편번호(Field 52)값이 필요함.
							if (ctr.getTransactionParty().getRealAddress().getPostCodeID() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)인 경우 거래자의 연락가능한 실제 거소 우편번호(Field 52)값이  반드시 필요합니다.");
							}
							// Conditional Mandatory 24 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)인 경우 거래자의 연락가능한 실제 거소 주소(Field 53)값이 필요함.
							if (ctr.getTransactionParty().getRealAddress().getLineOne() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)인 경우 거래자의 연락가능한 실제 거소 주소(Field 53)값이  반드시 필요합니다.");
							}
						}
						else {
							// 거래자의 연락가능한 실제 거소  정보 자체가 없음. Conditional Mandatory 21의 에러 메시지로 대체.
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)인 경우 거래자의 연락가능한 실제 거소 우편번호(Field 52)값이  반드시 필요합니다.");
						}
						*/
						if(ctr.getTransactionParty().getAddress() == null) {
							// Conditional Mandatory 25 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우 거래자 생년월일(Field 19)값이 필요함.
							if (ctr.getTransactionParty().getBirthDate() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우 거래자 생년월일(Field 19)값이  반드시 필요합니다.");
							}
							// Conditional Mandatory 26 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우 거래자 성별(Field 20)값이 필요함.
							if (ctr.getTransactionParty().getGender() == null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우 거래자 성별(Field 20)값이  반드시 필요합니다.");
							}
						}/*2009.01.06 서식검증중 불필요함(주석처리)-jto
						else {
							// Conditional Mandatory 27 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우에만 거래자 생년월일(Field 19)값을 입력할 수 있음.
							if (ctr.getTransactionParty().getBirthDate() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고  거래자 우편번호/주소(Field 17,18)에 값을 입력했기 때문에 거래자 생년월일(Field 19)값이  존재할 수 없습니다.");
							}
							// Conditional Mandatory 28 : 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고 거래자 우편번호/주소(Field 17,18)가 없을 경우에만 거래자 성별(Field 20)값을 입력할 수 있음.
							if (ctr.getTransactionParty().getGender() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자가 외국인(Field 15(거래자 국적코드)의 값이 KR이 아닌 경우)이고  거래자 우편번호/주소(Field 17,18)에 값을 입력했기 때문에 거래자 성별(Field 20)에 값이 존재할 수 없습니다.");
							}
						}*/
					}
				}
				else { // Conditional Mandatory 29 : 거래자가 개인이 아닌 경우에 입력되지 않아야 하는 필드 체크
					if (ctr.getTransactionParty().getAddress() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이  01,04,06,07,11,99)이 아닌 경우에 거래자 주소(Field 17 및 Field 18)값이 존재할 수 없습니다.");
					}
					if (ctr.getTransactionParty().getMixedRealNameID() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이  01,04,06,07,11,99)이 아닌 경우에 거래자 실명조합번호(Field 22)값이 존재할 수 없습니다.");
					}
					/*if (ctr.getTransactionParty().getNationality() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,99)이 아닌 경우에 거래자 국적명(Field 16)값이 존재할 수 없습니다.");
					}
					if (ctr.getTransactionParty().getNationalityCode() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,99)이 아닌 경우에 거래자 국적코드(Field 15)값이 존재할 수 없습니다.");
					}*/
					/*
					if (ctr.getTransactionParty().getRealAddress() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,99)이 아닌 경우에 거래자 거래자의 연락가능한 실제 거소 정보(Field 52,53)값이 존재할 수 없습니다.");
					}*/
					if (ctr.getTransactionParty().getPassportID() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이  01,04,06,07,11,99)이 아닌 경우에 여권번호(Field 23)값이 존재할 수 없습니다.");
					}
					if (ctr.getTransactionParty().getBirthDate() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이  01,04,06,07,11,99)이 아닌 경우에 거래자 생년월일(Field 19)값이 존재할 수 없습니다.");
					}
					if (ctr.getTransactionParty().getGender() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,11,99)이 아닌 경우에 성별(Field 20)값이 존재할 수 없습니다.");
					}
					if (ctr.getTransactionParty().getJob() != null){
						throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 개인(Field 13(거래자(사업자)실명번호구분)값이 01,04,06,07,11,99)이 아닌 경우에 거래자 직업정보(Field 24,25,26,27)값이 존재할 수 없습니다.");
					}
				}
				
				// 거래자가 법인 또는 단체일 경우(090317기타코드추가jto)
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
								// Conditional Mandatory 30 : 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이고 대표자명(Field 28)을 입력하였을 때에 대표자 국적명(Field 32)값이 반드시 필요함.
								if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationality() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이고 대표자명(Field 28)을 입력하였을 때에 대표자 국적명(Field 32)값이 반드시 필요합니다.");
								}
								// Conditional Mandatory 31 : 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이고 대표자명(Field 28)을 입력하였을 때에 대표자 국적코드(Field 31)값이 반드시 필요함.
								if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationalityCode() == null){
									throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이고 대표자명(Field 28)을 입력하였을 때에 대표자 국적코드(Field 31)값이 반드시 필요합니다.");
								}
							}
						}
					}
				}
				else {// Conditional Mandatory 32 : 거래자가 법인 또는 단체가 아닌 경우에 입력되지 않아야 하는 필드 체크
					if (ctr.getEnterpriseOrganization() != null){
						if (ctr.getEnterpriseOrganization().getRepresentativePerson()!= null){
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getName() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 대표자명(Field 28)값이 존재할 수 없습니다.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getTypeCode() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 대표자 실명번호구분(Field 29)값이 존재할 수 없습니다.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getID() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 대표자 실명번호(Field 30)값이 존재할 수 없습니다.");
							}
							/*
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getAddress() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이 아닐 경우에 대표자 자택 주소정보(Field 64,65)값이 존재할 수 없습니다.");
							}
							*/
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationality() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 대표자 국적명(Field 32)값이 존재할 수 없습니다.");
							}
							if (ctr.getEnterpriseOrganization().getRepresentativePerson().getNationalityCode() != null){
								throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 대표자 국적코드(Field 31)값이 존재할 수 없습니다.");
							}
						}
						if (ctr.getEnterpriseOrganization().getEstablishmentDate() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 사업체(단체) 설립일(Field 33)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getKSICCode() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 업종코드(Field 34)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getAddress() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 사업체(단체)본점 주소 정보(Field 36,38)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getBusinessSiteAddress()!=null){
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 사업체(단체)사업장 주소 정보(Field 37,39)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getContact() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 사업체(단체)본점 전화번호(Field 40)값이 존재할 수 없습니다.");
						}					
						if (ctr.getEnterpriseOrganization().getBusinessSiteContact() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이02,03,05,08,09,11,12,99)이 아닐 경우에 사업체(단체)사업장 전화번호(Field 41)값이 존재할 수 없습니다.");
						}					
						if (ctr.getEnterpriseOrganization().getCorporationID() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,05,08,09,11,12,99)이 아닐 경우에 법인등록번호(Field 35)값이 존재할 수 없습니다.");
						}
						/*
						if (ctr.getEnterpriseOrganization().getScaleType() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이 아닐 경우에 기업규모 코드(Field 69)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getFinancialInstitutionYN() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이 아닐 경우에 금융기관 여부(Field 70)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getNonprofitYN() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이 아닐 경우에 비영리단체 여부(Field 71)값이 존재할 수 없습니다.");
						}
						if (ctr.getEnterpriseOrganization().getListedStocksYN() != null) {
							throw new FlatFileConvertingException("##[선택적 필수 항목 위반] 거래자(사업자)가 법인(Field 13(거래자(사업자)실명번호구분)값이 02,03,08,09)이 아닐 경우에 상장 여부(Field 72)값이 존재할 수 없습니다.");
						}
						*/
					}
				}
			}
			else {
				// 거래정보 자체가 없음. Mandatory 11 조건으로 에러 메세지를 대체함.
				throw new FlatFileConvertingException("##[필수 항목 위반] 거래자(사업자)명(Field 12)에 값이 없습니다.");
			}
		}
		// 취소보고일 때
		else if (  ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.CANCEL)
				|| ctr.getMessageCode().getStringValue().equals(TypeCode.MessageType.TEST_CANCEL)){
			
			if ( ctr.getReportAdministrationParty() != null ){
				// Mandatory 06 : 보고기관명(Field 07)
				if (ctr.getReportAdministrationParty().getName() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관명(Field 07)에 값이 없습니다.");
				}
				
				// Mandatory 07 : 보고기관코드(Field 08)
				if (ctr.getReportAdministrationParty().getCode() == null){
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관코드(Field 08)에 값이 없습니다.");
				}
				
				// Mandatory 10 : 보고담당자전화번호(Field 11)
				if (ctr.getReportAdministrationParty().getContact() != null){
					if (ctr.getReportAdministrationParty().getContact().getTelephoneID() == null){
						throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자전화번호(Field 11)에 값이 없습니다.");
					}
				}
				else {
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자전화번호(Field 11)에 값이 없습니다.");
				}
			}
			else {
				// 보고기관정보 자체가 없음. Mandatory 06 조건으로 에러 메세지를 대체함.
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고기관명(Field 07)에 값이 없습니다.");
			}
			
			// Mandatory 08 : 보고책임자명(Field 09)
			if (ctr.getPersonName() == null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고책임자명(Field 09)에 값이 없습니다.");
			}
			
			if (ctr.getTransaction() != null){
				if (ctr.getTransaction().getPersonName() == null){
					// Mandatory 09 : 보고담당자명(Field 10)
					throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자명(Field 10)에 값이 없습니다.");
				}
				
				// 기타 값이 거래정보 엘리먼트에서 입력되지 말아야 하는 필드 체크
				if (ctr.getTransaction().getAgent() != null
				 || ctr.getTransaction().getAgentExistYN() != null
				 || ctr.getTransaction().getAmount() != null
				 || ctr.getTransaction().getBranchAddress() != null
				 || ctr.getTransaction().getBranchFinancialUnitName() != null
				 || ctr.getTransaction().getChannelCode() != null
				 || ctr.getTransaction().getDateTime() != null
				 || ctr.getTransaction().getMeansCode() != null
				 || ctr.getTransaction().getTypeCode() != null ){
					throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01) 값이 03이면 취소보고이므로 거래내역 정보를 입력할 수 없습니다.");
				}
			}
			else {
				// 거래정보 자체가 없음. Mandatory 09 조건으로 에러 메세지를 대체함.
				throw new FlatFileConvertingException("##[필수 항목 위반] 보고담당자명(Field 10)에 값이 없습니다.");
			}
			
			// 기타 값이 입력되지 말아야 하는 필드 체크
			if(ctr.getCounterPartParty() != null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01) 값이 03이면 취소보고이므로 상대계좌 정보를 입력할 수 없습니다.");
			}
			if (ctr.getEnterpriseOrganization() != null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01) 값이 03이면 취소보고이므로 기관 또는 단체 정보를 입력할 수 없습니다.");
			}
			if (ctr.getTransactionParty() != null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01) 값이 03이면 취소보고이므로 거래자 정보를 입력할 수 없습니다.");
			}
			if (ctr.getSecuritiesDocument() != null){
				throw new FlatFileConvertingException("##[필수 항목 위반] 메시지타입(Field 01) 값이 03이면 취소보고이므로 유가증권 및 자기앞수표 정보를 입력할 수 없습니다.");
			}
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
		  
		  //JobType에 들어갈 아무런 값이 없으면 unset
		  if (isJobNull){
			  newTP.unsetJob();
		  }

		  // 조건적 필수/optional 항목 
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
		  
		  // OpenAgent가 Null인지 체크
		  if (isAgentNull){
			  newAcc.unsetOpenAgent();
		  }
		  
		  // Account가 Null인지 체크
		  if ( isAccountNull ) newTP.unsetAccount();
		  else 	isTrnPartyNull = false;
		  
		  		  
		  // TransactionParty가 Null인지 체크		  
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
		  
		  //1224일 수정
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
		 
		  // EnterpriseOrganization Null 체크
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
		  
		  // Agent Null 체크
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
		  
		  // SecuritiesDocument Null 체크
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
		//System.out.println("신규 서식의 flat파일을 처리하여 xml로 저장함 > ./../Messages/temp/" + filename);
		  
		  return poDoc;
	}
	
	/**
	 * 해당 Index(키 값)의 property 값을 읽어온다. 
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
		  
		  
		String flatfile = "CTRSTART||99||2009-00059448||20090617||||1/1||2009-00059448||대구은행||1331||신덕열||곽영도||T6982/2152||게이트뱅크（주）||03||0001078643003||||||||||||||||||||||||||||박남대||01||5611041079910||KR||한국||||||1101112910415||150010||700442||서울 영등포구 여의도동 교보증권빌딩１１층||대구 중구 남산2동 １７５번지서현교육관Ｂ／Ｄ／８０１호||0803302114||0534210810||20090615093228||영업부||706712||01||01||700000000||11||2||||||||||||||||||002040031883||영업부||706712||20031211||9||||||||||||||||||||||||||||||||||||||||||CTREND";
		//String flatfile = "CTRSTART||01||2008-11111111||20081018||||0000001/0000001||2008-11111111||금융정보분석원||9400||김부장||김부장||02-3479-2193||홍길동||04||111111111111111||660250||경남 진주시 강남동   111111||20081001||3||1111-1111-1111-1111||111111111111111||한국||KR||111111111111111||01||과천||17124||1111111111||||||||||||||||||||||||||||||20081001121212||성남||999999||01||01||3000000000||21||2||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||CTREND";
		String[] token = flatfile.split("\\|\\|");
		FlatFileToXML089 flatFileToXML089 = new FlatFileToXML089(flatfile,"test.SND");
		CurrencyTransactionReportDocument ctr = flatFileToXML089.convertToXML(token);
		System.out.println(ctr.toString());
		flatFileToXML089.checkConditionalMandatory(ctr);
		
	}
}
