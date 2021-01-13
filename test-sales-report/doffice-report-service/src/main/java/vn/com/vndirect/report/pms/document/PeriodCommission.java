package vn.com.vndirect.report.pms.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.Transient;

@SolrDocument(collection = "period-commission")
@Getter
@Setter
@Builder
public class PeriodCommission {

    @Indexed
    private String id;
    @Indexed
    private String periodId;
    @Indexed
    private Long totalTradingFee;
    @Indexed
    private Long totalMatchQuantity;
    @Indexed
    private Long totalCommission;
    @Indexed
    private Long created;

    @Transient
    private String position;
    @Transient
    private String careByName;
    @Transient
    private String hrCode;
    @Transient
    private String departmentName;

    @Indexed
    private String createdText;
}
