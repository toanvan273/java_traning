package vn.com.vndirect.report.datasource;

import org.apache.commons.lang.StringUtils;
import vn.com.vndirect.report.model.AccessURL;
import vn.com.vndirect.report.model.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionFactory {
	public static final int STAFF_PERMISSION = 0b0011;
	public static final int MANAGER_PERMISSION = 0b0101;
	public static final int DIRECTOR_PERMISSION = 0b1001;
	public static final int HOME_PERMISSION = 0b0001;
	public static final int ADMIN_PERMISSION = 0b00010001;

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String MANAGER = "MANAGER";
	public static final String MANAGER_VOL = "MANAGER_VOL";
	public static final String DIRECTOR = "DIRECTOR";
	public static final String TEAM_LEADER = "TEAMLEADER";
	public static final String NOCAREBY = "_NOCAREBY";
	public static final String PMS_ADMIN_ROLE = "ADMIN";

	public static final String STAFF_URL = "/daily-sale-report/staff";
	public static final String MANAGER_URL = "/daily-manager-report/manager";
	public static final String MANAGER_VOL_URL = "/daily-manager-report/manager-vol";
	public static final String DIRECTOR_URL = "/daily-director-report/director";

	public static final String REVENUE_ADMIN_URL = "/revenue-report/admin";
	public static final String COMMISSION_ADMIN_URL = "/commission-report/admin";

	/**
	 * Description:
	 * @param memberShipType
	 * @return
	 * TODO: remover memberShipType = EMPLOYEE when golive other role
	 */
	public static Permission createPermission(String memberShipType) {
		int authority = 0b000;
		if(StringUtils.isEmpty(memberShipType)) return new Permission();

		List<AccessURL> accessURL = new ArrayList<AccessURL>();
		if (EMPLOYEE.equals(memberShipType)) {
			Permission permission = new Permission();			
			authority = STAFF_PERMISSION;
			permission.setPermision(authority);			
			accessURL.add(new AccessURL(true, STAFF_URL));
			permission.setAccessURL(accessURL);
			return permission;
		} else if (memberShipType.startsWith(MANAGER)) {
			Permission permission = new Permission();
			authority = STAFF_PERMISSION | MANAGER_PERMISSION;
			permission.setPermision(authority);
			if(!memberShipType.endsWith(NOCAREBY)) {
				accessURL.add(new AccessURL(false, STAFF_URL));
			}
			accessURL.add(new AccessURL(true, MANAGER_URL));
			permission.setAccessURL(accessURL);
			return permission;
		} else if (TEAM_LEADER.equals(memberShipType)) {
			Permission permission = new Permission();
			authority = STAFF_PERMISSION | MANAGER_PERMISSION;
			permission.setPermision(authority);
			accessURL.add(new AccessURL(false, STAFF_URL));
			accessURL.add(new AccessURL(true, MANAGER_URL));
			permission.setAccessURL(accessURL);			
			return permission;
		} else if (memberShipType.startsWith(DIRECTOR)) {
			Permission permission = new Permission();			
			authority = STAFF_PERMISSION | MANAGER_PERMISSION | DIRECTOR_PERMISSION;
			permission.setPermision(authority);
			if(!memberShipType.endsWith(NOCAREBY)) {
				accessURL.add(new AccessURL(false, STAFF_URL));
			}
			accessURL.add(new AccessURL(false, MANAGER_URL));
			accessURL.add(new AccessURL(true, DIRECTOR_URL));
			permission.setAccessURL(accessURL);			
			return permission;
		} else if (PMS_ADMIN_ROLE.equals(memberShipType)) {
			Permission permission = new Permission();
			authority = ADMIN_PERMISSION;
			permission.setPermision(authority);
			accessURL.add(new AccessURL(true, REVENUE_ADMIN_URL));
			accessURL.add(new AccessURL(true, COMMISSION_ADMIN_URL));
			permission.setAccessURL(accessURL);
			return permission;
		} else {
			return new Permission();
		}
	}
}
