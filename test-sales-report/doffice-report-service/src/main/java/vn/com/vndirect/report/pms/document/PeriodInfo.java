package vn.com.vndirect.report.pms.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.Transient;

@Getter @Setter
@Builder
@SolrDocument(collection = "period-info")
@AllArgsConstructor @NoArgsConstructor
public class PeriodInfo {

    @Id
    private String id;

    @Indexed
    private String periodId;
    @Indexed
    private String customerName;
    @Indexed
    private String customerId;
    @Indexed
    private String customerAccountNumber;

    @Indexed
    private Long totalTradingFee;
    @Indexed
    private Long totalMatchQuantity;

    @Transient
    private String typeCalculate;
    @Transient
    private String careByName;
    @Transient
    private String hrCode;
    @Transient
    private String departmentName;

    @Indexed
    private Long fromDate;
    @Indexed
    private Long toDate;
    @Indexed
    private Long created;
    @Indexed
    private String fromDateText;
    @Indexed
    private String toDateText;
    @Indexed
    private String createdText;
}
