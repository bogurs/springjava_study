package kr.go.kofiu.str.validation.str;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.conf.ValidationInfo;
import kr.go.kofiu.str.validation.AbstractValidator;
import kr.go.kofiu.str.validation.ValidationException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * STR 정형화 검증 수행
 * 
 * @author 최성웅
 * @throws StrValidationException
 * @throws ValidationException
 */
public class StrValidator extends AbstractValidator {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private STRDocument strDoc = null;
	private String orgCd = "";
	private List<StrValidatable> validationItems = new ArrayList<StrValidatable>();
	private StrMessageTypeCdValidationItem messageTypeCdCk = new StrMessageTypeCdValidationItem();

	public final STRDocument getStrDocument() {
		return this.strDoc;
	}

	/**
	 * 
	 * @param strStream
	 * @throws IOException
	 * @throws XmlException
	 * @throws StrValidationException
	 * @throws XPathExpressionException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public StrValidator(InputStream strStream) throws IOException,
			XmlException, StrValidationException, XPathExpressionException,
			SAXException, ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		this(STRDocument.Factory.parse(strStream));
	}

	/**
	 * 
	 * @param strDoc
	 * @throws StrValidationException
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InstantiationException
	 * @throws IllegalAccessExce ption
	 * @throws ClassNotFoundException
	 */
	public StrValidator(STRDocument strDoc) throws StrValidationException,
			XPathExpressionException, IOException, SAXException,
			ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		
		// 기준정보 정형성 체크
		ArrayList<XmlValidationError> errorList = new ArrayList<XmlValidationError>();
		if (!strDoc.validate(new XmlOptions().setErrorListener(errorList))){
			String errorStr = errorList.get(0).getMessage();
			
			throw new StrValidationException(errorStr);
		}

		if (strDoc.toString().startsWith("<STR")) {
		}

//		if (!strDoc.validate())
//			throw new StrValidationException(
//					"(STRValidationError) : STR서식이  XML 스키마 정의에 위배됩니다.");

		this.strDoc = strDoc;
		this.orgCd = this.strDoc.getSTR().getOrganization().getOrgName()
				.getCode();
		this.refresh();
	}

	@Override
	public final void validate() throws StrValidationException, ValidationException {

		// STR MessageTypeCode Validation Check( '01' / '99' 가 아닐 경우 예외처리 )
		messageTypeCdCk.validate(this.strDoc);
		
		//기준정보 특정 조건 체크

		for (StrValidatable vi : this.validationItems) {
			vi.validate(this.strDoc);
		}
		if (logger.isLoggable(Level.FINEST))
			logger.finest(" STR Validation 검증 완료 ");
	}

	public final void refresh() throws XPathExpressionException, IOException,
			SAXException, ParserConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, STRConfigureException {
		ValidationInfo vi = STRConfigure.getInstance().getAgentInfo().getReportOrgInfo(
				this.orgCd).getValidationInfo();
		this.refresh(vi.getStrValidationConfig(),
				"/str-validation/item");
	};

	/**
	 * @param configFile
	 * @param xPathExpr
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Override
	public final void refresh(String configFile, String xPathExpr)
			throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		NodeList nodes = this.getNodeList(configFile, xPathExpr);

		if (null != nodes && 1 > nodes.getLength())
			throw new XPathExpressionException(
					"(Config설정오류) : Config.xml에 등록된 strValidationConfig 파일이 올바르지 않거나 Validation Item이 존재하지 않습니다.");

		for (int knx = 0; knx < nodes.getLength(); knx++) {
			String itemClassNm = nodes.item(knx).getTextContent();

			if (this.logger.isLoggable(Level.INFO)) {
				//this.logger.info("validation item class: " + itemClassNm);
			}

			this.validationItems.add((StrValidatable) Class
					.forName(itemClassNm).newInstance());
		}
	}

	/**
	 * STR 문서 안의 기관 코드를 얻어온다.<br/>
	 * (비고) validate() 메소드 호출 이후 본 메소드를 호출해야 문서 안의 기관 코드를 얻을 수 있다.
	 * @return STR 문서 상의 기관 코드
	 */
	public String getOrgCd() {
		return this.orgCd;
	}
}
