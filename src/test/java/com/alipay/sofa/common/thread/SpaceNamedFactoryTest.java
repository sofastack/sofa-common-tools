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

import com.alipay.sofa.common.thread.space.SpaceNamedThreadFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author huzijie
 * @version SpaceNamedFactoryTest.java, v 0.1 2020年11月11日 11:04 上午 huzijie Exp $
 */
public class SpaceNamedFactoryTest extends ThreadPoolTestBase {

    @Test
    public void testSpaceNamedThreadFactory() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10 , TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new SpaceNamedThreadFactory("testPool","testSpace"));
        Future future = threadPoolExecutor.submit((Callable<Object>) () -> Thread.currentThread().getName());
        Assert.assertEquals("testSpace-testPool-0-thread-1", future.get());
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
    }

    @Test
    public void testSofaThreadPoolExecutor() throws ExecutionException, InterruptedException {
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2), "testPool1", "space1");
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2), "testPool2", "space1");
        SofaThreadPoolExecutor sofaThreadPoolExecutor3 = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2), "testPool3");
        String threadName1 = (String) sofaThreadPoolExecutor1.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        String threadName2 = (String) sofaThreadPoolExecutor2.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        String threadName3 = (String) sofaThreadPoolExecutor3.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        Assert.assertEquals("space1-testPool1-0-thread-1", threadName1);
        Assert.assertEquals("space1-testPool2-1-thread-1", threadName2);
        Assert.assertTrue("thread name is: " + threadName3, Pattern.matches("pool-(\\d+)-thread-1", threadName3));
        sofaThreadPoolExecutor1.shutdown();
        sofaThreadPoolExecutor2.shutdown();
        sofaThreadPoolExecutor3.shutdown();
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
    }

    @Test
    public void testSofaScheduledThreadPoolExecutor() throws ExecutionException, InterruptedException {
        SofaScheduledThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaScheduledThreadPoolExecutor(1 ,"testPool1", "space2");
        SofaScheduledThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaScheduledThreadPoolExecutor(1 ,"testPool2", "space2");
        SofaScheduledThreadPoolExecutor sofaThreadPoolExecutor3 = new SofaScheduledThreadPoolExecutor(1 ,"testPool3");
        String threadName1 = (String) sofaThreadPoolExecutor1.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        String threadName2 = (String) sofaThreadPoolExecutor2.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        String threadName3 = (String) sofaThreadPoolExecutor3.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        Assert.assertEquals("space2-testPool1-0-thread-1", threadName1);
        Assert.assertEquals("space2-testPool2-1-thread-1", threadName2);
        Assert.assertTrue(Pattern.matches("pool-(\\d+)-thread-1", threadName3));
        sofaThreadPoolExecutor1.shutdown();
        sofaThreadPoolExecutor2.shutdown();
        sofaThreadPoolExecutor3.shutdown();
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
    }

    @Test
    public void testOverrideThreadPoolExecutor() throws ExecutionException, InterruptedException {
        SofaThreadPoolExecutor sofaThreadPoolExecutor = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2), "testPool", "space3");
        sofaThreadPoolExecutor.setThreadFactory(new NamedThreadFactory("test"));
        String threadName = (String) sofaThreadPoolExecutor.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        Assert.assertTrue(Pattern.matches("test-(\\d+)-thread-1", threadName));
        sofaThreadPoolExecutor.shutdown();
        sofaThreadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
    }

    @Test
    public void testUseSpaceNamedFactoryWithOutSpace() throws ExecutionException, InterruptedException {
        SofaThreadPoolExecutor sofaThreadPoolExecutor1 = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2));
        sofaThreadPoolExecutor1.setThreadFactory(new SpaceNamedThreadFactory("test", "none"));
        String threadName1 = (String) sofaThreadPoolExecutor1.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        SofaThreadPoolExecutor sofaThreadPoolExecutor2 = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2), "test", "none");
        String threadName2 = (String) sofaThreadPoolExecutor2.submit((Callable<Object>) () -> Thread.currentThread().getName()).get();
        Assert.assertEquals("none-test-0-thread-1", threadName1);
        Assert.assertEquals("none-test-0-thread-1", threadName2);
        sofaThreadPoolExecutor1.shutdown();
        sofaThreadPoolExecutor2.shutdown();
        sofaThreadPoolExecutor1.awaitTermination(100, TimeUnit.SECONDS);
        sofaThreadPoolExecutor2.awaitTermination(100, TimeUnit.SECONDS);
    }
}
