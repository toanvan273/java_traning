package vn.com.vndirect.report;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class LoggingRequestFilter extends GenericFilterBean {

	private final static String X_REAL_IP_HEADER = "x-real-ip";

	private final static String X_FORWARDED_FOR_HEADER = "x-forwarded-for";

	private final static String[] IGNORE_PATHS = {
			"/resources/", "favicon.ico"
	};

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String url = getRequestUrl(request);

		if(isIgnorePath(url)) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
//		logger.info("jsessionId logging filter = " + request.getSession().getId() + " ; username = " + SecurityContextHolder.getContext().getAuthentication().getName());
		HttpSession httpSession = request.getSession();
		httpSession.setMaxInactiveInterval(3600);

		String referer = request.getHeader("Referer");
		String realIp  = getRealIp(request);
		logger.info("Real: " + realIp + " - Remote: " + request.getRemoteAddr()  + " - " + (StringUtils.isEmpty(referer) ? "[No Refer]" : referer)  + " - "+ url);
		long start = System.currentTimeMillis();
		try {
			chain.doFilter(servletRequest, servletResponse);
		} finally {
			HttpServletResponse response = (HttpServletResponse) servletResponse;
			logger.info("Real: " + realIp + " - Remote: " + request.getRemoteAddr()  + " - " + url + " - " + response.getStatus() + " - " + (System.currentTimeMillis() - start));
		}

	}

	private String getRequestUrl(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String query = request.getQueryString();
		if(!StringUtils.isEmpty(query)) url += "?" + query;
		int idx = (url = url.toLowerCase()).indexOf("password");
		if(idx < 1) return url;
		return url.substring(0, idx) + "[SENSITIVE DATA]";
	}

	private boolean isIgnorePath(String url) {
		for(String item : IGNORE_PATHS) {
			if(url.contains(item)) return true;
		}
		return false;
	}

	private String getRealIp(HttpServletRequest request) {
		Enumeration<String> _enum = request.getHeaderNames();

		String forwardFor = null;

		while(_enum.hasMoreElements()) {
			String header = _enum.nextElement();
			if(X_REAL_IP_HEADER.equalsIgnoreCase(header)) {
				return request.getHeader(header);
			} else if(X_FORWARDED_FOR_HEADER.equalsIgnoreCase(header)) {
				forwardFor = request.getHeader(header);
			}
		}
		return StringUtils.isEmpty(forwardFor) ? request.getRemoteAddr() : forwardFor;
	}
}