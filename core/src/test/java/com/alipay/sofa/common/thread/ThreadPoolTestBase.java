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
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.thread.log.ThreadLogger;
import org.junit.After;
import org.junit.Before;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/18
 */
public class ThreadPoolTestBase {
    protected static final String         INFO  = "INFO";
    protected static final String         WARN  = "WARN";
    protected static final String         ERROR = "ERROR";

    protected ListAppender<ILoggingEvent> infoListAppender;
    protected ListAppender<ILoggingEvent> aberrantListAppender;

    @Before
    public void beforeTest() {
        System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");
        System.setProperty("logging.path", "./logs");
        System.setProperty("logging.level.com.alipay.sofa.thread", "debug");
        System.setProperty("file.encoding", "UTF-8");

        infoListAppender = new ListAppender<ILoggingEvent>();
        aberrantListAppender = new ListAppender<ILoggingEvent>();
        infoListAppender.start();
        aberrantListAppender.start();
        ((Logger) ThreadLogger.INFO_THREAD_LOGGER).addAppender(infoListAppender);
        ((Logger) ThreadLogger.WARN_THREAD_LOGGER).addAppender(aberrantListAppender);
    }

    @After
    @SuppressWarnings("unchecked")
    public void clearUp() throws Exception {
        ThreadPoolGovernor.getInstance().stopGovernorSchedule();

        Field f = ThreadPoolGovernor.class.getDeclaredField("registry");
        f.setAccessible(true);
        Map<String, ThreadPoolMonitorWrapper> registry = (Map<String, ThreadPoolMonitorWrapper>) f
            .get(ThreadPoolGovernor.getInstance());
        for (ThreadPoolMonitorWrapper executor : registry.values()) {
            executor.stopMonitor();
            executor.getThreadPoolExecutor().shutdownNow();
        }
        registry.clear();
    }

    protected String getInfoViaIndex(int i) {
        if (i < 0) {
            return "";
        }
        return infoListAppender.list.get(i).toString();
    }

    protected String getWarnViaIndex(int i) {
        if (i < 0) {
            return "";
        }
        return aberrantListAppender.list.get(i).toString();
    }

    protected String lastInfoString() {
        return getInfoViaIndex(infoListAppender.list.size() - 1);
    }

    protected String lastWarnString() {
        return getWarnViaIndex(aberrantListAppender.list.size() - 1);
    }

    protected boolean isMatch(String str, String type, String reg) {
        return str.matches("\\[" + type + "\\] " + reg);
    }

    protected boolean isLastInfoMatch(String reg) {
        return isMatch(lastInfoString(), INFO, reg);
    }

    protected boolean isLastWarnMatch(String reg) {
        return isMatch(lastWarnString(), WARN, reg);
    }

    protected boolean isLastErrorMatch(String reg) {
        return isMatch(lastWarnString(), ERROR, reg);
    }

    protected boolean consecutiveInfoPattern(int startIndex, String... patterns) {
        for (String pattern : patterns) {
            if (!isMatch(getInfoViaIndex(startIndex++), INFO, ".+" + pattern + ".+")) {
                return false;
            }
        }
        return true;
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

    static class SleepCallableTask implements Callable<String> {
        private long sleepTime;

        public SleepCallableTask(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public String call() throws Exception {
            try {
                Thread.sleep(sleepTime);
            } catch (Throwable e) {
                // do nothing
            }
            return "sleepCallableTask";
        }
    }
}
