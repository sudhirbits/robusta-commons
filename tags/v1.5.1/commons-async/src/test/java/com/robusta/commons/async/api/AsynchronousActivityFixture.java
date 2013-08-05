package com.robusta.commons.async.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AsynchronousActivityFixture<Parameters, Results> {
    private Parameters parameters;
    private Results results;
    private String jobType;
    private Closure<Parameters> doAsynchronously;

    public static interface Closure<Parameters> {
        void doAsynchronously(Parameters parameters);
    }

    private AsynchronousActivityFixture() {
        this.doAsynchronously = new Closure<Parameters>() {
            @Override
            public void doAsynchronously(Parameters parameters) {
                //do nothing...
            }
        };
    }

    public static <Parameters, Results> AsynchronousActivityFixture<Parameters, Results> anAsynchronousActivity() {
        return new AsynchronousActivityFixture<Parameters, Results>();
    }

    public AsynchronousActivityFixture<Parameters, Results> withParameters(Parameters parameters) {
        this.parameters = parameters; return this;
    }

    public AsynchronousActivityFixture<Parameters, Results> withResults(Results results) {
        this.results = results; return this;
    }

    public AsynchronousActivityFixture<Parameters, Results> withJobType(String jobType) {
        this.jobType = jobType; return this;
    }

    public AsynchronousActivityFixture<Parameters, Results> withOperation(Closure<Parameters> doAsynchronously) {
        this.doAsynchronously = doAsynchronously; return this;
    }

    public AsynchronousActivity<Parameters, Results> build() {
        return new AsynchronousActivity<Parameters, Results>() {
            @Override
            public Results perform(Parameters inputs) throws PerformanceException {
                assertThat(inputs, is(parameters));
                return results;
            }

            @Override
            public JobType jobType() {
                return JobType.with(jobType);
            }
        };
    }
}
