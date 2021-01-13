package vn.com.vndirect.report.service;

public class Roles {
	
	public static final String AND_MEMBERSHIP_TYPE = " AND membershipType:";

	public static final String AND_USER_NAME = " AND userName:";

	public final static String DOMAIN_STAFF_TITLE = "VND_STAFF_TITLE";
	
	public final static String CUSTOMER_ACCOUNT_RELATIONSHIP = "CUSTOMER_ACCOUNT_RELATIONSHIP";
	
	public final static String STAFF_ROLE = "EMPLOYEE";
	
	public final static String MANAGER_ROLE = "MANAGER";
	
	public final static String TEAM_LEADER = "TEAMLEADER";
	
	public final static String DIRECTOR = "DIRECTOR";
	
	public final static String HRCODE = "hrcode";
	
	public final static String DOMAIN = "domain:";
	
	public final static String GROUP_ID = "groupId";
	
	public final static String GROUP = "group";
	
	public static String createMembershipQueryByUser(String role, String userName) {
		userName = userName.replace("\\", "\\\\");
		return DOMAIN +  DOMAIN_STAFF_TITLE + AND_MEMBERSHIP_TYPE + role + AND_USER_NAME + userName;
	}
	
	public static String createMembershipQueryByDepartment(String role, String department) {
		return DOMAIN +  DOMAIN_STAFF_TITLE + AND_MEMBERSHIP_TYPE + role + " AND groupName:" + department;
	}
	
	public static String createMembershipQueryByUser(String userName) {
		userName = userName.replace("\\", "\\\\");
		return DOMAIN +  DOMAIN_STAFF_TITLE + AND_USER_NAME + userName;
	}
	
	public static String createMembershipCareByQueryByUser(String userName) {
		userName = userName.replace("\\", "\\\\");
		return DOMAIN + CUSTOMER_ACCOUNT_RELATIONSHIP + AND_USER_NAME + userName;
	}
}
