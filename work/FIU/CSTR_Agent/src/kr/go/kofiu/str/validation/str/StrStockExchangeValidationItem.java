package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.Stock;
/**
 * STR 증권 정보 정형화 검증 수행.
 * 거래종류가 매수,매도,입고,출고,대체입고,대체출고 일 경우 존재
 * @param str
 *            보고 기관이 발행한 STR XML
 *  @throw StrValidationException
 */
public class StrStockExchangeValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

//		List<Transaction> tlists = str.getSTR().getDetail()
//				.getTransactionList();
		Transaction[] tlists = str.getSTR().getDetail().getTransactionArray();
		String methodCd = null;

		for (Transaction tx : tlists) {
			methodCd = tx.getMethod().getCode().toString();
			// List<Stock> stlists = tx.getStockList();
			Stock[] stlists = tx.getStockArray();
			
			if ("10".equals(methodCd) || "11".equals(methodCd)
					|| "12".equals(methodCd) || "13".equals(methodCd)
					|| "14".equals(methodCd) || "15".equals(methodCd)) {
				
				if (stlists.length < 1)
					throw new StrValidationException(
							"(STRValidation) 증권invalid : 거래종류가 매수,매도,입고,출고,대체입고,대체출고(거래종류코드 : '10'~'15') 일 경우 증권정보는  필수입력 입니다. <Stock> 제약조건을 확인 하십시오.");
			}
			// 증권거래 대상 금융기관 및 영업점 정보 검증
			for (Stock stk : stlists) {					
				if ("14".equals(methodCd) || "15".equals(methodCd)) {
					if (null == stk.getOrgName()
							|| "".equals(stk.getOrgName().getStringValue()))
						throw new StrValidationException(
								"(STRValidation) 증권대상금융기관invalid : 거래종류가 대체입고,대체출고 일 경우 지점명은 필수입력 입니다. <OrgName> 제약조건을 확인 하십시오.");
					if (null == stk.getBranchOffice()
							|| "".equals(stk.getBranchOffice().getStringValue()))
						throw new StrValidationException(
								"(STRValidation) 증권대상금융기관invalid : 거래종류가 대체입고,대체출고 일 경우 지점정보는 필수입력 입니다. <BranchOffice> 제약조건을 확인 하십시오.");
				}
			}			
		}
	}
}
