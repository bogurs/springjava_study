package kr.go.kofiu.str.validation.attach.bank.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.ConfirmAttachExcel;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;
/**
 * STR ÷������ ���� �������� �ŷ������� ������ ����ȭ ���� ����.
 * @param sh
 * 			÷�ε� ���� Sheet
 * @throws AttachmentValidationException
 */
public class BankTransDetailDataValidationItem implements XlsValidationItem {
	ConfirmAttachExcel confirm = new ConfirmAttachExcel();

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
		
		// check row value
		confirmExcelOfBankRowValue(rowPos + 1, colCnt, headerListMap, sh);
	}

	private void confirmExcelOfBankRowValue(int rowPos, int colCnt,
			Map<Object, String> headerListMap, Sheet sh) throws AttachmentValidationException {
		String pos = null;
		String tmpValue = null;
		for (int i = rowPos; i < sh.getRows(); i++) {
			for (int j = 0; j < colCnt; j++) {
				pos = "�ŷ��� " + (i - 11) + "��° ";
				// �ʼ��׸�üũ
				if ("�ŷ��Ϸù�ȣ".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + "��(��) �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�����".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ��߻��Ͻ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null
								|| "".equals(sh.getCell(j, i).getContents()
										.trim()) || sh.getCell(j, i)
								.getContents().trim().length() != 14)) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ 14�ڸ� �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�������".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ����ܸ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�ä�θ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("��ȭ�����ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("��ȯ�ŷ��ݾ�".equals(headerListMap.get("" + j))) {
					if (!"KRW"
							.equals(sh.getCell(j - 1, i).getContents().trim())
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("���ޱݾ�".equals(headerListMap.get("" + j))) {
					if (("02"
							.equals(sh.getCell(j + 20, i).getContents().trim())
							|| "05".equals(sh.getCell(j + 20, i).getContents()
									.trim())
							|| "09".equals(sh.getCell(j + 20, i).getContents()
									.trim())
							|| "13".equals(sh.getCell(j + 20, i).getContents()
									.trim())
							|| "15".equals(sh.getCell(j + 20, i).getContents()
									.trim()) || "21".equals(sh.getCell(j + 20,
							i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Աݱݾ�".equals(headerListMap.get("" + j))) {
					if (("01"
							.equals(sh.getCell(j + 19, i).getContents().trim())
							|| "04".equals(sh.getCell(j + 19, i).getContents()
									.trim())
							|| "08".equals(sh.getCell(j + 19, i).getContents()
									.trim())
							|| "12".equals(sh.getCell(j + 19, i).getContents()
									.trim())
							|| "14".equals(sh.getCell(j + 19, i).getContents()
									.trim()) || "20".equals(sh.getCell(j + 19,
							i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Ƿ��θ�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j + 2, i).getContents() != null && !""
							.equals(sh.getCell(j + 2, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Ƿ��νǸ��и�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j + 1, i).getContents() != null && !""
							.equals(sh.getCell(j + 1, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Ƿ��α���".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j - 1, i).getContents() != null && !""
							.equals(sh.getCell(j - 1, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Ƿ��νǸ����ڵ�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j - 16, i).getContents() != null && !""
							.equals(sh.getCell(j - 16, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j, i).getContents() == null || "".equals(sh
							.getCell(j, i).getContents().trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j, i).getContents() == null || "".equals(sh
							.getCell(j, i).getContents().trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ�ä���ڵ�".equals(headerListMap.get("" + j))) {
					if ((sh.getCell(j, i).getContents() == null || "".equals(sh
							.getCell(j, i).getContents().trim()))) {
						throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
								+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("���������ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException("(÷������Validation) ����XLS Invalid : "+pos
							+ headerListMap.get("" + j) + " ������ �ʼ� �Է� �Դϴ�.");
				}
				
				// ������  ����ȭ üũ
				tmpValue = sh.getCell(j, i).getContents();
				tmpValue = confirm.confirmNumberValue(""
						+ headerListMap.get("" + j), tmpValue);
				confirm.confirmLengthOfBankTransDetail(""
						+ headerListMap.get("" + j), tmpValue == null ? 0
						: tmpValue.trim().length(), pos);

				if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))) {
					confirm.confirmDeMethodCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))) {
					confirm.confirmDeMeanCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("�ŷ�ä���ڵ�".equals(headerListMap.get("" + j))) {
					confirm.confirmDeChnlCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("���������ڵ�".equals(headerListMap.get("" + j))
						&& !(sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					confirm.confirmScrtCd(
							sh.getCell(j, i).getContents().trim(), pos);
				}
				if ("�Ƿ��νǸ����ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j - 16, i).getContents() != null && !""
								.equals(sh.getCell(j - 16, i).getContents()
										.trim()))) {
					confirm.confirmRealNumberCd(sh.getCell(j, i).getContents()
							.trim(), pos + " �Ƿ���");
				}
				if ("�������ֽǸ����ڵ�".equals(headerListMap.get("" + j))
						&& !(sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					confirm.confirmRealNumberCd(sh.getCell(j, i).getContents()
							.trim(), pos + " ��������");
				}
				if ("���������ڵ�".equals(headerListMap.get("" + j))) {
					confirm.confirmCorClsfCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
			}
		}
	}
}
