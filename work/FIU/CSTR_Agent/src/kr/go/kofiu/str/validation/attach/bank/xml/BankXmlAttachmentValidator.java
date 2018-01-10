package kr.go.kofiu.str.validation.attach.bank.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.validation.AbstractValidator;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;

import org.apache.xmlbeans.XmlException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * STR ÷������ ���� xml���� ����ȭ ���� ����
 * 
 * @throw AttachmentValidationException
 */
public class BankXmlAttachmentValidator extends AbstractValidator {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private AddiStrBankDocument bankDoc = null;
	private List<BankXmlValidationItem> validationItems = new ArrayList<BankXmlValidationItem>();

	public final AddiStrBankDocument getBankDocument() {
		return this.bankDoc;
	}

	public BankXmlAttachmentValidator(String orgCd, InputStream inStream)
			throws XPathExpressionException, IOException, SAXException,
			ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			AttachmentValidationException, XmlException, STRConfigureException {
		this(orgCd, AddiStrBankDocument.Factory.parse(inStream));
	}

	public BankXmlAttachmentValidator(String orgCd, AddiStrBankDocument bankDoc)
			throws AttachmentValidationException, XPathExpressionException,
			IOException, SAXException, ParserConfigurationException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, STRConfigureException {

		if (null == bankDoc || !bankDoc.validate())
			throw new AttachmentValidationException(
					"(÷������ValidationError) : ÷������ ������ XML ��Ű�� ���ǿ� ����˴ϴ�.");

		this.bankDoc = bankDoc;
		this.refresh(orgCd);
	}

	@Override
	public final void validate() throws ValidationException {
		// TODO Auto-generated method stub
		for (BankXmlValidationItem vib : this.validationItems) {
			vib.validate(this.bankDoc);
		}

		if (logger.isLoggable(Level.FINEST))
			logger.finest(" ÷������ Validation ���� �Ϸ� ");
	}

	public final void refresh(String orgCd) throws XPathExpressionException,
			IOException, SAXException, ParserConfigurationException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, STRConfigureException {
		this.refresh(STRConfigure.getInstance().getAgentInfo().getReportOrgInfo(
				orgCd).getValidationInfo().getXmlAttachmentValidationConfig(),
				"/attachment-bank-xml-validation/item");
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

			this.validationItems.add((BankXmlValidationItem) Class.forName(
					itemClassNm).newInstance());
		}
	}
}