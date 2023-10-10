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
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * @author huzijie
 * @version SofaTracerCommandFactoryTest.java, v 0.1 2023年09月26日 3:13 PM huzijie Exp $
 */
public class SofaTracerCommandFactoryTest {

    @Test
    public void ofExecutingRunnable() {
        Runnable runnable = () -> {};
        Assert.assertTrue(SofaTracerCommandFactory.ofExecutingRunnable(runnable) instanceof SofaTracerCommandFactory.SofaTracerExecutingRunnable);
    }

    @Test
    public void ofRunnable() {
        Runnable runnable = () -> {};
        Runnable newRunnable = SofaTracerCommandFactory.ofRunnable(runnable);
        Assert.assertTrue(newRunnable instanceof SofaTracerRunnable);
        Assert.assertNotEquals(runnable, newRunnable);

        Runnable duplicateWrapRunnable = SofaTracerCommandFactory.ofRunnable(newRunnable);
        Assert.assertTrue(duplicateWrapRunnable instanceof SofaTracerRunnable);
        Assert.assertEquals(duplicateWrapRunnable, newRunnable);
    }

    @Test
    public void ofCallable() {
        Callable<Void> callable = () -> null;
        Callable<Void> newCallable = SofaTracerCommandFactory.ofCallable(callable);
        Assert.assertTrue(newCallable instanceof SofaTracerCallable);
        Assert.assertNotEquals(callable, newCallable);

        Callable<Void> duplicateWrapCallable = SofaTracerCommandFactory.ofCallable(newCallable);
        Assert.assertTrue(duplicateWrapCallable instanceof SofaTracerCallable);
        Assert.assertEquals(duplicateWrapCallable, duplicateWrapCallable);
    }
}
