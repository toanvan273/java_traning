package vn.com.vndirect.report.pms.service.factory;

import org.springframework.stereotype.Service;
import vn.com.vndirect.report.pms.model.Policy;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class MonthDateCalculationService extends AbstractCalculation {
    public MonthDateCalculationService(CalculationManagement calculationManagement) {
        super(calculationManagement);
    }

    public String registerCalculationCycle() {
        return "MONTH";
    }

    public String registerClosingTime() {
        return "DATE";
    }
    @Override
    public LocalDateTime makingFirstDate(LocalDateTime calculationDate, Policy policy) {
        Short closingDay = policy.getTerm().getClosingTimeValue();
        if (calculationDate.getDayOfMonth() <= closingDay) {
            return calculationDate
                    .withDayOfMonth(closingDay + 1)
                    .withMonth(calculationDate.getMonth().minus(1).getValue())
                    .withYear(calculationDate.getMonthValue() == 1 ? calculationDate.getYear() - 1 : calculationDate.getYear())
                    .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .toLocalDateTime();
        }
        return calculationDate
                .withDayOfMonth(closingDay + 1)
                .withMonth(calculationDate.getMonth().getValue())
                .withYear(calculationDate.getYear())
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime();

    }

    @Override
    public LocalDateTime makingLastDate(LocalDateTime calculationDate, Policy policy) {
        Short closingDay = policy.getTerm().getClosingTimeValue();

        if (calculationDate.getDayOfMonth() <= closingDay) {
            return  calculationDate
                    .withDayOfMonth(closingDay)
                    .withMonth(calculationDate.getMonth().getValue())
                    .withYear(calculationDate.getYear())
                    .with(LocalTime.MAX)
                    .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .toLocalDateTime();
        }
        return calculationDate
                .withDayOfMonth(closingDay)
                .withMonth(calculationDate.getMonth().plus(1).getValue())
                .withYear(calculationDate.getMonthValue() == 12 ? calculationDate.getYear() + 1 : calculationDate.getYear())
                .with(LocalTime.MAX)
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime();
    }
}
