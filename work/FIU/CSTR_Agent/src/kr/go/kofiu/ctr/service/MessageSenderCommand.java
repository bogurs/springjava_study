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
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.EnterpriseOrganizationType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.TransactionPartyType;
import krGovKofiuDataAggregateBusinessInformationEntitiesSchemaModule10.TransactionType;
import krGovKofiuDataRootSchemaModule10.CurrencyTransactionReportType;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서를 집중 기간에 전송한다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 8. 25
 * 수 정 이 력  : 보고문서를 processRequestDocument를 통해 전송하던 형태
 * 				 -> JMS/SOAP을 통해 ESB로 전송하는 형식으로 변경
 * 				또한, 보고 문서를 통해 ebXML 파싱을 위한 DocumentToSoap 클래스 추가
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageSenderCommand {
	
	/**
	 * 보고 문서 전송 정보
	 */
	private MessageContext context;
	
	public MessageSenderCommand(MessageContext ctx){
		this.context = ctx;
	}
	
	/**
	 * 2014. 9. 10 보고 문서 송신 변경
	 * 보고 문서를 전송한다.
	 * JMS/SOAP을 통해 ESB로 전송
	 * 이를 위해 보고 문서를 ebXML 파싱과정을 거친다.
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
	
	public String getGUID_FIUF9999(){ //Main Flow용 GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
        int num = r.nextInt(10000);
        String randomValue = String.format("%04d", num);
        CurrencyTransactionReportType ctrRpt = context.getXmlDoc().getCurrencyTransactionReport();
		String orgCd = ctrRpt.getReportAdministrationParty().getCode().getStringValue();
		String agentID = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentID+orgCd+"9400"+"FIUF9999"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}


	/**
	 * 보고 문서 헤더 정보 생성 생성 
	 * @return ItemAnonType[] 보고 문서 헤더 정보
	 * @throws AgentException 
	 */
	private ItemAnonType[] makeHeader() throws AgentException {
		int idx = 0;
		
		//IF_ID(8)+기관코드(4)+YYYYMMDDHHMMSSFF3+random(4)
		//IIB InterfaceID 값을 설정
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
				, kr.go.kofiu.ctr.util.DocumentObject.DOCUMENT_VERSION_OLD);
		
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
	 * 보고 문서 body 정보 생성
	 * @return ItemAnonType[] 보고 문서 body 정보
	 */
	private ItemAnonType[] makeBodys() {
		// XML For Body Data
		CurrencyTransactionReportType ctrRpt = context.getXmlDoc().getCurrencyTransactionReport();
		ArrayList list = new ArrayList();
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationPartyCode, 
				ctrRpt.getReportAdministrationParty().getCode().getStringValue())); // 보고 기관코드
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.ReportAdministrationPartyName, 
				ctrRpt.getReportAdministrationParty().getName().getStringValue())); // 보고 기관 명
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDocumentID, 
				ctrRpt.getDocumentID().getStringValue())); //문서 번호 
		list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CurrencyTransactionReportDate, 
				ctrRpt.getDate())); // 보고 일자 
		
		//	 이전  보고 문서 번호. 있을 수 도 있고 없을 수도 있다.
		if( ctrRpt.getFormerDocumentID() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.FormerDocumentID, 
				ctrRpt.getFormerDocumentID().getStringValue())); // 종전 문서 번호
		}

		//	 2006/01/17 일 추적을 위해 값 추가 
		if( ctrRpt.getMainDocumentID() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.MainDocumentID, 
					ctrRpt.getMainDocumentID().getStringValue())); // 기본문서 번호(4-1)
		}
		if( ctrRpt.getOrder() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionOrder, 
					ctrRpt.getOrder().getStringValue())); // 거래순번
		}
		
		// 2006/01/11 - 취소보고 추가로 인해 코드 변경 
		TransactionPartyType trnPartyType = ctrRpt.getTransactionParty(); 
		if ( trnPartyType != null ) {
			list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyTypeCode, 
					trnPartyType.getTypeCode().getStringValue())); //  거래자 실명 번호 구분 
	
			// 조건적 필수 - 2005/10/10 일 서식 변경 반영 
			if ( trnPartyType.getNationalityCode() != null ){
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyNationalityCode, 
						trnPartyType.getNationalityCode().getStringValue())); // 거래자의 국적 
			}
			
			// 11/23 추가 
			if ( trnPartyType.getAddress() != null 
					&& trnPartyType.getAddress().getPostCodeID() != null ) {
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionPartyAdressPostCodeID
						, trnPartyType.getAddress().getPostCodeID().getStringValue())); //자택 우편 번호
			}

			// 조건 적 필수. 값이 안나올 수 있다.
			if (trnPartyType.getAccount() != null 
				 && trnPartyType.getAccount().getBranchAddress() != null
				 && trnPartyType.getAccount().getBranchAddress().getPostCodeID() != null ) { 
				
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.AccountOpenPlaceAddressPostCodeID, 
						trnPartyType.getAccount().getBranchAddress().getPostCodeID().getStringValue())); // 계좌 개설점 우편 번호
			}
		}
		
		// 이 값은 조건적 필수라 값이 안 나올 수 있다.
		EnterpriseOrganizationType entOrgType = ctrRpt.getEnterpriseOrganization();
		if ( entOrgType != null ){
			// 11/23 추가 
			if (entOrgType.getRepresentativePerson() != null  
					&& entOrgType.getRepresentativePerson().getTypeCode() != null){
				list.add(new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.EnterpriseOraganizationRepresentativePersonTypeCode
					, entOrgType.getRepresentativePerson().getTypeCode().getStringValue()));
			}
			
			//	 표준 산업 분류 코드 
			if ( entOrgType.getKSICCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.EnterpriseOraganizationKSICCode, 
					entOrgType.getKSICCode().getStringValue())); 
			}
			
			// 사업자 단체 우편 번호
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
					trnType.getBranchAddress().getPostCodeID().getStringValue())); // 거래 영업점 우편 번호
			}
			
			if ( trnType.getChannelCode()  != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionChannelCode, 
					trnType.getChannelCode().getStringValue())); // 거래 방법
			}
			
			if (trnType.getMeansCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionMeansCode, 
					trnType.getMeansCode().getStringValue())); // 거래 수단
			}
			
			if ( trnType.getTypeCode() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionTypeCode, 
					trnType.getTypeCode().getStringValue())); // 거래 종류
			}
			
			// 2006/02/07 추가 
			if ( trnType.getDateTime() != null ) {
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.TransactionDateTime, 
						trnType.getDateTime()) ); // 거래 발생 일시
			}
		}
		
		
		//	 이 값은 조건적 필수라 값이 안 나올 수 있다.
		if ( ctrRpt.getSecuritiesDocument() != null ) {
			if ( ctrRpt.getSecuritiesDocument().getTypeCode() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.SecuritiesDocumentTypeCode, 
					ctrRpt.getSecuritiesDocument().getTypeCode().getStringValue())); //유가 증권 종류 코드

			if ( ctrRpt.getSecuritiesDocument().getPaymentMainFinancialUnitCode() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.SecuritesDocumentPaymentMainFinancialUnitCode, 
					ctrRpt.getSecuritiesDocument().getPaymentMainFinancialUnitCode().getStringValue())); // 지급 은행 코드
			
			if ( ctrRpt.getSecuritiesDocument().getBranchAddress() != null 
					&& ctrRpt.getSecuritiesDocument().getBranchAddress().getPostCodeID() != null )
				list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.PaymentBranchOfficeAddressPostCodeID, 
					ctrRpt.getSecuritiesDocument().getBranchAddress().getPostCodeID().getStringValue())); //제시영업점 우편번호
		}
		
		//	 이 값은 조건적 필수라 값이 안 나올 수 있다.
		if( ctrRpt.getCounterPartParty() != null
				&& ctrRpt.getCounterPartParty().getReceiptOrganizationCode() != null ) {
			list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.CounterPartPartyReceiptOrganizationCode, 
				ctrRpt.getCounterPartParty().getReceiptOrganizationCode().getStringValue())); // 수취 금융 기관 코드
		}
		
		list.add( new ItemAnonType(kr.go.kofiu.ctr.util.DocumentObject.AttachmentFileNameText, 
				context.getFilename())); // 첨부 파일 명 


		ItemAnonType[] bodys = new ItemAnonType[list.size()];
		Iterator iter = list.iterator();
		for ( int j = 0 ; iter.hasNext() ; j++) {
			bodys[j] = (ItemAnonType)iter.next();
		}
		return bodys;
	}

	
}
