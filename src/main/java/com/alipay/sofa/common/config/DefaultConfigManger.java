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

import com.alipay.sofa.common.utils.OrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhaowang
 * @version : SofaCommonConfig.java, v 0.1 2020年10月20日 8:30 下午 zhaowang Exp $
 */
public class DefaultConfigManger implements ConfigManager{

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigManger.class);

    private final List<ConfigSource> configSources = new LinkedList<>();
    private final List<ManagementListener> configListeners = new LinkedList<>();

    @Override
    public <T> T getOrDefault(ConfigKey<T> key) {
        return getConfig(key,key.getDefaultValue());
    }

    @Override
    public <T> T getOrCustomDefault(ConfigKey<T> key, T customDefault) {
        return getConfig(key,customDefault);
    }

    public <T> T getConfig(ConfigKey<T> key, T defaultValue) {
        T result = null;
        beforeConfigLoading(key);
        for (ConfigSource configSource : configSources) {
            result = configSource.getConfig(key);
            if(result != null){
                onConfigLoaded(key,configSource);
                return result;
            }
        }


        onLoadDefaultValue(key,defaultValue);
        return defaultValue;
    }

    private <T> void onLoadDefaultValue(ConfigKey<T> key, Object defaultValue) {
        for (ManagementListener configListener : configListeners) {
            configListener.onLoadDefaultValue(key,defaultValue);
        }
    }


    private <T> void beforeConfigLoading(ConfigKey<T> key) {
        for (ManagementListener configListener : configListeners) {
            configListener.beforeConfigLoading(key,configSources);
        }
    }

    private <T> void onConfigLoaded(ConfigKey<T> key, ConfigSource configSource) {
        for (ManagementListener configListener : configListeners) {
            configListener.afterConfigLoaded(key,configSource,configSources);
        }
    }


    @Override
    public void addConfigSource(ConfigSource configSource) {
        configSources.add(configSource);
        OrderComparator.sort(configSources);
    }

    @Override
    public void addConfigListener(ManagementListener configListener) {
        configListeners.add(configListener);
        OrderComparator.sort(configListeners);
    }

    //visible for test
    List<ConfigSource> getConfigSources() {
        return configSources;
    }

    //visible for test
    List<ManagementListener> getConfigListeners() {
        return configListeners;
    }
}