package com.robusta.commons.async.api;

public class JobContextHolder {
    private static ThreadLocal<AsynchronousJob> currentJob = new ThreadLocal<AsynchronousJob>();

    public static void setCurrentJob(AsynchronousJob job) {
        currentJob.set(job);
    }

    public static Long getCurrentJobId() {
        return getCurrentJob().jobId();
    }


    private static AsynchronousJob getCurrentJob() {
        AsynchronousJob job = currentJob.get();
        if(job == null) {
            job = new AsynchronousJob() {
                @Override
                public Long jobId() {
                    return Thread.currentThread().getId();
                }
            };
        }
        return job;
    }

    public static void clear() {
        currentJob.remove();
    }
}
