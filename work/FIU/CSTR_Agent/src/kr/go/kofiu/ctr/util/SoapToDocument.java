package kr.go.kofiu.ctr.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.co.kofiu.www.ctr.encodedTypes.DocumentObject;
import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.JmsSoapInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class SoapToDocument {
	
	public static String sndResultParser(String pollingResult){
		try {
			String result;	
	
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(pollingResult));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
	
			parser = dbf.newDocumentBuilder();
	
			Document doc;
			
			doc = parser.parse(is);			
			Element rootElement = doc.getDocumentElement();
			NodeList resultNode = rootElement.getElementsByTagName("processRequestDocumentResult");
			if(resultNode.getLength() == 0){
				result = JmsSoapInfo.SNR_ERROR_MSG;
				return result;
			}
			result = resultNode.item(0).getTextContent();	
			return result; //"OK"
		
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 수신 프로세스에서 DocumentObject(ebXML형식) SOAP 전문을 만들기 위해 사용한다.
	 * 
	 * 
	 * @param pollingResult 		: 폴링한 접수 증서를 포함한 ebXML SOAP 전문
	 * @return DocumentObject	: 변환된 XML String
	 */
	public static DocumentObject conventer(String pollingResult){
		DocumentObject docobj = new DocumentObject();
		/*ArrayList<ItemAnonType> arrBody = new ArrayList<ItemAnonType>();
		ArrayList<ItemAnonType> arrHeader = new ArrayList<ItemAnonType>();*/
		ItemAnonType[] arrheader;
		ItemAnonType[] arrbody;
		
		ItemAnonType[] header;
		ItemAnonType[] body;
		try {
			
			InputSource is = new InputSource(new StringReader(pollingResult));
			is.setEncoding("UTF-8");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();	
				
			NodeList headerNode = rootElement.getElementsByTagName("Headers").item(0).getChildNodes();
			arrheader = new ItemAnonType[headerNode.getLength()];
			for(int i = 0; i < headerNode.getLength() ; i++){
				Node itemNode = headerNode.item(i);
				if(itemNode.getNodeName() == "#text"){
					continue;
				}
				ItemAnonType temp = new ItemAnonType();
				if(itemNode.getChildNodes().getLength()==2){
					temp.setKey(itemNode.getChildNodes().item(0).getTextContent());
					temp.setValue(itemNode.getChildNodes().item(1).getTextContent());
				}else{
					temp.setKey(itemNode.getChildNodes().item(1).getTextContent());
					temp.setValue(itemNode.getChildNodes().item(3).getTextContent());
				}
				//arrHeader.add(temp);
				arrheader[i] = temp;
			}
			//header = new ItemAnonType[arrHeader.size()];
			header = new ItemAnonType[arrheader.length];
			for(int i = 0 ; i < header.length ; i++){
				//header[i] = arrHeader.get(i);
				header[i] = arrheader[i];
			}
			docobj.setHeaders(header);
			
			NodeList bodyNode = rootElement.getElementsByTagName("Bodys").item(0).getChildNodes();
			
			arrbody = new ItemAnonType[bodyNode.getLength()];
			for(int i = 0; i < bodyNode.getLength() ; i++){
				Node itemNode = bodyNode.item(i);
				if(itemNode.getNodeName() == "#text"){
					continue;
				}
				ItemAnonType temp = new ItemAnonType();
				if(itemNode.getChildNodes().getLength()==2){
					temp.setKey(itemNode.getChildNodes().item(0).getTextContent());
					temp.setValue(itemNode.getChildNodes().item(1).getTextContent());
				}else{
					temp.setKey(itemNode.getChildNodes().item(1).getTextContent());
					temp.setValue(itemNode.getChildNodes().item(3).getTextContent());
				}
				//arrBody.add(temp);
				arrbody[i] = temp;
				
			}
			//body = new ItemAnonType[arrBody.size()];
			body = new ItemAnonType[arrbody.length];
			for(int i = 0 ; i < body.length ; i++){
				body[i] = arrbody[i];
			}
			docobj.setBodys(body);
			
			byte[] attach;
			NodeList attachNode = rootElement.getElementsByTagName("Attachment");
			if(attachNode.item(0).getNodeName().trim() !="#text"){
				attach = attachNode.item(0).getTextContent().getBytes();
			}else{
				attach = attachNode.item(1).getTextContent().getBytes();
			}
			docobj.setAttachment(attach);
			
//			for(int i = 0 ; i < header.length ; i++){
//				System.out.println("Header "+i+" Key = " + header[i].getKey());
//				System.out.println("Header "+i+" Value = " + header[i].getValue());
//			}
//			for(int i = 0 ; i < body.length ; i++){
//				System.out.println("Body "+i+" Key = " + body[i].getKey());
//				System.out.println("Body "+i+" Value = " + body[i].getValue());
//			}

		}catch ( Exception e){
			e.printStackTrace();
		}
		
		return docobj;
		
	}
	
	private static String readFileAsString(String filePath) throws IOException {
	      StringBuffer fileData = new StringBuffer();
	      //BufferedReader reader = new BufferedReader(new FileReader(filePath));
	      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF8"));
	      char[] buf = new char[1024];
	      int numRead=0;
	      while((numRead=reader.read(buf)) != -1){
	          String readData = String.valueOf(buf, 0, numRead);
	          fileData.append(readData);
	      }
	      reader.close();
	      return fileData.toString();
	  }
	
	public static void main(String[] args) throws IOException{
		
		String data = readFileAsString("C:/Users/Song/Documents/file/RCVSample_result2.txt");
		//data = data.substring(1);
//		data = data.replace("\n", "");
//		data = data.replace("\r", "");	
		
		DocumentObject doc = conventer(data);
		System.out.println("Attach :\n" + new String(doc.getAttachment()));
	}

	public static String pollingRespParser(String processRequestDocumemt) {
		// 보고 증서 수신 후 String 필드 값 processRequestDocumentResult 처리 과정
		
		String processRequestDocumentResult;
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(processRequestDocumemt));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;

			parser = dbf.newDocumentBuilder();

			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			NodeList processRequestDocumentResultNode = rootElement.getElementsByTagName("processRequestDocumentResult");
			processRequestDocumentResult = processRequestDocumentResultNode.item(0).getTextContent();
			
			return processRequestDocumentResult;
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

}
