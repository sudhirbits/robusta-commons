package com.robusta.commons.async.defaults.jobops;

import com.robusta.commons.async.api.AsynchronousJobOperations;
import com.robusta.commons.async.api.AsynchronousJobStatusOperations;
import com.robusta.commons.async.api.JobStatus;
import com.robusta.commons.async.api.JobType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultInMemoryAsynchronousJobOperations<Parameters, Results> implements AsynchronousJobOperations<Parameters, Results>, AsynchronousJobStatusOperations<Results> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInMemoryAsynchronousJobOperations.class);
    private static Map<Long, AsynchronousJob> map = new ConcurrentHashMap<Long, AsynchronousJob>();

    private static class AsynchronousJob<Parameters, Results> {
        private JobStatus status;
        private Results results;
        private Parameters parameters;
        private Throwable exception;

        private AsynchronousJob(Parameters parameters) {
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
            return ToStringBuilder.reflectionToString(this);
        }
    }


    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Long create(JobType jobType, Parameters parameters) {
        Long id = new Random().nextLong();
        map.put(id, new AsynchronousJob(parameters));
        return id;
    }

    @Override
    public void start(Long jobId) {
        LOGGER.debug("Job marked started: Status will be progressing.");
        map.get(jobId).start();
    }

    @Override
    public void markComplete(Long jobId, Results results) {
        LOGGER.debug("Job marked complete: Status will be completed.");
        map.get(jobId).complete(results);
    }

    @Override
    public void markFailure(Long jobId, Throwable failure) {
        LOGGER.debug("Job marked complete: Status will be completed.");
        map.get(jobId).failed(failure);
    }

    @Override
    public Parameters parametersOfJob(Long jobId) {
        return (Parameters) map.get(jobId).parameters();
    }

    @Override
    public JobStatus statusOfJob(Long jobId) {
        return map.get(jobId).status();
    }

    @Override
    public Results resultsOfJob(Long jobId) {
        return (Results) map.get(jobId).results();
    }

    @Override
    public String failureOfJob(Long jobId) {
        return map.get(jobId).failure();
    }
}
