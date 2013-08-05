package com.robusta.commons.async.defaults.jobops;

import com.robusta.commons.async.api.JobStatus;
import com.robusta.commons.async.api.JobType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.exception.ExceptionUtils;

class AsynchronousJob<Parameters, Results> {
    private JobStatus status;
    private Results results;
    private final JobType jobType;
    private Parameters parameters;
    private Throwable exception;

    AsynchronousJob(JobType jobType, Parameters parameters) {
        this.jobType = jobType;
        this.parameters = parameters;
        this.status = JobStatus.INITIALIZED;
    }

    public void start() {
        this.status = JobStatus.PROGRESSING;
    }

    public void complete(Results results) {
        this.results = results;
        this.status = JobStatus.SUCCESSFUL;
    }

    public void failed(Throwable failure) {
        this.exception = failure;
        this.status = JobStatus.FAILED;
    }

    public Parameters parameters() {
        return this.parameters;
    }

    public Results results() {
        return this.results;
    }

    public String failure() {
        return ExceptionUtils.getMessage(this.exception);
    }

    public JobStatus status() {
        return this.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("jobType", jobType)
                .append("status", status)
                .toString();
    }
}
