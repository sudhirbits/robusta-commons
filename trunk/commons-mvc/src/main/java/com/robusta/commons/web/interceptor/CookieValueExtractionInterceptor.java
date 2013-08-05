package com.robusta.commons.web.interceptor;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newArrayList;

public abstract class CookieValueExtractionInterceptor extends PostHandleModelAttributesInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieValueExtractionInterceptor.class);

    protected abstract String cookieName();

    protected String extractCookieValueFromRequestWhenExistsOrNull(HttpServletRequest request) {
        String selectedCustomerCode = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            Collection<Cookie> cookieWithSelectedCustomer = filter(newArrayList(cookies), new Predicate<Cookie>() {
                @Override
                public boolean apply(Cookie input) {
                    return input.getName().equals(cookieName());
                }
            });
            if(cookieWithSelectedCustomer.size() > 0) {
                selectedCustomerCode = getFirst(cookieWithSelectedCustomer, null).getValue();
            }
        }
        LOGGER.debug("Selected cookie value from request cookies: '{}'", selectedCustomerCode);
        return selectedCustomerCode;
    }
}
