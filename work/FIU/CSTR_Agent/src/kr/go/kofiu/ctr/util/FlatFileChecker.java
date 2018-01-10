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

/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : FlatFile의 파일명을 검사하고, 파일 처음과 끝에 CTRSTART/CTREND 값이 있느지를 체크한다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 8. 25
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class FlatFileChecker {
	
	/**
	 * OK
	 */
	public static final int OK = 0;
	
	/**
	 * 파일 명이 올바르지 않다. 
	 */
	public static final int FILENAME_FORMAT_INVALID = 1;
	
	/**
	 * 파일 명이 올바르지 않다. 
	 */
	public static final int FILEDATE_INVALID = 2;

	/**
	 * CTREND가 올바르지 않다.
	 */
	public static final int CTREND_INVALID = 3;
	
	/**
	 * CTRSTART값이 올바르지 않고 지정된 날짜보다 오래 되었다.
	 */
	public static final int CTREND_INVALID_AND_TOO_OLD = 4; 
	
	/**
	 * CTRSTART값이 올바르지 않고 지정된 날짜를 지나지 않았다.
	 */
	public static final int CTRSTART_INVALID = 5;
	
	/**
	 * 하루의 millisecond값 
	 */
	private static final int ONE_DAY = 24*60*60*1000;
	
	/**
	 * Message Repository, key is message id, value is messaeg Object
	 */
	private static Hashtable messages = new Hashtable();
	
	static {
		try {
			URL messages = FlatFileChecker.class.getResource("/kr/go/kofiu/ctr/util/message.xml");
			parseMessageDefinitionXML(messages.openStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * FlatFile File 객체 
	 */
	private File flatfile;
	
	
	/**
	 * FlatFile data - Trim을 통해 제어 문자 제거 
	 */
	private String flatdata;
	
	
	/**
	 * 생성자 
	 * @param flatfile
	 * @throws IOException 
	 */
	public FlatFileChecker(File flatfile) throws IOException {
		this.flatfile = flatfile;
		flatdata = FileTool.getFileString(flatfile);
		if ( flatdata != null )
			flatdata = flatdata.trim();
	}
	
	/**
	 * 테스트용 생성자 
	 * @param flatfile
	 * @throws IOException 
	 */
	public FlatFileChecker(String flatdata) throws IOException {
		this.flatdata = flatdata; 
		if ( flatdata != null )
			flatdata = flatdata.trim();
	}
	

	/**
	 * FlatFile 명이 올바른지 체크한다. 
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

		// 날짜 체크 
		String today = DateUtil.getDateTime(DateUtil.yyyyMMdd);
		if ( Integer.parseInt(today) < Integer.parseInt(nodes[2]) ){
			return FILEDATE_INVALID;	
		}
		
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
	 * 파일 데이타의 시작 에 'CTRSTART' 값이 있는지 체크한다.
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
	 * 파일 데이타의 끝에 'CTREND' 값이 있는지 체크한다.
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
	 * 파일 체크 결과 값
	 */
	public String toString() {
		String msg = flatfile.getName();
		try {
			switch (isValid()) {
			case FILENAME_FORMAT_INVALID:
				msg += " 파일 명이 올바르지 않습니다.";
				break;
			case FILEDATE_INVALID:
				String[] nodes = getCTRInfoByFileName();
				msg += " 파일명의 날짜 값 " + nodes[2] + " 이 오늘 날짜 보다 미래의 값입니다.";
				break;
			case CTREND_INVALID:
				msg += " 파일 데이타  CTREND 값이 올바르지 않습니다.";
				break;
			case CTREND_INVALID_AND_TOO_OLD:
				msg += " 파일 데이타  CTREND 값이 올바르지 않고 오래된 파일입니다.";
				break;
			case CTRSTART_INVALID:
				msg += " 파일 데이타 CTRSTART 값이 올바르지 않습니다.";
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
			throw new FlatFileDataException("CTR 보고 문서가 포맷과 일치하지 않습니다. 보고문서 : '" +  flatdata + "'"); 
		}


		Message message = null;
		if ( tokens[1].equals(TypeCode.MessageType.CANCEL)
			|| tokens[1].equals(TypeCode.MessageType.TEST_CANCEL) ) {
			message = FlatFileChecker.getMessageById("110");
		} else {
			message = FlatFileChecker.getMessageById("100");
		}
		
		// 메세지 형식 체크
		message.validatePlainTextMessage(tokens);
		String[] nodes = getCTRInfoByFileName();
		
		

		
		
		// 보고일자 값 체크
		int rptDateInt = Integer.parseInt(tokens[3]);
		int todayInt   = Integer.parseInt(DateUtil.getDateTime(DateUtil.yyyyMMdd));
		if ( rptDateInt > todayInt ) {
			throw new FlatFileDataException("CTR보고문서의 보고일자(2)필드 값이  '" + tokens[3] + "' 입니다. 이는 오늘 날짜  '" + todayInt + "' 보다 미래입니다.");
		}
		

		
		// 유효성 체크(FIU 7차) 
		FlatFileValidity fCheck = new FlatFileValidity();
		Hashtable htReturn = fCheck.checkValidate(tokens,"088");
		


		if (!"Y".equals((String)htReturn.get("RESULT")) ){
			throw new FlatFileDataException( (String)htReturn.get("ERRMSG") );
		}
		

		
		if ( tokens[8] == null || !tokens[8].equals(nodes[1]) ){
			throw new FlatFileDataException("보고문서 파일명의 기관 코드 값 " + nodes[1] + "이 보고 문서에 기재된 기관 코드 값 " + tokens[8] + "과 다릅니다.");
		}
		
		
		
		
		
		// 분할 거래 값 체크 
		String rpt_doc_no = tokens[2];
		String rpt_order = tokens[5];
		String rpt_main_doc_no = tokens[6];
		String trnsct_amt = tokens[34];
		
		// 2006/02/01 추가
		String[] orders = rpt_order.split("/");
		for( int i = 0 ; i < orders.length; i++) orders[i] = orders[i].trim(); 
		
		int ordInt = Integer.parseInt(orders[0]);
		int totalInt = Integer.parseInt(orders[1]);

		// 단일 보고 일경우 2000만원 체크2010년 1월1일부터 
		if ( ordInt == 1 && totalInt == 1 ) { // "001/001".equals(rpt_order)
			// 취소 보고 일 경우 값이 없을 수 있다. 
			if ( trnsct_amt != null && !"".equals(trnsct_amt) ) {
				long trnsct_amtInt = Long.parseLong(trnsct_amt);
				if ( trnsct_amtInt < 20000000 ) {
					throw new FlatFileDataException("거래순번(4) 값이 '" +rpt_order+ "'으로 단일 보고이고 "
							+ "거래금액(원화)(27)이 '" + trnsct_amt + "'원으로 2천만원 이하입니다.");
				}
			}
		} else {
		//20130114 CSR 2013-0023 강철민 분할거래일경우 거래금액 0원 이하 체크
			if ( trnsct_amt != null && !"".equals(trnsct_amt) ) {
				long trnsct_amtInt = Long.parseLong(trnsct_amt);
				if ( !(trnsct_amtInt > 0) ) {
					throw new FlatFileDataException("거래순번(4) 값이 '" +rpt_order+ "'으로 분할거래 보고이고 "
							+ "거래금액(원화)(47)이 '" + trnsct_amt + "'원으로 0원 이하입니다.");
				}
			}
		}
		
		if ( ordInt == 1  ) { // "001".equals(orders[0])
			if ( !rpt_doc_no.equals(rpt_main_doc_no)) {
				throw new FlatFileDataException("거래순번(4) 값이 '" +rpt_order+ "'일 경우 문서번호(1)값과 기본문서번호(4-1)값이 동일해야 합니다."
						+ " 문서번호(1) : " + rpt_doc_no + ", 기본문서번호(4-1) : " + rpt_main_doc_no);
			}
		} 
		
		if ( ordInt > totalInt) {
			throw new FlatFileDataException("거래순번(4) 값이  '" + rpt_order + "'입니다. 거래순번 '" + ordInt 
					+ "'이 총거래수 '" + totalInt+"' 보다 큽니다.");
		}

		if ( ordInt != 1 && ordInt != totalInt) {
			if ( rpt_doc_no.equals(rpt_main_doc_no)) {
				throw new FlatFileDataException("거래순번(4) 값이 '" +rpt_order+ "'입니다. 거래순번 '" + ordInt 
						+ "'이 총거래수 '" + totalInt+"' 와 다른 경우 문서번호(1)값과 기본문서번호(4-1)값이 동일할 수 없습니다."
						+ " 문서번호(1) : " + rpt_doc_no + ", 기본문서번호(4-1) : " + rpt_main_doc_no);
			}			
		}
		
		//20111125 CSR 2011-0462 강철민 거래발생일시 validation check 추가
		if ( !( tokens[1].equals(TypeCode.MessageType.CANCEL) || tokens[1].equals(TypeCode.MessageType.TEST_CANCEL) ) ) {
			int todayDtInt = Integer.parseInt(DateUtil.getDateTime(DateUtil.yyyyMMdd));
			int trnsOcrDtInt = Integer.parseInt(tokens[29].substring(0,8));
			if ( trnsOcrDtInt > todayDtInt ) {
				throw new FlatFileDataException("CTR보고문서의 거래발생일시(29)필드 값이  '" + tokens[29] + "' 입니다. 이는 오늘 날짜  '" + todayInt + "' 보다 미래입니다.");
			}
			
			int trnsOcrYr = Integer.parseInt(tokens[29].substring(0,4));
			if ( trnsOcrYr < 2000 ) {
				throw new FlatFileDataException("CTR보고문서의 거래발생일시(29)필드 값이  '" + tokens[29] + "' 입니다. 2000년 이전의 날짜는 입력할 수 없습니다.");
			}
		}
	}
	
	/**
	 * message.xml 을 parse한다.
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

	public static void main(String[] args) throws IOException, FlatFileDataException{
		String flatfile = "CTRSTART||01||2007-97000001||20071103||||001/001||2007-97000001||TST1||TST1||김선봉||이수영||010-3131-3131||최명진||01||7710101212123||152897||서울 성북구||02-1212-1212||||한국||KR||||||||||||||||||20071102141000||과천점||427600||01||01||50000000||11||903070561092611||과천점||427600||20070808||||||||||||||||||||||||||||||||||||01||LG CNS||TF팀||상무||빌아저씨||||||||||||||||||||||||1||이수영||01||8112121212123||KR||한국||010-3131-3131||99||앙숙||1||이수영||01||8112121212123||010-3131-3131||KR||한국||99||앙숙||CTREND";
		FlatFileChecker flatFileChecker = new FlatFileChecker(flatfile);
		flatFileChecker.checkMessageFormat();
		
	}

	
	
	
}
