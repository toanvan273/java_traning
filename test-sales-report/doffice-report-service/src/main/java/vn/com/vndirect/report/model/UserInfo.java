package vn.com.vndirect.report.model;

import java.util.List;


import lombok.Getter;
import lombok.Setter;
import vn.com.vndirect.vndid.response.relationship.MembershipResponse;

@Setter
@Getter
public class UserInfo {
	private String userName;
	private String hrCode;
	private String memberShipType;
	private Permission permission;
	private List<MembershipResponse> membership;
	private List<Group> groups;
	
	public boolean valid() {
		return permission != null;
	}
}
