package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
/**
 * STR 보험정보 정형화 검증 수행.
 * 거래상품이 보험일 경우 존재
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrInsuranceValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		// List<Transaction> lists = str.getSTR().getDetail().getTransactionList();
		Transaction[] lists = str.getSTR().getDetail().getTransactionArray();
		String goodscd = null;

		for (Transaction tx : lists) {
			goodscd = tx.getGoods().getCode().toString();
			

			if ("61".equals(goodscd) || "62".equals(goodscd)
					|| "63".equals(goodscd) || "64".equals(goodscd)
					|| "65".equals(goodscd) || "66".equals(goodscd)) {
				if (tx.getInsurance() == null)
					throw new StrValidationException("(STRValidation) 보험정보Invalid : 거래상품이 보험일 경우 보험 정보 필수입력 입니다. <Insurance> 제약조건을 확인 하십시오.");
			}
		}
	}
}
