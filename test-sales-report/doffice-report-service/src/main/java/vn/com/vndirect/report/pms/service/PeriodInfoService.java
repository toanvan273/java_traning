package vn.com.vndirect.report.pms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.StatsOptions;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.repo.PeriodInfoRepository;

import java.util.Collections;
import java.util.List;

@Service
public class PeriodInfoService {

    private final PeriodInfoRepository repo;
    private final SolrTemplate solrTemplate;
    private final PeriodService periodService;

    public PeriodInfoService(PeriodInfoRepository repo, SolrTemplate solrTemplate, PeriodService periodService) {
        this.repo = repo;
        this.solrTemplate = solrTemplate;
        this.periodService = periodService;
    }

    public Page<PeriodInfo> findByPeriodIdsAndAccNo(List<String> periodIds, String accNo, Pageable pageable) {
        if (CollectionUtils.isEmpty(periodIds)) {
            return Page.empty();
        }
        Page<PeriodInfo> periodInfos = repo.findByPeriodIdInAndCustomerAccountNumberLikeAndTotalMatchQuantityNotAndTotalTradingFeeNot(periodIds, accNo, 0L, 0L, pageable);

        return CollectionUtils.isEmpty(periodInfos.getContent()) ? Page.empty() : periodInfos;
    }

    public List<PeriodInfo> findByPeriodIdsAndAccNo(List<String> periodIds, String accNo) {
        if (CollectionUtils.isEmpty(periodIds)) {
            return Collections.emptyList();
        }
        List<PeriodInfo> periodInfos = repo.findByPeriodIdInAndCustomerAccountNumberLikeAndTotalMatchQuantityNotAndTotalTradingFeeNot(periodIds, accNo, 0L, 0L);

        return CollectionUtils.isEmpty(periodInfos) ? Collections.emptyList() : periodInfos;
    }

    public StatsPage<PeriodInfo> getSumTotal(List<String> periodIds, String accNo) {
        StatsOptions statsOptions = new StatsOptions();
        statsOptions.addField("totalMatchQuantity");
        statsOptions.addField("totalTradingFee");

        SimpleQuery simpleQuery = new SimpleQuery(String.join(" AND ",
                "periodId: (" + String.join(" or ", periodIds) + ")",
                "customerAccountNumber:" + this.formatStringQuerySolr(accNo) + "*"));

        simpleQuery.setStatsOptions(statsOptions);

        return solrTemplate.queryForStatsPage("period-info", simpleQuery, PeriodInfo.class);
    }

    private String formatStringQuerySolr(String term) {
        if (StringUtils.isEmpty(term)) {
            return "";
        }
        return "\"" + term + "\"";
    }
}
