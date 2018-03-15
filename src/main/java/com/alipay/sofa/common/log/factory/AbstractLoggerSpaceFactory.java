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
package com.alipay.sofa.common.log.factory;

import com.alipay.sofa.common.utils.ReportUtil;
import com.alipay.sofa.common.log.adapter.level.AdapterLevel;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * updated by yangguanchao on 17/04/28.
 */
public abstract class AbstractLoggerSpaceFactory implements ILoggerFactory {

    private String source;

    /***
     * 改变指定日志的级别,子类需要复写此方法来实现动态改变日志级别
     * @param loggerName 指定的日志对象
     * @param adapterLevel 要修改为的日志级别
     */
    public Logger setLevel(String loggerName, AdapterLevel adapterLevel) throws Exception {
        ReportUtil.reportWarn("Unsupported change logger level in " + this.getClass()
                              + ", loggerName[" + loggerName + "]");
        return this.getLogger(loggerName);
    }

    public String getSource() {
        return source;
    }

    /**
     * @param source logback,log4j2,log4j,temp,nop
     */
    public AbstractLoggerSpaceFactory(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "LoggerSpaceFactory{" + "source='" + source + '\'' + '}';
    }
}
