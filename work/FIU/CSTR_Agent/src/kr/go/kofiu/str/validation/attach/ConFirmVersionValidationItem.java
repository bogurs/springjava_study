package kr.go.kofiu.str.validation.attach;

import jxl.Sheet;
/**
 *  ÷������ ���� �������� ��� �� ������ ����ȭ ���� ����
 *  @param sh
 * 			÷�ε� ���� Sheet
 *	@throws AttachmentValidationException
 */
public class ConFirmVersionValidationItem {

	public String validate(Sheet sh) {
		// TODO Auto-generated method stub
		String versionValue = null;
		if(sh.getCell(0,0).getContents() != null) {
			if(! (sh.getCell(0,0).getContents().trim()).equals("����")) {
				versionValue = "E";
				//throw new AttachmentValidationException("(÷������ValidationError) : Excel ������ �����ϴ�.");
			}else if(sh.getCell(1,0).getContents() == null ||
			   "".equals(sh.getCell(1,0).getContents().trim())) {
				versionValue = "E";
				//throw new AttachmentValidationException("(÷������ValidationError) : Excel �������� �����ϴ�.");
			}else if(sh.getCell(1,0).getContents().trim().length() > 15) {
				versionValue = "E";
				//throw new AttachmentValidationException("(÷������ValidationError) : Excel ������ �ڸ����� Ȯ�� �Ͻʽÿ�.(MAX:15)");
			}
		}		
		return versionValue;
	}
}
