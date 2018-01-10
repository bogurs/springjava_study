/**
 * 
 */
package kr.go.kofiu.ctr.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kr.go.kofiu.ctr.service.OutboxPollerJob;
import kr.go.kofiu.ctr.util.DateUtil;
import kr.go.kofiu.ctr.util.FileTool;
import kr.go.kofiu.ctr.util.TypeCode;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : FlatFile�� ���ϸ��� �˻��ϰ�, ���� ó���� ���� CTRSTART/CTREND ���� �ִ����� üũ�Ѵ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 8. 25
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileChecker089 {
	
	/**
	 * OK
	 */
	public static final int OK = 0;
	
	/**
	 * ���� ���� �ùٸ��� �ʴ�. 
	 */
	public static final int FILENAME_FORMAT_INVALID = 1;
	
	/**
	 * ���� ���� �ùٸ��� �ʴ�. 
	 */
	public static final int FILEDATE_INVALID = 2;

	/**
	 * CTREND�� �ùٸ��� �ʴ�.
	 */
	public static final int CTREND_INVALID = 3;
	
	/**
	 * CTRSTART���� �ùٸ��� �ʰ� ������ ��¥���� ���� �Ǿ���.
	 */
	public static final int CTREND_INVALID_AND_TOO_OLD = 4; 
	
	/**
	 * CTRSTART���� �ùٸ��� �ʰ� ������ ��¥�� ������ �ʾҴ�.
	 */
	public static final int CTRSTART_INVALID = 5;
	
	/**
	 * �Ϸ��� millisecond�� 
	 */
	private static final int ONE_DAY = 24*60*60*1000;
	
	/**
	 * Message Repository, key is message id, value is messaeg Object
	 */
	private static Hashtable messages = new Hashtable();
	
	static {
		try {
			URL messages = FlatFileChecker089.class.getResource("/kr/go/kofiu/ctr/util/message.xml");
			parseMessageDefinitionXML(messages.openStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * FlatFile File ��ü 
	 */
	private File flatfile;
	
	
	/**
	 * FlatFile data - Trim�� ���� ���� ���� ���� 
	 */
	private String flatdata;
	
	
	/**
	 * ������ 
	 * @param flatfile
	 * @throws IOException 
	 */
	public FlatFileChecker089(File flatfile) throws IOException {
		this.flatfile = flatfile;
		flatdata = FileTool.getFileString(flatfile);
		if ( flatdata != null )
			flatdata = flatdata.trim();
	}
	

	/**
	 * FlatFile ���� �ùٸ��� üũ�Ѵ�. 
	 * @return
	 * @throws IOException
	 */
	public int isValid() throws IOException
	{
		// file name validation
		String[] nodes = getCTRInfoByFileName();
		
		if ( nodes == null )
			return FILENAME_FORMAT_INVALID;
		
		if(nodes[0] == null || !nodes[0].equals("CTR"))	{
			return FILENAME_FORMAT_INVALID;
		} else if(nodes[1] == null || (nodes[1].length() != 4) ) {
			return FILENAME_FORMAT_INVALID;
		} else if(nodes[2] == null || nodes[2].length() != 8) {
			return FILENAME_FORMAT_INVALID;
		}
		
		try {
			DateUtil.parse(DateUtil.yyyyMMdd, nodes[2]);
		} catch (ParseException e) {
			return FILEDATE_INVALID;
		}

		// ��¥ üũ 
		String today = DateUtil.getDateTime(DateUtil.yyyyMMdd);
		if ( Integer.parseInt(today) < Integer.parseInt(nodes[2]) )
			return FILEDATE_INVALID;	
		
		if(nodes[3] == null || nodes[3].length() != 7 ){
			return FILENAME_FORMAT_INVALID;
		}
		
		
		// Check File data Ends with "CTREND" value
		// defend Read while not yet written
		if ( !endsWithFooter(flatdata) ) {
			// start/end validation
			long term = System.currentTimeMillis() - flatfile.lastModified();
			if ( term > ONE_DAY) {
				return CTREND_INVALID_AND_TOO_OLD;
			} else {
				return CTREND_INVALID;
			}
		} else if ( !startWithHeader(flatdata) ) {
			return CTRSTART_INVALID;
		}
		
		return OK;
	}
	
	
	
	/**
	 * ���� ����Ÿ�� ���� �� 'CTRSTART' ���� �ִ��� üũ�Ѵ�.
	 * @param flatdata
	 * @return
	 */
	private boolean startWithHeader(String flatdata) {
		if( flatdata != null && flatdata.startsWith("CTRSTART") ) {
			return true; 
		}

		return false;
	}
	 

	/**
	 * ���� ����Ÿ�� ���� 'CTREND' ���� �ִ��� üũ�Ѵ�.
	 * @param flatdata
	 * @return
	 */
	private boolean endsWithFooter(String flatdata) {
		if( flatdata != null && flatdata.endsWith("CTREND") ) {
			return true; 
		}
			
		return false;
	}

	/**
	 * getCTRInfoBy File Name
	 *
	 **/
	public String[] getCTRInfoByFileName() {
		String name = flatfile.getName();
		String nameWithoutExt = name.substring(0, name.lastIndexOf("."));
		String[] info = nameWithoutExt.split("_");

		if ( info.length != 4  ) {
			return null;
		}
		
		return info;
	}

	/**
	 * ���� üũ ��� ��
	 */
	public String toString() {
		String msg = flatfile.getName();
		try {
			switch (isValid()) {
			case FILENAME_FORMAT_INVALID:
				msg += " ���� ���� �ùٸ��� �ʽ��ϴ�.";
				break;
			case FILEDATE_INVALID:
				String[] nodes = getCTRInfoByFileName();
				msg += " ���ϸ��� ��¥ �� " + nodes[2] + " �� ���� ��¥ ���� �̷��� ���Դϴ�.";
				break;
			case CTREND_INVALID:
				msg += " ���� ����Ÿ  CTREND ���� �ùٸ��� �ʽ��ϴ�.";
				break;
			case CTREND_INVALID_AND_TOO_OLD:
				msg += " ���� ����Ÿ  CTREND ���� �ùٸ��� �ʰ� ������ �����Դϴ�.";
				break;
			case CTRSTART_INVALID:
				msg += " ���� ����Ÿ CTRSTART ���� �ùٸ��� �ʽ��ϴ�.";
				break;
			}
		} catch (IOException e) {
			// ignore
		}
		return msg;
	}

	
	/**
	 * get message by id
	 *
	 */
	public static Message getMessageById(String id) {
		Object obj = messages.get(id);
		if ( obj != null )
			return (Message)obj;
		
		return null;
	}
	
	/**
	 * 
	 * @throws FlatFileDataException
	 */
	public void checkMessageFormat() throws FlatFileDataException {
		String[] tokens = flatdata.split("\\|\\|"); //parse By Delimiter("||");
		
		// count check
		if( tokens == null || tokens.length < 2) { 
			throw new FlatFileDataException("CTR ���� ������ ���˰� ��ġ���� �ʽ��ϴ�. ������ : '" +  flatdata + "'"); 
		}
		
		Message message = null;
		if ( tokens[1].equals(TypeCode.MessageType.CANCEL)
			|| tokens[1].equals(TypeCode.MessageType.TEST_CANCEL) ) {
			message = FlatFileChecker089.getMessageById("111");
		} else {
			message = FlatFileChecker089.getMessageById("101");
		}
		
		// �޼��� ���� üũ
		message.validatePlainTextMessage(tokens);
		String[] nodes = getCTRInfoByFileName();
		
	
		
		// �������� �� üũ
		int rptDateInt = Integer.parseInt(tokens[3]);
		int todayInt = Integer.parseInt(DateUtil.getDateTime(DateUtil.yyyyMMdd));
		if ( rptDateInt > todayInt ) {
			throw new FlatFileDataException("CTR�������� ��������(2)�ʵ� ����  '" + tokens[3]
			                 + "' �Դϴ�. �̴� ���� ��¥  '" + todayInt + "' ���� �̷��Դϴ�.");
		}
		
	
		
		// ��ȿ�� üũ(FIU 7��) 
		FlatFileValidity fCheck = new FlatFileValidity();
		Hashtable htReturn = fCheck.checkValidate(tokens,"089");
		

		if (!"Y".equals((String)htReturn.get("RESULT")) ){
			throw new FlatFileDataException( (String)htReturn.get("ERRMSG") );
		}
		
	
		
		if ( tokens[8] == null || !tokens[8].equals(nodes[1]) )
			throw new FlatFileDataException("������ ���ϸ��� ��� �ڵ� �� " + nodes[1]
			                       + "�� ���� ������ ����� ��� �ڵ� �� " + tokens[8] + "�� �ٸ��ϴ�.");
		
		// ���� �ŷ� �� üũ 
		String rpt_doc_no = tokens[2];
		String rpt_order = tokens[5];
		String rpt_main_doc_no = tokens[6];
		String trnsct_amt = tokens[47];
		
		// 2006/02/01 �߰�
		String[] orders = rpt_order.split("/");
		for( int i = 0 ; i < orders.length; i++) orders[i] = orders[i].trim(); 
		
		int ordInt = Integer.parseInt(orders[0]);
		int totalInt = Integer.parseInt(orders[1]);

		// ���� ���� �ϰ�� 2000���� üũ
		if ( ordInt == 1 && totalInt == 1 ) { // "001/001".equals(rpt_order)
			// ��� ���� �� ��� ���� ���� �� �ִ�. 
			if ( trnsct_amt != null && !"".equals(trnsct_amt) ) {
				long trnsct_amtInt = Long.parseLong(trnsct_amt);
				if ( trnsct_amtInt < 20000000 ) {
					throw new FlatFileDataException("�ŷ�����(4) ���� '" +rpt_order+ "'���� ���� �����̰� "
							+ "�ŷ��ݾ�(��ȭ)(47)�� '" + trnsct_amt + "'������ 2õ���� �����Դϴ�.");
				}
			}
		} else {
		//20130114 CSR 2013-0023 ��ö�� ���Ұŷ��ϰ�� �ŷ��ݾ� 0�� ���� üũ
			if ( trnsct_amt != null && !"".equals(trnsct_amt) ) {
				long trnsct_amtInt = Long.parseLong(trnsct_amt);
				if ( !(trnsct_amtInt > 0) ) {
					throw new FlatFileDataException("�ŷ�����(4) ���� '" +rpt_order+ "'���� ���Ұŷ� �����̰� "
							+ "�ŷ��ݾ�(��ȭ)(47)�� '" + trnsct_amt + "'������ 0�� �����Դϴ�.");
				}
			}
		}
		
		if ( ordInt == 1  ) { // "001".equals(orders[0])
			if ( !rpt_doc_no.equals(rpt_main_doc_no)) {
				throw new FlatFileDataException("�ŷ�����(4) ���� '" +rpt_order+ "'�� ��� ������ȣ(1)���� �⺻������ȣ(4-1)���� �����ؾ� �մϴ�."
						+ " ������ȣ(1) : " + rpt_doc_no + ", �⺻������ȣ(4-1) : " + rpt_main_doc_no);
			}
		} 
		
		if ( ordInt > totalInt) {
			throw new FlatFileDataException("�ŷ�����(4) ����  '" + rpt_order + "'�Դϴ�. �ŷ����� '" + ordInt 
					+ "'�� �Ѱŷ��� '" + totalInt+"' ���� Ů�ϴ�.");
		}

		if ( ordInt != 1 && ordInt != totalInt) {
			if ( rpt_doc_no.equals(rpt_main_doc_no)) {
				throw new FlatFileDataException("�ŷ�����(4) ���� '" +rpt_order+ "'�Դϴ�. �ŷ����� '" + ordInt 
						+ "'�� �Ѱŷ��� '" + totalInt+"' �� �ٸ� ��� ������ȣ(1)���� �⺻������ȣ(4-1)���� ������ �� �����ϴ�."
						+ " ������ȣ(1) : " + rpt_doc_no + ", �⺻������ȣ(4-1) : " + rpt_main_doc_no);
			}			
		}
		
		//20111125 CSR 2011-0462 ��ö�� �ŷ��߻��Ͻ� validation check �߰�
		if ( !( tokens[1].equals(TypeCode.MessageType.CANCEL) || tokens[1].equals(TypeCode.MessageType.TEST_CANCEL) ) ) {
			int todayDtInt = Integer.parseInt(DateUtil.getDateTime(DateUtil.yyyyMMdd));
			int trnsOcrDtInt = Integer.parseInt(tokens[42].substring(0,8));
			if ( trnsOcrDtInt > todayDtInt ) {
				throw new FlatFileDataException("CTR�������� �ŷ��߻��Ͻ�(42)�ʵ� ����  '" + tokens[42] + "' �Դϴ�. �̴� ���� ��¥  '" + todayInt + "' ���� �̷��Դϴ�.");
			}
			
			int trnsOcrYr = Integer.parseInt(tokens[42].substring(0,4));
			if ( trnsOcrYr < 2000 ) {
				throw new FlatFileDataException("CTR�������� �ŷ��߻��Ͻ�(42)�ʵ� ����  '" + tokens[42] + "' �Դϴ�. 2000�� ������ ��¥�� �Է��� �� �����ϴ�.");
			}
		}
	}
	
	/**
	 * message.xml �� parse�Ѵ�.
	 */
	public static void parseMessageDefinitionXML(InputStream in) throws Exception {
		
		// doc builde
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(in);

		// message node list
		NodeList msgList = doc.getElementsByTagName(Message.ROOT);
		
		for(int i=0; i < msgList.getLength(); i++) {

			Element msg = (Element)msgList.item(i);			
			Message messageObject = new Message(msg.getAttribute(Message.ID));
			messageObject.setName(msg.getAttribute(Message.NAME));
			
			// fild node list
			NodeList fldList = msg.getElementsByTagName(Field.ROOT);
			
			for(int j=0; j < fldList.getLength(); j++) {
				Element fld = (Element)fldList.item(j);
				Field fieldObject = new Field(Integer.toString(j+1));
				fieldObject.setXmlSeq(fld.getAttribute(Field.XML_SEQ));
				fieldObject.setDocNo(fld.getAttribute(Field.DOC_NO));
				fieldObject.setName(fld.getAttribute(Field.NAME));
				fieldObject.setTag(fld.getAttribute(Field.TAG));
				fieldObject.setConstrain(fld.getAttribute(Field.CONSTRAIN));
				fieldObject.setType(fld.getAttribute(Field.TYPE));
				fieldObject.setCodeset(fld.getAttribute(Field.CODESET));
				fieldObject.setLength(fld.getAttribute(Field.LENGTH));
				fieldObject.setFixed(fld.getAttribute(Field.FIXED));
				fieldObject.setFormat(fld.getAttribute(Field.FORMAT));
				
				messageObject.addField(fieldObject);
			}
			
			messages.put(messageObject.getId(), messageObject);
		}

	}

	/**
	 * 
	 * @return
	 */
	public String getFlatdata() {
		return flatdata;
	}

	/**
	 * 
	 * @param flatdata
	 */
	public void setFlatdata(String flatdata) {
		this.flatdata = flatdata;
	}

	/**
	 * 
	 * @return
	 */
	public File getFlatfile() {
		return flatfile;
	}

	/**
	 * 
	 * @param flatfile
	 */
	public void setFlatfile(File flatfile) {
		this.flatfile = flatfile;
	}
/*	
	public static void main(String[] args) throws IOException, FlatFileDataException{
		File[] arrflatfile = new File(CtrAgentEnvInfo.OUTBOX_DIR_NAME).listFiles();
		File flatfile = arrflatfile[0];
		System.out.println(flatfile.getName());
		
		FlatFileChecker089 flatFileChecker = new FlatFileChecker089(flatfile);
		flatFileChecker.checkMessageFormat();
		System.out.println(flatFileChecker.isValid());
	}
*/
}
