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
package com.alipay.sofa.common.config.source;

import com.alipay.sofa.common.utils.StringUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author zhaowang
 * @version : CacheConfigSourceWrapper.java, v 0.1 2021年12月20日 8:19 下午 zhaowang
 */
public class ConfigSourceCacheWrapper extends AbstractConfigSource {

    private AbstractConfigSource         delegate;
    private LoadingCache<String, String> cache;

    public ConfigSourceCacheWrapper(AbstractConfigSource delegate, long expireAfterSecond) {
        this.delegate = delegate;
        this.cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(expireAfterSecond))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    String value = delegate.doGetConfig(key);
                    if (value == null) {
                        return "";
                    } else {
                        return value;
                    }
                }
            });
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String doGetConfig(String key) {
        if (key == null) {
            return null;
        }
        try {
            return cache.get(key);
        } catch (ExecutionException | UncheckedExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasKey(String key) {
        return StringUtil.isNotBlank(doGetConfig(key));
    }

    @Override
    public int getOrder() {
        return delegate.getOrder();
    }
}