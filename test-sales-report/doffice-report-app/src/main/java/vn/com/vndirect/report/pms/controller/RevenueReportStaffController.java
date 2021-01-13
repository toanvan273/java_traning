package vn.com.vndirect.report.pms.controller;

import static vn.com.vndirect.report.service.Roles.HRCODE;
import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.service.PeriodInfoService;
import vn.com.vndirect.report.pms.service.PeriodService;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ReportUtils;
import vn.com.vndirect.vndid.session.model.UserSession;

import java.util.Map;

import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;

@Controller
@RequestMapping(value = "revenue-report")
public class RevenueReportStaffController extends CommonRevenueReportController {

	private final PeriodService periodService;

	@Autowired
	public RevenueReportStaffController(DataReportFiller dataReportFiller,
											DataReportExporter dataReportExporter, 
											PeriodInfoService periodInfoService, 
											PeriodService periodService) throws JRException {
		super(dataReportFiller, dataReportExporter, periodInfoService);
		this.periodService = periodService;
	}

	@RequestMapping(method = RequestMethod.GET, value = {"staff", "staff/"})
	public ModelAndView staffReport(HttpSession session, Authentication auth) {
		UserInfo userInfo = validate(session, auth);
		if(!userInfo.valid()) return new ModelAndView("redirect:/403");

		ModelAndView mv = new ModelAndView("revenue-report-staff");

		UserSession userSession = (UserSession) auth.getDetails();
		mv.addObject("fullName", userSession.getUser().getFullName());
		mv.addObject("department", userInfo.getGroups().get(0).getFullName());

		mv.addObject(HRCODE, userInfo.getHrCode());
		return mv;
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/staff-view"})
	public void view(@ModelAttribute RevenueQuery query,
					HttpSession session,
					Authentication auth,
					HttpServletResponse response) throws Exception {
		UserInfo userInfo = validate(session, auth);
		if(!userInfo.valid()) return;

		Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
		Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

		List<Period> periods = periodService.findByHrCodeAndFromDateAndToDate(userInfo.getHrCode(), fromDate, toDate);
		List<String> periodIds = periods.stream().map(Period::getId).collect(Collectors.toList());
		Page<PeriodInfo> periodInfos = periodInfoService.findByPeriodIdsAndAccNo(periodIds, query.getAcctNo(), PageRequest.of(query.getPageNum() - 1, query.getPageSize()));
		periodInfos.forEach(periodInfo -> {
			Period period = periods.stream().filter(p -> periodInfo.getPeriodId().equals(p.getId())).findAny().get();

			periodInfo.setCareByName(period.getCareByName());
			periodInfo.setHrCode(period.getHrCode());
			periodInfo.setDepartmentName(period.getDepartmentName());
			periodInfo.setTypeCalculate("Doanh số phí thưc");
		});
		session.setAttribute("totalPage", periodInfos.getTotalPages());

		JsonDataSource ds = this.convert(periodInfos.getContent());
		RevenueSummarize revenueSummarize = this.getSumTotalByPeriodS(periodIds, query.getAcctNo());
		Map<String, Object> param = this.putParamToJasper(revenueSummarize);
		param.put("pageNumber", query.getPageNum());
		param.put("pageSize", query.getPageSize());
		this.initData(reportTemplate, ds, param);
		this.exportHtml(session, response);
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/staff-view/export"})
	public void export(@ModelAttribute RevenueQuery query, HttpSession session, Authentication auth, HttpServletResponse response) throws Exception {
		UserInfo userInfo = validate(session, auth);
		if(!userInfo.valid()) return ;

		Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
		Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

		List<Period> periods = periodService.findByHrCodeAndFromDateAndToDate(userInfo.getHrCode(), fromDate, toDate);
		List<String> periodIds = periods.stream().map(Period::getId).collect(Collectors.toList());
		List<PeriodInfo> periodInfos = periodInfoService.findByPeriodIdsAndAccNo(periodIds, query.getAcctNo());
		periodInfos.stream().forEach(periodInfo -> {
			Period period = periods.stream().filter(p -> periodInfo.getPeriodId().equals(p.getId())).findAny().get();

			periodInfo.setCareByName(period.getCareByName());
			periodInfo.setHrCode(period.getHrCode());
			periodInfo.setDepartmentName(period.getDepartmentName());
			periodInfo.setTypeCalculate("Doanh số phí thưc");
		});

		JsonDataSource dataSource = this.convert(periodInfos);
		RevenueSummarize revenueSummarize = this.getSumTotalByPeriodS(periodIds, query.getAcctNo());
		this.initData(reportExportTemplate, dataSource, this.putParamJasperToExport(query, revenueSummarize));

		String exportFileName = createFileNameReport(nameFileDailyReport, query.getAcctNo(), query.getFromDate(), query.getToDate(), false, false);
		this.exportExcel(auth, exportFileName, response);
	}
}
