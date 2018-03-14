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

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.alipay.sofa.common.log.Constants.*;

/**
 * @author xuanbei
 * @since 2017/07/03
 */
public class LoggerSpaceManagerUsageTest {
    public static final String  RPC_LOG_SPACE = "com.alipay.sofa.rpc.app";

    private static String       userHome      = "./logs";
    private static String       appName1      = "app1";
    private static File         logFile1      = new File(userHome + File.separator + appName1
                                                         + File.separator + "common-default.log");

    private static String       appName2      = "app2";
    private static File         logFile2      = new File(userHome + File.separator + appName2
                                                         + File.separator + "common-default.log");

    private SpaceId             spaceId1;
    private Map<String, String> properties1;

    private SpaceId             spaceId2;
    private Map<String, String> properties2;

    {
        spaceId1 = new SpaceId(RPC_LOG_SPACE);
        spaceId1.withTag("logging.test.path", userHome);
        spaceId1.withTag("appname", appName1);

        properties1 = new HashMap<String, String>();
        properties1.put("logging.test.path", userHome);
        properties1.put("appname", appName1);

        spaceId2 = new SpaceId(RPC_LOG_SPACE);
        spaceId2.withTag("logging.test.path", userHome);
        spaceId2.withTag("appname", appName2);

        properties2 = new HashMap<String, String>();
        properties2.put("logging.test.path", userHome);
        properties2.put("appname", appName2);
    }

    @Test
    public void testEquals() {
        SpaceId tempSpaceId1 = new SpaceId(RPC_LOG_SPACE);
        tempSpaceId1.withTag("logging.test.path", userHome);
        tempSpaceId1.withTag("appname", appName1);
        tempSpaceId1.withTag("abc", "bac");
        tempSpaceId1.withTag("bac", "bac");
        tempSpaceId1.withTag("fgr", "bac");
        tempSpaceId1.withTag("765", "bac");

        SpaceId tempSpaceId2 = new SpaceId(RPC_LOG_SPACE);
        tempSpaceId2.withTag("fgr", "bac");
        tempSpaceId2.withTag("logging.test.path", userHome);
        tempSpaceId2.withTag("765", "bac");
        tempSpaceId2.withTag("abc", "bac");
        tempSpaceId2.withTag("appname", appName1);
        tempSpaceId2.withTag("bac", "bac");

        org.slf4j.Logger logger1 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc",
            tempSpaceId1, properties2);
        org.slf4j.Logger logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc",
            tempSpaceId2, properties2);
        Assert.assertSame(logger1, logger2);
    }

    @Test
    public void testLog4j1() throws IOException, InterruptedException {

        if (System.getProperties().containsKey(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY)) {
            System.getProperties().remove(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        }
        System.setProperty(LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        System.setProperty(LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        System.setProperty(LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");

        String logStr1 = "app1 test log4j1";
        String logStr2 = "app2 test log4j1";

        org.slf4j.Logger logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId1,
            properties1);
        logger.info(logStr1);

        SpaceId tempSpaceId = new SpaceId(RPC_LOG_SPACE);
        tempSpaceId.withTag("appname", appName1);
        tempSpaceId.withTag("logging.test.path", userHome);
        org.slf4j.Logger logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc",
            tempSpaceId, properties1);
        Assert.assertSame(logger, logger2);

        logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        logger.info(logStr2);

        logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        Assert.assertSame(logger, logger2);

        Thread.sleep(3000);

        String content1 = FileUtils.readFileToString(logFile1);
        Assert.assertTrue(content1.contains(logStr1));

        String content2 = FileUtils.readFileToString(logFile2);
        Assert.assertTrue(content2.contains(logStr2));
    }

    @Test
    public void testLog4j2() throws Exception {

        if (System.getProperties().containsKey(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY)) {
            System.getProperties().remove(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        }
        System.setProperty(LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");
        System.setProperty(LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        System.setProperty(LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");

        String logStr1 = "app1 test log4j2";
        String logStr2 = "app2 test log4j2";

        org.slf4j.Logger logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId1,
            properties1);
        logger.info(logStr1);

        SpaceId tempSpaceId = new SpaceId(RPC_LOG_SPACE);
        tempSpaceId.withTag("appname", appName1);
        tempSpaceId.withTag("logging.test.path", userHome);
        org.slf4j.Logger logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc",
            tempSpaceId, properties1);
        Assert.assertSame(logger, logger2);

        logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        logger.info(logStr2);

        logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        Assert.assertSame(logger, logger2);

        Thread.sleep(3000);

        String content1 = FileUtils.readFileToString(logFile1);
        Assert.assertTrue(content1.contains(logStr1));

        String content2 = FileUtils.readFileToString(logFile2);
        Assert.assertTrue(content2.contains(logStr2));
    }

    @Test
    public void testLogBack() throws Exception {

        if (System.getProperties().containsKey(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY)) {
            System.getProperties().remove(SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        }
        System.setProperty(LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        System.setProperty(LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");
        System.setProperty(LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");

        String logStr1 = "app1 test logback";
        String logStr2 = "app2 test logback";

        org.slf4j.Logger logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId1,
            properties1);
        logger.info(logStr1);

        SpaceId tempSpaceId = new SpaceId(RPC_LOG_SPACE);
        tempSpaceId.withTag("appname", appName1);
        tempSpaceId.withTag("logging.test.path", userHome);
        org.slf4j.Logger logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc",
            tempSpaceId, properties1);
        Assert.assertSame(logger, logger2);

        logger = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        logger.info(logStr2);

        logger2 = LoggerSpaceManager.getLoggerBySpace("com.alipay.rpc", spaceId2, properties2);
        Assert.assertSame(logger, logger2);

        Thread.sleep(3000);

        String content1 = FileUtils.readFileToString(logFile1);
        Assert.assertTrue(content1.contains(logStr1));

        String content2 = FileUtils.readFileToString(logFile2);
        Assert.assertTrue(content2.contains(logStr2));
    }
}
