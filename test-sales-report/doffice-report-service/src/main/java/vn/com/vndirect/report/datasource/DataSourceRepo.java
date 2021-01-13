package vn.com.vndirect.report.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.homedirect.common.solr.repository.RepositoryException;
import com.homedirect.common.solr.repository.SolrClientCreator;

import net.sf.jasperreports.engine.data.JRCsvDataSource;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:thuan.nhu@homedirect.com.vn
 * Nov 1, 2017
 */
public abstract class DataSourceRepo implements DisposableBean {

	public final static int MAX_ROW = 10_000;

	private final Logger LOGGER;

	protected HttpSolrClient solrClient;

	protected  String remote;

	public DataSourceRepo(Environment environment, String schemaName) throws MalformedURLException {
		LOGGER = LoggerFactory.getLogger(getClass());

		remote = environment.getProperty("solr.url." + schemaName );
		String username = null;
		String password = null;
		if(StringUtils.isEmpty(remote)) {
			remote = environment.getProperty("solr.url") ;
			if(StringUtils.isEmpty(remote)) throw new RuntimeException("No Remote " + schemaName);

			remote += remote.charAt(remote.length() - 1) == '/' ? schemaName : "/" + schemaName; 
			username = environment.getProperty("solr.username");
			password = environment.getProperty("solr.password");
		} else {
			username = environment.getProperty("solr.username." + schemaName);
			password = environment.getProperty("solr.password." + schemaName);
		}

		this.solrClient = SolrClientCreator.create(remote, username, password, 30*1000);
	}

	public QueryResponse query(SolrQuery solrQuery, int currentPage, int pageSize) {
		try {
			pageSize = Math.max(pageSize, MAX_ROW);
			solrQuery.setStart((currentPage - 1) * pageSize);
			solrQuery.setRows(pageSize);

			return solrClient.query(solrQuery);
		} catch(Exception exp) {
			LOGGER.error(exp.getMessage(), exp);
			return null;
		}
	}

	public SolrDocumentList queryAsJson(String query) throws SolrServerException, IOException {		
		SolrDocumentList list = doQuery(query);
		return list;
	}

	public List<Count> queryFacet(String query, String fieldName) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setFacet(true);
		solrQuery.setRows(0);
		solrQuery.addFacetField(fieldName);
		solrQuery.setFacetLimit(1_000_000);
		solrQuery.setFacetMinCount(1);
		QueryResponse response = solrClient.query(solrQuery);
		FacetField facetField = response.getFacetField(fieldName);
		List<Count> lstCount = facetField.getValues();
		return lstCount;
	}
	
	public SolrDocumentList doQuery(String query) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setRows(1_000_000);
		solrQuery.setSort("date", ORDER.asc);

		QueryResponse rsp = solrClient.query(solrQuery);

		SolrDocumentList list = rsp.getResults();
		return list;
	}
	
	public QueryResponse queryGroup(String query, String fieldList, String... groupField) throws SolrServerException, IOException {	
		return doQuery(query, fieldList, groupField);
	} 
	
	public QueryResponse doQuery(String query, String fieldList, String... groupField) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setRows(1_000_000);
		solrQuery.setSort("date", ORDER.asc);
		
		if(!StringUtils.isEmpty(fieldList)) {
			solrQuery.add("fl", fieldList);
		}
		
		if (groupField != null && groupField.length > 0) {
			solrQuery.add("group", "true");
			for (int i = 0; i < groupField.length; i++) {
				solrQuery.add("group.field", groupField[i]);
			}
		}
		QueryResponse rsp = solrClient.query(solrQuery, METHOD.POST);
		return rsp;
	}
	
	public QueryResponse queryCondition(StringBuilder query, String... stringConditions) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery(query.toString());
		solrQuery.setRows(0);
		Hashtable<String, List<String>> conditions = detachCondition(stringConditions);
		Enumeration<String> keys = conditions.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			List<String> values = conditions.get(key);
			for(int i = 0; i< values.size(); i++) {
				solrQuery.add(key, values.get(i));
			}
		}
		QueryResponse response = solrClient.query(solrQuery);
		return response;
	}
	
	protected Hashtable<String, List<String>> detachCondition(String... stringConditions) {
		Hashtable<String, List<String>> conditions = new Hashtable<String, List<String>>();
		for (int i = 0; i < stringConditions.length; i++) {
			String[] stringCondition = stringConditions[i].split("&");
			for(int j = 0; j< stringCondition.length; j++) {
				int index = stringCondition[j].indexOf("=");
				String key = stringCondition[j].substring(0, index);
				String value = stringCondition[j].substring(index + 1);
				if (conditions.containsKey(key)) {
					List<String> values = conditions.get(key);
					values.add(value);
					conditions.put(key, values);
				} else {
					List<String> values = new ArrayList<String>();
					values.add(value);
					conditions.put(key, values);
				}
			}
		}
		return conditions;
	}

	public JRCsvDataSource queryAsJRCSVStream(String query, String[] columns) throws UnsupportedEncodingException {
		return new JRCsvDataSource(queryAsCSVStream(query, columns), "utf8");
	}

	public InputStream queryAsCSVStream(String query, String[] columns) {
		try {
			String url = remote + "/select?q=" + URLEncoder.encode(query, "utf8") + "&wt=csv&fl=" + String.join(",", columns); 
			LOGGER.info("Query CSV " + url);
			HttpGet request = new HttpGet(url);
			HttpResponse response = solrClient.getHttpClient().execute(request);

			HttpEntity entity = response.getEntity();
			if (entity != null)  return entity.getContent();
		} catch(Exception exp) {
			LOGGER.error(exp.getMessage(), exp);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <X> X getFieldValue(String id, String field) {
		SolrQuery solrQuery = new  SolrQuery();
		solrQuery.setQuery("id:" + id);
		solrQuery.setFields("id", field);
		solrQuery = solrQuery.setStart(0);

		try {
			QueryResponse rsp = solrClient.query(solrQuery);
			Iterator<SolrDocument> iterator = rsp.getResults().iterator();
			return iterator.hasNext() ? (X)iterator.next().getFieldValue(field) : null;
		} catch(Exception exp) {
			LOGGER.error(exp.getMessage(), exp);
			return null;
		}
	}

	public List<Object> getMutilFieldValue(String id, String...fields) {
		SolrQuery solrQuery = new  SolrQuery();
		solrQuery.setQuery("id:" + id);
		solrQuery.setFields(fields);
		solrQuery = solrQuery.setStart(0);

		try {
			QueryResponse rsp = solrClient.query(solrQuery);
			Iterator<SolrDocument> iterator = rsp.getResults().iterator();
			if(!iterator.hasNext()) return Collections.emptyList();

			SolrDocument doc = iterator.next();
			List<Object> result = new ArrayList<>(fields.length);
			for(String field : fields) {
				result.add(doc.getFieldValue(field));
			}
			return result;
		} catch(Exception exp) {
			LOGGER.error(exp.getMessage(), exp);
			return Collections.emptyList();
		}
	}

	public Collection<Object> getFieldValues(String id, String field) {
		SolrQuery solrQuery = new  SolrQuery();
		solrQuery.setQuery("id:" + id);
		solrQuery.setFields("id", field);
		solrQuery = solrQuery.setStart(0);

		try {
			QueryResponse rsp = solrClient.query(solrQuery);
			Iterator<SolrDocument> iterator = rsp.getResults().iterator();
			return iterator.hasNext() ? iterator.next().getFieldValues(field) : null;
		} catch(Exception exp) {
			LOGGER.error(exp.getMessage(), exp);
			return null;
		}
	}

	public boolean exist(String id) throws RepositoryException {
		SolrQuery solrQuery = new  SolrQuery();
		solrQuery.setQuery("id:" + id);
		solrQuery.setFields("id");
		solrQuery.setStart(0);
		solrQuery.setRows(1);

		try {
			QueryResponse rsp = solrClient.query(solrQuery);
			Iterator<SolrDocument> iterator = rsp.getResults().iterator();
			return iterator.hasNext();
		} catch(Exception exp) {
			throw new RepositoryException(RepositoryException.SERVER_ERROR, exp);
		}
	}

	public String deleteByQuery(String query) throws RepositoryException {
		try {
			UpdateResponse response = solrClient.deleteByQuery(query);
			solrClient.commit(false, false);
			String deleteStatus = String.valueOf(response.getStatus());
			if(!"0".equals(deleteStatus)) return deleteStatus;
			return postDeleteByQuery(query);
		} catch (SolrServerException | IOException e) {
			throw new RepositoryException(RepositoryException.SERVER_ERROR, e);
		}
	}

	public String deleteById(String id) throws RepositoryException {
		try {
			UpdateResponse response = solrClient.deleteById(id);
			solrClient.commit(false, false);
			String deleteStatus = String.valueOf(response.getStatus());
			if(!"0".equals(deleteStatus)) return deleteStatus;
			return postDeleteByQuery("id:" + id);
		} catch (SolrServerException | IOException e) {
			throw new RepositoryException(-1, e);
		}
	}

	public String deleteById(List<String> ids) throws RepositoryException {
		try {
			UpdateResponse response = solrClient.deleteById(ids);
			solrClient.commit(false, false);
			String deleteStatus = String.valueOf(response.getStatus());
			if(!"0".equals(deleteStatus)) return deleteStatus;

			StringBuilder builder = new StringBuilder();
			for(String id : ids) {
				if(builder.length() > 0) builder.append('\n');
				builder.append(deleteById(id));
			}
			return builder.toString();
		} catch (SolrServerException | IOException e) {
			throw new RepositoryException(RepositoryException.SERVER_ERROR, e);
		}
	}

	public String postDeleteByQuery(String query) throws RepositoryException {
		StringBuilder builder = new StringBuilder();
		builder.append("<add><delete><query>").append(query).append("</query></delete></add>");
		try {
			HttpPost httpPost = new HttpPost(remote + "/update");
			httpPost.setEntity(new StringEntity(builder.toString()));
			httpPost.addHeader("Content-type", "text/xml");

			HttpClient client = solrClient.getHttpClient();
			HttpResponse response = client.execute(httpPost);

			return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			throw new RepositoryException(-1, e);
		}
	}

	public Integer countAll() throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery("*:*");
		solrQuery.setRows(0);

		Long total = solrClient.query(solrQuery).getResults().getNumFound();
		return total.intValue();
	}

	@Override
	public void destroy() throws Exception {
		solrClient.close();
		LOGGER.info(solrClient.getBaseURL() + ": Shutdown done!");
	}

}
