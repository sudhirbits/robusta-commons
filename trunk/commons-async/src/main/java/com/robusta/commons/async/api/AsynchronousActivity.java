package com.robusta.commons.async.api;

public interface AsynchronousActivity<Parameters, Results> {
    Results perform(Parameters parameters) throws PerformanceException;
    JobType jobType();

    class PerformanceException extends Exception {
    }
}
