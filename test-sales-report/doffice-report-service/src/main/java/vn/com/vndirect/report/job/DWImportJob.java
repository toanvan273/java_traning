package vn.com.vndirect.report.job;

import java.util.Calendar;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.homedirect.common.solr.repository.SolrClientCreator;

import vn.com.vndirect.report.service.CacheService;
import vn.com.vndirect.report.service.ExecutableService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:thuan.nhu@vndirect.com.vn
 * Dec 13, 2019
 */
@Service
class DWImportJob extends ExecutableService {

    private final static Logger logger = LoggerFactory.getLogger(DWImportJob.class);
    
    @Autowired
    private CacheService<SolrDocumentList> cacheService;

    private String dailySaleReportSolrUrl;
    private String accountPortfolioSolrUrl;
    
    private int syncProceedDate = -1;
    private int syncHour = -1;
    private int syncMinute = -1;
    
    private int clearCacheDate = -1;
    private int clearCacheHour = -1;
    private int clearCacheMinute = -1;


    @Autowired
    public DWImportJob(Environment env) {
    	super(60, 60);
        this.dailySaleReportSolrUrl = env.getProperty("solr.url.daily-sale-report");
        this.accountPortfolioSolrUrl = env.getProperty("solr.url.account-portfolio");
        
        try {
            syncHour = Integer.parseInt(env.getProperty("solr.sync.hour"));
            syncMinute = Integer.parseInt(env.getProperty("solr.sync.minute"));
            
            clearCacheHour = Integer.parseInt(env.getProperty("cache.clear.hour"));
            clearCacheMinute = Integer.parseInt(env.getProperty("cache.clear.minute"));
        } catch (Exception e) {
            logger.error(e.toString(), e);
            syncHour = -1;
            syncMinute = -1;
            
            clearCacheHour = -1;
            clearCacheMinute = -1;
        }
        logger.info("Sync data from warehouse at " + syncHour + ":" + syncMinute);
    }
    
    @Override
    public void execute() {
		processSyncData();
		processClearCache();
    }
    
    private void processSyncData() {
    	if(syncHour < 0) return;
		Calendar calendar = Calendar.getInstance();
		int date = calendar.get(Calendar.DATE);
		if(date == syncProceedDate) return;
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour != syncHour) return;
		int minute = calendar.get(Calendar.MINUTE);
		if(minute < syncMinute) return;
		  
		syncProceedDate = date;
		  
		sync();
    }

    private void processClearCache() {
    	if(clearCacheHour < 0) return;
    	Calendar calendar = Calendar.getInstance();
		int date = calendar.get(Calendar.DATE);
		if(date == clearCacheDate) return;
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour != clearCacheHour) return;
		int minute = calendar.get(Calendar.MINUTE);
		if(minute < clearCacheMinute) return;
		  
		clearCacheDate = date;
		  
		boolean clearCacheResult = cacheService.creatOrReplaceDb();
		logger.info("Clear data cache: " + clearCacheResult);
    }
    
    public void sync() {
        syncData(dailySaleReportSolrUrl);
        syncData(accountPortfolioSolrUrl);
    }

    private void syncData(String url) {
        try {
            logger.info("Start sync data from warehouse");
            HttpSolrClient solrClient = SolrClientCreator.create(url, null, null, 5*60*1000);
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/dataimport");
            params.set("command", "full-import");
            QueryResponse response = solrClient.query(params);
            logger.info("Sync data from warehouse " + response.getStatus() + " - " + response.getException());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

}
