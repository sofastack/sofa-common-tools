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
package com.alipay.sofa.common.log.principle;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

/**
 * Created by kevin.luy@alipay.com on 16/9/12.
 */
public class LogbackTest {

    @Test
    public void testIndependentSpaceLogback() throws JoranException {

        URL url1 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/logback/log-conf.xml");
        LoggerContext loggerContext1 = new LoggerContext();
        new ContextInitializer(loggerContext1).configureByResource(url1);
        ch.qos.logback.classic.Logger logger1 = loggerContext1.getLogger("com.foo.Bar");
        logger1.info("log4j2 - 1");
        Assert.assertNotNull(logger1);

        //logback logger 2

        URL url2 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/logback/logback_b.xml");
        LoggerContext loggerContext2 = new LoggerContext();
        new ContextInitializer(loggerContext2).configureByResource(url2);
        Logger logger2 = loggerContext2.getLogger("com.foo.Bar2");
        logger2.info("log4j2 - 222");
        Assert.assertNotNull(logger2);

        Assert.assertNotSame(logger1, logger2);

    }
}
