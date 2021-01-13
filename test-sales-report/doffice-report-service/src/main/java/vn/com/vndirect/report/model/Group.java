package vn.com.vndirect.report.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Group {

	
	private String code;
	private String fullName;
	
	
	private List<Employee> employees;
}
