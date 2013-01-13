package com.robusta.commons.async.test;

import com.robusta.commons.async.api.JobType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("test")
public class TestAsynchronousActivity implements TestOperation {
    @Override
    @Transactional
    public TestResults perform(TestParameters testParameters) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return new TestResults(testParameters.getParameter1(), testParameters.getParameter2());
    }

    @Override
    public JobType jobType() {
        return JobType.with("CUSTOMER_SPREADSHEET_IMPORT");
    }
}
