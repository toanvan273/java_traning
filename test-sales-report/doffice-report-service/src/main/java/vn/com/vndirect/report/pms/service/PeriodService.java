package vn.com.vndirect.report.pms.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.repo.PeriodRepository;

@Service
public class PeriodService {

    private final PeriodRepository repo;
    private final SolrTemplate solrTemplate;

    public PeriodService(PeriodRepository repo, SolrTemplate solrTemplate) {
        this.repo = repo;
        this.solrTemplate = solrTemplate;
    }

    public List<Period> findByHrCodeAndFromDateAndToDate(String hrCode, Long fromDate, Long toDate) {
        List<Period> periods = repo.findByHrCodeAndFromDateGreaterThanEqualAndToDateLessThanEqual(hrCode, fromDate, toDate);
        return CollectionUtils.isEmpty(periods) ? Collections.emptyList() : periods;
    }

    public List<Period> findByFromDateAndToDate(Long fromDate, Long toDate) {
        List<Period> periods = repo.findByFromDateGreaterThanEqualAndToDateLessThanEqual(fromDate, toDate);
        return CollectionUtils.isEmpty(periods) ? Collections.emptyList() : periods;
    }

    public List<Period> findByInfo(String departmentName, String hrCode, String careByName, String position, Long fromDate, Long toDate) {
        SimpleQuery simpleQuery = new SimpleQuery(String.join(" AND ",
                "departmentName:" + formatStringQuerySolr(departmentName),
                "hrCode:" + formatStringQuerySolr(hrCode),
                "careByName:" + formatStringQuerySolr(careByName),
                "position:" + formatStringQuerySolr(position),
                "fromDate:" + "[ " + fromDate + " TO *]",
                "toDate:" + "[* TO " + toDate + " ]"));

        simpleQuery.setRows(100000000);
        Page<Period> periods = solrTemplate.queryForPage("period", simpleQuery, Period.class);

        return periods.toList();
    }

    private String formatStringQuerySolr(String term) {
        if (StringUtils.isEmpty(term)) {
            return "*";
        }
        term = term.trim();
        return "\"" + term + "\"";
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
