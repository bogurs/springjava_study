package kr.go.kofiu.str.validation.attach;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * STR ÷������ ���� Version �������� ÷������ Ÿ��(���� : 'B', ���� : 'S') ������ ��ȸ�Ͽ� orgType ������
 * ��ȯ�Ͽ� �ش�.
 * 
 * @param inStream
 *            ÷������ ���� ��Ʈ�� ������
 * @param orgType
 *            ÷������ Ÿ��( ���� : 'B', ���� : 'S' )
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

		// ÷������ ���� ���� ���� üũ
		for (cnt = 0; cnt < cntSh; cnt++) {
			versionValue = versionCk.validate(sh[cnt]);
		}
		// ÷������ ���� ��� Ÿ�� ������ �����´�.
		if (!"E".equals(versionValue))
			orgType = sh[0].getCell(1, 0).getContents().trim().substring(0, 1);

		return orgType;

	}

}
