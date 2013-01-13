package com.robusta.commons.async.api;

import com.robusta.commons.domain.user.User;

public interface AsynchronousInvocation<Activity extends AsynchronousActivity<Parameters, Results>, Parameters, Results> {
    Long invokeAsynchronouslyAndReturnHandle(AsynchronousContext<Parameters> context);

    public static class AsynchronousContext<Parameters> {
        private User user;
        private Parameters parameters;

        private AsynchronousContext(User user, Parameters parameters) {
            this.parameters = parameters;
            this.user = user;
        }

        public static <Parameters> AsynchronousContext<Parameters> anAsynchronousContextWith(User user, Parameters parameters) {
            return new AsynchronousContext<Parameters>(user, parameters);
        }

        public User user() {
            return user;
        }

        public Parameters parameters() {
            return parameters;
        }
    }
}
