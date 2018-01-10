package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.STRDocument;
/**
 * STR 대량 STR 연계시 메세지 타입 코드가 '01' 또는 '99'가 아닐 경우 정형화 검증 수행.
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrMessageTypeCdValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {
		
		String messageTypeCd = null;	
		try{
		messageTypeCd = str.getSTR().getMaster().getMessageTypeCode().toString();
		}catch(Exception e){
			throw new StrValidationException(
					"(STRValidation) 메세지타입코드Invalid : 메세지 타입 코드를 확인하여 주십시오.");
		}
		
		if ( checkCode(messageTypeCd) )
			throw new StrValidationException(
					"(STRValidation) 메세지타입코드Invalid : 메세지 타입 코드를 확인하여 주십시오.");
	}
	
	private boolean checkCode(String messageTypeCd){
		
		//메세지 타입 코드가 '01' 또는 '99' 인지 체크
		if ("01".equals(messageTypeCd) || "99".equals(messageTypeCd)){
			return false;
		}
		return true;		
	}
}
