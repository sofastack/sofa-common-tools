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

import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;
import com.alipay.sofa.common.log.factory.LoggerSpaceFactory4Log4j2Builder;
import com.alipay.sofa.common.space.SpaceId;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Log4j2ConsoleLoggingTest {
    @Test
    public void testConsoleLogLevel() throws Exception {
        String loggerName = "com.foo.bar.console";
        String spaceName = "sofa.console";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Flip the global console logging switch on and set the level to WARN
        LogSpace spaceInfo = new LogSpace()
            .setProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true")
            .setProperty(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "WARN")
            .putAll(LogEnvUtils.processGlobalSystemLogProperties());

        LoggerSpaceFactory4Log4j2Builder loggerSpaceFactory4Log4j2Builder = new LoggerSpaceFactory4Log4j2Builder(
            new SpaceId(spaceName), spaceInfo);
        AbstractLoggerSpaceFactory loggerSpaceFactory = loggerSpaceFactory4Log4j2Builder.build(
            spaceName, this.getClass().getClassLoader());
        CommonLoggingConfigurations.appendConsoleLoggerName(loggerName);

        Logger logger = loggerSpaceFactory.getLogger(loggerName);
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertFalse(logger.isDebugEnabled());
        Assert.assertFalse(logger.isTraceEnabled());

        String traceLog = "test trace info";
        String debugLog = "test debug info";
        String infoLog = "test info info";
        String warnLog = "test warn info";
        String errorLog = "test error info";
        logger.trace(traceLog);
        logger.debug(debugLog);
        logger.info(infoLog);
        logger.warn(warnLog);
        logger.error(errorLog);

        String logString = outContent.toString();
        Assert.assertTrue(logString.contains(warnLog));
        Assert.assertTrue(logString.contains(errorLog));
        Assert.assertFalse(logString.contains(traceLog));
        Assert.assertFalse(logString.contains(debugLog));
        Assert.assertFalse(logString.contains(infoLog));
    }
}
