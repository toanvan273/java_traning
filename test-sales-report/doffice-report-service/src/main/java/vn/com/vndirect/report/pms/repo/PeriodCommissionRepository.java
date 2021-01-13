package vn.com.vndirect.report.pms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;
import vn.com.vndirect.report.pms.document.PeriodCommission;

import java.util.List;

public interface PeriodCommissionRepository extends SolrCrudRepository<PeriodCommission, String> {

    Page<PeriodCommission> findByPeriodIdInAndTotalMatchQuantityNotAndTotalTradingFeeNotAndTotalCommissionNot(List<String> periodIds, Pageable pageable, Long totalMatchQuantity, Long totalTradingFee, Long totalCommission);

    List<PeriodCommission> findByPeriodIdInAndTotalMatchQuantityNotAndTotalTradingFeeNotAndTotalCommissionNot(List<String> periodIds, Long totalMatchQuantity, Long totalTradingFee, Long totalCommission);
}
