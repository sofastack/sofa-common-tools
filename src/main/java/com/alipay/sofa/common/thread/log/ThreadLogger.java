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
package com.alipay.sofa.common.thread.log;

import com.alipay.sofa.common.utils.ThreadLoggerFactory;
import org.slf4j.Logger;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/3/19
 */
public class ThreadLogger {
    public static final Logger INFO_THREAD_LOGGER = ThreadLoggerFactory
                                                      .getLogger("com.alipay.sofa.thread");
    public static final Logger WARN_THREAD_LOGGER = ThreadLoggerFactory
                                                      .getLogger("com.alipay.sofa.thread.warn");

    public static void debug(String format, Object... arguments) {
        if (INFO_THREAD_LOGGER.isDebugEnabled()) {
            INFO_THREAD_LOGGER.debug(format, arguments);
        }
    }

    public static void info(String format, Object... arguments) {
        if (INFO_THREAD_LOGGER.isInfoEnabled()) {
            INFO_THREAD_LOGGER.info(format, arguments);
        }
    }

    public static void warn(String format, Object... arguments) {
        if (WARN_THREAD_LOGGER.isWarnEnabled()) {
            WARN_THREAD_LOGGER.warn(format, arguments);
        }
    }

    public static void error(String format, Object... arguments) {
        if (WARN_THREAD_LOGGER.isErrorEnabled()) {
            WARN_THREAD_LOGGER.error(format, arguments);
        }
    }
}
