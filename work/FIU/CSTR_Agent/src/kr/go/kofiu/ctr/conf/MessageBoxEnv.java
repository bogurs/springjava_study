package kr.go.kofiu.ctr.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 3
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class MessageBoxEnv {
	
	/**
	 * 기관 코드 집합 
	 */
	Collection col;
	
	/**
	 * 2분 대기 설정
	 */
	private int outboxIOWaitMinutes = 0;
	
	/**
	 * MessageBox 관리시 기관 코드 폴더 별로 정리 여부 
	 */
	private boolean folderPerFcltyCode;
	
	/**
	 * 생성자 
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
