package vn.com.vndirect.report.pms.repo;

import org.springframework.data.solr.repository.SolrCrudRepository;
import vn.com.vndirect.report.pms.document.JobHistory;

import java.util.Optional;

public interface JobHistoryRepository extends SolrCrudRepository<JobHistory, String> {
    Optional<JobHistory> findByCreatedBetweenAndExecutionDateIsNotNullAndStartTimeIsNotNullAndFinishTimeIsNotNull(Long fromDate, Long toDate);
}
