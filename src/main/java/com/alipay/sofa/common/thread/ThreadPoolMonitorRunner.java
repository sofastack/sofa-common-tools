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
import com.alipay.sofa.common.tracer.TracerIdAdapter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * The runner to monitor the thread pool
 * @author huzijie
 * @version TheadPoolRunner.java, v 0.1 2020年10月26日 4:42 下午 huzijie Exp $
 */
public class ThreadPoolMonitorRunner implements Runnable {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
                                                           "yyyy-MM-dd HH:mm:ss,SSS").withZone(
                                                           ZoneId.systemDefault());
    private final ThreadPoolConfig         config;
    private final ThreadPoolStatistics     statistics;

    public ThreadPoolMonitorRunner(ThreadPoolConfig threadPoolConfig,
                                   ThreadPoolStatistics threadPoolStatistics) {
        this.statistics = threadPoolStatistics;
        this.config = threadPoolConfig;
    }

    @Override
    public void run() {
        try {
            if (ThreadPoolGovernor.getInstance().isGlobalMonitorLoggable()) {
                int decayedTaskCount = 0;
                for (Map.Entry<ExecutingRunnable, Long> entry : statistics.getExecutingTasks()
                    .entrySet()) {
                    decayedTaskCount = calculateDecayedTaskCounts(decayedTaskCount, entry);
                }

                // threadPoolName, #queue, #executing, #idle, #pool, #decayed
                ThreadLogger.info("Thread pool '{}' info: [{},{},{},{},{}]", config.getIdentity(),
                    statistics.getQueueSize(), statistics.getExecutingTasks().size(),
                    statistics.getPoolSize() - statistics.getExecutingTasks().size(),
                    statistics.getPoolSize(), decayedTaskCount);
                if (statistics.getTotalTaskCount() != 0) {
                    // just log for thread pool which has task executed
                    // SofaScheduledThreadPoolExecutor don't count the in queue time, it's always 0
                    // threadPoolName, #averageStayInQueueTime, #averageRunningTime
                    ThreadLogger.info("Thread pool '{}' average static info: [{},{}]",
                        config.getIdentity(), statistics.getAverageStayInQueueTime(),
                        statistics.getAverageRunningTime());
                    statistics.resetAverageStatics();
                }
            }
        } catch (Throwable e) {
            ThreadLogger.warn("ThreadPool '{}' is interrupted when running: {}",
                this.config.getIdentity(), e);
        }
    }

    /**
     * Calculate the count of decayed tasks
     * @param decayedTaskCount the count to update
     * @param entry the executing tasks
     * @return the updated count
     */
    private int calculateDecayedTaskCounts(int decayedTaskCount,
                                           Map.Entry<ExecutingRunnable, Long> entry) {
        ExecutingRunnable task = entry.getKey();
        // SofaThreadPoolExecutor use task.getDequeueTime(), SofaScheduledThreadPoolExecutor use entry.getValue()
        long executionTime = System.currentTimeMillis()
                             - (task.getDequeueTime() == 0 ? entry.getValue() : task
                                 .getDequeueTime());

        if (executionTime >= config.getTaskTimeoutMilli()) {
            ++decayedTaskCount;
            printStackTrace(task, entry.getKey().getThread());
        }
        return decayedTaskCount;
    }

    /**
     * Print the decayed task's stack trace
     * @param task the decayed task
     * @param executingThread the execute thread of the task
     */
    private void printStackTrace(ExecutingRunnable task, Thread executingThread) {
        if (!task.isPrinted()) {
            task.setPrinted(true);
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement e : executingThread.getStackTrace()) {
                sb.append("    ").append(e).append("\n");
            }
            String traceId = TracerIdAdapter.getInstance().traceIdSafari(executingThread);
            try {
                ThreadLogger
                    .warn(
                        "Task {} in thread pool {} started on {}{} exceeds the limit of {} execution time with stack trace:\n    {}",
                        task, config.getIdentity(), DATE_FORMAT.format(Instant.ofEpochMilli(task
                            .getDequeueTime())), traceId == null ? "" : " with traceId " + traceId,
                        config.getTaskTimeout() + config.getTimeUnit().toString(), sb.toString()
                            .trim());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
