package vn.com.vndirect.report.pms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.StatsOptions;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.data.solr.core.query.result.StatsResult;
import org.springframework.stereotype.Component;
import vn.com.vndirect.report.pms.client.PmsPolicyServiceClient;
import vn.com.vndirect.report.pms.document.DailyDerivativeRevenue;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.model.Beneficiary;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.model.Pos;
import vn.com.vndirect.report.pms.model.Term;
import vn.com.vndirect.report.pms.repo.PeriodInfoRepository;
import vn.com.vndirect.report.pms.repo.PeriodRepository;
import vn.com.vndirect.report.pms.service.factory.CalculationManagement;
import vn.com.vndirect.report.pms.service.factory.CalculationRevenueService;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static vn.com.vndirect.report.pms.utils.Constants.ACCTNO;
import static vn.com.vndirect.report.pms.utils.Constants.CERTIFICATE;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_PATTERN;
import static vn.com.vndirect.report.pms.utils.Constants.DATE_TIME_PATTERN;
import static vn.com.vndirect.report.pms.utils.Constants.MATCH_QUANTITY;
import static vn.com.vndirect.report.pms.utils.Constants.REAL_TRADING_FEE;
import static vn.com.vndirect.report.pms.utils.SolrUtils.formatListStringQuerySolr;
import static vn.com.vndirect.report.pms.utils.SolrUtils.formatSolrDate;
import static vn.com.vndirect.report.pms.utils.SolrUtils.formatStringQuerySolr;

@Component
@Slf4j
public class RevenueCalculationService {

    private final PmsPolicyServiceClient policyServiceClient;
    private final CalculationManagement calculationManagement;
    private final PeriodRepository periodRepository;
    private final PeriodInfoRepository periodInfoRepository;
    private final SolrTemplate solrTemplate;

    public RevenueCalculationService(PmsPolicyServiceClient policyServiceClient, CalculationManagement calculationManagement, PeriodRepository periodRepository, PeriodInfoRepository periodInfoRepository, SolrTemplate solrTemplate) {
        this.policyServiceClient = policyServiceClient;
        this.calculationManagement = calculationManagement;
        this.periodRepository = periodRepository;
        this.periodInfoRepository = periodInfoRepository;
        this.solrTemplate = solrTemplate;
    }

    public void calculatingRevenueReport(LocalDateTime calculationDate) {
        log.info("\n\n======> START CALCULATING REVENUE REPORT");
        List<Policy> policyModels = policyServiceClient.getPolicy();

        if (calculationDate.isBefore(DateUtils.now()) || calculationDate.equals(DateUtils.now())) {
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
                        Page<GroupEntry<DailyDerivativeRevenue>> hrGroupEntries = getHrCode(beneficiary.getRole(), beneficiary.getTitle(), beneficiary.getJobDescription(),
                                beneficiary.getPos().stream().map(Pos::getPosCode).collect(Collectors.toList()), beneficiary.getCertificate().isRequired(),
                                policyModel.getProductCode(), formatSolrDate(start));

                        for (GroupEntry<DailyDerivativeRevenue> customerTransactionGroupEntry : hrGroupEntries.getContent()) {
                            DailyDerivativeRevenue customerTransaction = customerTransactionGroupEntry.getResult().getContent().get(0);

                            Page<GroupEntry<DailyDerivativeRevenue>> departmentIdGroupEntries = getDepartmentId(beneficiary.getRole(), beneficiary.getTitle(), beneficiary.getJobDescription(),
                                    customerTransaction.getHrCode(), beneficiary.getPos().stream().map(Pos::getPosCode).collect(Collectors.toList()), beneficiary.getCertificate().isRequired(),
                                    policyModel.getProductCode(), formatSolrDate(start));

                            for (GroupEntry<DailyDerivativeRevenue> departmentId : departmentIdGroupEntries) {
                                DailyDerivativeRevenue customerTransactionByDepartment = departmentId.getResult().getContent().get(0);

                                Optional<Period> opt = periodRepository.findByPolicyIdAndHrCodeAndDepartmentIdAndFromDateAndToDate(policyModel.getId(), customerTransactionByDepartment.getHrCode(),
                                        customerTransactionByDepartment.getDepartmentId(), tempPeriod.getFromDate(), tempPeriod.getToDate());
                                if (opt.isPresent()) {
                                    continue;
                                }
                                Period period = Period.builder()
                                        .id(UUID.randomUUID().toString())
                                        .created(DateUtils.localDateToEpochMilli(DateUtils.now()))
                                        .createdText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(DateUtils.now())).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                                        .fromDate(tempPeriod.getFromDate())
                                        .fromDateText(DateUtils.toLocalDateTime(tempPeriod.getFromDate()).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                                        .toDate(tempPeriod.getToDate())
                                        .toDateText(DateUtils.toLocalDateTime(tempPeriod.getToDate()).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                                        .name(policyModel.getName())
                                        .policyId(policyModel.getId())
                                        .hrCode(customerTransactionGroupEntry.getGroupValue())
                                        .careByName(customerTransactionByDepartment.getStaffFullname())
                                        .departmentId(customerTransactionByDepartment.getDepartmentId())
                                        .departmentName(customerTransactionByDepartment.getDepartmentName())
                                        .hasCert(beneficiary.getCertificate().isRequired())
                                        .jds(beneficiary.getJobDescription())
                                        .posCode(beneficiary.getPos().stream().map(Pos::getPosCode).collect(Collectors.toList()))
                                        .posName(customerTransaction.getPosName())
                                        .position(beneficiary.getTitle())
                                        .productCode(policyModel.getProductCode())
                                        .build();

                                periodRepository.save(period);
                            }
                        }
                    }

                    //TODO STEP2: create Period Info list
                    List<Period> calculationPeriods = periodRepository.findByPolicyIdAndFromDateAndToDate(policyModel.getId(), tempPeriod.getFromDate(), tempPeriod.getToDate());
                    for (Period calculationPeriod : calculationPeriods) {
                        try {
                            makePeriodInfo(calculationDate, tempPeriod, policyModel, beneficiary, calculationPeriod);
                        } catch (ParseException e) {
                            return;
                        }
                    }
                }
            }

        }
        log.info("\n\n======> FINISH CALCULATING REVENUE REPORT");
    }

    public Page<GroupEntry<DailyDerivativeRevenue>> getHrCode(String role, String position, String jds, List<String> posCode,
                                                              boolean hasCert, List<String> productCodes,
                                                              String date) {

        SimpleQuery query = new SimpleQuery(new SimpleStringCriteria(String.join(" AND ",
                formatStringQuerySolr("role", role),
                formatStringQuerySolr("position", position),
                formatStringQuerySolr("jds", jds),
                formatListStringQuerySolr("posCode", "or", posCode),
                formatListStringQuerySolr("product", "or", productCodes),
                formatStringQuerySolr("date", date),
                formatStringQuerySolr("hasCertificate", String.valueOf(hasCert)))));

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("hrCode");
        groupOptions.setLimit(1000000);
        groupOptions.setOffset(BigDecimal.ZERO.intValue());
        query.setGroupOptions(groupOptions);
        query.setRows(1000000);

        GroupPage<DailyDerivativeRevenue> customerTransactions = solrTemplate.queryForGroupPage(DailyDerivativeRevenue.class.getAnnotation(SolrDocument.class).collection(),
                query, DailyDerivativeRevenue.class);
        return customerTransactions.getGroupResult("hrCode").getGroupEntries();
    }

    public Page<GroupEntry<DailyDerivativeRevenue>> getDepartmentId(String role, String position, String jds, String hrCode,
                                                                    List<String> posCode, boolean hasCert, List<String> productCodes,
                                                                    String date) {

        SimpleQuery query = new SimpleQuery(new SimpleStringCriteria(String.join(" AND ",
                formatStringQuerySolr("role", role),
                formatStringQuerySolr("position", position),
                formatStringQuerySolr("jds", jds),
                formatStringQuerySolr("hrCode", hrCode),
                formatListStringQuerySolr("posCode", "or", posCode),
                formatListStringQuerySolr("product", "or", productCodes),
                formatStringQuerySolr("date", date),
                formatStringQuerySolr("hasCertificate", String.valueOf(hasCert)))));

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("departmentId");
        groupOptions.setLimit(1000000);
        groupOptions.setOffset(BigDecimal.ZERO.intValue());
        query.setGroupOptions(groupOptions);
        query.setRows(1000000);

        GroupPage<DailyDerivativeRevenue> customerTransactions = solrTemplate.queryForGroupPage(DailyDerivativeRevenue.class.getAnnotation(SolrDocument.class).collection(),
                query, DailyDerivativeRevenue.class);
        return customerTransactions.getGroupResult("departmentId").getGroupEntries();
    }

    public StatsPage<DailyDerivativeRevenue> getAcctnoByHrCode(String role, String position, String jds, List<String> posCode,
                                                               boolean hasCert, List<String> productCodes, String date,
                                                               String hrCode, String departmentId, Pageable pageable) {

        SimpleQuery query = new SimpleQuery(new SimpleStringCriteria(String.join(" AND ",
                formatStringQuerySolr("role", role),
                formatStringQuerySolr("position", position),
                formatStringQuerySolr("hrCode", hrCode),
                formatStringQuerySolr("jds", jds),
                formatStringQuerySolr("departmentId", departmentId),
                formatListStringQuerySolr("posCode", "or", posCode),
                formatListStringQuerySolr("product", "or", productCodes),
                formatStringQuerySolr("date", date),
                formatStringQuerySolr("hasCertificate", String.valueOf(hasCert)))));

        StatsOptions statsOptions = new StatsOptions();
        statsOptions.addFacet(ACCTNO).addField(MATCH_QUANTITY);
        statsOptions.addFacet(ACCTNO).addField(REAL_TRADING_FEE);
        query.setStatsOptions(statsOptions);
        query.setPageRequest(pageable);

        return solrTemplate.queryForStatsPage(DailyDerivativeRevenue.class.getAnnotation(SolrDocument.class).collection(), query, DailyDerivativeRevenue.class);
    }

    public StatsPage<DailyDerivativeRevenue> getStats(String role, String position, String jds, List<String> posCode,
                                                      boolean hasCert, List<String> productCodes, String date, String hrCode,
                                                      String acctNo, String departmentId, Pageable pageable) {

        SimpleQuery query = new SimpleQuery(new SimpleStringCriteria(String.join(" AND ",
                formatStringQuerySolr("role", role),
                formatStringQuerySolr("position", position),
                formatStringQuerySolr("hrCode", hrCode),
                formatStringQuerySolr("jds", jds),
                formatStringQuerySolr("departmentId", departmentId),
                formatListStringQuerySolr("posCode", "or", posCode),
                formatListStringQuerySolr("product", "or", productCodes),
                formatStringQuerySolr(ACCTNO, acctNo),
                formatStringQuerySolr("date", date),
                formatStringQuerySolr("hasCertificate", String.valueOf(hasCert)))));

        StatsOptions statsOptions = new StatsOptions();
        statsOptions.addField(MATCH_QUANTITY);
        statsOptions.addField(REAL_TRADING_FEE);
        query.setStatsOptions(statsOptions);
        query.setPageRequest(pageable);

        return solrTemplate.queryForStatsPage(DailyDerivativeRevenue.class.getAnnotation(SolrDocument.class).collection(), query, DailyDerivativeRevenue.class);
    }

    private void makePeriodInfo(LocalDateTime calculationDate, Period tempPeriod, Policy policyModel, Beneficiary beneficiary, Period calculationPeriod) throws ParseException {
        for (LocalDateTime start = calculationDate; start.isBefore(DateUtils.toLocalDateTime(tempPeriod.getToDate())); start = start.plusDays(1)) {
            StatsPage<DailyDerivativeRevenue> acctnoByHrCode = getAcctnoByHrCode(beneficiary.getRole(), beneficiary.getTitle(), beneficiary.getJobDescription(),
                    beneficiary.getPos().stream().map(Pos::getPosCode).collect(Collectors.toList()), beneficiary.getCertificate().isRequired(),
                    policyModel.getProductCode(), formatSolrDate(start), calculationPeriod.getHrCode(), calculationPeriod.getDepartmentId(), PageRequest.of(0, 100));
            Map<String, StatsResult> matchQuantityByAcctno = acctnoByHrCode.getFieldStatsResult(MATCH_QUANTITY).getFacetStatsResult(ACCTNO);

            for (String acctNo : matchQuantityByAcctno.keySet()) {
                Optional<PeriodInfo> periodInfoOptional = periodInfoRepository
                        .findByPeriodIdAndCustomerAccountNumberAndFromDateLessThanEqualAndToDateGreaterThanEqual(calculationPeriod.getId(),
                                acctNo, DateUtils.localDateToEpochMilli(start), DateUtils.localDateToEpochMilli(start));
                if (periodInfoOptional.isPresent()) {
                    continue;
                }


                StatsPage<DailyDerivativeRevenue> statsPage = getStats(beneficiary.getRole(), beneficiary.getTitle(), beneficiary.getJobDescription(),
                        beneficiary.getPos().stream().map(Pos::getPosCode).collect(Collectors.toList()), beneficiary.getCertificate().isRequired(),
                        policyModel.getProductCode(), formatSolrDate(start), calculationPeriod.getHrCode(), acctNo, calculationPeriod.getDepartmentId(), PageRequest.of(0, 100));
                Long totalMatchQuantity = new BigDecimal(String.valueOf(statsPage.getFieldStatsResult(MATCH_QUANTITY).getSum())).longValue();
                Long totalTradingFee = new BigDecimal(String.valueOf(statsPage.getFieldStatsResult(REAL_TRADING_FEE).getSum())).longValue();

                if (statsPage.getContent().isEmpty()) {
                    continue;
                }

                DailyDerivativeRevenue customerTransaction = statsPage.getContent().get(0);
                periodInfoOptional = periodInfoRepository.findByPeriodIdAndCustomerAccountNumberAndToDate(calculationPeriod.getId(),
                        acctNo, DateUtils.localDateToEpochMilli(start.minusDays(1)));
                if (!periodInfoOptional.isPresent()) {
                    PeriodInfo periodInfo = PeriodInfo
                            .builder()
                            .periodId(calculationPeriod.getId())
                            .created(DateUtils.localDateToEpochMilli(DateUtils.now()))
                            .createdText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(DateUtils.now())).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                            .customerAccountNumber(acctNo)
                            .customerId(customerTransaction.getCustId())
                            .customerName(customerTransaction.getCustomerFullname())
                            .totalMatchQuantity(totalMatchQuantity)
                            .totalTradingFee(totalTradingFee)
                            //TODO need to improve
                            .id(UUID.randomUUID().toString())
                            .fromDate(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN))
                            .fromDateText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN)).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                            .toDate(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN))
                            .toDateText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN)).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                            .build();
                    periodInfoRepository.save(periodInfo);
                    continue;
                }
                PeriodInfo periodInfo = periodInfoOptional.get();
                periodInfo.setToDate(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN));
                periodInfo.setToDateText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(customerTransaction.getDate(), DATE_PATTERN)).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
                periodInfo.setTotalMatchQuantity(periodInfo.getTotalMatchQuantity() + customerTransaction.getMatchQuantity());
                periodInfo.setTotalTradingFee(periodInfo.getTotalTradingFee() + customerTransaction.getRealTradingFee());
                periodInfoRepository.save(periodInfo);
            }

        }
    }
}
