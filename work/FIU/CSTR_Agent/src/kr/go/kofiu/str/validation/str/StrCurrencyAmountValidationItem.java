package kr.go.kofiu.str.validation.str;

import java.math.BigDecimal;
import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
/**
 * STR ��ȭ���� ���� ����ȭ ���� ����.
 * 1.��ȭ������ KRW�̰� �ŷ� ���� �ڵ尡 '99'(��Ÿ)�� �ƴ� ��� ���� 1�̻��̾�� ��
 * 2.��ȭ������ KRW�̿��� ��� ���� 1�̻��̾�� ��
 * 3.�ŷ� ��ȭ�� ��ȭ(MoneyType="KRW")�� �ƴϰ� �ŷ� ���(Method/@code)�� 99�� �ƴϸ鼭 
 *   ��ȭ�ŷ����� 1�������� ��� ���� �߻�
 *   @param str
 *            ���� ����� ������ STR XML
 *   @throw StrValidationException
 */
public class StrCurrencyAmountValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		// List<Transaction> lists = str.getSTR().getDetail().getTransactionList();
		Transaction[] lists = str.getSTR().getDetail().getTransactionArray();
		String moneyTypeCd = null;
		String methodCd = null;
		
		for (Transaction tx : lists) {
			moneyTypeCd = tx.getMoneyType().getCode();
			methodCd = tx.getMethod().getCode().toString();
			
			if ("KRW".equals(moneyTypeCd) && !"99".equals(methodCd)
					&& tx.getKRWAmount().compareTo(new BigDecimal("1")) == -1) {				
				throw new StrValidationException("(STRValidation) ��ȭ�ݾ�Invalid : ��ȭ��ȭ�ݾ��� 1�̻��̾�� �մϴ�. <KRWAmount> ���������� Ȯ�� �Ͻʽÿ�.");
			}			
			
			if (!"KRW".equals(moneyTypeCd) && !"99".equals(methodCd)){
					if (tx.getForeignAmount().compareTo(new BigDecimal("1")) == -1 )
						throw new StrValidationException("(STRValidation) ��ȭ�ݾ�Invalid : ��ȭ��ȭ�ݾ��� 1�̻��̾�� �մϴ�. <ForeignAmount> ���������� Ȯ�� �Ͻʽÿ�.");
			
					if( tx.getUSDAmount().compareTo(new BigDecimal("1")) == -1) 
						throw new StrValidationException("(STRValidation) ��ȭ�ݾ�Invalid : �޷���ȭ�ݾ��� 1�̻��̾�� �մϴ�. <USDAmount> ���������� Ȯ�� �Ͻʽÿ�.");
			}
		}
	}
}
