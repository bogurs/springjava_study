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
	 * STR ���ϸ��� �־��� ����Ģ�� �������� �˻��Ѵ�.
	 * @param strFileNm
	 * @throws Exception STR ���ϸ��� ��� ��Ģ�� �ٸ�
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
					throw new Exception("��ȿ���� ���� STR ���ϸ�(" + strFileNm
							+ ") STR ���ϸ��� ��¥ �κ��� �����Ϻ��� �̷��� �� �����ϴ�.");
				}
			} catch (ParseException pe) {
				throw new Exception("��ȿ���� ���� STR ���ϸ�(" + strFileNm + ") "
						+ pe.getMessage());
			}
		} else {
			throw new Exception(
					"��ȿ���� ���� STR ���ϸ�("
							+ strFileNm
							+ ") STR ���ϸ� ������ STR_<����ڵ�4�ڸ�>_<��¥(yyyyMMdd ����)>_<����8�ڸ�>.xml�Դϴ�.");
		}
	}
}
