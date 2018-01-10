package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/** 
 * STR ÷������ ���� �ŷ������� ��� ����ȭ ���� ����
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmTransactionValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub

		Map<String, String> headerMap = new HashMap<String, String>();
		
		//�ŷ���
		headerMap.put("10", sh.getCell(0,10).getContents().trim());
		if(! headerMap.containsValue("�ŷ���")) {
			throw new AttachmentValidationException("(÷������ValidationError) : �ŷ��� ������ �����ϴ�.");
		}
	}
}
