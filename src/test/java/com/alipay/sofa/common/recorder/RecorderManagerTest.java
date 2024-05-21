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
package com.alipay.sofa.common.recorder;

import com.alipay.sofa.common.insight.NoopRecorder;
import com.alipay.sofa.common.insight.Recorder;
import com.alipay.sofa.common.insight.RecorderManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author muqingcai
 * @version 2024年5月13日 下午12:45:52
 */
public class RecorderManagerTest {
    @Test
    public void testGetRecorder() {
        Recorder recorderBeforeInit = RecorderManager.getRecorder();
        Assert.assertTrue(recorderBeforeInit instanceof NoopRecorder);

        RecorderManager.init();
        Recorder recorderAfterInit = RecorderManager.getRecorder();
        Assert.assertTrue(recorderAfterInit instanceof SampleRecorder);
    }
}
