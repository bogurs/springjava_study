package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * STR 첨부파일 Excel 계좌정보 데이터 정형화 검증 수행.
 * @param sh
 * 			첨부된 엑셀 Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmAccountDataValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		ConfirmAttachExcel confirm = new ConfirmAttachExcel();
		//은행,증권구분
		String gp = sh.getCell(1,0).getContents().trim().substring(0, 1);
		
		//헤더타이틀지정
		int rowPos = 0;
		int colCnt = 0;

		String tmpValue = null;
		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();
		
		//계좌정보
		headerMap.put("4", sh.getCell(0,4).getContents().trim());
		if(! headerMap.containsValue("계좌정보")) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : 계좌정보가 없습니다.");
		}else {
			headerListMap.put(++headerListIdx +"", "계좌개설 금융기관");
			headerListMap.put(++headerListIdx +"", "계좌번호");
			if("S".equals(gp)) {
				headerListMap.put(++headerListIdx +"", "계좌구분");
			}else {
				headerListMap.put(++headerListIdx +"", "계좌종류(상품)");					
			}
			headerListMap.put(++headerListIdx +"", "계좌개설일자");
			headerListMap.put(++headerListIdx +"", "계좌개설점명");
			headerListMap.put(++headerListIdx +"", "계좌관리점명");
			headerListMap.put(++headerListIdx +"", "계좌잔액");
			headerListMap.put(++headerListIdx +"", "개설금융기관코드");
			headerListMap.put(++headerListIdx +"", "계좌종류코드");
			headerListMap.put(++headerListIdx +"", "계좌개설점코드");
			headerListMap.put(++headerListIdx +"", "계좌관리점코드");

			rowPos = 5;
			colCnt = 11;
			for(int i = 0; i < colCnt; i++) {
				// 데이터  정형화 체크
				if("B".equals(gp) &&
						 ! "계좌종류(상품)".equals(headerListMap.get(""+ i)) &&
						 ! "계좌잔액".equals(headerListMap.get(""+ i)) &&
						 ! "계좌종류코드".equals(headerListMap.get(""+ i)) &&
						 (sh.getCell(i, rowPos+1).getContents() == null ||
						  "".equals(sh.getCell(i, rowPos+1).getContents().trim()))) {
					throw new AttachmentValidationException("(첨부파일ValidationError) : "+headerListMap.get(""+ i) +" 정보는 필수 입력 입니다.");
				}else if("S".equals(gp) &&
						 ! "계좌구분".equals(headerListMap.get(""+ i)) &&
						 ! "계좌잔액".equals(headerListMap.get(""+ i)) &&
						 ! "계좌종류코드".equals(headerListMap.get(""+ i)) &&
						 (sh.getCell(i, rowPos+1).getContents() == null ||
						  "".equals(sh.getCell(i, rowPos+1).getContents().trim()))) {
					throw new AttachmentValidationException("(첨부파일ValidationError) : "+headerListMap.get(""+ i) +" 정보는 필수 입력 입니다.");
				}
				
								if("계좌종류코드".equals(headerListMap.get(""+ i)) &&
				   ! (sh.getCell(i, rowPos+1).getContents() == null ||
					"".equals(sh.getCell(i, rowPos+1).getContents().trim()))) {
					confirm.confirmAccountKndCd(sh.getCell(i, rowPos+1).getContents().trim());
				}
				tmpValue = sh.getCell(i, rowPos+1).getContents();
				tmpValue = confirm.confirmNumberValue(""+ headerListMap.get(""+ i), tmpValue);
				confirm.confirmLengthOfAccountInfo(""+ headerListMap.get(""+ i), tmpValue == null ? 0 : tmpValue.trim().length());
			}
			headerListMap.clear();
			headerListIdx = -1;
		}

	}

}
