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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhaowang
 * @version : SofaCommonConfig.java, v 0.1 2020年10月20日 8:30 下午 zhaowang Exp $
 */
public class SofaCommonConfig implements CommonConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(SofaCommonConfig.class);


    private static final SofaCommonConfig INSTANCE;

    static{
        INSTANCE = new SofaCommonConfig();
        INSTANCE.setConfigSource(new SystemPropertyConfigSource());
    }

    List<ConfigSource> configSources = new LinkedList<>();

    public static SofaCommonConfig getInstance(){
        return INSTANCE;
    }


    @Override
    public <T> T getConfig(SofaConfig<T> key) {
        return getConfig(key,null);
    }

    @Override
    public <T> T getConfig(SofaConfig<T> key, T defaultValue) {
        T result = null;
        for (ConfigSource configSource : configSources) {
            result = configSource.getConfig(key);
            if(result != null){
                return result;
            }
        }

        if(defaultValue!=null){
            if(!key.getDefaultValue().equals(defaultValue)){
                LOGGER.warn("Config {}'s defaultValue {} does not equals to actually defaultValue {}",key.toString(),key.getDefaultValue(),defaultValue);
            }
            return defaultValue;
        }else{
           return key.getDefaultValue();
        }
    }



    @Override
    public void setConfigSource(ConfigSource configSource) {
        configSources.add(configSource);
        OrderComparator.sort(configSources);
    }
}