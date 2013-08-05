package com.robusta.commons.web.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

/**
 * Provides a method to lookup auth token from the web request and
 * saving a token against the response.
 */
public interface AuthenticationTokenStrategy {
    public static final String AUTH_TOKEN_NAME = "authn_token";
    String extractAuthenticationToken(HttpServletRequest request) throws NoSuchElementException;
    void saveAuthenticationToken(String token, HttpServletResponse response);
    
}
