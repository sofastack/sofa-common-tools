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
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.log.spi.LogbackFilterGenerator;
import com.alipay.sofa.common.log.spi.LogbackReInitializer;
import com.alipay.sofa.common.utils.AssertUtil;
import org.slf4j.Logger;

import java.net.URL;
import java.util.*;

/**
 * @author qilong.zql
 * @sicne 1.0.15
 */
public class LogbackLoggerSpaceFactory extends AbstractLoggerSpaceFactory {

    private SpaceId       spaceId;
    private LoggerContext loggerContext;
    private Properties    properties;
    private URL           confFile;

    /**
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
        Iterator<LogbackFilterGenerator> matchers = ServiceLoader.load(
            LogbackFilterGenerator.class, this.getClass().getClassLoader()).iterator();
        while (matchers.hasNext()) {
            LogbackFilterGenerator matcher = matchers.next();
            this.loggerContext.getTurboFilterList().addAll(
                Arrays.asList(matcher.generatorFilters()));
        }
        initialize();
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

    private void initialize() {
        AssertUtil.notNull(loggerContext);
        AssertUtil.notNull(properties);
        AssertUtil.notNull(confFile);

        for (Map.Entry entry : properties.entrySet()) {
            loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
        }

        if (confFile != null) {
            try {
                new ContextInitializer(loggerContext).configureByResource(confFile);
            } catch (JoranException e) {
                throw new IllegalStateException("Logback loggerSpaceFactory build error", e);
            }
        }
    }

    public void reInitialize(Map<String, String> environment) {
        Iterator<LogbackReInitializer> matchers = ServiceLoader.load(LogbackReInitializer.class,
            this.getClass().getClassLoader()).iterator();
        properties.putAll(environment);
        if (matchers.hasNext()) {
            LogbackReInitializer logbackReInitializer = matchers.next();
            logbackReInitializer.reInitialize(spaceId, loggerContext, properties, confFile);
        }
    }
}