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
package com.alipay.sofa.common.log.proxy;

import com.alipay.sofa.common.utils.AssertUtil;
import com.alipay.sofa.common.log.MultiAppLoggerSpaceManager;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;
import org.slf4j.Logger;

/**
 * space
 * Created by kevin.luy@alipay.com on 2016/12/1.
 */
public class TemporaryILoggerFactory extends AbstractLoggerSpaceFactory {

    private final String      space;
    private final ClassLoader spaceClassLoader;
    private final Logger      tempLogger;
    private final SpaceId     spaceId;
    private LoggerSelector    loggerSelector;

    public TemporaryILoggerFactory(String space, ClassLoader spaceClassLoader, Logger tempLogger) {
        this(new SpaceId(space), spaceClassLoader, tempLogger);
    }

    public TemporaryILoggerFactory(SpaceId spaceId, ClassLoader spaceClassLoader, Logger tempLogger) {
        // 这里是常量,常量为什么还要调用super?
        super("temp");
        AssertUtil.notNull(spaceId);
        AssertUtil.notNull(spaceClassLoader);
        AssertUtil.notNull(tempLogger);
        this.space = spaceId.getSpaceName();
        this.spaceId = spaceId;
        this.spaceClassLoader = spaceClassLoader;
        this.tempLogger = tempLogger;
        this.loggerSelector = buildLoggerSelector();
        AssertUtil.notNull(this.loggerSelector);
    }

    protected LoggerSelector buildLoggerSelector() {
        return new LoggerSelector();
    }

    @Override
    public Logger getLogger(String name) {
        return new LoggerProxy(loggerSelector, name);
    }

    /**
     * decide use temp logger or  initialized logger?
     */
    public class LoggerSelector {
        private boolean warned = false;

        public Logger select(String name) {
            //init
            if (!isSpaceInitialized()) {
                //返回临时logger代理类使用NOP_LOGGER_FACTORY，初始化后再代理到实际logger
                if (!warned) {
                    tempLogger.warn(
                        ">>> Logger Space:{} has not be initialized! Use app logger temporary！",
                        space);
                    warned = true;
                }
                return tempLogger;
            }
            if (warned) {
                tempLogger.info("<<< Logger Space:{} was initialized! Use this space logger.",
                    space);
                warned = false;
            }
            return getLoggerBySpace(name);
        }

        protected Logger getLoggerBySpace(String name) {
            return MultiAppLoggerSpaceManager.getLoggerBySpace(name, spaceId, spaceClassLoader);
        }

        protected boolean isSpaceInitialized() {
            return MultiAppLoggerSpaceManager.isSpaceInitialized(spaceId);
        }

    }
}
