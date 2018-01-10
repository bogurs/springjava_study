package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.Stock;
/**
 * STR ���� ���� ����ȭ ���� ����.
 * �ŷ������� �ż�,�ŵ�,�԰�,���,��ü�԰�,��ü��� �� ��� ����
 * @param str
 *            ���� ����� ������ STR XML
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
							"(STRValidation) ����invalid : �ŷ������� �ż�,�ŵ�,�԰�,���,��ü�԰�,��ü���(�ŷ������ڵ� : '10'~'15') �� ��� ����������  �ʼ��Է� �Դϴ�. <Stock> ���������� Ȯ�� �Ͻʽÿ�.");
			}
			// ���ǰŷ� ��� ������� �� ������ ���� ����
			for (Stock stk : stlists) {					
				if ("14".equals(methodCd) || "15".equals(methodCd)) {
					if (null == stk.getOrgName()
							|| "".equals(stk.getOrgName().getStringValue()))
						throw new StrValidationException(
								"(STRValidation) ���Ǵ��������invalid : �ŷ������� ��ü�԰�,��ü��� �� ��� �������� �ʼ��Է� �Դϴ�. <OrgName> ���������� Ȯ�� �Ͻʽÿ�.");
					if (null == stk.getBranchOffice()
							|| "".equals(stk.getBranchOffice().getStringValue()))
						throw new StrValidationException(
								"(STRValidation) ���Ǵ��������invalid : �ŷ������� ��ü�԰�,��ü��� �� ��� ���������� �ʼ��Է� �Դϴ�. <BranchOffice> ���������� Ȯ�� �Ͻʽÿ�.");
				}
			}			
		}
	}
}
