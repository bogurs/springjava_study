package kr.go.kofiu.str.validation.attach;

import jxl.Sheet;
/**
 *  첨부파일 엑셀 버전정보 헤더 및 데이터 정형화 검증 수행
 *  @param sh
 * 			첨부된 엑셀 Sheet
 *	@throws AttachmentValidationException
 */
public class ConFirmVersionValidationItem {

	public String validate(Sheet sh) {
		// TODO Auto-generated method stub
		String versionValue = null;
		if(sh.getCell(0,0).getContents() != null) {
			if(! (sh.getCell(0,0).getContents().trim()).equals("버전")) {
				versionValue = "E";
				//throw new AttachmentValidationException("(첨부파일ValidationError) : Excel 버전이 없습니다.");
			}else if(sh.getCell(1,0).getContents() == null ||
			   "".equals(sh.getCell(1,0).getContents().trim())) {
				versionValue = "E";
				//throw new AttachmentValidationException("(첨부파일ValidationError) : Excel 버전값이 없습니다.");
			}else if(sh.getCell(1,0).getContents().trim().length() > 15) {
				versionValue = "E";
				//throw new AttachmentValidationException("(첨부파일ValidationError) : Excel 버전값 자리수를 확인 하십시오.(MAX:15)");
			}
		}		
		return versionValue;
	}
}
