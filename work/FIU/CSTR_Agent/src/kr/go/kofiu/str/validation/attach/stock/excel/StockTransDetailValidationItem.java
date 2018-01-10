package kr.go.kofiu.str.validation.attach.stock.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;

/**
 * STR ÷������ ���� �������� �ŷ��󼼳��� ��� ����ȭ ���� ����
 * 
 * @param sh
 *            ÷�ε� ���� Sheet
 * @throw AttachmentValidationException
 * 
 */
public class StockTransDetailValidationItem implements XlsValidationItem {

	public void validate(Sheet sh) throws AttachmentValidationException {
		int headerListIdx = -1;
		int customerInfoRowPos = 0;
		int customerInfoColCnt = 0;

		Map<Object, String> headerListMap = new HashMap<Object, String>();
		headerListMap.put(++headerListIdx + "", "�ŷ��Ϸù�ȣ");
		headerListMap.put(++headerListIdx + "", "�ŷ�����");
		headerListMap.put(++headerListIdx + "", "�ŷ��߻��Ͻ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�������");
		headerListMap.put(++headerListIdx + "", "�ŷ����ܸ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�ä�θ�");
		headerListMap.put(++headerListIdx + "", "����");
		headerListMap.put(++headerListIdx + "", "�Ÿ������ڵ�");
		headerListMap.put(++headerListIdx + "", "�Ÿ������");
		headerListMap.put(++headerListIdx + "", "�ܰ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�����");
		headerListMap.put(++headerListIdx + "", "�ŷ��ݾ�");
		headerListMap.put(++headerListIdx + "", "����ݾ�");
		headerListMap.put(++headerListIdx + "", "��ǥ�ݾ�");
		headerListMap.put(++headerListIdx + "", "�������Ǹ�");
		headerListMap.put(++headerListIdx + "", "�������ǽ��۹�ȣ");
		headerListMap.put(++headerListIdx + "", "�������������ȣ");
		headerListMap.put(++headerListIdx + "", "�������ޱ��������");
		headerListMap.put(++headerListIdx + "", "�������޿�������");
		headerListMap.put(++headerListIdx + "", "�����ܰ�");
		headerListMap.put(++headerListIdx + "", "�������ܰ�");
		headerListMap.put(++headerListIdx + "", "�̼����ܰ�");
		headerListMap.put(++headerListIdx + "", "���ڱ�/���ֱ�");
		headerListMap.put(++headerListIdx + "", "�������");
		headerListMap.put(++headerListIdx + "", "�����������");
		headerListMap.put(++headerListIdx + "", "���/��ü���¹�ȣ");
		headerListMap.put(++headerListIdx + "", "���/��ü��");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ��ȣ");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ���");
		headerListMap.put(++headerListIdx + "", "�������ֱ���");
		headerListMap.put(++headerListIdx + "", "�ŷ������ڵ�");
		headerListMap.put(++headerListIdx + "", "�ŷ������ڵ�");
		headerListMap.put(++headerListIdx + "", "�ŷ�ä���ڵ�");
		headerListMap.put(++headerListIdx + "", "���������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������ޱ�������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������޿������ڵ�");
		headerListMap.put(++headerListIdx + "", "����������ڵ�");
		headerListMap.put(++headerListIdx + "", "�������ֽǸ����ڵ�");
		headerListMap.put(++headerListIdx + "", "������ڵ�");
		headerListMap.put(++headerListIdx + "", "���������ڵ�");

		customerInfoRowPos = 11;
		customerInfoColCnt = 40;
		// check header
		for (int i = 0; i < customerInfoColCnt; i++) {
			if (!headerListMap.containsValue(sh.getCell(i, customerInfoRowPos)
					.getContents().trim())) {
				throw new AttachmentValidationException(
						"(÷������Validation) ����XLS Invalid : "
								+ headerListMap.get("" + i) + " ��� ������ �����ϴ�.");
			}
		}
	}
}
