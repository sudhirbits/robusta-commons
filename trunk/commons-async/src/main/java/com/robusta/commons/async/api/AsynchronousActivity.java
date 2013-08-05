package com.robusta.commons.async.api;

/**
 * Any Activity that needs to be run asynchronously should
 * implement AsynchronousActivity. Only then the asynchronous operation
 * can be injected into AsynchronousInvocationFactoryBean to get
 * an instance of AsynchronousInvocation&lt;Activity&gt;
 *
 * A synchronous flow can then use a AsynchronousInvocation&lt;Activity&gt;
 * to invokeAsynchronouslyAndReturnHandle which returns immediately but
 * invokes the operation in Activity asynchronously.
 * @param <Parameters> Parameters for the asynchronous operation
 * @param <Results> Results of the asynchronous operation
 * @see AsynchronousInvocationFactoryBean
 * @see AsynchronousInvocation
 * @see AsynchronousJobOperations
 */
public interface AsynchronousActivity<Parameters, Results> {
    /**
     * Asynchronous operation. Will be invoked with the Parameters
     * with which AsynchronousInvocation.invokeAsynchronouslyAndReturnHandle
     * was originally invoked. When Results are returned, the asynchronous
     * operation is marked complete with success. When PerformanceException
     * is thrown from the implementation, operation is marked complete with
     * failure.
     * @param parameters Parameters
     * @return Results
     * @throws PerformanceException To mark operation as failed.
     */
    Results perform(Parameters parameters) throws PerformanceException;

    /**
     * This job type will be passed on to AsynchronousJobOperations for
     * creation of job.
     * @return JobType
     */
    JobType jobType();

    public static class PerformanceException extends Exception {
        public PerformanceException(String message) {
            super(message);
        }

        public PerformanceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
