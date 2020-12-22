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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The running statics of a {@link ThreadPoolExecutor}
 * @author huzijie
 * @version ThreadPoolStatistics.java, v 0.1 2020年10月26日 5:38 下午 huzijie Exp $
 */
public class ThreadPoolStatistics {
    /**
     * the counted {@link ThreadPoolExecutor}
     */
    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * the key is executing tasks, the value is dequeue time when executor is {@link SofaScheduledThreadPoolExecutor}
     */
    private final Map<ExecutingRunnable, Long> executingTasks = new ConcurrentHashMap<>();

    /**
     * the total time for task executing
     */
    AtomicLong totalRunningTime          = new AtomicLong();

    /**
     * the total time for task in queue
     */
    AtomicLong totalStayInQueueTime = new AtomicLong();

    /**
     * total tasks put to thread pool
     */
    AtomicLong totalTaskCount            = new AtomicLong();

    public ThreadPoolStatistics(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * Return the running tasks of the {@link ThreadPoolExecutor}
     * @return the executingTasks
     */
    public Map<ExecutingRunnable, Long> getExecutingTasks() {
        return executingTasks;
    }

    /**
     * Return the queue size of the {@link ThreadPoolExecutor}
     * @return the queue size
     */
    public long getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    /**
     * Return the pool size og the {@link ThreadPoolExecutor}
     * @return the pool size
     */
    public long getPoolSize() {
        return threadPoolExecutor.getPoolSize();
    }

    /**
     * add total running time
     * @param runningTime the added runningTime
     */
    public void addTotalRunningTime(long runningTime) {
        totalRunningTime.addAndGet(runningTime);
    }

    /**
     * add total stay in queue time
     * @param stayInQueueTime the added stay in queue time
     */
    public void addTotalStayInQueueTime(long stayInQueueTime) {
        totalStayInQueueTime.addAndGet(stayInQueueTime);
    }

    /**
     * increase total task count
     */
    public void addTotalTaskCount() {
        totalTaskCount.incrementAndGet();
    }

    /**
     * return the total task count
     * @return the total task count
     */
    public long getTotalTaskCount() {
        return totalTaskCount.get();
    }

    /**
     * get the average running time
     * @return average running time
     */
    public long getAverageRunningTime() {
        return this.totalTaskCount.get() == 0 ? -1 : this.totalRunningTime.get()
                / this.totalTaskCount.get();
    }

    /**
     * get the average stay in queue time
     * @return average stay in queue time
     */
    public long getAverageStayInQueueTime() {
        return this.totalTaskCount.get() == 0 ? -1 : this.totalStayInQueueTime.get()
                / this.totalTaskCount.get();
    }

    /**
     * reset each statics, it may cause the result inaccurate
     */
    public void resetAverageStatics() {
        this.totalTaskCount.set(0);
        this.totalRunningTime.set(0);
        this.totalStayInQueueTime.set(0);
    }
}
