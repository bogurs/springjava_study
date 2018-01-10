package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;

/**
 * STR���� ���� �ڵ�(brancdCd)�� 02/04/06�� �ƴ� ��� ���� �繫�� �ڵ�(branchOfficeCd)�� ���� ����
 * ��ȣ(brancdZipCd)�� ��� �ȵǴµ� �̸� �˻��Ѵ�. *
 * 
 * @param str
 *            ���� ����� ������ STR XML
 * @exception StrValidationException
 *                �˻� ��� ���� �ڵ�(brancdCd)�� 02/04/06�� �ƴ� �� ���� �繫��
 *                �ڵ�(branchOfficeCd)�� ���� ���� ��ȣ(branchZipCd)�� ����� ���
 */
public class StrBranchValidationItem implements StrValidatable {
	
	private boolean checkBranchCd(String num) {

		if ("02".equals(num) || "04".equals(num) || "06".equals(num)) {
			return false;
		}
		return true;
	}

	public void validate(STRDocument str) throws StrValidationException {
		String brnchCd = null;
		String branchOfficeCd = null;
		String branchZipCd = null;

		//  List<Transaction> lists = str.getSTR().getDetail().getTransactionList();
		Transaction[] lists = str.getSTR().getDetail().getTransactionArray();

		for (Transaction tx : lists) {
			brnchCd = tx.getChannel().getCode().toString();

			if (this.checkBranchCd(brnchCd)) {
				if (null == tx.getBranchOffice()
						|| "".equals(tx.getBranchOffice().getStringValue()))
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : ���� ����(BranchOffice)�� �����Ǿ����ϴ�.");

				branchOfficeCd = tx.getBranchOffice().getCode();
				branchZipCd = tx.getBranchOffice().getZipCode();
				if (null == branchOfficeCd || "".equals(branchOfficeCd)) {
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : ���� �ڵ�(BranchCode) ���� �����Ǿ����ϴ�.");
				}
				if (null == branchZipCd || "".equals(branchZipCd)) {
					throw new StrValidationException(
							"(STRValidation) ��������Invalid : ���� �����ȣ(BranchZipCode) ���� �����Ǿ����ϴ�.");
				}
			}
		}
	}
}
