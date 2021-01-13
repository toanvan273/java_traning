package vn.com.vndirect.report.pms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.StatsOptions;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.vndirect.report.pms.document.PeriodCommission;
import vn.com.vndirect.report.pms.repo.PeriodCommissionRepository;

import java.util.Collections;
import java.util.List;


@Service
public class PeriodCommissionService {

    private final PeriodCommissionRepository repo;
    private final SolrTemplate solrTemplate;

    public PeriodCommissionService(PeriodCommissionRepository repo, SolrTemplate solrTemplate) {
        this.repo = repo;
        this.solrTemplate = solrTemplate;
    }

    public List<PeriodCommission> findByPeriods(List<String> periodIds) {
        if (CollectionUtils.isEmpty(periodIds)) {
            return Collections.emptyList();
        }
        List<PeriodCommission> periodRevenues = repo.findByPeriodIdInAndTotalMatchQuantityNotAndTotalTradingFeeNotAndTotalCommissionNot(periodIds, 0L, 0L, 0L);

        return CollectionUtils.isEmpty(periodRevenues) ? Collections.emptyList() : periodRevenues;
    }

    public Page<PeriodCommission> findByPeriods(List<String> periods, Pageable pageable) {
        if (CollectionUtils.isEmpty(periods)) {
            return Page.empty();
        }
        Page<PeriodCommission> periodRevenues = repo.findByPeriodIdInAndTotalMatchQuantityNotAndTotalTradingFeeNotAndTotalCommissionNot(periods, pageable, 0L, 0L, 0L);

        return CollectionUtils.isEmpty(periodRevenues.getContent()) ? Page.empty() : periodRevenues;
    }

    public StatsPage<PeriodCommission> getSumTotal(List<String> periodIds) {
        StatsOptions statsOptions = new StatsOptions();
        statsOptions.addField("totalMatchQuantity");
        statsOptions.addField("totalTradingFee");
        statsOptions.addField("totalCommission");

        SimpleQuery simpleQuery = new SimpleQuery("periodId: (" + String.join(" or ", periodIds) + ")");
        simpleQuery.setStatsOptions(statsOptions);

        return solrTemplate.queryForStatsPage("period-commission", simpleQuery, PeriodCommission.class);
    }
}
