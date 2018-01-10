package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.User;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
import kr.go.kofiu.str.validation.RealNumValidator;

/**
 * STR �ŷ��� �Ǹ��ȣ üũ �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ Digit Check ���� üũ �Ǹ��ȣ�ڵ尡
 * �ܱ����� ��� ���� �������� üũ - <User>�� �Ǹ��ȣ�� <UserRelation>�� �Ǹ��ȣ�� �������.
 * <UserRrelation>�� �ŷ��ڿ����� '01','08','11'�� ��� ���� �Ǵ� �����ּ� ��1 <UserRelation>��
 * �ŷ��ڿ����� '01'�̰� �Ǹ��ȣ�����ڵ尡 '01','04','06','07'�� ��� �ʼ�
 * 
 * @param str
 *            ���� ����� ������ STR XML
 * @throw StrValidationException
 */
public class StrUserValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {
		
//		List<Transaction> tlists = str.getSTR().getDetail()
//				.getTransactionList();
//		List<User> ulists = str.getSTR().getDetail().getUserList();
		Transaction[] tlists = str.getSTR().getDetail().getTransactionArray();
		User[] ulists = str.getSTR().getDetail().getUserArray();
		RealNumValidator rNumValidator = new RealNumValidator();
		String relationRoleCd = null;
		String realNmCd = null;
		String userRealNmCd = null;
		
		
		for (User user : ulists) {
			
			
				realNmCd = user.getRealNumber().getCode().toString();
			
			
			if(realNmCd.equals(null) || realNmCd.trim().equals(""))
				throw new StrValidationException(
						"(STRValidation) �ŷ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�����ڵ尡 �����̰ų� �������� �ʽ��ϴ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�..");
			if(user.getRealNumber().getStringValue().equals(null) || user.getRealNumber().getStringValue().trim().equals(""))
				throw new StrValidationException(
						"(STRValidation) �ŷ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�� �����̰ų� �������� �ʽ��ϴ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�..");

			if (("01".equals(realNmCd) || "02".equals(realNmCd))
					&& !rNumValidator.validZuminNumber(user.getRealNumber()
							.getStringValue())) {
				throw new StrValidationException(
						"(STRValidation) �ŷ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�..");
			}
			if ("03".equals(realNmCd)
					&& !rNumValidator.validBizNumber(user.getRealNumber()
							.getStringValue())) {
				throw new StrValidationException(
						"(STRValidation) �ŷ��� �Ǹ��ȣinvalid : �Ǹ��ȣ�����ڵ尡 '01','02','03'�� ��� �Ǹ��ȣ�� Ȯ�� �Ͻʽÿ�. <RealNumber> ���������� Ȯ�� �Ͻʽÿ�..");
			}

			for (Transaction tx : tlists) {
//				List<UserRelation> urlists = tx.getUserRelationList();
				UserRelation[] urlists = tx.getUserRelationArray();

				for (UserRelation userRel : urlists) {
					relationRoleCd = userRel.getRelationRole().getCode().toString();
					userRealNmCd = userRel.getRealNumber().getCode().toString();
					
					if (user.getRealNumber().getStringValue().equals(
							userRel.getRealNumber().getStringValue())
							&& ("01".equals(relationRoleCd)
									|| "02".equals(relationRoleCd)
									|| "03".equals(relationRoleCd)
									|| "09".equals(relationRoleCd)
									|| "10".equals(relationRoleCd)
									|| "11".equals(relationRoleCd)
									|| "12".equals(relationRoleCd)
									|| "13".equals(relationRoleCd)
									|| "15".equals(relationRoleCd)
									|| "16".equals(relationRoleCd)
									|| "17".equals(relationRoleCd) || "18"
									.equals(relationRoleCd))) {
						if (user.getNationality() == null
								|| "".equals(user.getNationality()
										.getStringValue()))
							throw new StrValidationException(
									"(STRValidation) �ŷ��� ����invalid : �ŷ������ڵ尡 '01','02','03','09','10','11','12','13','15','16','17','18'�� ��� �ʼ��Է� �Դϴ�. <Nationality> ���������� Ȯ�� �Ͻʽÿ�.");
					}

					if (user.getRealNumber().getStringValue().equals(
							userRel.getRealNumber().getStringValue())
							&& ("01".equals(relationRoleCd)
									|| "08".equals(relationRoleCd) || "11"
									.equals(relationRoleCd))) {
						if ((user.getAddress() == null || "".equals(user
								.getAddress().getStringValue()))
								&& (user.getAddressCompany() == null || ""
										.equals(user.getAddressCompany()))) {

							throw new StrValidationException(
									"(STRValidation) �ŷ��� �ּ�(����)/�ּ�(����)invalid : <UserRelation>�� �ŷ��ڿ����� '01','08','11'�� ��� �������� �Է� �Դϴ�. <Address> or <AddressCompany> ���������� Ȯ�� �Ͻʽÿ�.");
						}
					}

					if (user.getRealNumber().getStringValue().equals(
							userRel.getRealNumber().getStringValue())
							&& "01".equals(relationRoleCd)) {
						if ("01".equals(userRealNmCd)
								|| "04".equals(userRealNmCd)
								|| "06".equals(userRealNmCd)
								|| "07".equals(userRealNmCd)) {
							if (user.getOccupationType() == null
									|| "".equals(user.getOccupationType()
											.getStringValue())) {
								throw new StrValidationException(
										"(STRValidation) ��������invalid :  <UserRelation>�� �ŷ��ڿ����� '01'�̰� �Ǹ��ȣ�����ڵ尡 '01','04','06','07'�� ��� <OccupationType>�� �ʼ� �Է� �Դϴ�. <OccupationType> ���������� Ȯ�� �Ͻʽÿ�.");
							}
						}
					}
				}
			}
		}
	}
}
