package kr.go.kofiu.str.validation.str;

import kr.go.kofiu.str.STRDocument;
import kr.go.kofiu.str.STRDocument.STR.Master.Suspicion;
import kr.go.kofiu.str.STRDocument.STR.Master.SuspicionReport;
/**
 * STR �ǽɽ����� �ŷ� ���� ���� ����ȭ ���� ����.
 * @param str
 *            ���� ����� ������ STR XML
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
						"(STRValidation) ��ŸƯ¡������invalid : �ǽɽ����� �ŷ�����(Suspicion)�� ���� ��� ��Ÿ Ư¡ �� ���������� �־�� �մϴ�. <EtcPecularityType> ���������� Ȯ�� �Ͻʽÿ�.");
			}
		}
	}
}
