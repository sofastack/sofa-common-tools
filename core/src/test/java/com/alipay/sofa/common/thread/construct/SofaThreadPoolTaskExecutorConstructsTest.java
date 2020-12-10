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

import com.alipay.sofa.common.thread.SofaThreadPoolTaskExecutor;
import com.alipay.sofa.common.thread.bean.TestTaskDecorator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutionException;

/**
 * @author huzijie
 * @version SofaThreadPoolTaskExecutorConstructsTest.java, v 0.1 2020年11月09日 4:43 下午 huzijie Exp $
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:thread/sofaThreadPoolTaskExecutor.xml" })
public class SofaThreadPoolTaskExecutorConstructsTest {

    @Autowired
    @Qualifier("testSofaThreadPoolTaskExecutorA")
    private ThreadPoolTaskExecutor sofaThreadPoolTaskExecutorA;

    @Autowired
    @Qualifier("testSofaThreadPoolTaskExecutorB")
    private ThreadPoolTaskExecutor sofaThreadPoolTaskExecutorB;

    @Test
    public void testSofaThreadPoolTaskExecutorA() throws ExecutionException, InterruptedException {
        Assert.assertTrue(sofaThreadPoolTaskExecutorA instanceof SofaThreadPoolTaskExecutor);
        Assert.assertEquals(50, sofaThreadPoolTaskExecutorA.getCorePoolSize());
        Assert.assertEquals(100, sofaThreadPoolTaskExecutorA.getMaxPoolSize());
        Assert.assertEquals(10, sofaThreadPoolTaskExecutorA.getKeepAliveSeconds());
        Assert.assertEquals("testThreadExecutorA-", sofaThreadPoolTaskExecutorA.getThreadNamePrefix());
        Assert.assertEquals("testThreadPoolNameA", ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorA).getThreadPoolName());
        Assert.assertEquals("testSpaceA", ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorA).getSpaceName());
        Assert.assertEquals(2000, ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorA).getTaskTimeout());
        Assert.assertEquals(10000, ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorA).getPeriod());
        Assert.assertTrue(sofaThreadPoolTaskExecutorA.getThreadPoolExecutor().allowsCoreThreadTimeOut());
        TestTaskDecorator.clearCount();
        sofaThreadPoolTaskExecutorA.submit(() -> { }).get();
        Assert.assertEquals(1, TestTaskDecorator.count.get());
        sofaThreadPoolTaskExecutorA.shutdown();
    }

    @Test
    public void testSofaThreadPoolTaskExecutorB() throws ExecutionException, InterruptedException {
        Assert.assertTrue(sofaThreadPoolTaskExecutorB instanceof SofaThreadPoolTaskExecutor);
        Assert.assertEquals(100, sofaThreadPoolTaskExecutorB.getCorePoolSize());
        Assert.assertEquals(200, sofaThreadPoolTaskExecutorB.getMaxPoolSize());
        Assert.assertEquals(20, sofaThreadPoolTaskExecutorB.getKeepAliveSeconds());
        Assert.assertEquals("testThreadExecutorB-", sofaThreadPoolTaskExecutorB.getThreadNamePrefix());
        Assert.assertEquals("testThreadPoolNameB", ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorB).getThreadPoolName());
        Assert.assertEquals("testSpaceB", ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorB).getSpaceName());
        Assert.assertEquals(3000, ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorB).getTaskTimeout());
        Assert.assertEquals(5000, ((SofaThreadPoolTaskExecutor) sofaThreadPoolTaskExecutorB).getPeriod());
        TestTaskDecorator.clearCount();
        sofaThreadPoolTaskExecutorB.submit(() -> { }).get();
        Assert.assertEquals(1, TestTaskDecorator.count.get());
        sofaThreadPoolTaskExecutorB.shutdown();
    }

    @Configuration
    static class SofaThreadPoolTaskExecutorConstructsTestConfiguration {

        @Bean
        public ThreadPoolTaskExecutor testSofaThreadPoolTaskExecutorB(TaskDecorator taskDecorator) {
            SofaThreadPoolTaskExecutor sofaThreadPoolTaskExecutor = new SofaThreadPoolTaskExecutor();
            sofaThreadPoolTaskExecutor.setCorePoolSize(100);
            sofaThreadPoolTaskExecutor.setMaxPoolSize(200);
            sofaThreadPoolTaskExecutor.setKeepAliveSeconds(20);
            sofaThreadPoolTaskExecutor.setThreadNamePrefix("testThreadExecutorB-");
            sofaThreadPoolTaskExecutor.setThreadPoolName("testThreadPoolNameB");
            sofaThreadPoolTaskExecutor.setSpaceName("testSpaceB");
            sofaThreadPoolTaskExecutor.setTaskTimeout(3000);
            sofaThreadPoolTaskExecutor.setPeriod(5000);
            sofaThreadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
            sofaThreadPoolTaskExecutor.setTaskDecorator(taskDecorator);
            return sofaThreadPoolTaskExecutor;
        }
    }
}
