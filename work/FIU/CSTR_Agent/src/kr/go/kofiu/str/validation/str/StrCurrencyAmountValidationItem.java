package kr.go.kofiu.str.validation.str;

import java.math.BigDecimal;
import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
/**
 * STR 통화종류 정보 정형화 검증 수행.
 * 1.통화종류가 KRW이고 거래 종류 코드가 '99'(기타)가 아닐 경우 값은 1이상이어야 함
 * 2.통화종류가 KRW이외의 경우 값은 1이상이어야 함
 * 3.거래 통화가 원화(MoneyType="KRW")가 아니고 거래 방식(Method/@code)가 99가 아니면서 
 *   미화거래량이 1보다작을 경우 오류 발생
 *   @param str
 *            보고 기관이 발행한 STR XML
 *   @throw StrValidationException
 */
public class StrCurrencyAmountValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		// List<Transaction> lists = str.getSTR().getDetail().getTransactionList();
		Transaction[] lists = str.getSTR().getDetail().getTransactionArray();
		String moneyTypeCd = null;
		String methodCd = null;
		
		for (Transaction tx : lists) {
			moneyTypeCd = tx.getMoneyType().getCode();
			methodCd = tx.getMethod().getCode().toString();
			
			if ("KRW".equals(moneyTypeCd) && !"99".equals(methodCd)
					&& tx.getKRWAmount().compareTo(new BigDecimal("1")) == -1) {				
				throw new StrValidationException("(STRValidation) 통화금액Invalid : 원화통화금액은 1이상이어야 합니다. <KRWAmount> 제약조건을 확인 하십시오.");
			}			
			
			if (!"KRW".equals(moneyTypeCd) && !"99".equals(methodCd)){
					if (tx.getForeignAmount().compareTo(new BigDecimal("1")) == -1 )
						throw new StrValidationException("(STRValidation) 통화금액Invalid : 외화통화금액은 1이상이어야 합니다. <ForeignAmount> 제약조건을 확인 하십시오.");
			
					if( tx.getUSDAmount().compareTo(new BigDecimal("1")) == -1) 
						throw new StrValidationException("(STRValidation) 통화금액Invalid : 달러통화금액은 1이상이어야 합니다. <USDAmount> 제약조건을 확인 하십시오.");
			}
		}
	}
}
