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
package com.alipay.sofa.common.config;

import com.alipay.sofa.common.config.converter.DefaultConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhaowang
 * @version : DefaultConverterTest.java, v 0.1 2020年12月09日 1:58 下午 zhaowang Exp $
 */
public class DefaultConverterTest {

    @Test
    public void testDefaultConverter() {
        DefaultConverter converter = new DefaultConverter();

        Long l = converter.convert(null, Long.class);
        Assert.assertNull(l);

        Class<Long> clazz = null;
        try {
            Object convert = converter.convert("1", clazz);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("Type cannot be null", e.getMessage());
        }

        Object result;
        result = converter.convert("1.23", float.class);
        Assert.assertEquals(1.23f, result);
        result = converter.convert("1.23", double.class);
        Assert.assertEquals(1.23d, result);
        result = converter.convert("1", byte.class);
        Assert.assertEquals((byte) 1, result);
        result = converter.convert("1", short.class);
        Assert.assertEquals((short) 1, result);
        result = converter.convert("1", int.class);
        Assert.assertEquals(1, result);
        result = converter.convert("1", long.class);
        Assert.assertEquals((long) 1, result);
        result = converter.convert("1", char.class);
        Assert.assertEquals('1', result);
        result = converter.convert("1", boolean.class);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testListMap() {
        DefaultConverter converter = new DefaultConverter();
        String str = "[{a=1}]";

        List<HashMap<Object, Object>> result = converter.convert(str, List.class);
        Assert.assertNotNull(result.get(0));
        Assert.assertEquals("1", result.get(0).get("a"));

        str = "[{a=1,b=2}{c=3,d=4}]";
        result = converter.convert(str, List.class);
        Assert.assertNotNull(result.get(0));
        Assert.assertNotNull(result.get(1));
        Assert.assertEquals("1", result.get(0).get("a"));
        Assert.assertEquals("2", result.get(0).get("b"));
        Assert.assertEquals("3", result.get(1).get("c"));
        Assert.assertEquals("4", result.get(1).get("d"));

    }

    @Test
    public void testNotSupport() {
        DefaultConverter converter = new DefaultConverter();

        try {
            HashMap convert = converter.convert("1", HashMap.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("DefaultConverter not support type [class java.util.HashMap],"
                                + "failed to convert value [1].", e.getMessage());
        }

    }
}