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

    String   key;
    /**
     * alias of sofa config, previous alias has higher priority
     */
    String[] alias;

    T        defaultValue;

    String   description;

    Class<T> type;

    /**
     * change in this config will take effect at runtime
     */
    boolean  instantly;

    public SofaConfig(String key, String[] alias, Class<T> type, T defaultValue, boolean instantly,
                      String description) {
        this.key = key;
        this.alias = alias;
        this.type = type;
        this.defaultValue = defaultValue;
        this.instantly = instantly;
        this.description = description;
    }

    public static <T> SofaConfig<T> build(String key, Class<T> type, T defaultValue, boolean instantly, String description) {
        return new SofaConfig<>(key, null, type, defaultValue, instantly, description);
    }

    public static <T> SofaConfig<T> build(String key, Class<T> type, T defaultValue, boolean instantly, String description,String[] alias) {
        return new SofaConfig<>(key, alias, type, defaultValue, instantly, description);
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
        return type;
    }

    public boolean isInstantly() {
        return instantly;
    }

}