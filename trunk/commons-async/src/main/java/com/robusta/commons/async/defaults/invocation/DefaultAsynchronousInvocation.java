package com.robusta.commons.async.defaults.invocation;

import com.robusta.commons.async.api.AsynchronousActivity;
import com.robusta.commons.async.api.AsynchronousExecution;
import com.robusta.commons.async.api.AsynchronousInvocation;
import com.robusta.commons.async.api.AsynchronousJobOperations;
import com.robusta.commons.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ClassUtils;

import static com.robusta.commons.async.api.JobContextHolder.getCurrentJobId;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.aop.support.AopUtils.isAopProxy;
import static org.springframework.util.ReflectionUtils.findMethod;

public class DefaultAsynchronousInvocation<Activity extends AsynchronousActivity<Parameters, Results>, Parameters, Results> implements AsynchronousInvocation<Activity, Parameters, Results> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsynchronousInvocation.class);

    private AsynchronousJobOperations<Parameters, Results> jobOperations;
    private AsynchronousExecution<Activity> invokable;

    public DefaultAsynchronousInvocation(Activity activity, AsynchronousExecution<Activity> invokable, AsynchronousJobOperations<Parameters, Results> jobOperations) {
        this.invokable = invokable;
        this.jobOperations = jobOperations;
        this.invokable.setPerformer(activity);
        Class targetClass = isAopProxy(invokable) ? getTargetClass(invokable) : invokable.getClass();
        if(!findMethod(targetClass, "performAsynchronously", User.class, Long.class).isAnnotationPresent(Async.class)) {
            LOGGER.warn("AsynchronousExecution implementation - {}.performAsynchronously is not annotated with @Async. Operation will be synchronous", ClassUtils.getQualifiedName(targetClass));
        }
    }

    @Override
    public Long invokeAsynchronouslyAndReturnHandle(AsynchronousContext<Parameters> context) {
        // Step 1 is to persist the job and get the job handle.
        Long jobId = jobOperations.create(getCurrentJobId(), invokable.jobType(), context.parameters());
        LOGGER.debug("Created Async Job with id: '{}'", jobId);

        // Step 2 is the invoke the delegated service that is annotated with @Async.
        invokable.performAsynchronously(context.user(), jobId);
        LOGGER.debug("Job with id: '{}' has been initiated. This sequence will return with job handle of Job Id", jobId);
        return jobId;
    }
}
