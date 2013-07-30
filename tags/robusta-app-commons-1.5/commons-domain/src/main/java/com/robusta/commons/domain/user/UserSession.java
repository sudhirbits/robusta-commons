package com.robusta.commons.domain.user;

public interface UserSession {
    User loggedUser();
    Authentication authentication();

    public static interface Authentication {
        String authToken();
    }
}
