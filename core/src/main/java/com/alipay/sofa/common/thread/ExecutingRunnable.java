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
 * The wrapper to the {@link Runnable} to save it's execute {@link Thread}
 * @author huzijie
 * @version ExecutingRunnable.java, v 0.1 2020年10月26日 4:22 下午 huzijie Exp $
 */
class ExecutingRunnable {
    public Runnable r;
    public Thread   t;

    public ExecutingRunnable(Runnable r, Thread t) {
        this.r = r;
        this.t = t;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ExecutingRunnable) {
            ExecutingRunnable er = (ExecutingRunnable) obj;
            return this.t == er.t && this.r == er.r;
        }
        return false;
    }

    @Override
    public String toString() {
        return r.toString() + t.toString();
    }
}