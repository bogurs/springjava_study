package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * 첨부파일 엑셀 조회정보 헤더 정형화 검증 수행
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmInquiryValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		// 헤더타이틀지정
		int rowPos = 0;
		int colCnt = 0;

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
