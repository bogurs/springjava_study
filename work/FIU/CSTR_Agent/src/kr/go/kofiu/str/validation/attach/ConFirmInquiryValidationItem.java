package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * ÷������ ���� ��ȸ���� ��� ����ȭ ���� ����
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmInquiryValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		// ���Ÿ��Ʋ����
		int rowPos = 0;
		int colCnt = 0;

		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();

		// ��ȸ����
		headerMap.put("7", sh.getCell(0, 7).getContents().trim());
		if (!headerMap.containsValue("��ȸ����")) {
			throw new AttachmentValidationException("(÷������ValidationError) : ��ȸ������ �����ϴ�.");
		} else {
			headerListMap.put(++headerListIdx + "", "��ȸ�Ͻ�");
			headerListMap.put(++headerListIdx + "", "��ȸ����");
			headerListMap.put(++headerListIdx + "", "��ȸ������");
			headerListMap.put(++headerListIdx + "", "��ȸ������");
			headerListMap.put(++headerListIdx + "", "��ȸ���ڵ�");

			rowPos = 8;
			colCnt = 5;
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
