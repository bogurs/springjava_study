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
			throw new DecryptionException("PKCS#7 Msg Type�� �ƴմϴ�. Msg Type : " + p7Type);
		}
		
		bVerify = p7VrfUtil.verify(signedMessage, null, null);
		
		//������� ������ ����
		if(bVerify){
			recvData = p7VrfUtil.getRecvData();
		} else {
			throw new DecryptionException(p7VrfUtil.getErrorMsg());
		}
		
		
		
		return recvData;
	}
	
	/**
	 * ���� ���� ��ȣȭ �� ���� ���� 
	 * @param originMsg ��ȣȭ�ϰ� ������ ���� �޼��� 
	 * @return
	 * @throws IOException 
	 * @throws AgentException
	 */
	public byte[] encrypt(byte[] originMsg, String orgCd) throws Exception {
		
		
			GpkiCmsUtil gpkicmsutil = new GpkiCmsUtil(); 
			String envFile = STRConfigure.getInstance().getAgentInfo().getFiuInfo().getEncryptionInfo().getKeyManageInfo().getCertificate();
			//��ȣȭ�� ���� ����Ű(FIU�� ����Ű)
			byte[] byteGPKIKmCert = getFileByte(envFile);
			
			//��ȣȭ ����(ARIA���)
			byte[] result = gpkicmsutil.genEnvelopedData("ARIA", byteGPKIKmCert, originMsg);
			
			String errMsg = gpkicmsutil.getErrorMsg();
			if(errMsg != null){
				throw new GpkiApiException(errMsg);
			}
			
			//byte[] base64Msg = Base64.encodeBase64(result);
			
			//��ȣȭ �޽��� ��ȯ
			//return envelopMsg;
			return result;	
	}
	
	public byte[] decryptEnveloped(byte[] encryptedMsg, String orgCd) throws IOException {
		try {
			
		//��ȣȭ ���̺귯�� init�� ���� ȯ�漳�� ���丮 ���
		String Gpkiconf = "./pkilib";
				
		byte[] decodedMsg = Base64.decodeBase64(encryptedMsg);
		
		//��ȣȭ ������ ����
		byte[] original;
		
		original = getInstance().decryptEnveloped(decodedMsg, getFileByte(certFile),getFileByte(certKey), certPin, Gpkiconf);
		

		return original;
		
		} catch (DecryptionException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
			return null;
		} catch (GpkiApiException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 20111126 �߰��� : �ű� ó����� (GpkiCmsUtil ���)
	 * ���� ���ŵ� Enveloped �޼����� �Է¹޾� ��ȣ�� ���� �޼����� �����Ѵ�.
	 * @param byte[] envelopedMessage
	 * @param byte[] byteGPKIKmKey - cer ����
	 * @param byte[] byteGPKIKmKey - key ����
	 * @param String strGPKIPasswd
	 * @param String gpkiConfPath - Gpki License ������ ��ġ�� ������
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
			throw new DecryptionException("Enveloped ��ȣȭ ���� : " + gpkicmsutil.getErrorMsg());
		}
		
		byteRecData = gpkicmsutil.getOrgBytes();

		if (byteRecData == null)
		{
			throw new DecryptionException(gpkicmsutil.getErrorMsg());
		}
		
		return byteRecData;
	}
	
	/**
     * ������ ����Ÿ�� �о�´�.
     * ���� ���� ������ ���� ���� �о���� ���� ���ȴ�.
     * @param filepath   ���� �� 
     * @return ���� ����Ÿ 
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
	 * ���� ���� 
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
			throw new IOException("[ERROR] : " + "config ������ �����ϴ� �� ������ �߻��Ͽ����ϴ�.");
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
