package kr.go.kofiu.ctr.util;

import java.util.Iterator;
import java.util.Vector;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Message {

	/**
	 * message.xml - Message 항목의 Attribute message 키 네임 
	 */
	public static final String ROOT = "message";
	
	/**
	 * message.xml - Message 항목의 Attribute id 키 네임 
	 */
	public static final String ID = "id";
	
	/**
	 * message.xml - Message 항목의 Attribute name 키 네임 
	 */
	public static final String NAME = "name";
	
	/**
	 * message.xml - Message 항목의 Attribute delimiter 키 네임 
	 */
	public static final String DELIMITER = "delimiter";
	
	/**
	 * message.xml - Message 항목의 Attribute header 키 네임 
	 */
	public static final String HEADER = "header";
	
	/**
	 * message.xml - Message 항목의 Attribute footer 키 네임 
	 */
	public static final String FOOTER = "footer";
	
	
	/**
	 * id 값 
	 */
	private String id     = null;
	
	/**
	 * 이름 
	 */
	private String name   = null;
	
	/**
	 * 필드 목록 
	 */
	private Vector fields = new Vector();
	
	/**
	 * 생성자 
	 * @param id
	 * @throws FlatFileDataException
	 */
	public Message(String id) throws FlatFileDataException {
		setId(id);	
	}

	/**
	 * 
	 * @param fld
	 * @throws FlatFileDataException
	 */
	public void addField(Field fld) throws FlatFileDataException {
		
		// validation
		if(fld.getId() < 1 ) { throw new FlatFileDataException("ID 값은 양의 숫자이어야 합니다."); }
		if(getFieldById(fld.getId()) != null) { throw new FlatFileDataException(fld.getId() + " 값이 중복되었습니다."); }
		if(getFieldByTag(fld.getTag()) != null) { throw new FlatFileDataException(fld.getTag() + " 값이 중복되었습니다."); }
		
		fields.add(fld);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Field getFieldById(int id) {
		Iterator iter = fields.iterator();
		while(iter.hasNext()) {
			Field field = (Field)iter.next();
			if ( id == field.getId() ) {
				return field;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param tag
	 * @return
	 */
	public Field getFieldByTag(String tag) {
		Iterator iter = fields.iterator();
		while(iter.hasNext()) {
			Field field = (Field)iter.next();
			if( tag.equals(field.getTag())) { 
				return field; 
			}
		}
		return null;
	}

	/**
	 * 
	 * @param xmlSeq
	 * @return
	 */
	public Field getFieldByXmlSeq(String xmlSeq) {
		Iterator iter = fields.iterator();
		while(iter.hasNext()) {
			Field field = (Field)iter.next();
			if( xmlSeq.equals(field.getXmlSeq())) { 
				return field; 
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getId() { 
		return id; 
	}
	
	/**
	 * 
	 * @param id
	 * @throws FlatFileDataException
	 */
	public void setId(String id) throws FlatFileDataException { 
		if(id == null) { 
			throw new FlatFileDataException("Message id is null!"); 
		}
		this.id = id; 
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
			throw new FlatFileDataException("Message name is null!"); 
		}
		this.name = name; 
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 * @throws FlatFileDataException
	 */
	public String[] validatePlainTextMessage(String[] values) throws FlatFileDataException {
		// count check
		if( values.length != (fields.size() + 2)) { 
			throw new FlatFileDataException("CTR 보고 문서 항목 개수가 " + values.length + "개입니다. 항목은 " + (fields.size() + 2) + " 개여야 합니다." ); 
		}
		
		// header / footer check
		if(values[0] == null) { 
			throw new FlatFileDataException("'CTRSTART' 값이 없습니다."); 
		}
		if(!values[0].equals("CTRSTART")) {
			throw new FlatFileDataException(values[0] + "값이 'CTRSTART' 여야 합니다.");  
		}
		
		if(values[values.length-1] == null) { 
			throw new FlatFileDataException("'CTREND' 값이 없습니다."); 
		}
		if(!values[values.length-1].equals("CTREND")) {
			throw new FlatFileDataException(values[values.length-1] + "값이 'CTREND' 여야 합니다." ); 
		}
		
		int i = 0;
		Iterator iter = fields.iterator();
		while(iter.hasNext()) {
			Field field = (Field)iter.next();
			field.validate(values[++i]);
		}
		
		return values;
	}

	public String toString() {
		String toStr = "id = " + this.id + ", name = " + this.name	+ ", fields.size =" + this.fields.size();
		return toStr;
	}

}// end of message class definition
