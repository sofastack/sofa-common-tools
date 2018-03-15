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
import org.slf4j.impl.JCLLoggerAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日志环境工具类:根据不同的环境选择不同的日志实现:日志实现仍然使用log4j,不过兼容了 commons-logging 日志打印的情况
 * <p>
 * Created by yangguanchao on 17/01/17.
 */
public class LoggerSpaceFactory4CommonsLoggingBuilder extends AbstractLoggerSpaceFactoryBuilder {

    public LoggerSpaceFactory4CommonsLoggingBuilder(SpaceInfo spaceInfo) {
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
            Field field = DOMConfigurator.class.getDeclaredField("props");
            field.setAccessible(true);
            field.set(domConfigurator, getProperties());

            domConfigurator.doConfigure(url, repo);

            return new AbstractLoggerSpaceFactory(getLoggingToolName()) {

                ConcurrentMap<String, JCLLoggerAdapter> loggerMap = new ConcurrentHashMap<String, JCLLoggerAdapter>();

                @Override
                public Logger setLevel(String loggerName, AdapterLevel adapterLevel)
                                                                                    throws Exception {
                    JCLLoggerAdapter jclLoggerAdapter = (JCLLoggerAdapter) this
                        .getLogger(loggerName);
                    //获取 log4j 实例
                    org.apache.log4j.Logger log4j = repo.getLogger(loggerName);
                    org.apache.log4j.Level log4jLevel = this.toLog4jLevel(adapterLevel);
                    log4j.setLevel(log4jLevel);
                    return jclLoggerAdapter;
                }

                @Override
                public Logger getLogger(String name) {
                    JCLLoggerAdapter slf4jLogger = (JCLLoggerAdapter) this.loggerMap.get(name);
                    if (slf4jLogger != null) {
                        return slf4jLogger;
                    }

                    JCLLoggerAdapter newInst = createCommonsLoggingLoggerAdapter2Slf4j(name);
                    JCLLoggerAdapter oldInst = this.loggerMap.putIfAbsent(name, newInst);
                    return oldInst == null ? newInst : oldInst;
                }

                private JCLLoggerAdapter createCommonsLoggingLoggerAdapter2Slf4j(String name) {
                    //初始化 log4j 实例
                    org.apache.log4j.Logger log4j = repo.getLogger(name);
                    try {
                        org.apache.commons.logging.impl.Log4JLogger log4jLogger = new org.apache.commons.logging.impl.Log4JLogger(
                            log4j);

                        Constructor<JCLLoggerAdapter> constructor = JCLLoggerAdapter.class
                            .getDeclaredConstructor(org.apache.commons.logging.Log.class,
                                String.class);
                        constructor.setAccessible(true);
                        return constructor.newInstance(log4jLogger, name);
                    } catch (Throwable e) {
                        throw new IllegalStateException(
                            "get adapter from commons-logging to slf4j logger from Log4j  err!", e);
                    }
                }

                private org.apache.log4j.Level toLog4jLevel(AdapterLevel adapterLevel) {
                    if (adapterLevel == null) {
                        throw new IllegalStateException(
                            "AdapterLevel is NULL when adapter common-logging and log4j.");
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
                            throw new IllegalStateException(
                                adapterLevel + " is unknown when adapter common-logging and log4j.");
                    }
                }
            };

        } catch (Throwable e) {
            throw new IllegalStateException(
                "Log4j for commons-logging loggerSpaceFactory build error!", e);
        }
    }
}
