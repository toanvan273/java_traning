package vn.com.vndirect.report.pms.repo;

import org.springframework.data.solr.repository.SolrCrudRepository;
import vn.com.vndirect.report.pms.document.DailyDerivativeRevenue;

public interface CustomerTransactionRepository extends SolrCrudRepository<DailyDerivativeRevenue, String> {
}
