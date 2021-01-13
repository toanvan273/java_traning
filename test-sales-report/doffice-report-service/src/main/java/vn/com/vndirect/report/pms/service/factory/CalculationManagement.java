package vn.com.vndirect.report.pms.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalculationManagement {

    private final Map<String, CalculationRevenueService> MAP;
    private @Autowired CommonCalculationTempPeriod commonCalculationTempPeriod;

    public CalculationManagement() {
        MAP = new HashMap<>();
    }

    public void register(String key, CalculationRevenueService service) {
        MAP.put(key, service);
    }

    public CalculationRevenueService get(String key) {
        return MAP.getOrDefault(key, commonCalculationTempPeriod);
    }
}
