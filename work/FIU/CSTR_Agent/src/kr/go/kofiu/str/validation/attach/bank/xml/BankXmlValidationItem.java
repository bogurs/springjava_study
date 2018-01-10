package kr.go.kofiu.str.validation.attach.bank.xml;

import kr.go.kofiu.addiStrBank.AddiStrBankDocument;
import kr.go.kofiu.str.validation.ValidationException;

public interface BankXmlValidationItem {
	public void validate(AddiStrBankDocument bankDoc)
			throws ValidationException;
}
