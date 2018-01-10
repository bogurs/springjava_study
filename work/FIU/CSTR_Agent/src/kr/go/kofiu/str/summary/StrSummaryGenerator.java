package kr.go.kofiu.str.summary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.util.RetryCommand;
import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR;
import kr.go.kofiu.str.STRDocument.STR.Master;
import kr.go.kofiu.str.STRDocument.STR.Organization;
import kr.go.kofiu.str.conf.STRAgentInfo;
import kr.go.kofiu.str.conf.STRConfigure;
import kr.go.kofiu.str.conf.STRConfigureException;
import kr.go.kofiu.str.security.GpkiUtil;
import kr.go.kofiu.str.util.DateUtil;
import kr.go.kofiu.str.validation.ValidationException;
import kr.go.kofiu.str.validation.str.StrValidationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.xml.sax.SAXException;

import com.gpki.gpkiapi.exception.GpkiApiException;


/**
 * ���� ����� STR XML�� ������� ��� ������ �����ϰ�, STR�� FIU�� �������� ��� �ִ� ���� Ű�� ��ȣȭ ���� �� Base64
 * Encoding�Ͽ� ��� ������ ������ ����, �̸� ���� ��� ������ �� ���� Ű�� ���� �����Ѵ�.
 * 
 * @author �����
 */
public final class StrSummaryGenerator {
	private String securityCode = "";
	private String GUID = null;
	private STRDocument strDoc = null;
	private String orgCd = "";
	private String fileNm = "";
	private int seq = 0;
	private int maxseq = 0;
	
	

	/**
	 * ���� ����� ���� STR XML ������ ǥ���ϴ� STRDocument XML Beans ��ü�� ������� ��ü�� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param strStream
	 *            ���� ����� ���� STR XML ������ ���� InputStream ��ü
	 * @throws StrValidationException
	 *             XML�� STR XML Schema�� �ٸ� ���
	 */
	public StrSummaryGenerator(STRDocument strDoc, String orgCd, String fileNm, String GUID, int seq, int maxseq)
			throws StrValidationException {
		//kr.go.kofiu.str.STRDocument
		ArrayList<XmlValidationError> errorList = new ArrayList<XmlValidationError>();
		if (!strDoc.validate(new XmlOptions().setErrorListener(errorList))){
			String errorStr = errorList.get(0).getMessage();
			
			/*String errorStr = "";
			for(int i = 0 ; i < errorList.size() ; i++){
				String errorTemp = errorList.get(i).getMessage();
				//errorTemp = errorTemp.substring(errorTemp.lastIndexOf(":"), errorTemp.length());
				if(i > 0){
					errorStr = errorStr + " || " + errorTemp;
				}else{
					errorStr = errorTemp;
				}
			}*/
			//throw new StrValidationException("(STRValidationError) : STR������  XML ��Ű�� ���ǿ� ����˴ϴ�." + errorStr);
			throw new StrValidationException(errorStr);
		}
		this.strDoc = strDoc;
		this.orgCd = orgCd;
		this.fileNm = fileNm;
		this.seq = seq;
		this.maxseq = maxseq;
		this.GUID = GUID;
	}

	/**
	 * ���� ����� ���� STR XML ������ ���� InputStream ��ü�� ������� ��ü�� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param strStream
	 *            ���� ����� ���� STR XML ������ ���� InputStream ��ü
	 * @throws XmlException
	 *             XML�� parsing�ϰų� compile�� �� ���� �߻�
	 * @throws IOException
	 *             XML�� �д� ���� ���� �߻�
	 * @throws ValidationException
	 *             XML�� STR XML Schema�� �ٸ� ���
	 */
	public StrSummaryGenerator(InputStream strStream ,String orgCd, String fileNm, String GUID, int seq, int maxseq) throws XmlException,
			IOException, StrValidationException {
		this(STRDocument.Factory.parse(strStream), orgCd, fileNm, GUID, seq,  maxseq);
		
	}

	
	/**
	 * STR XML�κ��� ��� header / body�� �����ϰ� ����� ���� STR XML�� Base64 Encoding�Ͽ� ���
	 * ������ ������ �� ���� ����� ����� �������� ���� �����Ѵ�.
	 * 
	 * @return ���� ������ STR ��� ����(header + body + Base64 Encoding�� STR)
	 * @throws SAXException
	 * @throws IOException
	 * @throws GpkiApiException
	 */
	/*public final byte[] generate() throws IOException,
			SAXException, GpkiApiException, STRConfigureException {
		
	}*/

	/**
	 * @throws Exception 
	 * STR�� PKCS#7 ������ ������ ���� �� �̸� Base64 Encoding�Ѵ�.
	 * 
	 * @return PKCS#7 ������ ������ ������� �� Base64 ���ڵ��� STR
	 * @throws GpkiApiException
	 *             PKCS#7 ������ ���� ���� �� ����
	 * @throws IOException
	 *             ������, ���� ���� �д� ���� ����
	 * @throws SAXException
	 *             ���� ���� XML �д� ���� ����
	 * @throws  
	 */
	public final String makeAttachment(String orgCd) throws Exception {
		byte[] signedData = null;
		if(maxseq == 1){
			String data = this.strDoc.toString();
			
			if(securityCode.equals("00")){
				signedData = Base64.encodeBase64(data.getBytes());
			}else if(securityCode.equals("01")){
				signedData = Base64.encodeBase64(GpkiUtil.getInstance().encrypt(data.getBytes(), orgCd));
			}else if(securityCode.equals("02")){
				signedData = GpkiUtil.getInstance().makeSignature(data.getBytes());
			}else if(securityCode.equals("03")){
				byte[] encryptData = GpkiUtil.getInstance().encrypt(data.getBytes(), orgCd);
				signedData = GpkiUtil.getInstance().makeSignature(encryptData);
			}
		}else{
			signedData = readData(fileNm);	
		}
		
		//�׽�Ʈ
		return "<Attachment xsi:type=\"xsd:base64Binary\">"
		//+ GpkiCodec.getInstance().doBase64Encoding(this.strDoc.toString().getBytes())
		//+ "<![CDATA[" + new String(Base64.encodeBase64(this.strDoc.toString().getBytes())) + "]]>"
		+ "<![CDATA[" + new String(signedData) + "]]>"
		+ "</Attachment>";
	}

	public final String makeHeader(String orgCd) throws IOException, SAXException, AgentException, STRConfigureException {
		STRAgentInfo ai = STRConfigure.getInstance().getAgentInfo();
		STR str = this.strDoc.getSTR();
		//�ʼ��� ����
		
		// MessageTypeCode üũ
		if(str.getMaster().getMessageTypeCode().toString().equals(null)||str.getMaster().getMessageTypeCode().toString().length()==0){
			throw new AgentException("�������� �ʼ����� MessageTypeCode �� �����Ǿ����ϴ�.");
		}
		
		
		StringBuffer header = new StringBuffer("<Headers>");
		
        		
		
		header.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">GlobalUniqueID</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+GUID+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">InterfaceID</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+"FIUF9998"+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">SecurityCode</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+securityCode+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">CurrentFileCount</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+seq+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">TotalFileCount</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+maxseq+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">MessageCreationDateTime</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+DateUtil.getDateTime(DateUtil.yyyyMMddHHmmss)+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">ReportFormVersionID</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+str.getVersion()+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">ModuleVersionID</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+this.getModuleVersionId()+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">ReportAdministrationPartyIPName</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+ai.getIp()+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">MessageTypeCode</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+str.getMaster().getMessageTypeCode().toString()+"</value>")
			.append("</item>")
			.append("<item>")
			.append("	<key xsi:type=\"xsd:anyType\">AgentID</key>")
			.append("	<value xsi:type=\"xsd:anyType\">"+ai.getId()+"</value>")
			.append("</item>")
			
			.append("</Headers>");

		
		return header.toString();
	}

	/**
	 * ��� ���� Body�� �����Ѵ�.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws STRConfigureException
	 * @throws AgentException 
	 */
	public final String makeBody() throws STRConfigureException, IOException,
			SAXException, AgentException {
		
		Organization org = this.strDoc.getSTR().getOrganization();
		Master master = this.strDoc.getSTR().getMaster();
		String FormerOrgDocNum = null == master.getFormerOrgDocNum() ? "" : master.getFormerOrgDocNum();
		String FormerFiuDocNum = null == master.getFormerFiuDocNum() ? "" : master.getFormerFiuDocNum();
		//String fileNm = "STR" + master.getDocSendDate().substring(2, 6)	+ master.getOrgDocNum() + master.getFiuDocNum() + ".xml";
		StringBuffer body = new StringBuffer("<Bodys>");
		
		// �ִ밪�� 1�̻��϶� ó������ �߰� (15.04.14)
		String tempFileNm ="";
		if(maxseq==1){
			tempFileNm = fileNm;
		}else{
			tempFileNm = fileNm.substring(fileNm.lastIndexOf("/")+1, fileNm.lastIndexOf("_."));
		}
		// �ʼ��� ���� ���� 
		
		//�������ڵ�(�߰����ڵ�) üũ
		if(master.getOrgDocNum().equals(null)||master.getOrgDocNum().length()==0){
			throw new AgentException("�������� �ʼ����� OrgDocNum �� �����Ǿ����ϴ�.");
		//������ üũ
		}else if(master.getFiuDocNum().equals(null)||master.getFiuDocNum().length()==0){
			throw new AgentException("�������� �ʼ����� FiuDocNum �� �����Ǿ����ϴ�.");
		//����ڵ� üũ
		}else if(org.getOrgName().getCode().equals(null)||org.getOrgName().getCode().length()==0){
			throw new AgentException("�������� �ʼ����� OrgCode �� �����Ǿ����ϴ�.");
		//����� üũ
		}else if(org.getOrgName().getStringValue().equals(null)||org.getOrgName().getStringValue().length()==0){
			throw new AgentException("�������� �ʼ����� OrgName �� �����Ǿ����ϴ�.");
		}

		body.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">RepOrgCode</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+STRConfigure.getInstance().getAgentInfo().getReportOrgInfo(org.getOrgName().getCode()).getRepOrgCd()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">OrgCode</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+org.getOrgName().getCode()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">OrgName</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+org.getOrgName().getStringValue()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">UsrId</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+org.getMainAuthor().getUserid()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">OrgDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+master.getOrgDocNum()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">OrgDocNumSeq</key>")
		.append("	<value xsi:type=\"xsd:anyType\"/>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">FiuDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+master.getFiuDocNum()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">StartDate</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+master.getStartDate()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">EndDate</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+master.getEndDate()+"</value>")
		.append("</item>")
		
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">FormerOrgDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+FormerOrgDocNum+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">FormerFiuDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+FormerFiuDocNum+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">RelationOrgDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\"/>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">RelationFiuDocNum</key>")
		.append("	<value xsi:type=\"xsd:anyType\"/>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">AttachmentfileName</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+fileNm +" </value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">DocSendDate</key>")
		.append("	<value xsi:type=\"xsd:anyType\">"+master.getDocSendDate()+"</value>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">RptDocMgntNo</key>")
		.append("	<value xsi:type=\"xsd:anyType\"/>")
		.append("</item>")
		.append("<item>")
		.append("	<key xsi:type=\"xsd:anyType\">ApdPrvdSttmnt</key>")
		.append("	<value xsi:type=\"xsd:anyType\"/>")
		.append("</item>")
		.append("</Bodys>");
		

		return body.toString();
	}

	private String getModuleVersionId() throws FileNotFoundException,
			IOException, SAXException {
		BufferedReader br = new BufferedReader(new FileReader(STRConfigure
				.getInstance().getAgentInfo().getControlFile()));

		return br.readLine().trim();
	}

	
	public Object action() throws Exception {
		StringBuilder summary = null;
		try{
			
				STRAgentInfo ai = STRConfigure.getInstance().getAgentInfo();
				
		        boolean encryptYN = ai.getFiuInfo().getEncryptionInfo().getKeyManageInfo().isEnabled();
				boolean signedYN = ai.getReportOrgInfo(orgCd).getEncryptionInfo().getSigningInfo().isEnabled();
				if(!encryptYN && !signedYN){
					securityCode = "00";
				}else if(encryptYN && !signedYN){
					securityCode = "01";
				}else if(!encryptYN && signedYN){
					securityCode = "02";
				}else if(encryptYN && signedYN){
					securityCode = "03";
				}
			
				summary = new StringBuilder("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
						.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
						.append("xmlns:str=\"http://www.kofiu.co.kr/str\">")
						.append(" <soapenv:Header/> ")
						.append(" <soapenv:Body> ")
						.append("  <str:SuspicousTransactionReport soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">")
						.append("   <document xsi:type=\"enc:DocumentObject\" xmlns:enc=\"http://www.kofiu.co.kr/str/encodedTypes\">")
						.append(this.makeHeader(orgCd))
						.append(this.makeBody())
						.append( this.makeAttachment(orgCd) ) // ÷������ ���ڼ���
						.append("   </document>") 
						.append("  </str:SuspicousTransactionReport>")
						.append(" </soapenv:Body> ")
						.append("</soapenv:Envelope> ");
			
			return summary.toString().getBytes(); 
		
		}catch(AgentException e){
			throw new AgentException(e);
		}
		
	}
	
	private byte[] readData(String filePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileNm));
		String data = "";
		String temp = "";
		while((temp = br.readLine())!=null){
			data += temp+"\r\n";
		}
		br.close();
		
		return data.getBytes();
		
	}
}
