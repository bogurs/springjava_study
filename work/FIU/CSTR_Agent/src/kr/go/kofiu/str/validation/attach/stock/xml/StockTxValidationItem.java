package kr.go.kofiu.str.validation.attach.stock.xml;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.addiStrStock.TransDetail;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
/**
 * STR 첨부파일 증권 xml파일 거래정보 정형화 검증 수행
 * @param stockDoc
 * 			첨부된 증권 xml
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
			// 증권거래시 필수
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
						"(첨부파일Validation) 증권XML Invalid : 증권거래시 매매코드,매매종목명,단가,거래수량은 필수 입력입니다.");
			}
		}
	}
}
