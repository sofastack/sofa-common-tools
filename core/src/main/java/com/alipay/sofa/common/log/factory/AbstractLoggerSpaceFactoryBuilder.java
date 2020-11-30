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

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.SpaceInfo;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.utils.AssertUtil;
import com.alipay.sofa.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * Created by kevin.luy@alipay.com on 16/9/22.
 * Updated by guanchao.ygc@alibaba-inc.com on 14/04/28.
 */
public abstract class AbstractLoggerSpaceFactoryBuilder implements LoggerSpaceFactoryBuilder {
    private static final Logger logger = LoggerFactory
                                           .getLogger(AbstractLoggerSpaceFactoryBuilder.class);
    private SpaceId             spaceId;
    private SpaceInfo           spaceInfo;

    private String              spaceDirectoryPrefix;

    public AbstractLoggerSpaceFactoryBuilder(SpaceId spaceId, SpaceInfo space) {
        AssertUtil.notNull(space);
        AssertUtil.notNull(spaceId);
        this.spaceId = spaceId;
        this.spaceInfo = space;
        spaceDirectoryPrefix = spaceId.getSpaceName().replace('.', '/') + "/" + LOG_DIRECTORY + "/"
                               + getLoggingToolName() + "/";
    }

    @Override
    public AbstractLoggerSpaceFactory build(String spaceName, ClassLoader spaceClassloader) {
        AssertUtil.hasText(spaceName);
        AssertUtil.notNull(spaceClassloader);
        // load config file
        URL configFileUrl = getSpaceLogConfigFileURL(spaceClassloader, spaceName);
        // set default logging.level and logging.path
        specifySpaceLogConfigProperties(spaceName);
        return doBuild(spaceName, spaceClassloader, configFileUrl);
    }

    private List<URL> getResources(ClassLoader classLoader, String path) {
        List<URL> rtn = new ArrayList<>();
        try {
            Enumeration<URL> allUrls = classLoader.getResources(path);
            if (allUrls != null) {
                while (allUrls.hasMoreElements()) {
                    rtn.add(allUrls.nextElement());
                }
            }
        } catch (IOException e) {
            logger.warn("Error when get resources of {} from classpath", path, e);
            return null;
        }
        return rtn;
    }

    protected URL getSpaceLogConfigFileURL(ClassLoader spaceClassloader, String spaceName) {
        /*
         * customize log config file like logging.path.config.{spaceId}. it can be
         * configured via VM option or spring boot config file. Notice that when
         * configured via VM option and use log4j2, the configure file path must
         * end with log4j2/log-conf-custom.xml.
         */
        String loggingConfig = spaceInfo.getProperty(String.format(Constants.LOGGING_CONFIG_PATH,
            spaceId.getSpaceName()));
        if (StringUtil.isNotEmpty(loggingConfig)) {
            return spaceClassloader.getResource(loggingConfig);
        }

        String suffix = LogEnvUtils.getLogConfEnvSuffix(spaceName);

        //TODO avoid this pattern "log-conf.xml.console"
        String logConfigLocation = spaceDirectoryPrefix + LOG_XML_CONFIG_FILE_NAME + suffix;

        String logConfigPropertyLocation = spaceDirectoryPrefix + LOG_CONFIG_PROPERTIES + suffix;

        URL configFileUrl = null;
        List<URL> logConfigFileUrls = getResources(spaceClassloader, logConfigLocation);
        List<URL> logConfigPropertyFileUrls = getResources(spaceClassloader,
            logConfigPropertyLocation);
        try {
            configFileUrl = getResourceByPriority(logConfigFileUrls, logConfigPropertyFileUrls);

            //recommend this pattern "log-conf-console.xml"
            if (configFileUrl == null && StringUtil.isNotEmpty(suffix)) {
                //try again with another env profile file pattern;
                logConfigLocation = spaceDirectoryPrefix
                                    + String.format(LOG_XML_CONFIG_FILE_ENV_PATTERN,
                                        suffix.substring(1));
                configFileUrl = spaceClassloader.getResource(logConfigLocation);
            }
        } catch (Exception e) {
            logger.warn("Error when get resources of {} from classpath", spaceName, e);
        }

        AssertUtil.state(configFileUrl != null, this + " build error: No " + getLoggingToolName()
                                                + " config file (" + configFileUrl + ") found!");
        return configFileUrl;
    }

    // Use getResourceByPriority instead
    @Deprecated
    protected URL getResource(ClassLoader spaceClassloader, List<URL> logConfigFileUrls,
                              List<URL> configPropertyFileUrls) throws IOException {
        return getResourceByPriority(logConfigFileUrls, configPropertyFileUrls);
    }

    protected URL getResourceByPriority(List<URL> logConfigFileUrls, List<URL> logConfigPropertyFileUrls) throws IOException {
        if (logConfigFileUrls == null || logConfigFileUrls.isEmpty()) {
            return null;
        } else {
            List<ConfigFile> configFiles = new ArrayList<>();
            for (URL logConfigUrl : logConfigFileUrls) {
                int priority = DEFAULT_PRIORITY;

                if (logConfigPropertyFileUrls != null) {
                    for (URL configPropertyUrl : logConfigPropertyFileUrls) {
                        final String absoluteConfigPath = new File(configPropertyUrl.getFile())
                            .getParentFile().getAbsolutePath();
                        final String absoluteLogPath = new File(logConfigUrl.getFile())
                            .getParentFile().getAbsolutePath();
                        if (absoluteConfigPath.equals(absoluteLogPath)) {
                            InputStream inputStream = null;
                            try {
                                URLConnection uConn = configPropertyUrl.openConnection();
                                uConn.setUseCaches(false);
                                inputStream = uConn.getInputStream();
                                Properties properties = new Properties();
                                properties.load(inputStream);
                                String priorityStr = properties.getProperty(PRIORITY_KEY);
                                if (StringUtil.isNotEmpty(priorityStr)) {
                                    priority = Integer.parseInt(priorityStr);
                                }

                                String loggerConsoleWhiteSetStr = properties.getProperty(LOGGER_CONSOLE_WHITE_SET_KEY);
                                if (StringUtil.isNotEmpty(loggerConsoleWhiteSetStr)) {
                                    for (String logger: loggerConsoleWhiteSetStr.split(",")) {
                                        CommonLoggingConfigurations.appendConsoleLoggerName(logger);
                                    }
                                }
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            }
                            break;
                        }
                    }
                }

                ConfigFile configFile = new ConfigFile(priority, logConfigUrl);
                if (logger.isDebugEnabled()) {
                    logger.debug("Find url {}, priority is {}", logConfigUrl, priority);
                }
                configFiles.add(configFile);
            }

            // In descending order
            configFiles.sort((o1, o2) -> -Integer.compare(o1.priority, o2.priority));
            return configFiles.get(0).url;
        }
    }

    private void specifySpaceLogConfigProperties(String spaceName) {
        /*
         * 1.space's logger path
         */
        String loggingPathKey = LOG_PATH_PREFIX + spaceName;
        String defaultLoggingPath = spaceInfo.getProperty(LOG_PATH);
        if (spaceInfo.getProperty(loggingPathKey) == null) {
            spaceInfo.setProperty(IS_DEFAULT_LOG_PATH, Boolean.TRUE.toString());
            spaceInfo.setProperty(loggingPathKey, defaultLoggingPath);
        }

        /*
         * 2.space's logger level
         */
        String loggingLevelKey = LOG_LEVEL_PREFIX + spaceName;
        if (spaceInfo.getProperty(loggingLevelKey) == null) {
            spaceInfo.setProperty(IS_DEFAULT_LOG_LEVEL, Boolean.TRUE.toString());
            spaceInfo.setProperty(loggingLevelKey, DEFAULT_MIDDLEWARE_SPACE_LOG_LEVEL);
            for (int i = LOG_LEVEL.length(); i < loggingLevelKey.length(); ++i) {
                if (loggingLevelKey.charAt(i) == '.') {
                    String level = spaceInfo.getProperty(loggingLevelKey.substring(0, i + 1)
                                                         + LOG_START);
                    if (!StringUtil.isBlank(level)) {
                        spaceInfo.setProperty(loggingLevelKey, level);
                    }
                }
            }
        }

    }

    protected abstract AbstractLoggerSpaceFactory doBuild(String spaceName,
                                                          ClassLoader spaceClassloader,
                                                          URL confFileUrl);

    protected abstract String getLoggingToolName();

    protected SpaceId getSpaceId() {
        return spaceId;
    }

    protected Properties getProperties() {
        return spaceInfo.properties();
    }

    private class ConfigFile {
        final int priority;
        final URL url;

        ConfigFile(int priority, URL url) {
            this.priority = priority;
            this.url = url;
        }
    }
}
