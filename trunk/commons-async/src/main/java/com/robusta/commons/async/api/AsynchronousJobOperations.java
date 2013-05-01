package com.robusta.commons.async.api;

public interface AsynchronousJobOperations<Parameters, Results> {
    Long create(Long parentJobId, JobType jobType, Parameters parameters);
    void start(Long jobId);
    void markComplete(Long jobId, Results results);
    void markFailure(Long jobId, Throwable failure);
    Parameters parametersOfJob(Long jobId);
}
