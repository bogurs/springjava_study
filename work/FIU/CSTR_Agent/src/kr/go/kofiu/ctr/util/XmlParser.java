package kr.go.kofiu.ctr.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.go.kofiu.ctr.conf.EsbCheckServiceInfo;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlParser {

	public static EsbCheckServiceInfo getCheckAgentInfo(String xmlDoc)
			throws ParserConfigurationException, SAXException, IOException {		
		String MemoryData;
		String TimeResult;
		String IPResult;
		String ModuleVersionResult;
		String TimeValue;
		String moduleVersion;
		String resultCode;
		String resultMessage;

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlDoc));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;

		parser = dbf.newDocumentBuilder();

		Document doc = parser.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList MemoryDataNode = rootElement.getElementsByTagName("MemoryData");
		MemoryData = MemoryDataNode.item(0).getTextContent();
		NodeList TimeResultNode = rootElement.getElementsByTagName("TimeResult");
		TimeResult = TimeResultNode.item(0).getTextContent();
		NodeList IPResultNode = rootElement.getElementsByTagName("IPResult");
		IPResult = IPResultNode.item(0).getTextContent();
		NodeList ModuleVersionResultNode = rootElement.getElementsByTagName("ModuleVersionResult");
		ModuleVersionResult = ModuleVersionResultNode.item(0).getTextContent();
		NodeList TimeValueNode = rootElement.getElementsByTagName("TimeValue");
		TimeValue = TimeValueNode.item(0).getTextContent();
		NodeList moduleVersionNode = rootElement.getElementsByTagName("moduleVersion");
		moduleVersion = moduleVersionNode.item(0).getTextContent();
		NodeList resultCodeNode = rootElement.getElementsByTagName("resultCode");
		resultCode = resultCodeNode.item(0).getTextContent();
		NodeList resultMessageNode = rootElement.getElementsByTagName("resultMessage");
		resultMessage = resultMessageNode.item(0).getTextContent();

		EsbCheckServiceInfo ctx = new EsbCheckServiceInfo(MemoryData, TimeResult, IPResult, ModuleVersionResult, TimeValue, moduleVersion, resultCode, resultMessage);
		
		return ctx;
	}

	public static EsbCheckServiceInfo getModuleUpdateInfo(String xmlDoc) throws ParserConfigurationException, SAXException, IOException {
		String RPT_MDL_VER;//
		String RPT_MDL_BLOB_MDL_LIB;//
		String RPT_MDL_BLOB_MDL_LIB_NM;
		String RPT_MDL_BLOB_MDL_LIB_SIZE;
		String RPT_MDL_BLOB_CONFIG;//
		String RPT_MDL_BLOB_CONFIG_NM;
		String RPT_MDL_BLOB_CONFIG_SIZE;
		String RPT_MDL_BLOB_PBL_KEY;//
		String RPT_MDL_BLOB_PBL_KEY_NM;
		String RPT_MDL_BLOB_PBL_KEY_SIZE;
		
		String resultCode;
		String resultMessage;

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlDoc));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;

		parser = dbf.newDocumentBuilder();

		Document doc = parser.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList RPT_MDL_VERNode = rootElement.getElementsByTagName("RPT_MDL_VER");
		RPT_MDL_VER = RPT_MDL_VERNode.item(0).getTextContent();
		NodeList RPT_MDL_BLOB_MDL_LIBNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_MDL_LIB");
		RPT_MDL_BLOB_MDL_LIB = RPT_MDL_BLOB_MDL_LIBNode.item(0).getTextContent();
		//NodeList RPT_MDL_BLOB_CONFIGNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_CONFIG");
		//RPT_MDL_BLOB_CONFIG = RPT_MDL_BLOB_CONFIGNode.item(0).getTextContent();
		RPT_MDL_BLOB_CONFIG = null;
		NodeList RPT_MDL_BLOB_PBL_KEYNode = rootElement.getElementsByTagName("RPT_MDL_BLOB_PBL_KEY");
		RPT_MDL_BLOB_PBL_KEY = RPT_MDL_BLOB_PBL_KEYNode.item(0).getTextContent();
		
		
		NodeList resultCodeNode = rootElement.getElementsByTagName("resultCode");
		resultCode = resultCodeNode.item(0).getTextContent();
		NodeList resultMessageNode = rootElement.getElementsByTagName("resultMessage");
		resultMessage = resultMessageNode.item(0).getTextContent();
		
		EsbCheckServiceInfo ctx = new EsbCheckServiceInfo(RPT_MDL_VER, RPT_MDL_BLOB_MDL_LIB, RPT_MDL_BLOB_CONFIG, RPT_MDL_BLOB_PBL_KEY, resultCode, resultMessage);

		return ctx;
	}

	public static EsbCheckServiceInfo getHeartBeatResult(String esbSoapService) throws SAXException, IOException, ParserConfigurationException {
		// HeartBeat 결과 값 가져오기 OK 또는 에러 메시지
		String check;
		
		String resultCode;
		String resultMessage;

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(esbSoapService));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;

		parser = dbf.newDocumentBuilder();

		Document doc = parser.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList checkNode = rootElement.getElementsByTagName("check");
		check = checkNode.item(0).getTextContent();
		
		NodeList resultCodeNode = rootElement.getElementsByTagName("resultCode");
		resultCode = resultCodeNode.item(0).getTextContent();
		NodeList resultMessageNode = rootElement.getElementsByTagName("resultMessage");
		resultMessage = resultMessageNode.item(0).getTextContent();
		
		EsbCheckServiceInfo ctx = new EsbCheckServiceInfo(check, resultCode, resultMessage);

		return ctx;
	}
}
