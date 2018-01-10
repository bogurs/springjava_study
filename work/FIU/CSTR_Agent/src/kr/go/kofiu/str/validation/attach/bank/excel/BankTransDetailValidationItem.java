package kr.go.kofiu.str.validation.attach.bank.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;
/**
 * STR ÷������ ���� �������� �ŷ������� ��� ����ȭ ���� ����.
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class BankTransDetailValidationItem implements XlsValidationItem {
	
	public void validate(Sheet sh) throws AttachmentValidationException {
		// TODO Auto-generated method stub
		int headerListIdx = -1;
		int rowPos = 0;
		int colCnt = 0;

		Map<Object, String> headerListMap = new HashMap<Object, String>();
		headerListMap.put(++headerListIdx + "", "�ŷ��Ϸù�ȣ");
		headerListMap.put(++headerListIdx + "", "�ŷ�����");
		headerListMap.put(++headerListIdx + "", "�ŷ��߻��Ͻ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�������");
		headerListMap.put(++headerListIdx + "", "�ŷ����ܸ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�ä�θ�");
		headerListMap.put(++headerListIdx + "", "����");
		headerListMap.put(++headerListIdx + "", "��ȭ�����ڵ�");
		headerListMap.put(++headerListIdx + "", "��ȯ�ŷ��ݾ�");
		headerListMap.put(++headerListIdx + "", "���ޱݾ�");
		headerListMap.put(++headerListIdx + "", "�Աݱݾ�");
		headerListMap.put(++headerListIdx + "", "�ܾ�");
		headerListMap.put(++headerListIdx + "", "��ޱ��������");
		headerListMap.put(++headerListIdx + "", "�������");
		headerListMap.put(++headerListIdx + "", "�������Ǹ�");
		headerListMap.put(++headerListIdx + "", "�������ǽ��۹�ȣ");
		headerListMap.put(++headerListIdx + "", "�������������ȣ");
		headerListMap.put(++headerListIdx + "", "�������ޱ��������");
		headerListMap.put(++headerListIdx + "", "�������޿�������");
		headerListMap.put(++headerListIdx + "", "�Ƿ��θ�");
		headerListMap.put(++headerListIdx + "", "�Ƿ��νǸ���");
		headerListMap.put(++headerListIdx + "", "�Ƿ��νǸ��ȣ");
		headerListMap.put(++headerListIdx + "", "�Ƿ��α���");
		headerListMap.put(++headerListIdx + "", "�����������");
		headerListMap.put(++headerListIdx + "", "�����¹�ȣ");
		headerListMap.put(++headerListIdx + "", "�������ָ�");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ��ȣ");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ���");
		headerListMap.put(++headerListIdx + "", "�������ֱ���");
		headerListMap.put(++headerListIdx + "", "�ŷ������ڵ�");
		headerListMap.put(++headerListIdx + "", "�ŷ������ڵ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�ä���ڵ�");
		headerListMap.put(++headerListIdx + "", "��ޱ�������ڵ�");
		headerListMap.put(++headerListIdx + "", "������ڵ�");
		headerListMap.put(++headerListIdx + "", "���������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������ޱ�������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������޿������ڵ�");
		headerListMap.put(++headerListIdx + "", "�Ƿ��νǸ����ڵ�");
		headerListMap.put(++headerListIdx + "", "����������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ����ڵ�");
		headerListMap.put(++headerListIdx + "", "���������ڵ�");

		rowPos = 11;
		colCnt = 41;
		// check header
		for (int i = 0; i < colCnt; i++) {
			if (!headerListMap.containsValue(sh.getCell(i, rowPos)
					.getContents().trim())) {
				throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+headerListMap.get(""
						+ i)
						+ " ��� ������ �����ϴ�.");
			}
		}	
	}
}
