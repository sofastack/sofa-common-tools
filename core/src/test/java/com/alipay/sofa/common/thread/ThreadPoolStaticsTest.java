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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huzijie
 * @version ThreadPoolStaticsTest.java, v 0.1 2020年11月10日 11:56 上午 huzijie Exp $
 */
public class ThreadPoolStaticsTest extends ThreadPoolTestBase {

    @Test
    public void testSofaThreadPoolExecutor() throws Exception {
        SofaThreadPoolExecutor executor = new SofaThreadPoolExecutor(50, 50,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.AbortPolicy());
        executor.stopSchedule();
        Assert.assertEquals(-1, executor.getStatistics().getAverageRunningTime());
        Assert.assertEquals(-1, executor.getStatistics().getAverageStayInQueueTime());
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Object lock = new Object();
        synchronized (lock) {
            while (executor.getActiveCount() != 0) {
                lock.wait(1000);
            }
        }
        System.out.println("Average running time:" + executor.getStatistics().getAverageRunningTime());
        Assert.assertEquals(1000, executor.getStatistics().getAverageRunningTime(), 10);
        System.out.println("Average stay in queue time:" + executor.getStatistics().getAverageStayInQueueTime());
        Assert.assertEquals(500, executor.getStatistics().getAverageStayInQueueTime(), 10);
        executor.getStatistics().resetAverageStatics();
        Assert.assertEquals(0, executor.getStatistics().getTotalTaskCount());
        executor.shutdown();
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSofaScheduledThreadPoolExecutor() throws Exception {
        SofaScheduledThreadPoolExecutor executor = new SofaScheduledThreadPoolExecutor(10);
        executor.stopSchedule();
        Assert.assertEquals(-1, executor.getStatistics().getAverageRunningTime());
        Assert.assertEquals(-1, executor.getStatistics().getAverageStayInQueueTime());
        for (int i = 0; i < 10; i++) {
            executor.scheduleWithFixedDelay(() -> {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
        Thread.sleep(5000);
        System.out.println("Average running time:" + executor.getStatistics().getAverageRunningTime());
        Assert.assertEquals(1000, executor.getStatistics().getAverageRunningTime(), 10);
        System.out.println("Average stay in queue time:" + executor.getStatistics().getAverageStayInQueueTime());
        Assert.assertEquals(0, executor.getStatistics().getAverageStayInQueueTime());
        executor.getStatistics().resetAverageStatics();
        Assert.assertEquals(0, executor.getStatistics().getTotalTaskCount());
        executor.shutdown();
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }
}
