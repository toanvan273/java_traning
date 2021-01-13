package vn.com.vndirect.report.datasource;

public final class DailySaleReport {
	// truong bao cao chinh
	public static final String ACCT_NO = "acctNo";
	public static final String DATE = "date";
	public static final String NSR_RA = "nsrRA";
	public static final String NSR_V_NVALUE = "nsrVNvalue";
	public static final String NSR_SSS = "nsrSSS";
	public static final String NSR_BOND = "nsrBond";
	public static final String NSR_FUND = "nsrFund";
	public static final String NSR_STOCK_DEBT_UTTB = "nsrStockDebtUttb";
	public static final String NSR_DEAL_FINANCING = "nsrDealFinancing";
	public static final String NSR_DMARGIN = "nsrDmargin";
	public static final String NSR_DERIVATIVE_TRADING = "nsrDerivativeTrading";
	public static final String NSR_STOCK_TRADING = "nsrStockTrading";
	public static final String FUND_VALUE = "fundValue";
	public static final String BOND_VALUE = "bondValue";
	public static final String TOTAL_DERIVATIVE_NAV = "totalDerivativeNAV";
	public static final String STOCK_TOTAL_STOCK_AMOUNT = "stockTotalStockAmount";
	public static final String STOCK_TOTAL_CASH_AMOUNT = "stockTotalCashAmount";
	public static final String OVERDUE_DEBT = "overdueDebt";
	public static final String DEBT_STOCK_MARGIN = "debtStockMargin";
	public static final String TRANSFER_AMOUNT = "transferAmount";
	public static final String WITHDRAW_AMOUNT = "withdrawAmount";
	public static final String TOTAL_ASSET = "totalAsset";
	public static final String TOTAL_NSR = "totalNSR";
	public static final String AUM = "aum";
	public static final String NAV = "nav";
	public static final String DERIVATIVE_TRADING_FEE = "derivativeTradingFee";
	public static final String DERIVATIVE_MATCH_QUANTITY = "derivativeMatchQuantity";
	public static final String STOCK_TRADING_FEE = "stockTradingFee";
	public static final String STOCK_TRADING_VALUE = "stockTradingValue";
	public static final String ASSET_MANAGEMENT_FLAG = "assetManagementFlag";
	public static final String FUND_FLAG = "fundFlag";
	public static final String BOND_FLAG = "bondFlag";
	public static final String MARGIN_DERIVATIVE_FLAG = "marginDerivativeFlag";
	public static final String MARGIN_STOCK_FLAG = "marginStockFlag";
	public static final String DERIVATIVE_FLAG = "derivativeFlag";
	public static final String STOCK_FLAG = "stockFlag";
	public static final String ACTIVE_FLAG = "activeFlag";
	public static final String LAST_TRADING_DATE = "lastTradingDate";
	public static final String GROUP_NAME = "groupName";
	public static final String HR_CODE = "hrCode";
	public static final String SALE_FULL_NAME = "saleFullName";
	public static final String BROKER_ID = "brokerId";
	public static final String OPEN_DATE = "openDate";
	public static final String CUST_FULL_NAME = "custFullName";
	public static final String CUST_ID = "custId";
	public static final String GROUP_ID_USERID = "groupIdUserid";
	
	// tong hop cuoi ky
	public static final String END_PERIOD_ASSET_MANAGEMENT_FLAG = "endPeriodAssetManagementFlag";
	public static final String END_PERIOD_FUND_VALUE = "endPeriodFundValue";
	public static final String END_PERIOD_BOND_VALUE = "endPeriodBondValue";
	public static final String END_PERIOD_TOTAL_DERIVATIVE_NAV = "endPeriodTotalDerivativeNAV";
	public static final String END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT = "endPeriodStockTotalStockAmount";
	public static final String END_PERIOD_STOCK_TOTAL_CASH_AMOUNT = "endPeriodStockTotalCashAmount";
	public static final String END_PERIOD_TOTAL_ASSET = "endPeriodTotalAsset";
	public static final String END_PERIOD_DEBT_STOCK_MARGIN = "endPeriodDebtStockMargin";
	public static final String END_PERIOD_OVERDUE_DEBT = "endPeriodOverdueDebt";
	public static final String END_PERIOD_ACTIVE_ACCOUNT_MANAGEMENT = "endPeriodActiveAccountManagement";
	public static final String END_PERIOD_ACCOUNT_MANAGEMENT = "endPeriodAccountManagement";
	public static final String END_PERIOD_GROUP_ID_USERID = "endPeriodGroupIdUserid";
	public static final String END_PERIOD_SALE_HR_CODE = "endPeriodSaleHrCode";
	
	// tong hop nav
	public static final String DEBT_STOCK_MARGIN_NAV = "debtStockMarginNAV";
	public static final String FUND_VALUE_NAV = "fundValueNAV";
	public static final String BOND_VALUE_NAV = "bondValueNAV";
	public static final String TOTAL_DERIVATIVE_NAVNAV = "totalDerivativeNAVNAV";
	public static final String STOCK_TOTAL_STOCK_AMOUNT_NAV = "stockTotalStockAmountNAV";
	public static final String STOCK_TOTAL_CASH_AMOUNT_NAV = "stockTotalCashAmountNAV";
	public static final String AVERAGE_DEBT_STOCK_MARGIN_NAV = "averageDebtStockMarginNAV";
	public static final String AVERAGE_FUND_VALUE_NAV = "averageFundValueNAV";
	public static final String AVERAGE_BOND_VALUE_NAV = "averageBondValueNAV";
	public static final String AVERAGE_TOTAL_DERIVATIVE_NAVNAV = "averageTotalDerivativeNAVNAV";
	public static final String AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_NAV = "averageStockTotalStockAmountNAV";
	public static final String AVERAGE_STOCK_TOTAL_CASH_AMOUNT_NAV = "averageStockTotalCashAmountNAV";
	public static final String AVERAGE_NAV = "averageNAV";	
	public static final String END_PERIOD_FUND_VALUE_NAV = "endPeriodFundValueNAV";
	public static final String END_PERIOD_BOND_VALUE_NAV = "endPeriodBondValueNAV";
	public static final String END_PERIOD_TOTAL_DERIVATIVE_NAVNAV = "endPeriodTotalDerivativeNAVNAV";
	public static final String END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT_NAV = "endPeriodStockTotalStockAmountNAV";
	public static final String END_PERIOD_STOCK_TOTAL_CASH_AMOUNT_NAV = "endPeriodStockTotalCashAmountNAV";
	public static final String END_PERIOD_DEBT_STOCK_MARGIN_NAV = "endPeriodDebtStockMarginNAV";
	public static final String END_PERIOD_NAV = "endPeriodNAV";
	
	// tong hop aum
	public static final String DEBT_STOCK_MARGIN_AUM = "debtStockMarginAUM";
	public static final String FUND_VALUE_AUM = "fundValueAUM";
	public static final String BOND_VALUE_AUM = "bondValueAUM";
	public static final String TOTAL_DERIVATIVE_NAVAUM = "totalDerivativeNAVAUM";
	public static final String STOCK_TOTAL_STOCK_AMOUNT_AUM = "stockTotalStockAmountAUM";
	public static final String STOCK_TOTAL_CASH_AMOUNT_AUM = "stockTotalCashAmountAUM";
	public static final String AVERAGE_DEBT_STOCK_MARGIN_AUM = "averageDebtStockMarginAUM";
	public static final String AVERAGE_FUND_VALUE_AUM = "averageFundValueAUM";
	public static final String AVERAGE_BOND_VALUE_AUM = "averageBondValueAUM";
	public static final String AVERAGE_TOTAL_DERIVATIVE_NAVAUM = "averageTotalDerivativeNAVAUM";
	public static final String AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_AUM = "averageStockTotalStockAmountAUM";
	public static final String AVERAGE_STOCK_TOTAL_CASH_AMOUNT_AUM = "averageStockTotalCashAmountAUM";
	public static final String AVERAGE_AUM = "averageAUM";
	public static final String END_PERIOD_FUND_VALUE_AUM = "endPeriodFundValueAUM";
	public static final String END_PERIOD_BOND_VALUE_AUM = "endPeriodBondValueAUM";
	public static final String END_PERIOD_TOTAL_DERIVATIVE_NAVAUM = "endPeriodTotalDerivativeNAVAUM";
	public static final String END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT_AUM = "endPeriodStockTotalStockAmountAUM";
	public static final String END_PERIOD_STOCK_TOTAL_CASH_AMOUNT_AUM = "endPeriodStockTotalCashAmountAUM";
	public static final String END_PERIOD_DEBT_STOCK_MARGIN_AUM = "endPeriodDebtStockMarginAUM";
	public static final String END_PERIOD_AUM = "endPeriodAUM";
	
	// tong hop portfolio
	public static final String CAPITAL_VALUE = "capitalValue";
	public static final String CAPITAL_PRICE = "capitalPrice";
	public static final String MARKET_VALUE = "marketValue";
	public static final String CURRENT_PRICE = "currentPrice";
	public static final String MORTAGE_QUANTITY = "mortageQuantity";
	public static final String BLOCKED_QUANTITY = "blockedQuantity";
	public static final String T3 = "T3";
	public static final String T2 = "T2";
	public static final String T1 = "T1";
	public static final String T0 = "T0";
	public static final String TRADE_QUANTITY = "tradeQuantity";
	public static final String SYMBOL = "symbol";
	
	//
	public static final String RECORD_COUNT = "recordCount";
	public static final String DAY_COUNT = "dayCount";
	public static final String IS_END_PERIOD = "isEndPeriod";
}
