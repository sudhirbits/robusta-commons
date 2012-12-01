package com.robusta.commons.domain.user;

public class UserFixture {
    public static final User DEFAULT = aUser().withDefaults().build();
    private String userName;
    private UserType userType;
    private String loginName;

    private UserFixture() {
    }

    public static UserFixture aUser() {
        return new UserFixture();
    }

    public UserFixture withDefaults() {
        userType = UserType.FirstMapper;
        userName = "Last, First, Initials";
        loginName = "first.last";
        return this;
    }

    public User build() {
        return new User() {

            @Override
            public String name() {
                return userName;
            }

            @Override
            public UserType type() {
                return userType;
            }

            @Override
            public String loginName() {
                return loginName;
            }

            @Override
            public String identifier() {
                return "123456";
            }
        };
    }

    public UserFixture withLoginName(String loginName) {
        this.loginName = loginName; return this;
    }
}