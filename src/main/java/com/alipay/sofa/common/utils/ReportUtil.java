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

import com.alipay.sofa.common.log.Constants;

/**
 * ReportUtil
 * <p>
 * 参照:slf4j 输出信息到控制台及默认（业务app中配置的）日志
 * <p>
 * Created by yangguanchao on 16/9/24.
 */
public class ReportUtil {
    /**/
    public static void report(String msg) {
        System.out.println("Sofa-Middleware-Log SLF4J : " + msg);
        Constants.DEFAULT_LOG.info("Sofa-Middleware-Log SLF4J : " + msg);
    }

    public static void reportWarn(String msg) {
        System.out.println("Sofa-Middleware-Log SLF4J Warn : " + msg);
        Constants.DEFAULT_LOG.warn("Sofa-Middleware-Log SLF4J Warn : " + msg);
    }

    public static void reportError(String msg, Throwable throwable) {
        System.err.println("Sofa-Middleware-Log SLF4J Error : " + msg + ", " + throwable);
        if (throwable != null) {
            throwable.printStackTrace();
        }
        Constants.DEFAULT_LOG.error("Sofa-Middleware-Log SLF4J Error: " + msg);
    }
}
