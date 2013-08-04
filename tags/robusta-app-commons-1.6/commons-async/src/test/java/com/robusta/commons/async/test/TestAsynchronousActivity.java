package com.robusta.commons.async.test;

import com.robusta.commons.async.api.AsynchronousInvocation;
import com.robusta.commons.async.api.JobType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.robusta.commons.async.api.AsynchronousInvocation.AsynchronousContext.anAsynchronousContextWith;
import static com.robusta.commons.domain.user.UserFixture.DEFAULT;

@Component("test")
public class TestAsynchronousActivity implements TestOperation {

    @Autowired
    @Qualifier("test2AsyncInvocation")
    private AsynchronousInvocation<TestOperation2, TestOperation2.TestParameters2, TestOperation2.TestResults2> invocation2;

    @Override
    @Transactional
    public TestResults perform(TestParameters testParameters) {
        try {
            for(int i = 0; i < 1; i++) {
                invocation2.invokeAsynchronouslyAndReturnHandle(anAsynchronousContextWith(DEFAULT, new TestOperation2.TestParameters2("one" + i, "two" + i)));
            }
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new TestResults(testParameters.getParameter1(), testParameters.getParameter2());
    }

    @Override
    public JobType jobType() {
        return JobType.with("JOB_TYPE_1");
    }
}
