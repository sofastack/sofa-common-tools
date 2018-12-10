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
package com.alipay.sofa.common.boot.initializer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.OptionHelper;
import com.alipay.sofa.common.boot.filter.DefaultLogbackFilterGenerator;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.spi.LogbackReInitializer;
import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Marker;
import org.springframework.util.ClassUtils;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
public class DefaultLogbackReInitializer implements LogbackReInitializer {

    private static final String BRIDGE_HANDLER = "org.slf4j.bridge.SLF4JBridgeHandler";

    @Override
    public void reInitialize(final SpaceId spaceId, LoggerContext loggerContext,
                             final Properties properties, URL confFile) {
        if (isAlreadyReInitialized(loggerContext)) {
            return;
        }
        stopAndReset(loggerContext);
        loggerContext.getTurboFilterList().remove(DefaultLogbackFilterGenerator.FILTER);
        markAsReInitialized(loggerContext);
        initProperties(loggerContext, properties);
        if (isConsoleAppenderOpen(spaceId.getSpaceName(), properties)) {
            final ConsoleAppender appender = consoleAppender(loggerContext, properties);
            loggerContext.addTurboFilter(new TurboFilter() {
                @Override
                public FilterReply decide(Marker marker, Logger logger, Level level, String format,
                                          Object[] params, Throwable t) {
                    if (!logger.isAttached(appender)) {
                        logger.setLevel(getConsoleLevel(spaceId.getSpaceName(), properties));
                        logger.addAppender(appender);
                    }
                    return FilterReply.NEUTRAL;
                }
            });
        } else {
            try {
                new ContextInitializer(loggerContext).configureByResource(confFile);
            } catch (JoranException e) {
                throw new IllegalStateException("Logback loggerSpaceFactory re-build error", e);
            }
        }

    }

    private Level getConsoleLevel(String spaceId, Properties properties) {
        PropertiesGetter propertiesGetter = new PropertiesGetter(properties);
        String level = propertiesGetter.getProperty(SOFA_MIDDLEWARE_LOG_CONSOLE_LEVEL);
        if (StringUtil.isBlank(level)) {
            level = propertiesGetter.getProperty(
                String.format(SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, spaceId), "INFO");
        }
        return Level.toLevel(level, Level.INFO);
    }

    private ConsoleAppender consoleAppender(LoggerContext loggerContext, Properties properties) {
        PropertiesGetter propertiesGetter = new PropertiesGetter(properties);
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        String logPattern = propertiesGetter.getProperty(SOFA_MIDDLEWARE_LOG_CONSOLE_PATTERN,
            SOFA_MIDDLEWARE_LOG_CONSOLE_PATTERN_DEFAULT);
        encoder.setPattern(OptionHelper.substVars(logPattern, loggerContext));
        encoder.setContext(loggerContext);
        encoder.start();
        appender.setEncoder(encoder);
        appender.setName("CONSOLE");
        appender.start();
        return appender;
    }

    private boolean isConsoleAppenderOpen(String spaceId, Properties properties) {
        PropertiesGetter propertiesGetter = new PropertiesGetter(properties);
        return "true".equalsIgnoreCase(propertiesGetter
            .getProperty(SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH))
               || "true".equalsIgnoreCase(propertiesGetter.getProperty(String.format(
                   SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, spaceId)));
    }

    private void markAsReInitialized(LoggerContext loggerContext) {
        loggerContext.putObject(DefaultLogbackReInitializer.class.getCanonicalName(), new Object());
    }

    private boolean isAlreadyReInitialized(LoggerContext loggerContext) {
        return loggerContext.getObject(DefaultLogbackReInitializer.class.getCanonicalName()) != null;
    }

    private void stopAndReset(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
        if (isBridgeHandlerAvailable()) {
            addLevelChangePropagator(loggerContext);
        }
    }

    protected final boolean isBridgeHandlerAvailable() {
        return ClassUtils.isPresent(BRIDGE_HANDLER, this.getClass().getClassLoader());
    }

    private void addLevelChangePropagator(LoggerContext loggerContext) {
        LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
        levelChangePropagator.setResetJUL(true);
        levelChangePropagator.setContext(loggerContext);
        loggerContext.addListener(levelChangePropagator);
    }

    private void initProperties(LoggerContext loggerContext, Properties properties) {
        for (Map.Entry entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String originValue = (String) entry.getValue();
            String value = System.getProperty(key, originValue);
            loggerContext.putProperty(key, value);
        }
    }

    class PropertiesGetter {
        Properties properties;

        PropertiesGetter(Properties properties) {
            this.properties = properties;
        }

        String getProperty(String key) {
            if (StringUtil.isBlank(System.getProperty(key))) {
                return (String) properties.get(key);
            }
            return System.getProperty(key);
        }

        String getProperty(String key, String defaultValue) {
            String value = getProperty(key);
            return StringUtil.isBlank(value) ? defaultValue : value;
        }
    }
}