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

import com.alipay.sofa.common.log.base.AbstraceLogTestBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static com.alipay.sofa.common.log.Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY;

/**
 * 注释:随机选择
 * <p>
 * SLF4J: Class path contains multiple SLF4J bindings.
 * SLF4J: Found binding in [jar:file:/Users/yangguanchao/.m2/org/slf4j/slf4j-log4j12/1.7.21/slf4j-log4j12-1.7.21.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: Found binding in [jar:file:/Users/yangguanchao/.m2/org/apache/logging/log4j/log4j-slf4j-impl/2.6.2/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: Found binding in [jar:file:/Users/yangguanchao/.m2/ch/qos/logback/logback-classic/1.1.7/logback-classic-1.1.7.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
 * SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
 * <p>
 * <p/>
 * Created by kevin.luy@alipay.com on 16/9/19.
 */
public class LoggerSpaceManagerTest extends AbstraceLogTestBase {

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
    public void TestDisableLoggerSpaceFactory() {
        System.setProperty(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        //        if (!MultiAppLoggerSpaceManager.isSpaceInitialized("com.alipay.sofa.rpc")) {
        //            MultiAppLoggerSpaceManager.init("com.alipay.sofa.rpc", null);
        //        }
        Logger logger = LoggerSpaceManager.getLoggerBySpace("com.foo.Bar", "com.alipay.sofa.rpc");
        Logger logger2 = LoggerSpaceManager.getLoggerBySpace("com.foo.Bar", "com.alipay.sofa.rpc");
        Assert.assertSame(logger, logger2);
        Assert.assertSame(logger, Constants.DEFAULT_LOG);
    }

    //    @Test(expected = IllegalStateException.class)
    //    public void TestGetAndThenInit() {
    //        Logger logger = LoggerSpaceManager.getLoggerBySpace("com.foo.Bar2", "com.alipay.sofa.rest");
    //        LoggerSpaceManager.init("com.foo.Bar2", null);
    //    }
}
