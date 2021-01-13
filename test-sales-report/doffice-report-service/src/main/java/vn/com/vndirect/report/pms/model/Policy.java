package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    private Long id;
    private String code, name, description;
    private Beneficiary beneficiary;
    private Long businessId;
    private String beneficialValue;
    private LocalDateTime effectedDate, expiredDate, createdOn;
    private String createdBy, approvedBy;
    private String status;
    private String typeName, statusName, beneficialValueName;
    private List<String> productCode;
    private Term term;
    private Condition condition;
    private String rankType;
    private String rankConcernedValue;
    private List<Rank> rank = new ArrayList<>();
}
