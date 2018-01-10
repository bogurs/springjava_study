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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent 업데이트를 수행한다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 11. 29
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentUpdateCommand extends RetryCommand {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AgentUpdateCommand.class);

	/**
	 * 생성자
	 * @param numOfRetries 재시도 횟수 
	 * @param timeToWait 실패 후 다음 재시도를 몇초 후에 할지에 대한 시간 값. 단위 초
	 */
	public AgentUpdateCommand( int numOfRetries, long timeToWait) {
		super(numOfRetries, timeToWait);
	}

	/**
	 * 집중 기관에 업데이트 요청을 수행한다.
	 * @throws AgentException 
	 */
	protected Object action() throws AgentException {
		UpdateResponse response = new UpdateResponse(UpdateResponse.UPDATE);
		try { 
			// http로 모듈 추가 다운로드 
			JMSSOAPService jmssoapService = JMSSOAPService.getInstance();			
			
			logger.info("연계 모듈 라이브러리 파일 다운로드 시작");
			String GUID = getGUID_FIUF0001();
			EsbCheckServiceInfo checkServiceInfo = (EsbCheckServiceInfo) jmssoapService.EsbSoapService(Controller.CTRMODULEUPDATE, GUID);
			
			response.setNewModuleVersion(checkServiceInfo.getRPT_MDL_VER());
			
			//15.03.19 수정
			//기존 외부포탈 기능 중 업데이트에 LIB 또는 KEY 파일은 필수 값이 아니므로 널체크를 진행해야 함.
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

			logger.info("연계 모듈 라이브러리 파일 웹으로 다운로드 완료.");
			//new ReportAction(Controller.CTRMODULEUPDATE, checkServiceInfo.getResultCode(), checkServiceInfo.getResultMessage()).doAct();
			
		} catch (Exception e) {
			logger.info("CTR 업데이트에 실패하였습니다. Agent를 종료합니다.");
			CTRAgent.getInstance().shutdown();
		}
		
		return response;
		
	}
	
	public String getGUID_FIUF0001(){ //Check Agent용 GUID
		java.util.Random r = new java.util.Random(); //java.util.Random r = new Random(); 이라고 쓸 수 있다.         
		int num = r.nextInt(10000);
		String randomValue = String.format("%04d", num);
        String agentId = Configure.getInstance().getAgentInfo().getId();
		String GUID = agentId+"CTR"+CurrentTimeGetter.formatCustom("yyyyMMddHHmmssSSS")+randomValue;
		return GUID;
	}

	
    /**
     * Get 방식으로 대상 웹서버에 요청을 보낸다.
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
