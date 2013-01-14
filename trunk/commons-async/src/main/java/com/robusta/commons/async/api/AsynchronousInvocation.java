package com.robusta.commons.async.api;

import com.robusta.commons.domain.user.User;

/**
 * Asynchronous invocation of an activity that needs to be branch off from a
 * synchronous flow.
 *
 * Use AsynchronousInvocationFactoryBean and inject your activity to get a
 * AsynchronousInvocation.
 *
 * Invoke invokeAsynchronouslyAndReturnHandle to submit the activity to
 * asynchronous execution and get a handle (job id).
 *
 * Use AsynchronousJobStatusOperations to get job status and Results
 * JobStatus.SUCCESSFUL and Failure message JobStatus.FAILED
 * @param <Activity> Operation that needs to be run asynchronously implementing AsynchronousActivity
 * @param <Parameters> Parameters to the asynchronous operation
 * @param <Results> Results of the asynchronous operation
 * @see JobStatus
 * @see AsynchronousJobStatusOperations
 */
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
