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

import com.alipay.sofa.common.thread.SofaThreadPoolConstants;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
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
 * @version SofaThreadPoolTaskExecutorApiTest.java, v 0.1 2020年11月09日 3:38 下午 huzijie Exp $
 */
public class SofaThreadPoolExecutorConstructsTest extends ThreadPoolTestBase {

    private static final Class<?>           spaceThreadFactory = SpaceNamedThreadFactory.class;

    private static BlockingQueue<Runnable>  queue;

    private static ThreadFactory            threadFactory;

    private static Class<?>                 defaultThreadFactory;

    private static RejectedExecutionHandler rejectedExecutionHandler;

    private static RejectedExecutionHandler defaultRejectedExecutionHandler;

    @BeforeClass
    public static void setup() throws NoSuchFieldException, IllegalAccessException {
        queue = new LinkedBlockingQueue<>(1000);
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler, "testThreadPool",
            "testSpace", 1, 10, TimeUnit.SECONDS);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler, "testThreadPool", 1,
            10, TimeUnit.SECONDS);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler, "testThreadPool",
            "testSpace");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler, "testThreadPool");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, "testThreadPool", "testSpace");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, "testThreadPool");
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
        Assert.assertEquals(defaultThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(defaultRejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, rejectedExecutionHandler);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
        Assert.assertEquals(defaultThreadFactory, threadPoolExecutor.getThreadFactory().getClass());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
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
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 50, 60,
            TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
        Assert.assertEquals(10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals(50, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals(60, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(queue, threadPoolExecutor.getQueue());
        Assert.assertEquals(threadFactory, threadPoolExecutor.getThreadFactory());
        Assert.assertEquals(rejectedExecutionHandler,
            threadPoolExecutor.getRejectedExecutionHandler());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
        Assert.assertNull(threadPoolExecutor.getConfig().getSpaceName());
        Assert.assertTrue(Pattern.matches(SofaThreadPoolExecutor.class.getSimpleName() + "\\S+",
            threadPoolExecutor.getConfig().getThreadPoolName()));
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeout());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, threadPoolExecutor.getConfig()
            .getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT, threadPoolExecutor
            .getConfig().getTaskTimeoutMilli());
        Assert.assertEquals(TimeUnit.MILLISECONDS, threadPoolExecutor.getConfig().getTimeUnit());
    }
}
