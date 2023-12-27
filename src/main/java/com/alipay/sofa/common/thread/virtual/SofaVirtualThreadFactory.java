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
package com.alipay.sofa.common.thread.virtual;

import java.util.concurrent.ExecutorService;

/**
 * Delegate for virtual thread handling on JDK 21.
 * This is a dummy version for reachability on JDK less than 21.
 *
 * @author huzijie
 * @version SofaVirtualThreadFactory.java, v 0.1 2023年11月20日 3:57 PM huzijie Exp $
 */
public class SofaVirtualThreadFactory {

    /**
     * Create a virtual thread with name.
     *
     * @param name thread name
     * @param runnable task to run with thread
     * @return a virtual thread with runnable
     * @since 2.1.0
     */
    public static Thread ofThread(String name, Runnable runnable) {
        throw new UnsupportedOperationException("Virtual threads not supported on JDK <21");
    }

    /**
     * Create a virtual thread with name.
     *
     * @param name thread name
     * @param inheritInheritableThreadLocals {@code true} to inherit, {@code false} to not inherit
     * @param uncaughtExceptionHandler uncaught exception handler
     * @param runnable task to run with thread
     * @return a virtual thread with runnable
     * @since 2.1.0
     */

    public static Thread ofThread(String name, boolean inheritInheritableThreadLocals,
                                  Thread.UncaughtExceptionHandler uncaughtExceptionHandler,
                                  Runnable runnable) {
        throw new UnsupportedOperationException("Virtual threads not supported on JDK <21");
    }

    /**
     * Creates an Executor that starts a new virtual Thread for each task.
     * The number of threads created by the Executor is unbounded.
     *
     * @param prefix thread prefix
     * @return a new executor that creates a new virtual Thread for each task
     * @since 2.1.0
     */
    public static ExecutorService ofExecutorService(String prefix) {
        throw new UnsupportedOperationException("Virtual threads not supported on JDK <21");
    }

    /**
     * Creates an Executor that starts a new virtual Thread for each task.
     * The number of threads created by the Executor is unbounded.
     *
     * @param prefix thread prefix
     * @param start the starting value of the counter
     * @param inheritInheritableThreadLocals {@code true} to inherit, {@code false} to not inherit
     * @param uncaughtExceptionHandler uncaught exception handler
     * @return a new executor that creates a new virtual Thread for each task
     * @since 2.1.0
     */
    public static ExecutorService ofExecutorService(String prefix,
                                                    long start,
                                                    boolean inheritInheritableThreadLocals,
                                                    Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        throw new UnsupportedOperationException("Virtual threads not supported on JDK <21");
    }
}
