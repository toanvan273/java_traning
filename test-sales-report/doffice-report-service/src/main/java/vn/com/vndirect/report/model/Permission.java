package vn.com.vndirect.report.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {	
	private int permision;
	private List<AccessURL> accessURL;
}
