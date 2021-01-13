package vn.com.vndirect.report.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.solr.common.SolrDocumentList;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.com.vndirect.report.datasource.mapdb.MapDbRepository;

@Service
public class CacheService <E extends SolrDocumentList> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
	
	public static final String STAFF_SUMMARY = "STAFF_SUMMARY";
	public static final String MANAGER_SUMMARY = "MANAGER_SUMMARY";
	public static final String ACCOUNT_PORTFOLIO = "ACCOUNT_PORTFOLIO";
	
	protected MapDbRepository<E> repo;
	
	@Autowired
	public CacheService(Environment environment) {
		repo = new MapDbRepository<E>(environment, "dor-cache", "dor-cache.db", true);
	}

    public E save(String cacheName, String key, E value) {
    	if (StringUtils.isEmpty(cacheName)) return null;
        if (StringUtils.isEmpty(key)) return null;
        if(value == null) return null;
        
        try {
            return repo.add(cacheName, key, value);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return null;
        }
    }

    public E update(String cacheName, String key, E newValue) {
        try {
            return repo.update(cacheName, key, newValue);
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
            return repo.contains(cacheName, key);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }

    public Optional<E> findOne(String cacheName, String key) {
        try {
            return repo.findOne(cacheName, key);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return Optional.empty();
        }
    }

    public Map<String, E> findAll(String cacheName) {
        try {
            HTreeMap<String, E> all = repo.findAll(cacheName);
            return all;
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
}
