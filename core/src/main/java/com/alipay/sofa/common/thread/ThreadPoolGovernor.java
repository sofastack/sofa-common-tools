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

import com.alipay.sofa.common.thread.log.ThreadLogger;
import com.alipay.sofa.common.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.*;

import static com.alipay.sofa.common.thread.SofaThreadPoolConstants.DEFAULT_GOVERNOR_INTERVAL;
import static com.alipay.sofa.common.thread.SofaThreadPoolConstants.DEFAULT_GOVERNOR_LOGGER_ENABLE;

/**
 * The governor to manager the {@link ThreadPoolExecutor}s
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/17
 */
public class ThreadPoolGovernor {
    private ThreadPoolGovernor () {}

    private static final ThreadPoolGovernor INSTANCE = new ThreadPoolGovernor();

    public static String                                  CLASS_NAME         = ThreadPoolGovernor.class
                                                                                        .getCanonicalName();

    private final ScheduledExecutorService                governorScheduler =  Executors.newScheduledThreadPool(1,
                                                                                            new NamedThreadFactory("t.p.g"));

    private final ScheduledExecutorService                monitorScheduler =   Executors.newScheduledThreadPool(
                              Runtime.getRuntime().availableProcessors() + 1, new NamedThreadFactory("s.t.p.e"));

    private final Object                                   monitor            = new Object();

    private final GovernorInfoDumper                       governorInfoDumper = new GovernorInfoDumper();

    private final ConcurrentHashMap<String, ThreadPoolMonitorWrapper>   registry           = new ConcurrentHashMap<>();

    private long                                          period             = DEFAULT_GOVERNOR_INTERVAL;

    private boolean                                       loggable           = DEFAULT_GOVERNOR_LOGGER_ENABLE;

    private ScheduledFuture<?> governorScheduledFuture;

    public static ThreadPoolGovernor getInstance() {
        return INSTANCE;
    }

    /**
     * Start the governor info dump task
     */
    public synchronized void startSchedule() {
        if (governorScheduledFuture == null) {
            governorScheduledFuture = governorScheduler.scheduleAtFixedRate(governorInfoDumper, period, period,
                TimeUnit.SECONDS);
            ThreadLogger.info("Started {} with period: {} SECONDS", CLASS_NAME, period);
        } else {
            ThreadLogger.warn("{} has already started with period: {} SECONDS.", CLASS_NAME, period);
        }
    }

    /**
     * Stop the governor info dump task
     */
    public synchronized void stopSchedule() {
        if (governorScheduledFuture != null) {
            governorScheduledFuture.cancel(true);
            governorScheduledFuture = null;
            ThreadLogger.info("Stopped {}.", CLASS_NAME);
        } else {
            ThreadLogger.warn("{} is not scheduling!", CLASS_NAME);
        }
    }

    /**
     * Restart the governor info dump task
     */
    private void reScheduleInfoDump() {
        synchronized (monitor) {
            if (governorScheduledFuture != null) {
                governorScheduledFuture.cancel(true);
                governorScheduledFuture = governorScheduler.scheduleAtFixedRate(governorInfoDumper, period, period,
                        TimeUnit.SECONDS);
                ThreadLogger.info("Reschedule {} with period: {} SECONDS", CLASS_NAME, period);
            }
        }
    }

    /**
     * Register the {@link ThreadPoolExecutor} with {@link ThreadPoolConfig}
     * and {@link ThreadPoolStatistics} to the governor
     * @param threadPoolExecutor the base thread pool
     * @param threadPoolConfig the description of the thread pool
     * @param threadPoolStatistics the running statistics of the thread pool
     */
    public void registerThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor,
                                           ThreadPoolConfig threadPoolConfig,
                                           ThreadPoolStatistics threadPoolStatistics) {
        final String name = threadPoolConfig.getIdentity();
        if (StringUtil.isEmpty(name)) {
            ThreadLogger.error("Rejected registering request of instance {} with empty name: {}.",
                    threadPoolExecutor, name);
            return;
        }

        ThreadPoolMonitorWrapper threadPoolMonitorWrapper = new ThreadPoolMonitorWrapper(threadPoolExecutor
                , threadPoolConfig, threadPoolStatistics);
        if (registry.putIfAbsent(name, threadPoolMonitorWrapper) != null) {
            ThreadLogger.error(
                    "Rejected registering request of instance {} with duplicate name: {}",
                    threadPoolExecutor, name);
        } else {
            registry.get(name).startMonitor();
            ThreadLogger.info("Thread pool with name '{}' registered", name);
        }
    }

    /**
     * Unregister the {@link ThreadPoolExecutor} by it's {@link ThreadPoolConfig}
     * @param threadPoolConfig the description of the thread pool, it's identity is unique
     */
    public void unregisterThreadPoolExecutor(ThreadPoolConfig threadPoolConfig) {
        final String name = threadPoolConfig.getIdentity();
        if (StringUtil.isEmpty(name)) {
            ThreadLogger.error("Thread pool with empty name unregistered, may cause memory leak");
            return;
        }
        ThreadPoolMonitorWrapper threadPoolMonitorWrapper = registry.remove(name);
        if (threadPoolMonitorWrapper != null) {
            threadPoolMonitorWrapper.stopMonitor();
            ThreadLogger.info("Thread pool with name '{}' unregistered", name);
        }
    }

    /**
     * Get the {@link ThreadPoolExecutor} by it's identity
     * @param identity the unique identity
     * @return the {@link ThreadPoolExecutor}
     */
    public ThreadPoolExecutor getThreadPoolExecutor(String identity) {
        return registry.get(identity).getThreadPoolExecutor();
    }

    /**
     * Start the monitor the {@link ThreadPoolExecutor} registered in the governor
     * @param threadPoolConfig the description of the thread pool, it's identity is unique
     */
    public void startSchedule(ThreadPoolConfig threadPoolConfig) {
        ThreadPoolMonitorWrapper monitor = registry.get(threadPoolConfig.getIdentity());
        monitor.startMonitor();
    }

    /**
     * Stop to monitor the {@link ThreadPoolExecutor} registered in the governor
     * @param threadPoolConfig the description of the thread pool, it's identity is unique
     */
    public void stopSchedule(ThreadPoolConfig threadPoolConfig) {
        ThreadPoolMonitorWrapper monitor = registry.get(threadPoolConfig.getIdentity());
        monitor.stopMonitor();
    }

    /**
     * Restart the monitor the {@link ThreadPoolExecutor} registered in the governor
     * @param threadPoolConfig the description of the thread pool, it's identity is unique
     */
    public void reSchedule(ThreadPoolConfig threadPoolConfig) {
        ThreadPoolMonitorWrapper monitor = registry.get(threadPoolConfig.getIdentity());
        monitor.restartMonitor();
    }

    /**
     * The task to dump all {@link ThreadPoolExecutor} registered in the governor
     */
    class GovernorInfoDumper implements Runnable {
        @Override
        public void run() {
            try {
                if (loggable) {
                    for (Map.Entry<String, ThreadPoolMonitorWrapper> entry : registry.entrySet()) {
                        ThreadLogger.info("Thread pool '{}' exists with instance: {}",
                            entry.getKey(), entry.getValue().getThreadPoolExecutor());
                    }
                }
            } catch (Throwable e) {
                ThreadLogger.warn("{} is interrupted when running: {}", this, e);
            }
        }
    }

    /**
     * The period of the dump task
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * update the period of the dump task, then restart the task
     * @param period the dump task period
     */
    public void setPeriod(long period) {
        this.period = period;
        reScheduleInfoDump();
    }

    /**
     * The log switch of the dump task
     * @return whether log the dump
     */
    public boolean isLoggable() {
        return loggable;
    }

    /**
     * Update the log swtirch of the dump task
     * @param loggable whether log the dump
     */
    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }

    /**
     * The thread pool to executor the monitor tasks
     * @return the {@link ScheduledExecutorService}
     */
    public ScheduledExecutorService getMonitorScheduler() {
        return monitorScheduler;
    }
}
