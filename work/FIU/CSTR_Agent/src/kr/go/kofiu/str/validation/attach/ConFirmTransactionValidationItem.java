package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/** 
 * STR 첨부파일 엑셀 거래상세정보 헤더 정형화 검증 수행
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmTransactionValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub

		Map<String, String> headerMap = new HashMap<String, String>();
		
		//거래상세
		headerMap.put("10", sh.getCell(0,10).getContents().trim());
		if(! headerMap.containsValue("거래상세")) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : 거래상세 정보가 없습니다.");
		}
	}
}
