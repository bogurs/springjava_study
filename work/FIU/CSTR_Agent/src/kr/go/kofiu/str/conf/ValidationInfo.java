package kr.go.kofiu.str.conf;

public class ValidationInfo {
	private String strValidationConfig = "";
	private String xmlAttachmentValidationConfig = "";
	private String xlsAttachmentValidationConfig = "";

	public String getStrValidationConfig() {
		return strValidationConfig;
	}

	public void setStrValidationConfig(String strValidationConfig) {
		this.strValidationConfig = strValidationConfig;
	}

	public String getXmlAttachmentValidationConfig() {
		return xmlAttachmentValidationConfig;
	}

	public void setXmlAttachmentValidationConfig(
			String xmlAttachmentValidationConfig) {
		this.xmlAttachmentValidationConfig = xmlAttachmentValidationConfig;
	}

	public String getXlsAttachmentValidationConfig() {
		return xlsAttachmentValidationConfig;
	}

	public void setXlsAttachmentValidationConfig(
			String xlsAttachmentValidationConfig) {
		this.xlsAttachmentValidationConfig = xlsAttachmentValidationConfig;
	}

	public String toString() {
		StringBuilder xmlContent = new StringBuilder("        <Validation");

		if (null != this.strValidationConfig
				&& !"".equals(this.strValidationConfig)) {
			xmlContent.append("\n          strValidationConfig=\"").append(
					this.strValidationConfig).append("\"");
		}
		
		if (null != this.xmlAttachmentValidationConfig
				&& !"".equals(this.xmlAttachmentValidationConfig)) {
			xmlContent.append("\n          xmlAttachmentValidationConfig=\"").append(
					this.xmlAttachmentValidationConfig).append("\"");
		}
		
		if (null != this.xlsAttachmentValidationConfig
				&& !"".equals(this.xlsAttachmentValidationConfig)) {
			xmlContent.append("\n          xslAttachmentValidationConfig=\"").append(
					this.xlsAttachmentValidationConfig).append("\"");
		}	

		return xmlContent.append("/>\n").toString();
	}
}
