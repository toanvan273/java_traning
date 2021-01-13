package vn.com.vndirect.report.datasource;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class AccountPortfolioRepo extends DataSourceRepo {

	@Autowired
	public AccountPortfolioRepo(Environment environment) throws MalformedURLException {
		super(environment, "account-portfolio");
	}
}
