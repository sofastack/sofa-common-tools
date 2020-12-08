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

import com.alipay.sofa.common.utils.Ordered;

/**
 * @author zhaowang
 * @version : ConfigSource.java, v 0.1 2020年10月20日 7:57 下午 zhaowang Exp $
 *
 * TODO Starter 里面把 Spring 里面的值复制进来
 */
public interface ConfigSource extends Ordered {

    // TODO 补注释
    <T> T getConfig(ConfigKey<T> key);

    String getName();

    String getStringConfig(ConfigKey key);

    String getEffectiveKey(ConfigKey configKey);

    //todo 增加 Callback
    // 不加 Callback 了，当更新key-value的时候，现在没有办法映射到 ConfigKey 上.
    // 如果要加Callback 就要维护一个 key -> ConfigKey 的映射关系。

}