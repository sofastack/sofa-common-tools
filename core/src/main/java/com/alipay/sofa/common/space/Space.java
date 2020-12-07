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
package com.alipay.sofa.common.space;

import com.alipay.sofa.common.code.LogCode2Description;
import com.alipay.sofa.common.log.LogSpace;
import com.alipay.sofa.common.thread.space.ThreadPoolSpace;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class Space {
    private LogSpace            logSpace;
    private ThreadPoolSpace     threadPoolSpace;
    private LogCode2Description logCode2Description;

    public LogSpace getLogSpace() {
        return logSpace;
    }

    public void setLogSpace(LogSpace logSpace) {
        this.logSpace = logSpace;
    }

    public ThreadPoolSpace getThreadPoolSpace() {
        return threadPoolSpace;
    }

    public void initTreadPoolSpace() {
        if (threadPoolSpace == null) {
            synchronized (this) {
                if (threadPoolSpace == null) {
                    this.threadPoolSpace = new ThreadPoolSpace();
                }
            }
        }
    }

    public LogCode2Description getLogCode2Description() {
        return logCode2Description;
    }

    public void setLogCode2Description(LogCode2Description logCode2Description) {
        this.logCode2Description = logCode2Description;
    }
}
