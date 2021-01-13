package vn.com.vndirect.report.pms.controller;

import static vn.com.vndirect.report.support.StaffReportUtils.createFileNameReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;
import vn.com.vndirect.report.config.IsAdmin;
import vn.com.vndirect.report.pms.controller.CommonRevenueReportController.RevenueSummarize;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodCommission;
import vn.com.vndirect.report.pms.service.PeriodCommissionService;
import vn.com.vndirect.report.pms.service.PeriodService;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ReportUtils;

@Controller
@RequestMapping(value = "commission-report")
public class CommissionReportAdminController extends CommonCommissionReportController {

    public CommissionReportAdminController(DataReportFiller dataReportFiller,
                                           DataReportExporter dataExporter,
                                           PeriodService periodService,
                                           PeriodCommissionService periodCommissionService) throws JRException {
        super(dataReportFiller, dataExporter, periodService, periodCommissionService);
    }

    @GetMapping(value = {"admin", "admin/"})
    @IsAdmin
    public ModelAndView staffReport(HttpSession session, Authentication authentication) {
        return new ModelAndView("commission-report-admin");
    }

    @GetMapping(value = {"/admin-view"})
    @IsAdmin
    public void view(@ModelAttribute CommissionQuery query, HttpSession session, HttpServletResponse response) throws Exception {
        Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
        Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

        List<Period> periods = periodService.findByInfo(query.getDepartment(), query.getHrCode(), query.getCareByName(),
                query.getPosition(), fromDate, toDate);

        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
        Page<PeriodCommission> periodRevenues = periodCommissionService.findByPeriods(periods.stream()
                .map(Period::getId)
                .collect(Collectors.toList()), pageable);
        this.fillInfo(periodRevenues.getContent(), periods);

        session.setAttribute("totalPage", periodRevenues.getTotalPages());
        RevenueSummarize revenueSummarize = this.getSumTotal(query, fromDate, toDate);
        JsonDataSource ds = convertToJson(periodRevenues.getContent());
        Map<String, Object> param = this.putParamToJasper(revenueSummarize);
        param.put("pageNumber", query.getPageNum());
        param.put("pageSize", query.getPageSize());
        initData(commissionReport, ds, param);

        this.exportHtml(session, response);
    }

    @GetMapping(value = {"/admin-view/export"})
    @IsAdmin
    public void export(@ModelAttribute CommissionQuery query, Authentication auth, HttpServletResponse response) throws Exception {
        Long fromDate = this.formatDate(query.getFromDate(), ReportUtils.CONVERT_BEGIN_DATE_TYPE);
        Long toDate = this.formatDate(query.getToDate(), ReportUtils.CONVERT_END_DATE_TYPE);

        List<Period> periods = periodService.findByInfo(query.getDepartment(), query.getHrCode(), query.getCareByName(),
                query.getPosition(), fromDate, toDate);
        List<PeriodCommission> periodRevenues = periodCommissionService.findByPeriods(periods.stream()
                .map(Period::getId)
                .collect(Collectors.toList()));
        this.fillInfo(periodRevenues, periods);

        RevenueSummarize revenueSummarize = this.getSumTotal(query, fromDate, toDate);
        JsonDataSource ds = convertToJson(periodRevenues);
        initData(templateExport, ds, this.putParamJasperToExport(query, revenueSummarize));

        String exportFileName = createFileNameReport(nameFileDailyReport, query.getHrCode(), query.getFromDate(), query.getToDate(), false, false);
        this.exportExcel(auth, exportFileName, response);
    }
}
