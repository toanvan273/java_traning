package vn.com.vndirect.report.controller;

import static vn.com.vndirect.report.exception.ExceptionSupport.checkBrokenPipeEx;
import static vn.com.vndirect.report.service.Roles.HRCODE;
import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;
import static vn.com.vndirect.report.datasource.DailySaleReport.ACCT_NO;
import static vn.com.vndirect.report.datasource.PermissionFactory.STAFF_PERMISSION;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;
import vn.com.vndirect.report.datasource.Statistic;
import vn.com.vndirect.report.model.Permission;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.StaffSummaryService;
import vn.com.vndirect.report.support.StaffReportUtils;
/**
 * Description : business of sale daily report
 *
 */
@Controller
@RequestMapping(value = "daily-sale-report")
public class DailyStaffReportController extends AbstractReportController {

	private final static Logger logger = LoggerFactory.getLogger(DailyStaffReportController.class);

	@Autowired
	private StaffSummaryService staffSummaryService;
	
	@Value("${daily-sale-report}")
	private String dailySaleReportTemplate;
	
	@Value("${account-portfolio}")
	private String accountPortfolioTemplate;
	
	@Value("${nsr-detail}")
	private String nsrDetailTemplate;
	
	@Value("${nav-detail}")
	private String navDetailTemplate;
	
	@Value("${aum-detail}")
	private String aumDetailTemplate;
	
	@Value("${average-nav}")
	private String averageNavDetailTemplate;
	
	@Value("${average-aum}")
	private String averageAumDetailTemplate;
	
	@Autowired
	public DailyStaffReportController(DataReportFiller dataReportFiller, DataReportExporter dataExporter) throws JRException {
		super(dataReportFiller, dataExporter);
		setRequiredPermission(STAFF_PERMISSION);
	}

	/**
	 * Description: show main page daily sale report for staff(sale)
	 * @param authentication
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"staff", "staff/"})
	public ModelAndView staffReport(HttpSession session, Authentication authentication) {
		UserInfo userInfo = validate(session, authentication);
		
		if(!userInfo.valid()) return new ModelAndView("redirect:/403");
		Permission permission = userInfo.getPermission();
		session.setAttribute("accessURL", permission.getAccessURL());
		
		String hrcode = userInfo.getHrCode();
		session.setAttribute(HRCODE, hrcode);
		
		ModelAndView mv = new ModelAndView("daily-sale-report-staff");
		return mv;
	}

	/**
	 * Description: show html page of sale daily report
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param acctno
	 * @param product
	 * @param authentication
	 * @param response
	 * @param request
	 * @throws NumberFormatException 
	 * @throws Exception 
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"staff-view"})
	public void viewSaleReport(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "pageNum", required = false, defaultValue = "0") String pageNum,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
			if(StringUtils.isEmpty(pageNum)) { // thuannd review bo code day - dung default value
				pageNum = "0";
			}
			try {
				process(type, firstDayText, lastDayText, acctno, product, session, authentication, response,
						dailySaleReportTemplate, getDailyReportDownloadFileName(), true, false, true, Integer.valueOf(pageNum), isTotal);
			} catch (Exception e) {
				if (!checkBrokenPipeEx(e)) throw e;
			}
	}


	/**
	 * Description: show html page of account portfolio
	 * @param type
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"account-portfolio-view"})
	public void viewAccountPortfolio(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			processPortfolio(type, firstDayText, lastDayText, acctno, product, session, authentication, response,
					accountPortfolioTemplate, getPortfolioDownloadFileName(), isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}

	/**
	 * Description: show html page of nsr detail
	 * @param type
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"nsr-detail-view"})
	public void viewNSRDetail(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, acctno, product, session, authentication, response, 
					nsrDetailTemplate, getNsrDetailDownloadFileName(), isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}

	/**
	 * Description: show html page of value of end preriod NAV
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param acctno
	 * @param product
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"nav-detail-view"})
	public void viewValueOfEndPeriodNAV(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, acctno, product, session, authentication, response, 
					navDetailTemplate, getNavEndperiodDownloadFileName(), true, isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}

	/**
	 * Description: show html page of value of end period AUM
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param acctno
	 * @param product
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"aum-detail-view"})
	public void viewValueOfEndPeriodAUM(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, acctno, product, session, authentication, response, 
					aumDetailTemplate, getAumEndperiodDownloadFileName(), true, isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}

	/**
	 * Description: show html page of average of NAV
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param acctno
	 * @param product
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"average-nav-detail-view"})
	public void viewAverageNAV(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, acctno, product, session, authentication, response, 
					averageNavDetailTemplate, getNavAverageDownloadFileName(), isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}

	/**
	 * Description: show average of AUM
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param acctno
	 * @param product
	 * @param authentication
	 * @param response
	 * @param request
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"average-aum-detail-view"})
	public void viewAverageAUM(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "acctno", required = false) String acctno,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, acctno, product, session, authentication, response, 
					averageAumDetailTemplate, getAumAverageDownloadFileName(), isTotal);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}
	
	/**
	 * Description: get group name of sale in endperiod
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param session
	 * @param authentication
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"groupname"})
	public ResponseEntity<String> getGroupname(
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText, 
			HttpSession session,
			Authentication authentication) throws Exception {
		String hrCode = (String) session.getAttribute(HRCODE);
		String groupName = staffSummaryService.getGroupNameInEndperiod(lastDayText, hrCode, "groupName");
		return ResponseEntity.ok(groupName);
	}
	
	// thuannd review thiet ke object va pass object nay vao
	protected void process(String type, 
							String firstDayText, 
							String lastDayText, 
							String acctno, 
							String product, 
							HttpSession session, 
							Authentication authentication, 
							HttpServletResponse response, 
							String templateFileName, 
							String exportName, 
							boolean appendHeaderFooter, 
							boolean isEndperiodReport,
							boolean isPagination, 
							int pageNum, 
							String isTotal) throws Exception {
		if(StringUtils.isEmpty(type)) type = "html";
		Map<String, Object> parameters = new HashMap<>();
		long beginProcess= System.currentTimeMillis();
		String hrCode = (String) session.getAttribute(HRCODE);
		if(hrCode == null) {
			throw new Exception("Forbidden");
		}

		SolrDocumentList summaryData = staffSummaryService.getSummaryData(firstDayText, lastDayText, hrCode);
		SolrDocumentList filterAccountSummary = filterAccountRequest(summaryData, acctno);
		
		SolrDocumentList filterSummaryData = staffSummaryService.filterProduct(filterAccountSummary, product);
		int totalPage = 0;
		if(isPagination) {		
			totalPage = getTotalPage(filterSummaryData.size()); 
			logger.info("Total page: " + totalPage);
			session.setAttribute("totalPage", totalPage);
			parameters.put("pageSize", getPageSize());
		}

		Date queryLastDate = StaffReportUtils.convertDate(lastDayText, StaffReportUtils.DATE_SHORT_FORMAT, StaffReportUtils.CONVERT_BEGIN_DATE_TYPE);
		parameters.put(Statistic.QUERY_LAST_DATE_NAME, queryLastDate);
		
		JsonDataSource ds = convertToJson(filterSummaryData);
		pageNum = Math.min(pageNum, totalPage);
		initData(templateFileName, ds, parameters); //, totalPage, pageNum, filterSummaryData.size()
		
		String exportFileName = createFileNameReport(exportName, acctno, firstDayText, lastDayText, isEndperiodReport, Boolean.parseBoolean(isTotal));
		exportData(type, response, authentication, session, exportFileName, appendHeaderFooter, pageNum);
		long timeStop = System.currentTimeMillis();
		logger.info("tong thoi gian process:" + (timeStop - beginProcess));
	}
	
	protected void process(String type, String firstDayText, String lastDayText, String acctno, String product, 
			HttpSession session, Authentication authentication, HttpServletResponse response, 
			String templateFileName, String exportName, boolean isEndperiodReport, String isTotal) throws Exception {
		process(type, firstDayText, lastDayText, acctno, product, session, authentication, 
				response, templateFileName, exportName, false, isEndperiodReport, false, 0, isTotal);
	}
	
	protected void process(String type, String firstDayText, String lastDayText, String acctno, String product, 
			HttpSession session, Authentication authentication, HttpServletResponse response, 
			String templateFileName, String exportName, String isTotal) throws Exception {
		process(type, firstDayText, lastDayText, acctno, product, session, authentication, 
				response, templateFileName, exportName, false, false, false, 0, isTotal);
	}
	
	// thuannd review thiet ke object va pass object nay vao
	protected void processPortfolio(String type, String firstDayText, String lastDayText, String acctno, String product, 
			HttpSession session, Authentication authentication, HttpServletResponse response, 
			String templateFileName, String exportName, String isTotal) throws Exception {
		if(StringUtils.isEmpty(type)) type = "html";
		Map<String, Object> parameters = new HashMap<>();
		
		String hrCode = (String) session.getAttribute(HRCODE);
		if(hrCode == null) {
			throw new Exception("Forbidden");
		}
		
		// lay du lieu tu daily_sale_report view
		SolrDocumentList summaryData = staffSummaryService.getSummaryData(firstDayText, lastDayText, hrCode);
		SolrDocumentList filterAccountSummary = filterAccountRequest(summaryData, acctno);		
		
		SolrDocumentList filterSummaryData = staffSummaryService.filterProduct(filterAccountSummary, product);
		// lay du lieu tu portfolio view
		SolrDocumentList portfolioData = staffSummaryService.getPortfolioData(lastDayText, hrCode);
		
		SolrDocumentList accountPortfolioSummaryList = staffSummaryService.getPortfolioSummaryData(filterSummaryData, portfolioData);
		
		Date queryLastDate = StaffReportUtils.convertDate(lastDayText, StaffReportUtils.DATE_SHORT_FORMAT, StaffReportUtils.CONVERT_BEGIN_DATE_TYPE);
		parameters.put(Statistic.QUERY_LAST_DATE_NAME, queryLastDate);
		
		JsonDataSource ds = convertToJson(accountPortfolioSummaryList);
		initData(templateFileName, ds, parameters);
		String exportFileName = createFileNameReport(exportName, acctno, firstDayText, lastDayText , true, Boolean.parseBoolean(isTotal));
		exportData(type, response, authentication, session, exportFileName, 0);
	}
	
	private SolrDocumentList filterAccountRequest(SolrDocumentList summaryData, String acctnoRequest) {
		if (StringUtils.isEmpty(acctnoRequest)) return summaryData;

		SolrDocumentList filterSummaryData = new SolrDocumentList();
		Iterator<SolrDocument> iterator = summaryData.iterator();
		while (iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String acctno = (String) doc.getFieldValue(ACCT_NO);

			if (acctno.equals(acctnoRequest)) {
				filterSummaryData.add(doc);
				break;
			}
		}
		
		return filterSummaryData;
	}
	
}
