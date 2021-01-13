package vn.com.vndirect.report.datasource;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public abstract class Statistic {

	public static final String ACCOUNT_NO_FIELD_NAME = "acctNo";
	public static final String ACTIVE_FLAG_FIELD_NAME = "activeFlag";
	public static final String DATE_FIELD_NAME = "date";
	public static final String SYMBOL_FIELD_NAME = "symbol";
	public static final String QUERY_LAST_DATE_NAME = "queryLastDate";
	public static final String QUERY_PRODUCT_LIST = "queryProductList";
	
	public static final String SALE_HR_CODE = "hrCode";
	public static final String MANAGER_HR_CODE = "managerHrCode";
	public static final String GROUP_ID_USERID = "groupIdUserid";
	public static final String SALE_FULL_NAME = "saleFullName";

	public abstract SolrDocumentList stats(SolrDocumentList list);
	
	protected void sum(Map<String, Object> values, SolrDocument doc, String fieldName) {
		sumIf(values, doc, fieldName, fieldName, null, null);
	}
	
	protected void sumIf(Map<String, Object> values, SolrDocument doc, String srcFieldName, String destFieldName, String conditionFieldName, Object conditionValue) {
		// Lay ra gia tri theo fieldName
		Object value = doc.getFieldValue(srcFieldName);
		if(value == null) return;
		
		// kiem tra co thoa man dieu kien condition hay khong, neu khong thoa man thi bo qua
		if(conditionFieldName != null) {
			Object conditionFieldValue = doc.getFieldValue(conditionFieldName);
			if(!sameValue(conditionValue, conditionFieldValue)) return;
		}
		
		// neu chua co field nay trong ket qua thi put vao luon
		if(!values.containsKey(destFieldName)) {
			values.put(destFieldName, value);
			return;
		} 
		
		// neu da co field nay thi cong value vao gia tri hien tai
		Object currentValue = values.get(destFieldName);
		if(currentValue instanceof Integer) {
			values.put(destFieldName, (int) currentValue + (int) value);
			return;
		}
		if(value instanceof Double) {
			values.put(destFieldName, (double) currentValue + (double) value);
			return;
		}
	}
	
	/*
	 * Kiem tra xem 2 object co cung gia tri hay khong?
	 * neu ca 2 deu bang null -> return true
	 */
	private boolean sameValue(Object object, Object otherObject) {
		// neu 1 object != null thi so sanh voi object con lai
		if(object != null) return object.equals(otherObject);
		
		// neu ca 2 object deu la null thi coi nhu bang nhau -> return true
		if(otherObject == null) return true;
		
		// object == null va otherObject != null -> return false
		return false;
	}
	
	protected void update(Map<String, Object> values, SolrDocument doc, String srcFieldName, String destFieldName, boolean overwrite) {
		Object value = doc.getFieldValue(srcFieldName);
		if(!values.containsKey(destFieldName) && value != null) {
			values.put(destFieldName, value);
			return;
		}
		if(!overwrite) return;
		
		if(value == null) {
			values.remove(destFieldName);
			return;
		}
		values.put(destFieldName, value);
	}
	
	protected void update(Map<String, Object> values, SolrDocument doc, String fieldName, boolean overwrite) {
		update(values, doc, fieldName, fieldName, overwrite);
	}
	
	protected void update(Map<String, Object> values, SolrDocument doc, String fieldName) {
		update(values, doc, fieldName, fieldName, true);
	}
	
	protected void update(Map<String, Object> values, SolrDocument doc, String srcFieldName, String destFieldName) {
		update(values, doc, srcFieldName, destFieldName, true);
	}
	
	protected SolrDocumentList convertToDocumentList(Hashtable<String, Hashtable<String, Object>> accountStatistic) {
		SolrDocumentList summaryList = new SolrDocumentList();
		Enumeration<String> accNos = accountStatistic.keys();
		while(accNos.hasMoreElements()) {
			String accNo = accNos.nextElement();
			SolrDocument newDoc = new SolrDocument(accountStatistic.get(accNo));
			summaryList.add(newDoc);
		}
		return summaryList;
	}
	
}
	