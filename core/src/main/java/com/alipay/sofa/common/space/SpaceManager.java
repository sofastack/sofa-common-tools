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
package com.alipay.sofa.common.space;

import com.alipay.sofa.common.utils.ReportUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class SpaceManager {
    private static final ConcurrentHashMap<SpaceId, Space> SPACES_MAP = new ConcurrentHashMap<>();

    /**
     * Get space specified by spaceId
     * This will create a new Space if it doesn't exist
     * @param spaceId space ID
     * @return Space
     */
    public static Space getSpace(SpaceId spaceId) {
        return SPACES_MAP.computeIfAbsent(spaceId, key -> {
            ReportUtil.reportInfo("Space is created for " + spaceId.getSpaceName());
            return new Space();
        });
    }

    public static Space getSpace(String spaceName) {
        return getSpace(SpaceId.withSpaceName(spaceName));
    }
}
