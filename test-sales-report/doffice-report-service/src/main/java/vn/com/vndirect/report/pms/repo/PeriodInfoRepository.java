package vn.com.vndirect.report.pms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.solr.repository.Stats;
import vn.com.vndirect.report.pms.document.PeriodInfo;

import java.util.List;
import java.util.Optional;

public interface PeriodInfoRepository extends SolrCrudRepository<PeriodInfo, String> {

    Optional<PeriodInfo> findByPeriodIdAndCustomerAccountNumberAndFromDateLessThanEqualAndToDateGreaterThanEqual(String periodId, String acctno, Long fromDate, Long toDate);
    Optional<PeriodInfo> findByPeriodIdAndCustomerAccountNumberAndToDate(String periodId, String acctno, Long toDate);
    @Stats(value = {"totalTradingFee", "totalMatchQuantity"})
    StatsPage<PeriodInfo> findByPeriodId(String periodId, Pageable pageable);
    Page<PeriodInfo> findByPeriodIdInAndCustomerAccountNumberLikeAndTotalMatchQuantityNotAndTotalTradingFeeNot(List<String> periodIds, String customerAccountNumber, Long totalMatchQuantity, Long totalTradingFee, Pageable pageable);
    List<PeriodInfo> findByPeriodIdInAndCustomerAccountNumberLikeAndTotalMatchQuantityNotAndTotalTradingFeeNot(List<String> periodId, String customerAccountNumber, Long totalMatchQuantity, Long totalTradingFee);
}
