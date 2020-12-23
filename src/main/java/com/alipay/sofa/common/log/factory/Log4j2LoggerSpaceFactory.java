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

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.utils.StringUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
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
    private static final String CONSOLE = "CONSOLE";

    private ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();
    private SpaceId                       spaceId;
    private Properties                    properties;
    private LoggerContext                 loggerContext;
    private URL                           confFile;

    private final ConsoleAppender consoleAppender;
    private final Level consoleLevel;

    /**
     * @param source logback,log4j2,log4j,temp,nop
     */
    public Log4j2LoggerSpaceFactory(SpaceId spaceId, Properties properties, URL confFile,
                                    String source) throws Throwable {
        super(source);
        this.spaceId = spaceId;
        this.properties = properties;
        this.confFile = confFile;

        consoleAppender = createConsoleAppender();
        consoleLevel = getConsoleLevel();

        this.loggerContext = initialize();
        attachConsoleAppender();
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
            Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                "getConfiguration", String.class, URI.class, ClassLoader.class);
            config = (Configuration) getConfigurationMethod.invoke(configurationFactory,
                spaceId.getSpaceName(), confFile.toURI(), this.getClass().getClassLoader());
        } catch (NoSuchMethodException noSuchMethodException) {
            // log4j-core 2.7+ version
            Method getConfigurationMethod = configurationFactory.getClass().getMethod(
                "getConfiguration", LoggerContext.class, String.class, URI.class, ClassLoader.class);
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

    private void attachConsoleAppender() {
        String value = properties.getProperty(String.format(
                Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, spaceId.getSpaceName()));
        if (StringUtil.isEmpty(value)) {
            value = properties.getProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        }

        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            loggerContext.addFilter(new AbstractFilter() {
                private void process(org.apache.logging.log4j.core.Logger logger, Level level) {
                    if (CommonLoggingConfigurations.shouldAttachConsoleAppender(logger.getName())
                            && !logger.getAppenders().containsKey(CONSOLE)) {
                        logger.addAppender(consoleAppender);
                        int intLevel = Level.DEBUG.intLevel();
                        if (logger.getLevel() != null) {
                            intLevel = logger.getLevel().intLevel();
                        }
                        if (intLevel > consoleLevel.intLevel()) {
                            logger.setLevel(level);
                        }
                    }
                }

                @Override
                public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Message msg,
                                     Throwable t) {
                    process(logger, level);
                    return Result.NEUTRAL;
                }

                @Override
                public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Object msg,
                                     Throwable t) {
                    process(logger, level);
                    return Result.NEUTRAL;
                }

                @Override
                public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String msg,
                                     Object... params) {
                    process(logger, level);
                    return Result.NEUTRAL;
                }
            });
        }
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

    @Deprecated
    public void reInitialize(Map<String, String> environment) {
        // for compatibility
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

    private ConsoleAppender createConsoleAppender() {
        String logPattern = properties.getProperty(Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOG4J2_PATTERN,
                Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOG4J2_PATTERN_DEFAULT);

        PatternLayout patternLayout = PatternLayout.newBuilder().withPattern(logPattern).build();
        ConsoleAppender.Builder builder = ConsoleAppender.newBuilder();
        builder.withLayout(patternLayout).withName(CONSOLE);
        ConsoleAppender appender = builder.build();
        appender.start();
        return appender;
    }

    private Level getConsoleLevel() {
        String defaultLevel = properties.getProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "INFO");
        String level = properties.getProperty(String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, spaceId), defaultLevel);
        return Level.toLevel(level);
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
