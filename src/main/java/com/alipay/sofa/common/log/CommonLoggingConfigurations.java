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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/9
 */
public class CommonLoggingConfigurations {

    private final static       Set<String> LOGGER_CONSOLE_WHITE_SET = ConcurrentHashMap.newKeySet();

    private final static       Set<String> LOGGER_CONSOLE_PREFIX_WHITE_SET = ConcurrentHashMap.newKeySet();

    /**
     *  For configurations from outside, especially Spring Boot
     */
    private final static Map<String, String> EXTERNAL_CONFIGURATIONS = new ConcurrentHashMap<>();

    /**
     * Subsequent same invocation will override previous value
     */
    public static void loadExternalConfiguration(String key, String value) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
            EXTERNAL_CONFIGURATIONS.put(key, value);
        }
    }

    public static Map<String, String> getExternalConfigurations() {
        return EXTERNAL_CONFIGURATIONS;
    }

    public static void appendConsoleLoggerName(String loggerName) {
        LOGGER_CONSOLE_WHITE_SET.add(loggerName);
    }

    public static void appendConsolePrefixWhiteLoggerName(String loggerName) {
        LOGGER_CONSOLE_PREFIX_WHITE_SET.add(loggerName);
    }

    public static void addAllConsoleLogger(Set<String> set) {
        LOGGER_CONSOLE_WHITE_SET.addAll(set);
    }

    public static void addAllConsolePrefixWhiteLoggerName(Set<String> set) {
        LOGGER_CONSOLE_PREFIX_WHITE_SET.addAll(set);
    }

    public static Set<String> getLoggerConsoleWhiteSet() {
        return LOGGER_CONSOLE_WHITE_SET;
    }

    public static Set<String> getLoggerConsolePrefixWhiteSet() {
        return LOGGER_CONSOLE_PREFIX_WHITE_SET;
    }

    public static boolean shouldAttachConsoleAppender(String loggerName) {
        return LOGGER_CONSOLE_WHITE_SET.contains(loggerName) || LOGGER_CONSOLE_PREFIX_WHITE_SET.stream().anyMatch(loggerName::startsWith);
    }

    public static void clearLoggerConsoleProperties() {
        LOGGER_CONSOLE_WHITE_SET.clear();
        LOGGER_CONSOLE_PREFIX_WHITE_SET.clear();
    }
}
