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

/**
 * The wrapper to the {@link Runnable} to save it's execute {@link Thread}
 * @author huzijie
 * @version ExecutingRunnable.java, v 0.1 2020年10月26日 4:22 下午 huzijie Exp $
 */
class ExecutingRunnable implements Runnable {

    public Runnable          originRunnable;

    public Thread            thread;

    private long             enqueueTime;

    private long             dequeueTime;

    private long             finishTime;

    private volatile boolean printed;

    public ExecutingRunnable(Runnable originRunnable) {
        if (originRunnable == null) {
            throw new NullPointerException();
        }
        this.originRunnable = originRunnable;
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
            return this.thread == er.thread && this.originRunnable == er.originRunnable;
        }
        return false;
    }

    @Override
    public String toString() {
        if (thread == null) {
            return originRunnable.toString();
        }
        return originRunnable.toString() + thread.toString();
    }

    @Override
    public void run() {
        originRunnable.run();
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(long enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public long getDequeueTime() {
        return dequeueTime;
    }

    public void setDequeueTime(long dequeueTime) {
        this.dequeueTime = dequeueTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public long getRunningTime() {
        return finishTime - dequeueTime;
    }

    public long getStayInQueueTime() {
        return dequeueTime - enqueueTime;
    }
}
