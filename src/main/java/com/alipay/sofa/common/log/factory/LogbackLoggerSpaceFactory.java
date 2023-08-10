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
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.spi.MDCAdapter;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * @author qilong.zql
 * @since  1.0.15
 */
public class LogbackLoggerSpaceFactory extends AbstractLoggerSpaceFactory {

    private static final Method            logContextSetMDCAdapterMethod;

    private final SpaceId                  spaceId;
    private final LoggerContext            loggerContext;
    private final Properties               properties;
    private ConsoleAppender<ILoggingEvent> consoleAppender;

    static {
        // Resolve logContext#setMDCAdapter method if logback version >= 1.4.8
        Method logContextSetMDCAdapter;
        try {
            logContextSetMDCAdapter = LoggerContext.class.getDeclaredMethod("setMDCAdapter",
                MDCAdapter.class);
        } catch (Throwable t) {
            logContextSetMDCAdapter = null;
        }

        logContextSetMDCAdapterMethod = logContextSetMDCAdapter;
    }

    public LogbackLoggerSpaceFactory(SpaceId spaceId, LoggerContext loggerContext,
                                     Properties properties, URL confFile, String source) {
        super(source);
        this.spaceId = spaceId;
        this.loggerContext = loggerContext;
        this.properties = properties;

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
        }
        try {
            new ContextInitializer(loggerContext);
            configureByResource(confFile, loggerContext);
        } catch (JoranException e) {
            throw new IllegalStateException("Logback loggerSpaceFactory build error", e);
        }

        // invoke loggerContext.setMDCAdapter(MDC.getMDCAdapter());
        if (logContextSetMDCAdapterMethod != null) {
            try {
                logContextSetMDCAdapterMethod.invoke(loggerContext, MDC.getMDCAdapter());
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to invoke setMDCAdapter method", t);
            }
        }

        String value = properties.getProperty(String.format(
            Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, spaceId.getSpaceName()));
        if (StringUtil.isEmpty(value)) {
            value = properties.getProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            consoleAppender = createConsoleAppender();
            loggerContext.addTurboFilter(new TurboFilter() {
                @Override
                public FilterReply decide(Marker marker, ch.qos.logback.classic.Logger logger,
                                          Level level, String format, Object[] params, Throwable t) {
                    if (CommonLoggingConfigurations.shouldAttachConsoleAppender(logger.getName())
                        && !logger.isAttached(consoleAppender)) {
                        logger.addAppender(consoleAppender);
                    }
                    return FilterReply.NEUTRAL;
                }
            });
        }
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender() {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        String logPattern = properties.getProperty(
                Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN,
                Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN_DEFAULT);
        // create appender filter
        Level consoleLevel = getConsoleLevel(spaceId.getSpaceName());
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(consoleLevel.toString());

        String pattern;
        try {
            pattern = OptionHelper.substVars(logPattern, loggerContext);
        } catch (ScanException e) {
            throw new IllegalArgumentException("Failed to subst vars pattern: " + logPattern, e);
        }
        encoder.setPattern(pattern);
        encoder.setContext(loggerContext);
        encoder.start();
        appender.setEncoder(encoder);
        appender.setName(CONSOLE);
        filter.start();
        appender.addFilter(filter);
        appender.start();
        return appender;
    }

    public SpaceId getSpaceId() {
        return spaceId;
    }

    public Properties getProperties() {
        return properties;
    }

    private Level getConsoleLevel(String spaceId) {
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

    private Level toLogbackLevel(AdapterLevel adapterLevel) {
        if (adapterLevel == null) {
            throw new IllegalStateException("AdapterLevel is NULL when adapter to logback.");
        }
        return switch (adapterLevel) {
            case TRACE -> Level.TRACE;
            case DEBUG -> Level.DEBUG;
            case INFO -> Level.INFO;
            case WARN -> Level.WARN;
            case ERROR -> Level.ERROR;
            default -> throw new IllegalStateException(adapterLevel
                    + " is unknown when adapter to logback.");
        };
    }

    // logback 1.4.8 remove this method: https://github.com/qos-ch/logback/commit/4b06e062488e4cb87f22be6ae96e4d7d6350ed6b
    public static void configureByResource(URL url, LoggerContext loggerContext)
                                                                                throws JoranException {
        if (url == null) {
            throw new IllegalArgumentException("URL argument cannot be null");
        }
        final String urlString = url.toString();
        if (urlString.endsWith("xml")) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(url);
        } else {
            throw new LogbackException("Unexpected filename extension of file [" + url
                                       + "]. Should be .xml");
        }
    }
}
