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

/**
 * The running statics of a {@link ThreadPoolExecutor}
 * @author huzijie
 * @version ThreadPoolStatistics.java, v 0.1 2020年10月26日 5:38 下午 huzijie Exp $
 */
public class ThreadPoolStatistics {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Map<ExecutingRunnable, RunnableExecutionInfo> executingTasks = new ConcurrentHashMap<>();

    public ThreadPoolStatistics(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * Return the running tasks of the {@link ThreadPoolExecutor}
     * @return the executingTasks
     */
    public Map<ExecutingRunnable, RunnableExecutionInfo> getExecutingTasks() {
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
}