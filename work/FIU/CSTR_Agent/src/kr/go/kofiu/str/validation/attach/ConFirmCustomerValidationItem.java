package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * STR 첨부파일 엑셀 고객정보 헤더 정형화 검증 수행
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmCustomerValidationItem implements XlsValidationItem {
	
	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		//은행,증권구분
		String gp = sh.getCell(1,0).getContents().trim().substring(0, 1);
		
		//헤더타이틀지정
		int rowPos = 0;
		int colCnt = 0;

		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();
		
		// 고객정보
		headerMap.put("1", sh.getCell(0, 1).getContents().trim());
		if (!headerMap.containsValue("고객정보")) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : 고객정보가 없습니다.");
		} else {
			headerListMap.put(++headerListIdx + "", "계좌주(사업자)명");
			headerListMap.put(++headerListIdx + "", "실명번호 구분");
			headerListMap.put(++headerListIdx + "", "계좌주(사업자)실명번호");
			headerListMap.put(++headerListIdx + "", "계좌주 국적");
			headerListMap.put(++headerListIdx + "", "실명번호 구분코드");
			if ("S".equals(gp)) {
				headerListMap.put(++headerListIdx + "", "직업");
			}

			rowPos = 2;
			colCnt = "S".equals(gp) ? 6 : 5;
			for (int i = 0; i < colCnt; i++) {
				if (!headerListMap.containsValue(sh.getCell(i, rowPos)
						.getContents().trim())) {
					throw new AttachmentValidationException("(첨부파일ValidationError) : "+headerListMap
							.get("" + i)
							+ " 정보가 없습니다.");
				} 				
			}
			headerListMap.clear();
			headerListIdx = -1;
		}

	}

}
