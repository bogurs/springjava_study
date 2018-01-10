package kr.go.kofiu.ctr.conf;

public class EsbCheckServiceInfo {

	// Check Agent
	private String MemoryData;
	private String TimeResult;
	private String IPResult;
	private String ModuleVersionResult;
	private String TimeValue;
	private String moduleVersion;

	// Module Update
	private String RPT_MDL_VER;
	private String RPT_MDL_BLOB_MDL_LIB;
	private String RPT_MDL_BLOB_CONFIG;
	private String RPT_MDL_BLOB_PBL_KEY;

	// Heart Beat
	private String check;

	// ResultCode, ResultMessage
	private String resultCode;
	private String resultMessage;

	public String getMemoryData() {
		return MemoryData;
	}

	public String getTimeResult() {
		return TimeResult;
	}

	public String getIPResult() {
		return IPResult;
	}

	public String getModuleVersionResult() {
		return ModuleVersionResult;
	}

	public String getTimeValue() {
		return TimeValue;
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public String getRPT_MDL_VER() {
		return RPT_MDL_VER;
	}

	public String getRPT_MDL_BLOB_MDL_LIB() {
		return RPT_MDL_BLOB_MDL_LIB;
	}

	public String getRPT_MDL_BLOB_CONFIG() {
		return RPT_MDL_BLOB_CONFIG;
	}

	public String getRPT_MDL_BLOB_PBL_KEY() {
		return RPT_MDL_BLOB_PBL_KEY;
	}

	public String getCheck() {
		return check;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	/**
	 *  Check Agent遂 setting 持失切
	 * @param memoryData
	 * @param timeResult
	 * @param iPResult
	 * @param moduleVersionResult
	 * @param timeValue
	 * @param moduleVersion
	 * @param resultCode
	 * @param resultMessage
	 */
	public EsbCheckServiceInfo(String memoryData, String timeResult,
			String iPResult, String moduleVersionResult, String timeValue,
			String moduleVersion, String resultCode,
			String resultMessage) {
		super();
		MemoryData = memoryData;
		TimeResult = timeResult;
		IPResult = iPResult;
		ModuleVersionResult = moduleVersionResult;
		TimeValue = timeValue;
		this.moduleVersion = moduleVersion;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	/**
	 *  Module Update遂 setting 持失切
	 * @param rPT_MDL_VER
	 * @param rPT_MDL_BLOB_MDL_LIB
	 * @param rPT_MDL_BLOB_CONFIG
	 * @param rPT_MDL_BLOB_PBL_KEY
	 * @param resultCode
	 * @param resultMessage
	 */
	public EsbCheckServiceInfo(String rPT_MDL_VER,
			String rPT_MDL_BLOB_MDL_LIB, String rPT_MDL_BLOB_CONFIG,
			String rPT_MDL_BLOB_PBL_KEY, String resultCode,
			String resultMessage) {
		super();
		RPT_MDL_VER = rPT_MDL_VER;
		RPT_MDL_BLOB_MDL_LIB = rPT_MDL_BLOB_MDL_LIB;
		RPT_MDL_BLOB_CONFIG = rPT_MDL_BLOB_CONFIG;
		RPT_MDL_BLOB_PBL_KEY = rPT_MDL_BLOB_PBL_KEY;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	/**
	 *  Heart Beat遂 setting 持失切
	 * @param check
	 * @param resultCode
	 * @param resultMessage
	 */
	public EsbCheckServiceInfo(String check, String resultCode,
			String resultMessage) {
		super();
		this.check = check;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

}
