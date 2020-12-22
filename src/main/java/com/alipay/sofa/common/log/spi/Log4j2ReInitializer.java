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
package com.alipay.sofa.common.log.spi;

import com.alipay.sofa.common.space.SpaceId;
import org.apache.logging.log4j.core.LoggerContext;

import java.net.URL;
import java.util.Properties;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
@Deprecated
public interface Log4j2ReInitializer {
    void reInitialize(SpaceId spaceId, LoggerContext loggerContext, Properties properties,
                      URL confFile);
}
