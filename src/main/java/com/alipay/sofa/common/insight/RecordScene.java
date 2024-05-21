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
 * @version 2024年4月19日 上午9:55:54
 */
public enum RecordScene {
    /**
     * record 场景
     */
    BOLT_REQUEST_HANDLE("boltRequestHandle", "bolt 协议 RPC 请求处理"),

    TR_REQUEST_HANDLE("trRequestHandle", "tr 协议 RPC 请求处理"),

    SOFA_STARTUP("sofaStartup", "应用启动"),

    SPRING_BEAN_REFRESH("springBeanRefresh", "应用 bean 刷新");

    private final String scene;
    private final String desc;

    RecordScene(String scene, String desc) {
        this.scene = scene;
        this.desc = desc;
    }

    /**
     * Gets get scene.
     *
     * @return the get scene
     */
    public String getScene() {
        return scene;
    }

    /**
     * Gets get desc.
     *
     * @return the get desc
     */
    public String getDesc() {
        return desc;
    }
}
