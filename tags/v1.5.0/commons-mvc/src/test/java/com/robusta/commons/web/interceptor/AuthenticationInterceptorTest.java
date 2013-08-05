package com.robusta.commons.web.interceptor;

import com.robusta.commons.auth.api.AuthenticationFailedException;
import com.robusta.commons.auth.api.UserAuthentication;
import com.robusta.commons.domain.user.User;
import com.robusta.commons.domain.user.UserSession;
import com.robusta.commons.web.auth.AuthenticationTokenStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class AuthenticationInterceptorTest {
    private AuthenticationInterceptor interceptor;
    private Mockery mockery = new JUnit4Mockery();
    private UserAuthentication authentication;
    private AuthenticationTokenStrategy tokenStrategy;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserSession session;

    @Before
    public void setUp() throws Exception {
        authentication = mockery.mock(UserAuthentication.class);
        tokenStrategy = mockery.mock(AuthenticationTokenStrategy.class);
        request = mockery.mock(HttpServletRequest.class);
        response = mockery.mock(HttpServletResponse.class);
        session = mockery.mock(UserSession.class);
        interceptor = new AuthenticationInterceptor(authentication, tokenStrategy);
    }

    @Test(expected = RuntimeException.class)
    public void testPreHandle_handlerIsNotSupported_shouldThrowException() throws Exception {
        interceptor.preHandle(request, response, new NotHandlerMethod());
    }

    @Test
    public void testPreHandle_controllerIsAnnotatedWithAuthDisabled_shouldSkipAuthAndReturnTrue() throws Exception {
        assertThat(interceptor.preHandle(request, response, new HandlerMethod(new ControllerAnnotatedWithAuthDisabled(), aMethod())), is(true));
    }

    @Test
    public void testPreHandle_controllerRequiresAuthentication_authTokenIsNotPresent_shouldRedirectToLogin() throws Exception {
        expectingExtractAuthTokenOnTokenStrategyWillReturn(null);
        expectingSendRedirectCallOnResponseTo("login.html");
        assertThat(interceptor.preHandle(request, response, new HandlerMethod(new NormalController(), aMethod())), is(false));
    }

    @Test
    public void testPreHandle_controllerRequiresAuthentication_authenticationSuccess_shouldProceed() throws Exception {
        expectingExtractAuthTokenOnTokenStrategyWillReturn("SomeAuthTokenValue");
        expectingLookupSessionAgainstAuthTokenWillReturn("SomeAuthTokenValue", session);
        mockery.checking(new Expectations() {{
            oneOf(session).loggedUser(); will(returnValue(mockery.mock(User.class)));
        }});
        assertThat(interceptor.preHandle(request, response, new HandlerMethod(new NormalController(), aMethod())), is(true));
    }

    @Test
    public void testPreHandle_controllerRequiresAuthentication_sessionCannotBeLookedUpFromAuthToken_shouldRedirectToLogin() throws Exception {
        expectingExtractAuthTokenOnTokenStrategyWillReturn("SomeAuthTokenValue");
        expectingLookupSessionAgainstAuthTokenWillThrowException("SomeAuthTokenValue");
        expectingSendRedirectCallOnResponseTo("login.html?invsess=true");
        assertThat(interceptor.preHandle(request, response, new HandlerMethod(new NormalController(), aMethod())), is(false));
    }

    private void expectingLookupSessionAgainstAuthTokenWillThrowException(final String tokenValue) throws AuthenticationFailedException {
        mockery.checking(new Expectations() {{
            oneOf(authentication).lookupUserSessionByToken(tokenValue); will(throwException(new AuthenticationFailedException("for testing")));
        }});
    }

    private void expectingLookupSessionAgainstAuthTokenWillReturn(final String token, final UserSession session) throws AuthenticationFailedException {
        mockery.checking(new Expectations() {{
            oneOf(authentication).lookupUserSessionByToken(token); will(returnValue(session));
        }});
    }

    private void expectingSendRedirectCallOnResponseTo(final String page) throws IOException {
        mockery.checking(new Expectations() {{
            oneOf(response).sendRedirect(page);
        }});
    }

    private void expectingExtractAuthTokenOnTokenStrategyWillReturn(final String tokenValue) {
        mockery.checking(new Expectations() {{
            oneOf(tokenStrategy).extractAuthenticationToken(request);
            if(tokenValue == null) will(throwException(new NoSuchElementException())); else will(returnValue(tokenValue));
        }});
    }

    private Method aMethod() {
        return this.getClass().getMethods()[0];
    }

    private class NotHandlerMethod {
    }

    @AuthDisabled
    private class ControllerAnnotatedWithAuthDisabled {
    }

    private class NormalController {
    }
}
