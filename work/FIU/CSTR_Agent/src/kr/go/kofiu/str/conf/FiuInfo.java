package kr.go.kofiu.str.conf;

public class FiuInfo {
	private EncryptionInfo encryptionInfo = null;

	public EncryptionInfo getEncryptionInfo() {
		return encryptionInfo;
	}

	public void setEncryptionInfo(EncryptionInfo encryptionInfo) {
		this.encryptionInfo = encryptionInfo;
	}
	
	public String toString() {
		StringBuilder xmlContent = new StringBuilder("    <FIU>\n");
		xmlContent.append(this.getEncryptionInfo().toString()).append("    </FIU>\n");
		
		
		
		return xmlContent.toString();
	}
}
