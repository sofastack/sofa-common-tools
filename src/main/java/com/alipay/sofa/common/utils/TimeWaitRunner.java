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
package com.alipay.sofa.common.utils;

/**
 * @author zhaowang
 * @version : TimeWaitLogger.java, v 0.1 2020年08月20日 7:46 下午 zhaowang Exp $
 */
public class TimeWaitRunner {
    private final long    waitTime;
    private final boolean runImmediately;

    private volatile long lastLogTime;

    public TimeWaitRunner(long waitTimeMills) {
        this.waitTime = waitTimeMills;
        this.runImmediately = false;
    }

    public TimeWaitRunner(long waitTimeMills, boolean runImmediately) {
        this.waitTime = waitTimeMills;
        this.runImmediately = runImmediately;
    }

    public void doWithRunnable(Runnable runnable) {
        long currentTimeMillis = System.currentTimeMillis();
        if (runImmediately) {
            runnable.run();
        } else if (currentTimeMillis > lastLogTime + waitTime) {
            synchronized (this) {
                if (currentTimeMillis > lastLogTime + waitTime) {
                    lastLogTime = currentTimeMillis;
                    runnable.run();
                }
            }

        }

    }

}