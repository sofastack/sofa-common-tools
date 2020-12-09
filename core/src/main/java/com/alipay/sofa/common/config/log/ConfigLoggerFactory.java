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
package com.alipay.sofa.common.config.log;

import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.common.utils.ThreadLoggerFactory;
import org.slf4j.Logger;

/**
 * @author zhaowang
 * @version : ConfigLoggerFactory.java, v 0.1 2020年12月01日 2:33 下午 zhaowang Exp $
 */
public class ConfigLoggerFactory {

    public static final Logger  CONFIG_COMMON_DIGEST_LOGGER = ThreadLoggerFactory
                                                                .getLogger("com.alipay.sofa.common.config.digest");

    private static final String COMMON_CONFIG_LOG_SPACE     = "sofa-common-tools";

    public static Logger getLogger(String name) {
        if (StringUtil.isEmpty(name)) {
            return null;
        }

        return LoggerSpaceManager.getLoggerBySpace(name, COMMON_CONFIG_LOG_SPACE);
    }

    public static Logger getLogger(Class<?> klass) {
        if (klass == null) {
            return null;
        }

        return getLogger(klass.getCanonicalName());
    }

}