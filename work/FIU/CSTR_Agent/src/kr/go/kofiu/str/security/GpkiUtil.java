package kr.go.kofiu.str.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.str.conf.EncryptionInfo;
import kr.go.kofiu.str.conf.STRConfigure;

import org.apache.commons.codec.binary.Base64;

import signgate.crypto.util.CertUtil;
import signgate.crypto.util.GpkiCmsUtil;
import signgate.crypto.util.PKCS7Util;

import com.gpki.gpkiapi.GpkiApi;
import com.gpki.gpkiapi.exception.GpkiApiException;

public class GpkiUtil {
	
	private String certFile;
	private String certKey;
	private String certPin;
	
	private static GpkiUtil instance = null;
	
	private GpkiUtil() {
		
		try{
			String gpkiPath = STRConfigure.getInstance().getAgentInfo().getFilePathInfo().getGPKICONF();
			GpkiApi.init(gpkiPath);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static GpkiUtil getInstance() {
		if (instance == null) {
			instance = new GpkiUtil();
		} 
		return instance;
	}
	
	public byte[] decryptSigned(byte[] signedMessage) throws DecryptionException {
		
		boolean bVerify = false;
		byte[] recvData = null;
		int p7Type = 0; 
		
		PKCS7Util p7VrfUtil = new PKCS7Util();
		p7Type = p7VrfUtil.getPKCS7Type(signedMessage);
		
		if (p7Type != 1 && p7Type != 2 && p7Type != 3) {
			throw new DecryptionException("PKCS#7 Msg Type이 아닙니다. Msg Type : " + p7Type);
		}
		
		bVerify = p7VrfUtil.verify(signedMessage, null, null);
		
		//서명검증 데이터 추출
		if(bVerify){
			recvData = p7VrfUtil.getRecvData();
		} else {
			throw new DecryptionException(p7VrfUtil.getErrorMsg());
		}
		
		
		
		return recvData;
	}
	
	/**
	 * 보고 문서 암호화 및 전자 서명 
	 * @param originMsg 암호화하고 서명할 원본 메세지 
	 * @return
	 * @throws IOException 
	 * @throws AgentException
	 */
	public byte[] encrypt(byte[] originMsg, String orgCd) throws Exception {
		
		
			GpkiCmsUtil gpkicmsutil = new GpkiCmsUtil(); 
			String envFile = STRConfigure.getInstance().getAgentInfo().getFiuInfo().getEncryptionInfo().getKeyManageInfo().getCertificate();
			//암호화를 위한 공개키(FIU의 공개키)
			byte[] byteGPKIKmCert = getFileByte(envFile);
			
			//암호화 진행(ARIA방식)
			byte[] result = gpkicmsutil.genEnvelopedData("ARIA", byteGPKIKmCert, originMsg);
			
			String errMsg = gpkicmsutil.getErrorMsg();
			if(errMsg != null){
				throw new GpkiApiException(errMsg);
			}
			
			//byte[] base64Msg = Base64.encodeBase64(result);
			
			//암호화 메시지 반환
			//return envelopMsg;
			return result;	
	}
	
	public byte[] decryptEnveloped(byte[] encryptedMsg, String orgCd) throws IOException {
		try {
			
		//암호화 라이브러리 init을 위한 환경설정 디렉토리 경로
		String Gpkiconf = "./pkilib";
				
		byte[] decodedMsg = Base64.decodeBase64(encryptedMsg);
		
		//복호화 데이터 형식
		byte[] original;
		
		original = getInstance().decryptEnveloped(decodedMsg, getFileByte(certFile),getFileByte(certKey), certPin, Gpkiconf);
		

		return original;
		
		} catch (DecryptionException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			return null;
		} catch (GpkiApiException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 20111126 추가됨 : 신규 처리방식 (GpkiCmsUtil 사용)
	 * 서명 제거된 Enveloped 메세지를 입력받아 복호후 원문 메세지를 리턴한다.
	 * @param byte[] envelopedMessage
	 * @param byte[] byteGPKIKmKey - cer 파일
	 * @param byte[] byteGPKIKmKey - key 파일
	 * @param String strGPKIPasswd
	 * @param String gpkiConfPath - Gpki License 파일이 위치한 절대경로
	 * @return byte[] byteRecData
	 * @throws DecryptionException
	 * @throws GpkiApiException 
	 */
	public byte[] decryptEnveloped(byte[] envelopedMessage, byte[] byteGPKIKmCert, byte[] byteGPKIKmKey, String strGPKIPasswd, String gpkiConfPath) throws GpkiApiException {

		boolean bverify = false;
		byte[] byteRecData = null;
		
		//GpkiApi License Load
		GpkiApi.init(gpkiConfPath);
		GpkiCmsUtil gpkicmsutil = new GpkiCmsUtil();
		
		bverify = gpkicmsutil.verifyEnveloped("ARIA", envelopedMessage, byteGPKIKmCert, byteGPKIKmKey, strGPKIPasswd);
		
		if (!bverify)
		{
			throw new DecryptionException("Enveloped 복호화 실패 : " + gpkicmsutil.getErrorMsg());
		}
		
		byteRecData = gpkicmsutil.getOrgBytes();

		if (byteRecData == null)
		{
			throw new DecryptionException(gpkicmsutil.getErrorMsg());
		}
		
		return byteRecData;
	}
	
	/**
     * 파일의 데이타를 읽어온다.
     * 접수 증서 인증서 파일 값을 읽어오기 위해 사용된다.
     * @param filepath   파일 명 
     * @return 파일 데이타 
  	 * @throws IOException 
    */
   	public static byte[] getFileByte(File f) throws IOException {
        // default window
  		byte[] bytes = null;
  		int offset = 0;
  		int numRead = 0;
  		BufferedInputStream bin = null;
  		try {
  			
  			bytes = new byte[(int) f.length()];
  			bin = new BufferedInputStream(new FileInputStream(f));
  			
  			while (offset < bytes.length
  					&& (numRead = bin.read(bytes, offset, bytes.length - offset)) >= 0) {
  				offset += numRead;
  			}

  			bin.close();
  		} finally {
  			if ( bin != null ) try { bin.close(); } catch (IOException e) { /* ignore */}
  		}

  		return bytes;
  	}

   	/**
   	 * 
   	 * @param filename
   	 * @return
   	 * @throws IOException
   	 */
	public static byte[] getFileByte(String filename) throws IOException {
		return getFileByte(new File(filename));
	}
	
	/**
	 * 전자 서명 
	 * 
	 * @param msg
	 * @return
	 * @throws IOException 
	 * @throws CertificateException 
	 */
	public byte[] makeSignature(byte[] msg) throws IOException 
	{
		String signCert = "";
		String signKey ="";
		String signPin = "";
		try{
		
			EncryptionInfo encryptionInfo = STRConfigure.getInstance().getAgentInfo().getFiuInfo().getEncryptionInfo();
			
			signCert = encryptionInfo.getSigningInfo().getCertificate(); 
			signKey = encryptionInfo.getSigningInfo().getKey();
			signPin = encryptionInfo.getSigningInfo().getPin();
		
		}catch(Exception e){
			throw new IOException("[ERROR] : " + "config 정보를 수신하는 중 에러가 발생하였습니다.");
		}
		
		CertUtil certutil = null;
		String signedMsg = null;
		
		try 
		{
			certutil = new CertUtil(signCert);
		} 
		catch (CertificateException e) 
		{
			throw new IOException("[ERROR] : " + certutil.getErrorMsg());
		}
		
		PKCS7Util p7SignUtil = new PKCS7Util();
			
		try
		{
			signedMsg = p7SignUtil.genSignedData(getFileByte(signKey), signPin, getFileByte(signCert), msg);
		}
		catch (Exception e)
		{
			System.out.println(p7SignUtil.getStackTraceMsg());
			throw new IOException("[ERROR] : " + p7SignUtil.getErrorMsg());
		}
		return signedMsg.getBytes();
		
	}
	
}
