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
    String LOG_DIRECTORY                                         = "log";
    String LOG_XML_CONFIG_FILE_NAME                              = "log-conf.xml";
    String LOG_XML_CONFIG_FILE_ENV_PATTERN                       = "log-conf-%s.xml";

    String LOG_PATH                                              = "logging.path";
    String LOG_PATH_PREFIX                                       = "logging.path.";

    String OLD_LOG_PATH                                          = "loggingRoot";
    String LOG_LEVEL_PREFIX                                      = "logging.level.";
    String DEFAULT_MIDDLEWARE_SPACE_LOG_LEVEL                    = "INFO";
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
}
