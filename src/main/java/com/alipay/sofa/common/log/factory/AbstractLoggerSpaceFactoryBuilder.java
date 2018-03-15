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

import com.alipay.sofa.common.utils.AssertUtil;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.SpaceInfo;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * Created by kevin.luy@alipay.com on 16/9/22.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public abstract class AbstractLoggerSpaceFactoryBuilder implements LoggerSpaceFactoryBuilder {
    private static final Logger logger = LoggerFactory
                                           .getLogger(AbstractLoggerSpaceFactoryBuilder.class);
    private SpaceInfo           spaceInfo;

    public AbstractLoggerSpaceFactoryBuilder(SpaceInfo space) {
        AssertUtil.notNull(space);
        this.spaceInfo = space;
    }

    @Override
    public AbstractLoggerSpaceFactory build(String spaceName, ClassLoader spaceClassloader) {
        AssertUtil.hasText(spaceName);
        AssertUtil.notNull(spaceClassloader);

        //load config file
        URL configFileUrl = getSpaceLogConfigFileURL(spaceClassloader, spaceName);

        // set default logging.level
        specifySpaceLogConfigProperites(spaceName);

        return doBuild(spaceName, spaceClassloader, configFileUrl);

    }

    private URL getSpaceLogConfigFileURL(ClassLoader spaceClassloader, String spaceName) {
        String suffix = LogEnvUtils.getLogConfEnvSuffix(spaceName);

        //TODO avoid this pattern "log-conf.xml.console"
        String logConfigLocation = spaceName.replace('.', '/') + "/" + LOG_DIRECTORY + "/"
                                   + getLoggingToolName() + "/" + LOG_XML_CONFIG_FILE_NAME + suffix;
        URL configFileUrl = spaceClassloader.getResource(logConfigLocation);

        //recommend this pattern "log-conf-console.xml"
        if (configFileUrl == null && suffix != null && !suffix.isEmpty()) {
            //try again with another env profile file pattern;
            logConfigLocation = spaceName.replace('.', '/')
                                + "/"
                                + LOG_DIRECTORY
                                + "/"
                                + getLoggingToolName()
                                + "/"
                                + String.format(LOG_XML_CONFIG_FILE_ENV_PATTERN,
                                    suffix.substring(1));
            configFileUrl = spaceClassloader.getResource(logConfigLocation);
        }
        AssertUtil.state(configFileUrl != null, this + " build error: No " + getLoggingToolName()
                                                + " config file (" + configFileUrl + ") found!");
        return configFileUrl;
    }

    private void specifySpaceLogConfigProperites(String spaceName) {
        //如果system.properties 与 properites 都含有某分配置，那么以 system.properties 为准，同时WARN警告，properties中重复定义会被抛弃；
        Iterator<Map.Entry<Object, Object>> iterator = spaceInfo.properties().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            if (System.getProperties().containsKey(entry.getKey())) {
                iterator.remove();
                logger.warn(
                    "Props key {}  is also already existed in System.getProps ({}:{}),so use it!",
                    entry.getKey(), entry.getKey(), System.getProperty((String) entry.getKey()));
            }
        }

        /**
         * == 1.space's logger path
         */
        String loggingPathKey = LOG_PATH_PREFIX + spaceName;
        if (System.getProperty(loggingPathKey) == null && System.getProperty(LOG_PATH) != null
            && spaceInfo.properties().getProperty(loggingPathKey) == null) {
            spaceInfo.properties().setProperty(loggingPathKey, System.getProperty(LOG_PATH));
        }

        /**
         * == 2.space's logger level
         */
        String loggingLevelKey = LOG_LEVEL_PREFIX + spaceName;
        if (System.getProperty(loggingLevelKey) == null
            && spaceInfo.properties().getProperty(loggingLevelKey) == null) {
            spaceInfo.properties().setProperty(loggingLevelKey,
                Constants.DEFAULT_MIDDLEWARE_SPACE_LOG_LEVEL);
        }

    }

    protected abstract AbstractLoggerSpaceFactory doBuild(String spaceName,
                                                          ClassLoader spaceClassloader,
                                                          URL confFileUrl);

    protected abstract String getLoggingToolName();

    protected Properties getProperties() {
        return spaceInfo.properties();
    }
}
