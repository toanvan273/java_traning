package vn.com.vndirect.report.pms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.vndirect.report.pms.client.PmsPolicyServiceClient;
import vn.com.vndirect.report.pms.document.DailyDerivativeRevenue;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.model.Beneficiary;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.model.Pos;
import vn.com.vndirect.report.pms.model.Term;
import vn.com.vndirect.report.pms.service.CommissionCalculationService;
import vn.com.vndirect.report.pms.service.RevenueCalculationService;
import vn.com.vndirect.report.pms.service.factory.CalculationManagement;
import vn.com.vndirect.report.pms.service.factory.CalculationRevenueService;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static vn.com.vndirect.report.pms.utils.Constants.CERTIFICATE;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_PATTERN;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_TIME_PATTERN;
import static vn.com.vndirect.report.pms.utils.SolrUtils.formatSolrDate;

@RequestMapping("/internal")
@RestController
@RequiredArgsConstructor
public class CalculationAdminController {

    private final RevenueCalculationService revenueCalculationService;
    private final CommissionCalculationService commissionCalculationService;
    private final PmsPolicyServiceClient policyServiceClient;
    private final CalculationManagement calculationManagement;

    @GetMapping("/calculate-revenue")
    public void calculateRevenue(@RequestParam String dateString) throws ParseException {
        LocalDateTime calculationDate = DateUtils.stringToLocalDateTime(dateString, DATE_PATTERN);
        revenueCalculationService.calculatingRevenueReport(calculationDate);

        List<Policy> policyModels = policyServiceClient.getPolicy();

        for (Policy policyModel : policyModels) {
            if (calculationDate.isAfter(policyModel.getEffectedDate()) || calculationDate.isEqual(policyModel.getEffectedDate())) {
                Beneficiary beneficiary = policyModel.getBeneficiary();
                if (!beneficiary.getCertificate().getName().equals(CERTIFICATE)) {
                    continue;
                }
                Term term = policyModel.getTerm();
                LocalDateTime lastDateOfThisPeriod;
                LocalDateTime firstDateOfThisPeriod;
                if (calculationDate.isBefore(policyModel.getEffectedDate()) || calculationDate.isAfter(policyModel.getExpiredDate()) || calculationDate.isAfter(DateUtils.now())) {
                    continue;
                }
                CalculationRevenueService builder = calculationManagement.get(term.getCalculationCycle() + "-" + term.getClosingTime());
                firstDateOfThisPeriod = builder.makingFirstDate(calculationDate, policyModel);
                lastDateOfThisPeriod = builder.makingLastDate(calculationDate, policyModel);

                Period tempPeriod = new Period();
                if (firstDateOfThisPeriod != null && lastDateOfThisPeriod != null) {
                    tempPeriod = Period.builder()
                            .created(DateUtils.localDateToEpochMilli(DateUtils.now()))
                            .id(UUID.randomUUID().toString())
                            .fromDate(DateUtils.localDateToEpochMilli(firstDateOfThisPeriod))
                            .toDate(DateUtils.localDateToEpochMilli(lastDateOfThisPeriod))
                            .name(policyModel.getName())
                            .policyId(policyModel.getId())
                            .build();
                }


                //TODO STEP1: create Period list
                Long maxTime = tempPeriod.getToDate();
                if (tempPeriod.getToDate().compareTo(DateUtils.localDateToEpochMilli(DateUtils.now())) >= 0) {
                    maxTime = (DateUtils.localDateToEpochMilli(DateUtils.now()));
                }
                for (LocalDateTime start = DateUtils.toLocalDateTime(tempPeriod.getFromDate()); start.isBefore(DateUtils.toLocalDateTime(maxTime)); start = start.plusDays(1)) {
                    commissionCalculationService.autoChangeStatusPeriodToCalculateCommission(start);
                }

                //TODO STEP2: create Period Info list
            }
        }
        commissionCalculationService.calculatingCommissionReport();
    }

    @GetMapping("/change-status")
    public void changeStatusForCalculatingCommission() {
        commissionCalculationService.autoChangeStatusPeriodToCalculateCommission(DateUtils.now());
    }

    @GetMapping("/calculate-commission")
    public void calculateCommission() {
        commissionCalculationService.calculatingCommissionReport();
    }
}
