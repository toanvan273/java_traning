package vn.com.vndirect.report.pms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.query.result.FieldStatsResult;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import vn.com.vndirect.report.controller.AbstractReportController;
import vn.com.vndirect.report.pms.controller.CommonRevenueReportController.RevenueSummarize;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodCommission;
import vn.com.vndirect.report.pms.service.PeriodCommissionService;
import vn.com.vndirect.report.pms.service.PeriodService;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.ReportUtils;
import vn.com.vndirect.report.support.StaffReportUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonCommissionReportController extends AbstractReportController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${pdf-content}")
    protected String pdfContent;

    @Value("${excel-content}")
    protected String excelContent;

    @Value("${html-content}")
    protected String htmlContent;

    @Value("${commission-report}")
    protected String commissionReport;

    @Value("${commission-report-export}")
    protected String templateExport;

    @Value("${commission-report-name}")
    protected String nameFileDailyReport;

    protected final PeriodService periodService;
    protected final PeriodCommissionService periodCommissionService;

    public CommonCommissionReportController(DataReportFiller dataReportFiller,
                                            DataReportExporter dataExporter,
                                            PeriodService periodService,
                                            PeriodCommissionService periodCommissionService) throws JRException {
        super(dataReportFiller, dataExporter);
        this.periodService = periodService;
        this.periodCommissionService = periodCommissionService;
    }

    public JsonDataSource convertToJson(List<PeriodCommission> periodRevenues) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(periodRevenues);
            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes("utf8"));
            //Create json datasource from json stream
            return new JsonDataSource(jsonDataStream);
        } catch(Exception exp) {
            logger.error(exp.getMessage(), exp);
            return null;
        }
    }

    protected RevenueSummarize getSumTotal(CommissionQuery query, Long fromDate, Long toDate) {
        List<Period> periods = periodService.findByInfo(query.getDepartment(), query.getHrCode(), query.getCareByName(),
                query.getPosition(), fromDate, toDate);
        if (CollectionUtils.isEmpty(periods)) {
            return RevenueSummarize.builder().build();
        }

        StatsPage<PeriodCommission> periodRevenues = periodCommissionService.getSumTotal(periods.stream().map(Period::getId).collect(Collectors.toList()));
        FieldStatsResult totalMatchQuantity = periodRevenues.getFieldStatsResult("totalMatchQuantity");
        FieldStatsResult totalTradingFee = periodRevenues.getFieldStatsResult("totalTradingFee");
        FieldStatsResult totalRevenue = periodRevenues.getFieldStatsResult("totalCommission");

        return RevenueSummarize.builder()
                .totalMatchQuantity(((Double) totalMatchQuantity.getSum()).longValue())
                .totalTradingFee(((Double) totalTradingFee.getSum()).longValue())
                .totalRevenue(((Double) totalRevenue.getSum()).longValue())
                .build();
    }

    protected List<PeriodCommission> fillInfo(List<PeriodCommission> periodRevenues, List<Period> periods) {
        periodRevenues.stream().forEach(pr -> {
            Period period = periods.stream().filter(p -> pr.getPeriodId().equals(p.getId())).findAny().get();
            pr.setCareByName(period.getCareByName());
            pr.setDepartmentName(period.getDepartmentName());
            pr.setHrCode(period.getHrCode());
            pr.setPosition(period.getPosition());
        });

        return periodRevenues;
    }

    protected Map<String, Object> putParamToJasper(RevenueSummarize revenueSummarize) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("totalMatchQuantity", revenueSummarize.getTotalMatchQuantity());
        parameters.put("totalTradingFee", revenueSummarize.getTotalTradingFee());
        parameters.put("totalRevenue", revenueSummarize.getTotalRevenue());

        return parameters;
    }

    protected Map<String, Object> putParamJasperToExport(CommissionQuery query, RevenueSummarize revenueSummarize) {
        Map<String, Object> parameters = this.putParamToJasper(revenueSummarize);
        parameters.put("departmentName", query.getDepartment());
        parameters.put("fromDate", query.getFromDate());
        parameters.put("toDate", query.getToDate());
        parameters.put("hrCode", query.getHrCode());
        parameters.put("position", query.getPosition());

        return parameters;
    }

    protected Long formatDate(String date, int type) throws ParseException {
        String dateFormat = "dd/MM/yyyy";

        return ReportUtils.convertDate(date, dateFormat, type).getTime();
    }

    protected void exportExcel(Authentication auth, String fileName, HttpServletResponse response) throws IOException, JRException {
        response.setContentType(excelContent);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);

        dataExporter.exportToXlsx(response.getOutputStream(), auth.getName());
    }

    protected void exportHtml(HttpSession session, HttpServletResponse response) throws IOException, JRException {
        SimpleHtmlExporterConfiguration htmlConfig = new SimpleHtmlExporterConfiguration();
        htmlConfig.setHtmlHeader(StaffReportUtils.getHtmlHeader());
        htmlConfig.setHtmlFooter(StaffReportUtils.getHtmlFooter(session));
        response.setContentType(htmlContent);

        dataExporter.exportToHtml(htmlConfig, null, response.getOutputStream());
    }

    @Setter
    @Getter
    public static class CommissionQuery {
        private String fromDate;
        private String toDate;
        private String product;
        private String department;
        private String careByName;
        private String hrCode;
        private Integer pageNum;
        private Integer pageSize;
        private String position;
    }
}
