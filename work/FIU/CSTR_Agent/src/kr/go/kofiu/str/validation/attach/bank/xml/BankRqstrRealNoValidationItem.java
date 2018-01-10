package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.addiStrBank.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� �ܱ��νǸ��ȣ ���� ����ȭ ���� ����
 * @param bankDoc
 * 				÷�ε� ���� xml
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
			// �Ƿ��νǸ��ȣ�� �Է½� �ʼ�
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
						"(÷������Validation) ����XML Invalid : �Ƿ��νǸ��ȣ �Է½� �Ƿ��θ�, �Ƿ��νǸ��и�, �Ƿ��νǸ����ڵ�, �Ƿ��α����� �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
