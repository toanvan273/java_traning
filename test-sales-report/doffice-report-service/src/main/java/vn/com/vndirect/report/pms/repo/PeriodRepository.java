package vn.com.vndirect.report.pms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;
import vn.com.vndirect.report.pms.document.Period;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PeriodRepository extends SolrCrudRepository<Period, String> {

    List<Period> findByPolicyIdOrderByCreated(Long policy);
    Page<Period> findByPolicyIdOrderByToDate(Long policy, Pageable pageable);

    List<Period> findByPolicyIdAndFromDateAndToDate(Long policy, Long fromDate, Long toDate);
    List<Period> findByPolicyIdAndToDate(Long policy, Long toDate);
    List<Period> findByPolicyIdAndCommissionCalculatedTrue(Long policyId);
    Optional<Period> findByPolicyIdAndHrCodeAndDepartmentIdAndFromDateAndToDate(Long policyId, String hrCode, String deparmentId, Long fromDate, Long toDate);
    List<Period> findByPolicyIdAndFromDateGreaterThanEqualAndToDateLessThanEqual(Long policyId, Long fromDate, Long toDate);
    Optional<Period> findByFromDateGreaterThanEqualAndToDateLessThanEqual(Date fromDate, Date toDate);

    List<Period> findByHrCodeAndFromDateGreaterThanEqualAndToDateLessThanEqual(String hrCode, Long fromDate, Long toDate);
    List<Period> findByFromDateGreaterThanEqualAndToDateLessThanEqual(Long fromDate, Long toDate);
}
