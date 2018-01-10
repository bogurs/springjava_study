package kr.go.kofiu.str.validation.attach;

public class ConfirmAttachExcel {
/**
 * STR ÷������ ���� �ŷ������� ����ȭ ���� Sub Methods
 * @throws AttachmentValidationException
 */
	public void confirmRealNumberCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "11".equals(cd) || "12".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + "�Ǹ����ڵ带 Ȯ���Ͻʽÿ�.");
		}
	}

	public void confirmRealNumberCd(String cd)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "11".equals(cd) || "12".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : �Ǹ��ȣ �����ڵ带 Ȯ���Ͻʽÿ�.");
		}
	}

	public void confirmLengthOfCustomerInfo(String header, int valueLen)
			throws AttachmentValidationException {
		if ("������(�����)��".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�Ǹ��ȣ ����".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("������(�����)�Ǹ��ȣ".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:15)");
		} else if ("������ ����".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�Ǹ��ȣ �����ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("����".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:3)");
		}
	}

	public void confirmAccountKndCd(String cd)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "11".equals(cd) || "12".equals(cd) || "21".equals(cd)
				|| "22".equals(cd) || "31".equals(cd) || "32".equals(cd)
				|| "41".equals(cd) || "42".equals(cd) || "51".equals(cd)
				|| "52".equals(cd) || "53".equals(cd) || "59".equals(cd)
				|| "61".equals(cd) || "62".equals(cd) || "63".equals(cd)
				|| "64".equals(cd) || "65".equals(cd) || "66".equals(cd)
				|| "91".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : ���������ڵ带 Ȯ���Ͻʽÿ�.");
		}
	}

	public String confirmNumberValue(String header, String value)
			throws AttachmentValidationException {
		if ("�����ܾ�".equals(header) || "��ȯ�ŷ��ݾ�".equals(header)
				|| "���ޱݾ�".equals(header) || "�Աݱݾ�".equals(header)
				|| "�ܾ�".equals(header) || "�ܰ�".equals(header)
				|| "�ŷ�����".equals(header) || "�ŷ��ݾ�".equals(header)
				|| "��ȯ�ŷ��ݾ�".equals(header) || "����ݾ�".equals(header)
				|| "��ǥ�ݾ�".equals(header) || "�����ܰ�".equals(header)
				|| "�������ܰ�".equals(header) || "���ڱ�/���ֱ�".equals(header)) {
			// check number
			try {
				new Double(value == null || "".equals(value) ? "0" : value);
			} catch (Exception e) {
				throw new AttachmentValidationException("(÷������ValidationError) : "+header
						+ " ���� �������� Ȯ�� �Ͻʽÿ� - " + value);
			}

			// check dot
			if (value.indexOf(".") != -1) {
				value = value.substring(0, value.indexOf("."))
						+ value.substring(value.indexOf(".") + 1);
			}
		}
		return value;
	}

	public void confirmLengthOfAccountInfo(String header, int valueLen)
			throws AttachmentValidationException {
		if ("���°��� �������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("���¹�ȣ".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:25)");
		} else if ("��������(��ǰ)".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:100)");
		} else if ("���±���".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:100)");
		} else if ("���°�������".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:8)");
		} else if ("���°�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("���°�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�����ܾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		} else if ("������������ڵ�".equals(header) && valueLen != 4) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		} else if ("���������ڵ�".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("���°������ڵ�".equals(header) && valueLen != 7) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		} else if ("���°������ڵ�".equals(header) && valueLen != 7) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		}
	}

	public void confirmLengthOfInquiryInfo(String header, int valueLen)
			throws AttachmentValidationException {
		if ("��ȸ�Ͻ�".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:14)");
		} else if ("��ȸ����".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("��ȸ������".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:8)");
		} else if ("��ȸ������".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:8)");
		} else if ("��ȸ���ڵ�".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		}
	}

	public void confirmLengthOfBankTransDetail(String header, int valueLen,
			String rowPos) throws AttachmentValidationException {
		if ("�ŷ��Ϸù�ȣ".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�ŷ�����".equals(header) && valueLen > 10) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:10)");
		} else if ("�ŷ��߻��Ͻ�".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:14)");
		} else if ("�ŷ�������".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�ŷ����ܸ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�ŷ�ä�θ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("����".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:100)");
		} else if ("��ȭ�����ڵ�".equals(header) && valueLen != 3) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:3)");
		} else if ("��ȯ�ŷ��ݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		} else if ("���ޱݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		} else if ("�Աݱݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		} else if ("�ܾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		} else if ("��ޱ��������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�������Ǹ�".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		} else if ("�������ǽ��۹�ȣ".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		} else if ("�������������ȣ".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		} else if ("�������ޱ��������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�������޿�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�Ƿ��θ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�Ƿ��νǸ���".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�Ƿ��νǸ��ȣ".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:15)");
		} else if ("�Ƿ��α���".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�����������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		} else if ("�����¹�ȣ".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:25)");
		} else if ("�������ָ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�������ֽǸ��ȣ".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:15)");
		} else if ("�������ֽǸ���".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		} else if ("�������ֱ���".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�ŷ������ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�ŷ������ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�ŷ�ä���ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:2)");
		} else if ("��ޱ�������ڵ�".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		} else if ("������ڵ�".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		} else if ("���������ڵ�".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("�������ޱ�������ڵ�".equals(header)
				&& (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		} else if ("�������޿������ڵ�".equals(header)
				&& (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		} else if ("�Ƿ��νǸ����ڵ�".equals(header)
				&& (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("����������ڵ�".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		} else if ("�������ֽǸ����ڵ�".equals(header)
				&& (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		} else if ("���������ڵ�".equals(header) && valueLen != 1) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header
					+ " �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:1)");
		}
	}

	public void confirmDeMethodCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "10".equals(cd) || "11".equals(cd) || "12".equals(cd)
				|| "13".equals(cd) || "14".equals(cd) || "15".equals(cd)
				|| "16".equals(cd) || "17".equals(cd) || "18".equals(cd)
				|| "19".equals(cd) || "20".equals(cd) || "21".equals(cd)
				|| "22".equals(cd) || "23".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + " �ŷ������ڵ带 Ȯ�� �Ͻʽÿ�.");
		}
	}

	public void confirmDeMeanCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + " �ŷ������ڵ带 Ȯ�� �Ͻʽÿ�.");
		}
	}

	public void confirmDeChnlCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "10".equals(cd) || "11".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + " �ŷ�ä���ڵ带 Ȯ�� �Ͻʽÿ�.");
		}
	}

	public void confirmCorClsfCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("A".equals(cd) || "U".equals(cd) || "D".equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + " ���������ڵ带 Ȯ�� �Ͻʽÿ�.");
		}
	}

	public void confirmScrtCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd) || "99"
				.equals(cd))) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+pos + " ���������ڵ带 Ȯ�� �Ͻʽÿ�.");
		}
	}
	
	public void confirmLengthOfStockTransDetail(String header, int valueLen, String rowPos) throws AttachmentValidationException {
		if("�ŷ��Ϸù�ȣ".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("�ŷ�����".equals(header) && valueLen > 10) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:10)");
		}else if("�ŷ��߻��Ͻ�".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:14)");
		}else if("�ŷ�������".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		}else if("�ŷ����ܸ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		}else if("�ŷ�ä�θ�".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		}else if("����".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:100)");
		}else if("�Ÿ������ڵ�".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		}else if("�Ÿ������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("�ܰ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�ŷ�����".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�ŷ��ݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("����ݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("��ǥ�ݾ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�������Ǹ�".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		}else if("�������ǽ��۹�ȣ".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		}else if("�������������ȣ".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:20)");
		}else if("�������ޱ��������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("�������޿�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("�����ܰ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�������ܰ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�̼����ܰ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("���ڱ�/���ֱ�".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:17)");
		}else if("�������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("�����������".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:60)");
		}else if("���/��ü���¹�ȣ".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:25)");
		}else if("���/��ü��".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:30)");
		}else if("�������ֽǸ��ȣ".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(MAX:15)");
		}else if("�������ֽǸ���".equals(header) && (valueLen > 0 && valueLen != 30)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:30)");
		}else if("�������ֱ���".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("�ŷ������ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("�ŷ������ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("�ŷ�ä���ڵ�".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("���������ڵ�".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("�������ޱ�������ڵ�".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		}else if("�������޿������ڵ�".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:7)");
		}else if("����������ڵ�".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:4)");
		}else if("�������ֽǸ����ڵ�".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("������ڵ�".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:2)");
		}else if("���������ڵ�".equals(header) && (valueLen > 0 && valueLen != 1)) {
			throw new AttachmentValidationException("(÷������ValidationError) : "+rowPos + header +" �ڸ����� Ȯ�� �Ͻʽÿ�(CHAR:1)");
		}
    }
}
