package vn.com.vndirect.report.pms.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import lombok.Getter;
import lombok.Setter;

@SolrDocument(collection = "daily-derivative-revenue")
@Getter @Setter
public class DailyDerivativeRevenue {

    @Indexed
    @Id
    private String id;
    @Indexed
    private String acctNo;
    @Indexed
    private String hrCode;
    @Indexed
    private String staffFullname;
    @Indexed
    private String customerFullname;
    @Indexed
    private String date;
    @Indexed
    private String departmentId;
    @Indexed
    private String departmentName;
    @Indexed
    private String role;
    @Indexed
    private String jds;
    @Indexed
    private String position;
    @Indexed
    private String posCode;
    @Indexed
    private String posName;
    @Indexed
    private boolean hasCertificate;
    @Indexed
    private String custId;
    @Indexed
    private String product;
    @Indexed
    private Long matchQuantity;
    @Indexed
    private Long realTradingFee;
}
