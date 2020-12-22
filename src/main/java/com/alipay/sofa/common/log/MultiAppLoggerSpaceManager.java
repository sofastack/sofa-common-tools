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
import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4Log4j2Builder;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4Log4jBuilder;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4LogbackBuilder;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactoryBuilder;
import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.utils.ClassLoaderUtil;
import com.alipay.sofa.common.utils.ReportUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * Generic usage steps:
 * 1. Initialize properties using <code>init(String,Map)</code>
 * 2. Get logger via <code>getLoggerBySpace</code>
 *
 * Created by kevin.luy@alipay.com on 2016/12/7.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class MultiAppLoggerSpaceManager {

    private static final AbstractLoggerSpaceFactory NOP_LOGGER_FACTORY = new AbstractLoggerSpaceFactory(
                                                                           "nop") {
                                                                           @Override
                                                                           public Logger getLogger(String name) {
                                                                               return Constants.DEFAULT_LOG;
                                                                           }
                                                                       };

    private static final Map<SpaceId, LogSpace> LOG_FACTORY_MAP = new ConcurrentHashMap<>();

    /**
     * Invoke this method before using if some special configurations for the log space are needed.
     * This method isn't mandatory because MultiAppLoggerSpaceManager will initialize an LogSpace with empty config map
     *
     * @param spaceName space name
     * @param props properties used to populate log context
     */
    public static void init(String spaceName, Map<String, String> props) {
        init(new SpaceId(spaceName), props, ClassLoaderUtil.getCallerClassLoader());
    }

    /**
     * Invoke this method before using if some special configurations for the log space are needed.
     * This method isn't mandatory because MultiAppLoggerSpaceManager will initialize an LogSpace with empty config map
     *
     * @param spaceId space identity
     * @param props properties used to populate log context
     */
    public static void init(SpaceId spaceId, Map<String, String> props, ClassLoader spaceClassloader) {
        if (isSpaceInitialized(spaceId)) {
            ReportUtil.reportWarn("Logger space: \"" + spaceId.getSpaceName()
                                  + "\" is already initialized!");
            return;
        }

        synchronized (spaceId) {
            if (isSpaceInitialized(spaceId)) {
                ReportUtil.reportWarn("Logger space: \"" + spaceId.getSpaceName()
                                      + "\" is already initialized!");
                return;
            }
            doInit(spaceId, props, spaceClassloader);
        }
        ReportUtil.reportInfo("Logger Space: \"" + spaceId.toString() + "\" init ok.");
    }

    @Deprecated
    public static void init(com.alipay.sofa.common.log.SpaceId spaceId, Map<String, String> props, ClassLoader spaceClassloader) {
        init((SpaceId) spaceId, props, spaceClassloader);
    }

    static void doInit(String spaceName, Map<String, String> props, ClassLoader spaceClassloader) {
        doInit(SpaceId.withSpaceName(spaceName), props, spaceClassloader);
    }

    /**
     * This method execute the actual initializing steps.
     * Before invoking this method, make sure necessary synchronization mechanism is followed.
     *
     * @param spaceId space identification
     * @param props properties used to populate log context
     * @param spaceClassloader the class loader used to load resources
     */
    static void doInit(SpaceId spaceId, Map<String, String> props, ClassLoader spaceClassloader) {
        LogSpace logSpace = new LogSpace(props, spaceClassloader);

        AbstractLoggerSpaceFactory loggerSpaceFactory = createILoggerFactory(spaceId, logSpace,
            spaceClassloader);
        logSpace.setAbstractLoggerSpaceFactory(loggerSpaceFactory);

        LOG_FACTORY_MAP.put(spaceId, logSpace);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name      logger name
     * @param spaceName space name
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, String spaceName) {
        ClassLoader callerClassLoader = ClassLoaderUtil.getCallerClassLoader();
        return getLoggerBySpace(name, new SpaceId(spaceName), callerClassLoader);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name    logger name
     * @param spaceId space identification
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId) {
        ClassLoader callerClassLoader = ClassLoaderUtil.getCallerClassLoader();
        return getLoggerBySpace(name, spaceId, callerClassLoader);
    }

    @Deprecated
    public static Logger getLoggerBySpace(String name, com.alipay.sofa.common.log.SpaceId spaceId) {
        return getLoggerBySpace(name, (SpaceId) spaceId);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name             logger name
     * @param spaceName        space name
     * @param spaceClassloader the class loader used to load resources
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, String spaceName,
                                          ClassLoader spaceClassloader) {
        return getLoggerBySpace(name, new SpaceId(spaceName), spaceClassloader);
    }

    /**
     * Get logger from specified spaceName
     * The return logger is obtained from corresponding LoggerFactory which is configured by its own log configs
     *
     * @param name             logger name
     * @param spaceId          space identification
     * @param spaceClassloader the class loader used to load resources
     * @return logger of org.slf4j.Logger type
     */
    public static Logger getLoggerBySpace(String name, SpaceId spaceId, ClassLoader spaceClassloader) {
        AbstractLoggerSpaceFactory abstractLoggerSpaceFactory = getILoggerFactoryBySpaceName(
            spaceId, spaceClassloader);
        return abstractLoggerSpaceFactory.getLogger(name);
    }

    @Deprecated
    public static Logger getLoggerBySpace(String name, com.alipay.sofa.common.log.SpaceId spaceId, ClassLoader spaceClassloader) {
        return getLoggerBySpace(name, (SpaceId) spaceId, spaceClassloader);
    }

    private static AbstractLoggerSpaceFactory getILoggerFactoryBySpaceName(SpaceId spaceId,
                                                                           ClassLoader spaceClassloader) {
        if (!isSpaceInitialized(spaceId)) {
            // If get logger without initializing, log space will be initialized with empty config map
            init(spaceId, null, spaceClassloader);
        }

        return LOG_FACTORY_MAP.get(spaceId).getAbstractLoggerSpaceFactory();
    }

    public static Logger setLoggerLevel(String loggerName, String spaceName,
                                        AdapterLevel adapterLevel) {
        return setLoggerLevel(loggerName, new SpaceId(spaceName), adapterLevel);
    }

    public static Logger setLoggerLevel(String loggerName, SpaceId spaceId,
                                        AdapterLevel adapterLevel) {
        ClassLoader callerClassLoader = ClassLoaderUtil.getCallerClassLoader();
        AbstractLoggerSpaceFactory abstractLoggerSpaceFactory = getILoggerFactoryBySpaceName(
            spaceId, callerClassLoader);
        try {
            abstractLoggerSpaceFactory.setLevel(loggerName, adapterLevel);
        } catch (Exception e) {
            ReportUtil.reportError("SetLoggerLevel Error : ", e);
        }
        return abstractLoggerSpaceFactory.getLogger(loggerName);
    }

    @Deprecated
    public static Logger setLoggerLevel(String loggerName, com.alipay.sofa.common.log.SpaceId spaceId,
                                        AdapterLevel adapterLevel) {
        return setLoggerLevel(loggerName, (SpaceId) spaceId, adapterLevel);
    }

    public static ILoggerFactory removeILoggerFactoryBySpaceName(String spaceName) {
        return removeILoggerFactoryBySpaceId(new SpaceId(spaceName));
    }

    public static ILoggerFactory removeILoggerFactoryBySpaceId(SpaceId spaceId) {
        if (spaceId == null) {
            return null;
        }

        LogSpace logSpace = LOG_FACTORY_MAP.get(spaceId);

        if (logSpace == null) {
            return null;
        }

        AbstractLoggerSpaceFactory oldFactory = logSpace.getAbstractLoggerSpaceFactory();
        LOG_FACTORY_MAP.remove(spaceId);
        ReportUtil.reportWarn("Log Space Name[" + spaceId.getSpaceName()
                              + "] is Removed from Current Log Space Manager!");
        return oldFactory;
    }

    @Deprecated
    public static ILoggerFactory removeILoggerFactoryBySpaceId(com.alipay.sofa.common.log.SpaceId spaceId) {
        return removeILoggerFactoryBySpaceId((SpaceId) spaceId);
    }

    public static boolean isSpaceInitialized(String spaceName) {
        return isSpaceInitialized(new SpaceId(spaceName));
    }

    public static boolean isSpaceInitialized(SpaceId spaceId) {
        return LOG_FACTORY_MAP.containsKey(spaceId);
    }

    @Deprecated
    public static boolean isSpaceInitialized(com.alipay.sofa.common.log.SpaceId spaceId) {
        return LOG_FACTORY_MAP.containsKey(spaceId);
    }

    private static AbstractLoggerSpaceFactory createILoggerFactory(SpaceId spaceId,
                                                                   LogSpace logSpace,
                                                                   ClassLoader spaceClassloader) {
        if (SOFA_MIDDLEWARE_LOG_DISABLE) {
            ReportUtil.reportWarn("Sofa-Middleware-Log is disabled!  -D"
                                  + SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
            return NOP_LOGGER_FACTORY;
        }

        // Configurations programmed manually will be overridden by following operation if keys are same.
        logSpace.putAll(LogEnvUtils.processGlobalSystemLogProperties());

        try {
            if (LOGBACK_MIDDLEWARE_LOG_DISABLE) {
                ReportUtil.reportWarn("Logback-Sofa-Middleware-Log is disabled! -D"
                                      + LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
            } else {
                if (LogEnvUtils.isLogbackUsable(spaceClassloader)) {
                    ReportUtil.reportDebug("Actual binding is of type [ " + spaceId.toString()
                                           + " Logback ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4LogbackBuilder = new LoggerSpaceFactory4LogbackBuilder(
                        spaceId, logSpace);

                    return loggerSpaceFactory4LogbackBuilder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            if (LOG4J2_MIDDLEWARE_LOG_DISABLE) {
                ReportUtil.reportWarn("Log4j2-Sofa-Middleware-Log is disabled!  -D"
                                      + LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
            } else {
                if (LogEnvUtils.isLog4j2Usable(spaceClassloader)) {
                    ReportUtil.reportDebug("Actual binding is of type [ " + spaceId.toString()
                                           + " Log4j2 ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4Log4j2Builder = new LoggerSpaceFactory4Log4j2Builder(
                        spaceId, logSpace);

                    return loggerSpaceFactory4Log4j2Builder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }

            if (LOG4J_MIDDLEWARE_LOG_DISABLE) {
                ReportUtil.reportWarn("Log4j-Sofa-Middleware-Log is disabled!  -D"
                                      + LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY + "=true");
            } else {
                if (LogEnvUtils.isLog4jUsable(spaceClassloader)) {
                    ReportUtil.reportDebug("Actual binding is of type [ " + spaceId.toString()
                                           + " Log4j ]");
                    LoggerSpaceFactoryBuilder loggerSpaceFactory4Log4jBuilder = new LoggerSpaceFactory4Log4jBuilder(
                        spaceId, logSpace);

                    return loggerSpaceFactory4Log4jBuilder.build(spaceId.getSpaceName(),
                        spaceClassloader);
                }
            }
            ReportUtil.reportWarn("[" + spaceId.toString() + "] No log util is usable, Default app logger will be used.");
        } catch (Throwable e) {
            ReportUtil.reportError("[" + spaceId.toString() + "] Build ILoggerFactory error! Default app logger will be used.",
                e);
        }

        return NOP_LOGGER_FACTORY;
    }
}
