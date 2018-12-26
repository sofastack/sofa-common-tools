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
package com.alipay.sofa.common.security;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * SensitiveDataUtil Tester.
 *
 * Repo : git@gitlab.alipay-inc.com:alipay-sofa/alipay-service-security.git
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>八月 11, 2017</pre>
 */
public class SensitiveDataUtilTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void test() {
        Assert.assertEquals("130******58", SensitiveDataUtil.alipayLogonIdHide("13012345658"));
        Assert.assertEquals("tit***@qq.com",
            SensitiveDataUtil.alipayLogonIdHide("titxue.yang@qq.com"));
        Assert.assertEquals("13****23", SensitiveDataUtil.alipayLogonIdHide("13123423"));
        Assert.assertEquals("130*58", SensitiveDataUtil.alipayLogonIdHideSMS("13012345658"));
        Assert
            .assertEquals("tit*@qq.*", SensitiveDataUtil.alipayLogonIdHideSMS("tit1.2345@qq.com"));
        Assert.assertEquals("13*23", SensitiveDataUtil.alipayLogonIdHideSMS("13123423"));
        Assert.assertEquals("6****************8",
            SensitiveDataUtil.idCardNoHide("612345678901234568"));
        Assert.assertEquals("622575******1496",
            SensitiveDataUtil.bankCardNoHide("6225751234561496"));
        Assert.assertEquals("62257****1496", SensitiveDataUtil.bankCardNoHide("6225712341496"));
        Assert.assertEquals("130******58", SensitiveDataUtil.phoneOrTelNoHide("13012345658"));
        Assert.assertEquals("0571-****8888", SensitiveDataUtil.phoneOrTelNoHide("0571-12348888"));
        Assert.assertEquals("130******58", SensitiveDataUtil.cellphoneHide("13012345658"));
        Assert.assertEquals("07****358", SensitiveDataUtil.cellphoneHide("071234358"));
        Assert.assertEquals("130*58", SensitiveDataUtil.cellphoneHideSMS("13012345658"));
        Assert.assertEquals("07*358", SensitiveDataUtil.cellphoneHideSMS("071234358"));
        Assert.assertEquals("*雷", SensitiveDataUtil.nameHide("李雷"));
        Assert.assertEquals("*梅梅", SensitiveDataUtil.nameHide("韩梅梅"));
        Assert.assertEquals("**相如", SensitiveDataUtil.nameHide("司马相如"));
        Assert.assertEquals("穆*****耶稣", SensitiveDataUtil.nameHide("穆罕穆德-约耶稣"));
    }

}
