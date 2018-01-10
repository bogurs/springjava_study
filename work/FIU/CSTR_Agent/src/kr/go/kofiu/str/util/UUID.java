package kr.go.kofiu.str.util;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************
 * <pre>
 * 업무   그룹명 : STR 시스템
 * 서브   업무명 : 보고 기관 Agent
 * 설         명 : 데이터베이스나 싱글톤 없이 메모리에서 유일한 프라이머리 키들을 만든다
 * 작    성   자 : 선만주
 * 작    성   일 : 2008.09.09
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class UUID {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UUID.class.getName());

	/**
	 * 동시 접근하여 같은 키를 생성하는 경우를 막기 위한 lock Object
	 */
	private static Object mutex = new Object();

	/**
	 * 반복되지 않은 seeder를 제공하기 위해 무작위로 보안한다.
	 */
	private static Random random = new Random();

	/**
	 * IP 주소 문자열
	 */
	private static String inetAddr;

	private static String Addr_PORT;
	private static String LISTEN_PORT_FOR_UUID;

	static {
		int inetAdd = 0;

		try {
			inetAdd = UUID.getInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception exc) {
			logger.severe("UUID getInetAddress Fail!");
		}

		UUID.inetAddr = Integer.toString(Math.abs(inetAdd));

		if (logger.isLoggable(Level.INFO)) {
			logger.info(inetAddr);
		}

		UUID.LISTEN_PORT_FOR_UUID = "19736";
		Addr_PORT = inetAddr + LISTEN_PORT_FOR_UUID;

		if (Addr_PORT.length() > 10) {
			Addr_PORT = Addr_PORT.substring(Addr_PORT.length() - 10);
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Seed Addr_PORT : " + Addr_PORT);
		}
	}

	/**
	 * 생성된 Object의 hash 값을 이용하여 Univesal Uniqe key 값을 반환한다.
	 * 
	 * @return
	 */
	public static String getUUID() {
		String timeNow = "";
		String lastValue = "";
		synchronized (mutex) {
			// random 값을 구한다.
			UUID uuid = new UUID();

			timeNow = Long.toString(System.currentTimeMillis());
			lastValue = Integer.toString(System.identityHashCode(uuid));
		}

		return (inetAddr + timeNow + lastValue);
	}

	/**
	 * java.math.Random 객체를 이용하여 Univesal Uniqe key 값을 반환한다.
	 * 
	 * @return
	 */
	public static String getUUIDByRandomOrigin() {
		String timeNow = "";
		String lastValue = "";
		synchronized (mutex) {
			timeNow = Long.toString(System.currentTimeMillis());
			lastValue = padding(Math.abs(random.nextInt(1000000000)), 9);
		}

		return (inetAddr + timeNow + lastValue);
	}

	/**
	 * java.math.Random 객체를 이용하여 Univesal Uniqe key 값을 반환한다.
	 * 
	 * @return
	 */
	public static String getUUIDByRandom() {
		String timeNow = "";
		String lastValue = "";

		synchronized (mutex) {
			timeNow = Long.toString(System.currentTimeMillis());
			lastValue = padding(Math.abs(random.nextInt(1000000000)), 9);
		}

		return (Addr_PORT + timeNow + lastValue);
	}

	/**
	 * 4자리 임의의 숫자 생성
	 * 
	 * @return
	 */
	public static int nextFourLengthInt() {
		return random.nextInt(10000);
	}

	/**
	 * 자리수를 맞추기 위해 자리수 만큼 0을 채운다.
	 * 
	 * @param val
	 *            값
	 * @param inx
	 *            자리수
	 * @return
	 */
	public static String padding(int val, int inx) {
		StringBuffer sBuf = new StringBuffer();
		String valStr = Integer.toString(val);

		if (valStr.length() < inx) {
			for (int jnx = 0; jnx < inx - valStr.length(); jnx++) {
				sBuf.append("0");
			}
		}

		return sBuf.append(valStr).toString();
	}

	/**
	 * 바이트 값을 int 값으로 변환
	 * 
	 * @param bytes
	 * @return
	 */
	private static int getInt(byte bytes[]) {
		int inx = 0;
		int jnx = 24;
		int lnx = 0;

		for (int knx = 0; jnx >= 0; knx++) {
			lnx = bytes[knx] & 0xff;
			inx += lnx << jnx;
			jnx -= 8;
		}

		return inx;
	}

	/**
	 * 
	 * @return
	 */
	public static String getInetAddr() {
		return inetAddr;
	}
}
