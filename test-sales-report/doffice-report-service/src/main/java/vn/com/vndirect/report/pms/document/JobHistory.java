package vn.com.vndirect.report.pms.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "job-history")
@Getter @Setter
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
public class JobHistory {

    @Id
    @Indexed
    private String id;
    @Indexed
    private String jobCode;
    @Indexed
    private String jobName;
    @Indexed
    private Long executionDate;
    @Indexed
    private String executionDateText;
    @Indexed
    private String startTime;
    @Indexed
    private String finishTime;
    @Indexed
    private Long created;
    @Indexed
    private String createdText;
}
