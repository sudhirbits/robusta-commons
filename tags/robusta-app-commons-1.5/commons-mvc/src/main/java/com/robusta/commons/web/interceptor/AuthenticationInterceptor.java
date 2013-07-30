package com.robusta.commons.web.interceptor;

import com.robusta.commons.auth.api.AuthenticationFailedException;
import com.robusta.commons.auth.api.UserAuthentication;
import com.robusta.commons.context.UserContextHolder;
import com.robusta.commons.domain.user.UserSession;
import com.robusta.commons.web.auth.AuthenticationTokenStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * User session is identified by an auth token.
 * This interceptor uses cookie based auth token retrieval and
 * looks up the user session. If a session is not obtained, the
 * user is redirected to the login page to get authenticated.
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private UserAuthentication authentication;
    private AuthenticationTokenStrategy tokenStrategy;

    public AuthenticationInterceptor(UserAuthentication authentication, AuthenticationTokenStrategy tokenStrategy) {
        this.authentication = authentication;
        this.tokenStrategy = tokenStrategy;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handlerIsAnnotatedToSkipAuthentication(handler)) {
            LOGGER.debug("Skipping authentication for authentication disabled controller methods on: {}", getControllerClass(handler));
            return true;
        }
        String authToken;
        UserSession userSession;
        try {
            authToken = tokenStrategy.extractAuthenticationToken(request);
            userSession = authentication.lookupUserSessionByToken(authToken);
        } catch (NoSuchElementException e) {
            LOGGER.debug("Unable to retrieve authentication token from request. User need to login. Redirecting");
            whenAuthenticationTokenIsMissingUserNeedsToLogin(response);
            return false;
        } catch (AuthenticationFailedException e) {
            LOGGER.debug("Unable to retrieve user session for token from request. User need to login. Redirecting");
            whenSessionLookupForAuthenticationTokenFailsUserNeedsToLogin(response);
            return false;
        }

        UserContextHolder.setCurrentUser(userSession.loggedUser());
        return super.preHandle(request, response, handler);
    }

    protected void whenSessionLookupForAuthenticationTokenFailsUserNeedsToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("login.html?invsess=true");
    }

    protected void whenAuthenticationTokenIsMissingUserNeedsToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("login.html");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContextHolder.clear();
        super.postHandle(request, response, handler, modelAndView);
    }

    private Class getControllerClass(Object handler) {
        if(!HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            throw new RuntimeException(String.format("This version of authentication interceptor works only " +
                    "with handler class: %s, but received: %s", HandlerMethod.class, handler.getClass()));
        }
        return ((HandlerMethod)handler).getBean().getClass();
    }

    private boolean handlerIsAnnotatedToSkipAuthentication(Object handler) {
        return getControllerClass(handler).isAnnotationPresent(AuthDisabled.class);
    }
}
