package vn.com.vndirect.report.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExportData {
	private String fileNameTemplate;
	private String prefixFileNameDownload;
	private Boolean endPeriodReport;
	private Boolean appendHeaderFooter;
}
