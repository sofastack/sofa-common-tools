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
package com.alipay.sofa.common.log.global;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.utils.ReportUtil;

/**
 * Slite2LogPathInit
 * <p>
 * Created by yangguanchao on 16/11/3.
 */
public class Slite2LogPathInit {

    /***
     * 在中间件 starter 的 ApplicationContextInitializer 要调用此函数做判断,来达到设置日志路径的目的:
     * <p>
     * 1.是否设置了全局路径参数 logging.path
     * 2.没有设置将设置路径 middlewareLoggingPath
     *
     * @param middlewareLoggingPath 中间件日志路径参数
     */
    public static void initLoggingPath(String middlewareLoggingPath) {
        if (isBlank((String) System.getProperties().get(Constants.LOG_PATH))
            && !isBlank(middlewareLoggingPath)) {
            System.getProperties().put(Constants.LOG_PATH, middlewareLoggingPath);
            ReportUtil.report("Actual " + Constants.LOG_PATH + " is [ " + middlewareLoggingPath
                              + " ]");
        }
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
