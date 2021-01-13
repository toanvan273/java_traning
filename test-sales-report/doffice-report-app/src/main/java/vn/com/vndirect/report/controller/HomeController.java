package vn.com.vndirect.report.controller;

import static vn.com.vndirect.report.datasource.PermissionFactory.HOME_PERMISSION;
import static vn.com.vndirect.report.support.DailyReport.REDIRECT_403;
import static vn.com.vndirect.report.support.DailyReport.REDIRECT_DAILY_MANAGER_REPORT_MANAGER;
import static vn.com.vndirect.report.support.DailyReport.REDIRECT_DAILY_REVENUE_REPORT;
import static vn.com.vndirect.report.support.DailyReport.REDIRECT_DAILY_SALE_REPORT_STAFF;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import vn.com.vndirect.report.model.UserInfo;
import vn.com.vndirect.report.service.DataReportExporter;
import vn.com.vndirect.report.service.DataReportFiller;
import vn.com.vndirect.report.service.Roles;

@Controller
public class HomeController extends AbstractReportController {
	
	private static final String ADMIN_ROLE = "ROLE_VNDS/ADMIN";

	public HomeController(DataReportFiller dataReportFiller, DataReportExporter dataExporter, Environment env) {
		super(dataReportFiller, dataExporter);
		setRequiredPermission(HOME_PERMISSION);
	}

	@RequestMapping(method = RequestMethod.GET, value = {"", "/"})
	public ModelAndView redirectToView(HttpSession session, Authentication authentication) {	
		if(isAdmin(authentication)) return new ModelAndView(REDIRECT_DAILY_REVENUE_REPORT);
			
		UserInfo userInfo = validate(session, authentication);

		String memberShipType = userInfo.getMemberShipType();

		if(StringUtils.isEmpty(memberShipType)) return new ModelAndView(REDIRECT_403);

		if (memberShipType.startsWith(Roles.DIRECTOR)) return new ModelAndView(REDIRECT_DAILY_MANAGER_REPORT_MANAGER);

		if (memberShipType.startsWith(Roles.MANAGER_ROLE)) return new ModelAndView(REDIRECT_DAILY_MANAGER_REPORT_MANAGER);

		if (Roles.TEAM_LEADER.equals(memberShipType)) return new ModelAndView(REDIRECT_DAILY_MANAGER_REPORT_MANAGER);

		if (Roles.STAFF_ROLE.equals(memberShipType)) return new ModelAndView(REDIRECT_DAILY_SALE_REPORT_STAFF);

		return new ModelAndView(REDIRECT_403);
	}	


	private boolean isAdmin(Authentication authentication) {
		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		for(String role : roles) {
			switch (role) {
			case ADMIN_ROLE:
				return true;

			default:
				break;
			}
		}
		return false;
	}
}
