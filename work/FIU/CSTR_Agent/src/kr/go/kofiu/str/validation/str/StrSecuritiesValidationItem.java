package kr.go.kofiu.str.validation.str;

import java.util.List;


import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.Securities;
import kr.go.kofiu.str.validation.RealNumValidator;
/**
 * STR ���� �������� ����ȭ ���� ����.
 * @param str
 *            ���� ����� ������ STR XML
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
							"(STRValidation) ��������Invalid : �ŷ������� ��ǥ,ä��,��Ÿ���������̰ų� �ŷ������� ��ǥ����,��ǥ����,����,������ ��� �������� ������ �ʼ��Է� �Դϴ�. <Securities> ���������� Ȯ�� �Ͻʽÿ�.");
			}
			for (Securities scrt : sLists) {
				realNum = scrt.getRealNumber().getCode().toString();
				
				if ((null == scrt.getOrgName() || "".equals(scrt.getOrgName().getStringValue()))
						&& (null == scrt.getIssueOrgName() || "".equals(scrt.getIssueOrgName().getStringValue()))) {
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : ��������� �������� �� �ϳ��� �ʼ� �����ؾ��մϴ�.");
				}
				
				if (("01".equals(realNum) || "02".equals(realNum))
						&& !rNumValidator.validZuminNumber(scrt.getRealNumber()
								.getStringValue())) {
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�.");
				}
				if ("03".equals(realNum)
						&& !rNumValidator.validBizNumber(scrt.getRealNumber()
								.getStringValue())) {
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�.");
				}
			}
		}
	}
}
