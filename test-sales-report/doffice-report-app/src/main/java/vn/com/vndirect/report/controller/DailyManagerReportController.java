package vn.com.vndirect.report.controller;

import static vn.com.vndirect.report.exception.ExceptionSupport.checkBrokenPipeEx;
import static vn.com.vndirect.report.service.Roles.GROUP;
import static vn.com.vndirect.report.datasource.PermissionFactory.MANAGER_PERMISSION;
import static vn.com.vndirect.report.support.StaffReportUtils.REPORT_TYPE_MANAGER;
import static vn.com.vndirect.report.datasource.DailySaleReport.*;

import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.transform.ToListResultTransformer;
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
import vn.com.vndirect.report.model.Group;
import vn.com.vndirect.report.model.Permission;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ManagerSummaryService;

/*
 * tk test new_ai_do_82 - 12345678
 */
		
@Controller
@RequestMapping(value = "daily-manager-report")
public class DailyManagerReportController extends AbstractReportController {

	private final static Logger logger = LoggerFactory.getLogger(DailyManagerReportController.class);
	
	@Value("${manager.daily-sale-report}")
	private String dailyManagerReportTemplate;
	
	@Value("${manager.nsr-detail}")
	private String nsrTemplate;
	
	@Value("${manager.nav-endperiod}")
	private String navEnperiodTemplate;
	
	@Value("${manager.aum-endperiod}")
	private String aumEnperiodTemplate;
	
	@Value("${manager.average-nav}")
	private String navAverageTemplate;
	
	@Value("${manager.average-aum}")
	private String aumAverageTemplate;
	
	@Value("${manager.field-list.salename-hrcode}")
	private String flSaleNameHrcode;
	
	@Value("${manager.query-param.groupname-groupid}")
	private String conditionGroupnameGroupId;
	
	@Autowired
	private ManagerSummaryService managerSummaryService;

	@Autowired
	public DailyManagerReportController(DataReportFiller dataReportFiller, DataReportExporter dataExporter) throws JRException {
		super(dataReportFiller, dataExporter);
		setRequiredPermission(MANAGER_PERMISSION);
	}

	/**
	 * Description: show view manager
	 * @param session
	 * @param authentication
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"manager", "manager/"})
	public ModelAndView managerReport(HttpSession session, Authentication authentication) throws Exception {
		UserInfo userInfo = validate(session, authentication);
		
		if(!userInfo.valid()) return new ModelAndView("redirect:/403");
		
		Permission permission = userInfo.getPermission();
		session.setAttribute("accessURL", permission.getAccessURL());

		List<Group> groups = userInfo.getGroups();
		session.setAttribute(GROUP, groups);
		
		ModelAndView mv = new ModelAndView("daily-sale-report-manager");
		return mv;
	}
	
	/**
	 * Description: api get list sale name and hrcode with condition are specific time and managercode
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param session
	 * @param authentication
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = {"/hrcode"})
	public ResponseEntity<SolrDocumentList> getListSaleFullNameHrCode(
			@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication) throws Exception {		
		List<Group> groupIdSessions =  (List<Group>) session.getAttribute(GROUP);
		if(!isValidGroupIdUserid(groupIdSessions, groupId)) throw new Exception(); //thuannd review exception can co message ro rang
		
		SolrDocumentList listSaleFullNameHrCode = managerSummaryService.getListSaleFullNameHrcode(firstDayText, lastDayText, groupId, flSaleNameHrcode, "hrCode");
		sortByLastName(listSaleFullNameHrCode);		
		return ResponseEntity.ok(listSaleFullNameHrCode);
	}
	
	/**
	 * Description: show html page of sale daily report for role manager
	 * @param type
	 * @param firstDayText
	 * @param lastDayText
	 * @param saleHrCode
	 * @param session
	 * @param authentication
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"manager-view"})
	public void viewManagerDailyReport(@RequestParam(name = "type", defaultValue = "html", required = false) String type,
			@RequestParam(name = "firstDay", required = false) String firstDayText,
			@RequestParam(name = "lastDay", required = false) String lastDayText,
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {	
		try {
			process(type, firstDayText, lastDayText, dailyManagerReportTemplate, getDailyReportDownloadFileName(), true,
					saleHrCode.trim(), groupId, false, isTotal, session, authentication, response);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e))
				throw e;
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
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
			try {
				process(type, firstDayText, lastDayText, nsrTemplate, getNsrDetailDownloadFileName(), false,
						saleHrCode, groupId, false, isTotal, session, authentication, response);
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
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, navEnperiodTemplate, getNavEndperiodDownloadFileName(), false,
					saleHrCode, groupId, true, isTotal, session, authentication, response);
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
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, aumEnperiodTemplate, getAumEndperiodDownloadFileName(), false,
					saleHrCode, groupId, true, isTotal, session, authentication, response);
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
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, navAverageTemplate, getNavAverageDownloadFileName(), false,
					saleHrCode, groupId, false, isTotal, session, authentication, response);
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
			@RequestParam(name = "saleHrCode", required = false) String saleHrCode,
			@RequestParam(name = "groupId", required = false) String groupId,
			@RequestParam(name = "total", defaultValue = "false", required = false) String isTotal,
			HttpSession session,
			Authentication authentication,
			HttpServletResponse response) throws Exception {
		try {
			process(type, firstDayText, lastDayText, aumAverageTemplate, getAumAverageDownloadFileName(), false,
					saleHrCode, groupId, false, isTotal, session, authentication, response);
		} catch (Exception e) {
			if (!checkBrokenPipeEx(e)) throw e;
		}
	}
	
	//thuannd review ham qua nhieu tham so - can thiet ke object va pass object vao
	protected void process(String type, String firstDayText, String lastDayText, String fileTemplate,
			String prefixFileNameExport, boolean appendHeaderFooter, String saleHrCode, String groupIdRequest,
			boolean isEndperiodReport, String isTotal, HttpSession session, Authentication authentication, HttpServletResponse response)
			throws Exception {
		
		List<Group> groupIdSessions =  (List<Group>) session.getAttribute(GROUP);
		if(!isValidGroupIdUserid(groupIdSessions, groupIdRequest)) throw new Exception();
		
		if (StringUtils.isEmpty(type)) type = "html";
		Map<String, Object> parameters = new HashMap<>();
		long beginProcess = System.currentTimeMillis(); // thuannd review long start =
		
		String groupSummary = groupIdSessions.stream().map(x -> x.getCode()).collect(Collectors.joining(" "));
		
		SolrDocumentList summaryData = managerSummaryService.getSummaryData(firstDayText, lastDayText, groupSummary);
		SolrDocumentList filterGroupIdRequest = filterdGroupRequest(groupIdRequest, summaryData);
		
		SolrDocumentList filterSummaryData = managerSummaryService.filterSaleHrCode(filterGroupIdRequest, saleHrCode);

		int pageNum = 0;
		int totalPage = 0;
		// man hinh chinh
//		if(appendHeaderFooter) totalPage = pageNum = 1 ;
		
		session.setAttribute("totalPage", totalPage);		
		JsonDataSource ds = convertToJson(filterSummaryData);		
		initData(fileTemplate, ds, parameters); //, totalPage, pageNum, filterSummaryData.size()

		String exportFileName = createFileNameReport(prefixFileNameExport, saleHrCode, firstDayText, lastDayText, isEndperiodReport, Boolean.parseBoolean(isTotal), REPORT_TYPE_MANAGER);
		exportData(type, response, authentication, session, exportFileName, appendHeaderFooter, 0);
		long timeStop = System.currentTimeMillis();
		logger.info("tong thoi gian process:" + (timeStop - beginProcess)); //thuannd review System.currentTime - start la duoc
	}
	
	private SolrDocumentList filterdGroupRequest(String groupIdRequest, SolrDocumentList summaryData) {
		List<String> listGroupIdRequest = Stream.of(groupIdRequest.split(" ")).collect(Collectors.toList());
		
		Iterator<SolrDocument> iterator = summaryData.iterator();
		SolrDocumentList result = new SolrDocumentList();
		
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String groupIdUserid = (String) doc.getFieldValue(GROUP_ID_USERID);
			
			if(listGroupIdRequest.contains(groupIdUserid)) result.add(doc);
		}
		
		return result;
	}
	
	private SolrDocumentList sortByLastName(SolrDocumentList solrDocumentList) {

		Collections.sort(solrDocumentList, new Comparator<SolrDocument>() {
			@Override
			public int compare(SolrDocument o1, SolrDocument o2) {
				String fullName1 = (String) o1.getFieldValue(SALE_FULL_NAME);
				String revertLastName1 = revertLastName(fullName1);

				String fullName2 = (String) o2.getFieldValue(SALE_FULL_NAME);
				String revertLastName2 = revertLastName(fullName2);

				return revertLastName1.compareToIgnoreCase(revertLastName2);
			}
		});
		return solrDocumentList;

	}

	private String revertLastName(String fullName) {
		int key = fullName.lastIndexOf(" ");
		String revertName = fullName.substring(key + 1, fullName.length()) + " " + fullName.substring(0, key);
		return revertName;
	}
}
