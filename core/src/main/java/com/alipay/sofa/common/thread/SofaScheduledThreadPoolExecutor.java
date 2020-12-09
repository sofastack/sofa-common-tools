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
import com.alipay.sofa.common.thread.space.SpaceNamedThreadFactory;
import com.alipay.sofa.common.utils.StringUtil;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sofa thread pool based on {@link ScheduledThreadPoolExecutor}
 * @author huzijie
 * @version SofaScheduledThreadPoolExecutor.java, v 0.1 2020年11月09日 2:19 下午 huzijie Exp $
 */
public class SofaScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final String        SIMPLE_CLASS_NAME = SofaScheduledThreadPoolExecutor.class
                                                             .getSimpleName();
    private static final AtomicInteger POOL_COUNTER      = new AtomicInteger(0);
    private final ThreadPoolConfig     config;
    private final ThreadPoolStatistics statistics;

    /**
     * Basic constructor
     * @param corePoolSize same as in {@link ScheduledThreadPoolExecutor}
     * @param threadFactory same as in {@link ScheduledThreadPoolExecutor}
     * @param handler same as in {@link ScheduledThreadPoolExecutor}
     * @param threadPoolName name of this thread pool
     * @param spaceName spaceName of this tread pool
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler, String threadPoolName,
                                           String spaceName, long taskTimeout, long period,
                                           TimeUnit timeUnit) {
        super(corePoolSize, threadFactory, handler);
        this.config = ThreadPoolConfig.newBuilder()
            .threadPoolName(StringUtil.isEmpty(threadPoolName) ? createName() : threadPoolName)
            .spaceName(spaceName).taskTimeout(taskTimeout).period(period).timeUnit(timeUnit)
            .build();
        this.statistics = new ThreadPoolStatistics(this);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
        if (StringUtil.isNotEmpty(spaceName)) {
            this.setThreadFactory(new SpaceNamedThreadFactory(threadPoolName, spaceName));
        }
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler, String threadPoolName,
                                           long taskTimeout, long period, TimeUnit timeUnit) {
        this(corePoolSize, threadFactory, handler, threadPoolName, null, taskTimeout, period,
            timeUnit);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler, String threadPoolName,
                                           String spaceName) {
        this(corePoolSize, threadFactory, handler, threadPoolName, spaceName, 0, 0, null);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler, String threadPoolName) {
        this(corePoolSize, threadFactory, handler, threadPoolName, 0, 0, null);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, String threadPoolName, String spaceName) {
        super(corePoolSize);
        this.config = ThreadPoolConfig.newBuilder()
            .threadPoolName(StringUtil.isEmpty(threadPoolName) ? createName() : threadPoolName)
            .spaceName(spaceName).build();
        this.statistics = new ThreadPoolStatistics(this);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
        if (StringUtil.isNotEmpty(spaceName)) {
            this.setThreadFactory(new SpaceNamedThreadFactory(threadPoolName, spaceName));
        }
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, String threadPoolName) {
        this(corePoolSize, threadPoolName, null);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize) {
        this(corePoolSize, "", null);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        this(corePoolSize);
        this.setThreadFactory(threadFactory);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        this(corePoolSize);
        this.setRejectedExecutionHandler(handler);
    }

    public SofaScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler) {
        this(corePoolSize);
        this.setThreadFactory(threadFactory);
        this.setRejectedExecutionHandler(handler);
    }

    private String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", POOL_COUNTER.getAndIncrement());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        ExecutingRunnable runner = new ExecutingRunnable(r);
        runner.setThread(t);
        this.statistics.getExecutingTasks().put(runner, System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        ExecutingRunnable runner = new ExecutingRunnable(r);
        runner.setThread(Thread.currentThread());
        this.statistics.addTotalRunningTime(System.currentTimeMillis()
                                            - statistics.getExecutingTasks().get(runner));
        this.statistics.addTotalTaskCount();
        this.statistics.getExecutingTasks().remove(runner);
    }

    @Override
    protected void terminated() {
        super.terminated();
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
    }

    public synchronized void startSchedule() {
        ThreadPoolGovernor.getInstance().startMonitorThreadPool(config.getIdentity());
    }

    public synchronized void stopSchedule() {
        ThreadPoolGovernor.getInstance().stopMonitorThreadPool(config.getIdentity());
    }

    public synchronized void reschedule() {
        ThreadPoolGovernor.getInstance().restartMonitorThreadPool(config.getIdentity());
    }

    public void updateThreadPoolName(String threadPoolName) {
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
        this.config.setThreadPoolName(threadPoolName);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
    }

    public void updatespaceName(String spaceName) {
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
        this.config.setSpaceName(spaceName);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
    }

    public void updatePeriod(long period) {
        this.config.setPeriod(period);
        reschedule();
    }

    public void updateTaskTimeout(long taskTimeout) {
        this.config.setTaskTimeout(taskTimeout);
        this.config.setTaskTimeoutMilli(this.config.getTimeUnit().toMillis(taskTimeout));
        ThreadLogger.info("Updated '{}' taskTimeout to {} {}", this.config.getIdentity(),
            taskTimeout, this.config.getTimeUnit());
    }

    public ThreadPoolConfig getConfig() {
        return config;
    }

    public ThreadPoolStatistics getStatistics() {
        return statistics;
    }
}
