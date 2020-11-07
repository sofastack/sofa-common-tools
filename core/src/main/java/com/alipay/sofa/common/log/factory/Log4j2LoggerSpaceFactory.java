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

import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.slf4j.Log4jLogger;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
public class Log4j2LoggerSpaceFactory extends AbstractLoggerSpaceFactory {

    private ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();
    private SpaceId                       spaceId;
    private Properties                    properties;
    private LoggerContext                 loggerContext;
    private URL                           confFile;

    /**
     * @param source logback,log4j2,log4j,temp,nop
     */
    public Log4j2LoggerSpaceFactory(SpaceId spaceId, Properties properties, URL confFile,
                                    String source) throws Throwable {
        super(source);
        this.spaceId = spaceId;
        this.properties = properties;
        this.confFile = confFile;
        this.loggerContext = initialize();
    }

    private LoggerContext initialize() throws Throwable {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            ThreadContext.put((String) entry.getKey(),
                properties.getProperty((String) entry.getKey()));
        }

        LoggerContext context = new LoggerContext(spaceId.getSpaceName(), null, confFile.toURI());

        Configuration config = null;
        ConfigurationFactory configurationFactory = ConfigurationFactory.getInstance();
        try {
            // log4j-core 2.3 version
            Class<?>[] parameterTypes = new Class[3];
            parameterTypes[0] = String.class;
            parameterTypes[1] = URI.class;
            parameterTypes[2] = ClassLoader.class;
            Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                "getConfiguration", parameterTypes);
            config = (Configuration) getConfigurationMethod.invoke(configurationFactory,
                spaceId.getSpaceName(), confFile.toURI(), this.getClass().getClassLoader());
        } catch (NoSuchMethodException noSuchMethodException) {
            // log4j-core 2.7+ version
            Class<?>[] parameterTypes = new Class[4];
            parameterTypes[0] = LoggerContext.class;
            parameterTypes[1] = String.class;
            parameterTypes[2] = URI.class;
            parameterTypes[3] = ClassLoader.class;
            Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                "getConfiguration", parameterTypes);
            config = (Configuration) getConfigurationMethod.invoke(configurationFactory, context,
                spaceId.getSpaceName(), confFile.toURI(), this.getClass().getClassLoader());
        }
        if (config == null) {
            throw new RuntimeException("No log4j2 configuration are found.");
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            config.getProperties().put((String) entry.getKey(), (String) entry.getValue());
        }
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            config.getProperties().put((String) entry.getKey(), (String) entry.getValue());
        }
        context.start(config);
        return context;
    }

    @Override
    public Logger setLevel(String loggerName, AdapterLevel adapterLevel) {
        final String key = Logger.ROOT_LOGGER_NAME.equals(loggerName) ? LogManager.ROOT_LOGGER_NAME
            : loggerName;
        org.apache.logging.log4j.core.Logger log4j2Logger = loggerContext.getLogger(key);
        org.apache.logging.log4j.Level log4j2Level = this.toLog4j2Level(adapterLevel);
        log4j2Logger.setLevel(log4j2Level);
        return getLogger(loggerName);
    }

    @Override
    public Logger getLogger(String name) {
        Logger logger = loggerMap.get(name);
        if (logger != null) {
            return logger;
        }
        loggerMap.putIfAbsent(name, newLogger(name, loggerContext));
        return loggerMap.get(name);
    }

    private Logger newLogger(String name, LoggerContext loggerContext) {
        final String key = Logger.ROOT_LOGGER_NAME.equals(name) ? LogManager.ROOT_LOGGER_NAME
            : name;
        return new Log4jLogger(loggerContext.getLogger(key), name);
    }

    private Level toLog4j2Level(AdapterLevel adapterLevel) {
        if (adapterLevel == null) {
            throw new IllegalStateException("AdapterLevel is NULL when adapter to log4j2.");
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
}
