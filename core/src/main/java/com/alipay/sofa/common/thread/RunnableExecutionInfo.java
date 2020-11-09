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
package com.alipay.sofa.common.thread;

/**
 * The execution info of the runnable task
 * @author huzijie
 * @version RunnableExecutionInfo.java, v 0.1 2020年10月26日 4:22 下午 huzijie Exp $
 */
class RunnableExecutionInfo {

    private volatile boolean printed;

    private long             taskKickOffTime;

    public RunnableExecutionInfo() {
        printed = false;
        taskKickOffTime = System.currentTimeMillis();
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public long getTaskKickOffTime() {
        return taskKickOffTime;
    }

    public void setTaskKickOffTime(long taskKickOffTime) {
        this.taskKickOffTime = taskKickOffTime;
    }
}
