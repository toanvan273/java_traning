package vn.com.vndirect.report.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationReport {
	
	// cau hinh chung cho cac bao cao
	@Value("${pdf-content}")
	protected String pdfContent;
	
	@Value("${excel-content}")
	protected String excelContent;
	
	@Value("${html-content}")
	protected String htmlContent;
	
	@Value("${page-size}")
	protected int pageSize;
	
	@Value("${daily}")
	protected String dailyReportDownloadFileName;
	
	@Value("${portfolio}")
	protected String portfolioDownloadFileName;
	
	@Value("${nav_average}")
	protected String navAverageDownloadFileName;
	
	@Value("${aum_average}")
	protected String aumAverageDownloadFileName;
	
	@Value("${nav_endperiod}")
	protected String navEndperiodDownloadFileName;
	
	@Value("${aum_endperiod}")
	protected String aumEndperiodDownloadFileName;
	
	@Value("${nsr}")
	protected String nsrDetailDownloadFileName;
	
	// cau hinh cho bao cao role nhan vien
	@Value("${daily-sale-report}")
	private String staffDailyReportTemplate;
	
	@Value("${account-portfolio}")
	private String staffAccountPortfolioTemplate;
	
	@Value("${nsr-detail}")
	private String staffNsrTemplate;
	
	@Value("${nav-detail}")
	private String staffNavEnperiodTemplate;
	
	@Value("${aum-detail}")
	private String staffAumEnperiodTemplate;
	
	@Value("${average-nav}")
	private String staffAverageNavDetailTemplate;
	
	@Value("${average-aum}")
	private String staffAverageAumDetailTemplate;
	
	// cau hinh bao cao role nhan vien 
	@Value("${manager.daily-sale-report}")
	private String managerDailyReportTemplate;
	
	@Value("${manager.nsr-detail}")
	private String managerNsrTemplate;
	
	@Value("${manager.nav-endperiod}")
	private String managerNavEnperiodTemplate;
	
	@Value("${manager.aum-endperiod}")
	private String managerAumEnperiodTemplate;
	
	@Value("${manager.average-nav}")
	private String managerNavAverageTemplate;
	
	@Value("${manager.average-aum}")
	private String managerAumAverageTemplate;
	
	@Value("${manager.field-list.salename-hrcode}")
	private String flSaleNameHrcode;
}
