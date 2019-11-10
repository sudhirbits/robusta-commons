package com.robusta.commons.async.defaults.execution;

import com.robusta.commons.async.api.*;
import com.robusta.commons.context.UserContextHolder;
import com.robusta.commons.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class DefaultAsynchronousExecution<Activity extends AsynchronousActivity<Parameters, Results>, Parameters, Results> implements AsynchronousExecution<Activity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsynchronousExecution.class);

    private Activity performer;
    private AsynchronousJobOperations<Parameters, Results> jobOperations;

    @Autowired
    DefaultAsynchronousExecution(AsynchronousJobOperations<Parameters, Results> jobOperations) {
        this.jobOperations = jobOperations;
    }

    @Override
    public void setPerformer(Activity performer) {
        this.performer = performer;
    }

    @Async
    @Override
    public void performAsynchronously(User user, Long jobId) {
        checkState(performer != null, "An asynchronous activity is required as the performer to perform this execution. Found none");
        checkArgument(user != null, "User is required for job operations");
        checkArgument(jobId != null, "Asynchronous job must be persisted and job id should be made available for this execution");
        UserContextHolder.setCurrentUser(user);
        JobContextHolder.setCurrentJob(makeAJob(jobId));
        try {
            LOGGER.debug("Starting job with id: '{}'", jobId);
            jobOperations.start(jobId);
            LOGGER.debug("Start complete for job with id: '{}'", jobId);

            try {
                LOGGER.debug("Performing job with id: '{}' using performer: {}", jobId, performer);
                Results results = performer.perform(jobOperations.parametersOfJob(jobId));
                LOGGER.debug("Job execution with id: '{}' was successful", jobId);
                jobOperations.markComplete(jobId, results);
            } catch (Throwable throwable) {
                LOGGER.debug("Exception during job execution", throwable);
                LOGGER.debug("Job execution with id: '{}' was unsuccessful", jobId);
                jobOperations.markFailure(jobId, throwable);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Irrecoverable job execution exception", throwable);
            throw new RuntimeException(throwable);
        } finally {
            UserContextHolder.clear();
            JobContextHolder.clear();
        }
    }

    private AsynchronousJob makeAJob(final Long jobId) {
        return new AsynchronousJob() {
            @Override
            public Long jobId() {
                return jobId;
            }
        };
    }

    @Override
    public JobType jobType() {
        return performer.jobType();
    }
}
