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
package com.alipay.sofa.common.log.factory;

import com.alipay.sofa.common.log.SpaceInfo;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.slf4j.Log4jLogger;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kevin.luy@alipay.com on 16/9/14.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceFactory4Log4j2Builder extends AbstractLoggerSpaceFactoryBuilder {

    public LoggerSpaceFactory4Log4j2Builder(SpaceInfo spaceInfo) {
        super(spaceInfo);
    }

    @Override
    protected String getLoggingToolName() {
        return "log4j2";
    }

    @Override
    public AbstractLoggerSpaceFactory doBuild(String spaceName, ClassLoader spaceClassloader,
                                              URL url) {

        try {
            final LoggerContext context = new LoggerContext(spaceName, null, url.toURI());
            Configuration config = null;
            ConfigurationFactory configurationFactory = ConfigurationFactory.getInstance();
            try {
                //log4j-core 2.3 version
                Class[] parameterTypes = new Class[3];
                parameterTypes[0] = String.class;
                parameterTypes[1] = URI.class;
                parameterTypes[2] = ClassLoader.class;
                Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                    "getConfiguration", parameterTypes);
                config = (Configuration) getConfigurationMethod.invoke(configurationFactory,
                    spaceName, url.toURI(), spaceClassloader);
            } catch (NoSuchMethodException noSuchMethodException) {
                //log4j-core 2.8 version
                Class[] parameterTypes = new Class[4];
                parameterTypes[0] = LoggerContext.class;
                parameterTypes[1] = String.class;
                parameterTypes[2] = URI.class;
                parameterTypes[3] = ClassLoader.class;
                Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                    "getConfiguration", parameterTypes);
                config = (Configuration) getConfigurationMethod.invoke(configurationFactory,
                    context, spaceName, url.toURI(), spaceClassloader);
            }
            if (config == null) {
                throw new RuntimeException("No log4j2 configuration are found.");
            }
            for (Map.Entry entry : getProperties().entrySet()) {
                //from Map<String,String>
                config.getProperties().put((String) entry.getKey(), (String) entry.getValue());
            }

            for (Map.Entry entry : System.getProperties().entrySet()) {
                //from Map<String,String>
                config.getProperties().put((String) entry.getKey(), (String) entry.getValue());
            }

            context.start(config);

            return new AbstractLoggerSpaceFactory(getLoggingToolName()) {

                private ConcurrentMap<String, org.apache.logging.slf4j.Log4jLogger> loggerMap = new ConcurrentHashMap<String, org.apache.logging.slf4j.Log4jLogger>();

                @Override
                public Logger setLevel(String loggerName, AdapterLevel adapterLevel)
                                                                                    throws Exception {
                    org.apache.logging.slf4j.Log4jLogger log4jLoggerAdapter = (org.apache.logging.slf4j.Log4jLogger) this
                        .getLogger(loggerName);
                    final String key = Logger.ROOT_LOGGER_NAME.equals(loggerName) ? LogManager.ROOT_LOGGER_NAME
                        : loggerName;
                    org.apache.logging.log4j.core.Logger log4j2Logger = context.getLogger(key);
                    //level
                    org.apache.logging.log4j.Level log4j2Level = this.toLog4j2Level(adapterLevel);
                    log4j2Logger.setLevel(log4j2Level);
                    return log4jLoggerAdapter;
                }

                @Override
                public org.slf4j.Logger getLogger(String name) {
                    final String key = Logger.ROOT_LOGGER_NAME.equals(name) ? LogManager.ROOT_LOGGER_NAME
                        : name;
                    org.apache.logging.log4j.core.Logger log4jLogger = context.getLogger(key);
                    Log4jLogger oldInst = this.loggerMap.get(key);
                    if (oldInst != null) {
                        return oldInst;
                    }
                    Log4jLogger newInst = new Log4jLogger(log4jLogger, key);
                    oldInst = this.loggerMap.putIfAbsent(key, newInst);
                    return oldInst == null ? newInst : oldInst;
                }

                private org.apache.logging.log4j.Level toLog4j2Level(AdapterLevel adapterLevel) {
                    if (adapterLevel == null) {
                        throw new IllegalStateException(
                            "AdapterLevel is NULL when adapter to log4j2.");
                    }
                    switch (adapterLevel) {
                        case TRACE:
                            return Level.TRACE;
                        case DEBUG:
                            return Level.DEBUG;
                        case INFO:
                            return Level.INFO;
                        case WARN:
                            return Level.WARN;
                        case ERROR:
                            return Level.ERROR;
                        default:
                            throw new IllegalStateException(adapterLevel
                                                            + " is unknown when adapter to log4j2.");
                    }
                }
            };
        } catch (Throwable e) {
            throw new IllegalStateException("Log4j2 loggerSpaceFactory build error!", e);
        }
    }

}
