package vn.com.vndirect.report.pms.document;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;

@SolrDocument(collection = "daily-sale-report")
@Getter
@Setter
public class SaleReportDocument {
    @Field
    @Id
    private String id;

    @Field
    private Date date;

    @Field
    private String hrCode;

    @Field
    private Double fundValue;
}
