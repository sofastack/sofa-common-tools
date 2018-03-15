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

import com.alipay.sofa.common.utils.AssertUtil;
import com.alipay.sofa.common.utils.ReportUtil;
import org.apache.logging.log4j.util.Strings;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * 日志环境工具类:根据不同的环境选择不同的日志实现
 * <p>
 * Created by yangguanchao on 16/9/20.
 */
public final class LogEnvUtils {

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
     * 对通用的全局日志相关的配置变量进行整理
     */
    public static void processGlobalSystemLogProperties() {

        /**
         *  == global.以系统变量file.encoding为准,获取不到再默认设置为UTF-8
         */

        if (System.getProperty(LOG_ENCODING_PROP_KEY) == null) {
            System.setProperty(LOG_ENCODING_PROP_KEY, UTF8_STR);
        }

        /**
         *  == global.logging.path
         */

        String loggingPath = System.getProperty(LOG_PATH);
        String loggingRoot = System.getProperty(OLD_LOG_PATH);
        //以loggingPath为准（可覆盖loggingRoot）
        if (loggingPath != null && !loggingPath.equalsIgnoreCase(loggingRoot)) {
            System.setProperty(OLD_LOG_PATH, loggingPath);
            return;
        }
        // only loggingRoot is configured
        if (loggingRoot != null && (loggingPath == null || loggingPath.isEmpty())) {
            System.setProperty(LOG_PATH, loggingRoot);
        }

        //还是提供一个默认值$HOME/logs,否则中间件单元测试报错,需要手动设置路径
        if ((loggingPath == null || loggingPath.isEmpty())
            && (loggingRoot == null || loggingRoot.isEmpty())) {
            System.setProperty(LOG_PATH, LOGGING_PATH_DEFAULT);
            System.setProperty(OLD_LOG_PATH, LOGGING_PATH_DEFAULT);
        }
    }

    public static String getLogConfEnvSuffix(String spaceName) {
        String logEnvConfig = System.getProperty(LOG_ENV_SUFFIX, Strings.EMPTY);
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

        suffix = (suffix == null) ? Strings.EMPTY : suffix;
        if (!suffix.isEmpty()) {
            ReportUtil.report(spaceName + " log configuration: " + LOG_XML_CONFIG_FILE_NAME
                              + suffix);
        }
        return suffix;
    }

}
