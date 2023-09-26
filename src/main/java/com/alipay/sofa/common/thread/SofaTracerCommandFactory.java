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

import com.alipay.common.tracer.core.async.SofaTracerCallable;
import com.alipay.common.tracer.core.async.SofaTracerRunnable;
import com.alipay.sofa.common.utils.ClassUtil;

import java.util.concurrent.Callable;

/**
 * Factory to create SOFA-Tracer work command.
 * @author huzijie
 * @version SofaTracerCommandFactory.java, v 0.1 2023年09月26日 2:53 PM huzijie Exp $
 */
public class SofaTracerCommandFactory {

    private static final String  SOFA_TRACER_RUNNABLE_CLASS_NAME = "com.alipay.common.tracer.core.async.SofaTracerRunnable";
    private static final boolean SOFA_TRACER_CLASS_PRESENT       = ClassUtil
                                                                     .isPresent(
                                                                         SOFA_TRACER_RUNNABLE_CLASS_NAME,
                                                                         SofaTracerCommandFactory.class
                                                                             .getClassLoader());

    static ExecutingRunnable ofExecutingRunnable(Runnable runnable) {
        if (!SOFA_TRACER_CLASS_PRESENT) {
            return new ExecutingRunnable(runnable);
        }
        return new SofaTracerCommandFactory.SofaTracerExecutingRunnable(runnable);
    }

    static Runnable ofRunnable(Runnable runnable) {
        if (!SOFA_TRACER_CLASS_PRESENT) {
            return runnable;
        }
        if (runnable instanceof SofaTracerRunnable) {
            return runnable;
        }
        return new SofaTracerRunnable(runnable);
    }

    static <V> Callable<V> ofCallable(Callable<V> callable) {
        if (!SOFA_TRACER_CLASS_PRESENT) {
            return callable;
        }
        if (callable instanceof SofaTracerCallable) {
            return callable;
        }
        return new SofaTracerCallable<>(callable);
    }

    /**
     * The wrapper to the {@link ExecutingRunnable} to transmit SofaTracerSpan.
     * @author huzijie
     * @version SofaTracerExecutingRunnable.java, v 0.1 2023年09月26日 11:45 AM huzijie Exp $
     */
    public static class SofaTracerExecutingRunnable extends ExecutingRunnable {

        private final SofaTracerRunnable sofaTracerRunnable;

        public SofaTracerExecutingRunnable(Runnable originRunnable) {
            super(originRunnable);
            if (originRunnable instanceof SofaTracerRunnable) {
                this.sofaTracerRunnable = (SofaTracerRunnable) originRunnable;
            } else {
                this.sofaTracerRunnable = new SofaTracerRunnable(originRunnable);
            }
        }

        @Override
        public void run() {
            sofaTracerRunnable.run();
        }
    }
}
