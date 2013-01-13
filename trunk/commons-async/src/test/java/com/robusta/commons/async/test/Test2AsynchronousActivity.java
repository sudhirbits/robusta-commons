package com.robusta.commons.async.test;

import com.robusta.commons.async.api.JobType;
import org.springframework.stereotype.Component;

@Component
public class Test2AsynchronousActivity implements TestOperation2 {
    @Override
    public TestResults2 perform(TestParameters2 testParameters) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new TestResults2(testParameters.getParameter1(), testParameters.getParameter2());
    }

    @Override
    public JobType jobType() {
        return JobType.with("CUSTOMER_SPREADSHEET_IMPORT");
    }
}
