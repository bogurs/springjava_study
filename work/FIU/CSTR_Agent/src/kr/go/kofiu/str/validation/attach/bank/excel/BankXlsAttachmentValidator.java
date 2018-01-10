package kr.go.kofiu.str.validation.attach.bank.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.validation.AbstractValidator;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;
import kr.go.kofiu.str.validation.attach.XlsValidationItem;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * STR ÷������ ���� �������� ����ȭ ���� ����
 * @throws AttachmentValidationException
 */
public class BankXlsAttachmentValidator extends AbstractValidator {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private int cnt;
	private int cntSh = 0;
	private Workbook wb = null;
	private Sheet[] sh = null;
	private List<XlsValidationItem> validationItems = new ArrayList<XlsValidationItem>();

	public final Workbook getWorkBook() {
		return this.wb;
	}

	public final Sheet[] getSheet() {
		return this.sh;
	}

	public BankXlsAttachmentValidator(String orgCd, InputStream inStream)
			throws BiffException, IOException, AttachmentValidationException,
			XPathExpressionException, SAXException,
			ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		this(orgCd, Workbook.getWorkbook(inStream));
	}

	public BankXlsAttachmentValidator(String orgCd, Workbook wb)
			throws AttachmentValidationException, XPathExpressionException,
			IOException, SAXException, ParserConfigurationException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, STRConfigureException {
		if (null == wb)
			throw new AttachmentValidationException("(÷������ValidationError) : ÷������ ���� �����Ͱ� �������� �ʽ��ϴ�.");

		this.wb = wb;
		this.sh = wb.getSheets();
		this.cntSh = wb.getNumberOfSheets();
		this.refresh(orgCd);
	}

	@Override
	public final void validate() throws ValidationException {
		for (XlsValidationItem vib : this.validationItems) {
			for (cnt = 0; cnt < this.cntSh; cnt++) {
				vib.validate(this.sh[cnt]);
			}
			
			if (logger.isLoggable(Level.FINEST))
				logger.finest(" ÷������ Validation ���� �Ϸ� ");
		}
		
		this.wb.close();
	}

	public final void refresh(String orgCd) throws XPathExpressionException, IOException,
			SAXException, ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		this
				.refresh(STRConfigure.getInstance().getAgentInfo().getReportOrgInfo(orgCd).getValidationInfo()
						.getXlsAttachmentValidationConfig(),
						"/attachment-bank-xls-validation/item");
	};

	@Override
	public void refresh(String configFile, String pathExpr) throws IOException,
			SAXException, ParserConfigurationException,
			XPathExpressionException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		NodeList nodes = this.getNodeList(configFile, pathExpr);
		
		if (null != nodes && 1 > nodes.getLength())
			throw new XPathExpressionException(
					"(Config��������) : Config.xml�� ��ϵ� attachmentValidationConfig ������ �ùٸ��� �ʰų� Validation Item�� �������� �ʽ��ϴ�.");

		for (int knx = 0; knx < nodes.getLength(); knx++) {
			String itemClassNm = nodes.item(knx).getTextContent();

			if (this.logger.isLoggable(Level.INFO)) {
				//this.logger.info("validation item class: " + itemClassNm);
			}

			this.validationItems.add((XlsValidationItem) Class.forName(
					itemClassNm).newInstance());
		}
	}
}
