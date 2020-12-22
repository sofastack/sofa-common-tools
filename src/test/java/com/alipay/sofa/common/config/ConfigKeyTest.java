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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaowang
 * @version : ConfigKeyTest.java, v 0.1 2020年12月09日 10:48 上午 zhaowang Exp $
 */
public class ConfigKeyTest {

    @Test
    public void testInit() {

        try {
            ConfigKey configKey = new ConfigKey(null, null, null, false, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertTrue(e.getMessage().contains("key"));
        }

        try {
            ConfigKey configKey = new ConfigKey("a", null, null, false, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertTrue(e.getMessage().contains("defaultValue"));
        }

        try {
            ConfigKey configKey = new ConfigKey("a", null, "", false, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertTrue(e.getMessage().contains("description"));
        }

        ConfigKey configKey = new ConfigKey("a", null, "", false, "desc");
        Assert.assertNotNull(configKey);
        String[] alias = configKey.getAlias();
        Assert.assertNotNull(alias);
        Assert.assertEquals(0, alias.length);
        Assert.assertFalse(configKey.isModifiable());
        Assert.assertEquals("desc", configKey.getDescription());
        Assert.assertEquals(String.class, configKey.getType());
    }

    @Test
    public void testBuild() {
        ConfigKey<String> key = ConfigKey.build("a", "default", true, "desc");
        Assert.assertEquals("a", key.getKey());
        Assert.assertEquals("default", key.getDefaultValue());
        Assert.assertTrue(key.isModifiable());
        Assert.assertEquals("desc", key.getDescription());
        Assert.assertEquals(0, key.getAlias().length);

        key = ConfigKey.build("a", "default", true, "desc", new String[] { "b", "c" });
        Assert.assertEquals("a", key.getKey());
        Assert.assertEquals("default", key.getDefaultValue());
        Assert.assertTrue(key.isModifiable());
        Assert.assertEquals("desc", key.getDescription());
        Assert.assertEquals(2, key.getAlias().length);

    }
}