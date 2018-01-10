package kr.go.kofiu.ctr.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.common.agent.Controller;
import kr.go.kofiu.common.agent.STRAgent;
import kr.go.kofiu.ctr.actions.ReportAction;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.EsbCheckServiceInfo;
import kr.go.kofiu.ctr.util.RetryCommand;
import kr.go.kofiu.ctr.util.UpdateResponse;
import kr.go.kofiu.str.util.CurrentTimeGetter;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Agent ������Ʈ�� �����Ѵ�.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateCommand extends RetryCommand {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateCommand.class);

	/**
	 * ������
	 * @param numOfRetries ��õ� Ƚ�� 
	 * @param timeToWait ���� �� ���� ��õ��� ���� �Ŀ� ������ ���� �ð� ��. ���� ��
	 */
	public AgentUpdateCommand( int numOfRetries, long timeToWait) {
		super(numOfRetries, timeToWait);
	}

	/**
	 * ���� ����� ������Ʈ ��û�� �����Ѵ�.
	 * @throws AgentException 
	 */
	protected Object action() throws AgentException {
		UpdateResponse response = new UpdateResponse(UpdateResponse.UPDATE);
		try { 
			// http�� ��� �߰� �ٿ�ε� 
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();			
			
			logger.info("���� ��� ���̺귯�� ���� �ٿ�ε� ����");
			String GUID = getGUID_FIUF0001();
			EsbCheckServiceInfo checkServiceInfo = (EsbCheckServiceInfo) jmssoapService.EsbSoapService(Controller.CTRMODULEUPDATE, GUID);
			
			response.setNewModuleVersion(checkServiceInfo.getRPT_MDL_VER());
			
			//15.03.19 ����
			//���� �ܺ���Ż ��� �� ������Ʈ�� LIB �Ǵ� KEY ������ �ʼ� ���� �ƴϹǷ� ��üũ�� �����ؾ� ��.
			String rptMdlBlobMdlLib = checkServiceInfo.getRPT_MDL_BLOB_MDL_LIB();
			String rptMdlBlobPblKey = checkServiceInfo.getRPT_MDL_BLOB_PBL_KEY();
			if(rptMdlBlobMdlLib != null || rptMdlBlobMdlLib != ""){
				response.setMdlLib(Base64.decodeBase64(rptMdlBlobMdlLib));
			}
//			response.setMdlLib(Base64.decodeBase64(checkServiceInfo.getRPT_MDL_BLOB_MDL_LIB()));
			//response.setConfig(Base64.decodeBase64(checkServiceInfo.getRPT_MDL_BLOB_CONFIG()));
			if(rptMdlBlobPblKey != null || rptMdlBlobPblKey != ""){
				response.setPblKey(Base64.decodeBase64(rptMdlBlobPblKey));
			}
//			response.setPblKey(Base64.decodeBase64(checkServiceInfo.getRPT_MDL_BLOB_PBL_KEY()));

			logger.info("���� ��� ���̺귯�� ���� ������ �ٿ�ε� �Ϸ�.");
			//new ReportAction(Controller.CTRMODULEUPDATE, checkServiceInfo.getResultCode(), checkServiceInfo.getResultMessage()).doAct();
			
		} catch (Exception e) {
			logger.info("CTR ������Ʈ�� �����Ͽ����ϴ�. Agent�� �����մϴ�.");
			CTRAgent.getInstance().shutdown();
		}
		
		return response;
		
	}
	
	public String getGUID_FIUF0001(){ //Check Agent�� GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); �̶�� �� �� �ִ�.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

	
    /**
     * Get ������� ��� �������� ��û�� ������.
     * @param destURL target server url
     * @return String  return value, if error occurred while request, result value is null;
     **/
    public byte[] requestHttpGetMethod(String destURL) throws IOException{
        byte[] result = null;
        try{
            URL url = new URL( destURL );
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            //con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            InputStream in = con.getInputStream();

            logger.debug("content type : " + con.getContentType());
            logger.debug("code : " + con.getResponseCode());
            logger.debug("message : " + con.getResponseMessage());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024*200);
            int ch = in.read();
            while(ch != -1 ) {
                baos.write(ch);
                ch = in.read();
            }
            in.close();
            result = baos.toByteArray();
            
      } catch (IOException ioe){
              throw ioe;
      }
      return result;
  }
	
}
