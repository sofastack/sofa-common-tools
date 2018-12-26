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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Constants middleware log 中涉及的常量信息,其中日志级别默认为 INFO
 * <p>
 * Created by yangguanchao on 16/10/26.
 */
public interface Constants {
    //app中配置,如果没有配置最终会使用（ROOT Log);
    Logger DEFAULT_LOG                                           = LoggerFactory
                                                                     .getLogger("com.alipay.sofa.common.log");
    String LOG_START                                             = "*";
    String LOG_DIRECTORY                                         = "log";
    String LOG_XML_CONFIG_FILE_NAME                              = "log-conf.xml";
    String LOG_XML_CONFIG_FILE_ENV_PATTERN                       = "log-conf-%s.xml";
    String LOG_CONFIG_PROPERTIES                                 = "config.properties";

    String LOG_PATH                                              = "logging.path";
    String LOG_PATH_PREFIX                                       = "logging.path.";

    String OLD_LOG_PATH                                          = "loggingRoot";
    String LOG_LEVEL                                             = "logging.level";
    String LOG_LEVEL_PREFIX                                      = "logging.level.";
    String LOG_CONFIG_PREFIX                                     = "logging.config.";
    String DEFAULT_MIDDLEWARE_SPACE_LOG_LEVEL                    = "INFO";
    String IS_DEFAULT_LOG_PATH                                   = "isDefaultLogPath";
    String IS_DEFAULT_LOG_LEVEL                                  = "isDefaultLogLevel";
    // 指定特定spaceName的环境配置，值格式: {spaceName:dev, spaceName:test, spaceName:product}, 多个用 '&' 符隔开
    String LOG_ENV_SUFFIX                                        = "log.env.suffix";
    //系统变量key -D
    String LOG_ENCODING_PROP_KEY                                 = "file.encoding";
    //禁用所有日志实现
    String SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY                  = "sofa.middleware.log.disable";
    //禁用 commons-logging 的 log4j 日志实现
    String LOG4J_COMMONS_LOGGING_MIDDLEWARE_LOG_DISABLE_PROP_KEY = "log4j.commons.logging.middleware.log.disable";
    //禁用log4j日志实现
    String LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY                 = "log4j.middleware.log.disable";
    //禁用log4j2日志实现
    String LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY                = "log4j2.middleware.log.disable";
    //禁用logback日志实现
    String LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY               = "logback.middleware.log.disable";
    //默认日志编码
    String UTF8_STR                                              = "UTF-8";
    //默认的中间件日志打印路径
    String LOGGING_PATH_DEFAULT                                  = System.getProperty("user.home")
                                                                   + File.separator + "logs";
    // 默认优先级为0，越大越高
    int    DEFAULT_PRIORITY                                      = 0;

    // 进程标识
    String PROCESS_MARKER                                        = "PID";
    // 配置前缀
    String SOFA_MIDDLEWARE_CONFIG_PREFIX                         = "sofa.middleware.log.";
    // 设置所有日志输出到控制台
    String SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH                = "sofa.middleware.log.console";
    // 单个 spaceId 输出到控制台配置格式
    String SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH             = "sofa.middleware.log.%s.console";
    // sofa-common-tools 自身日志开关
    String SOFA_MIDDLEWARE_LOG_INTERNAL_LEVEL                    = "sofa.middleware.log.internal.level";
    // 控制台日志级别, 默认为 INFO
    String SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL                 = "sofa.middleware.log.console.level";
    // 单个 spaceId 控制台日志配置级别，默认为 INFO
    String SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL              = "sofa.middleware.log.%s.console.level";
    // 控制台日志格式
    String SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN           = "sofa.middleware.log.console.logback.pattern";
    String SOFA_MIDDLEWARE_LOG_CONSOLE_LOG4J2_PATTERN            = "sofa.middleware.log.console.log4j2.pattern";
    // 默认控制台日志格式格式
    String SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN_DEFAULT   = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p ${PID:- } --- [%15.15t] %-40.40logger{39} : %m%n";
    String SOFA_MIDDLEWARE_LOG_CONSOLE_LOG4J2_PATTERN_DEFAULT    = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %X{PID} --- [%15.15t] %-40.40logger{39} : %m%n";
    // 指定 spaceId 的日志配置文件，logging.config.spaceId
    String LOGGING_CONFIG_PATH                                   = "logging.config.%s";
    // ExternalContext
    String SOFA_LOG_FIRST_INITIALIZE                             = "sofa.log.first.initialize";
}
