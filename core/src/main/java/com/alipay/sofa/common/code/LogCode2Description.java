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
package com.alipay.sofa.common.code;

import com.alipay.sofa.common.space.SpaceId;
import com.alipay.sofa.common.utils.ReportUtil;
import com.alipay.sofa.common.utils.StringUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/7
 */
public class LogCode2Description {
    private static final Map<SpaceId, LogCode2Description> LOG_CODE_2_DESCRIPTION_MAP = new ConcurrentHashMap<>();

    public static String convert(String spaceName, String code) {
        return convert(SpaceId.withSpaceName(spaceName), code);
    }

    public static String convert(SpaceId spaceId, String code) {
        LogCode2Description logCode2Description = null;
        if (isCodeSpaceInitialized(spaceId)) {
            logCode2Description = LOG_CODE_2_DESCRIPTION_MAP.get(spaceId);
        } else {
            logCode2Description = create(spaceId);
        }

        return logCode2Description.convert(code);
    }

    public static LogCode2Description create(String spaceName) {
        return create(SpaceId.withSpaceName(spaceName));
    }

    public static LogCode2Description create(SpaceId spaceId) {
        if (isCodeSpaceInitialized(spaceId)) {
            ReportUtil.reportWarn("Code space: \"" + spaceId.getSpaceName()
                                  + "\" is already initialized!");
            return LOG_CODE_2_DESCRIPTION_MAP.get(spaceId);
        }

        synchronized (spaceId) {
            if (isCodeSpaceInitialized(spaceId)) {
                ReportUtil.reportWarn("Code space: \"" + spaceId.getSpaceName()
                                      + "\" is already initialized!");
                return LOG_CODE_2_DESCRIPTION_MAP.get(spaceId);
            }
            LogCode2Description logCode2Description = doCreate(spaceId);
            ReportUtil.reportInfo("Code Space: \"" + spaceId.getSpaceName() + "\" init ok");
            return logCode2Description;
        }
    }

    private static LogCode2Description doCreate(SpaceId spaceId) {
        LogCode2Description logCode2Description = new LogCode2Description(spaceId);
        LOG_CODE_2_DESCRIPTION_MAP.put(spaceId, logCode2Description);
        return logCode2Description;
    }

    private static boolean isCodeSpaceInitialized(SpaceId spaceId) {
        return LOG_CODE_2_DESCRIPTION_MAP.containsKey(spaceId);
    }

    public static void removeCodeSpace(String spaceName) {
        removeCodeSpace(SpaceId.withSpaceName(spaceName));
    }

    public static void removeCodeSpace(SpaceId spaceId) {
        if (spaceId == null || !isCodeSpaceInitialized(spaceId)) {
            return;
        }

        LOG_CODE_2_DESCRIPTION_MAP.remove(spaceId);
    }

    private String     logFormat;
    private Properties properties;

    private LogCode2Description(SpaceId spaceId) {
        logFormat = spaceId.getSpaceName().toUpperCase() + "-%s: %s";
        String prefix = spaceId.getSpaceName().replace(".", "/") + "/log-codes";
        String encoding = Locale.getDefault().toString();
        if (StringUtil.isEmpty(encoding)) {
            encoding = Locale.ENGLISH.toString();
        }
        String fileName = prefix + "_" + encoding + ".properties";
        if (this.getClass().getClassLoader().getResource(fileName) == null) {
            fileName = prefix + ".properties";
        }

        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            properties = new Properties();

            if (in == null) {
                ReportUtil.reportError(String.format("Code file for CodeSpace \"%s\" doesn't exist!", spaceId.getSpaceName()));
            } else {
                InputStreamReader reader = new InputStreamReader(in);
                properties.load(reader);
            }
        } catch (Throwable e) {
            ReportUtil.reportError(String.format("Code space \"%s\" initializing failed!", spaceId.getSpaceName()), e);
        }
    }

    public String convert(String code) {
        Object description = properties.get(code);
        if (description == null) {
            description = "Unknown Code";
        }

        return String.format(logFormat, code, description);
    }
}
