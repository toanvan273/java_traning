package vn.com.vndirect.report.datasource;

import static vn.com.vndirect.report.datasource.DailySaleReport.*;

import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

import lombok.Setter;
import vn.com.vndirect.report.service.ReportUtils;

@Component
public class ManagerReportStatistic extends Statistic {

	private Hashtable<String, Hashtable<String, Object>> accountStatistic;
	
	private Date queryLastDate;
	
	@Setter
	private String groupIdUserid;

	@Override
	public SolrDocumentList stats(SolrDocumentList list) {
		Hashtable<String, Hashtable<String, Object>> summaryByHrCode = summaryByHrCode(list);
		Hashtable<String, Hashtable<String, Object>> summaryNAVAUM = summaryByHrCodeAndAccount(list);
		Hashtable<String, Hashtable<String, Object>> summaryProductEndPeriod = convertAccountToHrCode(accountStatistic);
		Hashtable<String, Hashtable<String, Object>> managerStatistic = merge(summaryByHrCode, summaryNAVAUM);
		managerStatistic = merge(managerStatistic, summaryProductEndPeriod);
		SolrDocumentList summaryList = convertToDocumentList(managerStatistic);	
		return summaryList;
	}
	
	protected Hashtable<String, Hashtable<String, Object>> summaryByHrCode(SolrDocumentList list) {
		Hashtable<String, Hashtable<String, Object>> managerStatistic = new Hashtable<String, Hashtable<String,Object>>();
		
		Iterator<SolrDocument> iterator = list.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String saleHrCode = (String)doc.getFieldValue(HR_CODE);
			String groupIdUserId = (String) doc.getFieldValue(GROUP_ID_USERID);
			String key = saleHrCode + "__" + groupIdUserId;
			Date date = (Date) doc.getFieldValue(DATE);
			
			if(ReportUtils.isSameDay(date, queryLastDate)) {
				doc.put(IS_END_PERIOD, 1);
			}
			if(!managerStatistic.containsKey(key)) {
				managerStatistic.put(key, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = managerStatistic.get(key);
			// bao cao chinh
			update(values, doc, HR_CODE);
			update(values, doc, SALE_FULL_NAME);
			update(values, doc, GROUP_NAME);
			update(values, doc, GROUP_ID_USERID);
			
			sumIf(values, doc, NAV, END_PERIOD_NAV, IS_END_PERIOD, 1);  // nav cuoi ky
			sumIf(values, doc, AUM, END_PERIOD_AUM, IS_END_PERIOD, 1);  // aum cuoi ky
			sumIf(values, doc, TOTAL_ASSET, END_PERIOD_TOTAL_ASSET, IS_END_PERIOD, 1);    // total asset cuoi ky
			sumIf(values, doc, DEBT_STOCK_MARGIN, END_PERIOD_DEBT_STOCK_MARGIN, IS_END_PERIOD, 1);   //  du no cho vay cuoi ky
			sumIf(values, doc, OVERDUE_DEBT, END_PERIOD_OVERDUE_DEBT, IS_END_PERIOD, 1);        // du no qua han tai cuoi ky
			
			if (isEndPeriod(doc)) {
				sumIf(values, doc, DEBT_STOCK_MARGIN, END_PERIOD_DEBT_STOCK_MARGIN_NAV, ASSET_MANAGEMENT_FLAG, 0);   //  du no cho vay cuoi ky nav
				sumIf(values, doc, DEBT_STOCK_MARGIN, END_PERIOD_DEBT_STOCK_MARGIN_AUM, ASSET_MANAGEMENT_FLAG, 1);   //  du no cho vay cuoi ky aum				
				sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, END_PERIOD_STOCK_TOTAL_CASH_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0); // tong tien co so cuoi ky nav			
				sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, END_PERIOD_STOCK_TOTAL_CASH_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1); // tong tien co so cuoi ky aum				
				sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0); // gia tri chung khoan cuoi ky nav
				sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, END_PERIOD_STOCK_TOTAL_STOCK_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1); // gia tri chung khoan cuoi ky aum				
				sumIf(values, doc, TOTAL_DERIVATIVE_NAV, END_PERIOD_TOTAL_DERIVATIVE_NAVNAV, ASSET_MANAGEMENT_FLAG, 0); // nav phai sinh cuoi ky nav
				sumIf(values, doc, TOTAL_DERIVATIVE_NAV, END_PERIOD_TOTAL_DERIVATIVE_NAVAUM, ASSET_MANAGEMENT_FLAG, 1); // nav phai sinh cuoi ky aum				
				sumIf(values, doc, BOND_VALUE, END_PERIOD_BOND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0); // gia tri trai phieu cuoi ky
				sumIf(values, doc, BOND_VALUE, END_PERIOD_BOND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1); // gia tri trai phieu cuoi ky				
				sumIf(values, doc, FUND_VALUE, END_PERIOD_FUND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0); // gia tri chung chi quy cuoi ky
				sumIf(values, doc, FUND_VALUE, END_PERIOD_FUND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1); // gia tri chung chi quy cuoi ky
			}
			
			sum(values, doc, STOCK_TRADING_VALUE); // gia tri giao dich co so
			sum(values, doc, STOCK_TRADING_FEE); // doanh thu phi giao dich co so
			
			// nsr
			sum(values, doc, TOTAL_NSR);    // tong nsr
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
			  
			sum(values, doc, WITHDRAW_AMOUNT);    // gia tri tien mat rut ra ngoai cong ty
			sum(values, doc, TRANSFER_AMOUNT);     // gia tri tien mat nop vao cong ty
		}
		return managerStatistic;
	}
	
	protected Hashtable<String, Hashtable<String, Object>> summaryByHrCodeAndAccount(SolrDocumentList list) {
		Hashtable<String, Hashtable<String, Object>> navAUMStatistic = new Hashtable<String, Hashtable<String,Object>>();
		
		Iterator<SolrDocument> iterator = list.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String acctNo = (String) doc.getFieldValue(ACCT_NO);
			String saleHrCode = (String) doc.getFieldValue(HR_CODE);
			String groupIdUserId = (String) doc.getFieldValue(GROUP_ID_USERID);
			String key = acctNo + "__" + saleHrCode + "__" + groupIdUserId;
			if(!navAUMStatistic.containsKey(key)) {
				navAUMStatistic.put(key, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = navAUMStatistic.get(key);
			update(values, doc, HR_CODE);
			update(values, doc, ACCT_NO);
			update(values, doc, GROUP_ID_USERID);
			
			// nav trung binh
			sum(values, doc, NAV);
			sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, STOCK_TOTAL_CASH_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, STOCK_TOTAL_STOCK_AMOUNT_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, TOTAL_DERIVATIVE_NAV, TOTAL_DERIVATIVE_NAVNAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, BOND_VALUE, BOND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, FUND_VALUE, FUND_VALUE_NAV, ASSET_MANAGEMENT_FLAG, 0);
			sumIf(values, doc, DEBT_STOCK_MARGIN, DEBT_STOCK_MARGIN_NAV, ASSET_MANAGEMENT_FLAG, 0);
			
			// aum trung binh
			sum(values, doc, AUM);
			sumIf(values, doc, STOCK_TOTAL_CASH_AMOUNT, STOCK_TOTAL_CASH_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, STOCK_TOTAL_STOCK_AMOUNT, STOCK_TOTAL_STOCK_AMOUNT_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, TOTAL_DERIVATIVE_NAV, TOTAL_DERIVATIVE_NAVAUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, BOND_VALUE, BOND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, FUND_VALUE, FUND_VALUE_AUM, ASSET_MANAGEMENT_FLAG, 1);
			sumIf(values, doc, DEBT_STOCK_MARGIN, DEBT_STOCK_MARGIN_AUM, ASSET_MANAGEMENT_FLAG, 1);
			
		}
		
		// tinh toan nav, aum trung binh
		Enumeration<String> keys = navAUMStatistic.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			Hashtable<String, Object> values = navAUMStatistic.get(key);
			average(values, NAV, AVERAGE_NAV);
			average(values, STOCK_TOTAL_CASH_AMOUNT_NAV, AVERAGE_STOCK_TOTAL_CASH_AMOUNT_NAV);
			average(values, STOCK_TOTAL_STOCK_AMOUNT_NAV, AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_NAV);
			average(values, TOTAL_DERIVATIVE_NAVNAV, AVERAGE_TOTAL_DERIVATIVE_NAVNAV);
			average(values, BOND_VALUE_NAV, AVERAGE_BOND_VALUE_NAV);
			average(values, FUND_VALUE_NAV, AVERAGE_FUND_VALUE_NAV);
			average(values, DEBT_STOCK_MARGIN_NAV, AVERAGE_DEBT_STOCK_MARGIN_NAV);
			
			average(values, AUM, AVERAGE_AUM);
			average(values, STOCK_TOTAL_CASH_AMOUNT_AUM, AVERAGE_STOCK_TOTAL_CASH_AMOUNT_AUM);
			average(values, STOCK_TOTAL_STOCK_AMOUNT_AUM, AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_AUM);
			average(values, TOTAL_DERIVATIVE_NAVAUM, AVERAGE_TOTAL_DERIVATIVE_NAVAUM);
			average(values, BOND_VALUE_AUM, AVERAGE_BOND_VALUE_AUM);
			average(values, FUND_VALUE_AUM, AVERAGE_FUND_VALUE_AUM);
			average(values, DEBT_STOCK_MARGIN_AUM, AVERAGE_DEBT_STOCK_MARGIN_AUM);
		}
		SolrDocumentList listNAVAUM = convertToDocumentList(navAUMStatistic);
		
		Hashtable<String, Hashtable<String, Object>> saleHrCodeStatistic = new Hashtable<String, Hashtable<String,Object>>();
		iterator = listNAVAUM.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String saleHrCode = (String) doc.getFieldValue(HR_CODE);
			String groupIdUserId = (String) doc.getFieldValue(GROUP_ID_USERID);
			String key = saleHrCode + "__" + groupIdUserId;
			if(!saleHrCodeStatistic.containsKey(key)) {
				saleHrCodeStatistic.put(key, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = saleHrCodeStatistic.get(key);			
			sum(values, doc, AVERAGE_NAV);
			sum(values, doc, AVERAGE_STOCK_TOTAL_CASH_AMOUNT_NAV);
			sum(values, doc, AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_NAV);
			sum(values, doc, AVERAGE_TOTAL_DERIVATIVE_NAVNAV);
			sum(values, doc, AVERAGE_BOND_VALUE_NAV);
			sum(values, doc, AVERAGE_FUND_VALUE_NAV);
			sum(values, doc, AVERAGE_DEBT_STOCK_MARGIN_NAV);
			
			sum(values, doc, AVERAGE_AUM);
			sum(values, doc, AVERAGE_STOCK_TOTAL_CASH_AMOUNT_AUM);
			sum(values, doc, AVERAGE_STOCK_TOTAL_STOCK_AMOUNT_AUM);
			sum(values, doc, AVERAGE_TOTAL_DERIVATIVE_NAVAUM);
			sum(values, doc, AVERAGE_BOND_VALUE_AUM);
			sum(values, doc, AVERAGE_FUND_VALUE_AUM);
			sum(values, doc, AVERAGE_DEBT_STOCK_MARGIN_AUM);
		}
		return saleHrCodeStatistic;
	}
	
	protected Hashtable<String, Hashtable<String, Object>> summaryByAccount(SolrDocumentList list) {
		// tong hop danh sach account theo trang thai va sale hrcode ngay cuoi ky
		Hashtable<String, Hashtable<String, Object>> accountStatistic = new Hashtable<String, Hashtable<String,Object>>();
		
		Iterator<SolrDocument> iterator = list.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String acctNo = (String) doc.getFieldValue(ACCT_NO);
			Date date = (Date) doc.getFieldValue(DATE);
			if(ReportUtils.isSameDay(date, queryLastDate)) {
				doc.put(IS_END_PERIOD, 1);
			}
			if(!accountStatistic.containsKey(acctNo)) {
				accountStatistic.put(acctNo, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = accountStatistic.get(acctNo);
			sum(values, doc, ACTIVE_FLAG);
			increment(values, DAY_COUNT, 1);
			if(isEndPeriod(doc)) {
				update(values, doc, HR_CODE, END_PERIOD_SALE_HR_CODE);
				update(values, doc, GROUP_ID_USERID, END_PERIOD_GROUP_ID_USERID);
			}
		}
		return accountStatistic;
	}
		
	protected Hashtable<String, Hashtable<String, Object>> convertAccountToHrCode(Hashtable<String, Hashtable<String, Object>> accountStatistic) {
		// chuyen tu danh sach account thanh danh sach theo sale hrcode
		Hashtable<String, Hashtable<String, Object>> saleHrCodeStatistic = new Hashtable<String, Hashtable<String,Object>>();
		Enumeration<String> accounts = accountStatistic.keys();
		while(accounts.hasMoreElements()) {
			String acctNo = accounts.nextElement();
			Hashtable<String, Object> acctValues = accountStatistic.get(acctNo);
			if(!acctValues.containsKey(END_PERIOD_SALE_HR_CODE) 
						|| !acctValues.containsKey(END_PERIOD_GROUP_ID_USERID)) continue;
			
			String endPeriodGroupIdUserid = (String) acctValues.get(END_PERIOD_GROUP_ID_USERID);
			if(!groupIdUserid.contains(endPeriodGroupIdUserid)) continue;
			
			String endPeriodSaleHrCode = (String) acctValues.get(END_PERIOD_SALE_HR_CODE);
			String key = endPeriodSaleHrCode + "__" + endPeriodGroupIdUserid;
			if(!saleHrCodeStatistic.containsKey(key)) {
				saleHrCodeStatistic.put(key, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> saleHrCodeValues = saleHrCodeStatistic.get(key);
			increment(saleHrCodeValues, END_PERIOD_ACCOUNT_MANAGEMENT, 1);
			int activeFlag = (int) acctValues.get(ACTIVE_FLAG);
			if(activeFlag > 0) {
				increment(saleHrCodeValues, END_PERIOD_ACTIVE_ACCOUNT_MANAGEMENT, 1);
			}
		}
		return saleHrCodeStatistic;
	}
	
	protected Hashtable<String, Hashtable<String, Object>> merge(Hashtable<String, Hashtable<String, Object>> listOne, Hashtable<String, Hashtable<String, Object>> listTwo) {
		Hashtable<String, Hashtable<String, Object>> result = new Hashtable<String, Hashtable<String,Object>>();
		result.putAll(listOne);
		Enumeration<String> keys = result.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			Hashtable<String, Object> valuesOne = result.get(key);
			if(!listTwo.containsKey(key)) continue;
			
			Hashtable<String, Object> valuesTwo = listTwo.get(key);
			valuesOne.putAll(valuesTwo);
		}
		return result;
	}
	
	protected void average(Hashtable<String, Object> values, String srcFieldName, String destFieldName) {
		String acctNo = (String) values.get(ACCT_NO);
		int dayCount = (int) accountStatistic.get(acctNo).get(DAY_COUNT);
		
		Double value = (Double) values.get(srcFieldName);
		if (value != null) values.put(destFieldName, value / dayCount);
	}
	
	protected void increment(Hashtable<String, Object> values, String name, int incrementValue) {
		if(!values.containsKey(name)) {
			values.put(name, incrementValue);
			return;
		}
		
		int currentValue = (int) values.get(name);
		values.put(name, currentValue + incrementValue);
	}
	
	protected boolean isEndPeriod(SolrDocument doc) {
		if(!doc.containsKey(IS_END_PERIOD)) return false;
		int isEndPeriod = (int) doc.getFieldValue(IS_END_PERIOD);
		return isEndPeriod == 1;
	}
	
	public void setDetailAccount(SolrDocumentList list) {
		accountStatistic = summaryByAccount(list);
	}
	
	public void setQueryLastDate(String queryLastDate) throws ParseException {
		this.queryLastDate = ReportUtils.convertDate(queryLastDate, ReportUtils.DATE_SHORT_FORMAT, ReportUtils.CONVERT_BEGIN_DATE_TYPE);
	}
}
