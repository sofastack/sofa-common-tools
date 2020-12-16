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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.OptionHelper;
import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.net.URL;
import java.util.*;

/**
 * @author qilong.zql
 * @since  1.0.15
 */
public class LogbackLoggerSpaceFactory extends AbstractLoggerSpaceFactory {

    private SpaceId                              spaceId;
    private LoggerContext                        loggerContext;
    private Properties                           properties;

    // Console appender on this logger context
    private final ConsoleAppender<ILoggingEvent> consoleAppender;
    private final Level                          consoleLevel;

    public LogbackLoggerSpaceFactory(SpaceId spaceId, LoggerContext loggerContext,
                                     Properties properties, URL confFile, String source) {
        super(source);
        this.spaceId = spaceId;
        this.loggerContext = loggerContext;
        this.properties = properties;
        consoleAppender = createConsoleAppender(loggerContext, properties);
        consoleLevel = getConsoleLevel(spaceId.getSpaceName(), properties);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
        }
        try {
            new ContextInitializer(loggerContext).configureByResource(confFile);
        } catch (JoranException e) {
            throw new IllegalStateException("Logback loggerSpaceFactory build error", e);
        }

        String value = properties.getProperty(String.format(
            Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, spaceId.getSpaceName()));
        if (StringUtil.isEmpty(value)) {
            value = properties.getProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            loggerContext.addTurboFilter(new TurboFilter() {
                @Override
                public FilterReply decide(Marker marker, ch.qos.logback.classic.Logger logger,
                                          Level level, String format, Object[] params, Throwable t) {
                    if (CommonLoggingConfigurations.shouldAttachConsoleAppender(logger.getName())
                        && !logger.isAttached(consoleAppender)) {
                        logger.addAppender(consoleAppender);
                        // effective level won't be null
                        if (logger.getEffectiveLevel().isGreaterOrEqual(consoleLevel)) {
                            logger.setLevel(consoleLevel);
                        }
                    }
                    return FilterReply.NEUTRAL;
                }
            });
        }
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(LoggerContext loggerContext, Properties properties) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        String logPattern = properties.getProperty(
                Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN,
                Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN_DEFAULT);
        encoder.setPattern(OptionHelper.substVars(logPattern, loggerContext));
        encoder.setContext(loggerContext);
        encoder.start();
        appender.setEncoder(encoder);
        appender.setName("CONSOLE");
        appender.start();
        return appender;
    }

    public SpaceId getSpaceId() {
        return spaceId;
    }

    public Properties getProperties() {
        return properties;
    }

    private Level getConsoleLevel(String spaceId, Properties properties) {
        String defaultLevel = properties.getProperty(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "INFO");
        String level = properties.getProperty(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, spaceId),
            defaultLevel);
        return Level.toLevel(level, Level.INFO);
    }

    @Override
    public Logger getLogger(String name) {
        return loggerContext.getLogger(name);
    }

    @Override
    public Logger setLevel(String loggerName, AdapterLevel adapterLevel) {
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) this
            .getLogger(loggerName);
        Level logbackLevel = this.toLogbackLevel(adapterLevel);
        logbackLogger.setLevel(logbackLevel);
        return logbackLogger;
    }

    @Deprecated
    public void reInitialize(Map<String, String> environment) {
        // for compatibility
    }

    private Level toLogbackLevel(AdapterLevel adapterLevel) {
        if (adapterLevel == null) {
            throw new IllegalStateException("AdapterLevel is NULL when adapter to logback.");
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
                                                + " is unknown when adapter to logback.");
        }
    }
}
