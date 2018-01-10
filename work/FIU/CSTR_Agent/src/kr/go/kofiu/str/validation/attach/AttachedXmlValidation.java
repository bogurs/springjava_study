package kr.go.kofiu.str.validation.attach;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.bank.xml.BankXmlAttachmentValidator;
import kr.go.kofiu.str.validation.attach.stock.xml.StockXmlAttachmentValidator;

import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;


/**
 * STR ÷������ XML ����ȭ ���� ����
 * 
 * @param xmlFile
 *            ÷�ε� xml ������
 */
public class AttachedXmlValidation {
	private String orgCd = "";

	public AttachedXmlValidation(String orgCd) {
		this.orgCd = orgCd;
	}

	public void xmlValidation(byte[] xmlFile) throws XPathExpressionException,
			IOException, SAXException, ParserConfigurationException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, ValidationException, XmlException,
			STRConfigureException {

		BankXmlAttachmentValidator bXmlValidator = null;
		StockXmlAttachmentValidator sXmlValidator = null;

		try {
			// ÷������ ����ȭ üũ (���� xml ÷������)
			bXmlValidator = new BankXmlAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xmlFile));
			bXmlValidator.validate();

		} catch (XmlException xEct) {
			// ÷������ ��Ȳȭ üũ (���� xml ÷������ )
			sXmlValidator = new StockXmlAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xmlFile));
			sXmlValidator.validate();
		}
	}
}
