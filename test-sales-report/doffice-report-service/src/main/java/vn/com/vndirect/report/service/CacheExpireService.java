package vn.com.vndirect.report.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.solr.common.SolrDocumentList;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.com.vndirect.report.datasource.mapdb.MapDbRepository;

public class CacheExpireService<E extends SolrDocumentList> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CacheExpireService.class);
	
	public static final String STAFF_SUMMARY = "STAFF_SUMMARY";
	public static final String MANAGER_SUMMARY = "MANAGER_SUMMARY";
	public static final String ACCOUNT_PORTFOLIO = "ACCOUNT_PORTFOLIO";
	
	protected MapDbRepository<CacheData<E>> repo;
	
	@Autowired
	public CacheExpireService(Environment environment) {
		repo = new MapDbRepository<CacheData<E>>(environment, "dor-cache", "dor-cache.db", false);
	}

    public E save(String cacheName, String key, E value) {
    	if (StringUtils.isEmpty(cacheName)) return null;
        if (StringUtils.isEmpty(key)) return null;
        if(value == null) return null;
        
        try {
        	CacheData<E> data = repo.add(cacheName, key, new CacheData<E>(value));
        	return data == null ? null : data.getData();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return null;
        }
    }

    public E update(String cacheName, String key, E newValue) {
        try {
        	CacheData<E> data = repo.update(cacheName, key, new CacheData<E>(newValue));
            return data == null ? null : data.getData();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return null;
        }
    }

    public boolean delete(String cacheName, String key) {
        try {
            return repo.delete(cacheName, key);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }

    public boolean delete(String cacheName, List<String> keys) {
        try {
            return repo.delete(cacheName, keys);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }

    public boolean contains(String cacheName, String key) {
        try {
            return findOne(cacheName, key).isPresent();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }
    
    public Optional<E> findOne(String cacheName, String key) {
        try {
        	Optional<CacheData<E>> optional = repo.findOne(cacheName, key);
        	if(!optional.isPresent())  return Optional.empty();
        	
        	CacheData<E> data = optional.get();
        	if(data == null) return Optional.empty();
        	if(data.getData() == null || isExpire(data)) {
        		repo.delete(cacheName, key);
        		return Optional.empty();
        	}
        	return Optional.ofNullable(data.getData());
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return Optional.empty();
        }
    }

    public Map<String, E> findAll(String cacheName) {
        try {
            HTreeMap<String, CacheData<E>> all = repo.findAll(cacheName);
            Map<String, E> result = new TreeMap<>();
            Iterator<Map.Entry<String, CacheData<E>>> iterator = all.entrySet().iterator();
            while(iterator.hasNext()) {
            	Map.Entry<String, CacheData<E>> entry = iterator.next();
            	if(entry.getValue() == null) {
            		repo.delete(cacheName, entry.getKey());
            		continue;
            	}
            	if(entry.getValue().getData() == null || isExpire(entry.getValue())) {
            		repo.delete(cacheName, entry.getKey());
            		continue;
            	}
            	result.put(entry.getKey(), entry.getValue().getData());
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return Collections.emptyMap();
        }
    }

    public long countAll(String cacheName) {
        try {
            return repo.countAll(cacheName);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return 0;
        }
    }

    public boolean creatOrReplaceDb() {
        try {
            return repo.creatOrReplace();
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }
    
    private boolean isExpire(CacheData<E> data) {
    	Calendar today = Calendar.getInstance();
    	Calendar cachedAt = Calendar.getInstance();
    	cachedAt.setTimeInMillis(data.getCachedAt());
    	return today.get(Calendar.DATE) != cachedAt.get(Calendar.DATE);
    }
    
    public static class CacheData<E extends Serializable>  implements Serializable {
    	
		private static final long serialVersionUID = 1L;
		private E data;
    	private long cachedAt;
    	
    	public CacheData() {
    		
    	}
    	
    	private CacheData(E data) {
    		this.data = data;
    		this.cachedAt = System.currentTimeMillis();
    	}
    	
    	public E getData() { return data; }
    	public void setData(E data) { this.data = data; }
		
    	public long getCachedAt() { return cachedAt; }
    	public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
    	
    }
}
