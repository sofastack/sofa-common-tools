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
package com.alipay.sofa.common.log.env;

import com.alipay.sofa.common.utils.*;

import java.util.HashMap;
import java.util.Map;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * 日志环境工具类:根据不同的环境选择不同的日志实现
 * <p>
 * Created by yangguanchao on 16/9/20.
 */
public final class LogEnvUtils {

    private static volatile Map<String, String> globalSystemProperties;

    private LogEnvUtils() {
    }

    public static boolean isLogbackUsable(ClassLoader spaceClassloader) {
        AssertUtil.notNull(spaceClassloader);
        try {
            return spaceClassloader.loadClass("ch.qos.logback.classic.LoggerContext") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isLog4j2Usable(ClassLoader spaceClassloader) {
        AssertUtil.notNull(spaceClassloader);
        try {
            return (spaceClassloader.loadClass("org.apache.logging.slf4j.Log4jLoggerFactory") != null);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isLog4jUsable(ClassLoader spaceClassloader) {
        AssertUtil.notNull(spaceClassloader);
        try {
            return (spaceClassloader.loadClass("org.slf4j.impl.Log4jLoggerFactory") != null);
        } catch (ClassNotFoundException e) {
            //   logger.debug("log4j dependency is not existed.");
            return false;
        }
    }

    /***
     * slf4j-jcl
     * commons-logger
     *
     * 默认使用 log4j 作为初始化实现
     * @param spaceClassloader
     * @return
     */
    public static boolean isCommonsLoggingUsable(ClassLoader spaceClassloader) {
        AssertUtil.notNull(spaceClassloader);
        try {
            return (spaceClassloader.loadClass("org.slf4j.impl.JCLLoggerAdapter") != null
                    && (spaceClassloader.loadClass("org.apache.commons.logging.impl.Log4JLogger") != null)
                    && (spaceClassloader.loadClass("org.apache.log4j.Logger") != null) && (spaceClassloader
                .loadClass("org.apache.commons.logging.Log") != null));
        } catch (ClassNotFoundException e) {
            //   logger.debug("log4j dependency is not existed.");
            return false;
        }
    }

    /**
     * 对通用的全局日志相关的配置变量进行整理：
     * logging.path
     * loggingRoot
     * file.encoding, defaults to UTF-8
     * Setting PID
     */
    public static Map<String, String> processGlobalSystemLogProperties() {
        if (globalSystemProperties != null) {
            return globalSystemProperties;
        }

        Map<String, String> properties = new HashMap<>();

        properties.put(PROCESS_MARKER, ProcessIdUtil.getProcessId());
        properties.put(LOG_ENCODING_PROP_KEY, System.getProperty(LOG_ENCODING_PROP_KEY, UTF8_STR));

        // 以系统变量 logging.path 和 loggingRoot 为准，优先 logging.path 配置项
        String loggingPath = System.getProperty(LOG_PATH);
        String loggingRoot = System.getProperty(OLD_LOG_PATH);
        if (!StringUtil.isBlank(loggingPath)) {
            loggingRoot = loggingPath;
        } else if (!StringUtil.isBlank(loggingRoot)) {
            loggingPath = loggingRoot;
        } else {
            // 还是提供一个默认值 $HOME/logs，否则中间件单元测试报错，需要手动设置路径
            loggingPath = LOGGING_PATH_DEFAULT;
            loggingRoot = LOGGING_PATH_DEFAULT;
        }
        properties.put(LOG_PATH, loggingPath);
        properties.put(OLD_LOG_PATH, loggingRoot);

        //设置 logging.level.* 和 logging.path.* 配置
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String lowerCaseKey = entry.getKey().toLowerCase();
            if (lowerCaseKey.startsWith(LOG_LEVEL_PREFIX)
                || lowerCaseKey.startsWith(LOG_PATH_PREFIX)
                || lowerCaseKey.startsWith(LOG_CONFIG_PREFIX)) {
                properties.put(lowerCaseKey, entry.getValue());
            }
        }
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                continue;
            }
            String lowerCaseKey = ((String) entry.getKey()).toLowerCase();
            if (lowerCaseKey.startsWith(LOG_LEVEL_PREFIX)
                || lowerCaseKey.startsWith(LOG_PATH_PREFIX)
                || lowerCaseKey.startsWith(LOG_CONFIG_PREFIX)) {
                properties.put(lowerCaseKey, (String) entry.getValue());
            }
        }

        globalSystemProperties = properties;
        keepCompatible(globalSystemProperties, !isLogStarterExist());
        return globalSystemProperties;
    }

    public static String getLogConfEnvSuffix(String spaceName) {
        String logEnvConfig = System.getProperty(LOG_ENV_SUFFIX, StringUtil.EMPTY_STRING);
        String[] spaceNameToEnvSuffix = logEnvConfig.split("&");
        String suffix = null;
        for (int i = 0; i < spaceNameToEnvSuffix.length && suffix == null; ++i) {
            String envConf = spaceNameToEnvSuffix[i];
            String[] conf = envConf.split(":");

            /* 配置格式错误或者不是相同的 spaceName, 直接 skip */
            if (conf.length != 2 || !conf[0].equals(spaceName)) {
                continue;
            } else if (!conf[1].isEmpty()) {
                /* .dev .test .product */
                conf[1] = "." + conf[1];
            }

            suffix = conf[1];
        }

        suffix = (suffix == null) ? StringUtil.EMPTY_STRING : suffix;
        if (!suffix.isEmpty()) {
            ReportUtil.reportDebug(spaceName + " log configuration: " + LOG_XML_CONFIG_FILE_NAME
                                   + suffix);
        }
        return suffix;
    }

    /**
     * keep compatible with previous version. Set system properties of the following attributes:
     * logging.path
     * loggingRoot
     * file.encoding
     */
    public static void keepCompatible(Map<String, String> context, boolean keep) {
        if (!keep) {
            return;
        }
        String loggingPath = System.getProperty(LOG_PATH, context.get(LOG_PATH));
        String fileEncoding = System.getProperty(LOG_ENCODING_PROP_KEY,
            context.get(LOG_ENCODING_PROP_KEY));
        System.setProperty(LOG_PATH, loggingPath);
        System.setProperty(OLD_LOG_PATH, System.getProperty(OLD_LOG_PATH, loggingPath));
        System.setProperty(LOG_ENCODING_PROP_KEY, fileEncoding);
    }

    public static boolean isLogStarterExist() {
        return ClassUtil.isPresent("com.alipay.sofa.common.boot.logging.Mark");
    }

    public static boolean filterAllLogConfig(String key) {
        return key.startsWith(SOFA_MIDDLEWARE_CONFIG_PREFIX) || key.startsWith(LOG_LEVEL_PREFIX)
               || key.startsWith(LOG_PATH_PREFIX) || key.startsWith(LOG_CONFIG_PREFIX)
               || key.equals(LOG_PATH) || key.equals(OLD_LOG_PATH)
               || key.equals(LOG_ENCODING_PROP_KEY);
    }

}
