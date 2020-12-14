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

import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.utils.ClassLoaderUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Single Application logger space manager.
 * If your application runs in multi-app environment/container (e.g., Tomcat, OSGi), use MultiAppLoggerSpaceManager instead.
 *
 * Created by kevin.luy@alipay.com on 16/9/12.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceManager {
    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name      logger name to get
     * @param spaceName space name
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, String spaceName) {
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceName);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name      logger name to get
     * @param spaceName space name
     * @param spaceClassloader the class loader used to load resources
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, String spaceName,
                                          ClassLoader spaceClassloader) {
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceName, spaceClassloader);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name      logger name to get
     * @param spaceId   space identification
     * @param properties properties associated with the log space
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId,
                                          Map<String, String> properties) {
        ClassLoader callerClassLoader = ClassLoaderUtil.getCallerClassLoader();
        return getLoggerBySpace(name, spaceId, properties, callerClassLoader);
    }

    @Deprecated
    public static Logger getLoggerBySpace(String name, com.alipay.sofa.common.log.SpaceId spaceId,
                                          Map<String, String> properties) {
        return getLoggerBySpace(name, (SpaceId) spaceId, properties);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * MultiAppLoggerSpaceManager requires manual initialization if special configurations is need
     * This method takes the responsibility for initializing
     *
     * @param name      logger name to get
     * @param spaceId space identification
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId,
                                          Map<String, String> properties,
                                          ClassLoader spaceClassloader) {
        if (!MultiAppLoggerSpaceManager.isSpaceInitialized(spaceId)) {
            MultiAppLoggerSpaceManager.init(spaceId, properties, spaceClassloader);
        }
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceId, spaceClassloader);
    }

    @Deprecated
    public static Logger getLoggerBySpace(String name, com.alipay.sofa.common.log.SpaceId spaceId,
                                          Map<String, String> properties,
                                          ClassLoader spaceClassloader) {
        return getLoggerBySpace(name, (SpaceId) spaceId, properties, spaceClassloader);
    }

    public static Logger setLoggerLevel(String loggerName, String spaceName,
                                        AdapterLevel adapterLevel) {
        //init first
        return setLoggerLevel(loggerName, new SpaceId(spaceName), adapterLevel);
    }

    public static Logger setLoggerLevel(String loggerName, SpaceId spaceId,
                                        AdapterLevel adapterLevel) {
        return MultiAppLoggerSpaceManager.setLoggerLevel(loggerName, spaceId, adapterLevel);
    }

    @Deprecated
    public static Logger setLoggerLevel(String loggerName,
                                        com.alipay.sofa.common.log.SpaceId spaceId,
                                        AdapterLevel adapterLevel) {
        return MultiAppLoggerSpaceManager.setLoggerLevel(loggerName, spaceId, adapterLevel);
    }

    public static ILoggerFactory removeILoggerFactoryBySpaceName(String spaceName) {
        return removeILoggerFactoryBySpaceId(new SpaceId(spaceName));
    }

    /**
     * Delete the ILoggerFactory specified by spaceId
     * @param spaceId space ID
     * @return deleted ILoggerFactory
     */
    public static ILoggerFactory removeILoggerFactoryBySpaceId(SpaceId spaceId) {
        return MultiAppLoggerSpaceManager.removeILoggerFactoryBySpaceId(spaceId);
    }

    @Deprecated
    public static ILoggerFactory removeILoggerFactoryBySpaceId(com.alipay.sofa.common.log.SpaceId spaceId) {
        return MultiAppLoggerSpaceManager.removeILoggerFactoryBySpaceId(spaceId);
    }
}
