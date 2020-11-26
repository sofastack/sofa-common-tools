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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolGovernorTest extends ThreadPoolTestBase {
    private static final long CUSTOMIZED_PERIOD = 1;

    @Before
    public void threadPoolGovernorTestSetup() {
        ThreadPoolGovernor.getInstance().setGovernorPeriod(CUSTOMIZED_PERIOD);
        ThreadPoolGovernor.getInstance().setGovernorLoggable(true);
        ThreadPoolGovernor.getInstance().startGovernorSchedule();
    }

    @Test
    public void testSameThreadPoolName() {
        String threadPoolName = "sameName";

        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
            threadPoolName);
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
            threadPoolName);
        Assert
            .assertTrue(isLastErrorMatch(String.format(
                "Rejected registering request of instance .+ with duplicate name: %s",
                threadPoolName)));
    }

    @Test
    public void testSameThreadAndDifferentSpacePoolName() {
        String threadPoolName = "sameName";
        String space1 = "space1";
        String space2 = "space2";

        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), threadPoolName, space1);
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), threadPoolName, space2);
        Assert.assertEquals(5, infoListAppender.list.size());
        Assert.assertTrue(isMatch(getInfoViaIndex(2), INFO, String.format(
            "Thread pool with name '%s' registered", sofaThreadPoolExecutor1.getConfig()
                .getIdentity())));
        Assert.assertTrue(isMatch(getInfoViaIndex(4), INFO, String.format(
            "Thread pool with name '%s' registered", sofaThreadPoolExecutor2.getConfig()
                .getIdentity())));
        Assert.assertEquals(0, aberrantListAppender.list.size());
    }

    @Test
    public void testThreadPoolExecutor() throws Exception {
        String threadPoolName1 = "threadPoolExecutor1";
        String threadPoolName2 = "threadPoolExecutor2";

        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), threadPoolName1);
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), threadPoolName2);
        Thread.sleep(2200);

        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertTrue(isMatch(getInfoViaIndex(2), INFO,
            String.format("Thread pool with name '%s' registered", threadPoolName1)));
        Assert.assertEquals(0, aberrantListAppender.list.size());

        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor1.getConfig());
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor2.getConfig());
        Assert.assertEquals(13, infoListAppender.list.size());
        Assert.assertTrue(isLastInfoMatch(String.format("Thread pool with name '%s' unregistered",
            threadPoolName2)));
        Assert.assertEquals(0, aberrantListAppender.list.size());
    }

    @Test
    public void testSofaThreadPoolExecutor() throws Exception {
        Assert.assertTrue(isLastInfoMatch(String.format("Started \\S+ with period: %s SECONDS",
            ThreadPoolGovernor.getInstance().getGovernorPeriod())));
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertEquals(0, aberrantListAppender.list.size());
        Assert.assertEquals(sofaThreadPoolExecutor1, ThreadPoolGovernor.getInstance()
            .getThreadPoolExecutor(sofaThreadPoolExecutor1.getConfig().getIdentity()));
        Assert.assertEquals(sofaThreadPoolExecutor2, ThreadPoolGovernor.getInstance()
            .getThreadPoolExecutor(sofaThreadPoolExecutor2.getConfig().getIdentity()));

        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor1.getConfig());
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor2.getConfig());
        Assert.assertEquals(13, infoListAppender.list.size());
        Assert.assertEquals(0, aberrantListAppender.list.size());
        sofaThreadPoolExecutor1.shutdownNow();
        sofaThreadPoolExecutor2.shutdownNow();
    }

    @Test
    public void testEmptyThreadPoolName() throws NoSuchFieldException, IllegalAccessException {
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), "");
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 1, 4,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), "");
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor1.getConfig());
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(
            sofaThreadPoolExecutor2.getConfig());
        Field filed = ThreadPoolConfig.class.getDeclaredField("identity");
        filed.setAccessible(true);
        filed.set(sofaThreadPoolExecutor1.getConfig(), null);
        filed.set(sofaThreadPoolExecutor2.getConfig(), null);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(sofaThreadPoolExecutor1,
            sofaThreadPoolExecutor1.getConfig(), sofaThreadPoolExecutor1.getStatistics());
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(sofaThreadPoolExecutor2,
            sofaThreadPoolExecutor2.getConfig(), sofaThreadPoolExecutor2.getStatistics());
        Assert.assertEquals(9, infoListAppender.list.size());
        Assert.assertTrue(isMatch(lastWarnString(), ERROR,
            "Rejected registering request of instance .+"));
        Assert.assertEquals(2, aberrantListAppender.list.size());
    }

    @Test
    public void testStartStopGovernor() {
        ThreadPoolGovernor.getInstance().startGovernorSchedule();
        Assert.assertTrue(ThreadPoolGovernor.getInstance().isGovernorLoggable());
        Assert.assertEquals(1, infoListAppender.list.size());
        Assert.assertEquals(1, aberrantListAppender.list.size());

        ThreadPoolGovernor.getInstance().stopGovernorSchedule();
        ThreadPoolGovernor.getInstance().stopGovernorSchedule();
        Assert.assertEquals(2, infoListAppender.list.size());
        Assert.assertEquals(2, aberrantListAppender.list.size());
    }

    @Test
    public void testGovernorLoggable() throws Exception {
        ThreadPoolGovernor.getInstance().setGovernorLoggable(false);
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert
            .assertEquals(CUSTOMIZED_PERIOD, ThreadPoolGovernor.getInstance().getGovernorPeriod());
        Assert.assertFalse(ThreadPoolGovernor.getInstance().isGovernorLoggable());
        Assert.assertEquals(5, infoListAppender.list.size());
        Assert.assertEquals(0, aberrantListAppender.list.size());
        ThreadPoolGovernor.getInstance().setGovernorLoggable(true);
    }

    @Test
    public void testGovernorReschedule() throws Exception {
        long NEW_PERIOD = 2;

        ThreadPoolGovernor.getInstance().setGovernorPeriod(NEW_PERIOD);
        Assert.assertTrue(isLastInfoMatch(String.format("Reschedule %s with period: %s SECONDS",
            ThreadPoolGovernor.CLASS_NAME, NEW_PERIOD)));
        new SofaThreadPoolExecutor(1, 1, 4, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        Thread.sleep(2200);

        Assert.assertEquals(NEW_PERIOD, ThreadPoolGovernor.getInstance().getGovernorPeriod());
        Assert.assertEquals(5, infoListAppender.list.size());
        Assert.assertEquals(0, aberrantListAppender.list.size());
        ThreadPoolGovernor.getInstance().setGovernorPeriod(CUSTOMIZED_PERIOD);
    }
}
