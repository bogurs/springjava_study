package kr.go.kofiu.str.validation.attach.stock.xml;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.addiStrStock.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� �ŷ��󼼳��� ����ȭ ���� ����
 * @param stockDoc
 * 			÷�ε� ���� xml
 * @throw AttachmentValidationException
 */
public class StockPayDpstTxValidationItem implements StockXmlValidationItem {

	public void validate(AddiStrStockDocument stockDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = stockDoc.getAddiStrStock()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;
		for (int i = 0; i < transDetailsCnt; i++) {
			// �Ա�,���� �ŷ��� �ʼ�
			if (("01".equals(transDetails[i].getTrnsKndNm().getCode()
					.toString()) || "22".equals(transDetails[i].getTrnsKndNm()
					.getCode().toString()))
					&& (transDetails[i].getPrtyFnncFcltyNm() == null
							|| "".equals(transDetails[i].getPrtyFnncFcltyNm()
									.getStringValue())
							|| transDetails[i].getPrtyAcctNo() == null
							|| "".equals(transDetails[i].getPrtyAcctNo())
							|| transDetails[i].getPrtyAcctHoldrNm() == null || ""
							.equals(transDetails[i].getPrtyAcctHoldrNm()))) {
				throw new AttachmentValidationException(
						"(÷������Validation) ����XML Invalid : �Ա�,���� �ŷ��� �����������,���/��ü���¹�ȣ,���/��ü��,����������ڵ� �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
