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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author muqingcai
 * @version  2024年4月19日 上午9:54:59
 */
public class RecordContext {
    private int                 requestId;
    private String              traceId;
    private String              rpcId;
    private String              targetServiceUniqueName;
    private String              methodName;
    private String              moduleName;
    private String              beanName;
    private Map<String, Object> extraInfo;

    /**
     * Constructor.
     */
    public RecordContext() {
        this.extraInfo = new ConcurrentHashMap<>();
    }

    /**
     * Constructor.
     *
     * @param requestId the request id
     */
    public RecordContext(int requestId) {
        this.requestId = requestId;
        this.extraInfo = new ConcurrentHashMap<>();
    }

    /**
     * Constructor.
     *
     * @param moduleName the module name
     * @param beanName   the bean name
     */
    public RecordContext(String moduleName, String beanName) {
        this.extraInfo = new ConcurrentHashMap<>();
        this.moduleName = moduleName;
        this.beanName = beanName;
    }

    /**
     * Gets get request id.
     *
     * @return the get request id
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets set request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets get trace id.
     *
     * @return the get trace id
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Sets set trace id.
     *
     * @param traceId the trace id
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * Gets get rpc id.
     *
     * @return the get rpc id
     */
    public String getRpcId() {
        return rpcId;
    }

    /**
     * Sets set rpc id.
     *
     * @param rpcId the rpc id
     */
    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
    }

    /**
     * Gets get target service unique name.
     *
     * @return the get target service unique name
     */
    public String getTargetServiceUniqueName() {
        return targetServiceUniqueName;
    }

    /**
     * Sets set target service unique name.
     *
     * @param targetServiceUniqueName the target service unique name
     */
    public void setTargetServiceUniqueName(String targetServiceUniqueName) {
        this.targetServiceUniqueName = targetServiceUniqueName;
    }

    /**
     * Gets get method name.
     *
     * @return the get method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets set method name.
     *
     * @param methodName the method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets get module name.
     *
     * @return the get module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets set module name.
     *
     * @param moduleName the module name
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Gets get bean name.
     *
     * @return the get bean name
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * Sets set bean name.
     *
     * @param beanName the bean name
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Gets get extra info.
     *
     * @return the get extra info
     */
    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    /**
     * Sets set extra info.
     *
     * @param extraInfo the extra info
     */
    public void setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "RecordContext{" + "requestId=" + requestId + ", traceId='" + traceId + '\''
               + ", rpcId='" + rpcId + '\'' + ", targetServiceUniqueName='"
               + targetServiceUniqueName + '\'' + ", methodName='" + methodName + '\''
               + ", moduleName='" + moduleName + '\'' + ", beanName='" + beanName + '\''
               + ", extraInfo=" + extraInfo + '}';
    }
}
