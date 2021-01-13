package vn.com.vndirect.report.pms.controller;

import lombok.RequiredArgsConstructor;
import org.apache.zookeeper.Op;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import vn.com.vndirect.report.pms.document.JobHistory;
import vn.com.vndirect.report.pms.model.CalculationModel.CalculationEvent;
import vn.com.vndirect.report.pms.model.CalculationModel.SolrImportStatus;
import vn.com.vndirect.report.pms.repo.JobHistoryRepository;
import vn.com.vndirect.report.pms.utils.Constants.JOB_CODE;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static vn.com.vndirect.report.pms.utils.Constants.DATE_PATTERN;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_TIME_PATTERN;

@RestController
@RequiredArgsConstructor
public class DailyAggregationTransactionController {

    private static final Optional<String> BUSY = Optional.of("busy");
    private static final Optional<String> IDLE = Optional.of("idle");
    private final JobHistoryRepository jobHistoryRepository;

    @Value("${solr.hosts}")
    private String solrUrl;
    private final RestTemplate restTemplate;

    private final ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/internal/daily-aggregation/complete-import")
    public void completeImport() throws InterruptedException {
        importingData();
    }

    private Optional<String> getStatusImportData() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SolrImportStatus> entity = restTemplate.getForEntity(solrUrl + "/daily-derivative-revenue/data-import-current-date", SolrImportStatus.class);
        SolrImportStatus solrImportStatus = entity.getBody();
        if (solrImportStatus != null) {
            return Optional.of(solrImportStatus.getStatus());
        }
        return Optional.empty();
    }
    
    private synchronized void importingData() throws InterruptedException {
        LocalDateTime now = DateUtils.now();
        Optional<JobHistory> optJobHistory = jobHistoryRepository.findByCreatedBetweenAndExecutionDateIsNotNullAndStartTimeIsNotNullAndFinishTimeIsNotNull(
                DateUtils.localDateToEpochMilli(DateUtils.localDateTimeAtStartOfDay(now)), DateUtils.localDateToEpochMilli(DateUtils.localDateTimeAtEndOfDay(now))
        );
        if (optJobHistory.isPresent()) {
            return;
        }
        JobHistory jobHistory = JobHistory
                .builder()
                .id(UUID.randomUUID().toString())
                .created(DateUtils.localDateToEpochMilli(now))
                .createdText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .executionDate(DateUtils.localDateToEpochMilli(now))
                .executionDateText(now.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .jobCode(JOB_CODE.IMPORTING_DATA.name())
                .jobName(JOB_CODE.convertName(JOB_CODE.IMPORTING_DATA) + now.format(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .build();
        jobHistoryRepository.save(jobHistory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("command", "full-import");
        map.add("verbose", "false");
        map.add("clean", "false");
        map.add("commit", "true");
        map.add("core", "daily-derivative-revenue");
        map.add("name", "data-import-current-date");

        HttpEntity<MultiValueMap<String, String>> formData = new HttpEntity<>(map, headers);
        jobHistory = jobHistory.toBuilder().startTime(DateUtils.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))).build();
        jobHistoryRepository.save(jobHistory);
        restTemplate.exchange(solrUrl + "/daily-derivative-revenue/data-import-current-date", HttpMethod.POST, formData, Object.class);
        do {
            wait(1000);
        } while (!getStatusImportData().isPresent() || getStatusImportData().equals(BUSY));
        if (getStatusImportData().equals(IDLE)) {
            jobHistory = jobHistory.toBuilder().finishTime(DateUtils.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))).build();
            jobHistoryRepository.save(jobHistory);
            applicationEventPublisher.publishEvent(new CalculationEvent(true, false, false, DateUtils.now().minusDays(1)));
        }
    }
}
