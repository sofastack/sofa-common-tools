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

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolGovernorTest extends ThreadPoolTestBase {
    private static final long CUSTOMIZED_PERIOD = 1;

    @Before
    public void threadPoolGovernorTestSetup() {
        ThreadPoolGovernor.setPeriod(CUSTOMIZED_PERIOD);
        ThreadPoolGovernor.setLoggable(true);
        ThreadPoolGovernor.startSchedule();
    }

    @Test
    public void testThreadPoolExecutor() throws Exception {
        String threadPoolName1 = "threadPoolExecutor1";
        String threadPoolName2 = "threadPoolExecutor2";

        ThreadPoolGovernor.registerThreadPoolExecutor(threadPoolName1, new ThreadPoolExecutor(1, 1,
            4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));
        ThreadPoolGovernor.registerThreadPoolExecutor(threadPoolName2, new ThreadPoolExecutor(1, 1,
            4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));
        Thread.sleep(2200);

        Assert.assertEquals(7, infoListAppender.list.size());
        Assert.assertTrue(isMatch(getInfoViaIndex(1), INFO,
            String.format("Thread pool with name '%s' registered", threadPoolName1)));
        Assert.assertEquals(0, warnListAppender.list.size());

        ThreadPoolGovernor.unregisterThreadPoolExecutor(threadPoolName1);
        ThreadPoolGovernor.unregisterThreadPoolExecutor(threadPoolName2);
        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertTrue(isLastInfoMatch(String.format("Thread pool with name '%s' unregistered",
            threadPoolName2)));
        Assert.assertEquals(0, warnListAppender.list.size());
    }

    @Test
    public void testSofaThreadPoolExecutor() throws Exception {
        Assert.assertTrue(isLastInfoMatch(String.format("Started \\S+ with period: %s SECONDS",
            ThreadPoolGovernor.getPeriod())));
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
        Assert.assertEquals(sofaThreadPoolExecutor1,
            ThreadPoolGovernor.getThreadPoolExecutor(sofaThreadPoolExecutor1.getName()));
        Assert.assertEquals(sofaThreadPoolExecutor2,
            ThreadPoolGovernor.getThreadPoolExecutor(sofaThreadPoolExecutor2.getName()));

        ThreadPoolGovernor.unregisterThreadPoolExecutor(sofaThreadPoolExecutor1.getName());
        ThreadPoolGovernor.unregisterThreadPoolExecutor(sofaThreadPoolExecutor2.getName());
        Assert.assertEquals(11, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
        sofaThreadPoolExecutor1.shutdownNow();
        sofaThreadPoolExecutor2.shutdownNow();
    }

    @Test
    public void testEmptyThreadPoolName() {
        ThreadPoolGovernor.registerThreadPoolExecutor("", new ThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));
        ThreadPoolGovernor.registerThreadPoolExecutor(null, new ThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));

        Assert.assertEquals(1, infoListAppender.list.size());
        Assert.assertTrue(isMatch(lastWarnString(), ERROR,
            "Rejected registering request of instance .+"));
        Assert.assertEquals(2, warnListAppender.list.size());
    }

    @Test
    public void testStartStopGovernor() {
        ThreadPoolGovernor.startSchedule();
        Assert.assertTrue(ThreadPoolGovernor.isLoggable());
        Assert.assertEquals(1, infoListAppender.list.size());
        Assert.assertEquals(1, warnListAppender.list.size());

        ThreadPoolGovernor.stopSchedule();
        ThreadPoolGovernor.stopSchedule();
        Assert.assertEquals(2, infoListAppender.list.size());
        Assert.assertEquals(2, warnListAppender.list.size());
    }

    @Test
    public void testGovernorLoggable() throws Exception {
        ThreadPoolGovernor.setLoggable(false);
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(CUSTOMIZED_PERIOD, ThreadPoolGovernor.getPeriod());
        Assert.assertFalse(ThreadPoolGovernor.isLoggable());
        Assert.assertEquals(5, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
        ThreadPoolGovernor.setLoggable(true);
    }

    @Test
    public void testGovernorReschedule() throws Exception {
        long NEW_PERIOD = 2;

        ThreadPoolGovernor.setPeriod(NEW_PERIOD);
        Assert.assertTrue(isLastInfoMatch(String.format("Reschedule %s with period: %s SECONDS",
            ThreadPoolGovernor.CLASS_NAME, NEW_PERIOD)));
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        for (ILoggingEvent e : infoListAppender.list) {
            System.out.println(e);
        }

        Assert.assertEquals(NEW_PERIOD, ThreadPoolGovernor.getPeriod());
        Assert.assertEquals(5, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
        ThreadPoolGovernor.setPeriod(CUSTOMIZED_PERIOD);
    }
}
