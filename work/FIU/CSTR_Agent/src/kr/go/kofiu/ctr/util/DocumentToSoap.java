package kr.go.kofiu.ctr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import kr.co.kofiu.www.ctr.encodedTypes.DocumentObject;
import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.ctr.conf.Configure;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentToSoap {

	/**
	 * 송신 프로세스에서 Soap 전문을 만들기 위해 사용한다.
	 * 
	 * 
	 * @param doc 		: 헤더,바디가 담긴 DocumentObject
	 * @return xmlStr	: 변환된 XML String
	 */
	public static String conventer(DocumentObject doc , byte[] att) {
		ItemAnonType[] headers = doc.getHeaders();
		ItemAnonType[] bodys = doc.getBodys();
		String xmlStr = "";
//		try {
//			xmlStr = setRequset(bodys,headers,att);
			xmlStr = setRequset_String(bodys, headers, att);
			
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return xmlStr;

	}
	
	public static String setEsbService_Request(String serviceMode){		
		try{			
			StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
			.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ctr=\"http://www.kofiu.co.kr/ctr/\">")
			.append("<soapenv:Header/>")
			.append("<soapenv:Body>")
			.append("	<ctr:processModuleCheck>");
			if(Controller.CHECKAGENT.equals(serviceMode)){
				sb.append("		<operation>"+"CTR"+serviceMode+"</operation>");
			}else{
				sb.append("		<operation>"+serviceMode+"</operation>");
			}
			sb.append("		<agentInfo>")
			.append("			<svrCd>CTR</svrCd>");
			if(Controller.HEARTBEAT.equals(serviceMode)){		
				sb.append("			<agentId></agentId>")
				.append("			<agentIp></agentIp>")
				.append("			<moduleVersion></moduleVersion>");
			}else if(Controller.CHECKAGENT.equals(serviceMode)){
				String agentId = Configure.getInstance().getAgentInfo().getId();
				String agentIp = Configure.getInstance().getAgentInfo().getIp();
				String agentVersion = Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR);
				sb.append("			<agentId>"+agentId+"</agentId>")
				.append("			<agentIp>"+agentIp+"</agentIp>")
				.append("			<moduleVersion>"+agentVersion+"</moduleVersion>");
			}else if(Controller.CTRMODULEUPDATE.equals(serviceMode)){			
				String agentVersion = Controller.getInstance().doControl(Controller.VERSION_GET, Controller.SERVICE_CTR);
				sb.append("			<agentId></agentId>")
				.append("			<agentIp></agentIp>")
				.append("			<moduleVersion>"+agentVersion+"</moduleVersion>");
			}
			sb.append("		</agentInfo>")
			.append("	</ctr:processModuleCheck>")
			.append("</soapenv:Body>")
			.append("</soapenv:Envelope>");
			return new String(sb);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String setRequset_String(ItemAnonType[] bodys, ItemAnonType[] headers, byte[] att){
		try{
			StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
			.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"	xmlns:ctr=\"http://www.kofiu.co.kr/ctr\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
			.append("<soapenv:Header/>")
			.append("<soapenv:Body>")
			.append("	<ctr:processRequestDocument soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">")
			.append("		<document xsi:type=\"enc:DocumentObject\" xmlns:enc=\"http://www.kofiu.co.kr/ctr/encodedTypes\">")
			.append("			<Headers xsi:type=\"enc:Map\">");
			for(int i = 0; i < headers.length ; i++){
				sb.append("			<item>")
				.append("				<key xsi:type=\"xsd:anyType\">" + headers[i].getKey() + "</key>")
				.append("				<value xsi:type=\"xsd:anyType\">" + headers[i].getValue() + "</value>")
				.append("			</item>");
			}
			sb.append("				</Headers>")
			.append("				<Bodys xsi:type=\"enc:Map\">");
			for(int i = 0; i < bodys.length ; i++){
				sb.append("			<item>")
				.append("				<key xsi:type=\"xsd:anyType\">" + bodys[i].getKey() + "</key>")
				.append("				<value xsi:type=\"xsd:anyType\">" + bodys[i].getValue() + "</value>")
				.append("			</item>");
			}
			sb.append("				</Bodys>")
			.append("				<Attachment xsi:type=\"xsd:base64Binary\">" + new String(att, "UTF-8") + "</Attachment>")
			.append("			</document>")
			.append("		</ctr:processRequestDocument>")
			.append("	</soapenv:Body>")
			.append("</soapenv:Envelope>");
			
			return new String(sb);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static String setRequset(ItemAnonType[] bodys, ItemAnonType[] headers, byte[] att) throws TransformerException {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse("./config/request.xml");
			Element rootElement = doc.getDocumentElement();	
			Element item;
			Element key;
			Element value;
			NodeList bodyNode = rootElement.getElementsByTagName("Bodys");
			for(int i = 0; i < bodys.length ; i++){
				item = doc.createElement("item");
				key = doc.createElement("key");
				key.setAttribute("xsi:type", "xsd:anyType"); //xsi:type="xsd:anyType"
				key.setTextContent((String) bodys[i].getKey());
				value = doc.createElement("value");
				value.setAttribute("xsi:type", "xsd:anyType");
				String temp = (String) bodys[i].getValue();
				value.setTextContent((String) bodys[i].getValue());
				
				item.appendChild(key);
				item.appendChild(value);
				bodyNode.item(0).appendChild(item);	
			}
			
			NodeList headerNode = rootElement.getElementsByTagName("Headers");
			for(int i = 0; i < headers.length ; i++){
				item = doc.createElement("item");
				key = doc.createElement("key");
				key.setAttribute("xsi:type", "xsd:anyType"); //xsi:type="xsd:anyType"
				key.setTextContent((String)headers[i].getKey());
				value = doc.createElement("value");
				value.setAttribute("xsi:type", "xsd:anyType");
				value.setTextContent((String)headers[i].getValue());
				item.appendChild(key);
				item.appendChild(value);
				headerNode.item(0).appendChild(item);	
			}
			
			NodeList attNode = rootElement.getElementsByTagName("Attachment");
			String str = new String(att, "UTF-8");
			attNode.item(0).setTextContent(new String(str));
			
			
			//DOMSource source = new DOMSource(doc);
			//StringWriter outWriter = new StringWriter();  
			//StreamResult result = new StreamResult( outWriter );
			org.apache.xml.security.Init.init();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    org.apache.xml.security.utils.XMLUtils.outputDOM(doc, baos, true);
		    //return baos.toByteArray();
			
		/*	TransformerFactory factory = TransformerFactory.newInstance(); 
			Templates transfomation = factory.newTemplates(source);
			Transformer transformer = transfomation.newTransformer();
			
//			Transformer transformer= factory.newTransformer();			
			transformer.transform(source, result); */
			
			//String output = outWriter.getBuffer().toString();
		    String temp = baos.toString();
			//return output; 
		    //System.out.println(new String(baos.toString().getBytes("euc-kr"), "ksc5601"));
			return baos.toString();
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 송신 프로세스에서 Soap Reply 를 받아서 결과를 추출한다.
	 * 
	 * 
	 * @param xmlDoc 	: Reply 전문
	 * @return result	: 결과값
	 */
	public static String getResult(String xmlDoc){
		String result;
		try {
//		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlDoc));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
	
		parser = dbf.newDocumentBuilder();
		Document doc = parser.parse(is);
		Element rootElement = doc.getDocumentElement();	
		NodeList responseNode = rootElement.getElementsByTagName("processRequestDocumentResult");
//		result = responseNode.item(0).getTextContent();
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "-1";
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "-1";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "-1";
		}
		
//		return result;
		return "";

	}
	
	public static void main(String args[]){
		String xml = "<NS1:Envelope xmlns:NS1=\"http://schemas.xmlsoap.org/soap/envelope/\">    <NS1:Body>        <NS2:processRequestDocumentResponse xmlns:NS2=\"http://www.kofiu.co.kr/ctr\">            <processRequestDocumentResult>sdfsdfsdfsdfsdfsdf</processRequestDocumentResult>        </NS2:processRequestDocumentResponse>    </NS1:Body></NS1:Envelope>";
		String result = getResult(xml);
		System.out.println(result);
	}

}
