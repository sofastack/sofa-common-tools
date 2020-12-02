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
package com.alipay.sofa.common.config;

import com.alipay.sofa.common.config.listener.ConfigListener;
import com.alipay.sofa.common.config.listener.LogConfigListener;
import com.alipay.sofa.common.config.source.ConfigSource;
import com.alipay.sofa.common.utils.Ordered;
import com.alipay.sofa.common.utils.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.alipay.sofa.common.configs.CommonToolsConfig.COMMON_THREAD_LOG_PERIOD;

/**
 * @author zhaowang
 * @version : CommonConfigTest.java, v 0.1 2020年10月21日 3:24 下午 zhaowang Exp $
 */
public class CommonConfigTest {

    @Test
    public void commonConfigTest() {
        System.setProperty(COMMON_THREAD_LOG_PERIOD.getKey(), "1000");
        Long config = SofaCommonConfig.getOrDefault(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(1000L, config.longValue());

        System.setProperty(COMMON_THREAD_LOG_PERIOD.getKey(), "");
        config = SofaCommonConfig.getOrDefault(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(10L, config.longValue());

        System.setProperty(COMMON_THREAD_LOG_PERIOD.getAlias()[0], "8");
        config = SofaCommonConfig.getOrDefault(COMMON_THREAD_LOG_PERIOD);
        Assert.assertEquals(8L, config.longValue());

    }

    @Test
    public void TestEnvConfigSource() {
        Map<String, String> envs = System.getenv();
        for (Map.Entry<String, String> entry : envs.entrySet()) {
            SofaConfig<String> key = buildKey(entry.getKey());
            String value = SofaCommonConfig.getOrDefault(key);
            Assert.assertTrue(StringUtil.isNotBlank(value));
        }
    }

    @Test
    public void TestEnvPropertiesOrder() {
        Map<String, String> envs = System.getenv();
        Map.Entry<String, String> next = envs.entrySet().iterator().next();
        String key = next.getKey();
        SofaConfig<String> sofaConfig = buildKey(key);
        Assert.assertTrue(StringUtil.isNotBlank(SofaCommonConfig.getOrDefault(sofaConfig)));

        String replaceValue = "ABCD";
        Assert.assertNotEquals(replaceValue, SofaCommonConfig.getOrDefault(sofaConfig));
        System.setProperty(key, replaceValue);
        Assert.assertEquals(replaceValue, SofaCommonConfig.getOrDefault(sofaConfig));

        System.clearProperty(key);

        Assert.assertTrue(StringUtil.isNotBlank(SofaCommonConfig.getOrDefault(sofaConfig)));
    }

    public SofaConfig<String> buildKey(String key) {
        return new SofaConfig<>(key, null, "", false, "");
    }

    @Test
    public void testConfigSourceOrder() {
        InnerSofaCommonConfig config = new InnerSofaCommonConfig();

        config.addConfigSource(new OrderConfigSource(1));
        config.addConfigSource(new OrderConfigSource(2));
        config.addConfigSource(new OrderConfigSource(5));
        config.addConfigSource(new OrderConfigSource(4));
        config.addConfigSource(new OrderConfigSource(3));
        config.addConfigSource(new OrderConfigSource(-3));
        config.addConfigSource(new OrderConfigSource(-2));
        config.addConfigSource(new OrderConfigSource(-4));

        List<ConfigSource> configSources = config.getConfigSources();

        int higher = Ordered.HIGHEST_PRECEDENCE;
        for (ConfigSource configSource : configSources) {
            int order = configSource.getOrder();
            Assert.assertTrue(order >= higher);
            higher = order;
        }
    }

    @Test
    public void testListenerSourceOrder() {
        InnerSofaCommonConfig config = new InnerSofaCommonConfig();

        config.addConfigListener(new OrderConfigListener(1));
        config.addConfigListener(new OrderConfigListener(2));
        config.addConfigListener(new OrderConfigListener(5));
        config.addConfigListener(new OrderConfigListener(4));
        config.addConfigListener(new OrderConfigListener(3));
        config.addConfigListener(new OrderConfigListener(-3));
        config.addConfigListener(new OrderConfigListener(-2));
        config.addConfigListener(new OrderConfigListener(-4));

        List<ConfigListener> listeners = config.getConfigListeners();

        int higher = Ordered.HIGHEST_PRECEDENCE;
        for (ConfigListener listener : listeners) {
            int order = listener.getOrder();
            Assert.assertTrue(order >= higher);
            higher = order;
        }
    }

    @Test
    public void testLogListenerOrder() {
        InnerSofaCommonConfig config = new InnerSofaCommonConfig();

        config.addConfigListener(new OrderConfigListener(1));
        config.addConfigListener(new OrderConfigListener(2));
        config.addConfigListener(new OrderConfigListener(5));
        config.addConfigListener(new OrderConfigListener(4));
        config.addConfigListener(new OrderConfigListener(3));
        config.addConfigListener(new LogConfigListener());
        config.addConfigListener(new OrderConfigListener(-3));
        config.addConfigListener(new OrderConfigListener(-2));
        config.addConfigListener(new OrderConfigListener(-4));

        List<ConfigListener> configListeners = config.getConfigListeners();
        ConfigListener listener = configListeners.get(configListeners.size() - 1);
        Assert.assertEquals(Ordered.LOWEST_PRECEDENCE, listener.getOrder());

    }

    static class OrderConfigSource extends AbstractConfigSource {

        private int order;

        public OrderConfigSource(int order) {
            this.order = order;
        }

        @Override
        public String doGetConfig(String key) {
            return null;
        }

        @Override
        public boolean hasKey(String key) {
            return false;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getOrder() {
            return order;
        }
    }

    static class OrderConfigListener implements ConfigListener {

        private int order;

        public OrderConfigListener(int order) {
            this.order = order;
        }

        @Override
        public void onLoadedConfig(SofaConfig key, ConfigSource configSource,
                                   List<ConfigSource> configSourceList) {

        }

        @Override
        public int getOrder() {
            return order;
        }
    }

}