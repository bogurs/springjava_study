package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;

/**
 * STR에서 지점 코드(brancdCd)가 02/04/06이 아닐 경우 지점 사무실 코드(branchOfficeCd)나 지점 우편
 * 번호(brancdZipCd)가 비면 안되는데 이를 검사한다. *
 * 
 * @param str
 *            보고 기관이 발행한 STR XML
 * @exception StrValidationException
 *                검사 결과 지점 코드(brancdCd)가 02/04/06이 아닐 경 지점 사무실
 *                코드(branchOfficeCd)나 지점 우편 번호(branchZipCd)가 비었을 경우
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
							"(STRValidation) 지점정보Invalid : 지점 정보(BranchOffice)가 누락되었습니다.");

				branchOfficeCd = tx.getBranchOffice().getCode();
				branchZipCd = tx.getBranchOffice().getZipCode();
				if (null == branchOfficeCd || "".equals(branchOfficeCd)) {
					throw new StrValidationException(
							"(STRValidation) 지점정보Invalid : 지점 코드(BranchCode) 값이 누락되었습니다.");
				}
				if (null == branchZipCd || "".equals(branchZipCd)) {
					throw new StrValidationException(
							"(STRValidation) 지점정보Invalid : 지점 우편번호(BranchZipCode) 값이 누락되었습니다.");
				}
			}
		}
	}
}
