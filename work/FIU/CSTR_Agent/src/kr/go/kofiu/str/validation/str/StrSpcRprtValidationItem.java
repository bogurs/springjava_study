package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Master.Suspicion;
import kr.go.kofiu.str.STRDocument.STR.Master.SuspicionReport;
/**
 * STR 의심스러운 거래 유형 정보 정형화 검증 수행.
 * @param str
 *            보고 기관이 발행한 STR XML
 * @throw StrValidationException
 */
public class StrSpcRprtValidationItem implements StrValidatable {

	public void validate(STRDocument str) throws StrValidationException {

		Suspicion susp = str.getSTR().getMaster().getSuspicion();
		SuspicionReport sReport = str.getSTR().getMaster().getSuspicionReport();

		if (susp == null) {
			if (null == sReport.getEtcPecularityType()
					|| "".equals(sReport.getEtcPecularityType())) {
				throw new StrValidationException(
						"(STRValidation) 기타특징및유형invalid : 의심스러운 거래유형(Suspicion)이 없을 경우 기타 특징 및 유형정보가 있어야 합니다. <EtcPecularityType> 제약조건을 확인 하십시오.");
			}
		}
	}
}
