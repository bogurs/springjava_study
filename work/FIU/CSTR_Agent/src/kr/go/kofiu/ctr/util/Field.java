package kr.go.kofiu.ctr.util;

import java.text.ParseException;

import kr.go.kofiu.ctr.util.DateUtil;

/**
 * *****************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 ******************************************************
 */
public class Field {
	
	/**
	 * message.xml - Field �׸��� Attribute field Ű ���� 
	 */
	public static final String ROOT = "field";
	
	/**
	 * message.xml - Field �׸��� Attribute xmlSeq Ű ���� 
	 */
	public static final String XML_SEQ = "xmlSeq";
	
	/**
	 * message.xml - Field �׸��� Attribute docNo Ű ���� 
	 */
	public static final String DOC_NO = "docNo";
	
	/**
	 * message.xml - Field �׸��� Attribute name Ű ���� 
	 */
	public static final String NAME = "name";
	
	/**
	 * message.xml - Field �׸��� Attribute tag Ű ���� 
	 */
	public static final String TAG = "tag";
	
	/**
	 * �������� �ʵ� mandatory/optional
	 */
	public static final String CONSTRAIN = "constrain";
	
	/**
	 * �������� �ʵ� �� - mandatory
	 */
	public static final String CONSTRAIN_MANDATORY = "Mandatory";

	/**
	 * �������� �ʵ� �� - optional
	 */
	public static final String CONSTRAIN_OPTIONAL = "Optional";

	/**
	 * �������� �ʵ� �� - NULL
	 */
	public static final String CONSTRAIN_NULL = "Null";
	
	
	/**
	 * ���� Type ���� - Code/String/Date/Number
	 */
	public static final String TYPE = "type";

	/**
	 * ���� Type ���� - Code/String/Date/Number
	 */
	public static final String CODESET = "codeset";
	
	/**
	 * ���� Type ���� - Code/String/Date/Number
	 */
	public static final String LENGTH = "length";
	
	/**
	 * ���� Type ���� - Code/String/Date/Number
	 */
	public static final String FIXED = "fixed";
	
	/**
	 * ���� Type ���� - Code/String/Date/Number
	 */
	public static final String FORMAT = "format";
	

	/**
	 * Type Code �� - �ڵ� 
	 */
	public static final String TYPE_CODE = "Code";
	
	/**
	 * Type Code �� - ����  
	 */
	public static final String TYPE_STRING = "String";
	
	/**
	 * Type Code �� - ��¥  
	 */
	public static final String TYPE_DATE = "Date";
	
	/**
	 * Type Code �� - ����  
	 */
	public static final String TYPE_NUMBER = "Number";

	/**
	 * Type Code �� - ��ȭ��ȣ ����
	 */
	public static final String TYPE_TELEPHONE_NUMBER = "TelephoneNumber";
	
	/**
	 * �ڸ��� ���� ���� - true
	 */
	public static final String FIXED_TRUE = "true";

	/**
	 * �ڸ��� ���� ���� - false
	 */
	public static final String FIXED_FALSE = "false";
	
	
	/**
	 * �ʵ� �Ƶ�� ��
	 */
	private int id		  			= -1;
	
	/**
	 * xml ���� ��ȣ 
	 */
	private String xmlSeq			= null;
	
	/**
	 * ���� ��ȣ 
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
	 * ���� ���� 
	 */
	private String constrain		= null;
	
	/**
	 * 
	 */
	private String type				= null;
	
	/**
	 * �ڵ� �� 
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
	 * ���� �ʵ忡 �Ҵ�Ǵ� ��
	 */
	private String value			= null;
	
	
	/**
	 * ������ 
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
				throw new FlatFileDataException("�ʵ� id ���� �����ϴ�."); 
			}
		} catch(Exception e) { 
			throw new FlatFileDataException("�ʵ� id �� '" + idStr 
					+ "'�� �������̾�� �մϴ�.", e); 
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
			throw new FlatFileDataException("�ʵ� xmlSeq ���� �����ϴ�."); 
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
			throw new FlatFileDataException("�ʵ� ���� ��ȣ(docNo)���� �����ϴ�."); 
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
			throw new FlatFileDataException("�ʵ� Tag ���� �����ϴ�."); 
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
			throw new FlatFileDataException("�ʵ� �̸��� �����ϴ�."); 
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
			throw new FlatFileDataException(getName() + "'�� constrain ����   '" + constrain + "' �ùٸ��� �ʽ��ϴ�."); 
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
				throw new FlatFileDataException("�ʵ� ������ type�� '" +type + "'�� �ùٸ��� �ʽ��ϴ�."); 
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
			throw new FlatFileDataException(getName() + " �� '" + lengthStr 
					+ "'�� ���ڰ� �ƴմϴ�.", e); 
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
			throw new FlatFileDataException(fixed + " �ùٸ� ���� �ƴմϴ�."); 
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
		
		// Madatory üũ 
		if( CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain)
				&& ( value == null || value.length() == 0 ) ) {
			throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
					+ " ���� �����ϴ�. " + constrain + " �� �Դϴ�.");
		}

		// Null üũ. ��� ���� �� ��츸 ����
		if ( CONSTRAIN_NULL.equalsIgnoreCase(constrain) && ( value != null && value.length() > 0) ){
			throw new FlatFileDataException("��� ������ ��� CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
					+ " ���� �����ؼ��� �ȵ˴ϴ�. ���� '" +value + "' ���� �����մϴ�.");
		}

		// optional
		if ( !CONSTRAIN_MANDATORY.equalsIgnoreCase(constrain) 
				&& ( value == null || value.length() == 0) ) {
			return ;
		}
		
		// length check
		if ( this.getLength() > -1 ) {
			if(value.getBytes().length > getLength()) {
				throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
						+ " ���� " + getLength() + " ���� ū " + value.getBytes().length + " �Դϴ�.");
			}
		
			if( isFixed() ) {
				if(value.getBytes().length != getLength() ) { 
					throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
							+ " ���� ���� ���̴� " + getLength() + "�� ��ġ �ؾ��մϴ�. �׷��� ���� ����(" + value+") ���̰� "
							+ value.getBytes().length + " �Դϴ�.");
				}
			}
		}
		
		// type check
		if( getType().equalsIgnoreCase(TYPE_NUMBER)) {
			try { 
				Long.parseLong(value);
				//Integer.parseInt(value); 
			} catch(Exception e) { 
				throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
						+ " ���� '" + value + "' �� ���������� �ƴմϴ�.", e); 
			}
		} else if( getType().equalsIgnoreCase(TYPE_DATE)) {
			if( this.format == null) { 
				throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
						+ " ��¥ ���� ���� �����ϴ�."); 
			}
			
			try{ 
				
				DateUtil.parse(format, value); 
			} catch(ParseException e) { 
				throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ")"   
						+ "�� ���� '" + value + "' �� ��¥  ������ �ùٸ��� �ʽ��ϴ�."); 
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
				throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ") ���� '"   
						+ value + "'�Դϴ�. �ڵ� ���� �ùٸ��� �ʽ��ϴ�. �ڵ� ���� ���� �� �ϳ����� �մϴ�. " + this.codeset);
			}				
		} else if(getType().equalsIgnoreCase(TYPE_TELEPHONE_NUMBER)) {
			// 2006/01/26 ��ȭ��ȣ üũ ���� ����
				
			 String[] strTel = value.split("-");
			 if( strTel.length > 4 ) 
					throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ") ���� '"   
							+ value + "'�Դϴ�. ��ȭ��ȣ�� '-' 3���� ���еǾ�߸� �մϴ�.");

			 for( int i = 0 ; i < strTel.length ; i++ )	 {
				 try {
					 // 02-507-2333-  ����ó�� ������ȣ�� ���� ����.
					 if( i < 3 ) {
						 Integer.parseInt(strTel[i]);
					 } else if ( strTel[i].length() != 0 ) {
						 Integer.parseInt(strTel[i]);
					 }
				 } catch (NumberFormatException e){
					  throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ") ���� '"   
								+ value + "'�Դϴ�. �� �� '" + strTel[i] + "'�� ���ڿ��� �մϴ�.");
				 }
			 }
		} else if(getType().equalsIgnoreCase(TYPE_STRING)) {
			if( this.format != null && !"".equals(format)) {
				if (!value.matches(this.format)) {
					throw new FlatFileDataException("CTR ������ �׸� " + getName() + "(" + getDocNo() + ") ���� '"   
							+ value + "'�Դϴ�. ���Ŀ� ���� �ʽ��ϴ�. ���� ǥ�� ������ �����ϼ���.(='" + format + "')");
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
