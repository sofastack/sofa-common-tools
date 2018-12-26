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
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * 使用即初始，但无法在多应用中（每个应用关于某个space的中间件日志，又有不同的定制需求（比如，日志目录路径））;
 * <p>
 * 请使用 @Link(MultiAppLoggerSpaceManager.class)
 * Created by kevin.luy@alipay.com on 16/9/12.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceManager {
    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name      loggerName
     * @param spaceName 独立的loggers空间,比如"com.alipay.sofa.rpc"；
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, String spaceName) {
        return getLoggerBySpace(name, new SpaceId(spaceName),
            Collections.<String, String> emptyMap());
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name      loggerName
     * @param spaceId   独立的loggers空间
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId,
                                          Map<String, String> properties) {
        //init first
        init(spaceId, properties);
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceId);
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name             loggerName
     * @param spaceName        独立的loggers空间,比如"com.alipay.sofa.rpc"；
     * @param spaceClassloader 该空间下独立的类加载器；（建议就是 APPClassloader 即可）
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, String spaceName,
                                          ClassLoader spaceClassloader) {
        return getLoggerBySpace(name, new SpaceId(spaceName),
            Collections.<String, String> emptyMap(), spaceClassloader);
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name             loggerName
     * @param spaceId          独立的loggers空间
     * @param spaceClassloader 该空间下独立的类加载器；（建议就是 APPClassloader 即可）
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId,
                                          Map<String, String> properties,
                                          ClassLoader spaceClassloader) {
        //init first
        init(spaceId, properties);
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceId, spaceClassloader);
    }

    /***
     * 更新日志级别,屏蔽底层差异
     * @param loggerName 要更新的日志名字
     * @param spaceName 日志对应的空间名称
     * @param adapterLevel 要更新的日志级别
     * @return 更新级别后的日志,与com.alipay.sofa.common.log.MultiAppLoggerSpaceManager#getLoggerBySpace(java.lang.String, java.lang.String) 返回的同一个日志
     */
    public static Logger setLoggerLevel(String loggerName, String spaceName,
                                        AdapterLevel adapterLevel) {
        //init first
        return setLoggerLevel(loggerName, new SpaceId(spaceName), adapterLevel);
    }

    /***
     * 更新日志级别,屏蔽底层差异
     * @param loggerName 要更新的日志名字
     * @param spaceId 日志对应的空间名称
     * @param adapterLevel 要更新的日志级别
     * @return 更新级别后的日志,与com.alipay.sofa.common.log.MultiAppLoggerSpaceManager#getLoggerBySpace(java.lang.String, java.lang.String) 返回的同一个日志
     */
    public static Logger setLoggerLevel(String loggerName, SpaceId spaceId,
                                        AdapterLevel adapterLevel) {
        //init first
        init(spaceId, Collections.<String, String> emptyMap());
        return MultiAppLoggerSpaceManager.setLoggerLevel(loggerName, spaceId, adapterLevel);
    }

    /**
     * 删除 spaceName 对应的 ILoggerFactory
     * @param spaceName
     * @return
     */
    public static ILoggerFactory removeILoggerFactoryBySpaceName(String spaceName) {
        return removeILoggerFactoryBySpaceId(new SpaceId(spaceName));
    }

    /**
     * 删除 spaceId 对应的 ILoggerFactory
     * @param spaceId
     * @return
     */
    public static ILoggerFactory removeILoggerFactoryBySpaceId(SpaceId spaceId) {
        return MultiAppLoggerSpaceManager.removeILoggerFactoryBySpaceId(spaceId);
    }

    private static void init(SpaceId spaceId, Map<String, String> properties) {
        if (MultiAppLoggerSpaceManager.isSpaceInitialized(spaceId)) {
            return;
        }
        synchronized (MultiAppLoggerSpaceManager.class) {//init lock
            if (MultiAppLoggerSpaceManager.isSpaceInitialized(spaceId)) {
                return;
            }
            MultiAppLoggerSpaceManager.doInit(spaceId, properties);
        }
    }
}
