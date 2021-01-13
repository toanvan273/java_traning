package vn.com.vndirect.report;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtils {

	private final static Logger logger = LoggerFactory.getLogger(SessionUtils.class);

	public static void logout(HttpServletRequest request) {
		try {
			Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
			if(authentication != null) authentication.setAuthenticated(false);
			SecurityContextHolder.getContext().setAuthentication(null);
			SecurityContextHolder.clearContext();
			HttpSession session = request.getSession();
			Enumeration<String> e = session.getAttributeNames();
			while (e.hasMoreElements()) {
				String attr = e.nextElement();
				session.setAttribute(attr, null);
				session.removeAttribute(attr);
			}
			logger.info(" session id =  ---------------> " + session.getId() + " : " + session.getAttributeNames());
			removeCookies(request);
			session.invalidate();
			session.setMaxInactiveInterval(0);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	public static void removeCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0);
			}
		}
	}
}