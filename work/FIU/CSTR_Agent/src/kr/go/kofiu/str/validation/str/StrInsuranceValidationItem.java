package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
/**
 * STR �������� ����ȭ ���� ����.
 * �ŷ���ǰ�� ������ ��� ����
 * @param str
 *            ���� ����� ������ STR XML
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
					throw new StrValidationException("(STRValidation) ��������Invalid : �ŷ���ǰ�� ������ ��� ���� ���� �ʼ��Է� �Դϴ�. <Insurance> ���������� Ȯ�� �Ͻʽÿ�.");
			}
		}
	}
}
