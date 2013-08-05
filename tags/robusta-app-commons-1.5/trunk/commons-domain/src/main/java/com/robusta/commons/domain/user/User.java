package com.robusta.commons.domain.user;

public interface User {
    String name();
    UserType type();
    String loginName();
    String identifier();
}
