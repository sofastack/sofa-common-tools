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
package com.alipay.sofa.common.log;

import java.util.Map;

/**
 * Use LogSpace instead
 *
 * Created by kevin.luy@alipay.com on 2016/12/1.
 */
@Deprecated
public class SpaceInfo extends LogSpace {
    public SpaceInfo putAll(Map properties) {
        super.putAll(properties);
        return this;
    }

    public SpaceInfo setProperty(String key, String value) {
        super.setProperty(key, value);
        return this;
    }
}
