package kr.go.kofiu.str.validation;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * STR ����ȭ�� ���� �߻� Ŭ����.
 *
 */
public abstract class AbstractValidator {
	public abstract void refresh(String configFile, String xPathExpr)
			throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, InstantiationException,
			IllegalAccessException, ClassNotFoundException;

	public abstract void validate() throws ValidationException;

	public NodeList getNodeList(String configFile, String xPathExpr)
			throws SAXException, IOException, ParserConfigurationException,
			XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		Document doc = factory.newDocumentBuilder().parse(configFile);

		XPathFactory xfactory = XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		XPathExpression expr = xpath.compile(xPathExpr);
		return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	}
}
