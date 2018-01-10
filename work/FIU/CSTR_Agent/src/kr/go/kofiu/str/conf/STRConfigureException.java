package kr.go.kofiu.str.conf;

/**
 * Remote Adapter의 config.xml에 잘못된 설정을 할 경우 발생하는 Exception
 */
public class STRConfigureException extends Exception {
	private static final long serialVersionUID = -9071325131835536093L;

	public STRConfigureException(String msg) {
		super(msg);
	}
}
