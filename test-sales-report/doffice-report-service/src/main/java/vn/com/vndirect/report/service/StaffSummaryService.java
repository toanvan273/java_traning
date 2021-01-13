package vn.com.vndirect.report.service;

import static vn.com.vndirect.report.datasource.DailySaleReport.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.com.vndirect.report.datasource.AccountPortfolioRepo;
import vn.com.vndirect.report.datasource.AccountPortfolioStatistic;
import vn.com.vndirect.report.datasource.SaleReportStatistic;

import static vn.com.vndirect.report.service.CacheService.*;

@Service
public class StaffSummaryService extends BaseSummaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(StaffSummaryService.class);
	
	@Autowired
	private SaleReportStatistic saleReportStatistic;
	
	@Autowired
	private AccountPortfolioRepo accountPortfolioRepo;
	@Autowired
	private AccountPortfolioStatistic accountPortfolioStatistic;
	
	public SolrDocumentList getSummaryData(String firstDayText, String lastDayText, String hrCode) throws ParseException, SolrServerException, IOException {
		// tao key cho request
		String key = createKeyCache(firstDayText, lastDayText, hrCode);
		Optional<SolrDocumentList> dataCached = cacheService.findOne(STAFF_SUMMARY, key);
		// neu co du lieu trong cache thi return ket qua
		if(dataCached.isPresent()) {
			return dataCached.get();
		}
		
		// neu ko co du lieu trong cache thi xu ly nhu binh thuong va put ket qua vao cache
		StringBuilder queryBuilder = createQueryBuilder(firstDayText, lastDayText, hrCode, null);
		String query = queryBuilder.toString();
		logger.info("Query for daily_sale_report_vw: " + query);
		long timeStart = System.currentTimeMillis();
		SolrDocumentList detailDataList = dailySaleReportRepo.queryAsJson(query);
		long timeEnd = System.currentTimeMillis();
		logger.info("thoi gian query solr: " + (timeEnd-timeStart) + ", total row:" + detailDataList.getNumFound());
		timeStart = System.currentTimeMillis();
		SolrDocumentList summaryList = saleReportStatistic.stats(detailDataList);
		timeEnd = System.currentTimeMillis();
		logger.info("thoi gian tong hop: " + (timeEnd-timeStart));
		
		cacheService.save(STAFF_SUMMARY, key, summaryList);
		
		return summaryList;
	}
	
	public SolrDocumentList getPortfolioSummaryData(SolrDocumentList summaryData, SolrDocumentList portfolioData) throws ParseException, SolrServerException, IOException {
		// tach ra list accountNo thoa man dieu kien
		Hashtable<String, String> accountList = new Hashtable<String, String>();
		Iterator<SolrDocument> summaryDataIterator = summaryData.iterator();
		while(summaryDataIterator.hasNext()) {
			SolrDocument doc = summaryDataIterator.next();
			String account  = (String)doc.getFieldValue(ACCT_NO);
			if(accountList.containsKey(account)) continue;
			
			String custFullName = ReportUtils.nvl(doc.getFieldValue(CUST_FULL_NAME), "");
			accountList.put(account, ReportUtils.nvl(custFullName, ""));
		}
		accountPortfolioStatistic.setListAccountFilter(accountList);
		SolrDocumentList accountPortfolioSummaryList = accountPortfolioStatistic.stats(portfolioData);
		return accountPortfolioSummaryList;
	}
	
	public SolrDocumentList getPortfolioData(String lastDayText, String hrCode) throws ParseException, SolrServerException, IOException {
		// tao key cho request
		String key = createKeyCache(lastDayText, hrCode);
		Optional<SolrDocumentList> dataCached = cacheService.findOne(ACCOUNT_PORTFOLIO, key);
		if(dataCached.isPresent()) {
			return dataCached.get();
		}
		
		StringBuilder queryBuilder = createQueryBuilder(lastDayText, lastDayText, hrCode, null);
		logger.info("Query for account_portfolio_vw: " + queryBuilder);
		SolrDocumentList detailDataList = accountPortfolioRepo.queryAsJson(queryBuilder.toString());
		
		cacheService.save(ACCOUNT_PORTFOLIO, key, detailDataList);
		
		return detailDataList;
	}
	
	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String hrcode, String accountNo) throws ParseException {
		StringBuilder builder = new StringBuilder();
		
		processDate(firstDayText, lastDayText, builder);
		processHrCode(hrcode, builder);
		processAccount(accountNo, builder);
		
		if(builder.length() < 1) builder.append("*:*");

		return builder;
	}
	
	protected void processManagerCode(String managerHrCode, StringBuilder builder) {
		if(!StringUtils.isEmpty(managerHrCode)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" managerHrCode:").append(managerHrCode);
		}
	}
	
	protected void processAccount(String accountNo, StringBuilder builder) {
		if(!StringUtils.isEmpty(accountNo)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" acctNo:").append(accountNo);
		}
	}
	
	public SolrDocumentList filterProduct(SolrDocumentList summaryList, String queryProductList) {
		// create lai list ket qua tong hop
		// apply theo dieu kien query product
		SolrDocumentList filterSummaryList = new SolrDocumentList();
		Iterator<SolrDocument> summaryListIterator = summaryList.iterator();
		while(summaryListIterator.hasNext()) {
			SolrDocument doc = summaryListIterator.next();
			if(inProductList(doc, queryProductList)) {
				filterSummaryList.add(doc);
			}
		}
		
		return filterSummaryList;
	}
	
	protected void processHrCode(String hrcode, StringBuilder builder) {
		if(!StringUtils.isEmpty(hrcode)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" hrCode:").append(hrcode);
		}
	}

	protected boolean inProductList(SolrDocument solrDocument, String queryProductList) {
		if(StringUtils.isEmpty(queryProductList)) {
			return true;
		}
		
		String[] listProducts = queryProductList.replace("\"", "").split(",");
		for (int i = 0; i < listProducts.length; i++) {
			String productName = listProducts[i];
			if(solrDocument.containsKey(productName)) {
				int productFlagValue = (int) solrDocument.getFieldValue(productName);
				if(productFlagValue > 0) { 
					return true;
				}
			} 
		}
		return false;
	}
	
	/**
	 * Description: get group name of sale in endperiod
	 * @param lastDayText
	 * @param hrCode
	 * @param groupField
	 * @return
	 * @throws ParseException
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public String getGroupNameInEndperiod(String lastDayText, String hrCode, String groupField) throws ParseException, SolrServerException, IOException {
		StringBuilder query = createQueryBuilder(lastDayText, lastDayText, hrCode, null);
		logger.info("Query for group name:" + query);
		QueryResponse response = dailySaleReportRepo.queryGroup(query.toString(), null, groupField);
		String groupname = getGroupName(response);
		return groupname;
	}
	
	public String getGroupName(QueryResponse response) {
		String groupName = "";
		List<Group> groups = response.getGroupResponse().getValues().get(0).getValues();
		if(groups.isEmpty()) {
			return groupName;
		}
		groupName = (String) groups.get(0).getResult().get(0).getFieldValue(GROUP_NAME);
		return groupName;
	}
}
