package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Term {
    private Long id;
    private Long policyId;
    private String calculationCycle;
    private Long calculationCycleValue;
    private String closingTime;
    private Short closingTimeValue;
}
