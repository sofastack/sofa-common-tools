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

import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ClassUtils;

/**
 * @author zhaowang
 * @version : AbstractConfigSource.java, v 0.1 2020年10月21日 2:38 下午 zhaowang Exp $
 */
public abstract class AbstractConfigSource implements ConfigSource {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigSource.class);

    //todo 暂时没用
    private ConversionService  conversionService;

    @Override
    public <T> T getConfig(SofaConfig<T> key) {
        String value = getStringConfig(key);
        return (T) changeValueType(value, key.getDefaultValue().getClass());

    }

    protected <T> String getStringConfig(SofaConfig<T> key) {
        String value = doGetConfig(key.getKey());
        if (StringUtil.isNotBlank(value)) {
            logConfigGet(value, value);
            return value;
        }

        for (String alias : key.getAlias()) {
            value = doGetConfig(alias);
            if (StringUtil.isNotBlank(value)) {
                logConfigGetWarn(alias, value);
                return value;
            }
        }
        return value;
    }

    protected void logConfigGetWarn(String key, String value) {
        LOGGER.warn("Get config from {} ,key = {} , value = {}", getName(), key, value);
    }

    protected void logConfigGet(String key, String value) {
        LOGGER.info("Get config from {} ,key = {} , value = {}", getName(), key, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T changeValueType(String value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType == null) {
            return (T) value;
        }
        ConversionService conversionServiceToUse = this.conversionService;
        if (conversionServiceToUse == null) {
            // Avoid initialization of shared DefaultConversionService if
            // no standard type conversion is needed in the first place...
            if (ClassUtils.isAssignableValue(targetType, value)) {
                return (T) value;
            }
            conversionServiceToUse = DefaultConversionService.getSharedInstance();
        }
        return conversionServiceToUse.convert(value, targetType);
    }

    public abstract String doGetConfig(String key);
}