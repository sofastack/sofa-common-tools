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
import com.alipay.sofa.common.utils.Ordered;
import org.slf4j.Logger;

import java.util.List;

import static com.alipay.sofa.common.config.log.ConfigLoggerFactory.CONFIG_COMMON_DIGEST_LOGGER;

/**
 * @author zhaowang
 * @version : ConfigLogListener.java, v 0.1 2020年12月01日 2:06 下午 zhaowang Exp $
 */
public class LogConfigListener extends AbstractConfigListener {

    private static final Logger LOGGER = CONFIG_COMMON_DIGEST_LOGGER;

    @Override
    public void afterConfigLoaded(ConfigKey configKey, ConfigSource configSource,
                                  List<ConfigSource> configSourceList) {
        String keyStr = configKey.getKey();
        String configName = configSource.getName();
        String value = configSource.getStringConfig(configKey);
        String effectKey = configSource.getEffectiveKey(configKey);
        LOGGER.info("Load {} from {} ,effect key is {}, value is \"{}\"", keyStr, configName,
            effectKey, value);
    }

    @Override
    public void onLoadDefaultValue(ConfigKey key, Object defaultValue) {
        if (key.getDefaultValue().equals(defaultValue)) {
            LOGGER.info("Load {} according defaultValue ,default value is \"{}\"", key.getKey(),
                defaultValue);
        } else {
            LOGGER.warn("Config {}'s defaultValue {} does not equals to actually defaultValue {}",
                key.toString(), key.getDefaultValue(), defaultValue);

        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}