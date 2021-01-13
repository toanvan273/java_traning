package vn.com.vndirect.report.pms.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.solr.repository.Stats;
import org.springframework.stereotype.Repository;
import vn.com.vndirect.report.pms.document.SaleReportDocument;

import java.util.List;

public interface SaleReportRepository extends SolrCrudRepository<SaleReportDocument, String> {

    List<SaleReportDocument> findAll();

    @Query(value = "*:*")
    @Stats(facets = "hrCode", value = "fundValue")
    StatsPage<SaleReportDocument> sumFundValueByHrCode(Pageable pageable);
}
