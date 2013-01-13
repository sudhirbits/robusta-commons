package com.robusta.commons.async.test;


import com.google.common.collect.Lists;
import com.robusta.commons.async.api.AsynchronousInvocation;
import com.robusta.commons.async.api.JobType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.robusta.commons.async.api.AsynchronousInvocation.AsynchronousContext.anAsynchronousContextWith;
import static com.robusta.commons.domain.user.UserFixture.DEFAULT;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:async-config.xml"})
public class AysncOperationsTest {
    @Autowired
    @Qualifier("testAsyncInvocation")
    private AsynchronousInvocation<TestOperation, TestOperation.TestParameters, TestOperation.TestResults> invocation;

    @Autowired
    @Qualifier("test2AsyncInvocation")
    private AsynchronousInvocation<TestOperation2, TestOperation2.TestParameters2, TestOperation2.TestResults2> invocation2;

    @Autowired
    private MyCustomAsyncJobOperations jobOperations;
    private List<Long> runningJobs = Collections.synchronizedList(Lists.<Long>newArrayList());
    private List<Long> failedJobs = Collections.synchronizedList(Lists.<Long>newArrayList());
    private List<Long> completedJobs = Collections.synchronizedList(Lists.<Long>newArrayList());
    private AtomicInteger count = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception {
        assertNotNull(invocation);
        jobOperations.setListener(new JobExecutionListener() {
            @Override
            public void started(Long jobId) {
                runningJobs.add(jobId);
            }

            @Override
            public void failed(Long jobId, Throwable failure) {
                runningJobs.remove(jobId);
                failedJobs.add(jobId);
                count.decrementAndGet();
            }

            @Override
            public void completed(Long jobId, Object o) {
                runningJobs.remove(jobId);
                completedJobs.add(jobId);
                count.decrementAndGet();
            }

            @Override
            public void created(JobType jobType, Object o) {
                count.incrementAndGet();
            }
        });
    }

    @Test
    public void testName() throws Exception {
        for(int i = 0; i < 1; i++) {
            invocation.invokeAsynchronouslyAndReturnHandle(anAsynchronousContextWith(DEFAULT, new TestOperation.TestParameters("one" + i, "two" + i)));
            invocation2.invokeAsynchronouslyAndReturnHandle(anAsynchronousContextWith(DEFAULT, new TestOperation2.TestParameters2("three" + i, "four" + i)));
        }
        Thread.sleep(1000);
        while(count.get() != 0) {
            System.out.println("runningJobs.size() = " + runningJobs.size());
            System.out.println("completedJobs.size() = " + completedJobs.size());
            Thread.sleep(1000);
        }
    }
}
