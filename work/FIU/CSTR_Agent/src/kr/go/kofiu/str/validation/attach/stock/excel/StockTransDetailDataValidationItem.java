package kr.go.kofiu.str.validation.attach.stock.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.ConfirmAttachExcel;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;

/**
 * STR ÷������ ���� �������� �ŷ��󼼳��� ������ ����ȭ ���� ����
 * 
 * @param sh
 *            ÷�ε� ���� Sheet
 * @throw AttachmentValidationException
 * 
 */
public class StockTransDetailDataValidationItem implements XlsValidationItem {

	ConfirmAttachExcel confirm = new ConfirmAttachExcel();

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
		// check row value
		confirmExcelOfStockRowValue(customerInfoRowPos + 1, customerInfoColCnt,
				headerListMap, sh);
	}

	private void confirmExcelOfStockRowValue(int rowPos, int colCnt,
			Map<Object, String> headerListMap, Sheet sh)
			throws AttachmentValidationException {
		String pos = null;
		String tmpValue = null;
		for (int i = rowPos; i < sh.getRows(); i++) {
			for (int j = 0; j < colCnt; j++) {
				pos = "�ŷ��� " + (i - 11) + "��°  ";
				// �ʼ��׸�üũ
				if ("�ŷ��Ϸù�ȣ".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�����".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ��߻��Ͻ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null
								|| "".equals(sh.getCell(j, i).getContents()
										.trim()) || sh.getCell(j, i)
								.getContents().trim().length() != 14)) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ 14�ڸ� �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�������".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ����ܸ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�ä�θ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�Ÿ������ڵ�".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 23, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 23, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�Ÿ������".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 22, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 22, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ܰ�".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 21, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 21, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ�����".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 20, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 20, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ��ݾ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("����ݾ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�����������".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 6, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 6, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("���/��ü���¹�ȣ".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 5, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 5, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("���/��ü��".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 4, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 4, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ������ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("�ŷ�ä���ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				} else if ("����������ڵ�".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j - 6, i).getContents().trim()) || "22"
							.equals(sh.getCell(j - 6, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(÷������Validation) ����XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " ������ �ʼ� �Է� �Դϴ�.");
					}
				} else if ("���������ڵ�".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(÷������Validation) ����XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " ������ �ʼ� �Է� �Դϴ�.");
				}
				
				// ������  ����ȭ üũ
				tmpValue = sh.getCell(j, i).getContents();
				tmpValue = confirm.confirmNumberValue(""
						+ headerListMap.get("" + j), tmpValue);
				confirm.confirmLengthOfStockTransDetail(""
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
