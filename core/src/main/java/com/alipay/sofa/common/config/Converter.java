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
package com.alipay.sofa.common.config;

/**
 * @author zhaowang
 * @version : Converter.java, v 0.1 2020年12月09日 11:46 上午 zhaowang Exp $
 */
public interface Converter {

    /**
     * Convert value to target type object.
     * @param value source value
     * @param targetType  target type
     * @param <T>
     * @return object of target type
     */
    <T> T convert(String value, Class<T> targetType);
}