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
package com.alipay.sofa.common.tracer;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author huzijie
 * @version TracerIdConverter.java, v 0.1 2020年11月11日 5:40 下午 huzijie Exp $
 */
public class TracerIdConverterTest {

    @Test
    public void testTraceIdConverter() throws NoSuchFieldException, IllegalAccessException {
        TracerIdConverter converter = TracerIdConverter.getInstance();
        String traceId = converter.traceIdSafari(Thread.currentThread());
        Assert.assertEquals("sampleTraceId", traceId);
        Field field = TracerIdConverter.class.getDeclaredField("tracerIdAdapter");
        field.setAccessible(true);
        TracerIdAdapter tracerIdAdapter = (TracerIdAdapter) field.get(converter);
        field.set(converter, null);
        traceId = converter.traceIdSafari(Thread.currentThread());
        Assert.assertNull(traceId);
        field.set(converter, tracerIdAdapter);
    }
}
