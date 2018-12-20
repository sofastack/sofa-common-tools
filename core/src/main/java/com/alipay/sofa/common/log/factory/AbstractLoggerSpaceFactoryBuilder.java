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
import java.util.*;

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

    public AbstractLoggerSpaceFactoryBuilder(SpaceId spaceId, SpaceInfo space) {
        AssertUtil.notNull(space);
        AssertUtil.notNull(spaceId);
        this.spaceId = spaceId;
        this.spaceInfo = space;
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

    private URL getSpaceLogConfigFileURL(ClassLoader spaceClassloader, String spaceName) {
        String suffix = LogEnvUtils.getLogConfEnvSuffix(spaceName);

        //TODO avoid this pattern "log-conf.xml.console"
        String logConfigLocation = spaceName.replace('.', '/') + "/" + LOG_DIRECTORY + "/"
                                   + getLoggingToolName() + "/" + LOG_XML_CONFIG_FILE_NAME + suffix;

        String configProperyConfigLocation = spaceName.replace('.', '/') + "/" + LOG_DIRECTORY
                                             + "/" + getLoggingToolName() + "/"
                                             + LOG_CONFIG_PROPERTIES + suffix;

        URL configFileUrl = null;

        try {

            //拿到 log
            List<URL> logConfigFileUrls = new ArrayList<URL>();
            Enumeration<URL> logUrls = spaceClassloader.getResources(logConfigLocation);
            // 可能存在多个文件。
            if (logUrls != null) {
                while (logUrls.hasMoreElements()) {
                    // 读取一个文件
                    URL url = logUrls.nextElement();
                    logConfigFileUrls.add(url);
                }
            }

            //拿到配置文件
            List<URL> configPropertyFileUrls = new ArrayList<URL>();
            Enumeration<URL> configUrls = spaceClassloader
                .getResources(configProperyConfigLocation);
            // 可能存在多个文件。
            if (configUrls != null) {
                while (configUrls.hasMoreElements()) {
                    // 读取一个文件
                    URL url = configUrls.nextElement();
                    configPropertyFileUrls.add(url);
                }
            }

            configFileUrl = getResource(spaceClassloader, logConfigFileUrls, configPropertyFileUrls);

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
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error when get resources of " + spaceName + " from classpath", e);
            }
        }

        // 是否配置 logging.config.spaceName，注意如果在 Spring Boot 环境使用且是 log4j2 文件
        // 配置文件路径后缀必须是 log4j2/log-conf-custom.xml
        String loggingConfig = System.getProperty(String.format(LOGGING_CONFIG_PATH, spaceName));
        if (!StringUtil.isBlank(loggingConfig)) {
            configFileUrl = spaceClassloader.getResource(loggingConfig);
        }

        AssertUtil.state(configFileUrl != null, this + " build error: No " + getLoggingToolName()
                                                + " config file (" + configFileUrl + ") found!");
        return configFileUrl;
    }

    protected URL getResource(ClassLoader spaceClassloader, List<URL> logConfigFileUrls,
                              List<URL> configPropertyFileUrls) throws IOException {
        if (logConfigFileUrls == null || logConfigFileUrls.isEmpty()) {
            return null;
        } else if (logConfigFileUrls.size() == 1) {
            return logConfigFileUrls.get(0);
        } else {
            List<ConfigFile> configFiles = new ArrayList<ConfigFile>();
            for (URL logConfigUrl : logConfigFileUrls) {
                int priority = DEFAULT_PRIORITY;

                if (configPropertyFileUrls != null) {
                    for (URL configPropertyUrl : configPropertyFileUrls) {
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
                                String priorityStr = properties.getProperty("priority");
                                if (priorityStr != null) {
                                    priority = Integer.parseInt(priorityStr);
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
            Collections.sort(configFiles, new Comparator<ConfigFile>() {
                @Override
                public int compare(ConfigFile o1, ConfigFile o2) {
                    // 越大越前面
                    return o2.priority - o1.priority;
                }
            });
            return configFiles.get(0).url;
        }
    }

    private void specifySpaceLogConfigProperties(String spaceName) {
        /*
         * == 1.space's logger path
         */
        String loggingPathKey = LOG_PATH_PREFIX + spaceName;
        String defaultLoggingPath = spaceInfo.getProperty(LOG_PATH);
        if (spaceInfo.getProperty(loggingPathKey) == null) {
            spaceInfo.setProperty(IS_DEFAULT_LOG_PATH, Boolean.TRUE.toString());
            spaceInfo.setProperty(loggingPathKey, defaultLoggingPath);
        }

        /*
         * == 2.space's logger level
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
