package vn.com.vndirect.report.pms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
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
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.service.PeriodInfoService;
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

public class CommonRevenueReportController extends AbstractReportController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${pdf-content}")
    protected String pdfContent;

    @Value("${excel-content}")
    protected String excelContent;

    @Value("${html-content}")
    protected String htmlContent;

    @Value("${revenue-report}")
    protected String reportTemplate;

    @Value("${revenue-report-export}")
    protected String reportExportTemplate;

    @Value("${revenue-report-name}")
    protected String nameFileDailyReport;

    protected final PeriodInfoService periodInfoService;

    public CommonRevenueReportController(DataReportFiller dataReportFiller,
                                         DataReportExporter dataReportExporter,
                                         PeriodInfoService periodInfoService) throws JRException {
        super(dataReportFiller, dataReportExporter);
        this.periodInfoService = periodInfoService;
        this.dataReportFiller.setReportFileName("revenue-report.jrxml");
        this.dataReportFiller.compileReport();
    }

    protected Long formatDate(String date, int type) throws ParseException {
        String dateFormat = "dd/MM/yyyy";

        return ReportUtils.convertDate(date, dateFormat, type).getTime();
    }

    protected JsonDataSource convert(List<PeriodInfo> periodInfos) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(periodInfos);
            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes("utf8"));
            return new JsonDataSource(jsonDataStream);
        } catch(Exception exp) {
            logger.error(exp.getMessage(), exp);
            return null;
        }
    }

    /*protected boolean isEmployee(HttpSession session, Authentication authentication) {
        List<MembershipResponse> memberships = roleService.filterSessionRole(session, authentication, STAFF_ROLE);
        if(memberships.isEmpty()) return false;

        department department = (Department)session.getAttribute("department");
        if(department != null) return true;

        department = roleService.getDepartent(memberships.get(0).getGroupName());
        if(department != null) session.setAttribute("department", department);

        return true;
    }*/

    protected RevenueSummarize getSumTotalByPeriodS(List<String> periodIds, String accNo) {
        if (CollectionUtils.isEmpty(periodIds)) {
            return RevenueSummarize.builder().build();
        }

        StatsPage<PeriodInfo> periodInfos = periodInfoService.getSumTotal(periodIds, accNo);
        FieldStatsResult totalMatchQuantity = periodInfos.getFieldStatsResult("totalMatchQuantity");
        FieldStatsResult totalTradingFee = periodInfos.getFieldStatsResult("totalTradingFee");

        return RevenueSummarize.builder()
                .totalMatchQuantity(((Double) totalMatchQuantity.getSum()).longValue())
                .totalTradingFee(((Double) totalTradingFee.getSum()).longValue())
                .build();
    }

    protected Map<String, Object> putParamJasperToExport(RevenueQuery query, RevenueSummarize revenueSummarize) {
        Map<String, Object> parameters = this.putParamToJasper(revenueSummarize);
        parameters.put("careByName", query.getCareByName());
        parameters.put("accNo", query.getAcctNo());
        parameters.put("department", query.getDepartment());
        parameters.put("fromDate", query.getFromDate());
        parameters.put("toDate", query.getToDate());
        parameters.put("hrCode", query.getHrCode());
        parameters.put("role", query.getPosition());

        return parameters;
    }

    protected Map<String, Object> putParamToJasper(RevenueSummarize revenueSummarize) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("totalMatchQuantity", revenueSummarize.getTotalMatchQuantity());
        parameters.put("totalTradingFee", revenueSummarize.getTotalTradingFee());
        parameters.put("realTotalMatchQuantity", revenueSummarize.getTotalMatchQuantity());
        parameters.put("realTotalTradingFee", revenueSummarize.getTotalTradingFee());

        return parameters;
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
    public static class RevenueQuery {
        private String fromDate;
        private String toDate;
        private String acctNo;
        private String product;
        private Integer pageNum;
        private Integer pageSize;
        private String hrCode;
        private String department;
        private String careByName;
        private String position;
    }


    @Setter
    @Getter
    @Builder
    public static class RevenueSummarize {
        private Long totalMatchQuantity;
        private Long totalTradingFee;
        private Long totalRevenue;
    }
}
