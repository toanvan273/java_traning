package vn.com.vndirect.report.support;

import vn.com.vndirect.report.model.ExportData;

//thuannd review code nay de lam gi
public class ExportDataFactory {
	
	public static final String DAILY_MANAGER_TEMPLATE = "dailyManagerTemplate";
	public static final String NSR_TEMPLATE = "nsrTemplate";
	public static final String NAV_END_PERIOD_TEMPLATE = "nsrTemplate";
	public static final String AUM_END_PERIOD_TEMPLATE = "nsrTemplate";
	public static final String AUM_AVERAGE_TEMPLATE = "nsrTemplate";
	public static final String NAV_AVERAGE_TEMPLATE = "nsrTemplate";
	
	public static ExportData createExportData(String exportTemplateType) {
		if(exportTemplateType.contentEquals(DAILY_MANAGER_TEMPLATE)) {
			ExportData exportData = new ExportData();
		}
		return null;
	}
}
