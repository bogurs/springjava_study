package kr.go.kofiu.str.validation.attach.stock.xml;

import kr.go.kofiu.addiStrStock.AddiStrStockDocument;
import kr.go.kofiu.str.validation.ValidationException;

public interface StockXmlValidationItem {
	public void validate(AddiStrStockDocument stockDoc)
			throws ValidationException;
}
