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

import com.alipay.sofa.common.config.source.AbstractConfigSource;
import com.alipay.sofa.common.config.source.ConfigSourceCacheWrapper;
import com.alipay.sofa.common.config.source.ConfigSourceOrder;
import com.alipay.sofa.common.config.source.SystemPropertyConfigSource;
import com.google.common.cache.CacheBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

/**
 * @author zhaowang
 * @version : ConfigSourceCacheWrapperTest.java, v 0.1 2021年12月20日 8:19 下午 zhaowang
 */
public class ConfigSourceCacheWrapperTest {

    private ConfigSourceCacheWrapper cacheWrapper = new ConfigSourceCacheWrapper(
                                                      new SystemPropertyConfigSource(), 1);

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("key1", "value1");
        System.setProperty("key2", "");
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("key1");
        System.clearProperty("key2");
    }

    @Test
    public void testNull() {
        Assert.assertEquals(null, cacheWrapper.doGetConfig(null));
        Assert.assertEquals(false, cacheWrapper.hasKey(null));
    }

    @Test
    public void testCommon() {
        Assert.assertEquals("value1", cacheWrapper.doGetConfig("key1"));
        Assert.assertEquals("", cacheWrapper.doGetConfig("key2"));
        Assert.assertTrue(cacheWrapper.hasKey("key1"));
        Assert.assertFalse(cacheWrapper.hasKey("key2"));
    }

    @Test
    public void testException() {
        NullPointerException NPE = new NullPointerException();
        ExceptionConfigSource exceptionConfigSource = new ExceptionConfigSource(NPE);
        ConfigSourceCacheWrapper cacheWrapper = new ConfigSourceCacheWrapper(exceptionConfigSource,
            1);
        try {
            cacheWrapper.doGetConfig("");
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(t instanceof NullPointerException);
            Assert.assertSame(NPE, t);
        }

        try {
            cacheWrapper.hasKey("");
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(t instanceof NullPointerException);
            Assert.assertSame(NPE, t);
        }
    }

    @Test
    public void testNotExist() throws InterruptedException {
        String notExistKey = "key3";
        String value = "value3";
        System.clearProperty(notExistKey);
        Assert.assertEquals("", cacheWrapper.doGetConfig(notExistKey));
        System.setProperty(notExistKey, value);
        Assert.assertEquals("", cacheWrapper.doGetConfig(notExistKey));
        Thread.sleep(1000);
        Assert.assertEquals(value, cacheWrapper.doGetConfig(notExistKey));
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("SystemProperty", cacheWrapper.getName());
    }

    @Test
    public void testOrder() {
        Assert.assertEquals(ConfigSourceOrder.SYSTEM_PROPERTY, cacheWrapper.getOrder());
    }

    @Test
    public void testCacheTime() throws InterruptedException {
        System.setProperty("key3", "value3");
        Assert.assertEquals("value3", cacheWrapper.doGetConfig("key3"));
        System.setProperty("key3", "value4");
        Thread.sleep(500);
        Assert.assertEquals("value3", cacheWrapper.doGetConfig("key3"));
        Thread.sleep(500);
        Assert.assertEquals("value4", cacheWrapper.doGetConfig("key3"));
        System.clearProperty("key3");
    }

    @Test
    public void testConstructWithCacheBuild() {
        CacheBuilder cb = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(1))
            .maximumSize(1);
        ConfigSourceCacheWrapper wrapper = new ConfigSourceCacheWrapper(
            new SystemPropertyConfigSource(), cb);

        String key = "key3";
        String notExist = "notExist";
        String value = "value3";
        String difValue = "difValue";
        System.setProperty(key, value);
        Assert.assertEquals(value, wrapper.doGetConfig(key));
        System.setProperty(key, difValue);
        // load from cache
        Assert.assertEquals(value, wrapper.doGetConfig(key));
        // maximumSize = 1 ,cache will be replaced by notExist
        Assert.assertEquals("", wrapper.doGetConfig(notExist));
        // load from delegate
        Assert.assertEquals(difValue, wrapper.doGetConfig(key));
        System.clearProperty(value);
    }

    public class ExceptionConfigSource extends AbstractConfigSource {
        private RuntimeException t;

        public ExceptionConfigSource(RuntimeException t) {
            this.t = t;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        public String doGetConfig(String key) {
            throw t;
        }

        @Override
        public boolean hasKey(String key) {
            throw t;
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}