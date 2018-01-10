package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.InterUserRelation;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
/**
 * STR user ���� ���� ����ȭ ���� ����.
 * @param str
 *            ���� ����� ������ STR XML
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
								"(STRValidation) ����� ����Invalid : ����ڰ��� ���� ����<InterUserRelation> �� �����Ǿ����ϴ�.");
					}
				}
			}
		}
		for (InterUserRelation inur : inurlists) {
			if ("52".equals(inur.getRelation().getCode().toString())) {
				if (inur.getRemark() == null || "".equals(inur.getRemark())) {
					throw new StrValidationException(
							"(STRValidation) ����� ����Invalid : �ŷ��ڰ����ڵ尡 '52'�븮���� ��� ������� �ʼ��Է� �Դϴ�. <Remark> ���������� Ȯ�� �Ͻʽÿ�.");
				}
			}
		}
	}
}
