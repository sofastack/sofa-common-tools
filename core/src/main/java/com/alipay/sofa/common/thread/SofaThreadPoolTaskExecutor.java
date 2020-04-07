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
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/23
 */
public class SofaThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public static final String     SIMPLE_CLASS_NAME    = SofaThreadPoolTaskExecutor.class
                                                            .getSimpleName();
    private static long            DEFAULT_TASK_TIMEOUT = 30000;
    private static long            DEFAULT_PERIOD       = 5000;
    private static TimeUnit        DEFAULT_TIME_UNIT    = TimeUnit.MILLISECONDS;

    private SofaThreadPoolExecutor sofaThreadPoolExecutor;

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
                                                 RejectedExecutionHandler rejectedExecutionHandler) {
        Integer queueCapacity = ClassUtil.getField("queueCapacity", this);
        final TaskDecorator taskDecorator = ClassUtil.getField("taskDecorator", this);

        BlockingQueue<Runnable> queue = createQueue(queueCapacity);

        SofaThreadPoolExecutor executor;

        if (taskDecorator != null) {
            executor = new SofaThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler, createName(), DEFAULT_TASK_TIMEOUT, DEFAULT_PERIOD,
                DEFAULT_TIME_UNIT) {
                @Override
                public void execute(Runnable command) {
                    super.execute(taskDecorator.decorate(command));
                }
            };
        } else {
            executor = new SofaThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler, createName(), DEFAULT_TASK_TIMEOUT, DEFAULT_PERIOD,
                DEFAULT_TIME_UNIT);
        }

        Boolean allowCoreThreadTimeOut = ClassUtil.getField("allowCoreThreadTimeOut", this);
        if (allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }

        ClassUtil.setField("threadPoolExecutor", this, executor);
        this.sofaThreadPoolExecutor = executor;
        return executor;
    }

    private String createName() {
        return SIMPLE_CLASS_NAME + String.format("%08x", this.hashCode());
    }

    public String getThreadPoolName() {
        return sofaThreadPoolExecutor.getName();
    }

    public void setThreadPoolName(String threadPoolName) {
        sofaThreadPoolExecutor.setName(threadPoolName);
    }

    public void setTaskTimeout(long taskTimeout) {
        sofaThreadPoolExecutor.setTaskTimeout(taskTimeout);
    }

    public long getTaskTimeout() {
        return sofaThreadPoolExecutor.getTaskTimeout();
    }

    public void setPeriod(long period) {
        sofaThreadPoolExecutor.setPeriod(period);
    }

    public long getPeriod() {
        return sofaThreadPoolExecutor.getPeriod();
    }

    public TimeUnit getTimeUnit() {
        return sofaThreadPoolExecutor.getTimeUnit();
    }
}
