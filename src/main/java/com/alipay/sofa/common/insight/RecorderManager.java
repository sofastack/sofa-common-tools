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
package com.alipay.sofa.common.insight;

import com.alipay.sofa.common.config.log.ConfigLoggerFactory;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author muqingcai
 * @date 2024年4月11日 下午2:32:31
 */
public class RecorderManager {
    private static final Logger LOGGER = ConfigLoggerFactory.getLogger(RecorderManager.class);
    private static final AtomicReference<Recorder> RECORDER = new AtomicReference<>(NoopRecorder.INSTANCE);

    public static Recorder getRecorder() {
        return RECORDER.get();
    }

    public static void init() {
        Recorder targetRecorder = null;
        ServiceLoader<Recorder> loader = ServiceLoader.load(Recorder.class);
        Iterator<Recorder> iterator = loader.iterator();
        if (iterator.hasNext()) {
            targetRecorder = iterator.next();
        }
        if (targetRecorder != null) {
            boolean state = RECORDER.compareAndSet(NoopRecorder.INSTANCE, targetRecorder);
            if (state) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Init recorder successfully, class: {}", targetRecorder.getClass().getName());
                }
            } else {
                LOGGER.warn("Cannot init recorder repeatedly ");
            }
        }
    }
}
