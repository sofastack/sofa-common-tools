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
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/23
 */
public class SofaThreadPoolTaskExecutorTest extends ThreadPoolTestBase {
    @Test
    public void test() throws Exception {
        SofaThreadPoolTaskExecutor threadPool = new SofaThreadPoolTaskExecutor();
        threadPool.initialize();
        Assert.assertTrue(isMatch(getInfoViaIndex(0), INFO, String.format(
            "Thread pool with name '%s\\S+' registered",
            SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME)));
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Thread pool '%s\\S+' started with period: %s %s",
            SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME, threadPool.getPeriod(),
            threadPool.getTimeUnit())));

        threadPool.setPeriod(1000);
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Reschedule thread pool '%s\\S+' with period: %s %s",
            SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME, threadPool.getPeriod(),
            threadPool.getTimeUnit())));

        threadPool.setTaskTimeout(2200);
        Assert.assertTrue(isLastInfoMatch(String.format("Updated '%s\\S+' taskTimeout to %s %s",
            SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME, threadPool.getTaskTimeout(),
            threadPool.getTimeUnit())));
        threadPool.execute(new SleepTask(4200));
        threadPool.execute(new SleepTask(4200));
        Thread.sleep(9500);

        Assert.assertEquals(13, infoListAppender.list.size());
        Assert.assertEquals(2, warnListAppender.list.size());
        Assert.assertTrue(consecutiveInfoPattern(4, "1,1,0,1,0", "1,1,0,1,0", "1,1,0,1,1",
            "1,1,0,1,1", "0,1,0,1,0", "0,1,0,1,0", "0,1,0,1,1", "0,1,0,1,1", "0,0,1,1,0"));
        Assert
            .assertTrue(isMatch(
                lastWarnString().split("\n")[0],
                WARN,
                String
                    .format(
                        "Thread pool ('%s\\S+':) task (\\S+) exceeds the limit of execution time with stack trace: ",
                        SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME)));
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setAwaitTerminationSeconds(100);
        threadPool.shutdown();
        Assert.assertTrue(isLastInfoMatch(String.format(
            "Thread pool with name '%s\\S+' unregistered",
            SofaThreadPoolTaskExecutor.SIMPLE_CLASS_NAME)));
    }
}
