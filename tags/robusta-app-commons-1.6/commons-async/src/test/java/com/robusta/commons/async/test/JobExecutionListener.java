package com.robusta.commons.async.test;

import com.robusta.commons.async.api.JobType;

public interface JobExecutionListener<Parameters, Results> {
    void started(Long jobId);
    void failed(Long jobId, Throwable failure);
    void completed(Long jobId, Results results);
    void created(JobType jobType, Parameters parameters);
}
