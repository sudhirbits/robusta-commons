package com.robusta.commons.async.api;

public interface AsynchronousJobOperations<Parameters, Results> {
    Long create(JobType jobType, Parameters parameters);
    void start(Long jobId);
    void markComplete(Long jobId, Results results);
    void markFailure(Long jobId, Throwable failure);
    Parameters parametersOfJob(Long jobId);
    Results resultsOfJob(Long jobId);

//    interface AsynchronousJob {
//        public Long jobId();
//        public JobType jobType();
//        public JobStatus jobStatus();
//        public String parameters();
//        public String results();
//        public String failures();
//    }
}
