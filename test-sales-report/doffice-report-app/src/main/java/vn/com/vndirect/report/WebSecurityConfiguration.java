package vn.com.vndirect.report;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import vn.com.vndirect.vndid.auth.AuthFilter;
import vn.com.vndirect.vndid.auth.AuthenticationService;
import vn.com.vndirect.vndid.auth.LoginFilter;
import vn.com.vndirect.vndid.auth.TokenAuthenticationService;
import vn.com.vndirect.vndid.auth.TokenAuthenticationServiceImpl;
import vn.com.vndirect.vndid.security.RepoSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends RepoSecurityConfigurerAdapter {

//	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfiguration.class);

	private TokenAuthenticationService verifyTokenService;

	private String loginAppURL;

	public WebSecurityConfiguration(HttpClient httpClient, Environment env) {
		super(env.getProperty("domain.vnds", "VNDS"), httpClient, env);

		loginAppURL = env.getProperty("login.app");

		String verifyTokenUrl = env.getProperty("verify.token.url");
		if(StringUtils.isEmpty(verifyTokenUrl)) return;

		verifyTokenService = new TokenAuthenticationServiceImpl(env.getProperty("domain.vnds", "VNDS"), restTemplate, verifyTokenUrl, false);
	}

	@Override
	public AuthFilter createAuthFilter() {
		if(verifyTokenService == null) return new AuthFilter(authenticationService);
		return new AuthFilter(verifyTokenService, authenticationService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403").and().headers().frameOptions().sameOrigin();
		super.configure(http);
	}

	@Override
	public void configure(WebSecurity web) {
		super.configure(web);

		web.ignoring().antMatchers("/log",
				"/health", "/internal/**",
				"/resources/**", "/actuator/**").and().ignoring();
	}

	@Override
	public LoginFilter createLoginFilter() {
		AntPathRequestMatcher loginPathMatcher = new AntPathRequestMatcher("/login");
		AuthenticationService[] authenticationServices = null;
		if(verifyTokenService == null) {
			authenticationServices = new AuthenticationService[] {verifyTokenService, authenticationService};
		} else {
			authenticationServices = new AuthenticationService[] {authenticationService};
		}

		return new LoginFilter(authenticationServices, loginPathMatcher) {

			@Override
			public void doFilter(ServletRequest req, ServletResponse resp, FilterChain arg2) throws IOException, ServletException {
				HttpServletRequest request = (HttpServletRequest) req;
				HttpServletResponse response = (HttpServletResponse)resp;
				logger.info("jsessionId = " + request.getSession().getId());

				if("/logout".equals(request.getServletPath())) {
					redirectToLogout(request, response);
					return;
				}

				Authentication authentication = verifyTokenService.verify(request, response);
				logger.info("token-id = " + authentication.getCredentials() + " ; username = " + authentication.getName());
				if(authentication.isAuthenticated()) {
					String logoutUrl = (String)request.getSession().getAttribute("logoutUrl");
					if(StringUtils.isEmpty(logoutUrl)) {
						logoutUrl = loginAppURL + "/logout?httpReferer=http://" +  request.getHeader("host") + "/";
						request.getSession().setAttribute("logoutUrl", logoutUrl);
					}
					super.doFilter(req, resp, arg2);
					return;
				}

				redirectToLogin(request, response);
			}

			private void redirectToLogin(HttpServletRequest request, HttpServletResponse response ) {
				String url = loginAppURL + "/login?httpReferer=http://" +  request.getHeader("host") + "/";
				response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				response.setHeader("Location", url);
			}


			@Override
			protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authen) throws IOException, ServletException {
				super.successfulAuthentication(req, resp, chain, authen);
				HttpSession httpSession = req.getSession();
				httpSession.setMaxInactiveInterval(8*60*60);
				
				/*List<UserRepoGrantedAuthority> authorities = (List<UserRepoGrantedAuthority>)authen.getAuthorities();
				for(GrantedAuthority authority : authorities) {
					if(authority.getAuthority().endsWith("/EMPLOYEE")) {
						authorities.add(new UserRepoGrantedAuthority("VNDS_DEPARTMENT", "EMPLOYEE"));
					}  else if(authority.getAuthority().endsWith("/MANAGER")) {
						authorities.add(new UserRepoGrantedAuthority("VNDS_DEPARTMENT", "MANAGER"));
					} 
				}*/
			}

			@Override
			public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
				try {
					return super.attemptAuthentication(request, response);
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		};
	}

	private void redirectToLogout(HttpServletRequest request, HttpServletResponse response ) {
		SessionUtils.logout(request);
		String url = loginAppURL + "/logout?httpReferer=http://" +  request.getHeader("host") + "/";
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", url);
		HttpSession session = request.getSession();
		session.invalidate();
	}

	@Override
	public LogoutFilter createLogoutFilter() {
		LogoutAuth logoutAuth = new LogoutAuth(authenticationService);
		logoutAuth.setFilterProcessesUrl("/logout");
		return logoutAuth;
	}

}