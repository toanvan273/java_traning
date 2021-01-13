package vn.com.vndirect.report.pms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FieldStatsResult;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.data.solr.core.query.result.StatsResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.vndirect.report.pms.document.SaleReportDocument;
import vn.com.vndirect.report.pms.repo.SaleReportRepository;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SaleReportController {
	
    private final SaleReportRepository repo;

    @GetMapping("/page")
    public Map<String, StatsResult> getAllWithStats() {
        Pageable pageable = PageRequest.of(0, 1);
        StatsPage<SaleReportDocument> statsPage = repo.sumFundValueByHrCode(pageable);
        Map<String, FieldStatsResult> statsResult = statsPage.getFieldStatsResults();
        Map<String, StatsResult> statsResult1 = statsResult.get("fundValue").getFacetStatsResult("hrCode");
//        statsResult1.get("").getSum();
        return statsResult1;
//        return repository.findAllPage(pageable);
    }
}
