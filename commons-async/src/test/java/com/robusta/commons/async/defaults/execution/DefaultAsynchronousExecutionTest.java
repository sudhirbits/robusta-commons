package com.robusta.commons.async.defaults.execution;

import com.robusta.commons.async.api.AsynchronousActivity;
import com.robusta.commons.async.api.AsynchronousJobOperations;
import com.robusta.commons.async.api.JobType;
import com.robusta.commons.async.test.TestOperation;
import com.robusta.commons.domain.user.User;
import com.robusta.commons.test.mock.Mock;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.robusta.commons.test.mock.MockFactory.initMocks;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class DefaultAsynchronousExecutionTest {
    private DefaultAsynchronousExecution<TestOperation, TestOperation.TestParameters, TestOperation.TestResults> asynchronousExecution;

    private Mockery mockery;
    @Mock private AsynchronousJobOperations<TestOperation.TestParameters, TestOperation.TestResults> jobOperations;
    @Mock private TestOperation performer;
    private static final String JOB_TYPE = "TEST";
    private TestOperation.TestParameters parameters;
    private TestOperation.TestResults results;
    @Mock private User user;
    private static final Long jobId = -1L;
    private JobType jobType;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        asynchronousExecution = new DefaultAsynchronousExecution<TestOperation, TestOperation.TestParameters, TestOperation.TestResults>(jobOperations);
        asynchronousExecution.setPerformer(performer);
        parameters = new TestOperation.TestParameters("param1", "param2");
        results = new TestOperation.TestResults("result1", "result2");
        jobType = JobType.with(JOB_TYPE);
    }

    @Test(expected = IllegalStateException.class)
    public void testPerformAsynchronously_whenPerformerIsNotSet_shouldThrowException() throws Exception {
        asynchronousExecution.setPerformer(null);
        asynchronousExecution.performAsynchronously(user, jobId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformAsynchronously_whenUserIsUnavailable_shouldThrowException() throws Exception {
        asynchronousExecution.performAsynchronously(null, jobId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformAsynchronously_whenJobIdIsUnavailable_shouldThrowException() throws Exception {
        asynchronousExecution.performAsynchronously(user, null);
    }

    @Test
    public void testPerformAsynchronously_performerSucceeds() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(jobOperations).start(jobId);
            oneOf(jobOperations).parametersOfJob(jobId); will(returnValue(parameters));
            oneOf(jobOperations).markComplete(jobId, results);
        }});

        mockery.checking(new Expectations() {{
            oneOf(performer).perform(parameters); will(returnValue(results));
        }});
        asynchronousExecution.performAsynchronously(user, jobId);
    }

    @Test
    public void testPerformAsynchronously_performerFailsWithException() throws Exception {
        final AsynchronousActivity.PerformanceException exception = new AsynchronousActivity.PerformanceException();
        mockery.checking(new Expectations() {{
            oneOf(jobOperations).start(jobId);
            oneOf(jobOperations).parametersOfJob(jobId); will(returnValue(parameters));
            oneOf(jobOperations).markFailure(jobId, exception);
        }});

        mockery.checking(new Expectations() {{
            oneOf(performer).perform(parameters);will(throwException(exception));
        }});
        asynchronousExecution.performAsynchronously(user, jobId);
    }

    @Test(expected = RuntimeException.class)
    public void testPerformAsynchronously_asyncJobStartFailsWithException() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(jobOperations).start(jobId); will(throwException(new RuntimeException()));
        }});
        asynchronousExecution.performAsynchronously(user, jobId);
    }

    @Test
    public void testJobType() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(performer).jobType();will(returnValue(jobType));
        }});
        assertThat(asynchronousExecution.jobType(), is(jobType));
    }
}
