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
package com.alipay.sofa.common.log;

import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class LogSpace {
    private AbstractLoggerSpaceFactory abstractLoggerSpaceFactory;
    private Properties                 properties;
    private ClassLoader                spaceClassloader;

    public LogSpace() {
        properties = new Properties();
    }

    public LogSpace(Map<String, String> map, ClassLoader spaceClassloader) {
        this();
        this.spaceClassloader = spaceClassloader;
        putAll(map);
    }

    public ClassLoader getSpaceClassloader() {
        return spaceClassloader;
    }

    public void setSpaceClassloader(ClassLoader spaceClassloader) {
        this.spaceClassloader = spaceClassloader;
    }

    public AbstractLoggerSpaceFactory getAbstractLoggerSpaceFactory() {
        return abstractLoggerSpaceFactory;
    }

    public void setAbstractLoggerSpaceFactory(AbstractLoggerSpaceFactory abstractLoggerSpaceFactory) {
        this.abstractLoggerSpaceFactory = abstractLoggerSpaceFactory;
    }

    public Properties properties() {
        return properties;
    }

    public LogSpace putAll(Map<String, String> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public LogSpace setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
