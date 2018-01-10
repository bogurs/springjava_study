package kr.go.kofiu.str.validation.attach;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * STR 첨부파일 엑셀 Version 정보에서 첨부파일 타입(은행 : 'B', 증권 : 'S') 정보를 조회하여 orgType 값으로
 * 반환하여 준다.
 * 
 * @param inStream
 *            첨부파일 엑셀 스트림 데이터
 * @param orgType
 *            첨부파일 타입( 은행 : 'B', 증권 : 'S' )
 */
public class ConFirmOrgBankStock {

	public String conFirmBankOrStock(InputStream inStream)
			throws BiffException, IOException, AttachmentValidationException {

		ConFirmVersionValidationItem versionCk = new ConFirmVersionValidationItem();
		String orgType = null;
		String versionValue = null;
		Workbook wb = Workbook.getWorkbook(inStream);
		int cnt;
		int cntSh = wb.getNumberOfSheets();
		Sheet[] sh = wb.getSheets();

		// 첨부파일 엑셀 버전 정보 체크
		for (cnt = 0; cnt < cntSh; cnt++) {
			versionValue = versionCk.validate(sh[cnt]);
		}
		// 첨부파일 엑셀 기관 타입 정보를 가져온다.
		if (!"E".equals(versionValue))
			orgType = sh[0].getCell(1, 0).getContents().trim().substring(0, 1);

		return orgType;

	}

}
