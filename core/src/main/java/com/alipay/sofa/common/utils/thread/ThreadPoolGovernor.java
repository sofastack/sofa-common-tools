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
package com.alipay.sofa.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/17
 */
public class ThreadPoolGovernor {
    private static final Logger                    logger    = LoggerFactory
                                                                 .getLogger(ThreadPoolGovernor.class);

    private static long                            period    = 30;
    private static boolean                         loggable  = false;

    public static ScheduledExecutorService         scheduler = Executors.newScheduledThreadPool(1);

    private static Map<String, ThreadPoolExecutor> registry  = new ConcurrentHashMap<String, ThreadPoolExecutor>();

    static {
        scheduler.scheduleAtFixedRate(new GovernorInfoDumper(), period, period, TimeUnit.SECONDS);
    }

    /**
     * Can be used to manage JDK thread pool
     * @param name thread pool name
     * @param threadPoolExecutor thread pool instance
     */
    public static void registerThreadPoolExecutor(String name, ThreadPoolExecutor threadPoolExecutor) {
        registry.put(name, threadPoolExecutor);
        logger.info(String.format("ThreadPool with name '%s' registered", name));
    }

    public static void registerThreadPoolExecutor(SofaThreadPoolExecutor threadPoolExecutor) {
        registerThreadPoolExecutor(threadPoolExecutor.getName(), threadPoolExecutor);
    }

    public static void unregisterThreadPoolExecutor(String name) {
        registry.remove(name);
        logger.info(String.format("ThreadPool with name '%s' unregistered", name));
    }

    public static ThreadPoolExecutor getThreadPoolExecutor(String name) {
        return registry.get(name);
    }

    static class GovernorInfoDumper implements Runnable {
        @Override
        public void run() {
            if (loggable) {
                for (String name : registry.keySet()) {
                    logger.info(String.format("Thread pool %s exists with instance: %s", name,
                        registry.get(name)));
                }
            }
        }
    }

    public static long getPeriod() {
        return period;
    }

    public static void setPeriod(long period) {
        ThreadPoolGovernor.period = period;
    }

    public static boolean isLoggable() {
        return loggable;
    }

    public static void setLoggable(boolean loggable) {
        ThreadPoolGovernor.loggable = loggable;
    }
}
