package com.robusta.commons.async.defaults.invocation;

import com.robusta.commons.async.api.AsynchronousExecution;
import com.robusta.commons.async.api.AsynchronousInvocation;
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

@RunWith(JMock.class)
public class DefaultAsynchronousInvocationTest {
    private DefaultAsynchronousInvocation<TestOperation, TestOperation.TestParameters, TestOperation.TestResults> asynchronousInvocation;

    private Mockery mockery;
    @Mock private TestOperation testOperation;
    @Mock private AsynchronousExecution<TestOperation> asynchronousExecution;
    @Mock private AsynchronousJobOperations<TestOperation.TestParameters, TestOperation.TestResults> jobOperations;
    @Mock private User user;


    private AsynchronousInvocation.AsynchronousContext<TestOperation.TestParameters> context;
    private TestOperation.TestParameters parameters;
    private JobType jobType;
    private Long jobId;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockery.checking(new Expectations() {{
            oneOf(asynchronousExecution).setPerformer(testOperation);
        }});
        asynchronousInvocation = new DefaultAsynchronousInvocation<TestOperation, TestOperation.TestParameters, TestOperation.TestResults>(testOperation, asynchronousExecution, jobOperations);
        parameters = new TestOperation.TestParameters("params1", "params2");
        context = AsynchronousInvocation.AsynchronousContext.anAsynchronousContextWith(user, parameters);
        jobType = JobType.with("TEST");
        jobId = -1L;
    }

    @Test
    public void testInvokeAsynchronouslyAndReturnHandle() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(asynchronousExecution).jobType(); will(returnValue(jobType));
            oneOf(asynchronousExecution).performAsynchronously(user, jobId);
            oneOf(jobOperations).create(jobType, parameters); will(returnValue(jobId));
        }});
        asynchronousInvocation.invokeAsynchronouslyAndReturnHandle(context);
    }
}
