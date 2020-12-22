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

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

/**
 * Created by kevin.luy@alipay.com on 16/9/13.
 */
public class Log4jTest {

    @Test
    public void testIndependentSpaceLog4j() {
        LoggerRepository repo1 = new Hierarchy(new RootLogger((Level) Level.DEBUG));
        URL url1 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/log4j/log-conf.xml");
        OptionConverter.selectAndConfigure(url1, null, repo1);
        Logger logger1 = repo1.getLogger("com.foo.Bar");
        Assert.assertNotNull(logger1);

        //log4j logger 2

        LoggerRepository repo2 = new Hierarchy(new RootLogger((Level) Level.DEBUG));
        URL url2 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/log4j/log4j_b.xml");
        OptionConverter.selectAndConfigure(url2, null, repo2);
        Logger logger2 = repo1.getLogger("com.foo.Bar2");
        Assert.assertNotNull(logger2);

        Assert.assertNotSame(logger1, logger2);
    }

    @Test
    public void testLocalProperties() throws NoSuchFieldException, IllegalAccessException {

        LoggerRepository repo2 = new Hierarchy(new RootLogger((Level) Level.DEBUG));
        URL url2 = LogbackTest.class.getResource("/com/alipay/sofa/rpc/log/log4j/log4j_b.xml");
        DOMConfigurator domConfigurator = new DOMConfigurator();

        Field field = DOMConfigurator.class.getDeclaredField("props");
        field.setAccessible(true);
        Properties props = new Properties();
        field.set(domConfigurator, props);
        props.put("hello", "defaultb");

        domConfigurator.doConfigure(url2, repo2);

        Logger logger2 = repo2.getLogger("com.foo.Bar3");
        Assert.assertTrue(logger2.getAllAppenders().hasMoreElements());

    }
}
