package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.validation.ValidationException;

public class StrValidationException extends ValidationException {
	private static final long serialVersionUID = 9089698304400305565L;

	public StrValidationException() {
		super();
	}

	public StrValidationException(String msg) {
		super(msg);
	}

	public StrValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
