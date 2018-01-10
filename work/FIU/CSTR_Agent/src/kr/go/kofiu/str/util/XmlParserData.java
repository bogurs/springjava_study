package kr.go.kofiu.str.util;

import kr.go.kofiu.str.conf.STRConfigure;

public class XmlParserData {
	
	/**
	 * ����� ���� SOAP ����
	 * 
	 * @param userId ����� ���̵�
	 * @param orgCd  ����ڵ�
	 * @return xmlString
	 */
	public static String getUserCheckXML(String userId, String orgCd){
		String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ctr=\"http://www.kofiu.co.kr/ctr/\">" +
				"<soapenv:Header/>" +
				"		   <soapenv:Body>" +
				"		      <ctr:processModuleCheck>" +
				"		         <operation>STRUSERCHECK</operation>" +
				"		         <agentInfo>" +
				"           		<svrCd>STR</svrCd>" +
				"		            <agentId>"+STRConfigure.getInstance().getAgentInfo().getId()+"</agentId>" +
				"		            <agentIp/>" +
				"		            <moduleVersion/>" +
				"		         </agentInfo>" +
				"				 <userInfo>" +
	            "					<userId>"+userId+"</userId>" +
	            "					<orgCode>"+orgCd+"</orgCode>" +
	            "				</userInfo>" +
				"		      </ctr:processModuleCheck>" +
				"		   </soapenv:Body>" +
				"		</soapenv:Envelope>";
		
		return data;
	}
	
	/**
	 * ��� ������Ʈ JMS SOAP ����
	 * 
	 * @return xmlString
	 */
	public static String getModuleUpdateXML(){
		String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ctr=\"http://www.kofiu.co.kr/ctr/\">" +
				"<soapenv:Header/>" +
				"		   <soapenv:Body>" +
				"		      <ctr:processModuleCheck>" +
				"		         <operation>STRMODULEUPDATE</operation>" +
				"		         <agentInfo>" +
				"           		<svrCd>STR</svrCd>" +
				"		            <agentId>"+STRConfigure.getInstance().getAgentInfo().getId()+"</agentId>" +
				"		            <agentIp/>" +
				"		            <moduleVersion/>" +
				"		         </agentInfo>" +
				"		      </ctr:processModuleCheck>" +
				"		   </soapenv:Body>" +
				"		</soapenv:Envelope>";
		
		return data;
	}
	
	/**
	 * Agent Check JMS SOAP ����
	 * 
	 * @param nowVer Agent ���� ����
	 * @return xmlString
	 */
	public static String getCheckAgentXML(String nowVer){
		 String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ctr=\"http://www.kofiu.co.kr/ctr/\">" +
					"   <soapenv:Header/>" +
					"   <soapenv:Body>" +
					"		<ctr:processModuleCheck>" +
					"         <operation>STRCHECKAGENT</operation>" +
					"         <agentInfo>" +
					"            <svrCd>STR</svrCd>" +
					"            <agentId>"+STRConfigure.getInstance().getAgentInfo().getId()+"</agentId>" +
					"            <agentIp></agentIp>" +
					"            <moduleVersion>"+nowVer+"</moduleVersion>" +
					"         </agentInfo>" +
					"      </ctr:processModuleCheck>" +
					"   </soapenv:Body>" +
					"</soapenv:Envelope>";
		return data;
	}
	
	/**
	 * �ｺüũ JMS SOAP ����
	 * 
	 * @return xmlString
	 */
	public static String getHeartBeatXML(){
		String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ctr=\"http://www.kofiu.co.kr/ctr/\">" +
				"   <soapenv:Header/>" +
				"   <soapenv:Body>" +
				"		<ctr:processModuleCheck>" +
				"         <operation>HEARTBEAT</operation>" +
				"         <agentInfo>" +
				"            <svrCd>STR</svrCd>" +
				"            <agentId>"+STRConfigure.getInstance().getAgentInfo().getId()+"</agentId>" +
				"            <agentIp></agentIp>" +
				"            <moduleVersion></moduleVersion>" +
				"         </agentInfo>" +
				"      </ctr:processModuleCheck>" +
				"   </soapenv:Body>" +
				"</soapenv:Envelope>";
		
		return data;
	}

}
