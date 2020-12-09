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
package com.alipay.sofa.common.utils;

import com.alipay.sofa.common.log.LogLog;

/**
 * ReportUtil
 * <p>
 * 参照:slf4j 输出信息到控制台及默认（业务app中配置的）日志
 * <p>
 * Created by yangguanchao on 16/9/24.
 */
public class ReportUtil {
    /**
     * print Debug message
     * @param msg message
     */
    public static void reportDebug(String msg) {
        LogLog.debug(msg);
    }

    /**
     * keep compatible
     * @param msg message
     */
    @Deprecated
    public static void report(String msg) {
        reportDebug(msg);
    }

    /**
     * print Info message
     * @param msg message
     */
    public static void reportInfo(String msg) {
        LogLog.info(msg);
    }

    /**
     * print Warn message
     * @param msg message
     */
    public static void reportWarn(String msg) {
        LogLog.warn(msg);
    }

    public static void reportWarn(String msg, Throwable e) {
        LogLog.warn(msg, e);
    }

    public static void reportError(String msg, Throwable throwable) {
        LogLog.error(msg, throwable);
    }

    public static void reportError(String msg) {
        LogLog.error(msg, null);
    }
}
