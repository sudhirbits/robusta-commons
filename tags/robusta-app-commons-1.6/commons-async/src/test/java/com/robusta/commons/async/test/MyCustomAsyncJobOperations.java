package com.robusta.commons.async.test;

import com.robusta.commons.async.api.JobType;
import com.robusta.commons.async.defaults.jobops.DefaultInMemoryAsynchronousJobOperations;
import org.springframework.stereotype.Component;

@Component
public class MyCustomAsyncJobOperations<Parameters, Results> extends DefaultInMemoryAsynchronousJobOperations<Parameters, Results> {
    private JobExecutionListener<Parameters, Results> listener;

    public void setListener(JobExecutionListener<Parameters, Results> listener) {
        this.listener = listener;
    }

    @Override
    public Long create(Long parentJobId, JobType jobType, Parameters parameters) {
        Long jobId = super.create(parentJobId, jobType, parameters);
        listener.created(jobType, parameters);
        return jobId;
    }

    @Override
    public void markComplete(Long jobId, Results results) {
        super.markComplete(jobId, results);
        listener.completed(jobId, results);
    }

    @Override
    public void markFailure(Long jobId, Throwable failure) {
        super.markFailure(jobId, failure);
        listener.failed(jobId, failure);
    }

    @Override
    public Parameters parametersOfJob(Long jobId) {
        return super.parametersOfJob(jobId);
    }

    @Override
    public Results resultsOfJob(Long jobId) {
        return super.resultsOfJob(jobId);
    }

    @Override
    public void start(Long jobId) {
        super.start(jobId);
        listener.started(jobId);
    }
}
