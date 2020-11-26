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
package com.alipay.sofa.common.thread.space;

import com.alipay.sofa.common.thread.ThreadPoolGovernor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huzijie
 * @version spaceNameNamedThreadFactory.java, v 0.1 2020年11月10日 8:24 下午 huzijie Exp $
 */
public class SpaceNamedThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup   group;

    private final String        namePrefix;

    private final boolean       isDaemon;

    public SpaceNamedThreadFactory(String threadPoolName, String spaceName) {
        this(threadPoolName, spaceName, false);
    }

    public SpaceNamedThreadFactory(String threadPoolName, String spaceName, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = spaceName + "-" + threadPoolName + "-"
                     + ThreadPoolGovernor.getInstance().getSpaceNameThreadPoolNumber(spaceName)
                     + "-thread-";
        isDaemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(isDaemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
