package kr.go.kofiu.str.validation.attach.stock.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.ConfirmAttachExcel;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;

/**
 * STR 첨부파일 증권 엑셀파일 거래상세내역 데이터 정형화 검증 수행
 * 
 * @param sh
 *            첨부된 엑셀 Sheet
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
		headerListMap.put(++headerListIdx + "", "거래일련번호");
		headerListMap.put(++headerListIdx + "", "거래순번");
		headerListMap.put(++headerListIdx + "", "거래발생일시");
		headerListMap.put(++headerListIdx + "", "거래종류명");
		headerListMap.put(++headerListIdx + "", "거래수단명");
		headerListMap.put(++headerListIdx + "", "거래채널명");
		headerListMap.put(++headerListIdx + "", "적요");
		headerListMap.put(++headerListIdx + "", "매매종목코드");
		headerListMap.put(++headerListIdx + "", "매매종목명");
		headerListMap.put(++headerListIdx + "", "단가");
		headerListMap.put(++headerListIdx + "", "거래수량");
		headerListMap.put(++headerListIdx + "", "거래금액");
		headerListMap.put(++headerListIdx + "", "정산금액");
		headerListMap.put(++headerListIdx + "", "수표금액");
		headerListMap.put(++headerListIdx + "", "유가증권명");
		headerListMap.put(++headerListIdx + "", "유가증권시작번호");
		headerListMap.put(++headerListIdx + "", "유가증권종료번호");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관명");
		headerListMap.put(++headerListIdx + "", "현금지급영업점명");
		headerListMap.put(++headerListIdx + "", "유가잔고");
		headerListMap.put(++headerListIdx + "", "예수금잔고");
		headerListMap.put(++headerListIdx + "", "미수금잔고");
		headerListMap.put(++headerListIdx + "", "융자금/대주금");
		headerListMap.put(++headerListIdx + "", "취급점명");
		headerListMap.put(++headerListIdx + "", "상대금융기관명");
		headerListMap.put(++headerListIdx + "", "상대/대체계좌번호");
		headerListMap.put(++headerListIdx + "", "상대/대체명");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명번호");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분");
		headerListMap.put(++headerListIdx + "", "상대계좌주국적");
		headerListMap.put(++headerListIdx + "", "거래종류코드");
		headerListMap.put(++headerListIdx + "", "거래수단코드");
		headerListMap.put(++headerListIdx + "", "거래채널코드");
		headerListMap.put(++headerListIdx + "", "유가증권코드");
		headerListMap.put(++headerListIdx + "", "현금지급금융기관코드");
		headerListMap.put(++headerListIdx + "", "현금지급영업점코드");
		headerListMap.put(++headerListIdx + "", "상대금융기관코드");
		headerListMap.put(++headerListIdx + "", "상대계좌주실명구분코드");
		headerListMap.put(++headerListIdx + "", "취급점코드");
		headerListMap.put(++headerListIdx + "", "정정구분코드");

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
				pos = "거래상세 " + (i - 11) + "번째  ";
				// 필수항목체크
				if ("거래일련번호".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래순번".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래발생일시".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null
								|| "".equals(sh.getCell(j, i).getContents()
										.trim()) || sh.getCell(j, i)
								.getContents().trim().length() != 14)) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 14자리 필수 입력 입니다.");
				} else if ("거래종류명".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래수단명".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래채널명".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("매매종목코드".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 23, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 23, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("매매종목명".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 22, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 22, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("단가".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 21, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 21, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("거래수량".equals(headerListMap.get("" + j))) {
					if (("10"
							.equals(sh.getCell(j + 20, i).getContents().trim()) || "11"
							.equals(sh.getCell(j + 20, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("거래금액".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("정산금액".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("상대금융기관명".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 6, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 6, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("상대/대체계좌번호".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 5, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 5, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("상대/대체명".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j + 4, i).getContents().trim()) || "22"
							.equals(sh.getCell(j + 4, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("거래종류코드".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래수단코드".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("거래채널코드".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				} else if ("상대금융기관코드".equals(headerListMap.get("" + j))) {
					if (("01".equals(sh.getCell(j - 6, i).getContents().trim()) || "22"
							.equals(sh.getCell(j - 6, i).getContents().trim()))
							&& (sh.getCell(j, i).getContents() == null || ""
									.equals(sh.getCell(j, i).getContents()
											.trim()))) {
						throw new AttachmentValidationException(
								"(첨부파일Validation) 증권XLS Invalid : " + pos
										+ headerListMap.get("" + j)
										+ " 정보는 필수 입력 입니다.");
					}
				} else if ("정정구분코드".equals(headerListMap.get("" + j))
						&& (sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					throw new AttachmentValidationException(
							"(첨부파일Validation) 증권XLS Invalid : " + pos
									+ headerListMap.get("" + j)
									+ " 정보는 필수 입력 입니다.");
				}
				
				// 데이터  정형화 체크
				tmpValue = sh.getCell(j, i).getContents();
				tmpValue = confirm.confirmNumberValue(""
						+ headerListMap.get("" + j), tmpValue);
				confirm.confirmLengthOfStockTransDetail(""
						+ headerListMap.get("" + j), tmpValue == null ? 0
						: tmpValue.trim().length(), pos);

				if ("거래종류코드".equals(headerListMap.get("" + j))) {
					confirm.confirmDeMethodCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("거래수단코드".equals(headerListMap.get("" + j))) {
					confirm.confirmDeMeanCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("거래채널코드".equals(headerListMap.get("" + j))) {
					confirm.confirmDeChnlCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
				if ("유가증권코드".equals(headerListMap.get("" + j))
						&& !(sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					confirm.confirmScrtCd(
							sh.getCell(j, i).getContents().trim(), pos);
				}
				if ("상대계좌주실명구분코드".equals(headerListMap.get("" + j))
						&& !(sh.getCell(j, i).getContents() == null || ""
								.equals(sh.getCell(j, i).getContents().trim()))) {
					confirm.confirmRealNumberCd(sh.getCell(j, i).getContents()
							.trim(), pos + " 상대계좌주");
				}
				if ("정정구분코드".equals(headerListMap.get("" + j))) {
					confirm.confirmCorClsfCd(sh.getCell(j, i).getContents()
							.trim(), pos);
				}
			}
		}
	}
}
