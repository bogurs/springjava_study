package kr.go.kofiu.str.validation.str;

import java.util.List;


import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.Securities;
import kr.go.kofiu.str.validation.RealNumValidator;
/**
 * STR 유가 증권정보 정형화 검증 수행.
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrSecuritiesValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

//		List<Transaction> tLists = str.getSTR().getDetail()
//				.getTransactionList();
		Transaction[] tLists = str.getSTR().getDetail().getTransactionArray();
		RealNumValidator rNumValidator = new RealNumValidator();
		String meanCd = null;
		String methodCd = null;
		String realNum = null;
		for (Transaction tx : tLists) {
			// List<Securities> sLists = tx.getSecuritiesList();
			Securities[] sLists = tx.getSecuritiesArray();

			meanCd = tx.getMean().getCode().toString();
			methodCd = tx.getMethod().getCode().toString();

			if ("02".equals(meanCd) || "04".equals(meanCd)
					|| "08".equals(meanCd) || ("16".equals(methodCd)
					|| "17".equals(methodCd) || "22".equals(methodCd)
					|| "23".equals(methodCd))) {
				// if (sLists.isEmpty())
				if (sLists.length < 1)
					throw new StrValidationException(
							"(STRValidation) 유가증권Invalid : 거래수단이 수표,채권,기타유가증권이거나 거래종류가 수표지급,수표발행,지급,발행일 경우 유가증권 정보는 필수입력 입니다. <Securities> 제약조건을 확인 하십시오.");
			}
			for (Securities scrt : sLists) {
				realNum = scrt.getRealNumber().getCode().toString();
				
				if ((null == scrt.getOrgName() || "".equals(scrt.getOrgName().getStringValue()))
						&& (null == scrt.getIssueOrgName() || "".equals(scrt.getIssueOrgName().getStringValue()))) {
					throw new StrValidationException(
							"(STRValidation) 유가증권Invalid : 지급은행과 발행은행 중 하나는 필수 기재해야합니다.");
				}
				
				if (("01".equals(realNum) || "02".equals(realNum))
						&& !rNumValidator.validZuminNumber(scrt.getRealNumber()
								.getStringValue())) {
					throw new StrValidationException(
							"(STRValidation) 유가증권Invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오.");
				}
				if ("03".equals(realNum)
						&& !rNumValidator.validBizNumber(scrt.getRealNumber()
								.getStringValue())) {
					throw new StrValidationException(
							"(STRValidation) 유가증권Invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오.");
				}
			}
		}
	}
}
