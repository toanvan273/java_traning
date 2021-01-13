package vn.com.vndirect.report.service;

import static vn.com.vndirect.report.datasource.PermissionFactory.NOCAREBY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.homedirect.common.model.HdrResponse;
import com.homedirect.common.model.Page;

import vn.com.vndirect.report.model.Group;
import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.vndid.response.GroupProfileResponse;
import vn.com.vndirect.vndid.response.UserProfileResponse;
import vn.com.vndirect.vndid.response.relationship.MembershipResponse;
import vn.com.vndirect.vndid.service.GroupProfileService;
import vn.com.vndirect.vndid.service.SearchService;
import vn.com.vndirect.vndid.service.UserProfileService;
import vn.com.vndirect.vndid.service.request.SearchRequest;

@Service
@RefreshScope
public class RoleService {

	private final static String ROLE_MEMBERSHIP_ATTR = "role-memberships";

	private final static Logger logger = LoggerFactory.getLogger(RoleService.class);

	@Autowired
	protected SearchService searchService;

	@Autowired
	private GroupProfileService groupProfileService;

	@Autowired
	protected UserProfileService userProfileService;

	private Map<String,String> testAccounts;

	@Autowired
	public RoleService(Environment environment) {
		testAccounts = new TreeMap<>();
		loadTestAccounts(environment);
	}

	/**
	 * Description: get hrcode of sale
	 * @param username
	 * @return hrcode
	 */
	private String getHRCode(String username) {
		HdrResponse<UserProfileResponse> response = userProfileService.getByUserName(username);
		return response.getData().getDynamicField().getProperty("hrcode");
	}
	
	public UserInfo getUserInfo(String userName) {
		UserInfo userInfo = new UserInfo();
		
		userInfo.setUserName(userName);
		
		String hrCode = getHRCode(userName);
		userInfo.setHrCode(hrCode);
		
		String query = Roles.createMembershipQueryByUser(userName);
		List<MembershipResponse> membership = loadMemberships(query);
		userInfo.setMembership(membership);
		
		if(!membership.isEmpty()) {
			String memberShipType = membership.get(0).getMembershipType();
			String queryCareby =  Roles.createMembershipCareByQueryByUser(userName);
			List<MembershipResponse> membershipCareBy = loadMemberships(queryCareby);
			if(membershipCareBy.isEmpty()) {
				memberShipType = memberShipType + NOCAREBY;
			}
			userInfo.setMemberShipType(memberShipType);
		}
		
		List<Group> groups = new ArrayList<Group>();
		for(int i = 0; i < membership.size(); i++) {
			String groupId = membership.get(i).getGroupName();
			Group group = getGroup(groupId);
			groups.add(group);
		}
		userInfo.setGroups(groups);
		return userInfo;
	}
	
	public UserInfo getUserInfoForTest(String userName) {
		if(testAccounts.containsKey(userName)) {
			userName = testAccounts.get(userName);
		}
		return getUserInfo(userName);
	}
	
	private List<MembershipResponse> loadMemberships(String query) {
		logger.info("Query Memberships " + query);
		SearchRequest request = new SearchRequest(query);
		request.setPageNumber(1);
		request.setPageSize(1000);
		HdrResponse<Page<MembershipResponse>> hdr = searchService.searchMembership(request);
		if(hdr == null) {
			logger.error("No response for query memberships!");
			return Collections.emptyList();
		}
		if ( hdr.getData() == null || hdr.getData().getPageItems() == null) {
			logger.error("Load membership errors -" + hdr.getCode() + " - " + hdr.getDescription());
			return Collections.emptyList();
		} 
		return hdr.getData().getPageItems();
	}

	@SuppressWarnings("unchecked")
	private List<MembershipResponse> loadSessionRoles(HttpSession session, Authentication authentication) {
		List<MembershipResponse> memberships = (List<MembershipResponse>)session.getAttribute(ROLE_MEMBERSHIP_ATTR);
		if(memberships != null) return memberships;

		String userName = authentication.getName();

		String query = Roles.createMembershipQueryByUser(userName);
		memberships = loadMemberships(query);
		session.setAttribute(ROLE_MEMBERSHIP_ATTR, memberships);
		return memberships;
	}

	public boolean isRole(HttpSession session, Authentication authentication, String role) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for(GrantedAuthority authority : authorities) {
			logger.info("Authority " + role + " : " + authority.getAuthority());
			if(authority.getAuthority().endsWith("/" + role)) {
				//addRole(authentication, "VNDS_DEPARTMENT", role);
				return true;
			}
		}
		List<MembershipResponse> memberships =  loadSessionRoles(session, authentication);
		for(MembershipResponse membership : memberships) {
			logger.info("Role " + role + " : " + membership.getMembershipType());
			if(role.equals(membership.getMembershipType())) {
				//addRole(authentication, "VNDS_DEPARTMENT", role);
				return true;
			}
		}
		return false;
	}

	private Group getGroup(String code) {
		Group group = new Group();
		group.setCode(code);
		HdrResponse<GroupProfileResponse> groupProfileRsp = groupProfileService.getByGroupName(code);
		if(groupProfileRsp != null && groupProfileRsp.getData() != null) {
			group.setFullName(groupProfileRsp.getData().getFullName());
		}
		return group;
	}

	private void loadTestAccounts(Environment environment) {
		String value = environment.getProperty("test.accounts");
		if(StringUtils.isEmpty(value)) return;
		
		testAccounts = Stream.of(value.split(" "))
				.map(x -> x.split("-"))
				.collect(Collectors.toMap(x -> x[0], x -> x[1], (oldValue, newValue) -> newValue, TreeMap<String, String>::new));
	}
}
