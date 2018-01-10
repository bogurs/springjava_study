package kr.go.kofiu.ctr.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import krGovKofiuDataRootSchemaModule11.ResponseCurrencyTransactionReportDocument;
import krGovKofiuDataRootSchemaModule11.ResponseCurrencyTransactionReportType;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���������� FlatFile ���·� ��ȯ��.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class XMLtoFlatFile089 {
	/**
	 * XML�� �� ���� ������ �̿��Ͽ� FlatFile������ ����Ÿ�� �����. 
	 * @param RepXML
	 * @return
	 */
	public static String checkXMLAndConvertToFlatFile(String RepXML)  {
		return checkXMLAndConvertToFlatFile(new String(RepXML));
	}
	
	/**
	 *	XML�� �� ���� ������ �̿��Ͽ� FlatFile������ ����Ÿ�� �����. 
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
