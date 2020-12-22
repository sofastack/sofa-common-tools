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

import com.alipay.sofa.common.utils.Ordered;

import java.util.List;

/**
 * @author zhaowang
 * @version : ConfigListener.java, v 0.1 2020年12月01日 11:43 上午 zhaowang Exp $
 */
public interface ManagementListener extends Ordered {

    /**
     * Do some thing before loading config
     * @param configKey
     * @param configSources
     */
    void beforeConfigLoading(ConfigKey configKey, List<ConfigSource> configSources);

    /**
     * Do something when loaded config
     * @param key key of config
     * @param configSource config source which config loads from
     * @param configSourceList all config sources
     */
    void afterConfigLoaded(ConfigKey key, ConfigSource configSource,
                           List<ConfigSource> configSourceList);

    /**
     * Do something when loaded defaultValue
     * @param key
     * @param defaultValue
     */
    void onLoadDefaultValue(ConfigKey key, Object defaultValue);
}