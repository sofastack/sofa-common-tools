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
package com.alipay.sofa.common.tracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * use custom {@link TracerIdRetriever} to find trace id
 * @author huzijie
 * @version TracerAdapter.java, v 0.1 2020年11月11日 5:29 下午 huzijie Exp $
 */
public class TracerIdAdapter {

    private static final Logger          logger   = LoggerFactory.getLogger(TracerIdAdapter.class);

    private static final TracerIdAdapter INSTANCE = new TracerIdAdapter();

    private TracerIdRetriever            tracerIdRetriever;

    private TracerIdAdapter() {
        ServiceLoader<TracerIdRetriever> serviceLoader = ServiceLoader
            .load(TracerIdRetriever.class);
        Iterator<TracerIdRetriever> tracerIdAdapterIterator = serviceLoader.iterator();
        if (tracerIdAdapterIterator.hasNext()) {
            this.tracerIdRetriever = tracerIdAdapterIterator.next();
            logger.info("TracerIdConverter use tracerIdAdapter '{}'", tracerIdRetriever.getClass()
                .getName());
        } else {
            logger.info("TracerIdConverter can not find any tracerIdAdapter");
        }
    }

    public static TracerIdAdapter getInstance() {
        return INSTANCE;
    }

    public String traceIdSafari(Thread t) {
        if (tracerIdRetriever == null) {
            return null;
        }
        try {
            return tracerIdRetriever.getTracerId(t);
        } catch (Exception e) {
            //ignore
            return null;
        }
    }
}
