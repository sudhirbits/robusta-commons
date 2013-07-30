package com.robusta.commons.web.filter;

import com.robusta.commons.test.mock.Mock;
import com.robusta.commons.test.mock.MockFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(JMock.class)
public class RequestAndResponseLoggerTest {
    private Mockery mockery = new JUnit4Mockery();
    private Filter filter = new RequestAndResponseLogger();
    @Mock private FilterConfig filterConfig;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        MockFactory.initMocks(this);

    }

    @Test
    public void testInit() throws Exception {
        filter.init(filterConfig);
        assertNotNull(filterConfig);
    }

    @Test
    public void testDoFilter_nonJetty() throws Exception {
        expectingAttributeAccessorCallsOnRequestWillReturnTestValues();
        expectingDoFilerCallOnTheFilterChainWithRequestAndResponse();
        expectingGetStatusCallOnResponseWillReturnStatus();
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testDoFilter_notAnInterestingURI_shouldNotLogRequest() throws Exception {
        expectingGetRequestURIOnRequestWillACssRequest();
        expectingDoFilerCallOnTheFilterChainWithRequestAndResponse();
        expectingGetStatusCallOnResponseWillReturnStatus();
        filter.doFilter(request, response, filterChain);
    }

    private void expectingGetRequestURIOnRequestWillACssRequest() {
        mockery.checking(new Expectations() {{
            oneOf(request).getRequestURI(); will(returnValue("/some/url/on/the/server.css"));
        }});
    }

    private void expectingGetStatusCallOnResponseWillReturnStatus() {
        mockery.checking(new Expectations() {{
            oneOf(response).getStatus(); will(returnValue(200));
        }});
    }

    private void expectingAttributeAccessorCallsOnRequestWillReturnTestValues() {
        mockery.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getRequestURI(); will(returnValue("/some/url/on/the/server.html"));
            allowing(request).getQueryString(); will(returnValue("a=b&c=d&e=f"));
        }});
    }

    @Test
    public void testDoFilter_onJetty_noSuchMethodError() throws Exception {
        expectingAttributeAccessorCallsOnRequestWillReturnTestValues();
        expectingDoFilerCallOnTheFilterChainWithRequestAndResponse();
        expectingGetStatusCallOnResponseWillThrowNoSuchMethodErrorToSimulateJettyBehavior();
        filter.doFilter(request, response, filterChain);
    }

    private void expectingGetStatusCallOnResponseWillThrowNoSuchMethodErrorToSimulateJettyBehavior() {
        mockery.checking(new Expectations() {{
            oneOf(response).getStatus(); will(throwException(new NoSuchMethodError()));
        }});
    }

    private void expectingDoFilerCallOnTheFilterChainWithRequestAndResponse() throws IOException, ServletException {
        mockery.checking(new Expectations() {{
            oneOf(filterChain).doFilter(request, response);
        }});
    }

}