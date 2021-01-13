/***************************************************************************
 * Copyright 2016 by HomeDirect - All rights reserved.                *    
 **************************************************************************/
package vn.com.vndirect.report.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homedirect.common.util.StringSplitter;
import com.homedirect.common.util.TextSpliter;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import vn.com.vndirect.report.model.Group;
import vn.com.vndirect.report.model.Permission;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.PermissionService;
import vn.com.vndirect.report.support.StaffReportUtils;
import vn.com.vndirect.vndid.response.relationship.MembershipResponse;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:thuan.nhu@homedirect.com.vn
 * Nov 21, 2018
 */
//thuannd review khong dung setter va getter trong controller nhe
@Getter
@Setter
public abstract class AbstractReportController extends AbstractController {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

	@Value("${pdf-content}")
	private String pdfContent;

	@Value("${excel-content}")
	private String excelContent;

	@Value("${html-content}")
	private String htmlContent;

	@Value("${page-size}")
	private int pageSize;

	@Value("${daily}")
	private String dailyReportDownloadFileName;

	@Value("${portfolio}")
	private String portfolioDownloadFileName;

	@Value("${nav_average}")
	private String navAverageDownloadFileName;

	@Value("${aum_average}")
	private String aumAverageDownloadFileName;

	@Value("${nav_endperiod}")
	private String navEndperiodDownloadFileName;

	@Value("${aum_endperiod}")
	private String aumEndperiodDownloadFileName;

	@Value("${nsr}")
	private String nsrDetailDownloadFileName;

	protected DataReportFiller dataReportFiller;

	protected DataReportExporter dataExporter;

	private int requiredPermission;

	@Autowired
	private PermissionService permissionService;

	public AbstractReportController(DataReportFiller dataReportFiller, DataReportExporter dataExporter) {
		this.dataReportFiller = dataReportFiller;
		this.dataExporter = dataExporter;
	}

	public JsonDataSource convertToJson(SolrDocumentList solrDocumentList) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(solrDocumentList);
		ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes("utf8"));
		//Create json datasource from json stream
		return new JsonDataSource(jsonDataStream);
	}

	/*public void initData(String fileName, JsonDataSource ds, Map<String, Object> parameters) throws JRException {
		initData(fileName, ds, parameters, 0, 0, 0);
	}*/

	/**
	 * Description: init jrxml file, ds
	 * @param fileName
	 * @param ds
	 * @param parameters
	 * TODO
	 * @throws JRException
	 */

	public void initData(String fileName, JsonDataSource ds, Map<String, Object> parameters) throws JRException {
		dataReportFiller.setReportFileName(fileName);
		long timeStart = System.currentTimeMillis();
		dataReportFiller.compileReport();
		long timeEnd = System.currentTimeMillis();
		LOGGER.info("thoi gian compile report:" + (timeEnd-timeStart));
		
		dataReportFiller.setDataSource(ds);
		timeStart = System.currentTimeMillis();
		timeEnd = System.currentTimeMillis();
		
		/*LOGGER.info("thoi gian fill report:" + (timeEnd-timeStart));		
		
		if (totalPage != 0 && totalPage == pageNum) {
			JasperPrint jasperPrint = dataReportFiller.getJasperPrint();
			int pageHeight = (totalRecord % pageSize + 2)*30 + 40;
			jasperPrint.setPageHeight(pageHeight);
		}		
		dataExporter.setJasperPrint(dataReportFiller.getJasperPrint());*/

		LOGGER.info("thoi gian fill report:" + (timeEnd-timeStart));
		dataExporter.setJasperPrint(dataReportFiller.getJasperPrint(parameters));
	}	

	/**
	 * Description: export data with specific format
	 * @param type
	 * @param response
	 * @param authentication
	 * TODO
	 * @throws IOException 
	 */
	public void exportData(String type, 
							HttpServletResponse response, 
							Authentication authentication, 
							HttpSession session, 
							String nameReport, 
							boolean appendHeaderFooter, 
							int pageNumber) throws Exception {
		switch (type) {
		case "pdf":
			response.setContentType(pdfContent);
			dataExporter.exportToPdf(response.getOutputStream(), authentication.getName());
			break;

		case "excel":
			response.setContentType(excelContent);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + nameReport);
			dataExporter.exportToXlsx(response.getOutputStream(), authentication.getName());
			break;

		default:
			if(appendHeaderFooter) {
				SimpleHtmlExporterConfiguration shec = new SimpleHtmlExporterConfiguration();
				shec.setHtmlHeader(StaffReportUtils.getHtmlHeader());
				shec.setHtmlFooter(StaffReportUtils.getHtmlFooter(session));
				SimpleHtmlReportConfiguration shrc = null;
				if(pageNumber > 0) {
					shrc = new SimpleHtmlReportConfiguration();
					shrc.setPageIndex(pageNumber-1);
				}
				response.setContentType(htmlContent);
				dataExporter.exportToHtml(shec, shrc, response.getOutputStream());
			} else {
				response.setContentType(htmlContent);
				dataExporter.exportToHtml(response.getOutputStream());
			}
			break;
		}
	}

	public void exportData(String type, 
							HttpServletResponse response,
							Authentication authentication, 
							HttpSession session, String nameReport, int pageNum) throws Exception {
		exportData(type, response, authentication, session, nameReport, false, pageNum);
	}

	/**
	 * Description: get total page
	 * @param totalRecordSize
	 * @return
	 * TODO
	 */
	public int getTotalPage(int totalRecordSize) {
		int totalPage = totalRecordSize / pageSize;
		if (totalRecordSize % pageSize != 0) totalPage += 1;
		totalPage = Math.max(totalPage, 1);	
		return totalPage;
	}

	/**
	 * Description: check permission of use can access this screen right or not?
	 * @param session
	 * @param authentication
	 * @param screenPermission
	 * @return
	 * TODO
	 */
	public boolean isAccess(Permission permission) {
		if ((permission.getPermision() & requiredPermission) == requiredPermission) return true;
		return false;
	}

	/**
	 * Description: get list group id is managed by user
	 * @param session
	 * @param authentication
	 * TODO
	 */
	public List<String> getGroupId(List<MembershipResponse> memberships) {
		List<String> groupIds = new ArrayList<>();
		for (int i = 0; i < memberships.size(); i++) {
			groupIds.add(memberships.get(i).getGroupName());
		}
		return groupIds;
	}

	public UserInfo validate(HttpSession session, Authentication authentication) {
		String userName = authentication.getName();		
		UserInfo userInfo = roleService.getUserInfoForTest(userName);

		String memberShipType = userInfo.getMemberShipType();	
		Permission permission = getPermissionService().getPermission(memberShipType);
		if(!isAccess(permission)) return new UserInfo();
		userInfo.setPermission(permission);
		return userInfo;
	}

	protected boolean isValidGroupIdUserid(List<Group> groupIdSessions, String groupIdRequest) {
		//thuannd review dung ham nay
		String[] groupIdRequests = StringSplitter.toArray(groupIdRequest, ' ');// groupIdRequest.split(" ");

		Hashtable<String, String> groupIdValid = new Hashtable<>();
		for (int i = 0; i < groupIdSessions.size(); i++) {
			groupIdValid.put(groupIdSessions.get(i).getCode(), groupIdSessions.get(i).getFullName());
		}

		
		for (int i = 0; i < groupIdRequests.length; i++) {
			if(!groupIdValid.containsKey(groupIdRequests[i])) {
				return false;
			}
		}
		return true;
	}
}
