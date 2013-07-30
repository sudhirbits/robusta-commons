package com.robusta.commons.async.api;

public interface AsynchronousJobStatusOperations<Results> {
    JobStatus statusOfJob(Long jobId);
    Results resultsOfJob(Long jobId);
    String failureOfJob(Long jobId);
}
