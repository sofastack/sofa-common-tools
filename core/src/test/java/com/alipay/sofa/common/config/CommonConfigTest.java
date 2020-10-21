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

import org.junit.Assert;
import org.junit.Test;

import static com.alipay.sofa.common.configs.CommonToolsConfig.COMMON_THREAD_LOG_PERIOD;

/**
 * @author zhaowang
 * @version : CommonConfigTest.java, v 0.1 2020年10月21日 3:24 下午 zhaowang Exp $
 */
public class CommonConfigTest {

    @Test
    public void commonConfigTest() {
        System.setProperty(COMMON_THREAD_LOG_PERIOD.getKey(), "1000");
        Long config = SofaCommonConfig.getInstance().getConfig(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(1000L, config.longValue());

        System.setProperty(COMMON_THREAD_LOG_PERIOD.getKey(), "");
        config = SofaCommonConfig.getInstance().getConfig(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(10L, config.longValue());

        System.setProperty(COMMON_THREAD_LOG_PERIOD.getAlias()[0], "8");
        config = SofaCommonConfig.getInstance().getConfig(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(8L, config.longValue());

    }
}