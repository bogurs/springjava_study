package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR 첨부파일 은행 xml파일 외국인실명번호 정보 정형화 검증 수행
 * @param bankDoc
 * 				첨부된 은행 xml
 * @throw AttachmentValidationException
 */
public class BankRqstrRealNoValidationItem implements BankXmlValidationItem {

	public void validate(AddiStrBankDocument bankDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = bankDoc.getAddiStrBank()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;

		for (int i = 0; i < transDetailsCnt; i++) {
			// 의뢰인실명번호가 입력시 필수
			if (transDetails[i].getRqstrRealNo() != null
					&& (transDetails[i].getRqstrNm() == null
							|| "".equals(transDetails[i].getRqstrNm())
							|| transDetails[i].getRqstrRealNoClsfNm() == null
							|| transDetails[i].getRqstrRealNoClsfNm().getCode()
									.toString() == null
							|| "".equals(transDetails[i].getRqstrRealNoClsfNm()
									.getStringValue())
							|| transDetails[i].getRqstrNtnltyCd() == null || ""
							.equals(transDetails[i].getRqstrNtnltyCd()))) {
				throw new AttachmentValidationException(
						"(첨부파일Validation) 은행XML Invalid : 의뢰인실명번호 입력시 의뢰인명, 의뢰인실명구분명, 의뢰인실명구분코드, 의뢰인국적은 필수 입력입니다.");
			}
		}
	}
}
