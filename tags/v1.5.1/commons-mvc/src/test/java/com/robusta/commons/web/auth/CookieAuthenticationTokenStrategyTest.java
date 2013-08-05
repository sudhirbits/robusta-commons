package com.robusta.commons.web.auth;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

import static com.robusta.commons.web.auth.AuthenticationTokenStrategy.AUTH_TOKEN_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class CookieAuthenticationTokenStrategyTest {
    private AuthenticationTokenStrategy tokenStrategy;
    private HttpServletRequest request;
    private Mockery mockery;
    private Cookie cookie;
    private HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        mockery = new JUnit4Mockery();
        tokenStrategy = new CookieAuthenticationTokenStrategy();
        request = mockery.mock(HttpServletRequest.class);
        response = mockery.mock(HttpServletResponse.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void testExtractAuthenticationToken_noCookiesArePresentInRequest_shouldThrowException() throws Exception {
        expectingCallToGetCookiesFromRequestWillReturnNull();
        tokenStrategy.extractAuthenticationToken(request);
    }

    private void expectingCallToGetCookiesFromRequestWillReturnNull() {
        mockery.checking(new Expectations() {{
            oneOf(request).getCookies(); will(returnValue(null));
        }});

    }

    private void expectingCallToGetCookiesFromRequestWillReturn(final Cookie cookie) {
        mockery.checking(new Expectations() {{
            oneOf(request).getCookies(); will(returnValue(new Cookie[]{cookie}));
        }});
    }

    @Test(expected = NoSuchElementException.class)
    public void testExtractAuthenticationToken_requiredCookiesIsMissingInRequest_shouldThrowException() throws Exception {
        expectingCallToGetCookiesFromRequestWillReturn(new Cookie("NotOurCookie", "SomeRandomValue"));
        tokenStrategy.extractAuthenticationToken(request);
    }

    @Test
    public void testExtractAuthenticationToken_requiredCookiesIsPresentInRequest_shouldReturnTokenValue() throws Exception {
        expectingCallToGetCookiesFromRequestWillReturn(new Cookie(AUTH_TOKEN_NAME, "AuthTokenValue"));
        assertThat(tokenStrategy.extractAuthenticationToken(request), is(equalTo("AuthTokenValue")));
    }

    @Test
    public void testSaveAuthenticationToken() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(response).addCookie(with(Matchers.<Cookie>hasProperty("name", equalTo(AUTH_TOKEN_NAME))));
        }});
        tokenStrategy.saveAuthenticationToken("SomeTokenValueIsNotImportantHere!", response);
    }
}
