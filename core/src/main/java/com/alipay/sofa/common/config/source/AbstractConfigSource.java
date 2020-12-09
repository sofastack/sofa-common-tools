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
package com.alipay.sofa.common.config.source;

import com.alipay.sofa.common.config.ConfigKey;
import com.alipay.sofa.common.config.ConfigSource;
import com.alipay.sofa.common.config.Converter;
import com.alipay.sofa.common.config.converter.GlobalConverterHolder;
import com.alipay.sofa.common.utils.StringUtil;

/**
 * @author zhaowang
 * @version : AbstractConfigSource.java, v 0.1 2020年10月21日 2:38 下午 zhaowang Exp $
 */
public abstract class AbstractConfigSource implements ConfigSource {

    @Override
    public <T> T getConfig(ConfigKey<T> key) {
        String value = getStringConfig(key);
        return changeValueType(value, key.getType());

    }

    @Override
    public String getStringConfig(ConfigKey key) {
        String value = doGetConfig(key.getKey());
        if (StringUtil.isNotBlank(value)) {
            return value;
        }

        for (String alias : key.getAlias()) {
            value = doGetConfig(alias);
            if (StringUtil.isNotBlank(value)) {
                return value;
            }
        }
        return value;
    }

    @Override
    public String getEffectiveKey(ConfigKey configKey) {
        String key = configKey.getKey();
        if (hasKey(key)) {
            return key;
        }
        String[] alias = configKey.getAlias();
        for (String alia : alias) {
            if (hasKey(alia)) {
                return alia;
            }
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    protected <T> T changeValueType(String value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType == null) {
            return (T) value;
        }

        Converter converter = GlobalConverterHolder.getGlobalConverter();
        if (converter == null) {
            converter = GlobalConverterHolder.DEFAULT_CONVERTER;
        }
        return converter.convert(value, targetType);
    }

    public abstract String doGetConfig(String key);

    public abstract boolean hasKey(String key);
}