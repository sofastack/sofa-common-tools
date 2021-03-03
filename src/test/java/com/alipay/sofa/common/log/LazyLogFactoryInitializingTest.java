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
package com.alipay.sofa.common.log;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/3/3
 */
public class LazyLogFactoryInitializingTest {
    public static final String SPACE_NAME = "lazy.init";

    static {
        System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    public static Logger       logger     = MultiAppLoggerSpaceManager.getLoggerBySpace(
                                              "LAZY-INIT", SPACE_NAME);

    @Test
    public void test() throws Exception {
        logger.info("test1");
        logger.info("test2");
        logger.info("test3");
        logger.info("test4");

        File directory = new File(Constants.LOGGING_PATH_DEFAULT + "/lazy");
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith("monitor.log")) {
                        Assert.fail();
                    }
                }
            }
        }

        Map<String, String> props = new HashMap<>();
        props.put("logging.path." + SPACE_NAME, "./logs/");
        props.put("logging.level." + SPACE_NAME, "INFO");
        props.put("date.pattern." + SPACE_NAME, "yyyy-MM-dd");
        MultiAppLoggerSpaceManager.init(SPACE_NAME, props);

        try {
            Files.write(Paths.get("./logs/lazy/monitor.log"), "".getBytes(StandardCharsets.UTF_8));
        } catch (Throwable e) {
            // just ignore
        }
        logger.info("test1");
        logger.info("test2");
        logger.info("test3");
        logger.info("test4");

        List<String> lines = Files.readAllLines(Paths.get("./logs/lazy/monitor.log"), StandardCharsets.UTF_8);
        int count = 0;
        for (String line : lines) {
            if (line.contains("LAZY-INIT - test")) {
                ++count;
            }
        }
        Assert.assertTrue(count >= 4);
    }
}
