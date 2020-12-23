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
package com.alipay.sofa.common;

import com.alipay.sofa.common.config.ConfigKey;

/**
 * @author zhaowang
 * @version : CommonToolsConfig.java, v 0.1 2020年10月20日 8:53 下午 zhaowang Exp $
 */
public class CommonToolConfigKeys {

    public static final String            LOG_PREFIX               = "sofa.common.log.";

    public static final ConfigKey<String> COMMON_LOG_LEVEL         = ConfigKey
                                                                       .build(
                                                                           LOG_PREFIX + "level",
                                                                           "info",
                                                                           true,
                                                                           "控制日志输出级别",
                                                                           new String[] { "__cloud_engine__LOG_LEVEL" });

    public static final ConfigKey<String> COMMON_LOG_FILE          = ConfigKey
                                                                       .build(
                                                                           LOG_PREFIX + "file",
                                                                           "./logs",
                                                                           false,
                                                                           "控制输出路径",
                                                                           new String[] { "__cloud_engine__LOG_FILE" });

    public static final String            THREAD_PREFIX            = "com.alipay.sofa.common.thread.";

    public static final ConfigKey<Long>   COMMON_THREAD_LOG_PERIOD = ConfigKey
                                                                       .build(
                                                                           THREAD_PREFIX + "period",
                                                                           10L,
                                                                           false,
                                                                           "控制线程信息打印间隔",
                                                                           new String[] { "thread_log_period" });

}