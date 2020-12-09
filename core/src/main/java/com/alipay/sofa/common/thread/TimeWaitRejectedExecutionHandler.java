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
import com.alipay.sofa.common.utils.TimeWaitRunner;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaowang
 * @version : SofaRejectedExecutionHandler.java, v 0.1 2020年08月20日 8:05 下午 zhaowang Exp $
 */
public class TimeWaitRejectedExecutionHandler implements RejectedExecutionHandler {

    private RejectedExecutionHandler delegate;
    private TimeWaitRunner           timeWaitRunner;
    private SofaThreadPoolExecutor   threadPoolExecutor;

    public TimeWaitRejectedExecutionHandler(SofaThreadPoolExecutor executor, long waitTime,
                                            TimeUnit timeUnit) {
        this.timeWaitRunner = new TimeWaitRunner(timeUnit.toMillis(waitTime));
        this.delegate = executor.getRejectedExecutionHandler();
        this.threadPoolExecutor = executor;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        timeWaitRunner.doWithRunnable(this::logStackTrace);
        getDelegate().rejectedExecution(r, executor);
    }

    private void logStackTrace() {
        if (threadPoolExecutor != null) {
            String threadPoolName = threadPoolExecutor.getConfig().getThreadPoolName();
            String allStackTrace = getAllStackTrace(threadPoolExecutor);
            ThreadLogger.error(
                "Queue of thread pool {} is full with all stack trace: \n    {}\n\n",
                threadPoolName, allStackTrace);
        }
    }

    private String getAllStackTrace(SofaThreadPoolExecutor threadPoolExecutor) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<ExecutingRunnable, Long> entry : threadPoolExecutor.getStatistics()
            .getExecutingTasks().entrySet()) {
            for (StackTraceElement e : entry.getKey().thread.getStackTrace()) {
                sb.append("    ").append(e).append("\n");
            }
        }
        return sb.toString();
    }

    public RejectedExecutionHandler getDelegate() {
        return delegate;
    }

    public void setDelegate(RejectedExecutionHandler delegate) {
        this.delegate = delegate;
    }

    public void setTimeWaitRunner(TimeWaitRunner timeWaitRunner) {
        this.timeWaitRunner = timeWaitRunner;
    }

    public void setThreadPoolExecutor(SofaThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
