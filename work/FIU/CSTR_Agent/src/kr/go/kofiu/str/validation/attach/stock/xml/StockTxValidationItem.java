package kr.go.kofiu.str.validation.attach.stock.xml;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.addiStrStock.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR ÷������ ���� xml���� �ŷ����� ����ȭ ���� ����
 * @param stockDoc
 * 			÷�ε� ���� xml
 * @throw AttachmentValidationException
 */
public class StockTxValidationItem implements StockXmlValidationItem {

	public void validate(AddiStrStockDocument stockDoc)
			throws AttachmentValidationException {
		// TODO Auto-generated method stub

		TransDetail[] transDetails = stockDoc.getAddiStrStock()
				.getTransDetailArray();
		int transDetailsCnt = transDetails == null ? 0 : transDetails.length;
		for (int i = 0; i < transDetailsCnt; i++) {
			// ���ǰŷ��� �ʼ�
			if (("10".equals(transDetails[i].getTrnsKndNm().getCode()
					.toString()) || "11".equals(transDetails[i].getTrnsKndNm()
					.getCode().toString()))
					&& (transDetails[i].getItmCd() == null
							|| "".equals(transDetails[i].getItmCd())
							|| transDetails[i].getItmNm() == null
							|| "".equals(transDetails[i].getItmNm())
							|| transDetails[i].getUtpr() == null
							|| transDetails[i].getUtpr().longValue() < 0 || transDetails[i]
							.getTrnsQnty() < 0)) {
				throw new AttachmentValidationException(
						"(÷������Validation) ����XML Invalid : ���ǰŷ��� �Ÿ��ڵ�,�Ÿ������,�ܰ�,�ŷ������� �ʼ� �Է��Դϴ�.");
			}
		}
	}
}
