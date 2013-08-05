package com.robusta.commons.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RequestAndResponseLogger implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAndResponseLogger.class);
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logRequest(servletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
        logResponse(servletResponse);
    }

    private void logResponse(ServletResponse servletResponse) {
        try {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            LOGGER.info(String.format("Sending response with status: %s", response.getStatus()));
        } catch(NoSuchMethodError e) { // try handle the java.lang.NoSuchMethodError: javax.servlet.http.HttpServletResponse.getStatus()I in jetty.
            // no use in logging.
        }
    }

    private void logRequest(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if(!isAnInterestingRequestURI(requestURI)) {
            return;
        }
        LOGGER.info("Received '{}' request: '{}'", request.getMethod(), requestURI);
        if(LOGGER.isDebugEnabled()) {
            String queryString = request.getQueryString();
            if(!isNullOrEmpty(queryString))
                LOGGER.debug("URL parameters from request: '{}'", queryString);
        }
    }

    private boolean isAnInterestingRequestURI(String requestURI) {
        return requestURI.endsWith(".html") || requestURI.contains(".html?jsessionid=");
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
