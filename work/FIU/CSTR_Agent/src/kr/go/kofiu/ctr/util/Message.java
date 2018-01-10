package kr.go.kofiu.ctr.util;

import java.util.Iterator;
import java.util.Vector;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 30
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class Message {

	/**
	 * message.xml - Message �׸��� Attribute message Ű ���� 
	 */
	public static final String ROOT = "message";
	
	/**
	 * message.xml - Message �׸��� Attribute id Ű ���� 
	 */
	public static final String ID = "id";
	
	/**
	 * message.xml - Message �׸��� Attribute name Ű ���� 
	 */
	public static final String NAME = "name";
	
	/**
	 * message.xml - Message �׸��� Attribute delimiter Ű ���� 
	 */
	public static final String DELIMITER = "delimiter";
	
	/**
	 * message.xml - Message �׸��� Attribute header Ű ���� 
	 */
	public static final String HEADER = "header";
	
	/**
	 * message.xml - Message �׸��� Attribute footer Ű ���� 
	 */
	public static final String FOOTER = "footer";
	
	
	/**
	 * id �� 
	 */
	private String id     = null;
	
	/**
	 * �̸� 
	 */
	private String name   = null;
	
	/**
	 * �ʵ� ��� 
	 */
	private Vector fields = new Vector();
	
	/**
	 * ������ 
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
		if(fld.getId() < 1 ) { throw new FlatFileDataException("ID ���� ���� �����̾�� �մϴ�."); }
		if(getFieldById(fld.getId()) != null) { throw new FlatFileDataException(fld.getId() + " ���� �ߺ��Ǿ����ϴ�."); }
		if(getFieldByTag(fld.getTag()) != null) { throw new FlatFileDataException(fld.getTag() + " ���� �ߺ��Ǿ����ϴ�."); }
		
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
			throw new FlatFileDataException("CTR ���� ���� �׸� ������ " + values.length + "���Դϴ�. �׸��� " + (fields.size() + 2) + " ������ �մϴ�." ); 
		}
		
		// header / footer check
		if(values[0] == null) { 
			throw new FlatFileDataException("'CTRSTART' ���� �����ϴ�."); 
		}
		if(!values[0].equals("CTRSTART")) {
			throw new FlatFileDataException(values[0] + "���� 'CTRSTART' ���� �մϴ�.");  
		}
		
		if(values[values.length-1] == null) { 
			throw new FlatFileDataException("'CTREND' ���� �����ϴ�."); 
		}
		if(!values[values.length-1].equals("CTREND")) {
			throw new FlatFileDataException(values[values.length-1] + "���� 'CTREND' ���� �մϴ�." ); 
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
