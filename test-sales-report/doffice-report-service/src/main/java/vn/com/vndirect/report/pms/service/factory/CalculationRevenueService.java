package vn.com.vndirect.report.pms.service.factory;

import vn.com.vndirect.report.pms.model.Policy;

import java.time.LocalDateTime;

public interface CalculationRevenueService {

    LocalDateTime makingFirstDate(LocalDateTime calculationDate, Policy policy);
    LocalDateTime makingLastDate(LocalDateTime calculationDate, Policy policy);
}
