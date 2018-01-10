package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.STRDocument;
/**
 * STR �뷮 STR ����� �޼��� Ÿ�� �ڵ尡 '01' �Ǵ� '99'�� �ƴ� ��� ����ȭ ���� ����.
 * @param str
 *            ���� ����� ������ STR XML
 * @throw StrValidationException
 */
public class StrMessageTypeCdValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {
		
		String messageTypeCd = null;	
		try{
		messageTypeCd = str.getSTR().getMaster().getMessageTypeCode().toString();
		}catch(Exception e){
			throw new StrValidationException(
					"(STRValidation) �޼���Ÿ���ڵ�Invalid : �޼��� Ÿ�� �ڵ带 Ȯ���Ͽ� �ֽʽÿ�.");
		}
		
		if ( checkCode(messageTypeCd) )
			throw new StrValidationException(
					"(STRValidation) �޼���Ÿ���ڵ�Invalid : �޼��� Ÿ�� �ڵ带 Ȯ���Ͽ� �ֽʽÿ�.");
	}
	
	private boolean checkCode(String messageTypeCd){
		
		//�޼��� Ÿ�� �ڵ尡 '01' �Ǵ� '99' ���� üũ
		if ("01".equals(messageTypeCd) || "99".equals(messageTypeCd)){
			return false;
		}
		return true;		
	}
}
