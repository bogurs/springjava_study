package kr.go.kofiu.str.validation.str;

import java.util.List;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction;
import kr.go.kofiu.str.STRDocument.STR.Detail.Transaction.UserRelation;
import kr.go.kofiu.str.validation.RealNumValidator;

/**
 * STR 거래에대한 거래자역할 정보 정형과 검증 수행. 
 * 1.여러 번 반복될 때 거래역할코드가 중복되면 안됨. 
 * 2.의심거래자인 '01' 코드는 반드시 1개 존재해야함. 
 * 3.'03'거래대리인일 경우 '01'의심거래자가 존재해야함 ( 2번에 의해 생략 ). 
 * 4.'09'관련계좌개설대리인일 경우 '08'관련계좌 개설자가 존재해야함. 
 * 5.'12'당행송수취 계좌개설 대리인일 경우 '11'당행송수취 계좌개설자가 존재해야함.
 * 6.실명번호구분코드가 '01','02','03'일 경우 실명번호 Digit Check.
 * @param str
 *            보고 기관이 발행한 STR XML
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
								"(STRValidation) 거래에대한거래자역할invalid : <UserRelation>정보가 여러 번 반복될 때 거래역할코드가 중복되면 안됩니다. <UserRelation> 제약조건을 확인 하십시오.");
					}
				}
			}

			for (UserRelation ur : urLists) {
				try{
					relationCd1 = ur.getRelationRole().getCode().toString();
					rRoleCd = ur.getRelationRole().getCode().toString();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> 거래자역할invalid : RelationRoled의 코드값이 공백이거나 존재하지 않습니다. <RelationRole> 제약조건을 확인 하십시오.");
				}
				
				try{
					realNumCd = ur.getRealNumber().getCode().toString();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> 거래자역할invalid : RealNumber 코드값이 공백이거나 존재하지 않습니다. <RealNumber> 제약조건을 확인 하십시오.");
				}	
				
				try{
					realNum = ur.getRealNumber().getStringValue();
				}catch(Exception e){
					throw new StrValidationException(
							"(<RelationRole> 거래자역할invalid : RealNumber 실명번호가 공백이거나 존재하지 않습니다. <RealNumber> 제약조건을 확인 하십시오..");
				}	

				if (notValidUserRelation(urLists, "01"))
					throw new StrValidationException(
							"(STRValidation) 거래자역할invalid : 의심거래자인 '01' 코드는 반드시 1개 존재해야 합니다. <RelationRole> 제약조건을 확인 하십시오.");

				if ("09".equals(relationCd1)
						&& notValidUserRelation(urLists, "08"))
					throw new StrValidationException(
							"(STRValidation) 거래자역할invalid : '09'관련계좌개설대리인일 경우 '08'관련계좌 개설자가 존재해야 합니다. <RelationRole> 제약조건을 확인 하십시오.");

				if ("12".equals(relationCd1)
						&& notValidUserRelation(urLists, "11"))
					throw new StrValidationException(
							"(STRValidation) 거래자역할invalid : '12'당행송수취 계좌개설 대리인일 경우 '11'당행송수취 계좌개설자가 존재해야 합니다. <RelationRole> 제약조건을 확인 하십시오.");
				if (("01".equals(realNumCd) || "02".equals(realNumCd))
						&& !rNumValidator.validZuminNumber(realNum)) {
					throw new StrValidationException(
							"(STRValidation) 거래에대한 거래자역할 실명번호invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오.");
				}
				if ("03".equals(realNumCd)
						&& !rNumValidator.validBizNumber(realNum)) {
					throw new StrValidationException(
							"(STRValidation) 거래에대한 거래자역할 실명번호invalid : 실명번호구분코드가 '01','02','03'일 경우 실명번호를 확인 하십시오. <RealNumber> 제약조건을 확인 하십시오.");
				}

				if ("03".equals(rRoleCd) || "09".equals(rRoleCd)
						|| "12".equals(rRoleCd) || "15".equals(rRoleCd)) {
					if (ur.getInsuRelDesc() == null
							|| "".equals(ur.getInsuRelDesc()))
						throw new StrValidationException(
								"(STRValidation) 관계설명invalid : 거래역할코드가 '03','09','12','15'(거래 대리인,계좌개설 대리인,당행송수취 계좌개설 대리인,주피보험자)일 경우 필수입력 입니다. <InsuRelDesc> 제약조건을 확인 하십시오..");
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
