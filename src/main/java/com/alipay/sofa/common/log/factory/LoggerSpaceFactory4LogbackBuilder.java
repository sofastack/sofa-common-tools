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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.alipay.sofa.common.log.SpaceInfo;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import org.slf4j.Logger;

import java.net.URL;
import java.util.Map;

/**
 * Created by kevin.luy@alipay.com on 16/9/14.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceFactory4LogbackBuilder extends AbstractLoggerSpaceFactoryBuilder {
    public LoggerSpaceFactory4LogbackBuilder(SpaceInfo spaceInfo) {
        super(spaceInfo);
    }

    @Override
    public AbstractLoggerSpaceFactory doBuild(String spaceName, ClassLoader spaceClassloader,
                                              URL url) {
        final LoggerContext loggerContext = new LoggerContext();

        for (Map.Entry entry : getProperties().entrySet()) {
            //from Map<String,String>
            loggerContext.putProperty((String) entry.getKey(), (String) entry.getValue());
        }

        if (url != null) {
            try {
                new ContextInitializer(loggerContext).configureByResource(url);
            } catch (JoranException e) {
                throw new IllegalStateException("Logback loggerSpaceFactory build error", e);
            }
        }
        return new AbstractLoggerSpaceFactory(getLoggingToolName()) {

            @Override
            public Logger setLevel(String loggerName, AdapterLevel adapterLevel) throws Exception {
                ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) this
                    .getLogger(loggerName);
                ch.qos.logback.classic.Level logbackLevel = this.toLogbackLevel(adapterLevel);
                logbackLogger.setLevel(logbackLevel);
                return logbackLogger;
            }

            @Override
            public Logger getLogger(String name) {
                return loggerContext.getLogger(name);
            }

            private ch.qos.logback.classic.Level toLogbackLevel(AdapterLevel adapterLevel) {
                if (adapterLevel == null) {
                    throw new IllegalStateException("AdapterLevel is NULL when adapter to logback.");
                }
                switch (adapterLevel) {
                    case TRACE:
                        return ch.qos.logback.classic.Level.TRACE;
                    case DEBUG:
                        return ch.qos.logback.classic.Level.DEBUG;
                    case INFO:
                        return ch.qos.logback.classic.Level.INFO;
                    case WARN:
                        return ch.qos.logback.classic.Level.WARN;
                    case ERROR:
                        return ch.qos.logback.classic.Level.ERROR;
                    default:
                        throw new IllegalStateException(adapterLevel
                                                        + " is unknown when adapter to logback.");
                }
            }
        };
    }

    @Override
    protected String getLoggingToolName() {
        return "logback";
    }
}
