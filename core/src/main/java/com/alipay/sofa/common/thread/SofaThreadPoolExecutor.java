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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sofa thread pool based on {@link ThreadPoolExecutor}
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/16
 */
public class SofaThreadPoolExecutor extends ThreadPoolExecutor {
    private static final String        SIMPLE_CLASS_NAME = SofaThreadPoolExecutor.class
                                                             .getSimpleName();
    private static final AtomicInteger POOL_COUNTER      = new AtomicInteger(0);
    private final ThreadPoolConfig     config;
    private final ThreadPoolStatistics statistics;

    /**
     * Basic constructor
     * @param corePoolSize same as in {@link ThreadPoolExecutor}
     * @param maximumPoolSize same as in {@link ThreadPoolExecutor}
     * @param keepAliveTime same as in {@link ThreadPoolExecutor}
     * @param unit same as in {@link ThreadPoolExecutor}
     * @param workQueue same as in {@link ThreadPoolExecutor}
     * @param threadFactory same as in {@link ThreadPoolExecutor}
     * @param handler same as in {@link ThreadPoolExecutor}
     * @param threadPoolName name of this thread pool
     * @param namespace namespace of this tread pool
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName, String namespace, long taskTimeout,
                                  long period, TimeUnit timeUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.config = ThreadPoolConfig.newBuilder()
            .threadPoolName(StringUtil.isEmpty(threadPoolName) ? createName() : threadPoolName)
            .namespace(namespace).taskTimeout(taskTimeout).period(period).timeUnit(timeUnit)
            .build();
        this.statistics = new ThreadPoolStatistics(this);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName, long taskTimeout, long period,
                                  TimeUnit timeUnit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,
            threadPoolName, null, taskTimeout, period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName, String namespace) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,
            threadPoolName, namespace, 0, 0, null);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,
            threadPoolName, 0, 0, null);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  String threadPoolName, String namespace) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.config = ThreadPoolConfig.newBuilder()
            .threadPoolName(StringUtil.isEmpty(threadPoolName) ? createName() : threadPoolName)
            .namespace(namespace).build();
        this.statistics = new ThreadPoolStatistics(this);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  String threadPoolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadPoolName, null);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, "", null);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.setThreadFactory(threadFactory);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.setRejectedExecutionHandler(handler);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.setThreadFactory(threadFactory);
        this.setRejectedExecutionHandler(handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        this.statistics.getExecutingTasks().put(new ExecutingRunnable(r, t),
            new RunnableExecutionInfo());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        this.statistics.getExecutingTasks()
            .remove(new ExecutingRunnable(r, Thread.currentThread()));
    }

    @Override
    protected void terminated() {
        super.terminated();
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
    }

    public synchronized void startSchedule() {
        ThreadPoolGovernor.getInstance().startSchedule(config);
    }

    public synchronized void stopSchedule() {
        ThreadPoolGovernor.getInstance().stopSchedule(config);
    }

    public synchronized void reschedule() {
        ThreadPoolGovernor.getInstance().reSchedule(config);
    }

    public void updateThreadPoolName(String threadPoolName) {
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
        this.config.setThreadPoolName(threadPoolName);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
    }

    public void updateNamespace(String namespace) {
        ThreadPoolGovernor.getInstance().unregisterThreadPoolExecutor(this.getConfig());
        this.config.setNamespace(namespace);
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

    private String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", POOL_COUNTER.getAndIncrement());
    }
}
