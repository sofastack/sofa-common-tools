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
package com.alipay.sofa.common.config.listener;

import com.alipay.sofa.common.config.ConfigKey;
import com.alipay.sofa.common.config.ConfigSource;
import com.alipay.sofa.common.config.ManagementListener;

import java.util.List;

/**
 * AbstractConfigListener provides empty implements of ConfigListener.
 * @author zhaowang
 * @version : AbstractConfigListener.java, v 0.1 2020年12月07日 9:21 下午 zhaowang Exp $
 */
public abstract class AbstractConfigListener implements ManagementListener {
    @Override
    public void beforeConfigLoading(ConfigKey configKey, List<ConfigSource> configSources) {

    }

    @Override
    public void afterConfigLoaded(ConfigKey key, ConfigSource configSource,
                                  List<ConfigSource> configSourceList) {
    }

    @Override
    public void onLoadDefaultValue(ConfigKey key, Object defaultValue) {
    }
}