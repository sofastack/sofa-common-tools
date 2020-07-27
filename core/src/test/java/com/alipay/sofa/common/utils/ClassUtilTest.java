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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/23
 */
public class ClassUtilTest {
    @Test
    public void getFieldTest() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        Integer capacity = ClassUtil.getField("queueCapacity", threadPool);
        if (capacity != null) {
            Assert.assertEquals(Integer.MAX_VALUE, (long) capacity);
        }
    }

    @Test
    public void setFieldTest() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        ClassUtil.setField("queueCapacity", threadPool, 10);
        Integer capacity = ClassUtil.getField("queueCapacity", threadPool);
        if (capacity != null) {
            Assert.assertEquals(10, (long) capacity);
        }
    }
}
