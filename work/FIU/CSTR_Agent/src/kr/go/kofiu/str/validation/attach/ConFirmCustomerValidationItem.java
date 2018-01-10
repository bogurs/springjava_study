package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * STR ÷������ ���� ������ ��� ����ȭ ���� ����
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmCustomerValidationItem implements XlsValidationItem {
	
	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		//����,���Ǳ���
		String gp = sh.getCell(1,0).getContents().trim().substring(0, 1);
		
		//���Ÿ��Ʋ����
		int rowPos = 0;
		int colCnt = 0;

		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();
		
		// ������
		headerMap.put("1", sh.getCell(0, 1).getContents().trim());
		if (!headerMap.containsValue("������")) {
			throw new AttachmentValidationException("(÷������ValidationError) : �������� �����ϴ�.");
		} else {
			headerListMap.put(++headerListIdx + "", "������(�����)��");
			headerListMap.put(++headerListIdx + "", "�Ǹ��ȣ ����");
			headerListMap.put(++headerListIdx + "", "������(�����)�Ǹ��ȣ");
			headerListMap.put(++headerListIdx + "", "������ ����");
			headerListMap.put(++headerListIdx + "", "�Ǹ��ȣ �����ڵ�");
			if ("S".equals(gp)) {
				headerListMap.put(++headerListIdx + "", "����");
			}

			rowPos = 2;
			colCnt = "S".equals(gp) ? 6 : 5;
			for (int i = 0; i < colCnt; i++) {
				if (!headerListMap.containsValue(sh.getCell(i, rowPos)
						.getContents().trim())) {
					throw new AttachmentValidationException("(÷������ValidationError) : "+headerListMap
							.get("" + i)
							+ " ������ �����ϴ�.");
				} 				
			}
			headerListMap.clear();
			headerListIdx = -1;
		}

	}

}
