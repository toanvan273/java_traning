package vn.com.vndirect.report.service;

import static vn.com.vndirect.report.datasource.DailySaleReport.*;
import static vn.com.vndirect.report.service.CacheService.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.com.vndirect.report.datasource.DailySaleReportRepo;
import vn.com.vndirect.report.datasource.ManagerReportStatistic;

@Service
public class ManagerSummaryService extends BaseSummaryService {

	private static final Logger logger = LoggerFactory.getLogger(ManagerSummaryService.class);
	
	@Autowired
	private DailySaleReportRepo repo;
	
	@Autowired
	private ManagerReportStatistic managerReportStatistic;
	
	public SolrDocumentList getSummaryData(String firstDayText, String lastDayText, String groupIdUserid) throws ParseException, SolrServerException, IOException {
		
		// tao key cho request
		String key = createKeyCache(firstDayText, lastDayText, groupIdUserid);
		Optional<SolrDocumentList> dataCached = cacheService.findOne(MANAGER_SUMMARY, key);
		if(dataCached.isPresent()) {
			return dataCached.get();
		}
		
		SolrDocumentList detailAccountList = getDetailListAccount(firstDayText, lastDayText, groupIdUserid);
		SolrDocumentList detailDataList = getDetailData(firstDayText, lastDayText, groupIdUserid);
		
		managerReportStatistic.setQueryLastDate(lastDayText);
		managerReportStatistic.setGroupIdUserid(groupIdUserid);
		managerReportStatistic.setDetailAccount(detailAccountList);
		
		SolrDocumentList summaryDataList = managerReportStatistic.stats(detailDataList);
		
		cacheService.save(MANAGER_SUMMARY, key, summaryDataList);
		return summaryDataList;		
	}
	
	public SolrDocumentList filterSaleHrCode(SolrDocumentList summaryList, String saleHrCode) {
		SolrDocumentList filterSummaryList = new SolrDocumentList();
		Iterator<SolrDocument> summaryListIterator = summaryList.iterator();
		while(summaryListIterator.hasNext()) {
			SolrDocument doc = summaryListIterator.next();
			if(inSaleHrCodeList(doc, saleHrCode)) {
				filterSummaryList.add(doc);
			}
		}
		return filterSummaryList;
	}
	
	private boolean inSaleHrCodeList(SolrDocument doc, String querySaleHrCode) {
		if (StringUtils.isEmpty(querySaleHrCode)) {
			return true;
		}

		querySaleHrCode = querySaleHrCode + " ";
		String saleHrCode = (String) doc.getFieldValue(HR_CODE);		
		return querySaleHrCode.contains(saleHrCode + " ");
	}
	
	public SolrDocumentList getDetailData(String firstDayText, String lastDayText, String groupIdUserid) throws ParseException, SolrServerException, IOException {
		StringBuilder queryBuilder = createQueryBuilder(firstDayText, lastDayText, groupIdUserid, null, null);
		String query = queryBuilder.toString();
		logger.info("Query for daily_sale_report_vw: " + query);
		long timeStart = System.currentTimeMillis();
		SolrDocumentList detailDataList = dailySaleReportRepo.queryAsJson(query);
		long timeEnd = System.currentTimeMillis();
		logger.info("thoi gian query solr: " + (timeEnd-timeStart) + ", total row:" + detailDataList.getNumFound());
		return detailDataList;
	}
	
	public StringBuilder createQueryBuilder(String firstDayText, String lastDayText, String groupIdUserid, String saleHrCode, 
			String listAccount) throws ParseException {
		StringBuilder builder = new StringBuilder();
		
		processDate(firstDayText, lastDayText, builder);
		processSaleHrCode(saleHrCode, builder);
		processGroupIdUserid(groupIdUserid, builder);
		processListAccount(listAccount, builder);
		
		if(builder.length() < 1) builder.append("*:*");

		return builder;
	}
	
	protected void processGroupIdUserid(String groupIdUserid, StringBuilder builder) {
		if(!StringUtils.isEmpty(groupIdUserid)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append("groupIdUserid: (").append(groupIdUserid).append(")");
		}
	}
	
	protected void processSaleHrCode(String saleHrCode, StringBuilder builder) {
		if(!StringUtils.isEmpty(saleHrCode)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" hrCode:(").append(saleHrCode).append(")");
		}
	}
	
	protected void processListAccount(String listAccount, StringBuilder builder) {
		if(!StringUtils.isEmpty(listAccount)) {
			if(builder.length() > 0) appendAndClause(builder);
			builder.append(" acctNo:(").append(listAccount).append(")");
		}
	}
	
	protected SolrDocumentList getDetailListAccount(String firstDayText, String lastDayText, String groupIdUserid) throws ParseException, SolrServerException, IOException {
		// buoc 1: select distinct (acctNo)
		StringBuilder queryBuilder = createQueryBuilder(firstDayText, lastDayText, groupIdUserid, null, null);
		String query = queryBuilder.toString();
		logger.info("Query for account daily_sale_report_vw: " + query);
		List<Count> listAccount = dailySaleReportRepo.queryFacet(query, ACCT_NO); 
		String accounts = listAccount.stream().map(count -> count.getName()).collect(Collectors.joining(" "));
		
		// buoc 2: lay chi tiet theo acctNo, activeFlag trong ky
		queryBuilder = createQueryBuilder(firstDayText, lastDayText, null, null, accounts);
		query = queryBuilder.toString();
		String fieldList = String.join(",", ACCT_NO, HR_CODE, GROUP_ID_USERID, ACTIVE_FLAG, DATE);
		logger.info("Query for account acctive daily_sale_report_vw: " + query.length());
		QueryResponse queryResponse = dailySaleReportRepo.doQuery(query, fieldList, null);
		return queryResponse.getResults();
	}
	
	protected Map<String, Object> getListAccount(SolrDocumentList list) {
		Hashtable<String, Object> accounts = new Hashtable<String, Object>();
		Iterator<SolrDocument> iterator = list.iterator();
		while(iterator.hasNext()) {
			SolrDocument doc = iterator.next();
			String acctNo = (String) doc.getFieldValue(ACCT_NO);
			if(!accounts.containsKey(acctNo)) {
				accounts.put(acctNo, "");
			}
		}
		return accounts;
	}
	
	/**
	 * Description: function to stat list sale name and hrcode with condition are specific time and managercode
	 * @param query
	 * @param groupField
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * TODO
	 * @throws ParseException 
	 */
	public SolrDocumentList getListSaleFullNameHrcode(String firstDayText, String lastDayText, String groupIdUserid, String fieldList, String... groupField) throws SolrServerException, IOException, ParseException {
		StringBuilder query = createQueryBuilder(firstDayText, lastDayText, groupIdUserid, null, null);
		System.out.println(query.toString());
		QueryResponse response = repo.queryGroup(query.toString(), fieldList, groupField);
		SolrDocumentList solrListSaleNameHrcode = getSaleNameHrcode(response);
		return solrListSaleNameHrcode;
	}
	
	/**
	 * Description: function to create solrdocumentlist of hrcode and saleFullName
	 * @param response
	 * @return
	 * TODO
	 */
	public SolrDocumentList getSaleNameHrcode(QueryResponse response) {
		List<Group> groups = response.getGroupResponse().getValues().get(0).getValues();
		SolrDocumentList list = new SolrDocumentList();
		for (Group group : groups) {
			list.add(group.getResult().get(0));
		}
		return list;
	}
	
	
	/**
	 * Description: function to get list groupid and groupname with specific managercode
	 * @param query
	 * @param groupField
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * TODO
	 * @throws ParseException 
	 */
	public SolrDocumentList getListGroupIdGroupName(List<String> groupIdUserids, String...condition) throws SolrServerException, IOException, ParseException {
		String groupIdUserid = String.join(" ", groupIdUserids);		
		StringBuilder query = createQueryBuilder(null, null, groupIdUserid, null, null);
		QueryResponse response = repo.queryCondition(query, condition);	
		SolrDocumentList solrListGroupIdGroupName = getGroupIdGroupName(response);
		return solrListGroupIdGroupName;
	}
	
	/**
	 * Description: function to create solrdocumentlist of groupIdUserid and groupname
	 * @param response
	 * @return
	 * TODO
	 */
	public SolrDocumentList getGroupIdGroupName(QueryResponse response) {
		List<PivotField> infoGroupIdGroupName = response.getFacetPivot().getVal(0);
		SolrDocumentList list = new SolrDocumentList();;
		
		for(int i = 0; i < infoGroupIdGroupName.size(); i++) {			
			SolrDocument document = new SolrDocument();
			
			document.put(GROUP_ID_USERID, infoGroupIdGroupName.get(i).getValue());			
			Map<String, FieldStatsInfo> statInfo = infoGroupIdGroupName.get(i).getFieldStatsInfo();
			document.put(GROUP_NAME, statInfo.get(GROUP_NAME).getMax());	
			
			list.add(document);
		}
		return list;
	}
}
