package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR 첨부파일 은행 xml파일 외환거래금액 정형화 검증 수행
 * @param bankDoc
 * 				첨부된 은행 xml
 * @throw AttachmentValidationException
 *
 */
public class BankForeignTxValidationItem implements BankXmlValidationItem {

	public void validate(AddiStrBankDocument bankDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = bankDoc.getAddiStrBank()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;

		for (int i = 0; i < transDetailsCnt; i++) {
			// 외환거래일경우 필수
			if (!"KRW".equals(transDetails[i].getCurrCd())
					&& (transDetails[i].getFexchTrnsAmt() == null)) {
				throw new AttachmentValidationException(
						"(첨부파일Validation) 은행XML Invalid : 통화코드가 'KRW'가 아닐때 외환거래금액은 필수 입력입니다.");
			}
		}
	}
}
