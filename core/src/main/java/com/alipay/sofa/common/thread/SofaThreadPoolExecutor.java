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
     * @param spaceName spaceName of this tread pool
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName, String spaceName, long taskTimeout,
                                  long period, TimeUnit timeUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
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
                                  String threadPoolName, String spaceName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,
            threadPoolName, spaceName, 0, 0, null);
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
                                  String threadPoolName, String spaceName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.config = ThreadPoolConfig.newBuilder()
            .threadPoolName(StringUtil.isEmpty(threadPoolName) ? createName() : threadPoolName)
            .spaceName(spaceName).build();
        this.statistics = new ThreadPoolStatistics(this);
        ThreadPoolGovernor.getInstance().registerThreadPoolExecutor(this, config, statistics);
        if (StringUtil.isNotEmpty(spaceName)) {
            this.setThreadFactory(new SpaceNamedThreadFactory(threadPoolName, spaceName));
        }
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
    public void execute(Runnable command) {
        ExecutingRunnable runner = new ExecutingRunnable(command);
        runner.setEnqueueTime(System.currentTimeMillis());
        super.execute(runner);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        ExecutingRunnable executingRunnable = (ExecutingRunnable) r;
        executingRunnable.setDequeueTime(System.currentTimeMillis());
        executingRunnable.setThread(t);
        this.statistics.getExecutingTasks().put(executingRunnable, 0L);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        ExecutingRunnable executingRunnable = (ExecutingRunnable) r;
        executingRunnable.setFinishTime(System.currentTimeMillis());
        this.statistics.addTotalTaskCount();
        this.statistics.addTotalRunningTime(executingRunnable.getRunningTime());
        this.statistics.addTotalStayInQueueTime(executingRunnable.getStayInQueueTime());
        this.statistics.getExecutingTasks().remove(executingRunnable);
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

    public void updateSpaceName(String spaceName) {
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

    private String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", POOL_COUNTER.getAndIncrement());
    }
}
