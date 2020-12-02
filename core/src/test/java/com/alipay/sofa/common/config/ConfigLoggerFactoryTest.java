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
package com.alipay.sofa.common.config;

import com.alipay.sofa.common.config.log.ConfigLoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * @author zhaowang
 * @version : ConfigLoggerFactoryTest.java, v 0.1 2020年12月02日 2:50 下午 zhaowang Exp $
 */
public class ConfigLoggerFactoryTest {

    @Test
    public void test() {
        String name = null;
        Logger logger = ConfigLoggerFactory.getLogger(name);
        Assert.assertNull(logger);
        name = "com";
        logger = ConfigLoggerFactory.getLogger(name);
        Assert.assertNotNull(logger);

        Class klass = null;
        logger = ConfigLoggerFactory.getLogger(klass);
        Assert.assertNull(logger);

        klass = ConfigLoggerFactoryTest.class;
        logger = ConfigLoggerFactory.getLogger(klass);
        Assert.assertNotNull(logger);

    }
}