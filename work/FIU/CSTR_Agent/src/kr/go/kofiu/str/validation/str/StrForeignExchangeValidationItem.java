package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.ForeignExchange;

/**
 * STR ForeignExchange 정보 정형화 검증 수행. 
 * 거래수단이 외환이거나 거래종류가 환전일 경우 존재
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrForeignExchangeValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		// List<Transaction> lists = str.getSTR().getDetail().getTransactionList();
		Transaction[] lists = str.getSTR().getDetail().getTransactionArray();
		ForeignExchange fExchange = null;

		for (Transaction tx : lists) {
			String meanCd = tx.getMean().getCode().toString();
			String methodCd = tx.getMethod().getCode().toString();
			fExchange = tx.getForeignExchange();
			if ("05".equals(meanCd) || "03".equals(methodCd)) {
				if (null == fExchange
						|| "".equals(fExchange.getPurpose().getCode().toString())
						|| "".equals(fExchange.getPurpose().getStringValue()))
					throw new StrValidationException(
							"(STRValidation) 외환거래Invalid : 거래수단이 외환이거나거래종류가 환전일 경우 외환거래 정보 필수입력 입니다. <ForeignExchange> 제약조건을 확인 하십시오.");
			}
		}
	}
}
