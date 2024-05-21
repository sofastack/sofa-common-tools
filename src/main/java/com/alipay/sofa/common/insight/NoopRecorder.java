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

/**
 * @author muqingcai
 * @version 2024年4月19日 上午9:54:51
 */
public class NoopRecorder implements Recorder {
    public static final NoopRecorder INSTANCE = new NoopRecorder();

    private NoopRecorder() {
    }

    @Override
    public void start(RecordScene scene, RecordContext context) {

    }

    @Override
    public void stop(RecordScene scene, RecordContext context) {

    }
}
