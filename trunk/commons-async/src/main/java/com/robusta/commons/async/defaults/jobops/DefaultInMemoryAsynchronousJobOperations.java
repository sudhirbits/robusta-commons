package com.robusta.commons.async.defaults.jobops;

import com.robusta.commons.async.api.AsynchronousJobOperations;
import com.robusta.commons.async.api.AsynchronousJobStatusOperations;
import com.robusta.commons.async.api.JobStatus;
import com.robusta.commons.async.api.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultInMemoryAsynchronousJobOperations<Parameters, Results> implements AsynchronousJobOperations<Parameters, Results>, AsynchronousJobStatusOperations<Results> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInMemoryAsynchronousJobOperations.class);
    private static class InMemoryJobCache {
        private static Map<JobId, AsynchronousJob> jobsCache = new ConcurrentHashMap<JobId, AsynchronousJob>();
        private static Map<JobId, List<JobId>> parentJobIdToChildrenJobIdsCache = new ConcurrentHashMap<JobId, List<JobId>>();

        public static String print() {
            return new StringBuilder()
                    .append("InMemoryJobCache")
                    .append("{jobsCache=").append(jobsCache)
                    .append(", parentJobIdToChildrenJobIdsCache=").append(parentJobIdToChildrenJobIdsCache)
                    .append('}')
                    .toString();
        }

        public static synchronized Long newAsynchronousJob(Long parentJobIdAsLong, long jobIdAsLong, AsynchronousJob job) {
            JobId jobId = JobId.with(jobIdAsLong);
            JobId parentJobId = JobId.with(parentJobIdAsLong);
            jobsCache.put(jobId, job);
            List<JobId> childrenJobIds = null;
            if(parentJobIdToChildrenJobIdsCache.containsKey(parentJobId)) {
                childrenJobIds = parentJobIdToChildrenJobIdsCache.get(parentJobId);
            } else {
                childrenJobIds = newArrayList();
                parentJobIdToChildrenJobIdsCache.put(parentJobId, childrenJobIds);
            }
            childrenJobIds.add(jobId);
            return jobIdAsLong;
        }

        public static AsynchronousJob jobWith(Long jobId) {
            checkState(jobsCache.containsKey(JobId.with(jobId)));
            return jobsCache.get(JobId.with(jobId));
        }
    }


    @Override
    public String toString() {
        return InMemoryJobCache.print();
    }

    @Override
    public Long create(Long parentJobId, JobType jobType, Parameters parameters) {
        return InMemoryJobCache.newAsynchronousJob(parentJobId, new Random().nextLong(), new AsynchronousJob(jobType, parameters));
    }

    @Override
    public void start(Long jobId) {
        LOGGER.debug("Job marked started: Status will be progressing.");
        InMemoryJobCache.jobWith(jobId).start();
    }

    @Override
    public void markComplete(Long jobId, Results results) {
        LOGGER.debug("Job marked complete: Status will be completed.");
        InMemoryJobCache.jobWith(jobId).complete(results);
    }

    @Override
    public void markFailure(Long jobId, Throwable failure) {
        LOGGER.debug("Job marked complete: Status will be completed.");
        InMemoryJobCache.jobWith(jobId).failed(failure);
    }

    @Override
    public Parameters parametersOfJob(Long jobId) {
        return (Parameters) InMemoryJobCache.jobWith(jobId).parameters();
    }

    @Override
    public JobStatus statusOfJob(Long jobId) {
        return InMemoryJobCache.jobWith(jobId).status();
    }

    @Override
    public Results resultsOfJob(Long jobId) {
        return (Results) InMemoryJobCache.jobWith(jobId).results();
    }

    @Override
    public String failureOfJob(Long jobId) {
        return InMemoryJobCache.jobWith(jobId).failure();
    }
}
