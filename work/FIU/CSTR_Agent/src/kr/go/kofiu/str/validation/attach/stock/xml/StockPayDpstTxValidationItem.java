package kr.go.kofiu.str.validation.attach.stock.xml;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.addiStrStock.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR 첨부파일 증권 xml파일 거래상세내역 정형화 검증 수행
 * @param stockDoc
 * 			첨부된 증권 xml
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
			// 입금,지급 거래시 필수
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
						"(첨부파일Validation) 증권XML Invalid : 입금,지급 거래시 상대금융기관명,상대/대체계좌번호,상대/대체명,상대금융기관코드 필수 입력입니다.");
			}
		}
	}
}
