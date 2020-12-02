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

import com.alipay.sofa.common.config.listener.ConfigListener;
import com.alipay.sofa.common.config.source.ConfigSource;
import com.alipay.sofa.common.utils.OrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhaowang
 * @version : SofaCommonConfig.java, v 0.1 2020年10月20日 8:30 下午 zhaowang Exp $
 */
public class InnerSofaCommonConfig implements CommonConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(InnerSofaCommonConfig.class);

    List<ConfigSource> configSources = new LinkedList<>();
    List<ConfigListener> configListeners = new LinkedList<>();

    @Override
    public <T> T getOrDefault(SofaConfig<T> key) {
        return getConfig(key,key.getDefaultValue());
    }

    @Override
    public <T> T getOrCustomDefault(SofaConfig<T> key, T customDefault) {
        return getConfig(key,customDefault);
    }

    public <T> T getConfig(SofaConfig<T> key, T defaultValue) {
        T result = null;
        for (ConfigSource configSource : configSources) {
            result = configSource.getConfig(key);
            if(result != null){
                for (ConfigListener configListener : configListeners) {
                    configListener.onLoadedConfig(key,configSource,configSources);
                }
                return result;
            }
        }

        // todo 迁移到 listener 中
        if(!key.getDefaultValue().equals(defaultValue)){
            LOGGER.warn("Config {}'s defaultValue {} does not equals to actually defaultValue {}",key.toString(),key.getDefaultValue(),defaultValue);
        }
        return defaultValue;
    }


    @Override
    public void addConfigSource(ConfigSource configSource) {
        configSources.add(configSource);
        OrderComparator.sort(configSources);
    }

    @Override
    public void addConfigListener(ConfigListener configListener) {
        configListeners.add(configListener);
        OrderComparator.sort(configListeners);
    }

    //visible for test
    List<ConfigSource> getConfigSources() {
        return configSources;
    }

    //visible for test
    List<ConfigListener> getConfigListeners() {
        return configListeners;
    }
}