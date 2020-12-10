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
package com.alipay.sofa.common.thread.construct;

import com.alipay.sofa.common.thread.SofaScheduledThreadPoolExecutor;
import com.alipay.sofa.common.thread.SofaThreadPoolConstants;
import com.alipay.sofa.common.thread.ThreadPoolTestBase;
import com.alipay.sofa.common.thread.space.SpaceNamedThreadFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author huzijie
 * @version SofaScheduledTreadPoolExecutorConstrcutsTest.java, v 0.1 2020年11月09日 4:35 下午 huzijie Exp $
 */
public class SofaScheduledTreadPoolExecutorConstructsTest extends ThreadPoolTestBase {

    private static final Class<?>           spaceThreadFactory = SpaceNamedThreadFactory.class;

    private static ThreadFactory            threadFactory;

    private static Class<?>                 defaultThreadFactory;

    private static RejectedExecutionHandler rejectedExecutionHandler;

    private static RejectedExecutionHandler defaultRejectedExecutionHandler;

    @BeforeClass
    public static void setup() throws NoSuchFieldException, IllegalAccessException {
        threadFactory = r -> new Thread();
        defaultThreadFactory = Arrays.stream(Executors.class.getDeclaredClasses()).
                filter(aClass -> aClass.getName().contains("DefaultThreadFactory")).findFirst().get();
        rejectedExecutionHandler = (r, executor) -> { };
        Field field = ThreadPoolExecutor.class.getDeclaredField("defaultHandler");
        field.setAccessible(true);
        defaultRejectedExecutionHandler = (RejectedExecutionHandler) field.get(null);
    }

    @Test
    public void testBasicConstruct() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory, rejectedExecutionHandler, "testThreadPool", "testSpace", 1, 10,
            TimeUnit.SECONDS);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(spaceThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertEquals("testSpace", threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testSpace-testThreadPool", threadPoolExecutor.getConfig()
            .getIdentity());
        Assert.assertEquals(1, threadPoolExecutor.getConfig().getTaskTimeout());
        Assert.assertEquals(10, threadPoolExecutor.getConfig().getPeriod());
        Assert.assertEquals(1000, threadPoolExecutor.getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.SECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutSpaceName() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory, rejectedExecutionHandler, "testThreadPool", 1, 10, TimeUnit.SECONDS);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getIdentity());
        Assert.assertEquals(1, threadPoolExecutor.getConfig().getTaskTimeout());
        Assert.assertEquals(10, threadPoolExecutor.getConfig().getPeriod());
        Assert.assertEquals(1000, threadPoolExecutor.getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.SECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutMonitorParams() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory, rejectedExecutionHandler, "testThreadPool", "testSpace");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(spaceThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertEquals("testSpace", threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testSpace-testThreadPool", threadPoolExecutor.getConfig()
            .getIdentity());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutSpaceNameAndMonitorParams() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory, rejectedExecutionHandler, "testThreadPool");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getIdentity());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutThreadFactoryAndRejectedExecutionHandler() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, "testThreadPool", "testSpace");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(spaceThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertEquals("testSpace", threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testSpace-testThreadPool", threadPoolExecutor.getConfig()
            .getIdentity());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutSpaceNameAndThreadFactoryAndRejectedExecutionHandler() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, "testThreadPool");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(defaultThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getThreadPoolName());
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertEquals("testThreadPool", threadPoolExecutor.getConfig().getIdentity());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutThreadPoolNameAndSpaceNameAndThreadFactoryAndRejectedExecutionHandler() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(10);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(defaultThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutThreadPoolNameAndSpaceNameAndRejectedExecutionHandler() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutThreadPoolNameAndSpaceNameAndThreadFactory() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, rejectedExecutionHandler);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(defaultThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }

    @Test
    public void testConstructWithoutThreadPoolNameAndSpaceName() {
        SofaScheduledThreadPoolExecutor threadPoolExecutor = new SofaScheduledThreadPoolExecutor(
            10, threadFactory, rejectedExecutionHandler);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaScheduledThreadPoolExecutor.class.getSimpleName()
                                          + "\\S+", threadPoolExecutor.getConfig()
            .getThreadPoolName()));
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }
}
