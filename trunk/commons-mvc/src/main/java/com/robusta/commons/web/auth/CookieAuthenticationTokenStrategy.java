package com.robusta.commons.web.auth;

import com.google.common.base.Predicate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;

public class CookieAuthenticationTokenStrategy implements AuthenticationTokenStrategy {
    @Override
    public String extractAuthenticationToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            throw new NoSuchElementException("No cookies were sent in this request");
        }
        return getOnlyElement(filter(newArrayList(cookies), new Predicate<Cookie>() {
            @Override
            public boolean apply(Cookie input) {
                return input.getName().equals(AUTH_TOKEN_NAME);
            }
        })).getValue();
    }

    @Override
    public void saveAuthenticationToken(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_TOKEN_NAME, token);
        cookie.setPath("/"); // send of all requests from this context root.
        response.addCookie(cookie);
    }
}
