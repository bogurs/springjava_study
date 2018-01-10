package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.ForeignExchange;

/**
 * STR ForeignExchange ���� ����ȭ ���� ����. 
 * �ŷ������� ��ȯ�̰ų� �ŷ������� ȯ���� ��� ����
 * @param str
 *            ���� ����� ������ STR XML
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
							"(STRValidation) ��ȯ�ŷ�Invalid : �ŷ������� ��ȯ�̰ų��ŷ������� ȯ���� ��� ��ȯ�ŷ� ���� �ʼ��Է� �Դϴ�. <ForeignExchange> ���������� Ȯ�� �Ͻʽÿ�.");
			}
		}
	}
}
