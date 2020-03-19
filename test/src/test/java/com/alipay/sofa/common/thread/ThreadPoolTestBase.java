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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.alipay.sofa.common.thread.log.ThreadLogger;
import org.junit.Before;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolTestBase {
    protected ListAppender<ILoggingEvent> governListAppender;

    @Before
    public void beforeTest() {
        System.setProperty("logging.path", "./logs");
        System.setProperty("logging.level.com.alipay.sofa.thread", "debug");
        System.setProperty("file.encoding", "UTF-8");

        governListAppender = new ListAppender<ILoggingEvent>();
        governListAppender.start();
        ((Logger) ThreadLogger.THREAD_LOGGER).addAppender(governListAppender);
    }
}
