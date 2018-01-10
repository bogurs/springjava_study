package kr.go.kofiu.ctr.util;


import java.io.File;
import java.io.IOException;

import kr.go.kofiu.common.agent.AgentException;
import kr.go.kofiu.ctr.actions.ShutdownAction;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.EncryptionInfo;
import kr.go.kofiu.ctr.util.excption.DecryptionException;

import com.gpki.gpkiapi.exception.GpkiApiException;
/* song import kr.go.kofiu.ctr.util.GpkiEncryptor;
import kr.go.kofiu.ctr.util.IssacWebSecurity;

import com.gpki.sdk.bs.GpkiBASE64;
import com.gpki.sdk.bs.GpkiCERTIFICATE;
import com.gpki.sdk.bs.GpkiPUBLICKEY;
import com.penta.issacweb.IssacWebJNIServer;
*/

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� ���� ���� ���� �� ��ȣȭ, ���� ���� ��ȣȭ �� ���ڼ��� ������ ���� HelperŬ����
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SecurityUtil {

	/**
	 * ���� ���� ��ȣȭ �� ���� ���� 
	 * @param originMsg ��ȣȭ�ϰ� ������ ���� �޼��� 
	 * @return
	 * @throws AgentException
	 * @throws GpkiApiException 
	 */
	public static byte[] encrypt(byte[] originMsg) throws AgentException, GpkiApiException {		
		EncryptionInfo encryptInfo = Configure.getInstance().getAgentInfo().getEncryptionInfo();
		boolean isEncrption = encryptInfo.isEnabled();
		boolean isSigning = encryptInfo.getSigningInfo().isEnabled();
		
		String certFile = CtrAgentEnvInfo.getFIUPublicKeyFile();
		String signCert = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getSigningInfo().getCertificate();
		String signKey  = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getSigningInfo().getKey();
		String pin      = encryptInfo.getSigningInfo().getPin();
		String Gpkiconf = CtrAgentEnvInfo.GPKICONF_DIR;
		
		try {
			SgGpkiEncryptor encryptor = null;
			if ( isEncrption && isSigning ) {
				encryptor = new SgGpkiEncryptor(FileTool.getFileByte(certFile)
						, originMsg , pin
						, FileTool.getFileByte(signCert)
						, FileTool.getFileByte(signKey));
				
			} else if ( isEncrption && !isSigning  ) {
				encryptor = new SgGpkiEncryptor(FileTool.getFileByte(certFile), originMsg);
				
			} else if ( !isEncrption && isSigning  ) {
				encryptor = new SgGpkiEncryptor(originMsg , pin
						, FileTool.getFileByte(signCert)
						, FileTool.getFileByte(signKey));
			} else { 
				return originMsg;
			}
			
			byte[] envelopMsg = encryptor.processs(Gpkiconf);
			
			return envelopMsg;
		} catch (IOException e) {
			AgentException ae = new AgentException(e);
			//ae.addAction(new EmailAction("Agent���� ���� ���� ��ȣȭ�� ���� ������ ����  �б� ���� ", e));
			ae.addAction(new ShutdownAction());
			throw ae;
		}		
	}
	
	/**
	 * ���� ���� ��ȣȭ �� ���� ���� 
	 * @param encryptedMsg ���� ������ ��ȣȭ�� �޼���
	 * @return
	 * @throws IOException
	 */
	/*
	 2011 ���ȸ�� ��ȭ
	  �����ҽ� �ּ�ó��
	 public static byte[] decrypt(byte[] encryptedMsg) throws IOException {
		EncryptionInfo encryptInfo = Configure.getInstance().getAgentInfo().getEncryptionInfo();

		String certKey  = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getPrivateCertInfo().getCertificate();//"test1_env_seed.key";
		String pin = encryptInfo.getPrivateCertInfo().getPin();

		byte[] original = IssacWebSecurity.decrypt(encryptedMsg, FileTool.getFileByte(certKey), pin) ;
		return original;
	}
	*/
	
	/**
	 * 2011 ���ȸ�� ��ȭ
	 * ���� ���� ��ȣȭ �� ���� ���� 
	 * @param encryptedMsg ���� ������ ��ȣȭ�� �޼���
	 * @return
	 * @throws IOException
	 * @throws GpkiApiException 
	 * @throws DecryptionException 
	 */
	/*���������� ���� ��ȭȭ �� ���ڼ��� ������ ���� ������� �ʴ´�.FIUCF���� ���������� ���� ��ȣȭ��
	 * ���ڼ����� ���� �ʱ� �����̴�.
	 */
	public static byte[] decrypt(byte[] encryptedMsg) throws IOException, DecryptionException, GpkiApiException {
		EncryptionInfo encryptInfo = Configure.getInstance().getAgentInfo().getEncryptionInfo();

		String certKey  = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getPrivateCertInfo().getCertificate();//"test1_env_seed.key";
		/*��ȣȭ�� GPKI����Ű �߰�*/
		String GPKIKmCert = CtrAgentEnvInfo.getFIUPublicKeyFile();
		/* GPKI���̼��������� �ִ� ��θ� �߰�
		 * GPKI Init�� ���� �ʿ��ϴ�.
		 */
		String Gpkiconf = CtrAgentEnvInfo.GPKICONF_DIR;
		String pin = encryptInfo.getPrivateCertInfo().getPin();
		
		/**
		 * ����
		 * �������ŵ� ��ȣȭ �޽����� �޾� ��ȣȭ ó���Ѵ�.
		 */
		byte[] original = SecuKit.getInstance().decrypt(encryptedMsg, FileTool.getFileByte(GPKIKmCert),FileTool.getFileByte(certKey), pin, Gpkiconf) ;
		
		
		return original;
	}

/*	2011 ���ȸ�� ��ȭ
	�׽�Ʈ �ҽ��� �ּ�ó���Ѵ�.
	private String testEncrypt(String message) throws IOException{
		GpkiCERTIFICATE cert = new GpkiCERTIFICATE();
		String ceraat = CtrAgentEnvInfo.getFIUPublicKeyFile();
		cert.Read_File(ceraat);
		
		GpkiPUBLICKEY  pk = new GpkiPUBLICKEY();
		
		pk.ReadFromCertificate(cert);

		byte[] pkbyte = pk.Write_Memory_As_RSAPublicKey();
		GpkiBASE64 base64 = new GpkiBASE64();

		String pkStr = base64.Encode(pkbyte);

		IssacWebJNIServer issac =  new IssacWebJNIServer();
		byte[] ss = new byte[16];
		String encrypted = issac.issacweb_hybrid_encrypt_s(message, ss, pkStr);
		return encrypted;
	}

	public static void main(String[] args)  {
		SecurityUtil Main = new SecurityUtil();
		System.out.println("CTR ������ ��ȣȭ ��� �׽�Ʈ ....");
		String message = "Hello, World!";
		try {
			System.out.println("'"  + message + "' ��ȣȭ ���� ....");
			String encrypted = Main.testEncrypt(message);
			System.out.println("��ȣȭ ���� - '"  + encrypted + "'" );
		} catch (Throwable t) {
			System.out.println("��ȣȭ ����... " );
			t.printStackTrace();
		}
	}
*/
}
