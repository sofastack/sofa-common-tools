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
import com.alipay.sofa.common.utils.ClassUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/16
 */
public class SofaThreadPoolExecutor extends ThreadPoolExecutor implements Runnable {
    private static final String                   ENABLE_LOGGING       = System
                                                                           .getProperty(SofaThreadConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY);
    private static final String                   SIMPLE_CLASS_NAME    = SofaThreadPoolExecutor.class
                                                                           .getSimpleName();
    private static final DateTimeFormatter        DATE_FORMAT          = DateTimeFormatter
                                                                           .ofPattern(
                                                                               "yyyy-MM-dd HH:mm:ss,SSS")
                                                                           .withZone(
                                                                               ZoneId
                                                                                   .systemDefault());
    private static final long                     DEFAULT_TASK_TIMEOUT = 30000;
    private static final long                     DEFAULT_PERIOD       = 5000;
    private static final TimeUnit                 DEFAULT_TIME_UNIT    = TimeUnit.MILLISECONDS;
    private static final ScheduledExecutorService scheduler            = Executors
                                                                           .newScheduledThreadPool(
                                                                               1,
                                                                               new NamedThreadFactory(
                                                                                   "s.t.p.e"));
    private static final AtomicInteger            POOL_COUNTER         = new AtomicInteger(0);

    private String                                threadPoolName;

    private long                                  taskTimeout          = DEFAULT_TASK_TIMEOUT;
    private long                                  period               = DEFAULT_PERIOD;
    private TimeUnit                              timeUnit             = DEFAULT_TIME_UNIT;
    private long                                  taskTimeoutMilli     = timeUnit
                                                                           .toMillis(taskTimeout);
    private ScheduledFuture<?>                    scheduledFuture;

    private final Map<ExecutingRunnable, RunnableExecutionInfo>  executingTasks       = new ConcurrentHashMap<>();

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
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName, long taskTimeout, long period,
                                  TimeUnit timeUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolName = threadPoolName;
        this.taskTimeout = taskTimeout;
        this.period = period;
        this.timeUnit = timeUnit;
        this.taskTimeoutMilli = timeUnit.toMillis(taskTimeout);
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  String threadPoolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,
            threadPoolName, DEFAULT_TASK_TIMEOUT, DEFAULT_PERIOD, DEFAULT_TIME_UNIT);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  String threadPoolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.threadPoolName = threadPoolName;
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        threadPoolName = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        threadPoolName = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        threadPoolName = createName();
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        threadPoolName = createName();
        scheduleAndRegister(period, timeUnit);
    }

    @Override
    protected void terminated() {
        super.terminated();
        ThreadPoolGovernor.unregisterThreadPoolExecutor(threadPoolName);
        synchronized (this) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
        }
    }

    /**
     * System property {@code SOFA_THREAD_POOL_LOGGING_CAPABILITY} controls whether logging
     */
    private void scheduleAndRegister(long period, TimeUnit unit) {
        ThreadPoolGovernor.registerThreadPoolExecutor(this);
        if (Boolean.FALSE.toString().equals(ENABLE_LOGGING)) {
            return;
        }

        synchronized (this) {
            scheduledFuture = scheduler.scheduleAtFixedRate(this, period, period, unit);
            ThreadLogger.info("Thread pool '{}' started with period: {} {}", threadPoolName,
                period, unit);
        }
    }

    private String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", POOL_COUNTER.getAndIncrement());
    }

    public synchronized void startSchedule() {
        if (scheduledFuture == null) {
            scheduledFuture = scheduler.scheduleAtFixedRate(this, period, period, timeUnit);
            ThreadLogger.info("Thread pool '{}' started with period: {} {}", threadPoolName,
                period, timeUnit);
        } else {
            ThreadLogger.warn("Thread pool '{}' is already started with period: {} {}",
                threadPoolName, period, timeUnit);
        }
    }

    public synchronized void stopSchedule() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
            ThreadLogger.info("Thread pool '{}' stopped.", threadPoolName);
        } else {
            ThreadLogger.warn("Thread pool '{}' is not scheduling!", threadPoolName);
        }
    }

    private synchronized void reschedule(long period, TimeUnit unit) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = scheduler.scheduleAtFixedRate(this, period, period, unit);
            ThreadLogger.info("Reschedule thread pool '{}' with period: {} {}", threadPoolName,
                period, unit);
        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        executingTasks.put(new ExecutingRunnable(r, t), new RunnableExecutionInfo());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        executingTasks.remove(new ExecutingRunnable(r, Thread.currentThread()));
    }

    @Override
    public void run() {
        try {
            int decayedTaskCount = 0;
            for (Map.Entry<ExecutingRunnable, RunnableExecutionInfo> entry : executingTasks.entrySet()) {
                Runnable task = entry.getKey().r;
                Thread executingThread = entry.getKey().t;
                RunnableExecutionInfo executionInfo = entry.getValue();
                long executionTime = System.currentTimeMillis()
                                     - executionInfo.getTaskKickOffTime();

                if (executionTime >= taskTimeoutMilli) {
                    ++decayedTaskCount;

                    if (!executionInfo.isPrinted()) {
                        executionInfo.setPrinted(true);
                        StringBuilder sb = new StringBuilder();
                        for (StackTraceElement e : executingThread.getStackTrace()) {
                            sb.append("    ").append(e).append("\n");
                        }
                        String traceId = traceIdSafari(executingThread);
                        try {
                            ThreadLogger
                                .warn(
                                    "Task {} in thread pool {} started on {}{} exceeds the limit of {} execution time with stack trace:\n    {}",
                                    task, getThreadPoolName(), DATE_FORMAT.format(Instant
                                        .ofEpochMilli(executionInfo.getTaskKickOffTime())),
                                    traceId == null ? "" : " with traceId " + traceId,
                                    getTaskTimeout() + getTimeUnit().toString(), sb.toString()
                                        .trim());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // threadPoolName, #queue, #executing, #idle, #pool, #decayed
            ThreadLogger.info("Thread pool '{}' info: [{},{},{},{},{}]", getThreadPoolName(), this
                .getQueue().size(), executingTasks.size(),
                this.getPoolSize() - executingTasks.size(), this.getPoolSize(), decayedTaskCount);
        } catch (Throwable e) {
            ThreadLogger.warn("ThreadPool '{}' is interrupted when running: {}",
                this.threadPoolName, e);
        }
    }

    /**
     * Search in thread <code>t</code> for traceId if used in SOFA-RPC context.
     * This method is protected in that subclass may need to customized logic.
     * Using reflection not only because threadLocal fields of thread are private,
     * but also we don't want to introduce tracer dependency.
     * @param t the thread
     * @return traceId, maybe null if not found
     */
    protected String traceIdSafari(Thread t) {
        try {
            for (Object o : (Object[]) ClassUtil.getField("table",
                ClassUtil.getField("threadLocals", t))) {
                if (o != null) {
                    try {
                        return ClassUtil.getField(
                            "traceId",
                            ClassUtil.getField("sofaTracerSpanContext",
                                ClassUtil.getField("value", o)));
                    } catch (Throwable e) {
                        // do nothing
                    }
                }
            }
        } catch (Throwable e) {
            // This method shouldn't interfere with normal execution flow
            return null;
        }
        return null;
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        ThreadPoolGovernor.unregisterThreadPoolExecutor(this.threadPoolName);
        this.threadPoolName = threadPoolName;
        ThreadPoolGovernor.registerThreadPoolExecutor(threadPoolName, this);
    }

    public void setPeriod(long period) {
        this.period = period;
        reschedule(period, timeUnit);
    }

    public long getTaskTimeout() {
        return taskTimeout;
    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
        this.taskTimeoutMilli = timeUnit.toMillis(taskTimeout);
        ThreadLogger.info("Updated '{}' taskTimeout to {} {}", threadPoolName, taskTimeout,
            timeUnit);
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getPeriod() {
        return period;
    }

    static class ExecutingRunnable {
        public Runnable r;
        public Thread t;

        public ExecutingRunnable(Runnable r, Thread t) {
            this.r = r;
            this.t = t;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (obj instanceof ExecutingRunnable) {
                ExecutingRunnable er = (ExecutingRunnable) obj;
                return this.t == er.t && this.r == er.r;
            }
            return false;
        }

        @Override
        public String toString() {
            return r.toString() + t.toString();
        }
    }

    static class RunnableExecutionInfo {
        private volatile boolean printed;
        private long             taskKickOffTime;

        public RunnableExecutionInfo() {
            printed = false;
            taskKickOffTime = System.currentTimeMillis();
        }

        public boolean isPrinted() {
            return printed;
        }

        public void setPrinted(boolean printed) {
            this.printed = printed;
        }

        public long getTaskKickOffTime() {
            return taskKickOffTime;
        }

        public void setTaskKickOffTime(long taskKickOffTime) {
            this.taskKickOffTime = taskKickOffTime;
        }
    }
}
