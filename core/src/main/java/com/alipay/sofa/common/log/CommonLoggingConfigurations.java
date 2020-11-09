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

import com.alipay.sofa.common.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/9
 */
public class CommonLoggingConfigurations {
    // Logger set that console appender attaches
    private static Set<String>               loggerConsoleWhiteSet;

    // For configurations from outside, especially Spring Boot
    private final static Map<String, String> externalConfigurations = new HashMap<>();

    /**
     * Subsequent same invocation will override previous value
     */
    public static void loadExternalConfiguration(String key, String value) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
            externalConfigurations.put(key, value);
        }
    }

    public static Map<String, String> getExternalConfigurations() {
        return externalConfigurations;
    }

    public static void setLoggerConsoleWhiteSet(Set<String> set) {
        loggerConsoleWhiteSet = set;
    }

    public static boolean shouldAttachConsoleAppender(String loggerName) {
        if (loggerConsoleWhiteSet == null) {
            return true;
        }
        return loggerConsoleWhiteSet.contains(loggerName);
    }
}
