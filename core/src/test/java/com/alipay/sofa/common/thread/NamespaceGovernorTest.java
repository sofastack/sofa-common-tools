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
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author huzijie
 * @version NamespaceGovernorTest.java, v 0.1 2020年11月11日 3:12 下午 huzijie Exp $
 */
public class NamespaceGovernorTest extends ThreadPoolTestBase {

    @Test
    public void testUpdateMonitorThreadPoolByNamespace() throws InterruptedException {
        SofaThreadPoolExecutor threadPoolExecutor1 = new SofaThreadPoolExecutor(10, 10, 10 , TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), "testPool1","namespaceTest");
        SofaThreadPoolExecutor threadPoolExecutor2 = new SofaThreadPoolExecutor(10, 10, 10 , TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), "testPool2","namespaceTest");
        SofaThreadPoolExecutor threadPoolExecutor3 = new SofaThreadPoolExecutor(10, 10, 10 , TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), "testPool3");
        String identity1 = ThreadPoolConfig.buildIdentity("testPool1", "namespaceTest");
        String identity2 = ThreadPoolConfig.buildIdentity("testPool2", "namespaceTest");
        String identity3 = ThreadPoolConfig.buildIdentity("testPool3", null);
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity1).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity2).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity3).isStarted());
        ThreadPoolGovernor.getInstance().stopMonitorThreadPoolByNamespace("namespaceTest");
        Assert.assertTrue(isLastInfoMatch("Thread pool with namespace 'namespaceTest' stopped"));
        Assert.assertFalse(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity1).isStarted());
        Assert.assertFalse(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity2).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity3).isStarted());
        ThreadPoolGovernor.getInstance().startMonitorThreadPoolByNamespace("namespaceTest");
        Assert.assertTrue(isLastInfoMatch("Thread pool with namespace 'namespaceTest' started"));
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity1).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity2).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity3).isStarted());
        ThreadPoolGovernor.getInstance().setMonitorThreadPoolByNamespace("namespaceTest", 3000);
        Assert.assertTrue(isLastInfoMatch("Thread pool with namespace 'namespaceTest' rescheduled with period '3000'"));
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity1).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity2).isStarted());
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity3).isStarted());
        Assert.assertEquals(3000, ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity1)
                .getThreadPoolConfig().getPeriod());
        Assert.assertEquals(3000, ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity2)
                .getThreadPoolConfig().getPeriod());
        Assert.assertEquals(SofaThreadPoolConstants.DEFAULT_PERIOD, ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity3)
                .getThreadPoolConfig().getPeriod());
        threadPoolExecutor1.shutdown();
        threadPoolExecutor2.shutdown();
        threadPoolExecutor3.shutdown();
        threadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        threadPoolExecutor2.awaitTermination(100, TimeUnit.SECONDS);
        threadPoolExecutor3.awaitTermination(100, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateMonitorThreadPoolByErrorNamespace() throws InterruptedException {
        SofaThreadPoolExecutor threadPoolExecutor = new SofaThreadPoolExecutor(10, 10, 10 , TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), "testPool","right");
        String identity = ThreadPoolConfig.buildIdentity("testPool", "right");
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity).isStarted());
        ThreadPoolGovernor.getInstance().stopMonitorThreadPoolByNamespace("wrong");
        Assert.assertTrue(isLastErrorMatch("Thread pool with namespace 'wrong' is not registered yet"));
        Assert.assertTrue(ThreadPoolGovernor.getInstance().getThreadPoolMonitorWrapper(identity).isStarted());
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
    }
}
