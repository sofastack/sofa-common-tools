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

import java.util.Arrays;

/**
 * @author zhaowang
 * @version : SofaConfig.java, v 0.1 2020年10月20日 8:01 下午 zhaowang Exp $
 * <p>
 * 通过String key 拿到 ConfigKey。
 */
public class ConfigKey<T> {

    private final String   key;
    /**
     * alias of sofa config, previous alias has higher priority
     */
    private final String[] alias;

    private final T        defaultValue;

    private final String   description;

    /**
     * Indicate that if change this configSource will take effect at runtime
     */
    private final boolean  modifiable;

    public ConfigKey(String key, String[] alias, T defaultValue, boolean modifiable,
                     String description) {
        preCheckNotNull("key", key);
        preCheckNotNull("defaultValue", defaultValue);
        preCheckNotNull("description", description);

        this.key = key;
        if (null == alias) {
            this.alias = new String[0];
        } else {
            this.alias = alias;
        }
        this.defaultValue = defaultValue;
        this.modifiable = modifiable;
        this.description = description;
    }

    public static <T> ConfigKey<T> build(String key, T defaultValue, boolean modifiable, String description) {
        return new ConfigKey<>(key, null, defaultValue, modifiable, description);
    }

    public static <T> ConfigKey<T> build(String key, T defaultValue, boolean modifiable, String description, String[] alias) {
        return new ConfigKey<>(key, alias, defaultValue, modifiable, description);
    }

    private void preCheckNotNull(String key, Object value) {
        if (value == null) {
            throw new NullPointerException("\"" + key
                                           + "\" of ConfigKey cannot be null,please check it");
        }
    }

    public String getKey() {
        return key;
    }

    public String[] getAlias() {
        return Arrays.copyOf(alias, alias.length);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public Class<T> getType() {
        return (Class<T>) defaultValue.getClass();
    }

    public boolean isModifiable() {
        return modifiable;
    }
}