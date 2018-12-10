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

import com.alipay.sofa.common.log.ReportUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * ReportUtil Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since 18/06/04
 */
public class ReportUtilTest {

    @Test
    public void testUtils() {
        String errMsg = "Some Error Msg";
        boolean isException = false;
        try {
            ReportUtil.reportError("RuntimeException", new RuntimeException());
        } catch (Exception ex) {
            isException = true;
        }
        assertFalse(isException);
    }
}
