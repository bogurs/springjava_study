package kr.go.kofiu.str.validation.str;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class StrFileNamePatternMatcher {
	private static final Logger logger = Logger
			.getLogger(StrFileNamePatternMatcher.class.getName());

	private final String regExpr = "^STR_[A-Z0-9]{4}_[0-9]{8}_[0-9]{8}.xml$";

	private final String dateFmt = "yyyyMMdd";

	public static void main(String[] args) {
		StrFileNamePatternMatcher fm = new StrFileNamePatternMatcher();

		String[] fileNames = { "STR_A300_20091131_00000001.xml",
				"STR_A300_20091130_00000001.xml",
				"STR_A300_20091125_00000001.xml" };

		for (int inx = 0; inx < fileNames.length; inx++) {
			try {
				fm.match(fileNames[inx]);
			} catch (Exception ex) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe(ex.getMessage());
				}
			}
		}
	}

	/**
	 * STR 파일명이 주어진 명명규칙과 동일한지 검사한다.
	 * @param strFileNm
	 * @throws Exception STR 파일명이 명명 규칙과 다름
	 */
	public final void match(String strFileNm) throws Exception {
		String date = "";
		Date today = new Date(System.currentTimeMillis());
		Date fileDate = null;
		DateFormat df = new SimpleDateFormat(dateFmt);

		df.setLenient(false);

		if (Pattern.matches(this.regExpr, strFileNm)) {
			date = strFileNm.substring(9, 17);

			try {
				fileDate = df.parse(date);

				if (today.before(fileDate)) {
					throw new Exception("유효하지 않은 STR 파일명(" + strFileNm
							+ ") STR 파일명의 날짜 부분은 현재일보다 미래일 수 없습니다.");
				}
			} catch (ParseException pe) {
				throw new Exception("유효하지 않은 STR 파일명(" + strFileNm + ") "
						+ pe.getMessage());
			}
		} else {
			throw new Exception(
					"유효하지 않은 STR 파일명("
							+ strFileNm
							+ ") STR 파일명 형식은 STR_<기관코드4자리>_<날짜(yyyyMMdd 형식)>_<순번8자리>.xml입니다.");
		}
	}
}
