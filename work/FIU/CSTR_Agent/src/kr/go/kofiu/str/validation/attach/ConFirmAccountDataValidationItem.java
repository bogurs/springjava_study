package kr.go.kofiu.str.validation.attach;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
/**
 * STR ÷������ Excel �������� ������ ����ȭ ���� ����.
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class ConFirmAccountDataValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		ConfirmAttachExcel confirm = new ConfirmAttachExcel();
		//����,���Ǳ���
		String gp = sh.getCell(1,0).getContents().trim().substring(0, 1);
		
		//���Ÿ��Ʋ����
		int rowPos = 0;
		int colCnt = 0;

		String tmpValue = null;
		int headerListIdx = -1;
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<Object, String> headerListMap = new HashMap<Object, String>();
		
		//��������
		headerMap.put("4", sh.getCell(0,4).getContents().trim());
		if(! headerMap.containsValue("��������")) {
			throw new AttachmentValidationException("(÷������ValidationError) : ���������� �����ϴ�.");
		}else {
			headerListMap.put(++headerListIdx +"", "���°��� �������");
			headerListMap.put(++headerListIdx +"", "���¹�ȣ");
			if("S".equals(gp)) {
				headerListMap.put(++headerListIdx +"", "���±���");
			}else {
				headerListMap.put(++headerListIdx +"", "��������(��ǰ)");					
			}
			headerListMap.put(++headerListIdx +"", "���°�������");
			headerListMap.put(++headerListIdx +"", "���°�������");
			headerListMap.put(++headerListIdx +"", "���°�������");
			headerListMap.put(++headerListIdx +"", "�����ܾ�");
			headerListMap.put(++headerListIdx +"", "������������ڵ�");
			headerListMap.put(++headerListIdx +"", "���������ڵ�");
			headerListMap.put(++headerListIdx +"", "���°������ڵ�");
			headerListMap.put(++headerListIdx +"", "���°������ڵ�");

			rowPos = 5;
			colCnt = 11;
			for(int i = 0; i < colCnt; i++) {
				// ������  ����ȭ üũ
				if("B".equals(gp) &&
						 ! "��������(��ǰ)".equals(headerListMap.get(""+ i)) &&
						 ! "�����ܾ�".equals(headerListMap.get(""+ i)) &&
						 ! "���������ڵ�".equals(headerListMap.get(""+ i)) &&
						 (sh.getCell(i, rowPos+1).getContents() == null ||
						  "".equals(sh.getCell(i, rowPos+1).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������ValidationError) : "+headerListMap.get(""+ i) +" ������ �ʼ� �Է� �Դϴ�.");
				}else if("S".equals(gp) &&
						 ! "���±���".equals(headerListMap.get(""+ i)) &&
						 ! "�����ܾ�".equals(headerListMap.get(""+ i)) &&
						 ! "���������ڵ�".equals(headerListMap.get(""+ i)) &&
						 (sh.getCell(i, rowPos+1).getContents() == null ||
						  "".equals(sh.getCell(i, rowPos+1).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������ValidationError) : "+headerListMap.get(""+ i) +" ������ �ʼ� �Է� �Դϴ�.");
				}
				
								if("���������ڵ�".equals(headerListMap.get(""+ i)) &&
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
