package com.alipay.sofa.common.thread.virtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Delegate for virtual thread handling on JDK 21.
 * This is the actual version compiled against JDK 21.
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
        return Thread.ofVirtual().name(name).unstarted(runnable);
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

    public static Thread ofThread(String name, boolean inheritInheritableThreadLocals, Thread.UncaughtExceptionHandler uncaughtExceptionHandler , Runnable runnable) {
        return Thread.ofVirtual().name(name).inheritInheritableThreadLocals(inheritInheritableThreadLocals)
                .uncaughtExceptionHandler(uncaughtExceptionHandler).unstarted(runnable);
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
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name(prefix, 0).factory());
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
    public static ExecutorService ofExecutorService(String prefix, long start, boolean inheritInheritableThreadLocals, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        ThreadFactory threadFactory = Thread.ofVirtual().name(prefix, start)
                .inheritInheritableThreadLocals(inheritInheritableThreadLocals)
                .uncaughtExceptionHandler(uncaughtExceptionHandler)
                .factory();
        return Executors.newThreadPerTaskExecutor(threadFactory);
    }
}
