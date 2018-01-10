package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� ���ޱݾ� ����ȭ ���� ����
 * @param bankDoc
 * 				÷�ε� ���� xml
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
			// ������ ��� �ʼ�
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
						"(÷������Validation) ����XML Invalid : ������ ��� ���ޱݾ��� �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
