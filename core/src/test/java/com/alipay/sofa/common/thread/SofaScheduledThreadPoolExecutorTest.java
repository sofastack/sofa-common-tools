/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.common.thread;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author huzijie
 * @version SofaScheduledTreadPoolExecutorTest.java, v 0.1 2020年11月09日 2:42 下午 huzijie Exp $
 */
public class SofaScheduledThreadPoolExecutorTest extends ThreadPoolTestBase {
    private SofaScheduledThreadPoolExecutor threadPool;

    @Before
    public void setup() {
        threadPool = new SofaScheduledThreadPoolExecutor(1);
    }

    @After
    public void cleanExecutor() throws InterruptedException {
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();
            threadPool.awaitTermination(1000, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testDecayedTask() throws Exception {
        Assert.assertTrue(isMatch(getInfoViaIndex(0), INFO, String.format(
            "Thread pool '\\S+' started with period: %s %s", threadPool.getConfig().getPeriod(),
            threadPool.getConfig().getTimeUnit())));
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' registered"));

        threadPool.updatePeriod(1000);
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Restart thread pool '\\S+' with period: %s %s", threadPool.getConfig().getPeriod(),
            threadPool.getConfig().getTimeUnit())));

        threadPool.updateTaskTimeout(2200);
        Assert.assertTrue(isLastInfoMatch(String.format("Updated '\\S+' taskTimeout to %s %s",
            threadPool.getConfig().getTaskTimeout(), threadPool.getConfig().getTimeUnit())));

        ScheduledFuture future = threadPool.scheduleWithFixedDelay(new SleepTask(4200), 0, 1,
            TimeUnit.SECONDS);
        Thread.sleep(10500);

        Assert.assertEquals(16, infoListAppender.list.size());
        Assert.assertEquals(2, aberrantListAppender.list.size());
        Assert.assertTrue(consecutiveInfoPattern(4, "0,1,0,1,0", "0,1,0,1,0", "0,1,0,1,1",
            "0,1,0,1,1", "1,0,1,1,0", "0,420\\d", "0,1,0,1,0", "0,1,0,1,0", "0,1,0,1,1",
            "0,1,0,1,1", "1,0,1,1,0", "0,420\\d"));
        Assert.assertTrue(isMatch(lastWarnString().split("\n")[0], WARN,
            "Task \\S+ in thread pool \\S+ started on \\S+ \\S+ with traceId \\S+"
                    + " exceeds the limit of \\S+ execution time with stack trace:"));

        future.cancel(true);
        threadPool.shutdown();
        threadPool.awaitTermination(1000, TimeUnit.SECONDS);
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' unregistered"));
    }

    @Test
    public void testRename() {
        threadPool.updateThreadPoolName("sofaThreadPoolName");
        Assert.assertEquals(0, aberrantListAppender.list.size());
        Assert.assertEquals(6, infoListAppender.list.size());
        Assert.assertTrue(isMatch(getInfoViaIndex(3), INFO,
            "Thread pool with name '\\S+' unregistered"));
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' registered"));
    }

    @Test
    public void testSetSpaceName() {
        threadPool.updatespaceName("sofaThreadSpace");
        Assert.assertEquals(0, aberrantListAppender.list.size());
        Assert.assertEquals(6, infoListAppender.list.size());
        Assert.assertTrue(isMatch(getInfoViaIndex(3), INFO,
            "Thread pool with name '\\S+' unregistered"));
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' registered"));
    }

    @Test
    public void testStartStopThreadPool() {
        threadPool.startSchedule();
        Assert.assertTrue(isLastWarnMatch(String.format(
            "Thread pool '\\S+' is already started with period: %s %s", threadPool.getConfig()
                .getPeriod(), threadPool.getConfig().getTimeUnit())));

        threadPool.stopSchedule();
        Assert.assertTrue(isLastInfoMatch("Thread pool '\\S+' stopped."));
        threadPool.stopSchedule();
        Assert.assertTrue(isLastWarnMatch("Thread pool '\\S+' is not scheduling!"));
    }

    @Test
    public void testShutdownViolently() {
        threadPool.shutdownNow();
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' unregistered"));
        Assert.assertEquals(0, aberrantListAppender.list.size());
    }

    @Test
    public void testLoggingBurst() throws Exception {
        int numThreads = 5;

        threadPool.stopSchedule();
        threadPool = new SofaScheduledThreadPoolExecutor(5);
        threadPool.updatePeriod(100);
        threadPool.updateTaskTimeout(2050);
        for (int i = 0; i < numThreads; ++i) {
            threadPool.schedule(new SleepTask(5050), 1, TimeUnit.SECONDS);
        }
        threadPool.shutdown();
        threadPool.awaitTermination(100, TimeUnit.SECONDS);
        Assert.assertEquals(numThreads, aberrantListAppender.list.size());
        Assert.assertTrue(isLastInfoMatch("Thread pool with name '\\S+' unregistered"));
    }
}
