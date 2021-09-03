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
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4Log4j2Builder;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4LogbackBuilder;
import com.alipay.sofa.common.space.SpaceId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class LoggerSpaceFactoryLogbackBuilderTest extends AbstraceLogTestBase {

    LoggerSpaceFactory4LogbackBuilder loggerSpaceFactory4LogbackBuilder = new LoggerSpaceFactory4LogbackBuilder(
                                                                            new SpaceId(
                                                                                "com.alipay.sofa.rpc"),
                                                                            new SpaceInfo().putAll(LogEnvUtils
                                                                                .processGlobalSystemLogProperties()));

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
    public void testConsoleLogLevel() throws Exception {
        String loggerName = "com.foo.Bar";
        LogSpace spaceInfo = new LogSpace()
            //if turn on this, the space level will be debug,logger.isDebugEnabled() will return true.
            .setProperty(Constants.LOG_ENV_SUFFIX, "dev1")
            //.setProperty(Constants.LOG_LEVEL_PREFIX + "com.alipay.sofa.rpc", "debug")
            .setProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true")
            .setProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "WARN")
            .putAll(LogEnvUtils.processGlobalSystemLogProperties());

        loggerSpaceFactory4LogbackBuilder = new LoggerSpaceFactory4LogbackBuilder(new SpaceId(
            "com.alipay.sofa.rpc"), spaceInfo);

        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4LogbackBuilder.build(
            "com.alipay.sofa.rpc", this.getClass().getClassLoader());
        Logger logger = loggerSpaceFactory.setLevel(loggerName, AdapterLevel.INFO);
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        //if space level below this ,will occur error
        Assert.assertFalse(logger.isDebugEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        logger.trace("trace info===");
        logger.debug("debug info===");
        logger.info("info info===");
        logger.warn("warn info===");
    }

}
