package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
import kr.go.kofiu.str.validation.RealNumValidator;

/**
 * STR �ŷ������� �ŷ��ڿ��� ���� ������ ���� ����. 
 * 1.���� �� �ݺ��� �� �ŷ������ڵ尡 �ߺ��Ǹ� �ȵ�. 
 * 2.�ǽɰŷ����� '01' �ڵ�� �ݵ�� 1�� �����ؾ���. 
 * 3.'03'�ŷ��븮���� ��� '01'�ǽɰŷ��ڰ� �����ؾ��� ( 2���� ���� ���� ). 
 * 4.'09'���ð��°����븮���� ��� '08'���ð��� �����ڰ� �����ؾ���. 
 * 5.'12'����ۼ��� ���°��� �븮���� ��� '11'����ۼ��� ���°����ڰ� �����ؾ���.
 * 6.�Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ Digit Check.
 * @param str
 *            ���� ����� ������ STR XML
 * @throw StrValidationException
 */
public class StrUserRelationValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

//		List<Transaction> tlists = str.getSTR().getDetail()
//				.getTransactionList();
		Transaction[] tlists = str.getSTR().getDetail().getTransactionArray();
		RealNumValidator rNumValidator = new RealNumValidator();
		int cnt, cntNext, urCnt;
		String relationCd1 = null;
		String relationCd2 = null;
		String rRoleCd = null;
		String realNumCd = null;
		String realNum = null;

		for (Transaction tx : tlists) {
			urCnt = tx.sizeOfUserRelationArray();
			UserRelation[] urLists = tx.getUserRelationArray();

			for (cnt = 0; cnt < urCnt - 1; cnt++) {
				relationCd1 = tx.getUserRelationArray(cnt).getRelationRole()
						.getCode().toString();

				for (cntNext = cnt + 1; cntNext < urCnt; cntNext++) {
					relationCd2 = tx.getUserRelationArray(cntNext)
							.getRelationRole().getCode().toString();
					if (relationCd1.equals(relationCd2)) {
						throw new StrValidationException(
								"(STRValidation) �ŷ������Ѱŷ��ڿ���invalid : <UserRelation>������ ���� �� �ݺ��� �� �ŷ������ڵ尡 �ߺ��Ǹ� �ȵ˴ϴ�. <UserRelation> ���������� Ȯ�� �Ͻʽÿ�.");
					}
				}
			}

			for (UserRelation ur : urLists) {
				try{
					relationCd1 = ur.getRelationRole().getCode().toString();
					rRoleCd = ur.getRelationRole().getCode().toString();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> �ŷ��ڿ���invalid : RelationRoled�� �ڵ尪�� �����̰ų� �������� �ʽ��ϴ�. <RelationRole> ���������� Ȯ�� �Ͻʽÿ�.");
				}
				
				try{
					realNumCd = ur.getRealNumber().getCode().toString();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> �ŷ��ڿ���invalid : RealNumber �ڵ尪�� �����̰ų� �������� �ʽ��ϴ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�.");
				}	
				
				try{
					realNum = ur.getRealNumber().getStringValue();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> �ŷ��ڿ���invalid : RealNumber �Ǹ��ȣ�� �����̰ų� �������� �ʽ��ϴ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�..");
				}	

				if (notValidUserRelation(urLists, "01"))
					throw new StrValidationException(
							"(STRValidation) �ŷ��ڿ���invalid : �ǽɰŷ����� '01' �ڵ�� �ݵ�� 1�� �����ؾ� �մϴ�. <RelationRole> ���������� Ȯ�� �Ͻʽÿ�.");

				if ("09".equals(relationCd1)
						&& notValidUserRelation(urLists, "08"))
					throw new StrValidationException(
							"(STRValidation) �ŷ��ڿ���invalid : '09'���ð��°����븮���� ��� '08'���ð��� �����ڰ� �����ؾ� �մϴ�. <RelationRole> ���������� Ȯ�� �Ͻʽÿ�.");

				if ("12".equals(relationCd1)
						&& notValidUserRelation(urLists, "11"))
					throw new StrValidationException(
							"(STRValidation) �ŷ��ڿ���invalid : '12'����ۼ��� ���°��� �븮���� ��� '11'����ۼ��� ���°����ڰ� �����ؾ� �մϴ�. <RelationRole> ���������� Ȯ�� �Ͻʽÿ�.");
				if (("01".equals(realNumCd) || "02".equals(realNumCd))
						&& !rNumValidator.validZuminNumber(realNum)) {
					throw new StrValidationException(
							"(STRValidation) �ŷ������� �ŷ��ڿ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�.");
				}
				if ("03".equals(realNumCd)
						&& !rNumValidator.validBizNumber(realNum)) {
					throw new StrValidationException(
							"(STRValidation) �ŷ������� �ŷ��ڿ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�.");
				}

				if ("03".equals(rRoleCd) || "09".equals(rRoleCd)
						|| "12".equals(rRoleCd) || "15".equals(rRoleCd)) {
					if (ur.getInsuRelDesc() == null
							|| "".equals(ur.getInsuRelDesc()))
						throw new StrValidationException(
								"(STRValidation) ���輳��invalid : �ŷ������ڵ尡 '03','09','12','15'(�ŷ� �븮��,���°��� �븮��,����ۼ��� ���°��� �븮��,���Ǻ�����)�� ��� �ʼ��Է� �Դϴ�. <InsuRelDesc> ���������� Ȯ�� �Ͻʽÿ�..");
				}
			}
		}
	}

	private boolean notValidUserRelation(UserRelation[] urLists, String num) {
		for (UserRelation ur : urLists) {
			if (num.equals(ur.getRelationRole().getCode().toString())) {
				return false;
			}
		}
		return true;
	}
}
