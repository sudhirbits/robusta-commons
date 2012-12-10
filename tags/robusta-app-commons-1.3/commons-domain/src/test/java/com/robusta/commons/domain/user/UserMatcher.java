package com.robusta.commons.domain.user;

import com.robusta.commons.test.matchers.CompositeGenericMatcher;
import org.hamcrest.Matcher;

public class UserMatcher extends CompositeGenericMatcher<User, UserMatcher> {
    private UserMatcher() {
        super(User.class, UserMatcher.class);
    }

    public static UserMatcher isAUser() {
        return new UserMatcher();
    }

    public UserMatcher withName(Matcher<String> nameMatcher) {
        return matchProperty("name", nameMatcher, "user name");
    }
    public UserMatcher withType(Matcher<UserType> userTypeMatcher) {
        return matchProperty("type", userTypeMatcher, "user type");
    }
    public UserMatcher withLoginName(Matcher<String> loginNameMatcher) {
        return matchProperty("loginName", loginNameMatcher, "login name");
    }
    public UserMatcher withIdentifier(Matcher<String> identifierMatcher) {
        return matchProperty("identifier", identifierMatcher, "user id");
    }
}
