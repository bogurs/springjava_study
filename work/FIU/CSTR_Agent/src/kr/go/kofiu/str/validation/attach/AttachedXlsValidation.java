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
 *  STR ÷������ Excel ����ȭ ���� ����.
 *  @param orgType 
 *  				Excel Version ������ ���Ե� ����(B)/����(S) �̴ϼ� ��.
 *  @param xlsFile
 *  				÷�ε� ���� ������
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
			// ÷������ ����ȭ üũ (���� xls ÷������)
			bXlsValidator = new BankXlsAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xlsFile));
			bXlsValidator.validate();
		}
		else if ("S".equals(orgType)) {
			// ÷������ ����ȭ üũ (���� xls ÷������ )
			sXlsValidator = new StockXlsAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xlsFile));
			sXlsValidator.validate();
		}
	}
}
