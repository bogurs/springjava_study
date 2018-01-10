package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR 첨부파일 은행 xml파일 지급금액 정형화 검증 수행
 * @param bankDoc
 * 				첨부된 은행 xml
 * @throw AttachmentValidationException
 */
public class BankPayAmtValidationItem implements BankXmlValidationItem {

	public void validate(AddiStrBankDocument bankDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = bankDoc.getAddiStrBank()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;

		for (int i = 0; i < transDetailsCnt; i++) {
			// 지급인 경우 필수
			if (("02".equals(transDetails[i].getTrnsKndNm().getCode()
					.toString())
					|| "05".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "09".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "13".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "15".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString()) || "21".equals(transDetails[i]
					.getTrnsKndNm().getCode().toString()))
					&& transDetails[i].getPayAmt() == null) {
				throw new AttachmentValidationException(
						"(첨부파일Validation) 은행XML Invalid : 지급인 경우 지급금액은 필수 입력입니다.");
			}
		}
	}
}
