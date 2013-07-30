package com.robusta.commons.auth.api;

import com.robusta.commons.domain.user.UserSession;

public interface UserAuthentication {
    UserSession authenticateAndCreateUserSession(String loginName, String plainTextPassword) throws AuthenticationFailedException;
    UserSession lookupUserSessionByToken(String authToken) throws AuthenticationFailedException;
}
