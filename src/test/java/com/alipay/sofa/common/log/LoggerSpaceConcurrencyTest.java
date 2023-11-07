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
package com.alipay.sofa.common.log;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huzijie
 * @version LoggerSpaceConcurrencyTest.java, v 0.1 2023年11月07日 3:57 PM huzijie Exp $
 */
public class LoggerSpaceConcurrencyTest {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            5, 50, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));


    @Test
    public void testLogUtilGetLoggerConcurrently() throws InterruptedException {
        List<Logger> loggers = new CopyOnWriteArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            EXECUTOR.submit(() -> {
                try {
                    Logger logger = getLogger();
                    loggers.add(logger);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        Assert.assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
        Assert.assertEquals(loggers.size(), 10);

        for (Logger logger : loggers) {
            Assert.assertNotEquals("Constants.DEFAULT_LOG is not expected", logger, Constants.DEFAULT_LOG);
        }

    }

    private Logger getLogger() {
        return LoggerSpaceManager.getLoggerBySpace("TestLogger", "com.alipay.sofa.concurrency");
    }
}
