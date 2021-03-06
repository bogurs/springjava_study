package kr.go.kofiu.str.validation.attach.stock.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.validation.AbstractValidator;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.attach.AttachmentValidationException;

import org.apache.xmlbeans.XmlException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * STR 첨부파일 증권 xml파일 정형화 검증 수행
 * 
 * @throw AttachmentValidationException
 */
public class StockXmlAttachmentValidator extends AbstractValidator {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private AddiStrStockDocument stockDoc = null;
	private List<StockXmlValidationItem> validationItems = new ArrayList<StockXmlValidationItem>();

	public final AddiStrStockDocument getBankDocument() {
		return this.stockDoc;
	}

	public StockXmlAttachmentValidator(String orgCd, InputStream inStream)
			throws XPathExpressionException, IOException, SAXException,
			ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			ValidationException, XmlException, STRConfigureException {
		
		this(orgCd, AddiStrStockDocument.Factory.parse(inStream));
	}

	public StockXmlAttachmentValidator(String orgCd, AddiStrStockDocument stockDoc)
			throws AttachmentValidationException, XPathExpressionException,
			IOException, SAXException, ParserConfigurationException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, STRConfigureException {

		if (null == stockDoc || !stockDoc.validate())
			throw new AttachmentValidationException("(첨부파일ValidationError) : 첨부파일 서식이 XML 스키마 정의에 위배됩니다.");

		this.stockDoc = stockDoc;
		this.refresh(orgCd);
	}

	@Override
	public final void validate() throws ValidationException {
		// TODO Auto-generated method stub
		for (StockXmlValidationItem vis : this.validationItems) {
			vis.validate(this.stockDoc);
		}
		if (logger.isLoggable(Level.FINEST))
			logger.finest(" 첨부파일 Validation 검증 완료 ");
	}

	public final void refresh(String orgCd) throws XPathExpressionException, IOException,
			SAXException, ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		this
				.refresh(STRConfigure.getInstance().getAgentInfo().getReportOrgInfo(orgCd).getValidationInfo()
						.getXmlAttachmentValidationConfig(),
						"/attachment-stock-xml-validation/item"); // config.xml의
		// attachmentValidationConfig에
		// 설정된 파일 명을 수정해야함.
	};

	@Override
	public void refresh(String configFile, String pathExpr) throws IOException,
			SAXException, ParserConfigurationException,
			XPathExpressionException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		NodeList nodes = this.getNodeList(configFile, pathExpr);

		if (null != nodes && 1 > nodes.getLength())
			throw new XPathExpressionException(
					"(Config설정오류) : Config.xml에 등록된 attachmentValidationConfig 파일이 올바르지 않거나 Validation Item이 존재하지 않습니다.");

		for (int knx = 0; knx < nodes.getLength(); knx++) {
			String itemClassNm = nodes.item(knx).getTextContent();

			if (this.logger.isLoggable(Level.INFO)) {
				//this.logger.info("validation item class: " + itemClassNm);
			}

			this.validationItems.add((StockXmlValidationItem) Class.forName(
					itemClassNm).newInstance());
		}
	}
}
