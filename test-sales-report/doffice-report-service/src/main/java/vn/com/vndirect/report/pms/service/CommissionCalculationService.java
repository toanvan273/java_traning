package vn.com.vndirect.report.pms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.StatsPage;
import org.springframework.stereotype.Service;
import vn.com.vndirect.report.pms.client.PmsPolicyServiceClient;
import vn.com.vndirect.report.pms.document.Period;
import vn.com.vndirect.report.pms.document.PeriodCommission;
import vn.com.vndirect.report.pms.document.PeriodInfo;
import vn.com.vndirect.report.pms.model.Condition;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.model.Rank;
import vn.com.vndirect.report.pms.repo.PeriodInfoRepository;
import vn.com.vndirect.report.pms.repo.PeriodRepository;
import vn.com.vndirect.report.pms.repo.PeriodCommissionRepository;
import vn.com.vndirect.report.pms.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vn.com.vndirect.report.pms.utils.Constants.DATE_TIME_PATTERN;

@Service
@Slf4j
public class CommissionCalculationService {

    private final PmsPolicyServiceClient policyServiceClient;
    private final PeriodRepository periodRepository;
    private final PeriodInfoRepository periodInfoRepository;
    private final PeriodCommissionRepository periodCommissionRepository;

    public CommissionCalculationService(PmsPolicyServiceClient policyServiceClient, PeriodRepository periodRepository, PeriodInfoRepository periodInfoRepository, PeriodCommissionRepository periodCommissionRepository) {
        this.policyServiceClient = policyServiceClient;
        this.periodRepository = periodRepository;
        this.periodInfoRepository = periodInfoRepository;
        this.periodCommissionRepository = periodCommissionRepository;
    }

    public void autoChangeStatusPeriodToCalculateCommission(LocalDateTime calculationDate) {
        log.info("\n\n======> START CHANGE STATUS");

        List<Policy> policyModels = policyServiceClient.getPolicy();

        for (Policy policyModel : policyModels) {
            List<Period> periods = periodRepository.findByPolicyIdAndToDate(policyModel.getId(), DateUtils.localDateToEpochMilli(DateUtils.localDateTimeAtEndOfDay(calculationDate)));
            if (periods.isEmpty()) {
                continue;
            }
            for (Period calculationPeriod : periods) {
                calculationPeriod.setCommissionCalculated(true);
            }
            periodRepository.saveAll(periods);
        }
        log.info("\n\n======> FINISH CHANGE STATUS");
    }

    public void calculatingCommissionReport() {
        log.info("\n\n======> START CALCULATION COMMISSION");

        List<Policy> policyModels = policyServiceClient.getPolicy();
        for (Policy policyModel : policyModels) {
            List<Period> periods = periodRepository.findByPolicyIdAndCommissionCalculatedTrue(policyModel.getId());
            if (periods.isEmpty()) {
                continue;
            }
            for (Period calculationPeriod : periods) {
                Pageable pageable = PageRequest.of(0, 1);
                StatsPage<PeriodInfo> stats = periodInfoRepository.findByPeriodId(calculationPeriod.getId(), pageable);
                BigDecimal totalMatchQuantity = new BigDecimal(String.valueOf(stats.getFieldStatsResults().get("totalMatchQuantity").getSum()));
                BigDecimal totalTradingFee = new BigDecimal(String.valueOf(stats.getFieldStatsResults().get("totalTradingFee").getSum()));

                switch (policyModel.getRankType()) {
                    case "CLUSTER":
                        Optional<Rank> optRank = Optional.empty();
                        if (policyModel.getRankConcernedValue().equals("CONTRACTS_QUANTITY")) {
                            optRank = policyModel.getRank().stream().filter(rank -> rank.getFrom().compareTo(totalMatchQuantity) <= 0
                                    && (rank.getFrom().compareTo(totalMatchQuantity) >= 0 || rank.getTo() == null)).findAny();
                        }

                        if (policyModel.getRankConcernedValue().equals("FEE_TURNOVER")) {
                            optRank = policyModel.getRank().stream().filter(rank -> rank.getFrom().compareTo(totalTradingFee) <= 0
                                    && (rank.getFrom().compareTo(totalTradingFee) >= 0 || rank.getTo() == null)).findAny();
                        }

                        if (optRank.isPresent()) {
                            Rank rank = optRank.get();
                            BigDecimal revenue = BigDecimal.ZERO;
                            if (rank.getFixed() != null) {
                                if (policyModel.getBeneficialValue().equals("CONTRACTS_QUANTITY")) {
                                    revenue = rank.getFixed().multiply(totalMatchQuantity);
                                }

                                if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                                    revenue = rank.getFixed().multiply(totalTradingFee);
                                }
                            }
                            if (rank.getRate() != null) {
                                if (policyModel.getBeneficialValue().equals("CONTRACTS_QUANTITY")) {
                                    revenue = rank.getRate().multiply(totalMatchQuantity);
                                }

                                if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                                    revenue = rank.getRate().multiply(totalTradingFee);
                                }
                            }
                            PeriodCommission periodRevenue = PeriodCommission
                                    .builder()
                                    .id(UUID.randomUUID().toString())
                                    .periodId(calculationPeriod.getId())
                                    .totalTradingFee(totalTradingFee.longValue())
                                    .totalMatchQuantity(totalMatchQuantity.longValue())
                                    .totalCommission(revenue.longValue())
                                    .created(DateUtils.localDateToEpochMilli(DateUtils.now()))
                                    .createdText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(DateUtils.now())).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                                    .build();


                            periodCommissionRepository.save(periodRevenue);
                        }
                        break;
                    case "TIER":
                        List<Rank> ranks = policyModel.getRank();
                        BigDecimal revenue = BigDecimal.ZERO;
                        if (ranks.isEmpty()) break;
                        if (policyModel.getRankConcernedValue().equals("CONTRACTS_QUANTITY")) {
                            revenue = makingTierRevenue(policyModel, totalMatchQuantity, ranks);
                        }

                        if (policyModel.getRankConcernedValue().equals("FEE_TURNOVER")) {
                            revenue = makingTierRevenue(policyModel, totalTradingFee, ranks);
                        }

                        PeriodCommission periodRevenue = PeriodCommission
                                .builder()
                                .id(UUID.randomUUID().toString())
                                .periodId(calculationPeriod.getId())
                                .totalTradingFee(totalTradingFee.longValue())
                                .totalMatchQuantity(totalMatchQuantity.longValue())
                                .totalCommission(revenue.longValue())
                                .created(DateUtils.localDateToEpochMilli(DateUtils.now()))
                                .createdText(DateUtils.toLocalDateTime(DateUtils.localDateToEpochMilli(DateUtils.now())).format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                                .build();


                        periodCommissionRepository.save(periodRevenue);
                        break;
                    default:
                        break;
                }
            }
        }
        log.info("\n\n======> FINISH CALCULATION COMMISSION");
    }

    private void processCondition(Policy policyModel) {
        Condition condition = policyModel.getCondition();
        List<Rank> ranks = policyModel.getRank();
        String factor = condition.getFactorModel().getCode();
        String operator = condition.getOperatorModel().getCode();
        String valueType = condition.getFactorModel().getDataType();
        String value = condition.getValue();

    }

    private BigDecimal makingTierRevenue(Policy policyModel, BigDecimal totalCalculatedValue, List<Rank> ranks) {
        BigDecimal calculatedValue = totalCalculatedValue;
        BigDecimal revenue = BigDecimal.ZERO;
        int index = 0;
        while (calculatedValue.compareTo(BigDecimal.ZERO) > 0) {
            Rank rank = ranks.get(index++);
            if (rank.getTo() != null) {
                if (rank.getFixed() != null) {
                    if (policyModel.getBeneficialValue().equals("CONTRACTS_QUANTITY")) {
                        revenue = revenue.add(rank.getFixed().multiply(rank.getTo()));
                    }
                    if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                        revenue = revenue.add(rank.getFixed().multiply(rank.getTo()));
                    }
                }

                if (rank.getRate() != null) {
                    if (policyModel.getBeneficialValue().equals("CONTRACT_QUANTITY")) {
                        revenue = revenue.add(rank.getRate().multiply(rank.getTo()).divide(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                    }
                    if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                        revenue = revenue.add(rank.getRate().multiply(rank.getTo()).divide(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                    }
                }
                calculatedValue = calculatedValue.subtract(rank.getTo());
            }
            if (rank.getTo() == null) {
                if (rank.getFixed() != null) {
                    if (policyModel.getBeneficialValue().equals("CONTRACTS_QUANTITY")) {
                        revenue = revenue.add(rank.getFixed().multiply(calculatedValue));
                    }
                    if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                        revenue = revenue.add(rank.getFixed().multiply(calculatedValue));
                    }
                }

                if (rank.getRate() != null) {
                    if (policyModel.getBeneficialValue().equals("CONTRACT_QUANTITY")) {
                        revenue = revenue.add(rank.getRate().multiply(calculatedValue).divide(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                    }
                    if (policyModel.getBeneficialValue().equals("FEE_TURNOVER")) {
                        revenue = revenue.add(rank.getRate().multiply(calculatedValue).divide(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                    }
                }
                calculatedValue = BigDecimal.ZERO;
            }
        }
        return revenue;
    }
}
