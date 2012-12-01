package com.robusta.commons.context;

import com.robusta.commons.domain.user.UserFixture;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class UserContextHolderTest {
    private static final String LOGIN_NAME = "login_name";

    @Before
    public void setUp() throws Exception {
        UserContextHolder.clear();
    }

    @Test
    public void testSetCurrentUser_andVerifyThatAbleToGetUserIdentifierAndUsername() throws Exception {
        UserContextHolder.setCurrentUser(UserFixture.aUser().withDefaults().withLoginName(LOGIN_NAME).build());
        assertNotNull(UserContextHolder.getCurrentUserIdentifier());
        assertThat(UserContextHolder.getCurrentUsername(), is(equalTo(LOGIN_NAME)));
    }

    @Test(expected = RuntimeException.class)
    public void testNotSettingAUser_accessingUserShouldThrowException() throws Exception {
        UserContextHolder.getCurrentUsername();
    }
}
