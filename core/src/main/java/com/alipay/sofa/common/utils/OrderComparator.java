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
package com.alipay.sofa.common.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhaowang
 * @version : OrderComparator.java, v 0.1 2020年10月23日 10:11 上午 zhaowang Exp $
 */
public class OrderComparator implements Comparator<Ordered> {
    public static final OrderComparator INSTANCE = new OrderComparator();

    public OrderComparator() {
    }

    public int compare(Ordered o1, Ordered o2) {
        return this.doCompare(o1, o2);
    }

    private int doCompare(Ordered o1, Ordered o2) {
        int i1 = o1.getOrder();
        int i2 = o2.getOrder();
        return Integer.compare(i1, i2);
    }

    public static void sort(List<? extends Ordered> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Ordered[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }

    }
}