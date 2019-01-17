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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author qilong.zql
 * @sicne 1.0.17
 */
public class ResourceUtilTest {

    @Test
    public void testGetFile() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("test-resource-utils.properties");
        File file = ResourceUtil.getFile(url);
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        Assert.assertEquals(properties.getProperty("keyA"), "A");
        Assert.assertEquals(properties.getProperty("keyB"), "B");
    }

}