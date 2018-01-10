package kr.go.kofiu.str.validation;

import java.text.ParseException;
import java.util.regex.Pattern;

import kr.go.kofiu.str.util.DateUtil;
import kr.go.kofiu.str.validation.str.StrValidationException;

/**
 * STR �Ǹ��ȣ ( �ֹι�ȣ �Ǵ� �����ȣ ) Digit üũ
 * @param zuminNumber 
 * 						USER �ֹ� ��� ��ȣ
 * @param bizNumber 
 * 						��� ��� ��ȣ
 */
public class RealNumValidator {
	public boolean validZuminNumber(String zuminNumber) {
		if (Pattern.matches("\\d{6}[1234]\\d{6}", zuminNumber)) {
			String year = zuminNumber.charAt(7) <= '2' ? "19" : "20";
			try {
				DateUtil.parse(DateUtil.yyyyMMdd, year
						+ zuminNumber.substring(0, 6));
			} catch (ParseException e) {
				return false;
			}

			// �ֹι�ȣ �ڸ� �� ����
			int[] zuminNumberPos = new int[13];
			for (int i = 0; i < 6; i++)
				zuminNumberPos[i] = Integer
						.parseInt("" + zuminNumber.charAt(i));
			for (int i = 6; i < 13; i++)
				zuminNumberPos[i] = Integer
						.parseInt("" + zuminNumber.charAt(i));

			// �ֹι�ȣ �� �ڸ��� �¼� ����
			int sum = 0;
			int[] multiplies = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 };
			for (int i = 0; i < multiplies.length; i++)
				sum += (zuminNumberPos[i] *= multiplies[i]);

			// �ֹι�ȣ Ȯ��
			return ((11 - (sum % 11)) % 10 == zuminNumberPos[12]);
		}
		return false;
	}

	public boolean validBizNumber(String bizNumber) {
		if (Pattern.matches("\\d{10}", bizNumber)) {
			int[] bizNumberPos = new int[10];
			for (int i = 0; i < 10; i++)
				bizNumberPos[i] = Integer.parseInt("" + bizNumber.charAt(i));

			int sum = 0;
			int[] multiplies = new int[] { 1, 3, 7, 1, 3, 7, 1, 3, 5 };
			for (int i = 0; i < 9; i++)
				sum += (bizNumberPos[i] * multiplies[i]);
			sum += ((bizNumberPos[8] * 5) / 10);
			sum = sum % 10;
			if (sum != 0)
				sum = 10 - sum;

			return sum == bizNumberPos[9];
		}
		return false;
	}

}
