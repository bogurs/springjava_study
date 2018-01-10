package kr.go.kofiu.str.validation.attach;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import jxl.read.biff.BiffException;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.bank.excel.BankXlsAttachmentValidator;
import kr.go.kofiu.str.validation.attach.stock.excel.StockXlsAttachmentValidator;

import org.xml.sax.SAXException;

import com.gpki.gpkiapi.exception.GpkiApiException;

/**
 *  STR 첨부파일 Excel 정형화 검증 수행.
 *  @param orgType 
 *  				Excel Version 정보에 포함된 은행(B)/증권(S) 이니셜 값.
 *  @param xlsFile
 *  				첨부된 엑셀 데이터
 */
public class AttachedXlsValidation {
	private String orgCd = "";

	public AttachedXlsValidation(String orgCd) {
		this.orgCd = orgCd;
	}

	public void xlsValidation(byte[] xlsFile) throws BiffException,
			IOException, XPathExpressionException, SAXException,
			ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			ValidationException, GpkiApiException, STRConfigureException {

		BankXlsAttachmentValidator bXlsValidator = null;
		StockXlsAttachmentValidator sXlsValidator = null;
		ConFirmOrgBankStock confirmOrgType = new ConFirmOrgBankStock();

		String orgType = null;

		orgType = confirmOrgType.conFirmBankOrStock(new ByteArrayInputStream(
				xlsFile));
		
		if ("B".equals(orgType)) {
			// 첨부파일 정형화 체크 (은행 xls 첨부파일)
			bXlsValidator = new BankXlsAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xlsFile));
			bXlsValidator.validate();
		}
		else if ("S".equals(orgType)) {
			// 첨부파일 정형화 체크 (증권 xls 첨부파일 )
			sXlsValidator = new StockXlsAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xlsFile));
			sXlsValidator.validate();
		}
	}
}
