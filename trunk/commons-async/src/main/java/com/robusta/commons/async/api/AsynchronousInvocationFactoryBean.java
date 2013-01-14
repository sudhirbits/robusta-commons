package com.robusta.commons.async.api;

import com.robusta.commons.async.defaults.invocation.DefaultAsynchronousInvocation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.base.Preconditions.checkState;

/**
 * Factory bean to prepare an AsynchronousInvocation for an Activity that implements
 * AsynchronousActivity.
 *
 * <p>Steps to use
 * <ol>
 *     <li>Implement the asynchronous operation as an implementation of AsynchronousActivity</li>
 *     <li>Ensure your spring configuration enables Spring-Async and includes spring component-scan
 *     for com.robusta.commons.async.defaults</li>
 *     <li>Include a bean definition for com.robusta.commons.async.api.AsynchronousInvocationFactoryBean
 *     and inject your AsynchronousActivity into it</li>
 *     <li>Autowire AsynchronousInvocation&lt;Activity&gt; into your synchronous flow</li>
 *     <li>Use AsynchronousInvocation.invokeAsynchronouslyAndReturnHandle to start the
 *     asynchronous process</li>
 * </ol></p>
 * @param <Activity> Operation that needs to be run asynchronously implementing AsynchronousActivity
 * @param <Parameters> Parameters to the asynchronous operation
 * @param <Results> Results of the asynchronous operation
 * @see AsynchronousActivity
 * @see AsynchronousInvocation
 */
public class AsynchronousInvocationFactoryBean<Activity extends AsynchronousActivity<Parameters, Results>, Parameters, Results> implements FactoryBean<AsynchronousInvocation<Activity, Parameters, Results>>, InitializingBean {
    @Autowired
    private AsynchronousJobOperations<Parameters, Results> jobOperations;
    @Autowired
    private AsynchronousExecution<Activity> execution;

    private Activity activity;

    public void setActivity(Activity performer) {
        this.activity = performer;
    }

    @Override
    public AsynchronousInvocation<Activity, Parameters, Results> getObject() throws Exception {
        return new DefaultAsynchronousInvocation<Activity, Parameters, Results>(activity, execution, jobOperations);
    }

    @Override
    public Class<?> getObjectType() {
        return AsynchronousInvocation.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkState(activity != null);
    }
}
