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

import com.alipay.sofa.common.utils.StringUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.alipay.sofa.common.thread.SofaThreadPoolConstants.DEFAULT_PERIOD;
import static com.alipay.sofa.common.thread.SofaThreadPoolConstants.DEFAULT_TASK_TIMEOUT;

/**
 * The description of a wrapped {@link ThreadPoolExecutor}
 * @author huzijie
 * @version SofaThreadConfig.java, v 0.1 2020年10月26日 3:27 下午 huzijie Exp $
 */
public class ThreadPoolConfig {

    private String   threadPoolName;

    private String   namespace;

    private long     taskTimeout;

    private long     period;

    private TimeUnit timeUnit;

    private long     taskTimeoutMilli;

    private String   identity;

    public static SofaThreadConfigBuilder newBuilder() {
        return new SofaThreadConfigBuilder();
    }

    public ThreadPoolConfig(SofaThreadConfigBuilder builder) {
        this.threadPoolName = builder.threadPoolName;
        this.namespace = builder.namespace;
        this.taskTimeout = builder.taskTimeout == 0 ? DEFAULT_TASK_TIMEOUT : builder.taskTimeout;
        this.period = builder.period == 0 ? DEFAULT_PERIOD : period;
        this.timeUnit = builder.timeUnit == null ? TimeUnit.MILLISECONDS : builder.timeUnit;
        this.taskTimeoutMilli = this.timeUnit.toMillis(this.taskTimeout);
        this.identity = buildIdentity();
    }

    /**
     * The unique identity for the thread pool, generate by the threadPoolName and the namespace
     * @return the identity
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * The name of the thread pool，it's unique in one namespace
     * @return the threadPoolName
     */
    public String getThreadPoolName() {
        return threadPoolName;
    }

    /**
     * The namespace of the thread pool, it's unique
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * The value to judge whether the task is decayed
     * @return the taskTimeout
     */
    public long getTaskTimeout() {
        return taskTimeout;
    }

    /**
     * The period to monitor the thread pool
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * The {@link TimeUnit} for the period
     * @return The {@link TimeUnit}
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * The milli value of the taskTimeout, generate by the taskTimeout and the {@link TimeUnit}
     * @return the taskTimeoutMilli
     */
    public long getTaskTimeoutMilli() {
        return taskTimeoutMilli;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        this.identity = buildIdentity();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        this.identity = buildIdentity();
    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setTaskTimeoutMilli(long taskTimeoutMilli) {
        this.taskTimeoutMilli = taskTimeoutMilli;
    }

    /**
     * The generate method of the identity
     * @return the generated identity
     */
    private String buildIdentity() {
        if (StringUtil.isEmpty(namespace)) {
            return threadPoolName;
        }
        return namespace + "-" + threadPoolName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ThreadPoolConfig{");
        sb.append("threadPoolName='").append(threadPoolName).append('\'');
        sb.append(", namespace='").append(namespace).append('\'');
        sb.append(", taskTimeout=").append(taskTimeout);
        sb.append(", period=").append(period);
        sb.append(", timeUnit=").append(timeUnit);
        sb.append(", taskTimeoutMilli=").append(taskTimeoutMilli);
        sb.append(", identity='").append(identity).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static final class SofaThreadConfigBuilder {
        private String   threadPoolName;
        private String   namespace;
        private long     taskTimeout;
        private long     period;
        private TimeUnit timeUnit;

        private SofaThreadConfigBuilder() {
        }

        public SofaThreadConfigBuilder threadPoolName(String threadPoolName) {
            this.threadPoolName = threadPoolName;
            return this;
        }

        public SofaThreadConfigBuilder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public SofaThreadConfigBuilder taskTimeout(long taskTimeout) {
            this.taskTimeout = taskTimeout;
            return this;
        }

        public SofaThreadConfigBuilder period(long period) {
            this.period = period;
            return this;
        }

        public SofaThreadConfigBuilder timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public ThreadPoolConfig build() {
            return new ThreadPoolConfig(this);
        }
    }
}