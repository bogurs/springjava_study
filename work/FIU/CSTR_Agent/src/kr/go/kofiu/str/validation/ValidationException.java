package kr.go.kofiu.str.validation;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 3873659328489065798L;

	public ValidationException() {
		super();
	}
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public ValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
