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
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.log.factory.*;
import com.alipay.sofa.common.log.proxy.TemporaryILoggerFactoryPool;
import com.alipay.sofa.common.utils.ReportUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * Created by kevin.luy@alipay.com on 2016/12/7.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class MultiAppLoggerSpaceManager {

    private static final AbstractLoggerSpaceFactory            NOP_LOGGER_FACTORY = new AbstractLoggerSpaceFactory(
                                                                                      "nop") {
                                                                                      @Override
                                                                                      public Logger getLogger(String name) {
                                                                                          return Constants.DEFAULT_LOG;
                                                                                      }
                                                                                  };

    private static final ConcurrentHashMap<SpaceId, SpaceInfo> spacesMap          = new ConcurrentHashMap<SpaceId, SpaceInfo>();

    /**
     * 非必要初始化操作。（如果需要为某些space定义特殊的变量，则必须先初始化该方法）。
     * <p>
     * "非必要"，是因为：getLoggerBySpace默认也会执行init操作；
     * <p>
     * 使用 {@link MultiAppLoggerSpaceManager} API 建议一定要执行初始化操作即调用这个方法
     *
     * @param props 替换log xml中占位符，如果与 System.props 重复定义，优先以 System.props 配置为准；
     */
    public static void init(String spaceName, Map<String, String> props) {
        init(new SpaceId(spaceName), props);
    }

    /**
     * 非必要初始化操作。（如果需要为某些space定义特殊的变量，则必须先初始化该方法）。
     * <p>
     * "非必要"，是因为：getLoggerBySpace默认也会执行init操作；
     * <p>
     * 使用 {@link MultiAppLoggerSpaceManager} API 建议一定要执行初始化操作即调用这个方法
     */
    public static void init(SpaceId spaceId, Map<String, String> props) {
        if (isSpaceInitialized(spaceId)) {
            throw new IllegalStateException("Logger Space:" + spaceId.toString()
                                            + " is already initialized!");
        }
        synchronized (MultiAppLoggerSpaceManager.class) {
            if (isSpaceInitialized(spaceId)) {
                throw new IllegalStateException("Logger Space:" + spaceId.toString()
                                                + " is already initialized!");
            }
            doInit(spaceId, props);
        }
        ReportUtil.report("Logger Space:{" + spaceId.toString() + "} init ok.");
    }

    static void doInit(String spaceName, Map<String, String> props) {
        doInit(new SpaceId(spaceName), props);
    }

    static void doInit(SpaceId spaceId, Map<String, String> props) {
        SpaceInfo spaceInfo = new SpaceInfo();
        //以首次的为准；
        spacesMap.putIfAbsent(spaceId, spaceInfo);
        if (props != null) {
            spaceInfo.properties().putAll(props);
        }
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name      loggerName
     * @param spaceName 独立的loggers空间,比如"com.alipay.sofa.rpc"；
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, String spaceName) {
        return getLoggerBySpace(name, new SpaceId(spaceName));
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name      loggerName
     * @param spaceId   独立的loggers空间
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId) {
        return getLoggerBySpace(name, spaceId, MultiAppLoggerSpaceManager.class.getClassLoader());
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
        AbstractLoggerSpaceFactory abstractLoggerSpaceFactory = getILoggerFactoryBySpaceName(
            spaceId, MultiAppLoggerSpaceManager.class.getClassLoader());
        try {
            abstractLoggerSpaceFactory.setLevel(loggerName, adapterLevel);
        } catch (Exception e) {
            ReportUtil.reportError("SetLoggerLevel Error : ", e);
        }
        return abstractLoggerSpaceFactory.getLogger(loggerName);
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
        return getLoggerBySpace(name, new SpaceId(spaceName), spaceClassloader);
    }

    /**
     * 从 spaceName 的空间里寻找logger对象（而且这些 logger 是从该 spaceName 下的日志实现配置中解析而来)
     *
     * @param name             loggerName
     * @param spaceId          独立的loggers空间
     * @param spaceClassloader 该空间下独立的类加载器；（建议就是 APPClassloader 即可）
     * @return org.slf4j.Logger;
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId, ClassLoader spaceClassloader) {
        AbstractLoggerSpaceFactory abstractLoggerSpaceFactory = getILoggerFactoryBySpaceName(
            spaceId, spaceClassloader);
        return abstractLoggerSpaceFactory.getLogger(name);
    }

    /***
     * 根据 spaceName 在日志空间里移除指定 spaceName 的 ILoggerFactory
     *
     * @param spaceName 指定的日志空间名称
     * @return 被移除的 ILoggerFactory;不存在指定的 spaceName,则返回 null
     */
    public static ILoggerFactory removeILoggerFactoryBySpaceName(String spaceName) {
        return removeILoggerFactoryBySpaceId(new SpaceId(spaceName));
    }

    /***
     * 根据 spaceId 在日志空间里移除指定 spaceName 的 ILoggerFactory
     *
     * @param spaceId 指定的日志空间名称
     * @return 被移除的 ILoggerFactory;不存在指定的 spaceName,则返回 null
     */
    public static ILoggerFactory removeILoggerFactoryBySpaceId(SpaceId spaceId) {
        if (spaceId == null) {
            return null;
        }
        if (isSpaceILoggerFactoryExisted(spaceId)) {
            AbstractLoggerSpaceFactory iLoggeriFactory = spacesMap.get(spaceId)
                .getAbstractLoggerSpaceFactory();
            spacesMap.get(spaceId).setAbstractLoggerSpaceFactory(null);
            Logger rootLogger = iLoggeriFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.warn("Log Space Name[" + spaceId.toString()
                            + "] is Removed from Current Log Space Manager!");
            return iLoggeriFactory;
        }
        return null;
    }

    private static AbstractLoggerSpaceFactory getILoggerFactoryBySpaceName(SpaceId spaceId,
                                                                           ClassLoader spaceClassloader) {
        //该判断,线程安全不是必须的（最多产生个TemporaryILoggerFactory实例，而且[未初始化]应该基本只发生在启动场景，此时也就基本就是单一线程）,以便减少同步开销
        if (!isSpaceInitialized(spaceId)) {
            //temporary factory, and cache support
            return TemporaryILoggerFactoryPool.get(spaceId, spaceClassloader);
        }

        AbstractLoggerSpaceFactory iLoggerFactory = NOP_LOGGER_FACTORY;
        if (!isSpaceILoggerFactoryExisted(spaceId)) {
            synchronized (MultiAppLoggerSpaceManager.class) {
                if (!isSpaceILoggerFactoryExisted(spaceId)) {
                    iLoggerFactory = createILoggerFactory(spaceId, spaceClassloader);
                    spacesMap.get(spaceId).setAbstractLoggerSpaceFactory(iLoggerFactory);
                }
            }
        } else {
            iLoggerFactory = spacesMap.get(spaceId).getAbstractLoggerSpaceFactory();
        }
        return iLoggerFactory;
    }

    /**
     * 用于并发场景非严格判断space是否init用；该场景中不和初始化场景锁同步，也就是不保证并发时严格判断正确；
     *
     * @param spaceName
     * @return
     */
    public static boolean isSpaceInitialized(String spaceName) {
        return isSpaceInitialized(new SpaceId(spaceName));
    }

    /**
     * 用于并发场景非严格判断space是否init用；该场景中不和初始化场景锁同步，也就是不保证并发时严格判断正确；
     *
     * @param spaceId
     * @return
     */
    public static boolean isSpaceInitialized(SpaceId spaceId) {
        return spacesMap.get(spaceId) != null;
    }

    /**
     * @param spaceName
     * @return
     * @NotThreadSafe 该场景中不要求线程安全；
     */
    private static boolean isSpaceILoggerFactoryExisted(String spaceName) {
        return isSpaceILoggerFactoryExisted(new SpaceId(spaceName));
    }

    /**
     * @param spaceId
     * @return
     * @NotThreadSafe 该场景中不要求线程安全；
     */
    private static boolean isSpaceILoggerFactoryExisted(SpaceId spaceId) {
        return isSpaceInitialized(spaceId)
               && spacesMap.get(spaceId).getAbstractLoggerSpaceFactory() != null;
    }

    private static AbstractLoggerSpaceFactory createILoggerFactory(SpaceId spaceId,
                                                                   ClassLoader spaceClassloader) {
        if (System.getProperty(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY) != null
            && Boolean.TRUE.toString().equalsIgnoreCase(
                System.getProperty(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY))) {
            ReportUtil.reportWarn("Sofa-Middleware-Log is disabled!  -D"
                                  + SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
            return NOP_LOGGER_FACTORY;
        }
        // set log props
        LogEnvUtils.processGlobalSystemLogProperties();

        // do create
        try {
            if (LogEnvUtils.isLogbackUsable(spaceClassloader)) {
                String isLogbackDisable = System
                    .getProperty(LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
                if (isLogbackDisable != null
                    && Boolean.TRUE.toString().equalsIgnoreCase(isLogbackDisable)) {
                    ReportUtil.reportWarn("Logback-Sofa-Middleware-Log is disabled!  -D"
                                          + LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
                } else {
                    ReportUtil.report("Actual binding is of type [ " + spaceId.toString()
                                      + " Logback ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4LogbackBuilder = new LoggerSpaceFactory4LogbackBuilder(
                        spacesMap.get(spaceId));

                    return loggerSpaceFactory4LogbackBuilder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            if (LogEnvUtils.isLog4j2Usable(spaceClassloader)) {
                String isLog4j2Disable = System.getProperty(LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
                if (isLog4j2Disable != null
                    && Boolean.TRUE.toString().equalsIgnoreCase(isLog4j2Disable)) {
                    ReportUtil.reportWarn("Log4j2-Sofa-Middleware-Log is disabled!  -D"
                                          + LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
                } else {
                    ReportUtil.report("Actual binding is of type [ " + spaceId.toString()
                                      + " Log4j2 ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4Log4j2Builder = new LoggerSpaceFactory4Log4j2Builder(
                        spacesMap.get(spaceId));

                    return loggerSpaceFactory4Log4j2Builder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            if (LogEnvUtils.isLog4jUsable(spaceClassloader)) {
                String isLog4jDisable = System.getProperty(LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
                if (isLog4jDisable != null
                    && Boolean.TRUE.toString().equalsIgnoreCase(isLog4jDisable)) {
                    ReportUtil.reportWarn("Log4j-Sofa-Middleware-Log is disabled!  -D"
                                          + LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
                } else {
                    ReportUtil.report("Actual binding is of type [ " + spaceId.toString()
                                      + " Log4j ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4Log4jBuilder = new LoggerSpaceFactory4Log4jBuilder(
                        spacesMap.get(spaceId));

                    return loggerSpaceFactory4Log4jBuilder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            if (LogEnvUtils.isCommonsLoggingUsable(spaceClassloader)) {
                //此种情形:commons-logging 桥接到 log4j 实现,默认日志实现仍然是 log4j
                String isLog4jDisable = System
                    .getProperty(LOG4J_COMMONS_LOGGING_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
                if (isLog4jDisable != null
                    && Boolean.TRUE.toString().equalsIgnoreCase(isLog4jDisable)) {
                    ReportUtil
                        .reportWarn("Log4j-Sofa-Middleware-Log(But adapter commons-logging to slf4j) is disabled!  -D"
                                    + LOG4J_COMMONS_LOGGING_MIDDLEWARE_LOG_DISABLE_PROP_KEY
                                    + "=true");
                } else {
                    ReportUtil.report("Actual binding is of type [ " + spaceId.toString()
                                      + " Log4j (Adapter commons-logging to slf4j)]");

                    LoggerSpaceFactoryBuilder loggerSpaceFactory4Log4jBuilder = new LoggerSpaceFactory4CommonsLoggingBuilder(
                        spacesMap.get(spaceId));

                    return loggerSpaceFactory4Log4jBuilder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            ReportUtil.reportWarn("No log util is usable, Default app logger will be used.");
        } catch (Throwable e) {
            ReportUtil.reportError("Build ILoggerFactory error! Default app logger will be used.",
                e);
        }

        return NOP_LOGGER_FACTORY;
    }

}
