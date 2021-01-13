package vn.com.vndirect.report.pms.service.factory;

import org.springframework.stereotype.Component;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.time.LocalDateTime;

@Component
public class MonthEndOfPeriodCalculationService extends AbstractCalculation {

    public MonthEndOfPeriodCalculationService(CalculationManagement calculationManagement) {
        super(calculationManagement);
    }

    public String registerCalculationCycle() {
        return "MONTH";
    }

    public String registerClosingTime() {
        return "END_OF_PERIOD";
    }

    @Override
    public LocalDateTime makingFirstDate(LocalDateTime calculationDate, Policy policy) {
        return DateUtils.firstTimeOfMonth(calculationDate);
    }

    @Override
    public LocalDateTime makingLastDate(LocalDateTime calculationDate, Policy policy) {
        return DateUtils.lastTimeOfMonth(calculationDate);
    }
}
