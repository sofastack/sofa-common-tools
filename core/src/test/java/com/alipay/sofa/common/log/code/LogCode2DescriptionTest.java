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
package com.alipay.sofa.common.log.code;

import com.alipay.sofa.common.code.LogCode2Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/7
 */
public class LogCode2DescriptionTest {
    private static final String COMPONENT_NAME = "SOFA-TEST";

    @After
    public void after() {
        LogCode2Description.removeCodeSpace(COMPONENT_NAME);
    }

    @Test
    public void testDefaultLocale() {
        Locale.setDefault(new Locale("Unknown"));

        LogCode2Description logCode2Description = LogCode2Description.create(COMPONENT_NAME);
        Assert.assertEquals("SOFA-TEST-00-00000: All is well",
            logCode2Description.convert("00-00000"));
        Assert.assertEquals("SOFA-TEST-01-00001: Some things goes wrong",
            logCode2Description.convert("01-00001"));
        Assert.assertEquals("SOFA-TEST-11-11111: Unknown Code",
            logCode2Description.convert("11-11111"));
    }

    @Test
    public void testCnLocale() {
        Locale.setDefault(new Locale("zh", "CN"));

        LogCode2Description logCode2Description = LogCode2Description.create(COMPONENT_NAME);
        Assert.assertEquals("SOFA-TEST-00-00000: 一切都好", logCode2Description.convert("00-00000"));
        Assert.assertEquals("SOFA-TEST-01-00001: 出现了问题", logCode2Description.convert("01-00001"));
        Assert.assertEquals("SOFA-TEST-11-11111: Unknown Code",
            logCode2Description.convert("11-11111"));
    }

    @Test
    public void alreadyInitialized() {
        LogCode2Description logCode2Description1 = LogCode2Description.create(COMPONENT_NAME);
        LogCode2Description logCode2Description2 = LogCode2Description.create(COMPONENT_NAME);
        Assert.assertEquals(logCode2Description1, logCode2Description2);
    }

    @Test
    public void testDirectConvert() {
        Locale.setDefault(new Locale("zh", "CN"));

        Assert.assertEquals("SOFA-TEST-00-00000: 一切都好",
            LogCode2Description.convert(COMPONENT_NAME, "00-00000"));
        Assert.assertEquals("SOFA-TEST-01-00001: 出现了问题",
            LogCode2Description.convert(COMPONENT_NAME, "01-00001"));
    }
}
