package vn.com.vndirect.report.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import vn.com.vndirect.report.datasource.DailySaleReportRepo;

public class BaseSummaryService {
	
	@Autowired
	protected DailySaleReportRepo dailySaleReportRepo;
	
	@Autowired
	protected CacheService<SolrDocumentList> cacheService;

	protected void processDate(String firstDayText, String lastDayText, StringBuilder builder) throws ParseException {
		String feDateFormat = "dd/MM/yyyy";
		String beDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		
		Date firstDate = ReportUtils.convertDate(firstDayText, feDateFormat, ReportUtils.CONVERT_BEGIN_DATE_TYPE);
		Date lastDate = ReportUtils.convertDate(lastDayText, feDateFormat, ReportUtils.CONVERT_END_DATE_TYPE);

		SimpleDateFormat dateFormat = new SimpleDateFormat(beDateFormat);
		if(firstDate != null && lastDate != null) {
			String dateValue = dateFormat.format(firstDate);
			builder.append("date:[").append(dateValue).append("-7HOURS TO ");
			dateValue = dateFormat.format(lastDate);
			builder.append(dateValue).append("-7HOURS]");
		} else if(firstDate == null && lastDate != null) {
			builder.append("date:[* TO ");
			String dateValue = dateFormat.format(lastDate);
			builder.append(dateValue).append("-7HOURS]");
		} else if(firstDate != null && lastDate == null) {
			String dateValue = dateFormat.format(firstDate);
			builder.append("date:[").append(dateValue).append("-7HOURS TO NOW]");
		}
	}
	
	protected StringBuilder appendAndClause(StringBuilder builder) {
		builder.append(" AND ");
		return builder;
	}
	
	protected String createKeyCache(String... params) {
		return String.join("_", params);
	}
}
