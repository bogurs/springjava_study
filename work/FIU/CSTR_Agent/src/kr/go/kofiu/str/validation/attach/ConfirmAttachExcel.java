package kr.go.kofiu.str.validation.attach;

public class ConfirmAttachExcel {
/**
 * STR 첨부파일 엑셀 거래상세정보 정형화 검증 Sub Methods
 * @throws AttachmentValidationException
 */
	public void confirmRealNumberCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "11".equals(cd) || "12".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + "실명구분코드를 확인하십시오.");
		}
	}

	public void confirmRealNumberCd(String cd)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "11".equals(cd) || "12".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : 실명번호 구분코드를 확인하십시오.");
		}
	}

	public void confirmLengthOfCustomerInfo(String header, int valueLen)
			throws AttachmentValidationException {
		if ("계좌주(사업자)명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("실명번호 구분".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("계좌주(사업자)실명번호".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:15)");
		} else if ("계좌주 국적".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("실명번호 구분코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("직업".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:3)");
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
			throw new AttachmentValidationException("(첨부파일ValidationError) : 계좌종류코드를 확인하십시오.");
		}
	}

	public String confirmNumberValue(String header, String value)
			throws AttachmentValidationException {
		if ("계좌잔액".equals(header) || "외환거래금액".equals(header)
				|| "지급금액".equals(header) || "입금금액".equals(header)
				|| "잔액".equals(header) || "단가".equals(header)
				|| "거래수량".equals(header) || "거래금액".equals(header)
				|| "외환거래금액".equals(header) || "정산금액".equals(header)
				|| "수표금액".equals(header) || "유가잔고".equals(header)
				|| "예수금잔고".equals(header) || "융자금/대주금".equals(header)) {
			// check number
			try {
				new Double(value == null || "".equals(value) ? "0" : value);
			} catch (Exception e) {
				throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
						+ " 값이 숫자인지 확인 하십시오 - " + value);
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
		if ("계좌개설 금융기관".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("계좌번호".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:25)");
		} else if ("계좌종류(상품)".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:100)");
		} else if ("계좌구분".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:100)");
		} else if ("계좌개설일자".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:8)");
		} else if ("계좌개설점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("계좌관리점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("계좌잔액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:17)");
		} else if ("개설금융기관코드".equals(header) && valueLen != 4) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:4)");
		} else if ("계좌종류코드".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("계좌개설점코드".equals(header) && valueLen != 7) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:7)");
		} else if ("계좌관리점코드".equals(header) && valueLen != 7) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:7)");
		}
	}

	public void confirmLengthOfInquiryInfo(String header, int valueLen)
			throws AttachmentValidationException {
		if ("조회일시".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:14)");
		} else if ("조회점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("조회시작일".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:8)");
		} else if ("조회종료일".equals(header) && valueLen != 8) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:8)");
		} else if ("조회점코드".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+header
					+ " 자리수를 확인 하십시오(CHAR:7)");
		}
	}

	public void confirmLengthOfBankTransDetail(String header, int valueLen,
			String rowPos) throws AttachmentValidationException {
		if ("거래일련번호".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("거래순번".equals(header) && valueLen > 10) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:10)");
		} else if ("거래발생일시".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:14)");
		} else if ("거래종류명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("거래수단명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("거래채널명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("적요".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:100)");
		} else if ("통화구분코드".equals(header) && valueLen != 3) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:3)");
		} else if ("외환거래금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:17)");
		} else if ("지급금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:17)");
		} else if ("입금금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:17)");
		} else if ("잔액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:17)");
		} else if ("취급금융기관명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("취급점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("유가증권명".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:20)");
		} else if ("유가증권시작번호".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:20)");
		} else if ("유가증권종료번호".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:20)");
		} else if ("현금지급금융기관명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("현금지급영업점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("의뢰인명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("의뢰인실명구분".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("의뢰인실명번호".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:15)");
		} else if ("의뢰인국적".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("상대금융기관명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:60)");
		} else if ("상대계좌번호".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:25)");
		} else if ("상대계좌주명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("상대계좌주실명번호".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:15)");
		} else if ("상대계좌주실명구분".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:30)");
		} else if ("상대계좌주국적".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("거래종류코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("거래수단코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("거래채널코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(MAX:2)");
		} else if ("취급금융기관코드".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:4)");
		} else if ("취급점코드".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:7)");
		} else if ("유가증권코드".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("현금지급금융기관코드".equals(header)
				&& (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:4)");
		} else if ("현금지급영업점코드".equals(header)
				&& (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:7)");
		} else if ("의뢰인실명구분코드".equals(header)
				&& (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("상대금융기관코드".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:4)");
		} else if ("상대계좌주실명구분코드".equals(header)
				&& (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:2)");
		} else if ("정정구분코드".equals(header) && valueLen != 1) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header
					+ " 자리수를 확인 하십시오(CHAR:1)");
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
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + " 거래종류코드를 확인 하십시오.");
		}
	}

	public void confirmDeMeanCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + " 거래수단코드를 확인 하십시오.");
		}
	}

	public void confirmDeChnlCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd)
				|| "10".equals(cd) || "11".equals(cd) || "99".equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + " 거래채널코드를 확인 하십시오.");
		}
	}

	public void confirmCorClsfCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("A".equals(cd) || "U".equals(cd) || "D".equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + " 정정구분코드를 확인 하십시오.");
		}
	}

	public void confirmScrtCd(String cd, String pos)
			throws AttachmentValidationException {
		if (!("01".equals(cd) || "02".equals(cd) || "03".equals(cd)
				|| "04".equals(cd) || "05".equals(cd) || "06".equals(cd)
				|| "07".equals(cd) || "08".equals(cd) || "09".equals(cd) || "99"
				.equals(cd))) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+pos + " 유가증권코드를 확인 하십시오.");
		}
	}
	
	public void confirmLengthOfStockTransDetail(String header, int valueLen, String rowPos) throws AttachmentValidationException {
		if("거래일련번호".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("거래순번".equals(header) && valueLen > 10) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:10)");
		}else if("거래발생일시".equals(header) && valueLen != 14) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:14)");
		}else if("거래종류명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:30)");
		}else if("거래수단명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:30)");
		}else if("거래채널명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:30)");
		}else if("적요".equals(header) && valueLen > 100) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:100)");
		}else if("매매종목코드".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:20)");
		}else if("매매종목명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("단가".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("거래수량".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("거래금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("정산금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("수표금액".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("유가증권명".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:20)");
		}else if("유가증권시작번호".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:20)");
		}else if("유가증권종료번호".equals(header) && valueLen > 20) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:20)");
		}else if("현금지급금융기관명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("현금지급영업점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("유가잔고".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("예수금잔고".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("미수금잔고".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("융자금/대주금".equals(header) && valueLen > 17) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:17)");
		}else if("취급점명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("상대금융기관명".equals(header) && valueLen > 60) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:60)");
		}else if("상대/대체계좌번호".equals(header) && valueLen > 25) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:25)");
		}else if("상대/대체명".equals(header) && valueLen > 30) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:30)");
		}else if("상대계좌주실명번호".equals(header) && valueLen > 15) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(MAX:15)");
		}else if("상대계좌주실명구분".equals(header) && (valueLen > 0 && valueLen != 30)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:30)");
		}else if("상대계좌주국적".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("거래종류코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("거래수단코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("거래채널코드".equals(header) && valueLen != 2) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("유가증권코드".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("현금지급금융기관코드".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:4)");
		}else if("현금지급영업점코드".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:7)");
		}else if("상대금융기관코드".equals(header) && (valueLen > 0 && valueLen != 4)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:4)");
		}else if("상대계좌주실명구분코드".equals(header) && (valueLen > 0 && valueLen != 2)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("취급점코드".equals(header) && (valueLen > 0 && valueLen != 7)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:2)");
		}else if("정정구분코드".equals(header) && (valueLen > 0 && valueLen != 1)) {
			throw new AttachmentValidationException("(첨부파일ValidationError) : "+rowPos + header +" 자리수를 확인 하십시오(CHAR:1)");
		}
    }
}
