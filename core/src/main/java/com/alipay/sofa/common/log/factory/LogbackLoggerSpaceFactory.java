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

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.log.spi.ReInitializeChecker;
import com.alipay.sofa.common.log.spi.LogbackFilterGenerator;
import com.alipay.sofa.common.log.spi.LogbackReInitializer;
import com.alipay.sofa.common.utils.AssertUtil;
import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Logger;

import java.net.URL;
import java.util.*;

import static com.alipay.sofa.common.log.Constants.IS_DEFAULT_LOG_PATH;
import static com.alipay.sofa.common.log.Constants.LOG_LEVEL_PREFIX;
import static com.alipay.sofa.common.log.Constants.LOG_PATH;

/**
 * @author qilong.zql
 * @since  1.0.15
 */
public class LogbackLoggerSpaceFactory extends AbstractLoggerSpaceFactory {

    private SpaceId       spaceId;
    private LoggerContext loggerContext;
    private Properties    properties;
    private URL           confFile;

    /**LogbackLoggerSpaceFactor
     * @param loggerContext
     * @param properties
     * @param confFile
     * @param source
     */
    public LogbackLoggerSpaceFactory(SpaceId spaceId, LoggerContext loggerContext,
                                     Properties properties, URL confFile, String source) {
        super(source);
        this.spaceId = spaceId;
        this.loggerContext = loggerContext;
        this.properties = properties;
        this.confFile = confFile;
        boolean willReinitialize = false;
        Iterator<ReInitializeChecker> checkers = ServiceLoader.load(ReInitializeChecker.class,
            this.getClass().getClassLoader()).iterator();
        while (checkers.hasNext()) {
            willReinitialize = !checkers.next().isReInitialize();
        }
        Iterator<LogbackFilterGenerator> matchers = ServiceLoader.load(
            LogbackFilterGenerator.class, this.getClass().getClassLoader()).iterator();
        while (matchers.hasNext() && willReinitialize) {
            LogbackFilterGenerator matcher = matchers.next();
            this.loggerContext.getTurboFilterList().addAll(
                Arrays.asList(matcher.generatorFilters()));
        }
        initialize(willReinitialize);
    }

    @Override
    public Logger getLogger(String name) {
        return loggerContext.getLogger(name);
    }

    @Override
    public Logger setLevel(String loggerName, AdapterLevel adapterLevel) throws Exception {
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

    private void initialize(boolean willReInitialize) {
        AssertUtil.notNull(loggerContext);
        AssertUtil.notNull(properties);
        AssertUtil.notNull(confFile);

        for (Map.Entry entry : properties.entrySet()) {
            loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
        }

        if (confFile != null && !willReInitialize) {
            try {
                new ContextInitializer(loggerContext).configureByResource(confFile);
            } catch (JoranException e) {
                throw new IllegalStateException("Logback loggerSpaceFactory build error", e);
            }
        } else {
            BasicConfigurator basicConfigurator = new BasicConfigurator();
            basicConfigurator.setContext(loggerContext);
            basicConfigurator.configure(loggerContext);
        }
    }

    public void reInitialize(Map<String, String> environment) {
        properties.putAll(environment);
        String spaceLoggingPath = environment.get(Constants.LOG_PATH_PREFIX
                                                  + spaceId.getSpaceName());
        if (!StringUtil.isBlank(spaceLoggingPath)) {
            properties.setProperty(Constants.LOG_PATH_PREFIX + spaceId.getSpaceName(),
                spaceLoggingPath);
        } else if (Boolean.TRUE.toString().equals(properties.getProperty(IS_DEFAULT_LOG_PATH))) {
            properties.setProperty(Constants.LOG_PATH_PREFIX + spaceId.getSpaceName(),
                properties.getProperty(LOG_PATH));
        }

        String loggingLevelKey = LOG_LEVEL_PREFIX + spaceId.getSpaceName();
        if (Boolean.TRUE.toString().equals(properties.getProperty(Constants.IS_DEFAULT_LOG_LEVEL))
            && StringUtil.isBlank(environment.get(loggingLevelKey))) {
            for (int i = Constants.LOG_LEVEL.length(); i < loggingLevelKey.length(); ++i) {
                if (loggingLevelKey.charAt(i) == '.') {
                    String level = environment.get(loggingLevelKey.substring(0, i + 1)
                                                   + Constants.LOG_START);
                    if (!StringUtil.isBlank(level)) {
                        properties.setProperty(loggingLevelKey, level);
                    }
                }
            }
        }

        String spaceLoggingConfig = environment.get(String.format(Constants.LOGGING_CONFIG_PATH,
            spaceId.getSpaceName()));
        if (!StringUtil.isBlank(spaceLoggingConfig)) {
            confFile = this.getClass().getClassLoader().getResource(spaceLoggingConfig);
        }

        Iterator<LogbackReInitializer> matchers = ServiceLoader.load(LogbackReInitializer.class,
            this.getClass().getClassLoader()).iterator();
        if (matchers.hasNext()) {
            LogbackReInitializer logbackReInitializer = matchers.next();
            logbackReInitializer.reInitialize(spaceId, loggerContext, properties, confFile);
        }
    }
}