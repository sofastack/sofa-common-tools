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

import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by kevin.luy@alipay.com on 16/9/13.
 */
public class Log4j2Test {

    @Test
    public void testIndependentSpaceLog4j2() throws URISyntaxException {
        //  LoggerFactory.getLogger(LogbackTest.class).info("xxx");
        URL url1 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/log4j2/log-conf.xml");
        LoggerContext lc1 = new LoggerContext("rest", null, url1.toURI());
        lc1.start();
        org.apache.logging.log4j.core.Logger logger1 = lc1.getLogger("com.foo.Bar");
        logger1.info("log4j2 - 1");
        Assert.assertNotNull(logger1);

        //log4j2 logger2
        URL url2 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/log4j2/log4j2_b.xml");
        LoggerContext lc2 = new LoggerContext("rpc", null, url2.toURI());
        lc2.start();
        org.apache.logging.log4j.core.Logger logger2 = lc2.getLogger("com.foo.Bar2");
        logger2.info("log4j2 - 2");
        Assert.assertNotNull(logger2);
        Assert.assertNotSame(logger1, logger2);
    }

}
