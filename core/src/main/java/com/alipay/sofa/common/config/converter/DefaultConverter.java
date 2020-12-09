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
package com.alipay.sofa.common.config.converter;

import com.alipay.sofa.common.config.Converter;
import com.alipay.sofa.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhaowang
 * @version : DefaultConverter.java, v 0.1 2020年12月09日 11:51 上午 zhaowang Exp $
 */
public class DefaultConverter implements Converter {

    @Override
    public <T> T convert(String value, Class<T> targetType) {
        return (T) convertObjectFromString(targetType, value);
    }

    /**
     * TODO 需要考虑java的类型转换。
     *
     *  float 4 字节 32位IEEE 754单精度
     *  double 8 字节 64位IEEE 754双精度
     *  byte 1字节 -128到127
     *  short 2 字节 -32,768到32,767
     *  int 4 字节 -2,147,483,648到2,147,483,647
     *  long 8 字节 -9,223,372,036,854,775,808到9,223,372,036, 854,775,807
     *  char 2 字节 整个Unicode字符集
     *  boolean 1 位 True或者false
     *
     * @param type the type must not be null
     * @param value the value
     * @return object
     */
    public static Object convertObjectFromString(Class<?> type, String value) {
        if (value == null) {
            return null;
        }
        if (type == null) {
            throw new NullPointerException("Type cannot be null");
        }

        if (StringUtil.isBlank(value) && type != String.class) {
            return null;
        }

        if (type == String.class) {
            return value;
        } else if (type == Float.class || type == float.class) {
            return Float.valueOf(value);
        } else if (type == Double.class || type == double.class) {
            return Double.valueOf(value);
        } else if (type == Byte.class || type == byte.class) {
            return Byte.valueOf(value);
        } else if (type == Short.class || type == short.class) {
            return Short.valueOf(value);
        } else if (type == Integer.class || type == int.class) {
            return Integer.valueOf(value);
        } else if (type == Long.class || type == long.class) {
            return Long.valueOf(value);
        } else if (type == Character.class || type == char.class) {
            return Character.valueOf(value.toCharArray()[0]);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == List.class) {
            //如果属性类型是List，则认为是我们特殊定义的List<HashMap<Object, Object>>类型，用它特有的方式解析。
            return paresJson2ListMap(value);
        } else {
            // 如果是数组或者其他情况，则直接抛出不支持的异常
            throw new IllegalArgumentException("DefaultConverter not support type [" + type + "],"
                                               + "failed to convert value [" + value + "].");
        }
    }

    /**
     * Pares json to list map list.
     *
     * @param s the s
     * @return the list
     */
    public static List<HashMap<Object, Object>> paresJson2ListMap(String s) {
        List<HashMap<Object, Object>> result = new ArrayList<HashMap<Object, Object>>();
        s = s.substring(1, s.length() - 1);
        while (s.contains("{")) {
            HashMap<Object, Object> tmpMap = new HashMap<Object, Object>();
            String mapStr = s.substring(s.indexOf("{") + 1, s.indexOf("}"));
            String[] entryStr = mapStr.split(",");
            for (String entry : entryStr) {
                entry = entry.trim();
                String[] keyAndValue = entry.split("=");
                if (keyAndValue.length == 1) {
                    tmpMap.put(keyAndValue[0], "");
                }
                if (keyAndValue.length == 2) {
                    tmpMap.put(keyAndValue[0], keyAndValue[1]);
                }
            }
            result.add(tmpMap);
            s = s.substring(s.indexOf("}") + 1);

        }
        return result;
    }

}