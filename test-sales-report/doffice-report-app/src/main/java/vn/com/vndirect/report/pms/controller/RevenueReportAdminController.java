package vn.com.vndirect.report.pms.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vn.com.vndirect.report.config.IsAdmin;
import vn.com.vndirect.report.model.AccessURL;
import vn.com.vndirect.report.model.Permission;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.service.PeriodInfoService;
import vn.com.vndirect.report.pms.service.PeriodService;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ReportUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.com.vndirect.report.datasource.PermissionFactory.ADMIN_PERMISSION;
import static vn.com.vndirect.report.datasource.PermissionFactory.STAFF_PERMISSION;
import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;

@Controller
@RequestMapping(value = "revenue-report")
public class RevenueReportAdminController extends CommonRevenueReportController {

	private final PeriodService periodService;

	public RevenueReportAdminController(DataReportFiller dataReportFiller,
											DataReportExporter dataExporter, 
											PeriodInfoService periodInfoService, 
											PeriodService periodService) throws JRException {
		super(dataReportFiller, dataExporter, periodInfoService);
		// Khi cÃ³ cÃ³ quyá»�n admin thÃ¬ má»Ÿ comment
//		setRequiredPermission(ADMIN_PERMISSION);
		this.periodService = periodService;
	}

	@GetMapping(value = {"admin", "admin/"})
	@IsAdmin
	public ModelAndView staffReport(HttpSession session, Authentication auth) {
		return new ModelAndView("revenue-report-admin");
	}

	@GetMapping(value = {"/admin-view"})
	@IsAdmin
	public void view(@ModelAttribute RevenueQuery query, HttpSession session, HttpServletResponse response) throws Exception {
		Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
		Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

		List<Period> periods = periodService.findByInfo(query.getDepartment(), query.getHrCode(), query.getCareByName(), query.getPosition(), fromDate, toDate);
		List<String> periodIds = periods.stream().map(Period::getId).collect(Collectors.toList());
		Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
		Page<PeriodInfo> periodInfos = periodInfoService.findByPeriodIdsAndAccNo(periodIds, query.getAcctNo(), pageable);
		periodInfos.forEach(periodInfo -> {
			Period period = periods.stream().filter(p -> periodInfo.getPeriodId().equals(p.getId())).findAny().get();

			periodInfo.setCareByName(period.getCareByName());
			periodInfo.setHrCode(period.getHrCode());
			periodInfo.setDepartmentName(period.getDepartmentName());
			periodInfo.setTypeCalculate("Doanh sá»‘ phÃ­ thÆ°c");
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

	@GetMapping(value = {"/admin-view/export"})
	public void export(@ModelAttribute RevenueQuery query, Authentication auth, HttpServletResponse response) throws Exception {
		Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
		Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

		List<Period> periods = periodService.findByInfo(query.getDepartment(), query.getHrCode(), query.getCareByName(), query.getPosition(), fromDate, toDate);
		List<String> periodIds = periods.stream().map(Period::getId).collect(Collectors.toList());
		List<PeriodInfo> periodInfos = periodInfoService.findByPeriodIdsAndAccNo(periodIds, query.getAcctNo());
		periodInfos.forEach(periodInfo -> {
			Period period = periods.stream().filter(p -> periodInfo.getPeriodId().equals(p.getId())).findAny().get();

			periodInfo.setCareByName(period.getCareByName());
			periodInfo.setHrCode(period.getHrCode());
			periodInfo.setDepartmentName(period.getDepartmentName());
			periodInfo.setTypeCalculate("Doanh sá»‘ phÃ­ thÆ°c");
		});

		JsonDataSource dataSource = this.convert(periodInfos);

		RevenueSummarize revenueSummarize = this.getSumTotalByPeriodS(periodIds, query.getAcctNo());
		this.initData(reportExportTemplate, dataSource, this.putParamJasperToExport(query, revenueSummarize));

		String exportFileName = createFileNameReport(nameFileDailyReport, query.getAcctNo(), query.getFromDate(), query.getToDate(), false, false);
		this.exportExcel(auth, exportFileName, response);
	}
}
