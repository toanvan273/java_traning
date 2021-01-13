package vn.com.vndirect.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import vn.com.vndirect.vndid.auth.AuthenticationService;


public class LogoutAuth extends LogoutFilter {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogoutAuth.class);
    protected List<VerifyRequest> verifyRequests = new ArrayList<>(3);

    public LogoutAuth(AuthenticationService authenticationService) {
        this(authenticationService, null);
    }
    
    public LogoutAuth(AuthenticationService[] services) {
        this(services, null);
    }

    public LogoutAuth(AuthenticationService authenticationService, VerifyRequest verifier) {
        super(new LogoutSuccessHandler() {
            @SuppressWarnings("unused")
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
            	SessionUtils.logout(request);
            }
        }, new RepoLogoutHandler(authenticationService));
        addVerifyRequest(verifier);
    }
    
    public LogoutAuth(AuthenticationService[] services, VerifyRequest verifier) {
        super(new LogoutSuccessHandler() {
            @SuppressWarnings("unused")
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
            	SessionUtils.logout(request);
            }
        }, new RepoLogoutHandler(services));
        addVerifyRequest(verifier);
    }

    public void addVerifyRequest(VerifyRequest verifier) {
        if (verifier == null) return;
        verifyRequests.add(verifier);
    }

    protected boolean requiresLogout(HttpServletRequest request,
                                     HttpServletResponse response) {
        boolean result = super.requiresLogout(request, response);
        if (!result) return result;

        for (VerifyRequest verifier : verifyRequests) {
            if (verifier == null || verifier.verify(request)) continue;
            LOGGER.error("Verify Logout Request is Failed.");
            return false;
        }

        return true;
    }

    public static interface VerifyRequest {

        public boolean verify(HttpServletRequest request);

    }

    private static class RepoLogoutHandler implements LogoutHandler {

        private List<AuthenticationService> authenticationServices;

        public RepoLogoutHandler(AuthenticationService authenticationService) {
            this.authenticationServices = new ArrayList<>(2);
            if(authenticationService != null) this.authenticationServices.add(authenticationService);
        }
        
        public RepoLogoutHandler(AuthenticationService[] services) {
            this.authenticationServices = new ArrayList<>(2);
            Collections.addAll(authenticationServices, services);
        }

        @SuppressWarnings("unused")
        public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            try {
                for(AuthenticationService service : authenticationServices) {
                    service.logout(request, response);
                }
                SessionUtils.logout(request);
            } catch (Exception e) {
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                try {
                    response.getWriter().write(e.getMessage());
                } catch (Exception e1) {
                    LOGGER.error(e1.toString(), e1);
                }
            }
        }
    }

}