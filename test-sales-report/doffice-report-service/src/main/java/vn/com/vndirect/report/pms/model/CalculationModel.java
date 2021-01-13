package vn.com.vndirect.report.pms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CalculationModel {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SolrImportStatus {
        private String status;
    }
    @AllArgsConstructor @NoArgsConstructor @Getter @Setter
    public static class CalculationEvent {
        private boolean hasImportedData;
        private boolean hasCalculatedRevenue;
        private boolean hasChangeStatusPeriod;
        private LocalDateTime calculationDate;
    }
}
