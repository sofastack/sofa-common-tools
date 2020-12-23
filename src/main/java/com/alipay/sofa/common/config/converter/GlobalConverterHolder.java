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
package com.alipay.sofa.common.config.converter;

import com.alipay.sofa.common.config.Converter;

/**
 * @author zhaowang
 * @version : GlobalConverterHolder.java, v 0.1 2020年12月09日 11:51 上午 zhaowang Exp $
 */
public class GlobalConverterHolder {

    public static final Converter     DEFAULT_CONVERTER = new DefaultConverter();
    private static volatile Converter converter         = DEFAULT_CONVERTER;

    public static Converter getGlobalConverter() {
        return converter;
    }

    public static void setConverter(Converter converter) {
        synchronized (GlobalConverterHolder.class) {
            GlobalConverterHolder.converter = converter;
        }
    }
}