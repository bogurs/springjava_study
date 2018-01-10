package kr.go.kofiu.ctr.util;

import java.text.ParseException;

import kr.go.kofiu.ctr.util.DateUtil;

/**
 * *****************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 ******************************************************
 */
public class Field {
	
	/**
	 * message.xml - Field 항목의 Attribute field 키 네임 
	 */
	public static final String ROOT = "field";
	
	/**
	 * message.xml - Field 항목의 Attribute xmlSeq 키 네임 
	 */
	public static final String XML_SEQ = "xmlSeq";
	
	/**
	 * message.xml - Field 항목의 Attribute docNo 키 네임 
	 */
	public static final String DOC_NO = "docNo";
	
	/**
	 * message.xml - Field 항목의 Attribute name 키 네임 
	 */
	public static final String NAME = "name";
	
	/**
	 * message.xml - Field 항목의 Attribute tag 키 네임 
	 */
	public static final String TAG = "tag";
	
	/**
	 * 제약조건 필드 mandatory/optional
	 */
	public static final String CONSTRAIN = "constrain";
	
	/**
	 * 제약조건 필드 값 - mandatory
	 */
	public static final String CONSTRAIN_MANDATORY = "Mandatory";

	/**
	 * 제약조건 필드 값 - optional
	 */
	public static final String CONSTRAIN_OPTIONAL = "Optional";

	/**
	 * 제약조건 필드 값 - NULL
	 */
	public static final String CONSTRAIN_NULL = "Null";
	
	
	/**
	 * 값의 Type 정의 - Code/String/Date/Number
	 */
	public static final String TYPE = "type";

	/**
	 * 값의 Type 정의 - Code/String/Date/Number
	 */
	public static final String CODESET = "codeset";
	
	/**
	 * 값의 Type 정의 - Code/String/Date/Number
	 */
	public static final String LENGTH = "length";
	
	/**
	 * 값의 Type 정의 - Code/String/Date/Number
	 */
	public static final String FIXED = "fixed";
	
	/**
	 * 값의 Type 정의 - Code/String/Date/Number
	 */
	public static final String FORMAT = "format";
	

	/**
	 * Type Code 값 - 코드 
	 */
	public static final String TYPE_CODE = "Code";
	
	/**
	 * Type Code 값 - 문자  
	 */
	public static final String TYPE_STRING = "String";
	
	/**
	 * Type Code 값 - 날짜  
	 */
	public static final String TYPE_DATE = "Date";
	
	/**
	 * Type Code 값 - 숫자  
	 */
	public static final String TYPE_NUMBER = "Number";

	/**
	 * Type Code 값 - 전화번호 형식
	 */
	public static final String TYPE_TELEPHONE_NUMBER = "TelephoneNumber";
	
	/**
	 * 자릿수 고정 여부 - true
	 */
	public static final String FIXED_TRUE = "true";

	/**
	 * 자릿수 고정 여부 - false
	 */
	public static final String FIXED_FALSE = "false";
	
	
	/**
	 * 필드 아디디 값
	 */
	private int id		  			= -1;
	
	/**
	 * xml 순서 번호 
	 */
	private String xmlSeq			= null;
	
	/**
	 * 문서 번호 
	 */
	private String docNo			= null;
	
	/**
	 * 
	 */
	private String tag				= null;
	
	/**
	 * 
	 */
	private String name				= null;
	
	/**
	 * 제약 조건 
	 */
	private String constrain		= null;
	
	/**
	 * 
	 */
	private String type				= null;
	
	/**
	 * 코드 값 
	 */
	private String codeset			= null;
	
	/**
	 * 
	 */
	private String[] codesetArr		= null;
	
	/**
	 * 
	 */
	private int length				= 0;
	
	/**
	 * 
	 */
	private String format			= null;
	
	/**
	 * 
	 */
	private boolean fixed     		= true;
	
	/**
	 * 실제 필드에 할당되는 값
	 */
	private String value			= null;
	
	
	/**
	 * 생성자 
	 *
	 **/
	public Field(String id) throws Exception {
		setId(id);	
	}
	

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * 
	 * @param idStr
	 * @throws FlatFileDataException
	 */
	public void setId(String idStr) throws FlatFileDataException { 
		try { 
			if ( idStr != null && idStr.length() > 0 ) {
				this.id = Integer.parseInt(idStr);
			} else {
				throw new FlatFileDataException("필드 id 값이 없습니다."); 
			}
		} catch(Exception e) { 
			throw new FlatFileDataException("필드 id 값 '" + idStr 
					+ "'은 숫자형이어야 합니다.", e); 
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getXmlSeq() {
		return xmlSeq;
	}

	/**
	 * 
	 * @param xmlSeq
	 * @throws FlatFileDataException
	 */
	public void setXmlSeq(String xmlSeq) throws FlatFileDataException {
		if(xmlSeq == null) { 
			throw new FlatFileDataException("필드 xmlSeq 값이 없습니다."); 
		}
		this.xmlSeq = xmlSeq; 
	}

	/**
	 * 
	 * @return
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * 
	 * @param docNo
	 * @throws FlatFileDataException
	 */
	public void setDocNo(String docNo) throws FlatFileDataException {
		if(docNo == null) { 
			throw new FlatFileDataException("필드 문서 번호(docNo)값이 없습니다."); 
		}
		this.docNo = docNo;
	}

	/**
	 * 
	 * @return
	 */
	public String getTag() { 
		return this.tag; 
	}
	
	/**
	 * 
	 * @param tag
	 * @throws FlatFileDataException
	 */
	public void setTag(String tag) throws FlatFileDataException { 
		if(tag == null) { 
			throw new FlatFileDataException("필드 Tag 값이 없습니다."); 
		}
		this.tag = tag; 
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() { 
		return name;
	}
	
	/**
	 * 
	 * @param name
	 * @throws FlatFileDataException
	 */
	public void setName(String name) throws FlatFileDataException { 
		if(name == null) { 
			throw new FlatFileDataException("필드 이름이 없습니다."); 
		}
		this.name = name; 
	}
	
	/**
	 * 
	 * @return
	 */
	public String getConstrain() { 
		return this.constrain; 
	}
	
	/**
	 * 
	 * @param constrain
	 * @throws FlatFileDataException
	 */
	public void setConstrain(String constrain) throws FlatFileDataException{
		if(CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain) 
				|| CONSTRAIN_OPTIONAL.equals(constrain)
				|| CONSTRAIN_NULL.equals(constrain))  {
			this.constrain = constrain;
		} else {
			throw new FlatFileDataException(getName() + "'의 constrain 값이   '" + constrain + "' 올바르지 않습니다."); 
		}
	}


	/**
	 * 
	 * @return
	 */
	public String getType() { 
		return this.type; 
	}
	
	/**
	 * 
	 * @param type
	 * @throws FlatFileDataException
	 */
	public void setType(String type) throws FlatFileDataException { 
		if( type != null && type.length() > 0  ) {
			if( !TYPE_NUMBER.equalsIgnoreCase(type) && !TYPE_STRING.equalsIgnoreCase(type)
					&& !TYPE_DATE.equalsIgnoreCase(type) && !TYPE_CODE.equalsIgnoreCase(type) 
					&& !TYPE_TELEPHONE_NUMBER.equalsIgnoreCase(type) ) {
				throw new FlatFileDataException("필드 정의의 type값 '" +type + "'은 올바르지 않습니다."); 
			}
		}

		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public int getLength() { 
		return this.length; 
	}
	
	/**
	 * 
	 * @param lengthStr
	 * @throws FlatFileDataException
	 */
	public void setLength(String lengthStr) throws FlatFileDataException { 
		try { 
			if ( lengthStr != null && lengthStr.length() > 0 ) {
				this.length = Integer.parseInt(lengthStr);
			} else {
				this.length  = -1;
			}
		} catch(Exception e) { 
			throw new FlatFileDataException(getName() + " 값 '" + lengthStr 
					+ "'이 숫자가 아닙니다.", e); 
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFormat() {
		return this.format; 
	}
	
	/**
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format; 
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFixed() { 
		return this.fixed; 
	}
	
	/**
	 * 
	 * @param fixed
	 * @throws FlatFileDataException
	 */
	public void setFixed(String fixed) throws FlatFileDataException {
		// default true
		if ( fixed == null || fixed.length() == 0 ) 
			fixed = FIXED_TRUE;
		
		if(FIXED_TRUE.equalsIgnoreCase(fixed)) {
			this.fixed = true;
		} else if(fixed.equalsIgnoreCase(FIXED_FALSE)) {
			this.fixed = false;
		} else { 
			throw new FlatFileDataException(fixed + " 올바른 값이 아닙니다."); 
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCodeset() { 
		return this.codeset; 
	}
	
	/**
	 * 
	 * @param codeset
	 * @throws FlatFileDataException
	 */
	public void setCodeset(String codeset) throws FlatFileDataException {
		this.codeset = codeset; 
		this.codesetArr = codeset.split(",");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getValue()  {
		return this.value;
	}
	
	/**
	 * 
	 * @param value
	 * @throws FlatFileDataException
	 */
	public void setValue(String value) throws FlatFileDataException {
		validate(value); 
		this.value = value; 
	}
	
	/**
	 * 
	 * @param value
	 * @throws FlatFileDataException
	 */
	public void validate(String value) throws FlatFileDataException {
//		if(CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain) )
		
		// Madatory 체크 
		if( CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain)
				&& ( value == null || value.length() == 0 ) ) {
			throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
					+ " 값이 없습니다. " + constrain + " 값 입니다.");
		}

		// Null 체크. 취소 보고 일 경우만 존재
		if ( CONSTRAIN_NULL.equalsIgnoreCase(constrain) && ( value != null && value.length() > 0) ){
			throw new FlatFileDataException("취소 보고일 경우 CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
					+ " 값이 존재해서는 안됩니다. 현재 '" +value + "' 값이 존재합니다.");
		}

		// optional
		if ( !CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain) 
				&& ( value == null || value.length() == 0) ) {
			return ;
		}
		
		// length check
		if ( this.getLength() > -1 ) {
			if(value.getBytes().length > getLength()) {
				throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
						+ " 값이 " + getLength() + " 보다 큰 " + value.getBytes().length + " 입니다.");
			}
		
			if( isFixed() ) {
				if(value.getBytes().length != getLength() ) { 
					throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
							+ " 값의 문자 길이는 " + getLength() + "와 일치 해야합니다. 그러나 현재 문자(" + value+") 길이가 "
							+ value.getBytes().length + " 입니다.");
				}
			}
		}
		
		// type check
		if( getType().equalsIgnoreCase(TYPE_NUMBER)) {
			try { 
				Long.parseLong(value);
				//Integer.parseInt(value); 
			} catch(Exception e) { 
				throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
						+ " 값이 '" + value + "' 로 숫자형식이 아닙니다.", e); 
			}
		} else if( getType().equalsIgnoreCase(TYPE_DATE)) {
			if( this.format == null) { 
				throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
						+ " 날짜 형식 값이 없습니다."); 
			}
			
			try{ 
				
				DateUtil.parse(format, value); 
			} catch(ParseException e) { 
				throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ")"   
						+ "의 값이 '" + value + "' 로 날짜  형식이 올바르지 않습니다."); 
			}
		} else if(getType().equalsIgnoreCase(TYPE_CODE)) {
			boolean isMatch = false;
			for ( int i = 0 ; i < codesetArr.length ; i++ ){
				if ( codesetArr[i].equalsIgnoreCase(value) ) {
					isMatch = true;
					break;
				}
			}
			if( !isMatch ) {
				throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ") 값이 '"   
						+ value + "'입니다. 코드 값이 올바르지 않습니다. 코드 값은 다음 중 하나여야 합니다. " + this.codeset);
			}				
		} else if(getType().equalsIgnoreCase(TYPE_TELEPHONE_NUMBER)) {
			// 2006/01/26 전화번호 체크 로직 보완
				
			 String[] strTel = value.split("-");
			 if( strTel.length > 4 ) 
					throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ") 값이 '"   
							+ value + "'입니다. 전화번호는 '-' 3개로 구분되어야만 합니다.");

			 for( int i = 0 ; i < strTel.length ; i++ )	 {
				 try {
					 // 02-507-2333-  형태처럼 내선번호는 생략 가능.
					 if( i < 3 ) {
						 Integer.parseInt(strTel[i]);
					 } else if ( strTel[i].length() != 0 ) {
						 Integer.parseInt(strTel[i]);
					 }
				 } catch (NumberFormatException e){
					  throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ") 값이 '"   
								+ value + "'입니다. 이 중 '" + strTel[i] + "'은 숫자여야 합니다.");
				 }
			 }
		} else if(getType().equalsIgnoreCase(TYPE_STRING)) {
			if( this.format != null && !"".equals(format)) {
				if (!value.matches(this.format)) {
					throw new FlatFileDataException("CTR 보고문서 항목 " + getName() + "(" + getDocNo() + ") 값이 '"   
							+ value + "'입니다. 형식에 맞지 않습니다. 연계 표준 문서를 참조하세요.(='" + format + "')");
				}
			}
			
		}			 
							
	} // end of validate

	public String toString() {
		String toStr = "id = " + this.id + ", tag = " +this.tag + ", name = " + this.name 
				+ ", type = " + this.type + ", constrain = " + this.constrain 
				+ ", length =" + this.length + ", format =" + this.format + ", fixed =" + this.fixed;
		return toStr;
	}
} // end of class field definition	
