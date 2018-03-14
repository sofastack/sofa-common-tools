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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Created by kevin.luy@alipay.com on 2016/12/1.
 */
public class TemporaryLoggerProxyTest {
    static String  flag = "";
    static boolean init = false;

    @Test
    public void testTemporaryLoggerWork() {

        TemporaryILoggerFactory t = new TemporaryILoggerFactoryTest("11", this.getClass()
            .getClassLoader(), new LoggerTest("xx"));

        Logger logger = t.getLogger("xx");

        Assert.assertEquals("", flag);
        logger.trace("");//loggerTest xx
        Assert.assertEquals("xx", flag);

        //init space
        init = true;
        logger.trace(""); //loggerTest yy
        Assert.assertEquals("yy", flag);

    }

    static class TemporaryILoggerFactoryTest extends TemporaryILoggerFactory {

        public TemporaryILoggerFactoryTest(String space, ClassLoader spaceClassLoader,
                                           Logger tempLogger) {
            super(space, spaceClassLoader, tempLogger);
        }

        @Override
        protected LoggerSelector buildLoggerSelector() {

            class LoggerSelectorTest extends LoggerSelector {

                @Override
                protected Logger getLoggerBySpace(String name) {
                    return new LoggerTest("yy");
                }

                @Override
                protected boolean isSpaceInitialized() {
                    return init;
                }
            }
            ;

            return new LoggerSelectorTest();
        }

    }

    static class LoggerTest implements Logger {

        private String name;

        public LoggerTest(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        @Override
        public void trace(String msg) {
            flag = name;
        }

        @Override
        public void trace(String format, Object arg) {

        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(String format, Object... arguments) {

        }

        @Override
        public void trace(String msg, Throwable t) {

        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return false;
        }

        @Override
        public void trace(Marker marker, String msg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {

        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String msg) {

        }

        @Override
        public void debug(String format, Object arg) {

        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(String format, Object... arguments) {

        }

        @Override
        public void debug(String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return false;
        }

        @Override
        public void debug(Marker marker, String msg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String msg) {

        }

        @Override
        public void info(String format, Object arg) {

        }

        @Override
        public void info(String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(String format, Object... arguments) {

        }

        @Override
        public void info(String msg, Throwable t) {

        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return false;
        }

        @Override
        public void info(Marker marker, String msg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String msg) {

        }

        @Override
        public void warn(String format, Object arg) {

        }

        @Override
        public void warn(String format, Object... arguments) {

        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return false;
        }

        @Override
        public void warn(Marker marker, String msg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String msg) {

        }

        @Override
        public void error(String format, Object arg) {

        }

        @Override
        public void error(String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(String format, Object... arguments) {

        }

        @Override
        public void error(String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return false;
        }

        @Override
        public void error(Marker marker, String msg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {

        }
    }

}
