package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    private Long id;
    private Long policyId;
    private Factor factorModel;
    private Operator operatorModel;
    private String value;
}
