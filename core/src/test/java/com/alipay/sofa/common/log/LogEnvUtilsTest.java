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
import com.alipay.sofa.common.log.env.LogEnvUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qilong.zql 17/11/15-上午11:00
 */
public class LogEnvUtilsTest extends AbstraceLogTestBase {

    @Before
    @Override
    public void before() {
        System.setProperty(Constants.LOG_ENV_SUFFIX, "app:dev&app2:test&app3:");
    }

    @After
    @Override
    public void after() {
        System.clearProperty(Constants.LOG_ENV_SUFFIX);
    }

    @Test
    public void test() {
        Assert.assertEquals(".dev", LogEnvUtils.getLogConfEnvSuffix("app"));
        Assert.assertEquals(".test", LogEnvUtils.getLogConfEnvSuffix("app2"));
        Assert.assertEquals(Strings.EMPTY, LogEnvUtils.getLogConfEnvSuffix("app3"));
        Assert.assertEquals(Strings.EMPTY, LogEnvUtils.getLogConfEnvSuffix("app4"));
    }

}