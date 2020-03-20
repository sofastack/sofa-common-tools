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

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2020/3/16
 */
public class SofaThreadPoolExecutor extends ThreadPoolExecutor implements Runnable {
    private static long                          DEFAULT_TASK_TIMEOUT = 30;
    private static long                          DEFAULT_PERIOD       = 5;
    private static TimeUnit                      DEFAULT_TIME_UNIT    = TimeUnit.SECONDS;

    private String                               name;

    private long                                 taskTimeout          = DEFAULT_TASK_TIMEOUT;
    private long                                 period               = DEFAULT_PERIOD;
    private TimeUnit                             timeUnit             = DEFAULT_TIME_UNIT;
    private ScheduledFuture<?>                   scheduledFuture;
    private final Object                         monitor              = new Object();

    private Map<Runnable, RunnableExecutionInfo> executingTasks       = new ConcurrentHashMap<Runnable, RunnableExecutionInfo>();

    /**
     * Basic constructor
     * @param corePoolSize same as in {@link ThreadPoolExecutor}
     * @param maximumPoolSize same as in {@link ThreadPoolExecutor}
     * @param keepAliveTime same as in {@link ThreadPoolExecutor}
     * @param unit same as in {@link ThreadPoolExecutor}
     * @param workQueue same as in {@link ThreadPoolExecutor}
     * @param threadFactory same as in {@link ThreadPoolExecutor}
     * @param handler same as in {@link ThreadPoolExecutor}
     * @param name name of this thread pool
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String name, long taskTimeout, long period, TimeUnit timeUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.name = name;
        this.taskTimeout = taskTimeout;
        this.period = period;
        this.timeUnit = timeUnit;
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        name = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        name = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        name = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        name = createName();
        scheduleAndRegister(period, timeUnit);
    }

    /**
     * System property {@code SOFA_THREAD_POOL_LOGGING_CAPABILITY} controls whether logging
     */
    private void scheduleAndRegister(long period, TimeUnit unit) {
        ThreadPoolGovernor.registerThreadPoolExecutor(this);
        if ("true".equals(System
            .getProperty(SofaThreadConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY))) {
            synchronized (monitor) {
                scheduledFuture = ThreadPoolGovernor.scheduler.scheduleAtFixedRate(this, period,
                    period, unit);
            }
        }
    }

    private String createName() {
        return "P" + period + "T" + taskTimeout + "U" + timeUnit + this.hashCode();
    }

    public synchronized void startSchedule() {
        synchronized (monitor) {
            if (scheduledFuture == null) {
                scheduledFuture = ThreadPoolGovernor.scheduler.scheduleAtFixedRate(this, period,
                    period, timeUnit);
                ThreadLogger.info("Thread pool '{}' started with period: {}", name, period);
            }
        }
    }

    public synchronized void stopSchedule() {
        synchronized (monitor) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                ThreadLogger.info("Thread pool '{}' stopped.", name);
            }
        }
    }

    private void reschedule(long period, TimeUnit unit) {
        synchronized (monitor) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledFuture = ThreadPoolGovernor.scheduler.scheduleAtFixedRate(this, period,
                    period, unit);
                ThreadLogger.info("Reschedule thread pool '{}' with period: {}", name, period);
            }
        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        executingTasks.put(r, new RunnableExecutionInfo(t));
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        executingTasks.remove(r);
    }

    @Override
    public void run() {
        try {
            int decayedTaskCount = 0;
            for (Runnable task : executingTasks.keySet()) {
                RunnableExecutionInfo executionInfo = executingTasks.get(task);
                executionInfo.increaseBy(period);

                if (executionInfo.getExecutionTime() >= taskTimeout) {
                    ++decayedTaskCount;

                    if (!executionInfo.isPrinted()) {
                        executionInfo.setPrinted(true);
                        StringBuilder sb = new StringBuilder();
                        for (StackTraceElement e : executionInfo.getThread().getStackTrace()) {
                            sb.append("    ").append(e).append("\n");
                        }
                        ThreadLogger
                            .warn(
                                "ThreadPool {}: task {} exceeds the limit of execution time with stack trace: \n    {}",
                                getName(), task, sb.toString().trim());
                    }
                }
            }

            // threadPoolName, #queue, #executing, #idle, #pool, #decayed
            ThreadLogger.info("{} info: [{},{},{},{},{}]", getName(), this.getQueue().size(),
                executingTasks.size(), this.getPoolSize() - executingTasks.size(),
                this.getPoolSize(), decayedTaskCount);
        } catch (Exception e) {
            ThreadLogger.warn("ThreadPool '{}' is interrupted when running: {}", this.name, e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        ThreadPoolGovernor.unregisterThreadPoolExecutor(this.name);
        this.name = name;
        ThreadPoolGovernor.registerThreadPoolExecutor(name, this);
    }

    public void setPeriod(long period) {
        this.period = period;
        reschedule(period, timeUnit);
    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
        ThreadLogger.info("Updated taskTimeout to {}", taskTimeout);
    }

    static class RunnableExecutionInfo {
        private long             executionTime;
        private Thread           thread;
        private volatile boolean printed;

        public RunnableExecutionInfo(Thread thread) {
            executionTime = 0;
            this.thread = thread;
            printed = false;
        }

        public void increaseBy(long period) {
            executionTime += period;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
        }

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public boolean isPrinted() {
            return printed;
        }

        public void setPrinted(boolean printed) {
            this.printed = printed;
        }
    }
}
