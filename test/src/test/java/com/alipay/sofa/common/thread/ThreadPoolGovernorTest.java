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
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolGovernorTest extends ThreadPoolTestBase {
    @Test
    public void test() throws Exception {
        ThreadPoolGovernor.setPeriod(1);
        ThreadPoolGovernor.setLoggable(true);
        ThreadPoolGovernor.registerThreadPoolExecutor("test1", Executors.newSingleThreadExecutor());
        ThreadPoolGovernor.registerThreadPoolExecutor("test2", Executors.newSingleThreadExecutor());
        ThreadPoolGovernor.start();

        Thread.sleep(2200);
        List<ILoggingEvent> logList = governListAppender.list;
        Assert.assertEquals(6, logList.size());
    }
}
