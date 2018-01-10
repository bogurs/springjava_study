package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.InterUserRelation;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
/**
 * STR user 관계 정보 정형화 검증 수행.
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrInterUserRelationValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

//		List<Transaction> tlists = str.getSTR().getDetail()
//				.getTransactionList();
//		List<InterUserRelation> inurlists = str.getSTR().getDetail()
//				.getInterUserRelationList();
		Transaction[] tlists = str.getSTR().getDetail().getTransactionArray();
		InterUserRelation[] inurlists = str.getSTR().getDetail().getInterUserRelationArray();
		String relationCd = null;

		for (Transaction tx : tlists) {
			// List<UserRelation> urlists = tx.getUserRelationList();
			UserRelation[] urlists = tx.getUserRelationArray();

			for (UserRelation ur : urlists) {
				relationCd = ur.getRelationRole().getCode().toString();
				if ("02".equals(relationCd) || "03".equals(relationCd)
						|| "09".equals(relationCd) || "10".equals(relationCd)
						|| "12".equals(relationCd) || "13".equals(relationCd)) {
					
					// if (inurlists.isEmpty()) {
					if (inurlists.length < 1) {
						throw new StrValidationException(
								"(STRValidation) 사용자 정보Invalid : 사용자간의 관계 정보<InterUserRelation> 가 누락되었습니다.");
					}
				}
			}
		}
		for (InterUserRelation inur : inurlists) {
			if ("52".equals(inur.getRelation().getCode().toString())) {
				if (inur.getRemark() == null || "".equals(inur.getRemark())) {
					throw new StrValidationException(
							"(STRValidation) 사용자 정보Invalid : 거래자관계코드가 '52'대리인일 경우 관계명은 필수입력 입니다. <Remark> 제약조건을 확인 하십시오.");
				}
			}
		}
	}
}
