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

import com.alipay.sofa.common.log.LogSpace;
import com.alipay.sofa.common.space.SpaceId;

import java.net.URL;

/**
 * Created by kevin.luy@alipay.com on 16/9/14.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public class LoggerSpaceFactory4Log4j2Builder extends AbstractLoggerSpaceFactoryBuilder {

    public LoggerSpaceFactory4Log4j2Builder(SpaceId spaceId, LogSpace logSpace) {
        super(spaceId, logSpace);
    }

    @Override
    protected String getLoggingToolName() {
        return "log4j2";
    }

    @Override
    public AbstractLoggerSpaceFactory doBuild(String spaceName, ClassLoader spaceClassloader,
                                              URL url) {
        try {
            return new Log4j2LoggerSpaceFactory(getSpaceId(), getProperties(), url,
                getLoggingToolName());
        } catch (Throwable e) {
            throw new IllegalStateException("Log4j2 loggerSpaceFactory build error!", e);
        }
    }

}
