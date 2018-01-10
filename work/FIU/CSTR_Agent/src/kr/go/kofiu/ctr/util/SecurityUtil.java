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
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 보고 문서 전자 서명 및 암호화, 접수 증서 복호화 및 전자서명 검증을 위한 Helper클래스
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class SecurityUtil {

	/**
	 * 보고 문서 암호화 및 전자 서명 
	 * @param originMsg 암호화하고 서명할 원본 메세지 
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
			//ae.addAction(new EmailAction("Agent에서 보고 문서 복호화를 위한 인증서 파일  읽기 오류 ", e));
			ae.addAction(new ShutdownAction());
			throw ae;
		}		
	}
	
	/**
	 * 접수 증서 복호화 및 서명 검증 
	 * @param encryptedMsg 전자 서명할 암호화된 메세지
	 * @return
	 * @throws IOException
	 */
	/*
	 2011 보안모듈 고도화
	  기존소스 주석처리
	 public static byte[] decrypt(byte[] encryptedMsg) throws IOException {
		EncryptionInfo encryptInfo = Configure.getInstance().getAgentInfo().getEncryptionInfo();

		String certKey  = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getPrivateCertInfo().getCertificate();//"test1_env_seed.key";
		String pin = encryptInfo.getPrivateCertInfo().getPin();

		byte[] original = IssacWebSecurity.decrypt(encryptedMsg, FileTool.getFileByte(certKey), pin) ;
		return original;
	}
	*/
	
	/**
	 * 2011 보안모듈 고도화
	 * 접수 증서 복호화 및 서명 검증 
	 * @param encryptedMsg 전자 서명할 암호화된 메세지
	 * @return
	 * @throws IOException
	 * @throws GpkiApiException 
	 * @throws DecryptionException 
	 */
	/*접수증서에 대한 복화화 및 전자서명 검증은 현재 사용하지 않는다.FIUCF에서 접수증서에 대해 암호화및
	 * 전자서명을 하지 않기 때문이다.
	 */
	public static byte[] decrypt(byte[] encryptedMsg) throws IOException, DecryptionException, GpkiApiException {
		EncryptionInfo encryptInfo = Configure.getInstance().getAgentInfo().getEncryptionInfo();

		String certKey  = CtrAgentEnvInfo.USER_CERTIFICATE_DIR + File.separator + encryptInfo.getPrivateCertInfo().getCertificate();//"test1_env_seed.key";
		/*암호화용 GPKI공개키 추가*/
		String GPKIKmCert = CtrAgentEnvInfo.getFIUPublicKeyFile();
		/* GPKI라이센스파일이 있는 경로명 추가
		 * GPKI Init를 위해 필요하다.
		 */
		String Gpkiconf = CtrAgentEnvInfo.GPKICONF_DIR;
		String pin = encryptInfo.getPrivateCertInfo().getPin();
		
		/**
		 * 변경
		 * 서명제거된 암호화 메시지를 받아 복호화 처리한다.
		 */
		byte[] original = SecuKit.getInstance().decrypt(encryptedMsg, FileTool.getFileByte(GPKIKmCert),FileTool.getFileByte(certKey), pin, Gpkiconf) ;
		
		
		return original;
	}

/*	2011 보안모듈 고도화
	테스트 소스로 주석처리한다.
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
		System.out.println("CTR 보고기관 암호화 기능 테스트 ....");
		String message = "Hello, World!";
		try {
			System.out.println("'"  + message + "' 암호화 시작 ....");
			String encrypted = Main.testEncrypt(message);
			System.out.println("암호화 성공 - '"  + encrypted + "'" );
		} catch (Throwable t) {
			System.out.println("암호화 실패... " );
			t.printStackTrace();
		}
	}
*/
}
