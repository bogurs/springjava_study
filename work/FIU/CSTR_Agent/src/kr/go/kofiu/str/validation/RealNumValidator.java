package kr.go.kofiu.str.validation;

import java.text.ParseException;
import java.util.regex.Pattern;

import kr.go.kofiu.str.util.DateUtil;
import kr.go.kofiu.str.validation.str.StrValidationException;

/**
 * STR 실명번호 ( 주민번호 또는 사업번호 ) Digit 체크
 * @param zuminNumber 
 * 						USER 주민 등록 번호
 * @param bizNumber 
 * 						사업 등록 번호
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

			// 주민번호 자리 값 저장
			int[] zuminNumberPos = new int[13];
			for (int i = 0; i < 6; i++)
				zuminNumberPos[i] = Integer
						.parseInt("" + zuminNumber.charAt(i));
			for (int i = 6; i < 13; i++)
				zuminNumberPos[i] = Integer
						.parseInt("" + zuminNumber.charAt(i));

			// 주민번호 각 자리의 승수 저장
			int sum = 0;
			int[] multiplies = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 };
			for (int i = 0; i < multiplies.length; i++)
				sum += (zuminNumberPos[i] *= multiplies[i]);

			// 주민번호 확인
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
