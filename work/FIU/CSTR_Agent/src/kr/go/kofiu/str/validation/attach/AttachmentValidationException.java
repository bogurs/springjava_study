package kr.go.kofiu.str.validation.attach;

import kr.go.kofiu.str.validation.ValidationException;

public class AttachmentValidationException extends ValidationException {
	private static final long serialVersionUID = -299122344523205413L;

	public AttachmentValidationException() {
		super();
	}

	public AttachmentValidationException(String msg) {
		super(msg);
	}

	public AttachmentValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
