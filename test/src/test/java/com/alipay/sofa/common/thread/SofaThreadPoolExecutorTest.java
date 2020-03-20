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

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/19
 */
public class SofaThreadPoolExecutorTest extends ThreadPoolTestBase {
    @Test
    public void test() throws Exception {
        System.setProperty(SofaThreadConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY, "true");

        SofaThreadPoolExecutor threadPool = new SofaThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(2));
        threadPool.setPeriod(1);
        threadPool.setTaskTimeout(2);

        threadPool.execute(new SleepTask(3200));
        threadPool.execute(new SleepTask(3200));

        List<ILoggingEvent> logList = infoListAppender.list;
        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(13, logList.size());
    }

    @Test
    public void testRename() {

    }

    static class SleepTask implements Runnable {
        private long sleepTime;

        public SleepTask(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                // do nothing
            }
        }
    }
}
