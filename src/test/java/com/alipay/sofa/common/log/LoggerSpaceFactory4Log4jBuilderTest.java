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
package com.alipay.sofa.common.log;

import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import com.alipay.sofa.common.log.base.AbstraceLogTestBase;
import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4Log4jBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Created by kevin.luy@alipay.com on 16/9/19.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceFactory4Log4jBuilderTest extends AbstraceLogTestBase {

    LoggerSpaceFactory4Log4jBuilder loggerSpaceFactory4Log4jBuilder = new LoggerSpaceFactory4Log4jBuilder(
                                                                        new SpaceInfo());

    @Before
    @Override
    public void before() throws Exception {
        super.before();
    }

    @After
    @Override
    public void after() throws Exception {
        super.after();
    }

    @Test
    public void testBuild() {
        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4Log4jBuilder.build(
            "com.alipay.sofa.rpc", this.getClass().getClassLoader());
        Logger logger = loggerSpaceFactory.getLogger("com.foo.Bar");

        logger.info("LoggerSpaceFactory4Log4jBuilderTest  pass.");

        logger = loggerSpaceFactory.getLogger("com.foo.Bar");

        Assert.assertNotNull(logger);
    }

    @Test
    public void testLoggingLevelDefaultInfo() {
        System.clearProperty(Constants.LOG_LEVEL_PREFIX + "com.alipay.sofa.rpc");

        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4Log4jBuilder.build(
            "com.alipay.sofa.rpc", this.getClass().getClassLoader());
        Logger logger = loggerSpaceFactory.getLogger("com.foo.Bar");
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertFalse(logger.isDebugEnabled());
    }

    @Test
    public void testLoggingLevelDebug() {
        System.clearProperty(Constants.LOG_LEVEL_PREFIX + "com.alipay.sofa.rpc");

        SpaceInfo spaceInfo = new SpaceInfo();
        spaceInfo.properties().setProperty(Constants.LOG_LEVEL_PREFIX + "com.alipay.sofa.rpc",
            "debug");
        loggerSpaceFactory4Log4jBuilder = new LoggerSpaceFactory4Log4jBuilder(spaceInfo);

        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4Log4jBuilder.build(
            "com.alipay.sofa.rpc", this.getClass().getClassLoader());
        Logger logger = loggerSpaceFactory.getLogger("com.foo.Bar");
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertTrue(logger.isDebugEnabled());
    }

    @Test
    public void testLoggingLeveDynamicChange() throws Exception {
        String loggerName = "com.foo.Bar";
        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4Log4jBuilder.build(
            LOG_SPACE_TEST, this.getClass().getClassLoader());
        Logger logger = loggerSpaceFactory.getLogger(loggerName);

        System.err.println("init level INFO ===");

        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertFalse(logger.isDebugEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        logger.error("error level===");
        logger.warn("warn level===");
        logger.info("info level===");
        logger.debug("debug level===");
        logger.trace("trace level===");

        System.err.println("Change level to Debug ===");
        Logger logger1 = loggerSpaceFactory.setLevel(loggerName, AdapterLevel.DEBUG);
        //log4j1
        Assert
            .assertTrue(logger1 instanceof org.slf4j.impl.Log4jLoggerAdapter && logger == logger1);
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertTrue(logger.isDebugEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        logger.error("error level===");
        logger.warn("warn level===");
        logger.info("info level===");
        logger.debug("debug level===");
        logger.trace("trace level===");

        System.err.println("Change level to TRACE ===");
        Logger logger2 = loggerSpaceFactory.setLevel(loggerName, AdapterLevel.TRACE);
        //log4j1
        Assert
            .assertTrue(logger2 instanceof org.slf4j.impl.Log4jLoggerAdapter && logger == logger2);
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertTrue(logger.isDebugEnabled());
        Assert.assertTrue(logger.isTraceEnabled());
        logger.error("error level===");
        logger.warn("warn level===");
        logger.info("info level===");
        logger.debug("debug level===");
        logger.trace("trace level===");
    }
}
