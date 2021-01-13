package vn.com.vndirect.report.pms.service.factory;

import org.springframework.stereotype.Service;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.time.LocalDateTime;

@Service
public class CommonCalculationTempPeriod extends AbstractCalculation {

    public CommonCalculationTempPeriod(CalculationManagement calculationManagement) {
        super(calculationManagement);
    }

    public String registerCalculationCycle() {
        return "";
    }

    public String registerClosingTime() {
        return "";
    }

    @Override
    public LocalDateTime makingFirstDate(LocalDateTime calculationDate, Policy policy) {
        return DateUtils.now();
    }

    @Override
    public LocalDateTime makingLastDate(LocalDateTime calculationDate, Policy policy) {
        return DateUtils.now();
    }
}
