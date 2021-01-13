package vn.com.vndirect.report.pms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.com.vndirect.report.pms.document.JobHistory;
import vn.com.vndirect.report.pms.model.CalculationModel.CalculationEvent;
import vn.com.vndirect.report.pms.repo.JobHistoryRepository;
import vn.com.vndirect.report.pms.utils.Constants.JOB_CODE;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static vn.com.vndirect.report.pms.utils.Constants.DATE_PATTERN;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_TIME_PATTERN;

@Slf4j
@Service
public class CalculationService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final RevenueCalculationService revenueCalculationService;
    private final CommissionCalculationService commissionCalculationService;
    private final JobHistoryRepository jobHistoryRepository;

    public CalculationService(RevenueCalculationService revenueCalculationService, CommissionCalculationService commissionCalculationService,
                              ApplicationEventPublisher applicationEventPublisher, JobHistoryRepository jobHistoryRepository) {
        this.revenueCalculationService = revenueCalculationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.commissionCalculationService = commissionCalculationService;
        this.jobHistoryRepository = jobHistoryRepository;
    }

    @EventListener(condition = "#event.hasImportedData")
    @Async
    public void revenueCalculation(CalculationEvent event) {

        //TODO làm job care đến case đổi chính sách giữa kỳ
        LocalDateTime now = DateUtils.now();
        LocalDateTime calculationDate = event.getCalculationDate();

        JobHistory jobHistory = JobHistory
                .builder()
                .id(UUID.randomUUID().toString())
                .created(DateUtils.localDateToEpochMilli(now))
                .createdText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .executionDate(DateUtils.localDateToEpochMilli(now))
                .executionDateText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .jobCode(JOB_CODE.CALCULATING_REVENUE.name())
                .jobName(JOB_CODE.convertName(JOB_CODE.CALCULATING_REVENUE) + now.format(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .startTime(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
        jobHistoryRepository.save(jobHistory);
        //TODO notify MS Team channel
        revenueCalculationService.calculatingRevenueReport(calculationDate);

        jobHistory = jobHistory.toBuilder()
                .finishTime(DateUtils.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))).build();
        jobHistoryRepository.save(jobHistory);

        event.setHasCalculatedRevenue(true);
        event.setHasImportedData(false);
        applicationEventPublisher.publishEvent(event);
    }

    //TODO job change status period
    @EventListener(condition = "#event.hasCalculatedRevenue")
    @Async
    public void changeStatusPeriod(CalculationEvent event) throws ParseException {

        LocalDateTime now = DateUtils.now();
        JobHistory jobHistory = JobHistory
                .builder()
                .id(UUID.randomUUID().toString())
                .created(DateUtils.localDateToEpochMilli(now))
                .createdText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .executionDate(DateUtils.localDateToEpochMilli(now))
                .executionDateText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .jobCode(JOB_CODE.CHANGING_STATUS_TO_CALCULATE_COMMISSION.name())
                .jobName(JOB_CODE.convertName(JOB_CODE.CHANGING_STATUS_TO_CALCULATE_COMMISSION) + now.format(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .startTime(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
        jobHistoryRepository.save(jobHistory);
        LocalDateTime calculationDate = event.getCalculationDate();
        commissionCalculationService.autoChangeStatusPeriodToCalculateCommission(calculationDate);

        jobHistory = jobHistory.toBuilder()
                .finishTime(DateUtils.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))).build();
        jobHistoryRepository.save(jobHistory);

        event.setHasChangeStatusPeriod(true);
        event.setHasCalculatedRevenue(false);
        applicationEventPublisher.publishEvent(event);

    }

    //TODO job calculate commision
    @EventListener(condition = "#event.hasChangeStatusPeriod")
    @Async
    public void calculationCommissionReport(CalculationEvent event) {
        LocalDateTime now = DateUtils.now();
        JobHistory jobHistory = JobHistory
                .builder()
                .id(UUID.randomUUID().toString())
                .created(DateUtils.localDateToEpochMilli(now))
                .createdText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .executionDate(DateUtils.localDateToEpochMilli(now))
                .executionDateText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .jobCode(JOB_CODE.CHANGING_STATUS_TO_CALCULATE_COMMISSION.name())
                .jobName(JOB_CODE.convertName(JOB_CODE.CHANGING_STATUS_TO_CALCULATE_COMMISSION) + now.format(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .startTime(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .build();
        jobHistoryRepository.save(jobHistory);
        commissionCalculationService.calculatingCommissionReport();

        jobHistory = jobHistory.toBuilder()
                .finishTime(DateUtils.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))).build();
        jobHistoryRepository.save(jobHistory);
    }
}
