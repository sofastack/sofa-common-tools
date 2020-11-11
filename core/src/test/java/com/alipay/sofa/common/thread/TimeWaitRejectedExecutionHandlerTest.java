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

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href=mailto:orezsilence@163.com>zhangchengxi</a>
 */
public class TimeWaitRejectedExecutionHandlerTest extends ThreadPoolTestBase {

    @Test
    public void test() throws InterruptedException {

        SofaThreadPoolExecutor executor = new SofaThreadPoolExecutor(1,1,1, TimeUnit.HOURS,new LinkedBlockingDeque<>(1));
        TimeWaitRejectedExecutionHandler handler = new TimeWaitRejectedExecutionHandler(executor, 2, TimeUnit.SECONDS);
        executor.setRejectedExecutionHandler(handler);

        Runnable run10s = () -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        boolean exceptionOccur = false;
        try{
            executor.execute(run10s);
            executor.execute(run10s);
            try{
                executor.execute(run10s);
                Assert.fail();
            }catch (RejectedExecutionException e){
                // ignore
                exceptionOccur = true;
            }
            Assert.assertTrue(exceptionOccur);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
        executor.awaitTermination(100, TimeUnit.SECONDS);
    }
}
