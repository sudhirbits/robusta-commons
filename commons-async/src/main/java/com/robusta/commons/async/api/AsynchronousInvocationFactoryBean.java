package com.robusta.commons.async.api;

import com.robusta.commons.async.defaults.invocation.DefaultAsynchronousInvocation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.base.Preconditions.checkState;

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
