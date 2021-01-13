package vn.com.vndirect.report.pms.service.factory;

public abstract class AbstractCalculation implements CalculationRevenueService {

    private final CalculationManagement calculationManagement;

    public AbstractCalculation(CalculationManagement calculationManagement) {
        this.calculationManagement = calculationManagement;
        this.calculationManagement.register(registerCalculationCycle() + "-" + registerClosingTime(), this);
    }


    public abstract String registerCalculationCycle();
    public abstract String registerClosingTime();
}
