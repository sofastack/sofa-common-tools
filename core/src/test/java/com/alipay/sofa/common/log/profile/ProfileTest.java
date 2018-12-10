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
package com.alipay.sofa.common.log.profile;

import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.profile.diagnostic.Profiler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author luoguimu123
 * @version $Id: ProfileTest.java, v 0.1 2017年08月09日 下午3:12 luoguimu123 Exp $
 */
public class ProfileTest {

    public static final String RPC_LOG_SPACE = "com.alipay.sofa.rpc.app";

    @Test
    public void testProfile() throws FileNotFoundException {

        String userHome = "./logs";
        String appName1 = "profile";
        File logFile = new File(userHome + File.separator + appName1 + File.separator
                                + "common-default.log");

        SpaceId spaceId1 = new SpaceId(RPC_LOG_SPACE);
        spaceId1.withTag("logging.test.path", userHome);
        spaceId1.withTag("appname", appName1);
        Map<String, String> properties1 = new HashMap<String, String>();
        properties1.put("logging.test.path", userHome);
        properties1.put("appname", appName1);

        Logger logger = LoggerSpaceManager
            .getLoggerBySpace("com.alipay.rpc", spaceId1, properties1);

        Profiler.reset();
        Profiler.start("line1");
        Profiler.enter("line2");
        Profiler.release();
        Profiler.enter("line3");

        Profiler.release();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Profiler.release();
        if (Profiler.getDuration() > 3000) {
            logger.info(Profiler.dump());

        }

        Profiler.reset();

        Scanner scanner = new Scanner(new BufferedReader(new FileReader(logFile)));
        List<String> list = new ArrayList<String>();
        while (scanner.hasNext()) {
            list.add(scanner.nextLine());
        }
        Assert.assertTrue(list.size() >= 3);
        Assert.assertTrue(list.get(list.size() - 1).contains("line3"));
        Assert.assertTrue(list.get(list.size() - 2).contains("line2"));
        Assert.assertTrue(list.get(list.size() - 3).contains("line1"));

    }

    @Test
    public void testProfilerNotPrint() throws FileNotFoundException {

        String userHome = "./logs";
        String appName1 = "profile1";
        File logFile = new File(userHome + File.separator + appName1 + File.separator
                                + "common-default.log");

        SpaceId spaceId1 = new SpaceId(RPC_LOG_SPACE);
        spaceId1.withTag("logging.test.path", userHome);
        spaceId1.withTag("appname", appName1);
        Map<String, String> properties1 = new HashMap<String, String>();
        properties1.put("logging.test.path", userHome);
        properties1.put("appname", appName1);

        Logger logger = LoggerSpaceManager
            .getLoggerBySpace("com.alipay.rpc", spaceId1, properties1);

        Profiler.reset();
        Profiler.start("line1");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Profiler.release();
        if (Profiler.getDuration() > 8000) {
            logger.info(Profiler.dump());

        }

        Profiler.reset();

        Scanner scanner = new Scanner(new BufferedReader(new FileReader(logFile)));
        while (scanner.hasNext()) {
            Assert.assertFalse(scanner.nextLine().contains("line1"));
        }

    }

}