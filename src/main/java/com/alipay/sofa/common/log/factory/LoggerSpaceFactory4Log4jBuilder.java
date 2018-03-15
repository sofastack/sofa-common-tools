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
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日志环境工具类:根据不同的环境选择不同的日志实现
 * <p>
 * Created by yangguanchao on 17/01/17.
 */
public class LoggerSpaceFactory4Log4jBuilder extends AbstractLoggerSpaceFactoryBuilder {

    public LoggerSpaceFactory4Log4jBuilder(SpaceInfo spaceInfo) {
        super(spaceInfo);
    }

    @Override
    protected String getLoggingToolName() {
        return "log4j";
    }

    @Override
    public AbstractLoggerSpaceFactory doBuild(String spaceName, ClassLoader spaceClassloader,
                                              URL url) {

        try {
            final LoggerRepository repo = new Hierarchy(new RootLogger((Level) Level.WARN));

            DOMConfigurator domConfigurator = new DOMConfigurator();
            final Field field = DOMConfigurator.class.getDeclaredField("props");
            field.setAccessible(true);
            field.set(domConfigurator, getProperties());

            domConfigurator.doConfigure(url, repo);

            return new AbstractLoggerSpaceFactory(getLoggingToolName()) {

                ConcurrentMap<String, Log4jLoggerAdapter> loggerMap = new ConcurrentHashMap<String, Log4jLoggerAdapter>();

                @Override
                public Logger setLevel(String loggerName, AdapterLevel adapterLevel)
                                                                                    throws Exception {
                    Log4jLoggerAdapter log4jLoggerAdapter = (Log4jLoggerAdapter) this
                        .getLogger(loggerName);
                    org.apache.log4j.Logger log4jLogger = repo.getLogger(loggerName);
                    //level
                    org.apache.log4j.Level log4jLevel = this.toLog4jLevel(adapterLevel);
                    log4jLogger.setLevel(log4jLevel);
                    return log4jLoggerAdapter;
                }

                @Override
                public Logger getLogger(String name) {
                    Log4jLoggerAdapter log4jLoggerAdapter = this.loggerMap.get(name);
                    if (log4jLoggerAdapter != null) {
                        return log4jLoggerAdapter;
                    }

                    Log4jLoggerAdapter newInst = createSlf4jLogger(name);
                    Log4jLoggerAdapter oldInst = this.loggerMap.putIfAbsent(name, newInst);
                    return oldInst == null ? newInst : oldInst;
                }

                private org.apache.log4j.Level toLog4jLevel(AdapterLevel adapterLevel) {
                    if (adapterLevel == null) {
                        throw new IllegalStateException(
                            "AdapterLevel is NULL when adapter to log4j.");
                    }
                    switch (adapterLevel) {
                        case TRACE:
                            return org.apache.log4j.Level.TRACE;
                        case DEBUG:
                            return org.apache.log4j.Level.DEBUG;
                        case INFO:
                            return org.apache.log4j.Level.INFO;
                        case WARN:
                            return org.apache.log4j.Level.WARN;
                        case ERROR:
                            return org.apache.log4j.Level.ERROR;
                        default:
                            throw new IllegalStateException(adapterLevel
                                                            + " is unknown when adapter to log4j.");
                    }
                }

                private Log4jLoggerAdapter createSlf4jLogger(String name) {
                    org.apache.log4j.Logger log4jLogger = repo.getLogger(name);
                    //反射实例化
                    try {
                        Constructor<Log4jLoggerAdapter> constructor = Log4jLoggerAdapter.class
                            .getDeclaredConstructor(org.apache.log4j.Logger.class);
                        constructor.setAccessible(true);
                        return constructor.newInstance(log4jLogger);
                    } catch (Throwable e) {
                        throw new IllegalStateException("get slf4j logger from  log4jLogger err!",
                            e);
                    }
                }
            };

        } catch (Throwable e) {
            throw new IllegalStateException("Log4j loggerSpaceFactory build error!", e);
        }
    }

}
