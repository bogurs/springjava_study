package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.User;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
import kr.go.kofiu.str.validation.RealNumValidator;

/**
 * STR 거래자 실명번호 체크 실명번호구분코드가 '01','02','03'일 경우 실명번호 Digit Check 국적 체크 실명번호코드가
 * 외국인일 경우 존재 이중택일 체크 - <User>의 실명번호와 <UserRelation>의 실명번호가 연결고리다.
 * <UserRrelation>의 거래자역할이 '01','08','11'일 경우 자택 또는 직장주소 택1 <UserRelation>의
 * 거래자역할이 '01'이고 실명번호구분코드가 '01','04','06','07'일 경우 필수
 * 
 * @param str
 *            보고 기관이 발행한 STR XML
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
						"(STRValidation) 거래자 실명번호invalid : 실명번호구분코드가 공백이거나 존재하지 않습니다. <RealNumber> 제약조건을 확인 하십시오..");
			if(user.getRealNumber().getStringValue().equals(null) || user.getRealNumber().getStringValue().trim().equals(""))
				throw new StrValidationException(
						"(STRValidation) 거래자 실명번호invalid : 실명번호가 공백이거나 존재하지 않습니다. <RealNumber> 제약조건을 확인 하십시오..");

			if (("01".equals(realNmCd) || "02".equals(realNmCd))
					&& !rNumValidator.validZuminNumber(user.getRealNumber()
							.getStringValue())) {
				throw new StrValidationException(
						"(STRValidation) 거래자 실명번호invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오..");
			}
			if ("03".equals(realNmCd)
					&& !rNumValidator.validBizNumber(user.getRealNumber()
							.getStringValue())) {
				throw new StrValidationException(
						"(STRValidation) 거래자 실명번호invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오..");
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
									"(STRValidation) 거래자 국적invalid : 거래역할코드가 '01','02','03','09','10','11','12','13','15','16','17','18'일 경우 필수입력 입니다. <Nationality> 제약조건을 확인 하십시오.");
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
									"(STRValidation) 거래자 주소(자택)/주소(직장)invalid : <UserRelation>의 거래자역할이 '01','08','11'일 경우 이중택일 입력 입니다. <Address> or <AddressCompany> 제약조건을 확인 하십시오.");
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
										"(STRValidation) 직업구분invalid :  <UserRelation>의 거래자역할이 '01'이고 실명번호구분코드가 '01','04','06','07'일 경우 <OccupationType>는 필수 입력 입니다. <OccupationType> 제약조건을 확인 하십시오.");
							}
						}
					}
				}
			}
		}
	}
}
