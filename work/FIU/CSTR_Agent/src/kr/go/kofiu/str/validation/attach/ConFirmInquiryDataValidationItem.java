package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * 첨부파일 엑셀 조회정보 데이터 정형화 검증 수행
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmInquiryDataValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		ConfirmAttachExcel confirm = new ConfirmAttachExcel();

		// 헤더타이틀지정
		int rowPos = 0;
		int colCnt = 0;

		String tmpValue = null;
		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();

		// 조회정보
		headerMap.put("7", sh.getCell(0, 7).getContents().trim());
		if (!headerMap.containsValue("조회정보")) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : 조회정보가 없습니다.");
		} else {
			headerListMap.put(++headerListIdx + "", "조회일시");
			headerListMap.put(++headerListIdx + "", "조회점명");
			headerListMap.put(++headerListIdx + "", "조회시작일");
			headerListMap.put(++headerListIdx + "", "조회종료일");
			headerListMap.put(++headerListIdx + "", "조회점코드");

			rowPos = 8;
			colCnt = 5;
			// 데이터  정형화 체크
			for (int i = 0; i < colCnt; i++) {
				if (!"조회점명".equals(headerListMap.get("" + i))
						&& !"조회점코드".equals(headerListMap.get("" + i))
						&& (sh.getCell(i, rowPos + 1).getContents() == null || ""
								.equals(sh.getCell(i, rowPos + 1).getContents()
										.trim()))) {
					throw new AttachmentValidationException("(첨부파일ValidationError) : "+headerListMap
							.get("" + i)
							+ " 정보는 필수 입력 입니다.");
				}
				
				
				tmpValue = sh.getCell(i, rowPos + 1).getContents();
				confirm.confirmLengthOfInquiryInfo(""
						+ headerListMap.get("" + i), tmpValue == null ? 0
						: tmpValue.trim().length());
			}
			headerListMap.clear();
			headerListIdx = -1;
		}

	}

}
