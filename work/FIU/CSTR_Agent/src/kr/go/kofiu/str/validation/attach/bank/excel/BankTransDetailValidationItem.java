package kr.go.kofiu.str.validation.attach.bank.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;
/**
 * STR 첨부파일 은행 엑셀파일 거래상세정보 헤더 정형화 검증 수행.
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class BankTransDetailValidationItem implements XlsValidationItem {
	
	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		int headerListIdx = -1;
		int rowPos = 0;
		int colCnt = 0;

		Map<Object, String> headerListMap = new HashMap<Object, String>();
		headerListMap.put(++headerListIdx + "", "거래일련번호");
		headerListMap.put(++headerListIdx + "", "거래순번");
		headerListMap.put(++headerListIdx + "", "거래발생일시");
		headerListMap.put(++headerListIdx + "", "거래종류명");
		headerListMap.put(++headerListIdx + "", "거래수단명");
		headerListMap.put(++headerListIdx + "", "거래채널명");
		headerListMap.put(++headerListIdx + "", "적요");
		headerListMap.put(++headerListIdx + "", "통화구분코드");
		headerListMap.put(++headerListIdx + "", "외환거래금액");
		headerListMap.put(++headerListIdx + "", "지급금액");
		headerListMap.put(++headerListIdx + "", "입금금액");
		headerListMap.put(++headerListIdx + "", "잔액");
		headerListMap.put(++headerListIdx + "", "취급금융기관명");
		headerListMap.put(++headerListIdx + "", "취급점명");
		headerListMap.put(++headerListIdx + "", "유가증권명");
		headerListMap.put(++headerListIdx + "", "유가증권시작번호");
		headerListMap.put(++headerListIdx + "", "유가증권종료번호");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관명");
		headerListMap.put(++headerListIdx + "", "현금지급영업점명");
		headerListMap.put(++headerListIdx + "", "의뢰인명");
		headerListMap.put(++headerListIdx + "", "의뢰인실명구분");
		headerListMap.put(++headerListIdx + "", "의뢰인실명번호");
		headerListMap.put(++headerListIdx + "", "의뢰인국적");
		headerListMap.put(++headerListIdx + "", "상대금융기관명");
		headerListMap.put(++headerListIdx + "", "상대계좌번호");
		headerListMap.put(++headerListIdx + "", "상대계좌주명");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명번호");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분");
		headerListMap.put(++headerListIdx + "", "상대계좌주국적");
		headerListMap.put(++headerListIdx + "", "거래종류코드");
		headerListMap.put(++headerListIdx + "", "거래수단코드");
		headerListMap.put(++headerListIdx + "", "거래채널코드");
		headerListMap.put(++headerListIdx + "", "취급금융기관코드");
		headerListMap.put(++headerListIdx + "", "취급점코드");
		headerListMap.put(++headerListIdx + "", "유가증권코드");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관코드");
		headerListMap.put(++headerListIdx + "", "현금지급영업점코드");
		headerListMap.put(++headerListIdx + "", "의뢰인실명구분코드");
		headerListMap.put(++headerListIdx + "", "상대금융기관코드");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분코드");
		headerListMap.put(++headerListIdx + "", "정정구분코드");

		rowPos = 11;
		colCnt = 41;
		// check header
		for (int i = 0; i < colCnt; i++) {
			if (!headerListMap.containsValue(sh.getCell(i, rowPos)
					.getContents().trim())) {
				throw new AttachmentValidationException("(첨부파일Validation) 은행XLS Invalid : "+headerListMap.get(""
						+ i)
						+ " 헤더 정보가 없습니다.");
			}
		}	
	}
}
