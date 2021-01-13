package vn.com.vndirect.report.datasource;

import static vn.com.vndirect.report.datasource.DailySaleReport.*;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

@Component
public class SaleReportStatistic extends Statistic {	

	@Override
	public SolrDocumentList stats(SolrDocumentList list) {
		
		Hashtable<String, Hashtable<String, Object>> accountStatistic = new Hashtable<String, Hashtable<String,Object>>();
		
		Iterator<SolrDocument> iterator = list.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			doc.put(RECORD_COUNT, 1);
			String accountNo  = (String)doc.getFieldValue(ACCT_NO);
			if(!accountStatistic.containsKey(accountNo)) {
				accountStatistic.put(accountNo, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = accountStatistic.get(accountNo);
			// bao cao chinh
			update(values, doc, ACCT_NO, false);
			update(values, doc, CUST_ID, false);
			update(values, doc, CUST_FULL_NAME, false);
			update(values, doc, OPEN_DATE, false);
			update(values, doc, BROKER_ID, false);
			update(values, doc, SALE_FULL_NAME, false);
			update(values, doc, HR_CODE, false);
			update(values, doc, GROUP_NAME);
			update(values, doc, LAST_TRADING_DATE);
			
			sum(values, doc, ACTIVE_FLAG);
			
			sum(values, doc, STOCK_FLAG);
			sum(values, doc, DERIVATIVE_FLAG);
			sum(values, doc, MARGIN_STOCK_FLAG);
			sum(values, doc, MARGIN_DERIVATIVE_FLAG);
			sum(values, doc, BOND_FLAG);
			sum(values, doc, FUND_FLAG);
			sum(values, doc, ASSET_MANAGEMENT_FLAG);
			
			sum(values, doc, STOCK_TRADING_VALUE); // gia tri giao dich co so
			sum(values, doc, STOCK_TRADING_FEE); // doanh thu phi giao dich co so
			sum(values, doc, DERIVATIVE_MATCH_QUANTITY); // khoi luong hop dong phai sinh
			sum(values, doc, DERIVATIVE_TRADING_FEE); // doanh thu phi giao dich phai sinh
			
//			update(values, doc, "stockTotalStockAmount", "endPeriodStockTotalStockAmount");
			
			sum(values, doc, NAV);
			sum(values, doc, AUM);
			
			update(values, doc, NAV, END_PERIOD_NAV);
			update(values, doc, AUM, END_PERIOD_AUM);
			
			sum(values, doc, TOTAL_NSR);
			update(values, doc, TOTAL_ASSET, END_PERIOD_TOTAL_ASSET);
			sum(values, doc, WITHDRAW_AMOUNT);
			sum(values, doc, TRANSFER_AMOUNT);
			
			update(values, doc, DEBT_STOCK_MARGIN, END_PERIOD_DEBT_STOCK_MARGIN);
			update(values, doc, OVERDUE_DEBT, END_PERIOD_OVERDUE_DEBT);
			
			// nav trung binh
			sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, STOCK_TOTAL_CASH_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, STOCK_TOTAL_STOCK_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, TOTAL_DERIVATIVE_NAV, TOTAL_DERIVATIVE_NAVNAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, BOND_VALUE, BOND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, FUND_VALUE, FUND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, DEBT_STOCK_MARGIN, DEBT_STOCK_MARGIN_NAV, ASSET_MANAGEMENT_FLAG, 0);
			
			// aum trung binh
			sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, STOCK_TOTAL_CASH_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, STOCK_TOTAL_STOCK_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, TOTAL_DERIVATIVE_NAV, TOTAL_DERIVATIVE_NAVAUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, BOND_VALUE, BOND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, FUND_VALUE, FUND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, DEBT_STOCK_MARGIN, DEBT_STOCK_MARGIN_AUM, ASSET_MANAGEMENT_FLAG, 1);			
			
			// nav, aum cuoi ky
			update(values, doc, STOCK_TOTAL_CASH_AMOUNT, END_PERIOD_STOCK_TOTAL_CASH_AMOUNT);
			update(values, doc, STOCK_TOTAL_STOCK_AMOUNT, END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT);
			update(values, doc, TOTAL_DERIVATIVE_NAV, END_PERIOD_TOTAL_DERIVATIVE_NAV);
			update(values, doc, BOND_VALUE, END_PERIOD_BOND_VALUE);
			update(values, doc, FUND_VALUE, END_PERIOD_FUND_VALUE);
			update(values, doc, DEBT_STOCK_MARGIN, END_PERIOD_DEBT_STOCK_MARGIN);
			
			update(values, doc, ASSET_MANAGEMENT_FLAG, END_PERIOD_ASSET_MANAGEMENT_FLAG);
			
			// nsr
			sum(values, doc, NSR_STOCK_TRADING);
			sum(values, doc, NSR_DERIVATIVE_TRADING);
			sum(values, doc, NSR_DMARGIN);
			sum(values, doc, NSR_DEAL_FINANCING);
			sum(values, doc, NSR_STOCK_DEBT_UTTB);
			sum(values, doc, NSR_FUND);
			sum(values, doc, NSR_BOND);
			sum(values, doc, NSR_SSS);
			sum(values, doc, NSR_V_NVALUE);
			sum(values, doc, NSR_RA);
			
			sum(values, doc, RECORD_COUNT);
			update(values, doc, DATE);
		}		
		SolrDocumentList summaryList = convertToDocumentList(accountStatistic);	
		return summaryList;
	}
}
