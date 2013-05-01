package com.robusta.commons.async.test;

import com.robusta.commons.async.api.JobContextHolder;
import com.robusta.commons.async.api.JobType;
import org.springframework.stereotype.Component;

@Component
public class Test2AsynchronousActivity implements TestOperation2 {
    @Override
    public TestResults2 perform(TestParameters2 testParameters) throws PerformanceException {
        try {
            if(JobContextHolder.getCurrentJobId()%2 == 0) {
                throw new PerformanceException("Testing");
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new TestResults2(testParameters.getParameter1(), testParameters.getParameter2());
    }

    @Override
    public JobType jobType() {
        return JobType.with("JOB_TYPE_2");
    }
}
