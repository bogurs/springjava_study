package kr.go.kofiu.ctr.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import krGovKofiuDataRootSchemaModule10.ResponseCurrencyTransactionReportDocument;
import krGovKofiuDataRootSchemaModule10.ResponseCurrencyTransactionReportType;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 접수증서를 FlatFile 형태로 변환함.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class XMLtoFlatFile {
	/**
	 * XML로 된 접수 증서를 이용하여 FlatFile형태의 데이타를 만든다. 
	 * @param RepXML
	 * @return
	 */
	public static String checkXMLAndConvertToFlatFile(String RepXML)  {
		return checkXMLAndConvertToFlatFile(new String(RepXML));
	}
	
	/**
	 *	XML로 된 접수 증서를 이용하여 FlatFile형태의 데이타를 만든다. 
	 * @param RepXML
	 * @return
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public static String checkXMLAndConvertToFlatFile(byte[] RepXML) throws XmlException, IOException  {
		ResponseCurrencyTransactionReportDocument ResponseDoc = generateResponseDoc(RepXML);
		return convertToFlatFile(ResponseDoc);
	}

	/**
	 * 
	 * @param ResponseDoc
	 * @return
	 */
	public static String convertToFlatFile(ResponseCurrencyTransactionReportDocument ResponseDoc)  {
        String flatfile = "CTRSTART||";
		ResponseCurrencyTransactionReportType RepRpt = ResponseDoc.getResponseCurrencyTransactionReport();
		flatfile += RepRpt.getMessageCode().getStringValue() + "||";
		flatfile += RepRpt.getReportTypeCode().getStringValue() + "||";
		flatfile += RepRpt.getReportAdministrationPartyCode().getStringValue() + "||";
		flatfile += RepRpt.getDocumentID().getStringValue() + "||";
		flatfile += RepRpt.getDate() + "||";
		flatfile += RepRpt.getReceiptDateTime() + "||";
		flatfile += RepRpt.getTypeCode().getStringValue() + "||";
		flatfile += RepRpt.getType().getStringValue() + "||";
        
		return flatfile + "CTREND";
	}
	
	/**
	 * 
	 * @param RepXML
	 * @return
	 * @throws XmlException
	 * @throws IOException
	 */
	public static ResponseCurrencyTransactionReportDocument generateResponseDoc(byte[] RepXML) throws XmlException, IOException  {
		ArrayList validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setValidateOnSet();
        validationOptions.setErrorListener(validationErrors);
        ResponseCurrencyTransactionReportDocument ResponseDoc = 
        	ResponseCurrencyTransactionReportDocument.Factory.parse(new ByteArrayInputStream(RepXML) , validationOptions);
        
		return ResponseDoc;
	}
	
}
