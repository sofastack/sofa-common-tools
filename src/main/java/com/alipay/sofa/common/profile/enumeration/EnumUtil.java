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
package com.alipay.sofa.common.profile.enumeration;

import com.alipay.sofa.common.profile.enumeration.Enum.EnumType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author luoguimu123
 * @version $Id: EnumUtil.java, v 0.1 2017年08月01日 上午11:52 luoguimu123 Exp $
 */
public class EnumUtil {
    private static final Map entries = new WeakHashMap();

    public EnumUtil() {
    }

    public static Class getUnderlyingClass(Class enumClass) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Class var2;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var2 = getEnumType(enumClass).getUnderlyingClass();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var2;
    }

    public static boolean isNameDefined(Class enumClass, String name) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        boolean var3;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var3 = getEnumType(enumClass).nameMap.containsKey(name);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var3;
    }

    public static boolean isValueDefined(Class enumClass, Number value) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        boolean var3;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var3 = getEnumType(enumClass).valueMap.containsKey(value);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var3;
    }

    public static Enum getEnumByName(Class enumClass, String name) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Enum var4;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            EnumType enumType = getEnumType(enumClass);
            if (enumType.enumList.size() != enumType.nameMap.size()) {
                enumType.populateNames(enumClass);
            }

            var4 = (Enum) enumType.nameMap.get(name);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var4;
    }

    public static Enum getEnumByValue(Class enumClass, Number value) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Enum var3;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var3 = (Enum) getEnumType(enumClass).valueMap.get(value);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var3;
    }

    public static Enum getEnumByValue(Class enumClass, int value) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Enum var3;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var3 = (Enum) getEnumType(enumClass).valueMap.get(new Integer(value));
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var3;
    }

    public static Enum getEnumByValue(Class enumClass, long value) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Enum var4;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var4 = (Enum) getEnumType(enumClass).valueMap.get(new Long(value));
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var4;
    }

    public static Map getEnumMap(Class enumClass) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Map var2;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var2 = Collections.unmodifiableMap(getEnumType(enumClass).nameMap);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var2;
    }

    public static Iterator getEnumIterator(Class enumClass) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        Iterator var2;
        try {
            Thread.currentThread().setContextClassLoader(enumClass.getClassLoader());
            var2 = getEnumType(enumClass).enumList.iterator();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return var2;
    }

    static Map getEnumEntryMap(Class enumClass) {
        ClassLoader classLoader = enumClass.getClassLoader();
        Map entryMap = null;
        synchronized (entries) {
            entryMap = (Map) entries.get(classLoader);
            if (entryMap == null) {
                entryMap = new ConcurrentHashMap();
                entries.put(classLoader, entryMap);
            }

            return (Map) entryMap;
        }
    }

    static EnumType getEnumType(Class enumClass) {
        if (enumClass == null) {
            throw new NullPointerException("The Enum class must not be null");
        } else if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException(MessageFormat.format(
                "Class \"{0}\" is not a subclass of Enum", new Object[] { enumClass.getName() }));
        } else {
            ConcurrentHashMap entryMap = (ConcurrentHashMap) getEnumEntryMap(enumClass);
            EnumType enumType = (EnumType) entryMap.get(enumClass.getName());
            if (enumType == null) {
                Method createEnumTypeMethod = findStaticMethod(enumClass, "createEnumType",
                    new Class[0]);
                if (createEnumTypeMethod != null) {
                    try {
                        enumType = (EnumType) createEnumTypeMethod.invoke((Object) null,
                            new Object[0]);
                    } catch (IllegalAccessException var5) {
                        throw new RuntimeException(var5);
                    } catch (IllegalArgumentException var6) {
                        throw new RuntimeException(var6);
                    } catch (InvocationTargetException var7) {
                        throw new RuntimeException(var7);
                    } catch (ClassCastException var8) {
                        throw new RuntimeException(var8);
                    }
                }

                if (enumType != null) {
                    enumType.populateNames(enumClass);
                    EnumType existing = (EnumType) entryMap.putIfAbsent(enumClass.getName(),
                        enumType);
                    if (existing != null) {
                        enumType = existing;
                    }
                }
            }

            if (enumType == null) {
                throw new UnsupportedOperationException(MessageFormat.format(
                    "Could not create EnumType for class \"{0}\"",
                    new Object[] { enumClass.getName() }));
            } else {
                return enumType;
            }
        }
    }

    private static Method findStaticMethod(Class enumClass, String methodName, Class[] paramTypes) {
        Method method = null;
        Class clazz = enumClass;

        while (!clazz.equals(Enum.class)) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (NoSuchMethodException var6) {
                clazz = clazz.getSuperclass();
            }
        }

        return method != null && Modifier.isStatic(method.getModifiers()) ? method : null;
    }
}
