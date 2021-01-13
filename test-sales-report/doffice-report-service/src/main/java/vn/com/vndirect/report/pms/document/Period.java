package vn.com.vndirect.report.pms.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@SolrDocument(collection = "period")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Period {
    @Id
    private String id;
    @Indexed
    private String name;
    @Indexed
    private Long policyId;

    @Indexed
    private String careByName;
    @Indexed
    private String hrCode;
    @Indexed
    private String departmentId;
    @Indexed
    private String departmentName;

    @Indexed
    private boolean commissionCalculated;

    @Indexed
    private List<String> posCode;
    @Indexed
    private String posName;

    @Indexed
    private String position;
    @Indexed
    private String jds;
    @Indexed
    private boolean hasCert;
    @Indexed
    private List<String> productCode;

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
