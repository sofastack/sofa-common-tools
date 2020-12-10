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

import java.util.concurrent.*;

/**
 * Sofa thread pool based on {@link ThreadPoolTaskExecutor}
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 * Created on 2020/3/23
 */
public class SofaThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    public static final String       SIMPLE_CLASS_NAME = SofaThreadPoolTaskExecutor.class
                                                           .getSimpleName();
    protected SofaThreadPoolExecutor sofaThreadPoolExecutor;

    protected String                 threadPoolName;

    protected String                 spaceName;

    protected long                   taskTimeout;

    protected long                   period;

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
                                                 RejectedExecutionHandler rejectedExecutionHandler) {
        Integer queueCapacity = ClassUtil.getField("queueCapacity", this);
        final TaskDecorator taskDecorator = ClassUtil.getField("taskDecorator", this);

        BlockingQueue<Runnable> queue = createQueue(queueCapacity);

        SofaThreadPoolExecutor executor;

        // When used as Spring bean, setter method is called before init method
        if (threadPoolName == null) {
            threadPoolName = createName();
        }

        if (taskDecorator != null) {
            executor = new SofaThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler, threadPoolName, spaceName, taskTimeout, period,
                TimeUnit.MILLISECONDS) {
                @Override
                public void execute(Runnable command) {
                    super.execute(taskDecorator.decorate(command));
                }
            };
        } else {
            executor = new SofaThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler, threadPoolName, spaceName, taskTimeout, period,
                TimeUnit.MILLISECONDS);
        }

        Boolean allowCoreThreadTimeOut = ClassUtil.getField("allowCoreThreadTimeOut", this);
        if (allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }

        ClassUtil.setField("threadPoolExecutor", this, executor);
        this.sofaThreadPoolExecutor = executor;
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
        if (sofaThreadPoolExecutor != null) {
            sofaThreadPoolExecutor.updateThreadPoolName(threadPoolName);
        }
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
        if (sofaThreadPoolExecutor != null) {
            sofaThreadPoolExecutor.updateSpaceName(spaceName);
        }
    }

    public long getTaskTimeout() {
        if (sofaThreadPoolExecutor == null) {
            return 0;
        }
        return sofaThreadPoolExecutor.getConfig().getTaskTimeout();
    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
        if (sofaThreadPoolExecutor != null) {
            sofaThreadPoolExecutor.updateTaskTimeout(taskTimeout);
        }
    }

    public long getPeriod() {
        if (sofaThreadPoolExecutor == null) {
            return 0;
        }
        return sofaThreadPoolExecutor.getConfig().getPeriod();
    }

    public void setPeriod(long period) {
        this.period = period;
        if (sofaThreadPoolExecutor != null) {
            sofaThreadPoolExecutor.updatePeriod(period);
        }
    }

    public TimeUnit getTimeUnit() {
        if (sofaThreadPoolExecutor == null) {
            return TimeUnit.MILLISECONDS;
        }
        return sofaThreadPoolExecutor.getConfig().getTimeUnit();
    }
}
