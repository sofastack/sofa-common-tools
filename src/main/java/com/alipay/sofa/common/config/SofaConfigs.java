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

import com.alipay.sofa.common.config.listener.LogConfigListener;
import com.alipay.sofa.common.config.source.SystemEnvConfigSource;
import com.alipay.sofa.common.config.source.SystemPropertyConfigSource;

import static com.alipay.sofa.common.log.Constants.SOFA_MIDDLEWARE_CONFIG_CACHE_EXPIRE_TIME;
import static com.alipay.sofa.common.log.Constants.SOFA_MIDDLEWARE_CONFIG_CACHE_MAX_SIZE;

/**
 * @author zhaowang
 * @version : SofaCommonConfig.java, v 0.1 2020年12月01日 11:54 上午 zhaowang Exp $
 */
public class SofaConfigs {

    private static final DefaultConfigManager INSTANCE;
    public static final String               DEFAULT_EXPIRE_AFTER_SECOND = "5";
    public static final String               DEFAULT_MAX_SIZE            = "1000";

    static {
        long expireAfterSecond = Long.parseLong(System.getProperty(
            SOFA_MIDDLEWARE_CONFIG_CACHE_EXPIRE_TIME, DEFAULT_EXPIRE_AFTER_SECOND));
        long maxSize = Long.parseLong(System.getProperty(SOFA_MIDDLEWARE_CONFIG_CACHE_MAX_SIZE,
            DEFAULT_MAX_SIZE));

        INSTANCE = new DefaultConfigManager(expireAfterSecond, maxSize);
        // add ConfigSource
        INSTANCE.addConfigSource(new SystemPropertyConfigSource());
        INSTANCE.addConfigSource(new SystemEnvConfigSource());

        // add ConfigListener
        INSTANCE.addConfigListener(new LogConfigListener());
    }

    public static <T> T getOrDefault(ConfigKey<T> key) {
        return INSTANCE.getOrDefault(key);
    }

    public static <T> T getOrCustomDefault(ConfigKey<T> key, T customDefault) {
        return INSTANCE.getOrCustomDefault(key, customDefault);
    }

    public static <T> T getOrCustomDefaultWithCache(ConfigKey<T> key, T customDefault) {
        return INSTANCE.getOrCustomDefaultWithCache(key, customDefault);
    }

    public static <T> T getOrDefaultWithCache(ConfigKey<T> key) {
        return INSTANCE.getOrDefaultWithCache(key);
    }

    public static void addConfigSource(ConfigSource configSource) {
        INSTANCE.addConfigSource(configSource);
    }

    public static void addConfigListener(ManagementListener configListener) {
        INSTANCE.addConfigListener(configListener);
    }

    public static DefaultConfigManager getInstance() {
        return INSTANCE;
    }
}