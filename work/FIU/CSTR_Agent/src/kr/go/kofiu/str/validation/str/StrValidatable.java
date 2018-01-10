package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.validation.ValidationException;

public interface StrValidatable {
	public void validate(STRDocument str) throws ValidationException;
}
