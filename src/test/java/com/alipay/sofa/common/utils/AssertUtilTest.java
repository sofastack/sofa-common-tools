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

import org.junit.Test;

/**
 * AssertUtil Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since 18/06/04
 */
public class AssertUtilTest {

    /**
     * Method: isTrue(boolean expression, String message)
     */
    @Test
    public void testIsTrueForExpressionMessage() throws Exception {
        boolean isSuccess = false;
        AssertUtil.isTrue(!isSuccess, "isTrue");
        boolean isException = false;
        try {
            AssertUtil.isTrue(isSuccess, "isTrue");
        } catch (Exception ex) {
            isException = true;
        }
        AssertUtil.isTrue(isException);
    }

    /**
     * Method: isNull(Object object, String message)
     */
    @Test
    public void testIsNullForObjectMessage() throws Exception {
        Object object = null;
        AssertUtil.isNull(object, "null");
        AssertUtil.isNull(object);
    }
}
