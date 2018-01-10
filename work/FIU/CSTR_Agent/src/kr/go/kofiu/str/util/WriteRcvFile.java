package kr.go.kofiu.str.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gpki.gpkiapi.exception.GpkiApiException;

import kr.co.kofiu.www.ctr.encodedTypes.ItemAnonType;
import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.str.security.GpkiUtil;
import kr.go.kofiu.str.summary.StrDestSummary;
import kr.go.kofiu.str.summary.UtilSummary;

public class WriteRcvFile {
	private static boolean isDebug = false;
		
	public static UtilSummary parseUtilSummary(String xmlDoc, String svrType) throws AgentException {	
		
		UtilSummary us = new UtilSummary();
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlDoc));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
	
			parser = dbf.newDocumentBuilder();
	
			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			
			if(svrType.equals(Controller.CHECKAGENT)){
				NodeList ModuleVersionResultNode = rootElement.getElementsByTagName("ModuleVersionResult");
				us.setModuleVersionResult( ModuleVersionResultNode.item(0).getTextContent() );
				
				NodeList moduleVersionNode = rootElement.getElementsByTagName("moduleVersion");
				us.setModuleVersion(moduleVersionNode.item(0).getTextContent());
				
				NodeList errorCd = rootElement.getElementsByTagName("resultCode");
				us.setErrorCd(errorCd.item(0).getTextContent());
				
				NodeList errorMsg = rootElement.getElementsByTagName("resultMessage");
				us.setErrorMsg(errorMsg.item(0).getTextContent());
			}else if(svrType.equals(Controller.HEARTBEAT)
					||svrType.equals(Controller.USERCHECK)
					||svrType.equals(Controller.STRMODULEUPDATE)){
				
				NodeList errorCd = rootElement.getElementsByTagName("resultCode");
				us.setErrorCd(errorCd.item(0).getTextContent());
				
				NodeList errorMsg = rootElement.getElementsByTagName("resultMessage");
				us.setErrorMsg(errorMsg.item(0).getTextContent());
			}
		}catch (Exception e){
//			e.printStackTrace();
			throw new AgentException(e);
		}
		return us;
	}
	
	/**
	 * 최초의 값에서 필요한 기준정보를 가져온다. ( 송신 프로세스 용 )
	 * 
	 * @param soapXml
	 * @return
	 * @throws AgentException
	 */
	public static StrDestSummary parseSTRDocument(InputStream soapXml) throws AgentException{
		try {
			StrDestSummary strdest = new StrDestSummary();
		
			InputSource is = new InputSource(soapXml);
			//is.setEncoding("UTF-8");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			Node nl_org = rootElement.getElementsByTagName("Organization").item(0).getFirstChild();
			while(nl_org!=null){
				if(nl_org.getNodeName().equals("OrgName")){
					strdest.setOrgCode(nl_org.getAttributes().item(0).getNodeValue());
				}
				nl_org = nl_org.getNextSibling();
			}
			
			Node nl_master = rootElement.getElementsByTagName("Master").item(0).getFirstChild();
			while(nl_master!=null){
				if(nl_master.getNodeName().equals("OrgDocNum")){
					strdest.setOrgDocNum(nl_master.getTextContent());
				}else if(nl_master.getNodeName().equals("FiuDocNum")){
					strdest.setFiuDocNum(nl_master.getTextContent());
				}else if(nl_master.getNodeName().equals("MessageTypeCode")){
					strdest.setMessageTypeCode(nl_master.getTextContent());
				}else if(nl_master.getNodeName().equals("DocSendDate")){
					strdest.setDocSendDate( nl_master.getTextContent());
				}
				nl_master = nl_master.getNextSibling();
			}
			System.out.println("");
			
			return strdest;
		}catch (org.xml.sax.SAXException e){
			throw new AgentException(e);
		}catch (Exception e){
			e.printStackTrace();
			throw new AgentException(e);
		}

	}
	
	public static void createLogfile(String filePath , String fileName) throws IOException{
		if(isDebug){
			System.out.println("Log File = " + filePath + "/" + fileName);
		}
        FileWriter fw = new FileWriter(filePath + "/" + fileName); // 절대주소 경로 가능
        BufferedWriter bw = new BufferedWriter(fw);
        String str = ".";
        bw.write(str);
        bw.close();
	}
	
	/**
	 * 수신된 SOAP 전문을 Parse 한다. ( 송신 프로세스 용 )
	 * 
	 * @param soapXml
	 * @return
	 * @throws AgentException
	 */
	public static STRResponseDocument parseSoapMessage(String soapXml) throws AgentException{
		STRResponseDocument strdoc = new STRResponseDocument();
		try {
			
			InputSource is = new InputSource(new StringReader(soapXml));
			//is.setEncoding("UTF-8");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			System.out.println("");
				
			// parse 내용을 저장할 ItemAnonType를 가져온다.
			
			NodeList bodyNode = rootElement.getElementsByTagName("Bodys").item(0).getChildNodes();
			System.out.println(bodyNode.getLength());
			ItemAnonType[] arrbody = new ItemAnonType[bodyNode.getLength()];
			NodeList headerNode = rootElement.getElementsByTagName("Headers").item(0).getChildNodes();
			ItemAnonType[] arrheader = new ItemAnonType[headerNode.getLength()];
			
			
			// Header Parsing
			int headerSize = 0;
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
				
				arrheader[headerSize] = temp;
				headerSize++;
			}
			
			ItemAnonType[] header = new ItemAnonType[headerSize];
			for(int i = 0 ; i < header.length ; i++){
				//header[i] = arrHeader.get(i);
				header[i] = arrheader[i];
			}
			strdoc.setHeader(header);
			
			// Body Parsing
			int bodySize = 0;	
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
				
				arrbody[bodySize] = temp;
				bodySize++;
			}
			
			ItemAnonType[] body = new ItemAnonType[bodySize];
			for(int i = 0 ; i < body.length ; i++){
				body[i] = arrbody[i];
			}
			strdoc.setBody(body);
			
			/*byte[] attach;
			NodeList attachNode = rootElement.getElementsByTagName("Attachment");
			System.out.println(attachNode.item(0).getNodeName());
			if(attachNode.item(0).getNodeName().trim() !="#text"){
				attach = attachNode.item(0).getTextContent().getBytes();
			}else{
				attach = attachNode.item(1).getTextContent().getBytes();
			}
			InputStream en_attach = new ByteArrayInputStream(attach);
			
			// 가져온 데이터를 Base64 Decode, 서명검증 한다.
			byte[] plainText = readAndDecrypt(en_attach);
			
			// 가져온 데이터 앞뒤로 값을 붙혀넣는다.
			//String strvalue = "STRSTART||" + new String(plainText) + "STREND";
			strdoc.setAttachment(new String(plainText));*/
			
			
			if(isDebug){
				System.out.println("Header==================================");
				ItemAnonType[] headerTemp = strdoc.getHeader();
				for(int i = 0 ; i < headerTemp.length ; i++){
					System.out.println("Header "+i+" Key = " + headerTemp[i].getKey());
					System.out.println("Header "+i+" Value = " + headerTemp[i].getValue());
				}
				System.out.println("  Body==================================");
				ItemAnonType[] bodyTemp = strdoc.getBody();
				for(int i = 0 ; i < bodyTemp.length ; i++){
					System.out.println("Body "+i+" Key = " + bodyTemp[i].getKey());
					System.out.println("Body "+i+" Value = " + bodyTemp[i].getValue());
				}
				System.out.println("Attach==================================");
				System.out.println(strdoc.getAttachment());
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new AgentException(e);
		}

		return strdoc;
	}
	
	/**
	 * 수신된 SOAP 전문을 Parse 한다. ( 수신 프로세스 용 )
	 * 
	 * @param soapXml
	 * @return
	 * @throws AgentException
	 */
	public static STRResponseDocument parseDocumentMessage(String soapXml) throws AgentException{
		String BODYS = "";
		String HEADERS = "";
		String ATTACHMENT = "";
		
		STRResponseDocument strdoc = new STRResponseDocument();
		try {
			
			InputSource is = new InputSource(new StringReader(soapXml));
			is.setEncoding("UTF-8");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(is);
			Element rootElement = doc.getDocumentElement();
			System.out.println("");
			
			// parse 내용을 저장할 ItemAnonType를 가져온다.
			if(rootElement.getElementsByTagName("Bodys").getLength()!=0){
				BODYS = "Bodys";
				HEADERS = "Headers";
				ATTACHMENT = "Attachment";
			}else{
				BODYS = "NS2:Bodys";
				HEADERS = "NS3:Headers";
				ATTACHMENT = "NS4:Attachment";
			}
			
			NodeList bodyNode = rootElement.getElementsByTagName(BODYS).item(0).getChildNodes();
//			System.out.println(bodyNode.getLength());
			ItemAnonType[] arrbody = new ItemAnonType[bodyNode.getLength()];
			NodeList headerNode = rootElement.getElementsByTagName(HEADERS).item(0).getChildNodes();
			ItemAnonType[] arrheader = new ItemAnonType[headerNode.getLength()];
			
			
			// Header Parsing
			int headerSize = 0;
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
				
				arrheader[headerSize] = temp;
				headerSize++;
			}
			
			ItemAnonType[] header = new ItemAnonType[headerSize];
			for(int i = 0 ; i < header.length ; i++){
				//header[i] = arrHeader.get(i);
				header[i] = arrheader[i];
			}
			strdoc.setHeader(header);
			
			// Body Parsing
			int bodySize = 0;	
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
				
				String tempKey = (String)temp.getKey();
				if(tempKey.equals("OrgDocNum")){
					strdoc.setOrgDocNum((String)temp.getValue());
				}else if(tempKey.equals("DocSendDate")){
					strdoc.setDocSendDt((String)temp.getValue());
				}else if(tempKey.equals("AttachmentfileName")){
					strdoc.setAttachFileNm((String)temp.getValue());
				}
				
				arrbody[bodySize] = temp;
				bodySize++;
			}
			
			ItemAnonType[] body = new ItemAnonType[bodySize];
			for(int i = 0 ; i < body.length ; i++){
				body[i] = arrbody[i];
			}
			strdoc.setBody(body);
			
			byte[] attach;
			NodeList attachNode = rootElement.getElementsByTagName(ATTACHMENT);
			if(attachNode.item(0).getNodeName().trim() !="#text"){
				attach = attachNode.item(0).getTextContent().getBytes();
			}else{
				attach = attachNode.item(1).getTextContent().getBytes();
			}
			//InputStream en_attach = new ByteArrayInputStream(attach);
			
			// 가져온 데이터를 Base64 Decode 한다
			String temp = new String(attach);
			byte[] plainText = Base64.decodeBase64(attach);
			String tempDecode = new String(plainText);
			// 가져온 데이터를 Base64 Decode, 서명검증 한다.
			//byte[] plainText = readAndDecrypt(attach);
			
			strdoc.setAttachment(new String(plainText));
			
		}catch (Exception e){
			e.printStackTrace();
			throw new AgentException(e);
		}

		return strdoc;
	}
	/**
	 * InputStream에서 정보를 읽어 복호화한다.
	 * 
	 * @param is
	 *            Remote Adapter가 받은, FIU가 암호화하여 보낸 data
	 * @return 전자 서명 및 복호화된 data
	 * @throws IOException
	 * @throws SAXException
	 * @throws GpkiApiException
	 */
	public final static byte[] readAndDecrypt(byte[] data) throws IOException,
			SAXException, GpkiApiException {
		
		byte[] plainText = GpkiUtil.getInstance().decryptSigned(data);
		return plainText;
	}

}
