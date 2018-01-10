package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Account;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Account.BranchOffice;
/**
 * STR �������� ����ȭ ���� ����
 * @param str
 *            ���� ����� ������ STR XML
 * @throw StrValidationException
 */
public class StrAccountValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		// List<Transaction> tlists = str.getSTR().getDetail().getTransactionList();
		// List<Account> acclists = str.getSTR().getDetail().getAccountList();
		Transaction[] tlists = str.getSTR().getDetail().getTransactionArray();
		Account[] acclists = str.getSTR().getDetail().getAccountArray();
		String orgCd1 = null;
		String orgCd2 = null;
		BranchOffice bo = null;
		String rDate = null;

		for (Transaction tx : tlists) {
			
			orgCd1 = tx.getOrgName().getCode();
			/*if(orgCd1.equals(null) || orgCd1.equals("")){
				throw new StrValidationException(
						"(STRValidation) ����Invalid : �����<OrgName>�� �ڵ尪�� �������� �ʰų� �����Դϴ�.");
			}*/

			for (Account acc : acclists) {
				bo = acc.getBranchOffice();
				rDate = acc.getRegDate();
				orgCd2 = acc.getOrgName().getCode();

				if (orgCd1.equals(orgCd2)) {
					if (bo == null || "".equals(bo.getStringValue()))
						throw new StrValidationException(
								"(STRValidation) ��������Invalid : ������ ��� ��������<BranchOffice>�� �ʼ��� �Դϴ�.");
					if (rDate == null || "".equals(rDate))
						throw new StrValidationException(
								"(STRValidation) ��������Invalid : ������ ��� ���°����� ����<RegDate>�� �ʼ��� �Դϴ�.");
				}
			}
		}
	}
}