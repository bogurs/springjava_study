package kr.go.kofiu.ctr.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 3
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageBoxEnv {
	
	/**
	 * ��� �ڵ� ���� 
	 */
	Collection col;
	
	/**
	 * 2�� ��� ����
	 */
	private int outboxIOWaitMinutes = 0;
	
	/**
	 * MessageBox ������ ��� �ڵ� ���� ���� ���� ���� 
	 */
	private boolean folderPerFcltyCode;
	
	/**
	 * ������ 
	 *
	 */
	public MessageBoxEnv( ) {
		col = new ArrayList();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFolderPerFcltyCode() {
		return folderPerFcltyCode;
	}
	
	

	public int getOutboxIOWaitMinutes() {
		return outboxIOWaitMinutes;
	}

	public void setOutboxIOWaitMinutes(int outboxIOWaitMinutes) {
		this.outboxIOWaitMinutes = outboxIOWaitMinutes;
	}

	/**
	 * 
	 * @param folderPerFcltyCode
	 */
	public void setFolderPerFcltyCode(boolean folderPerFcltyCode) {
		this.folderPerFcltyCode = folderPerFcltyCode;
	}

	/**
	 * 
	 * @param fcltyCode
	 */
	public void setFcltyCode(String fcltyCode){
		col.add(fcltyCode);
	}

	/**
	 * 
	 * @return
	 */
	public Collection getFcltyCodes () {
		return col;
	}

	public String toString() {
		// MessageBoxEnv
		String xmlText = "\t<MessageBoxEnv folderPerFcltyCode=\"" 
					+ Boolean.toString(this.isFolderPerFcltyCode()) + "\">\n";
		Iterator iter = col.iterator();
		while(iter.hasNext() ){
			String fcltyCode = (String)iter.next();
			xmlText += "\t\t<fcltyCode>" + fcltyCode + "</fcltyCode>\n";
		}
		xmlText += "\t</MessageBoxEnv>\n";
		return xmlText;
	}

}
