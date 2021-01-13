package vn.com.vndirect.report.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homedirect.common.model.HdrResponse;
import com.homedirect.common.model.Page;

import vn.com.vndirect.vndid.response.relationship.MembershipResponse;
import vn.com.vndirect.vndid.service.SearchService;
import vn.com.vndirect.vndid.service.request.SearchRequest;

@Service
public class DirectorRoleService {
	
	private final static Logger logger = LoggerFactory.getLogger(DirectorRoleService.class);

	@Autowired
	private SearchService searchService;
	
	/**
	 * Description: get info of director
	 * @param userName
	 * @return
	 * TODO
	 */
	public List<MembershipResponse> loadDepartments(String userName) {
		String query = "membershipType:RELATION_MANAGER AND userName:" + userName;
		
		SearchRequest request = new SearchRequest(query);
		request.setPageNumber(1);
		request.setPageSize(1000);
		HdrResponse<Page<MembershipResponse>> hdr = searchService.searchMembership(request);
		if(hdr.getData() == null) {
			logger.error("Load department errors -" + hdr.getCode() + " - " + hdr.getDescription());
			return Collections.emptyList();
		}
		return hdr.getData().getPageItems();
		
	}

	
	

	
	
}
