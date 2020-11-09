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

/**
 * @author huzijie
 * @version TracerUtil.java, v 0.1 2020年10月26日 4:39 下午 huzijie Exp $
 */
public class TracerUtil {

    private TracerUtil() {
    }

    /**
     * Search in thread <code>t</code> for traceId if used in SOFA-RPC context.
     * This method is protected in that subclass may need to customized logic.
     * Using reflection not only because threadLocal fields of thread are private,
     * but also we don't want to introduce tracer dependency.
     * @param t the thread
     * @return traceId, maybe null if not found
     */
    public static String traceIdSafari(Thread t) {
        try {
            for (Object o : (Object[]) ClassUtil.getField("table",
                ClassUtil.getField("threadLocals", t))) {
                if (o != null) {
                    try {
                        return ClassUtil.getField(
                            "traceId",
                            ClassUtil.getField("sofaTracerSpanContext",
                                ClassUtil.getField("value", o)));
                    } catch (Throwable e) {
                        // do nothing
                    }
                }
            }
        } catch (Throwable e) {
            // This method shouldn't interfere with normal execution flow
            return null;
        }
        return null;
    }
}