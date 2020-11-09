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

/**
 * @author huzijie
 * @version SofaThreadPoolTaskScheduler.java, v 0.1 2020年11月09日 3:04 下午 huzijie Exp $
 */
public class SofaThreadPoolTaskSchedulerTest extends ThreadPoolTestBase {

    @Test
    public void test() throws Exception {
        SofaThreadPoolTaskScheduler threadPool = new SofaThreadPoolTaskScheduler();
        threadPool.initialize();
        Assert.assertTrue(isMatch(getInfoViaIndex(0), INFO, String.format(
            "Thread pool '%s\\S+' started with period: %s %s",
            SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME, threadPool.getPeriod(),
            threadPool.getTimeUnit())));
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Thread pool with name '%s\\S+' registered",
            SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME)));

        threadPool.setPeriod(1000);
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Restart thread pool '%s\\S+' with period: %s %s",
            SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME, threadPool.getPeriod(),
            threadPool.getTimeUnit())));

        threadPool.setTaskTimeout(2200);
        Assert.assertTrue(isLastInfoMatch(String.format("Updated '%s\\S+' taskTimeout to %s %s",
            SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME, threadPool.getTaskTimeout(),
            threadPool.getTimeUnit())));
        threadPool.scheduleWithFixedDelay(new SleepTask(4200), 1000);

        Thread.sleep(10500);

        Assert.assertEquals(14, infoListAppender.list.size());
        Assert.assertEquals(2, aberrantListAppender.list.size());
        Assert.assertTrue(consecutiveInfoPattern(4, "0,1,0,1,0", "0,1,0,1,0", "0,1,0,1,1",
            "0,1,0,1,1", "1,0,1,1,0", "0,1,0,1,0", "0,1,0,1,0", "0,1,0,1,1", "0,1,0,1,1",
            "1,0,1,1,0"));
        Assert
            .assertTrue(isMatch(
                lastWarnString().split("\n")[0],
                WARN,
                String
                    .format(
                        "Task \\S+ in thread pool (%s\\S+) started on \\S+ \\S+ exceeds the limit of \\S+ execution time with stack trace:",
                        SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME)));
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setAwaitTerminationSeconds(100);
        threadPool.shutdown();
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Thread pool with name '%s\\S+' unregistered",
            SofaThreadPoolTaskScheduler.SIMPLE_CLASS_NAME)));
    }
}
