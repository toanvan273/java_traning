package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rank {
    private Long id;
    private Long policyId;
    private String concernedValue;
    private String type, typeName;
    private BigDecimal from, to, rate, fixed;
}
