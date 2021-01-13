package vn.com.vndirect.report.datasource;

import static vn.com.vndirect.report.datasource.DailySaleReport.*;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

@Component
public class AccountPortfolioStatistic extends Statistic {

	Hashtable<String, String> listAccountFilter;
	
	@Override
	public SolrDocumentList stats(SolrDocumentList portfolioList) {
		// lay du lieu tu view portfolio, filter theo list account
		Hashtable<String, Hashtable<String, Object>> statistic = new Hashtable<String, Hashtable<String,Object>>();
		
		Iterator<SolrDocument> iterator = portfolioList.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			doc.put(RECORD_COUNT, 1);
			String accountNo  = (String) doc.getFieldValue(ACCT_NO);
			if(!listAccountFilter.containsKey(accountNo)) continue;
			doc.put(CUST_FULL_NAME, listAccountFilter.get(accountNo));
			String symbol = (String) doc.getFieldValue(SYMBOL);
			String key = accountNo + "__" + symbol;
			if(!statistic.containsKey(key)) {
				statistic.put(key, new Hashtable<String, Object>());
			}
			Hashtable<String, Object> values = statistic.get(key);
			update(values, doc, ACCT_NO, false);
			update(values, doc, CUST_FULL_NAME, false);
			update(values, doc, SYMBOL, false);
			update(values, doc, TRADE_QUANTITY, true);
			update(values, doc, T0);
			update(values, doc, T1);
			update(values, doc, T2);
			update(values, doc, T3);
			update(values, doc, BLOCKED_QUANTITY);
			update(values, doc, MORTAGE_QUANTITY);
			update(values, doc, CURRENT_PRICE);
			update(values, doc, MARKET_VALUE);
			update(values, doc, CAPITAL_PRICE);
			update(values, doc, CAPITAL_VALUE);
			
			sum(values, doc, RECORD_COUNT);
		}

		SolrDocumentList summaryList = convertToDocumentList(statistic);
		return summaryList;
	}

	public Hashtable<String, String> getListAccountFilter() {
		return listAccountFilter;
	}

	public void setListAccountFilter(Hashtable<String, String> listAccountFilter) {
		this.listAccountFilter = listAccountFilter;
	}
}
