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
package com.alipay.sofa.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author huzijie
 * @version CharsetUtilTest.java, v 0.1 2023年05月26日 10:29 AM huzijie Exp $
 */
public class CharsetUtilTest {

    private final String input = "一个中文字符串";
    private final byte[] utf8  = input.getBytes(StandardCharsets.UTF_8);
    private final byte[] gbk   = input.getBytes("GBK");

    public CharsetUtilTest() throws UnsupportedEncodingException {
    }

    @Test
    public void testAssertUtf8WellFormed() {
        CharsetUtil.assertUTF8WellFormed(utf8);
        Assert.assertThrows(IllegalArgumentException.class, () -> CharsetUtil.assertUTF8WellFormed(gbk));
    }

    @Test
    public void testMonitorUtf8WellFormed() {
        CharsetUtil.monitorUTF8WellFormed(utf8);
        CharsetUtil.monitorUTF8WellFormed(gbk);
    }

    @Test
    public void testCheckUtf8WellFormed() {
        CharsetUtil.checkUTF8WellFormed(utf8, 0);
        Assert.assertThrows(IllegalArgumentException.class, () -> CharsetUtil.checkUTF8WellFormed(gbk, 0));

        CharsetUtil.checkUTF8WellFormed(utf8, 1);
        CharsetUtil.checkUTF8WellFormed(gbk, 1);
    }

    @Test
    public void testIsUtf8WellFormed() {
        Assert.assertTrue(CharsetUtil.isUTF8WellFormed(utf8));
        Assert.assertFalse(CharsetUtil.isUTF8WellFormed(gbk));

        Assert.assertTrue(CharsetUtil.isUTF8WellFormed(utf8, 0, 3));
        Assert.assertFalse(CharsetUtil.isUTF8WellFormed(gbk, 0, 3));
    }
}
