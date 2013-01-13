package com.robusta.commons.async.defaults.jobops;

import com.robusta.commons.async.api.AsynchronousJobOperations;
import com.robusta.commons.async.api.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NoOpAsynchronousJobOperations<Parameters, Results> implements AsynchronousJobOperations<Parameters, Results> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpAsynchronousJobOperations.class);
    private static Map<Long, Object> map = new HashMap<Long, Object>();

    @Override
    public Long create(JobType jobType, Parameters parameters) {
        Long id = new Random().nextLong();
        map.put(id, parameters);
        return id;
    }

    @Override
    public void start(Long jobId) {
        LOGGER.debug("Job marked started: Status will be progressing.");
    }

    @Override
    public void markComplete(Long jobId, Results results) {
        LOGGER.debug("Job marked complete: Status will be completed.");
    }

    @Override
    public void markFailure(Long jobId, Throwable failure) {
        LOGGER.debug("Job marked complete: Status will be completed.");
    }

    @Override
    public Parameters parametersOfJob(Long jobId) {
        return (Parameters) map.get(jobId);
    }

    @Override
    public Results resultsOfJob(Long jobId) {
        return null;
    }
}
