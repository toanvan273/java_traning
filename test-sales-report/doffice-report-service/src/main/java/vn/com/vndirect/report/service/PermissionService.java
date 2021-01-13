package vn.com.vndirect.report.service;

import org.springframework.stereotype.Service;

import vn.com.vndirect.report.datasource.PermissionFactory;
import vn.com.vndirect.report.model.Permission;

@Service
public class PermissionService {

	public Permission getPermission(String memberShipType) {
		Permission permission = PermissionFactory.createPermission(memberShipType);
		return permission;
	}
}
