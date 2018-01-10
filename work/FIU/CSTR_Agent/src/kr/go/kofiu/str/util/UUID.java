package kr.go.kofiu.str.util;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************
 * <pre>
 * ����   �׷�� : STR �ý���
 * ����   ������ : ���� ��� Agent
 * ��         �� : �����ͺ��̽��� �̱��� ���� �޸𸮿��� ������ �����̸Ӹ� Ű���� �����
 * ��    ��   �� : ������
 * ��    ��   �� : 2008.09.09
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
	 * ���� �����Ͽ� ���� Ű�� �����ϴ� ��츦 ���� ���� lock Object
	 */
	private static Object mutex = new Object();

	/**
	 * �ݺ����� ���� seeder�� �����ϱ� ���� �������� �����Ѵ�.
	 */
	private static Random random = new Random();

	/**
	 * IP �ּ� ���ڿ�
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
	 * ������ Object�� hash ���� �̿��Ͽ� Univesal Uniqe key ���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public static String getUUID() {
		String timeNow = "";
		String lastValue = "";
		synchronized (mutex) {
			// random ���� ���Ѵ�.
			UUID uuid = new UUID();

			timeNow = Long.toString(System.currentTimeMillis());
			lastValue = Integer.toString(System.identityHashCode(uuid));
		}

		return (inetAddr + timeNow + lastValue);
	}

	/**
	 * java.math.Random ��ü�� �̿��Ͽ� Univesal Uniqe key ���� ��ȯ�Ѵ�.
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
	 * java.math.Random ��ü�� �̿��Ͽ� Univesal Uniqe key ���� ��ȯ�Ѵ�.
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
	 * 4�ڸ� ������ ���� ����
	 * 
	 * @return
	 */
	public static int nextFourLengthInt() {
		return random.nextInt(10000);
	}

	/**
	 * �ڸ����� ���߱� ���� �ڸ��� ��ŭ 0�� ä���.
	 * 
	 * @param val
	 *            ��
	 * @param inx
	 *            �ڸ���
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
	 * ����Ʈ ���� int ������ ��ȯ
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
