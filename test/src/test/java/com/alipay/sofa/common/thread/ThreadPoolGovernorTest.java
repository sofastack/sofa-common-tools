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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolGovernorTest extends ThreadPoolTestBase {
    @Before
    public void threadPoolGovernorTestSetup() {
        ThreadPoolGovernor.setPeriod(1);
        ThreadPoolGovernor.setLoggable(true);
        ThreadPoolGovernor.startSchedule();
    }

    @After
    @SuppressWarnings("unchecked")
    public void threadPoolGovernorTestClearUp() throws Exception {
        ThreadPoolGovernor.stopSchedule();

        Field f = ThreadPoolGovernor.class.getDeclaredField("registry");
        f.setAccessible(true);
        Map<String, ThreadPoolExecutor> registry = (Map<String, ThreadPoolExecutor>) f.get(null);
        registry.clear();
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
        Assert.assertTrue(infoListAppender.list.get(1).toString()
            .contains("ThreadPool with name '" + threadPoolName1 + "' registered"));
        Assert.assertEquals(0, warnListAppender.list.size());

        ThreadPoolGovernor.unregisterThreadPoolExecutor(threadPoolName1);
        ThreadPoolGovernor.unregisterThreadPoolExecutor(threadPoolName2);
        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertTrue(infoListAppender.list.get(8).toString()
            .contains("ThreadPool with name '" + threadPoolName2 + "' unregistered"));
        Assert.assertEquals(0, warnListAppender.list.size());
    }

    @Test
    public void testSofaThreadPoolExecutor() throws Exception {
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(7, infoListAppender.list.size());
        Assert.assertTrue(infoListAppender.list.get(0).toString()
            .contains("Started " + ThreadPoolGovernor.class.getCanonicalName()));
        Assert.assertEquals(0, warnListAppender.list.size());
        Assert.assertEquals(sofaThreadPoolExecutor1,
            ThreadPoolGovernor.getThreadPoolExecutor(sofaThreadPoolExecutor1.getName()));
        Assert.assertEquals(sofaThreadPoolExecutor2,
            ThreadPoolGovernor.getThreadPoolExecutor(sofaThreadPoolExecutor2.getName()));

        ThreadPoolGovernor.unregisterThreadPoolExecutor(sofaThreadPoolExecutor1.getName());
        ThreadPoolGovernor.unregisterThreadPoolExecutor(sofaThreadPoolExecutor2.getName());
        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
    }

    @Test
    public void testEmptyThreadPoolName() {
        ThreadPoolGovernor.registerThreadPoolExecutor("", new ThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));
        ThreadPoolGovernor.registerThreadPoolExecutor(null, new ThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));

        Assert.assertEquals(1, infoListAppender.list.size());
        Assert.assertEquals(2, warnListAppender.list.size());
        Assert.assertTrue(warnListAppender.list.get(0).toString().contains("Rejected registering"));
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

        Assert.assertEquals(1, ThreadPoolGovernor.getPeriod());
        Assert.assertFalse(ThreadPoolGovernor.isLoggable());
        Assert.assertEquals(3, infoListAppender.list.size());
        Assert.assertEquals(0, warnListAppender.list.size());
    }

    @Test
    public void testGovernorReschedule() throws Exception {
        ThreadPoolGovernor.setPeriod(2);
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(2, ThreadPoolGovernor.getPeriod());
        Assert.assertEquals(4, infoListAppender.list.size());
        Assert
            .assertTrue(infoListAppender.list
                .get(1)
                .toString()
                .contains(
                    "Reschedule " + ThreadPoolGovernor.class.getCanonicalName() + " with period"));
        Assert.assertEquals(0, warnListAppender.list.size());
    }
}
