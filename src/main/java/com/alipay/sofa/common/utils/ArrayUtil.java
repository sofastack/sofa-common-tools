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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author luoguimu123
 * @version $Id: ArrayUtil.java, v 0.1 2017年08月01日 下午12:02 luoguimu123 Exp $
 */
public class ArrayUtil {
    public static final Object[]    EMPTY_OBJECT_ARRAY           = new Object[0];
    public static final Class[]     EMPTY_CLASS_ARRAY            = new Class[0];
    public static final String[]    EMPTY_STRING_ARRAY           = new String[0];
    public static final long[]      EMPTY_LONG_ARRAY             = new long[0];
    public static final Long[]      EMPTY_LONG_OBJECT_ARRAY      = new Long[0];
    public static final int[]       EMPTY_INT_ARRAY              = new int[0];
    public static final Integer[]   EMPTY_INTEGER_OBJECT_ARRAY   = new Integer[0];
    public static final short[]     EMPTY_SHORT_ARRAY            = new short[0];
    public static final Short[]     EMPTY_SHORT_OBJECT_ARRAY     = new Short[0];
    public static final byte[]      EMPTY_BYTE_ARRAY             = new byte[0];
    public static final Byte[]      EMPTY_BYTE_OBJECT_ARRAY      = new Byte[0];
    public static final double[]    EMPTY_DOUBLE_ARRAY           = new double[0];
    public static final Double[]    EMPTY_DOUBLE_OBJECT_ARRAY    = new Double[0];
    public static final float[]     EMPTY_FLOAT_ARRAY            = new float[0];
    public static final Float[]     EMPTY_FLOAT_OBJECT_ARRAY     = new Float[0];
    public static final boolean[]   EMPTY_BOOLEAN_ARRAY          = new boolean[0];
    public static final Boolean[]   EMPTY_BOOLEAN_OBJECT_ARRAY   = new Boolean[0];
    public static final char[]      EMPTY_CHAR_ARRAY             = new char[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    public ArrayUtil() {
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(long[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(int[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(short[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(byte[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(double[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(float[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(boolean[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(char[] array) {
        return array != null && array.length > 0;
    }

    public static Object[] defaultIfNull(Object[] array) {
        return array == null ? EMPTY_OBJECT_ARRAY : array;
    }

    public static long[] defaultIfNull(long[] array) {
        return array == null ? EMPTY_LONG_ARRAY : array;
    }

    public static int[] defaultIfNull(int[] array) {
        return array == null ? EMPTY_INT_ARRAY : array;
    }

    public static short[] defaultIfNull(short[] array) {
        return array == null ? EMPTY_SHORT_ARRAY : array;
    }

    public static byte[] defaultIfNull(byte[] array) {
        return array == null ? EMPTY_BYTE_ARRAY : array;
    }

    public static double[] defaultIfNull(double[] array) {
        return array == null ? EMPTY_DOUBLE_ARRAY : array;
    }

    public static float[] defaultIfNull(float[] array) {
        return array == null ? EMPTY_FLOAT_ARRAY : array;
    }

    public static boolean[] defaultIfNull(boolean[] array) {
        return array == null ? EMPTY_BOOLEAN_ARRAY : array;
    }

    public static char[] defaultIfNull(char[] array) {
        return array == null ? EMPTY_CHAR_ARRAY : array;
    }

    public static Object[] defaultIfNull(Object[] array, Object[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static long[] defaultIfNull(long[] array, long[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static int[] defaultIfNull(int[] array, int[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static short[] defaultIfNull(short[] array, short[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static byte[] defaultIfNull(byte[] array, byte[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static double[] defaultIfNull(double[] array, double[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static float[] defaultIfNull(float[] array, float[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static boolean[] defaultIfNull(boolean[] array, boolean[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static char[] defaultIfNull(char[] array, char[] defaultArray) {
        return array == null ? defaultArray : array;
    }

    public static Object[] defaultIfNull(Object[] array, Class defaultComponentType) {
        return array == null ? (Object[]) ((Object[]) Array.newInstance(
            ClassUtil.getNonPrimitiveType(defaultComponentType), 0)) : array;
    }

    public static Object[] defaultIfEmpty(Object[] array) {
        return array == null ? EMPTY_OBJECT_ARRAY : array;
    }

    public static long[] defaultIfEmpty(long[] array) {
        return array == null ? EMPTY_LONG_ARRAY : array;
    }

    public static int[] defaultIfEmpty(int[] array) {
        return array == null ? EMPTY_INT_ARRAY : array;
    }

    public static short[] defaultIfEmpty(short[] array) {
        return array == null ? EMPTY_SHORT_ARRAY : array;
    }

    public static byte[] defaultIfEmpty(byte[] array) {
        return array == null ? EMPTY_BYTE_ARRAY : array;
    }

    public static double[] defaultIfEmpty(double[] array) {
        return array == null ? EMPTY_DOUBLE_ARRAY : array;
    }

    public static float[] defaultIfEmpty(float[] array) {
        return array == null ? EMPTY_FLOAT_ARRAY : array;
    }

    public static boolean[] defaultIfEmpty(boolean[] array) {
        return array == null ? EMPTY_BOOLEAN_ARRAY : array;
    }

    public static char[] defaultIfEmpty(char[] array) {
        return array == null ? EMPTY_CHAR_ARRAY : array;
    }

    public static Object[] defaultIfEmpty(Object[] array, Object[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static long[] defaultIfEmpty(long[] array, long[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static int[] defaultIfEmpty(int[] array, int[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static short[] defaultIfEmpty(short[] array, short[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static byte[] defaultIfEmpty(byte[] array, byte[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static double[] defaultIfEmpty(double[] array, double[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static float[] defaultIfEmpty(float[] array, float[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static boolean[] defaultIfEmpty(boolean[] array, boolean[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static char[] defaultIfEmpty(char[] array, char[] defaultArray) {
        return array != null && array.length != 0 ? array : defaultArray;
    }

    public static Object[] defaultIfEmpty(Object[] array, Class defaultComponentType) {
        return array != null && array.length != 0 ? array : (Object[]) ((Object[]) Array
            .newInstance(ClassUtil.getNonPrimitiveType(defaultComponentType), 0));
    }

    public static boolean equals(Object array1, Object array2) {
        if (array1 == array2) {
            return true;
        } else if (array1 != null && array2 != null) {
            Class clazz = array1.getClass();
            if (!clazz.equals(array2.getClass())) {
                return false;
            } else if (!clazz.isArray()) {
                return array1.equals(array2);
            } else {
                int i;
                if (array1 instanceof long[]) {
                    long[] longArray1 = (long[]) ((long[]) array1);
                    long[] longArray2 = (long[]) ((long[]) array2);
                    if (longArray1.length != longArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < longArray1.length; ++i) {
                            if (longArray1[i] != longArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof int[]) {
                    int[] intArray1 = (int[]) ((int[]) array1);
                    int[] intArray2 = (int[]) ((int[]) array2);
                    if (intArray1.length != intArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < intArray1.length; ++i) {
                            if (intArray1[i] != intArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof short[]) {
                    short[] shortArray1 = (short[]) ((short[]) array1);
                    short[] shortArray2 = (short[]) ((short[]) array2);
                    if (shortArray1.length != shortArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < shortArray1.length; ++i) {
                            if (shortArray1[i] != shortArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof byte[]) {
                    byte[] byteArray1 = (byte[]) ((byte[]) array1);
                    byte[] byteArray2 = (byte[]) ((byte[]) array2);
                    if (byteArray1.length != byteArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < byteArray1.length; ++i) {
                            if (byteArray1[i] != byteArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof double[]) {
                    double[] doubleArray1 = (double[]) ((double[]) array1);
                    double[] doubleArray2 = (double[]) ((double[]) array2);
                    if (doubleArray1.length != doubleArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < doubleArray1.length; ++i) {
                            if (Double.doubleToLongBits(doubleArray1[i]) != Double
                                .doubleToLongBits(doubleArray2[i])) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof float[]) {
                    float[] floatArray1 = (float[]) ((float[]) array1);
                    float[] floatArray2 = (float[]) ((float[]) array2);
                    if (floatArray1.length != floatArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < floatArray1.length; ++i) {
                            if (Float.floatToIntBits(floatArray1[i]) != Float
                                .floatToIntBits(floatArray2[i])) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof boolean[]) {
                    boolean[] booleanArray1 = (boolean[]) ((boolean[]) array1);
                    boolean[] booleanArray2 = (boolean[]) ((boolean[]) array2);
                    if (booleanArray1.length != booleanArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < booleanArray1.length; ++i) {
                            if (booleanArray1[i] != booleanArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else if (array1 instanceof char[]) {
                    char[] charArray1 = (char[]) ((char[]) array1);
                    char[] charArray2 = (char[]) ((char[]) array2);
                    if (charArray1.length != charArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < charArray1.length; ++i) {
                            if (charArray1[i] != charArray2[i]) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else {
                    Object[] objectArray1 = (Object[]) ((Object[]) array1);
                    Object[] objectArray2 = (Object[]) ((Object[]) array2);
                    if (objectArray1.length != objectArray2.length) {
                        return false;
                    } else {
                        for (i = 0; i < objectArray1.length; ++i) {
                            if (!equals(objectArray1[i], objectArray2[i])) {
                                return false;
                            }
                        }

                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    public static int hashCode(Object array) {
        if (array == null) {
            return 0;
        } else if (!array.getClass().isArray()) {
            return array.hashCode();
        } else {
            int hashCode = 17;
            int i;
            if (array instanceof long[]) {
                long[] longArray = (long[]) ((long[]) array);

                for (i = 0; i < longArray.length; ++i) {
                    hashCode = hashCode * 37 + (int) (longArray[i] ^ longArray[i] >> 32);
                }
            } else if (array instanceof int[]) {
                int[] intArray = (int[]) ((int[]) array);

                for (i = 0; i < intArray.length; ++i) {
                    hashCode = hashCode * 37 + intArray[i];
                }
            } else if (array instanceof short[]) {
                short[] shortArray = (short[]) ((short[]) array);

                for (i = 0; i < shortArray.length; ++i) {
                    hashCode = hashCode * 37 + shortArray[i];
                }
            } else if (array instanceof byte[]) {
                byte[] byteArray = (byte[]) ((byte[]) array);

                for (i = 0; i < byteArray.length; ++i) {
                    hashCode = hashCode * 37 + byteArray[i];
                }
            } else if (array instanceof double[]) {
                double[] doubleArray = (double[]) ((double[]) array);

                for (i = 0; i < doubleArray.length; ++i) {
                    long longBits = Double.doubleToLongBits(doubleArray[i]);
                    hashCode = hashCode * 37 + (int) (longBits ^ longBits >> 32);
                }
            } else if (array instanceof float[]) {
                float[] floatArray = (float[]) ((float[]) array);

                for (i = 0; i < floatArray.length; ++i) {
                    hashCode = hashCode * 37 + Float.floatToIntBits(floatArray[i]);
                }
            } else if (array instanceof boolean[]) {
                boolean[] booleanArray = (boolean[]) ((boolean[]) array);

                for (i = 0; i < booleanArray.length; ++i) {
                    hashCode = hashCode * 37 + (booleanArray[i] ? 1 : 0);
                }
            } else if (array instanceof char[]) {
                char[] charArray = (char[]) ((char[]) array);

                for (i = 0; i < charArray.length; ++i) {
                    hashCode = hashCode * 37 + charArray[i];
                }
            } else {
                Object[] objectArray = (Object[]) ((Object[]) array);

                for (i = 0; i < objectArray.length; ++i) {
                    hashCode = hashCode * 37 + hashCode(objectArray[i]);
                }
            }

            return hashCode;
        }
    }

    public static List toFixedList(Object[] array) {
        return array == null ? null : Arrays.asList(array);
    }

    public static List toList(Object array) {
        return toList(array, (List) null);
    }

    public static List toList(Object array, List list) {
        if (array == null) {
            return (List) list;
        } else {
            if (!array.getClass().isArray()) {
                if (list == null) {
                    list = new ArrayList(1);
                }

                ((List) list).add(array);
            } else {
                int i;
                if (array instanceof long[]) {
                    long[] longArray = (long[]) ((long[]) array);
                    if (list == null) {
                        list = new ArrayList(longArray.length);
                    }

                    for (i = 0; i < longArray.length; ++i) {
                        ((List) list).add(new Long(longArray[i]));
                    }
                } else if (array instanceof int[]) {
                    int[] intArray = (int[]) ((int[]) array);
                    if (list == null) {
                        list = new ArrayList(intArray.length);
                    }

                    for (i = 0; i < intArray.length; ++i) {
                        ((List) list).add(new Integer(intArray[i]));
                    }
                } else if (array instanceof short[]) {
                    short[] shortArray = (short[]) ((short[]) array);
                    if (list == null) {
                        list = new ArrayList(shortArray.length);
                    }

                    for (i = 0; i < shortArray.length; ++i) {
                        ((List) list).add(new Short(shortArray[i]));
                    }
                } else if (array instanceof byte[]) {
                    byte[] byteArray = (byte[]) ((byte[]) array);
                    if (list == null) {
                        list = new ArrayList(byteArray.length);
                    }

                    for (i = 0; i < byteArray.length; ++i) {
                        ((List) list).add(new Byte(byteArray[i]));
                    }
                } else if (array instanceof double[]) {
                    double[] doubleArray = (double[]) ((double[]) array);
                    if (list == null) {
                        list = new ArrayList(doubleArray.length);
                    }

                    for (i = 0; i < doubleArray.length; ++i) {
                        ((List) list).add(new Double(doubleArray[i]));
                    }
                } else if (array instanceof float[]) {
                    float[] floatArray = (float[]) ((float[]) array);
                    if (list == null) {
                        list = new ArrayList(floatArray.length);
                    }

                    for (i = 0; i < floatArray.length; ++i) {
                        ((List) list).add(new Float(floatArray[i]));
                    }
                } else if (array instanceof boolean[]) {
                    boolean[] booleanArray = (boolean[]) ((boolean[]) array);
                    if (list == null) {
                        list = new ArrayList(booleanArray.length);
                    }

                    for (i = 0; i < booleanArray.length; ++i) {
                        ((List) list).add(booleanArray[i] ? Boolean.TRUE : Boolean.FALSE);
                    }
                } else if (array instanceof char[]) {
                    char[] charArray = (char[]) ((char[]) array);
                    if (list == null) {
                        list = new ArrayList(charArray.length);
                    }

                    for (i = 0; i < charArray.length; ++i) {
                        ((List) list).add(new Character(charArray[i]));
                    }
                } else {
                    Object[] objectArray = (Object[]) ((Object[]) array);
                    if (list == null) {
                        list = new ArrayList(objectArray.length);
                    }

                    for (i = 0; i < objectArray.length; ++i) {
                        ((List) list).add(objectArray[i]);
                    }
                }
            }

            return (List) list;
        }
    }

    public static Map toMap(Object[] array) {
        return toMap(array, (Map) null);
    }

    public static Map toMap(Object[] array, Map map) {
        if (array == null) {
            return (Map) map;
        } else {
            if (map == null) {
                map = new HashMap((int) ((double) array.length * 1.5D));
            }

            for (int i = 0; i < array.length; ++i) {
                Object object = array[i];
                if (object instanceof Map.Entry) {
                    Entry entry = (Entry) object;
                    ((Map) map).put(entry.getKey(), entry.getValue());
                } else {
                    if (!(object instanceof Object[])) {
                        throw new IllegalArgumentException(
                            "Array element " + i + ", '" + object
                                    + "', is neither of type Map.Entry nor an Array");
                    }

                    Object[] entry = (Object[]) ((Object[]) object);
                    if (entry.length < 2) {
                        throw new IllegalArgumentException("Array element " + i + ", '" + object
                                                           + "', has a length less than 2");
                    }

                    ((Map) map).put(entry[0], entry[1]);
                }
            }

            return (Map) map;
        }
    }

    public static Object[] clone(Object[] array) {
        return array == null ? null : (Object[]) ((Object[]) array.clone());
    }

    public static long[] clone(long[] array) {
        return array == null ? null : (long[]) ((long[]) array.clone());
    }

    public static int[] clone(int[] array) {
        return array == null ? null : (int[]) ((int[]) array.clone());
    }

    public static short[] clone(short[] array) {
        return array == null ? null : (short[]) ((short[]) array.clone());
    }

    public static byte[] clone(byte[] array) {
        return array == null ? null : (byte[]) ((byte[]) array.clone());
    }

    public static double[] clone(double[] array) {
        return array == null ? null : (double[]) ((double[]) array.clone());
    }

    public static float[] clone(float[] array) {
        return array == null ? null : (float[]) ((float[]) array.clone());
    }

    public static boolean[] clone(boolean[] array) {
        return array == null ? null : (boolean[]) ((boolean[]) array.clone());
    }

    public static char[] clone(char[] array) {
        return array == null ? null : (char[]) ((char[]) array.clone());
    }

    public static boolean isSameLength(Object[] array1, Object[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(long[] array1, long[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(int[] array1, int[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(short[] array1, short[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(byte[] array1, byte[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(double[] array1, double[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(float[] array1, float[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(boolean[] array1, boolean[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static boolean isSameLength(char[] array1, char[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;
        return length1 == length2;
    }

    public static void reverse(Object[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                Object tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(long[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                long tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(int[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                int tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(short[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                short tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(byte[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                byte tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(double[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                double tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(float[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                float tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(boolean[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                boolean tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static void reverse(char[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; --j) {
                char tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                ++i;
            }

        }
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf((Object[]) array, (Object) objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object[] arrayToFind) {
        return indexOf((Object[]) array, (Object[]) arrayToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            int i;
            if (objectToFind == null) {
                for (i = startIndex; i < array.length; ++i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else {
                for (i = startIndex; i < array.length; ++i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    public static int indexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    Object first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || ObjectUtil.equals(array[i], first)) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (ObjectUtil.equals(array[j++], arrayToFind[var10++]));

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, 2147483647);
    }

    public static int lastIndexOf(Object[] array, Object[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            int i;
            if (objectToFind == null) {
                for (i = startIndex; i >= 0; --i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else {
                for (i = startIndex; i >= 0; --i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    Object last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || ObjectUtil.equals(array[i], last)) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (ObjectUtil.equals(array[j--], arrayToFind[var12--]));

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    public static boolean contains(Object[] array, Object[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(long[] array, long longToFind) {
        return indexOf(array, longToFind, 0);
    }

    public static int indexOf(long[] array, long[] arrayToFind) {
        return indexOf((long[]) array, (long[]) arrayToFind, 0);
    }

    public static int indexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (longToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    long first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var11 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var11++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(long[] array, long longToFind) {
        return lastIndexOf(array, longToFind, 2147483647);
    }

    public static int lastIndexOf(long[] array, long[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (longToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    long last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var13 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var13--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(long[] array, long longToFind) {
        return indexOf(array, longToFind) != -1;
    }

    public static boolean contains(long[] array, long[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(int[] array, int intToFind) {
        return indexOf((int[]) array, (int) intToFind, 0);
    }

    public static int indexOf(int[] array, int[] arrayToFind) {
        return indexOf((int[]) array, (int[]) arrayToFind, 0);
    }

    public static int indexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (intToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var10++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(int[] array, int intToFind) {
        return lastIndexOf(array, intToFind, 2147483647);
    }

    public static int lastIndexOf(int[] array, int[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (intToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    int last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var12--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(int[] array, int intToFind) {
        return indexOf(array, intToFind) != -1;
    }

    public static boolean contains(int[] array, int[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(short[] array, short shortToFind) {
        return indexOf((short[]) array, (short) shortToFind, 0);
    }

    public static int indexOf(short[] array, short[] arrayToFind) {
        return indexOf((short[]) array, (short[]) arrayToFind, 0);
    }

    public static int indexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (shortToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    short first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var10++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(short[] array, short shortToFind) {
        return lastIndexOf(array, shortToFind, 2147483647);
    }

    public static int lastIndexOf(short[] array, short[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (shortToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    short last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var12--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(short[] array, short shortToFind) {
        return indexOf(array, shortToFind) != -1;
    }

    public static boolean contains(short[] array, short[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(byte[] array, byte byteToFind) {
        return indexOf((byte[]) array, (byte) byteToFind, 0);
    }

    public static int indexOf(byte[] array, byte[] arrayToFind) {
        return indexOf((byte[]) array, (byte[]) arrayToFind, 0);
    }

    public static int indexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (byteToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    byte first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var10++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(byte[] array, byte byteToFind) {
        return lastIndexOf(array, byteToFind, 2147483647);
    }

    public static int lastIndexOf(byte[] array, byte[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (byteToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    byte last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var12--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(byte[] array, byte byteToFind) {
        return indexOf(array, byteToFind) != -1;
    }

    public static boolean contains(byte[] array, byte[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(double[] array, double doubleToFind) {
        return indexOf(array, doubleToFind, 0, 0.0D);
    }

    public static int indexOf(double[] array, double doubleToFind, double tolerance) {
        return indexOf(array, doubleToFind, 0, tolerance);
    }

    public static int indexOf(double[] array, double[] arrayToFind) {
        return indexOf(array, arrayToFind, 0, 0.0D);
    }

    public static int indexOf(double[] array, double[] arrayToFind, double tolerance) {
        return indexOf(array, arrayToFind, 0, tolerance);
    }

    public static int indexOf(double[] array, double doubleToFind, int startIndex) {
        return indexOf(array, doubleToFind, startIndex, 0.0D);
    }

    public static int indexOf(double[] array, double doubleToFind, int startIndex, double tolerance) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            double min = doubleToFind - tolerance;
            double max = doubleToFind + tolerance;

            for (int i = startIndex; i < array.length; ++i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(double[] array, double[] arrayToFind, int startIndex) {
        return indexOf(array, arrayToFind, startIndex, 0.0D);
    }

    public static int indexOf(double[] array, double[] arrayToFind, int startIndex, double tolerance) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    double firstMin = arrayToFind[0] - tolerance;
                    double firstMax = arrayToFind[0] + tolerance;
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] >= firstMin && array[i] <= firstMax) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var15 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (Math.abs(array[j++] - arrayToFind[var15++]) <= tolerance);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(double[] array, double doubleToFind) {
        return lastIndexOf(array, doubleToFind, 2147483647, 0.0D);
    }

    public static int lastIndexOf(double[] array, double doubleToFind, double tolerance) {
        return lastIndexOf(array, doubleToFind, 2147483647, tolerance);
    }

    public static int lastIndexOf(double[] array, double[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647, 0.0D);
    }

    public static int lastIndexOf(double[] array, double[] arrayToFind, double tolerance) {
        return lastIndexOf(array, arrayToFind, 2147483647, tolerance);
    }

    public static int lastIndexOf(double[] array, double doubleToFind, int startIndex) {
        return lastIndexOf(array, doubleToFind, startIndex, 0.0D);
    }

    public static int lastIndexOf(double[] array, double doubleToFind, int startIndex,
                                  double tolerance) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            double min = doubleToFind - tolerance;
            double max = doubleToFind + tolerance;

            for (int i = startIndex; i >= 0; --i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(double[] array, double[] arrayToFind, int startIndex) {
        return lastIndexOf(array, arrayToFind, startIndex, 0.0D);
    }

    public static int lastIndexOf(double[] array, double[] arrayToFind, int startIndex,
                                  double tolerance) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    double lastMin = arrayToFind[lastIndex] - tolerance;
                    double lastMax = arrayToFind[lastIndex] + tolerance;
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] >= lastMin && array[i] <= lastMax) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var17 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (Math.abs(array[j--] - arrayToFind[var17--]) <= tolerance);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(double[] array, double doubleToFind) {
        return indexOf(array, doubleToFind) != -1;
    }

    public static boolean contains(double[] array, double doubleToFind, double tolerance) {
        return indexOf(array, doubleToFind, tolerance) != -1;
    }

    public static boolean contains(double[] array, double[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static boolean contains(double[] array, double[] arrayToFind, double tolerance) {
        return indexOf(array, arrayToFind, tolerance) != -1;
    }

    public static int indexOf(float[] array, float floatToFind) {
        return indexOf(array, floatToFind, 0, 0.0F);
    }

    public static int indexOf(float[] array, float floatToFind, float tolerance) {
        return indexOf(array, floatToFind, 0, tolerance);
    }

    public static int indexOf(float[] array, float[] arrayToFind) {
        return indexOf(array, arrayToFind, 0, 0.0F);
    }

    public static int indexOf(float[] array, float[] arrayToFind, float tolerance) {
        return indexOf(array, arrayToFind, 0, tolerance);
    }

    public static int indexOf(float[] array, float floatToFind, int startIndex) {
        return indexOf(array, floatToFind, startIndex, 0.0F);
    }

    public static int indexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            float min = floatToFind - tolerance;
            float max = floatToFind + tolerance;

            for (int i = startIndex; i < array.length; ++i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(float[] array, float[] arrayToFind, int startIndex) {
        return indexOf(array, arrayToFind, startIndex, 0.0F);
    }

    public static int indexOf(float[] array, float[] arrayToFind, int startIndex, float tolerance) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    float firstMin = arrayToFind[0] - tolerance;
                    float firstMax = arrayToFind[0] + tolerance;
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] >= firstMin && array[i] <= firstMax) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var12 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (Math.abs(array[j++] - arrayToFind[var12++]) <= tolerance);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(float[] array, float floatToFind) {
        return lastIndexOf(array, floatToFind, 2147483647, 0.0F);
    }

    public static int lastIndexOf(float[] array, float floatToFind, float tolerance) {
        return lastIndexOf(array, floatToFind, 2147483647, tolerance);
    }

    public static int lastIndexOf(float[] array, float[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647, 0.0F);
    }

    public static int lastIndexOf(float[] array, float[] arrayToFind, float tolerance) {
        return lastIndexOf(array, arrayToFind, 2147483647, tolerance);
    }

    public static int lastIndexOf(float[] array, float floatToFind, int startIndex) {
        return lastIndexOf(array, floatToFind, startIndex, 0.0F);
    }

    public static int lastIndexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            float min = floatToFind - tolerance;
            float max = floatToFind + tolerance;

            for (int i = startIndex; i >= 0; --i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(float[] array, float[] arrayToFind, int startIndex) {
        return lastIndexOf(array, arrayToFind, startIndex, 0.0F);
    }

    public static int lastIndexOf(float[] array, float[] arrayToFind, int startIndex,
                                  float tolerance) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    float lastMin = arrayToFind[lastIndex] - tolerance;
                    float lastMax = arrayToFind[lastIndex] + tolerance;
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] >= lastMin && array[i] <= lastMax) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var14 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (Math.abs(array[j--] - arrayToFind[var14--]) <= tolerance);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(float[] array, float floatToFind) {
        return indexOf(array, floatToFind) != -1;
    }

    public static boolean contains(float[] array, float floatToFind, float tolerance) {
        return indexOf(array, floatToFind, tolerance) != -1;
    }

    public static boolean contains(float[] array, float[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static boolean contains(float[] array, float[] arrayToFind, float tolerance) {
        return indexOf(array, arrayToFind, tolerance) != -1;
    }

    public static int indexOf(boolean[] array, boolean booleanToFind) {
        return indexOf(array, booleanToFind, 0);
    }

    public static int indexOf(boolean[] array, boolean[] arrayToFind) {
        return indexOf((boolean[]) array, (boolean[]) arrayToFind, 0);
    }

    public static int indexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (booleanToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    boolean first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var10++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(boolean[] array, boolean booleanToFind) {
        return lastIndexOf(array, booleanToFind, 2147483647);
    }

    public static int lastIndexOf(boolean[] array, boolean[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (booleanToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    boolean last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var12--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(boolean[] array, boolean booleanToFind) {
        return indexOf(array, booleanToFind) != -1;
    }

    public static boolean contains(boolean[] array, boolean[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static int indexOf(char[] array, char charToFind) {
        return indexOf((char[]) array, (char) charToFind, 0);
    }

    public static int indexOf(char[] array, char[] arrayToFind) {
        return indexOf((char[]) array, (char[]) arrayToFind, 0);
    }

    public static int indexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (charToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int indexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            if (startIndex >= sourceLength) {
                return targetLength == 0 ? sourceLength : -1;
            } else {
                if (startIndex < 0) {
                    startIndex = 0;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    char first = arrayToFind[0];
                    int i = startIndex;
                    int max = sourceLength - targetLength;

                    while (true) {
                        while (i > max || array[i] == first) {
                            if (i > max) {
                                return -1;
                            }

                            int j = i + 1;
                            int end = j + targetLength - 1;
                            int var10 = 1;

                            do {
                                if (j >= end) {
                                    return i;
                                }
                            } while (array[j++] == arrayToFind[var10++]);

                            ++i;
                        }

                        ++i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static int lastIndexOf(char[] array, char charToFind) {
        return lastIndexOf(array, charToFind, 2147483647);
    }

    public static int lastIndexOf(char[] array, char[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, 2147483647);
    }

    public static int lastIndexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (charToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    public static int lastIndexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array != null && arrayToFind != null) {
            int sourceLength = array.length;
            int targetLength = arrayToFind.length;
            int rightIndex = sourceLength - targetLength;
            if (startIndex < 0) {
                return -1;
            } else {
                if (startIndex > rightIndex) {
                    startIndex = rightIndex;
                }

                if (targetLength == 0) {
                    return startIndex;
                } else {
                    int lastIndex = targetLength - 1;
                    char last = arrayToFind[lastIndex];
                    int min = targetLength - 1;
                    int i = min + startIndex;

                    while (true) {
                        while (i < min || array[i] == last) {
                            if (i < min) {
                                return -1;
                            }

                            int j = i - 1;
                            int start = j - (targetLength - 1);
                            int var12 = lastIndex - 1;

                            do {
                                if (j <= start) {
                                    return start + 1;
                                }
                            } while (array[j--] == arrayToFind[var12--]);

                            --i;
                        }

                        --i;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public static boolean contains(char[] array, char charToFind) {
        return indexOf(array, charToFind) != -1;
    }

    public static boolean contains(char[] array, char[] arrayToFind) {
        return indexOf(array, arrayToFind) != -1;
    }

    public static String toString(Object array) {
        return toString(array, "[]", "<null>");
    }

    public static String toString(Object array, String nullArrayStr) {
        return toString(array, nullArrayStr, "<null>");
    }

    public static String toString(Object array, String nullArrayStr, String nullElementStr) {
        if (array == null) {
            return nullArrayStr;
        } else {
            StringBuffer buffer = new StringBuffer();
            toString(buffer, array, nullArrayStr, nullElementStr);
            return buffer.toString();
        }
    }

    private static void toString(StringBuffer buffer, Object array, String nullArrayStr,
                                 String nullElementStr) {
        if (array == null) {
            buffer.append(nullElementStr);
        } else if (!array.getClass().isArray()) {
            buffer.append(ObjectUtil.toString(array, nullElementStr));
        } else {
            buffer.append('[');
            int length;
            int i;
            if (array instanceof long[]) {
                long[] longArray = (long[]) ((long[]) array);
                length = longArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(longArray[i]);
                }
            } else if (array instanceof int[]) {
                int[] intArray = (int[]) ((int[]) array);
                length = intArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(intArray[i]);
                }
            } else if (array instanceof short[]) {
                short[] shortArray = (short[]) ((short[]) array);
                length = shortArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(shortArray[i]);
                }
            } else if (array instanceof byte[]) {
                byte[] byteArray = (byte[]) ((byte[]) array);
                length = byteArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    } else {
                        buffer.append("0x");
                    }

                    String hexStr = Integer.toHexString(255 & byteArray[i]).toUpperCase();
                    if (hexStr.length() == 0) {
                        buffer.append("00");
                    } else if (hexStr.length() == 1) {
                        buffer.append("0");
                    }

                    buffer.append(hexStr);
                }
            } else if (array instanceof double[]) {
                double[] doubleArray = (double[]) ((double[]) array);
                length = doubleArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(doubleArray[i]);
                }
            } else if (array instanceof float[]) {
                float[] floatArray = (float[]) ((float[]) array);
                length = floatArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(floatArray[i]);
                }
            } else if (array instanceof boolean[]) {
                boolean[] booleanArray = (boolean[]) ((boolean[]) array);
                length = booleanArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(booleanArray[i]);
                }
            } else if (array instanceof char[]) {
                char[] charArray = (char[]) ((char[]) array);
                length = charArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    buffer.append(charArray[i]);
                }
            } else {
                Object[] objectArray = (Object[]) ((Object[]) array);
                length = objectArray.length;

                for (i = 0; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }

                    toString(buffer, objectArray[i], nullArrayStr, nullElementStr);
                }
            }

            buffer.append(']');
        }
    }
}
