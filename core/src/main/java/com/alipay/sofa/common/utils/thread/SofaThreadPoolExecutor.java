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
import java.util.concurrent.*;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2020/3/16
 */
public class SofaThreadPoolExecutor extends ThreadPoolExecutor implements Runnable {
    private static long                          DEFAULT_TASK_TIMEOUT = 30;
    private static long                          DEFAULT_PERIOD       = 5;
    private static TimeUnit                      DEFAULT_TIME_UNIT    = TimeUnit.SECONDS;

    private Logger                               logger               = LoggerFactory
                                                                          .getLogger(SofaThreadPoolExecutor.class);

    private String                               name;

    private long                                 taskTimeout          = DEFAULT_TASK_TIMEOUT;
    private long                                 period               = DEFAULT_PERIOD;
    private TimeUnit                             timeUnit             = DEFAULT_TIME_UNIT;

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
     * @param taskTimeout task execution timeout
     * @param period task checking and logging period
     * @param timeUnit unit of taskTimeout and period
     */
    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                  long taskTimeout, long period, TimeUnit timeUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.taskTimeout = taskTimeout;
        this.period = period;
        this.timeUnit = timeUnit;
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        scheduleAndRegister(period, timeUnit);
    }

    public SofaThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        scheduleAndRegister(period, timeUnit);
    }

    private void scheduleAndRegister(long period, TimeUnit unit) {
        ThreadPoolGovernor.scheduler.scheduleAtFixedRate(this, period, period, unit);
        ThreadPoolGovernor.registerThreadPoolExecutor(this);
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
        int decayedTaskCount = 0;
        for (Runnable task : executingTasks.keySet()) {
            RunnableExecutionInfo executionInfo = executingTasks.get(task);
            executionInfo.increaseBy(period);

            if (executionInfo.getExecutionTime() >= taskTimeout) {
                ++decayedTaskCount;

                logger.warn(String.format(
                    "Task %s exceeds the limit of execution time with stack trace:", task));
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement e : executionInfo.getThread().getStackTrace()) {
                    sb.append("    ").append(e).append("\n");
                }
                logger.warn(sb.toString());
            }
        }

        logger.info(String.format("[%d,%d,%d,%d,%d]", this.getQueue().size(),
            executingTasks.size(), this.getPoolSize() - executingTasks.size(), this.getPoolSize(),
            decayedTaskCount));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    static class RunnableExecutionInfo {
        private long   executionTime;
        private Thread thread;

        public RunnableExecutionInfo(Thread thread) {
            executionTime = 0;
            this.thread = thread;
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
    }
}
