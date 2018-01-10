package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� ��ȯ�ŷ��ݾ� ����ȭ ���� ����
 * @param bankDoc
 * 				÷�ε� ���� xml
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
			// ��ȯ�ŷ��ϰ�� �ʼ�
			if (!"KRW".equals(transDetails[i].getCurrCd())
					&& (transDetails[i].getFexchTrnsAmt() == null)) {
				throw new AttachmentValidationException(
						"(÷������Validation) ����XML Invalid : ��ȭ�ڵ尡 'KRW'�� �ƴҶ� ��ȯ�ŷ��ݾ��� �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
