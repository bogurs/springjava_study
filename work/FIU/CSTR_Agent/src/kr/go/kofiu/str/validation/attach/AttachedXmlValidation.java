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
 * STR 첨부파일 XML 정형화 검증 수행
 * 
 * @param xmlFile
 *            첨부된 xml 데이터
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
			// 첨부파일 정형화 체크 (은행 xml 첨부파일)
			bXmlValidator = new BankXmlAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xmlFile));
			bXmlValidator.validate();

		} catch (XmlException xEct) {
			// 첨부파일 정황화 체크 (증권 xml 첨부파일 )
			sXmlValidator = new StockXmlAttachmentValidator(this.orgCd,
					new ByteArrayInputStream(xmlFile));
			sXmlValidator.validate();
		}
	}
}
