package kr.go.kofiu.str.validation.attach;

import jxl.Sheet;
import kr.go.kofiu.str.validation.ValidationException;

public interface XlsValidationItem {
	public void validate(Sheet sh) throws ValidationException;
}
