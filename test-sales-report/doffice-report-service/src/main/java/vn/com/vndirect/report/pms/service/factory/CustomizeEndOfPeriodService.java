package vn.com.vndirect.report.pms.service.factory;

import org.springframework.stereotype.Service;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.repo.PeriodRepository;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomizeEndOfPeriodService extends AbstractCalculation {

    private final PeriodRepository periodRepository;

    public CustomizeEndOfPeriodService(PeriodRepository periodRepository, CalculationManagement calculationManagement) {
        super(calculationManagement);
        this.periodRepository = periodRepository;
    }

    public String registerCalculationCycle() {
        return "CUSTOMIZE";
    }

    public String registerClosingTime() {
        return "END_OF_PERIOD";
    }
    @Override
    public LocalDateTime makingFirstDate(LocalDateTime calculationDate, Policy policy) {
        List<Period> periods = periodRepository.findByPolicyIdAndFromDateGreaterThanEqualAndToDateLessThanEqual(policy.getId(),
                DateUtils.localDateToEpochMilli(calculationDate), DateUtils.localDateToEpochMilli(calculationDate));
        Long dayPerPeriod = policy.getTerm().getCalculationCycleValue();
        if (periods.isEmpty()) {
            LocalDateTime start = policy.getEffectedDate();
            LocalDateTime result = DateUtils.now();
            while (start.isBefore(calculationDate) || start.equals(calculationDate)) {
                result = start;
                start = start.plusDays(dayPerPeriod);
            }
            return result;
        }
        Period latestPeriod = periods.get(0);
        return DateUtils.toLocalDateTime(latestPeriod.getFromDate());
    }

    @Override
    public LocalDateTime makingLastDate(LocalDateTime calculationDate, Policy term) {
        return null;
    }
}
