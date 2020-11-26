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

import com.alipay.sofa.common.utils.ClassUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Sofa thread pool based on {@link ThreadPoolTaskScheduler}
 * @author huzijie
 * @version SofaThreadPoolTaskScheduler.java, v 0.1 2020年11月09日 2:31 下午 huzijie Exp $
 */
public class SofaThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    public static final String                SIMPLE_CLASS_NAME = SofaThreadPoolTaskScheduler.class
                                                                    .getSimpleName();
    protected SofaScheduledThreadPoolExecutor sofaScheduledThreadPoolExecutor;

    protected String                          threadPoolName;

    protected String                          spaceName;

    protected long                            taskTimeout;

    protected long                            period;

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
                                                 RejectedExecutionHandler rejectedExecutionHandler) {
        // When used as Spring bean, setter method is called before init method
        if (threadPoolName == null) {
            threadPoolName = createName();
        }

        SofaScheduledThreadPoolExecutor executor = new SofaScheduledThreadPoolExecutor(
            getPoolSize(), threadFactory, rejectedExecutionHandler, threadPoolName, spaceName,
            taskTimeout, period, TimeUnit.MILLISECONDS);

        Boolean removeOnCancelPolicy = ClassUtil.getField("removeOnCancelPolicy", this);
        if (removeOnCancelPolicy) {
            executor.setRemoveOnCancelPolicy(true);
        }

        ClassUtil.setField("scheduledExecutor", this, executor);
        this.sofaScheduledThreadPoolExecutor = executor;
        return executor;
    }

    protected String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", this.hashCode());
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        if (sofaScheduledThreadPoolExecutor != null) {
            sofaScheduledThreadPoolExecutor.updateThreadPoolName(threadPoolName);
        }
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
        if (sofaScheduledThreadPoolExecutor != null) {
            sofaScheduledThreadPoolExecutor.updatespaceName(spaceName);
        }
    }

    public long getTaskTimeout() {
        if (sofaScheduledThreadPoolExecutor == null) {
            return 0;
        }
        return sofaScheduledThreadPoolExecutor.getConfig().getTaskTimeout();

    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
        if (sofaScheduledThreadPoolExecutor != null) {
            sofaScheduledThreadPoolExecutor.updateTaskTimeout(taskTimeout);
        }
    }

    public long getPeriod() {
        if (sofaScheduledThreadPoolExecutor == null) {
            return 0;
        }
        return sofaScheduledThreadPoolExecutor.getConfig().getPeriod();
    }

    public void setPeriod(long period) {
        this.period = period;
        if (sofaScheduledThreadPoolExecutor != null) {
            sofaScheduledThreadPoolExecutor.updatePeriod(period);
        }
    }

    public TimeUnit getTimeUnit() {
        if (sofaScheduledThreadPoolExecutor == null) {
            return TimeUnit.MILLISECONDS;
        }
        return sofaScheduledThreadPoolExecutor.getConfig().getTimeUnit();
    }
}
