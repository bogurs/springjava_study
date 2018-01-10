package kr.go.kofiu.str.validation.attach.stock.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;

/**
 * STR 첨부파일 증권 엑셀파일 거래상세내역 헤더 정형화 검증 수행
 * 
 * @param sh
 *            첨부된 엑셀 Sheet
 * @throw AttachmentValidationException
 * 
 */
public class StockTransDetailValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		int headerListIdx = -1;
		int customerInfoRowPos = 0;
		int customerInfoColCnt = 0;

		Map<Object, String> headerListMap = new HashMap<Object, String>();
		headerListMap.put(++headerListIdx + "", "거래일련번호");
		headerListMap.put(++headerListIdx + "", "거래순번");
		headerListMap.put(++headerListIdx + "", "거래발생일시");
		headerListMap.put(++headerListIdx + "", "거래종류명");
		headerListMap.put(++headerListIdx + "", "거래수단명");
		headerListMap.put(++headerListIdx + "", "거래채널명");
		headerListMap.put(++headerListIdx + "", "적요");
		headerListMap.put(++headerListIdx + "", "매매종목코드");
		headerListMap.put(++headerListIdx + "", "매매종목명");
		headerListMap.put(++headerListIdx + "", "단가");
		headerListMap.put(++headerListIdx + "", "거래수량");
		headerListMap.put(++headerListIdx + "", "거래금액");
		headerListMap.put(++headerListIdx + "", "정산금액");
		headerListMap.put(++headerListIdx + "", "수표금액");
		headerListMap.put(++headerListIdx + "", "유가증권명");
		headerListMap.put(++headerListIdx + "", "유가증권시작번호");
		headerListMap.put(++headerListIdx + "", "유가증권종료번호");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관명");
		headerListMap.put(++headerListIdx + "", "현금지급영업점명");
		headerListMap.put(++headerListIdx + "", "유가잔고");
		headerListMap.put(++headerListIdx + "", "예수금잔고");
		headerListMap.put(++headerListIdx + "", "미수금잔고");
		headerListMap.put(++headerListIdx + "", "융자금/대주금");
		headerListMap.put(++headerListIdx + "", "취급점명");
		headerListMap.put(++headerListIdx + "", "상대금융기관명");
		headerListMap.put(++headerListIdx + "", "상대/대체계좌번호");
		headerListMap.put(++headerListIdx + "", "상대/대체명");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명번호");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분");
		headerListMap.put(++headerListIdx + "", "상대계좌주국적");
		headerListMap.put(++headerListIdx + "", "거래종류코드");
		headerListMap.put(++headerListIdx + "", "거래수단코드");
		headerListMap.put(++headerListIdx + "", "거래채널코드");
		headerListMap.put(++headerListIdx + "", "유가증권코드");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관코드");
		headerListMap.put(++headerListIdx + "", "현금지급영업점코드");
		headerListMap.put(++headerListIdx + "", "상대금융기관코드");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분코드");
		headerListMap.put(++headerListIdx + "", "취급점코드");
		headerListMap.put(++headerListIdx + "", "정정구분코드");

		customerInfoRowPos = 11;
		customerInfoColCnt = 40;
		// check header
		for (int i = 0; i < customerInfoColCnt; i++) {
			if (!headerListMap.containsValue(sh.getCell(i, customerInfoRowPos)
					.getContents().trim())) {
				throw new AttachmentValidationException(
						"(첨부파일Validation) 증권XLS Invalid : "
								+ headerListMap.get("" + i) + " 헤더 정보가 없습니다.");
			}
		}
	}
}
