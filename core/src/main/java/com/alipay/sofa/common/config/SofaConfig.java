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

/**
 * @author zhaowang
 * @version : SofaConfig.java, v 0.1 2020年10月20日 8:01 下午 zhaowang Exp $
 */
public class SofaConfig<T> {

    private final String   key;
    /**
     * alias of sofa config, previous alias has higher priority
     */
    private final String[] alias;

    private final T        defaultValue;

    private final String   description;

    private T              value;

    /**
     * change in this config will take effect at runtime
     * todo add listener
     */
    private final boolean  instantly;

    public SofaConfig(String key, String[] alias, T defaultValue, boolean instantly,
                      String description) {
        this.key = key;
        this.alias = alias;
        this.defaultValue = defaultValue;
        this.instantly = instantly;
        this.description = description;
    }

    public static <T> SofaConfig<T> build(String key, T defaultValue, boolean instantly, String description) {
        return new SofaConfig<>(key, null , defaultValue, instantly, description);
    }

    public static <T> SofaConfig<T> build(String key, T defaultValue, boolean instantly, String description,String[] alias) {
        return new SofaConfig<>(key, alias, defaultValue, instantly, description);
    }

    public String getKey() {
        return key;
    }

    public String[] getAlias() {
        return alias;
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

    public boolean isInstantly() {
        return instantly;
    }

    public T getOrDefault() {
        return SofaCommonConfig.getInstance().getOrDefault(this);
    }

    public static class Value<T> {
        private T            value;
        private ConfigSource configSource;

    }

}