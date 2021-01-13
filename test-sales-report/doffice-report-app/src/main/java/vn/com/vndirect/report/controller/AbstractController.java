/***************************************************************************
 * Copyright 2016 by HomeDirect - All rights reserved.                *    
 **************************************************************************/
package vn.com.vndirect.report.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ReportUtils;
import vn.com.vndirect.report.service.RoleService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:thuan.nhu@homedirect.com.vn
 * Nov 21, 2018
 */
public abstract class AbstractController {
	
//	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

	public static final int SUCCESS_CODE = 01;
	
	@Autowired
	protected RoleService roleService;
	
	@Value("${pdf-content}")
	private String pdfContent;
	
	@Value("${excel-content}")
	private String excelContent;
	
	@Value("${html-content}")
	private String htmlContent;
	
	protected DataReportFiller dataReportFiller;

	protected DataReportExporter dataExporter;

	public AbstractController() {

	}
	
	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String hrcode, String accountNo) throws ParseException {
		StringBuilder builder = new StringBuilder();
		
		processDate(firstDayText, lastDayText, builder);
		processHrCode(hrcode, builder);
		processAccount(accountNo, builder);
		
		if(builder.length() < 1) builder.append("*:*");

		return builder;
	}
	
	/**
	 * Description: query for get data of manager for specific time
	 * @param firstDayText
	 * @param lastDayText
	 * @param managerCode
	 * @return
	 * @throws ParseException
	 * TODO
	 */
	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String managerHrCode) throws ParseException {
		StringBuilder builder = new StringBuilder();		
		processDate(firstDayText, lastDayText, builder);
		processManagerCode(managerHrCode, builder);
		
		if(builder.length() < 1) builder.append("*:*");

		return builder;
	}
	
	/**
	 * Description: get list hrcode managed by manager
	 * @param firstDayText
	 * @param lastDayText
	 * @param hrcode
	 * @return
	 * @throws ParseException
	 * TODO
	 */
//	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String managerHrCode, String hrcode) throws ParseException {
//		StringBuilder builder = new StringBuilder();
//		
//		processDate(firstDayText, lastDayText, builder);
//		processManagerCode(managerHrCode, builder);
//		processHrCode(hrcode, builder);
//		System.out.println("query get hrcode: " + builder);
//		if(builder.length() < 1) builder.append("*:*");
//
//		return builder;
//	}
	
//	/**
//	 * Description: get data that has been aggregated by hrcode
//	 * @param firstDayText
//	 * @param lastDayText
//	 * @param hrcode
//	 * @return
//	 * @throws ParseException
//	 * TODO
//	 */
//	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String managerCode, String hrcode) throws ParseException {
//		StringBuilder builder = new StringBuilder();
//		
//		processDate(firstDayText, lastDayText, builder);
//		processHrCode(hrcode, builder);
//		
//		if(builder.length() < 1) builder.append("*:*");
//
//		return builder;
//	}
	
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
	
	protected void processHrCode(String hrcode, StringBuilder builder) {
		if(!StringUtils.isEmpty(hrcode)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" hrCode:").append(hrcode);
		}
	}
	
	protected void processManagerCode(String managerHrCode, StringBuilder builder) {
		if(!StringUtils.isEmpty(managerHrCode)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" managerHrCode:").append(managerHrCode);
		}
	}
	
	protected void processAccount(String accountNo, StringBuilder builder) {
		if(!StringUtils.isEmpty(accountNo)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" acctNo:").append(accountNo);
		}
	}
	
	protected StringBuilder appendAndClause(StringBuilder builder) {
		builder.append(" AND ");
		return builder;
	}
	
}
