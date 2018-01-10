package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Account;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Account.BranchOffice;
/**
 * STR 계좌정보 정형화 검증 수행
 * @param str
 *            보고 기관이 발행한 STR XML
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
						"(STRValidation) 정보Invalid : 기관명<OrgName>의 코드값이 존재하지 않거나 공백입니다.");
			}*/

			for (Account acc : acclists) {
				bo = acc.getBranchOffice();
				rDate = acc.getRegDate();
				orgCd2 = acc.getOrgName().getCode();

				if (orgCd1.equals(orgCd2)) {
					if (bo == null || "".equals(bo.getStringValue()))
						throw new StrValidationException(
								"(STRValidation) 계좌정보Invalid : 자행일 경우 지점정보<BranchOffice>는 필수값 입니다.");
					if (rDate == null || "".equals(rDate))
						throw new StrValidationException(
								"(STRValidation) 계좌정보Invalid : 자행일 경우 계좌개설일 정보<RegDate>는 필수값 입니다.");
				}
			}
		}
	}
}