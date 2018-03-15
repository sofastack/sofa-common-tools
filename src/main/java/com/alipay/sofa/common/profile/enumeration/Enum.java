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

import com.alipay.sofa.common.utils.ClassLoaderUtil;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.common.profile.enumeration.internal.NumberType;

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author luoguimu123
 * @version $Id: Enum.java, v 0.1 2017年08月01日 上午11:46 luoguimu123 Exp $
 */
public abstract class Enum extends Number implements NumberType, Comparable, Serializable {
    private static final long serialVersionUID = -3420208858441821772L;
    private String            name;
    private Number            value;

    protected Enum() {
    }

    protected static final Enum create() {
        return createEnum((String) null, (Number) null, false);
    }

    protected static final Enum create(String name) {
        return createEnum(name, (Number) null, false);
    }

    static final Enum createEnum(Number value) {
        return createEnum((String) null, value, true);
    }

    static final Enum createEnum(String name, Number value) {
        return createEnum(name, value, true);
    }

    private static Enum createEnum(String name, Number value, boolean withValue) {
        String enumClassName = null;

        Class enumClass;
        Enum enumObject;
        try {
            enumClassName = getCallerClassName();
            enumClass = ClassLoaderUtil.loadClass(enumClassName);
            enumObject = (Enum) enumClass.newInstance();
            enumObject.setName(StringUtil.trimToNull(name));
        } catch (ClassNotFoundException var8) {
            throw new RuntimeException("Could not find enum class " + enumClassName, var8);
        } catch (Exception var9) {
            throw new RuntimeException("Could not instantiate enum instance of class "
                                       + enumClassName, var9);
        }

        if (withValue && value == null) {
            throw new NullPointerException("The Enum value must not be null");
        } else {
            Enum.EnumType enumType = EnumUtil.getEnumType(enumClass);
            boolean flagMode = enumObject instanceof Flags;
            if (withValue) {
                enumObject.value = enumType.setValue(value, flagMode);
            } else {
                enumObject.value = enumType.getNextValue(flagMode);
            }

            enumType.enumList.add(enumObject);
            if (!enumType.valueMap.containsKey(enumObject.value)) {
                enumType.valueMap.put(enumObject.value, enumObject);
            }

            if (enumObject.name != null && !enumType.nameMap.containsKey(enumObject.name)) {
                enumType.nameMap.put(enumObject.name, enumObject);
            }

            return enumObject;
        }
    }

    private static String getCallerClassName() {
        StackTraceElement[] callers = (new Throwable()).getStackTrace();
        String enumClass = Enum.class.getName();

        for (int i = 0; i < callers.length; ++i) {
            StackTraceElement caller = callers[i];
            String className = caller.getClassName();
            String methodName = caller.getMethodName();
            if (!enumClass.equals(className) && "<clinit>".equals(methodName)) {
                return className;
            }
        }

        throw new RuntimeException("Cannot get Enum-class name");
    }

    public String getName() {
        if (this.name == null) {
            Class enumClass = this.ensureClassLoaded();
            Enum.EnumType enumType = EnumUtil.getEnumType(enumClass);
            enumType.populateNames(enumClass);
        }

        return this.name;
    }

    private Enum setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("Enum name already set: " + this.name);
        } else {
            this.name = name;
            return this;
        }
    }

    public Number getValue() {
        return this.value;
    }

    public byte byteValue() {
        return (byte) this.intValue();
    }

    public short shortValue() {
        return (short) this.intValue();
    }

    public int compareTo(Object otherEnum) {
        if (!this.getClass().equals(otherEnum.getClass())) {
            throw new RuntimeException(MessageFormat.format(
                "Could not compare object of \"{0}\" with object of \"{1}\"", new Object[] {
                        this.getClass().getName(), otherEnum.getClass().getName() }));
        } else {
            return ((Comparable) this.value).compareTo(((Enum) otherEnum).value);
        }
    }

    public boolean equals(Object obj) {
        return obj == this ? true
            : (obj != null && this.getClass().equals(obj.getClass()) ? this.value
                .equals(((Enum) obj).value) : false);
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.value.hashCode();
    }

    public String toString() {
        return this.getName();
    }

    public Class ensureClassLoaded() {
        Class enumClass = this.getClass();
        synchronized (enumClass) {
            return enumClass;
        }
    }

    protected Object writeReplace() throws ObjectStreamException {
        this.getName();
        return this;
    }

    protected Object readResolve() throws ObjectStreamException {
        Class enumClass = this.ensureClassLoaded();
        Enum.EnumType enumType = EnumUtil.getEnumType(enumClass);
        Enum enumObject = (Enum) enumType.nameMap.get(this.getName());
        if (enumObject == null) {
            enumType.populateNames(enumClass);
            enumObject = (Enum) enumType.nameMap.get(this.getName());
        }

        if (enumObject == null) {
            throw new InvalidClassException("Enum name \"" + this.getName()
                                            + "\" not found in class " + enumClass.getName());
        } else if (!enumObject.value.equals(this.value)) {
            throw new InvalidClassException("Enum value \"" + this.value
                                            + "\" does not match in class " + enumClass.getName());
        } else {
            return enumObject;
        }
    }

    protected abstract static class EnumType {
        private Number value;
        final Map      nameMap  = Collections.synchronizedMap(new HashMap());
        final Map      valueMap = Collections.synchronizedMap(new HashMap());
        final List     enumList = new ArrayList();

        protected EnumType() {
        }

        final Number setValue(Number value, boolean flagMode) {
            this.value = value;
            return value;
        }

        final Number getNextValue(boolean flagMode) {
            this.value = this.getNextValue(this.value, flagMode);
            if (flagMode && this.isZero(this.value)) {
                throw new UnsupportedOperationException("The flag value is out of range");
            } else {
                return this.value;
            }
        }

        final void populateNames(Class enumClass) {
            Field[] fields = enumClass.getFields();

            for (int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                int modifier = field.getModifiers();
                if (Modifier.isPublic(modifier) && Modifier.isFinal(modifier)
                    && Modifier.isStatic(modifier)) {
                    try {
                        Object value = field.get((Object) null);
                        Iterator j = this.valueMap.values().iterator();

                        while (j.hasNext()) {
                            Enum enumObject = (Enum) j.next();
                            if (value == enumObject && enumObject.name == null) {
                                enumObject.name = field.getName();
                                this.nameMap.put(enumObject.name, enumObject);
                                break;
                            }
                        }
                    } catch (IllegalAccessException var9) {
                        throw new RuntimeException(var9);
                    }
                }
            }

        }

        protected abstract Class getUnderlyingClass();

        protected abstract Number getNextValue(Number var1, boolean var2);

        protected abstract boolean isZero(Number var1);
    }
}