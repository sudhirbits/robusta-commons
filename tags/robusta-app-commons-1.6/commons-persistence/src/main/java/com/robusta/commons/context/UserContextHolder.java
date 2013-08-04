package com.robusta.commons.context;

import com.robusta.commons.domain.user.User;

public abstract class UserContextHolder {
    private static ThreadLocal<User> currentUser = new ThreadLocal<User>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static String getCurrentUsername() {
        return getCurrentUser().loginName();
    }

    public static String getCurrentUserIdentifier() {
        return getCurrentUser().identifier();
    }

    public static User getCurrentUser() {
        User user = currentUser.get();
        if(user == null) {
            throw new RuntimeException("User is not available in context");
        }
        return user;
    }

    public static void clear() {
        currentUser.remove();
    }
}
