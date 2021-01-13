package vn.com.vndirect.report.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManagerRequestData extends RequestData{
	private String groupId;
	private String saleHrCode;
	private Boolean total;
}
