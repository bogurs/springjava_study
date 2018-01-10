package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� �Աݱݾ� ����ȭ ���� ����
 * @param bankDoc
 * 				÷�ε� ���� xml
 * @throw AttachmentValidationException
 */
public class BankDpstAmtValidationItem implements BankXmlValidationItem {

	public void validate(AddiStrBankDocument bankDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = bankDoc.getAddiStrBank()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;

		for (int i = 0; i < transDetailsCnt; i++) {
			// �Ա��� ��� �ʼ�
			if (("01".equals(transDetails[i].getTrnsKndNm().getCode()
					.toString())
					|| "04".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "08".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "12".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString())
					|| "14".equals(transDetails[i].getTrnsKndNm().getCode()
							.toString()) || "20".equals(transDetails[i]
					.getTrnsKndNm().getCode().toString()))
					&& transDetails[i].getDpstAmt() == null) {
				throw new AttachmentValidationException(
						"(÷������Validation) ����XML Invalid : �Ա��� ��� �Աݱݾ���  �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
