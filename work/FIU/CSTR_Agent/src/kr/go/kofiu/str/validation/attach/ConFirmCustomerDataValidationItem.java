package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * STR 첨부파일 엑셀 고객정보 데이터 정형화 검증 수행
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmCustomerDataValidationItem implements XlsValidationItem {
	ConfirmAttachExcel confirm = new ConfirmAttachExcel();

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		//은행,증권구분
		String gp = sh.getCell(1,0).getContents().trim().substring(0, 1);
		
		//헤더타이틀지정
		int rowPos = 0;
		int colCnt = 0;

		String tmpValue = null;
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
			// 데이터  정형화 체크
			for (int i = 0; i < colCnt; i++) {
				if (!"계좌주 국적".equals(headerListMap.get("" + i))
						&& (sh.getCell(i, rowPos + 1).getContents() == null || ""
								.equals(sh.getCell(i, rowPos + 1).getContents()
										.trim()))) {
					throw new AttachmentValidationException("(첨부파일ValidationError) : "+headerListMap
							.get("" + i)
							+ " 정보는 필수 입력 입니다.");
				}
				
				
				if ("실명번호 구분코드".equals(headerListMap.get("" + i))) {
					confirm.confirmRealNumberCd(sh.getCell(i,
							rowPos + 1).getContents().trim());
				}
				tmpValue = sh.getCell(i, rowPos + 1).getContents();
				confirm.confirmLengthOfCustomerInfo(""
						+ headerListMap.get("" + i), tmpValue == null ? 0
						: tmpValue.trim().length());
			}
			headerListMap.clear();
			headerListIdx = -1;
		}

	}

}
