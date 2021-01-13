package vn.com.vndirect.report.datasource;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class DailySaleReportRepo extends DataSourceRepo {
	
	public final static String [] DAILY_SALE_REPORT_COLUMN = {
			"accNo",	        
			"customerId",
	        "openDate",
	        
	        "brokerId",
	        "saleFullName",
	        "saleHrCode",
	        "groupName",
	        
	        "lastestOrderDate",
	        
	        "accountStatus",
	        
	        "numberOfSerPro",
	        
	        "valueOfUnderlyingTransaction",
	        "valueOfFeeUnderlyingTransaction",
	        
	        "valueOfFeeDerivativeTransaction",
	        "numberOfDerivativeContract",
	        
	        "valueOfPortfolio",
	        
	        "valueOfEndPeriodNAV",
	        "valueOfEndPeriodAUM",
	        
	        "valueOfAverageFee",
	        
	        "totalNSR",
	        "totalAsset",
	        
	        "cashOut",
	        "cashIn",
	        
	        "totalCashAmount",
	        "debtStockMargin",
	        
	        "overdueDebtOfEndPeriod"
	        
	        
	        //--------------------------------------
	        
//	        "totalStockValue",
//	       
//	        "valueOfAverageNAV",
//	        "valueOfAverageAUM",
//	        
//	        "valueOfEndPeriodOutstandingLoan",
//	        
//	        "nsrDmargin",
//	        
//	        "fundValue",
//	        "totalDeriavtiveValue",
//	       
//	       
//	        "nsrDfinance",
//	       
//	        "nsrDerivativeTrading",
//	        "nsrStockTrading",
//	        "nsrFund",
//	        "nsrBond",
//	        
//	        
//	        "nsrSSS",
//	        "nsrRA",
//	        "nsrVNvalue",
//	       
//	        "bondValue"
	};

	@Autowired
	public DailySaleReportRepo(Environment environment) throws MalformedURLException {
		super(environment, "daily-sale-report");
	}
}
