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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Util to determine Charsets, the implements fork from guava.
 *
 * @author huzijie
 * @version CharsetUtil.java, v 0.1 2023年04月14日 2:06 PM huzijie Exp $
 * @see com.google.common.base.Utf8#isWellFormed(byte[])
 */
public class CharsetUtil {

    public static Logger CHARSET_MONITOR_LOG = LoggerFactory.getLogger("CHARSET_MONITOR_LOG");

    /**
     * Asserts the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     */
    public static void assertUtf8WellFormed(byte[] bytes) {
        checkUtf8WellFormed(bytes, 0);
    }

    /**
     * Asserts that a portion of the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     * @param off The starting position in the array to be checked.
     * @param len The length of the portion to be checked.
     */
    public static void assertUtf8WellFormed(byte[] bytes, int off, int len) {
        checkUtf8WellFormed(bytes, off, len, 0);
    }

    /**
     * Monitor the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     */
    public static void monitorUtf8WellFormed(byte[] bytes) {
        checkUtf8WellFormed(bytes, 1);
    }

    /**
     * Monitor that a portion of the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     * @param off The starting position in the array to be checked.
     * @param len The length of the portion to be checked.
     */
    public static void monitorUtf8WellFormed(byte[] bytes, int off, int len) {
        checkUtf8WellFormed(bytes, off, len, 1);
    }

    /**
     * Checks whether the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     * @param model The checking mode when bytes isn't well formed in UTF-8 encoding forma .
     *        <p>In model 0, it will throw IllegalArgumentException.
     *        <p>In mode 1, it will print error log in slf4j logger: CHARSET_MONITOR_LOG
     */
    public static void checkUtf8WellFormed(byte[] bytes, int model) {
        checkUtf8WellFormed(bytes, 0, bytes.length, model);
    }

    /**
     * Checks whether a portion of the given byte array is well formed in UTF-8 encoding format.
     * @param bytes The byte array to be checked.
     * @param off The starting position in the array to be checked.
     * @param len The length of the portion to be checked.
     * @param model The checking mode when bytes isn't well formed in UTF-8 encoding forma .
     *        <p>In model 0, it will throw IllegalArgumentException.
     *        <p>In mode 1, it will print error log in slf4j logger: CHARSET_MONITOR_LOG
     */
    public static void checkUtf8WellFormed(byte[] bytes, int off, int len, int model) {
        if (!isUtf8WellFormed(bytes, off, len)) {
            switch (model) {
                case 0:
                    throw new IllegalArgumentException("Input byte array is not well formed utf-8");
                case 1:
                    CHARSET_MONITOR_LOG
                        .error("Detect not well formed utf-8 input: {}"
                               + new String(bytes, off, len, StandardCharsets.UTF_8));
                default:
            }
        }
    }

    /**
     *
     * Determines whether the given byte array is well formed in UTF-8 encoding format according to Unicode 6.0.
     * @param bytes The byte array to be checked.
     *
     * @return true if the byte array is in well formed UTF-8 encoding format, false otherwise
     */
    public static boolean isUtf8WellFormed(byte[] bytes) {
        return isUtf8WellFormed(bytes, 0, bytes.length);
    }

    /**
     *
     * Determines whether a portion of the given byte array is well formed in UTF-8 encoding format according to Unicode 6.0.
     * @see com.google.common.base.Utf8#isWellFormed(byte[], int, int)
     * @param bytes The byte array to be checked.
     * @param off The starting position in the array to be checked.
     * @param len The length of the portion to be checked.
     *
     * @return true if the byte array is in well formed UTF-8 encoding format, false otherwise
     */
    public static boolean isUtf8WellFormed(byte[] bytes, int off, int len) {
        int end = off + len;
        if (off < 0 || end < off || end > bytes.length) {
            throw new IndexOutOfBoundsException("Illegal input arguments, start: " + off
                                                + ", end: " + end + ", size: " + bytes.length);
        }
        // Look for the first non-ASCII character.
        for (int i = off; i < end; i++) {
            if (bytes[i] < 0) {
                return isWellFormedSlowPath(bytes, i, end);
            }
        }
        return true;
    }

    private static boolean isWellFormedSlowPath(byte[] bytes, int off, int end) {
        int index = off;
        while (true) {
            int byte1;

            // Optimize for interior runs of ASCII bytes.
            do {
                if (index >= end) {
                    return true;
                }
            } while ((byte1 = bytes[index++]) >= 0);

            if (byte1 < (byte) 0xE0) {
                // Two-byte form.
                if (index == end) {
                    return false;
                }
                // Simultaneously check for illegal trailing-byte in leading position
                // and overlong 2-byte form.
                if (byte1 < (byte) 0xC2 || bytes[index++] > (byte) 0xBF) {
                    return false;
                }
            } else if (byte1 < (byte) 0xF0) {
                // Three-byte form.
                if (index + 1 >= end) {
                    return false;
                }
                int byte2 = bytes[index++];
                if (byte2 > (byte) 0xBF
                // Overlong? 5 most significant bits must not all be zero.
                    || (byte1 == (byte) 0xE0 && byte2 < (byte) 0xA0)
                    // Check for illegal surrogate codepoints.
                    || (byte1 == (byte) 0xED && (byte) 0xA0 <= byte2)
                    // Third byte trailing-byte test.
                    || bytes[index++] > (byte) 0xBF) {
                    return false;
                }
            } else {
                // Four-byte form.
                if (index + 2 >= end) {
                    return false;
                }
                int byte2 = bytes[index++];
                if (byte2 > (byte) 0xBF
                // Check that 1 <= plane <= 16. Tricky optimized form of:
                // if (byte1 > (byte) 0xF4
                //     || byte1 == (byte) 0xF0 && byte2 < (byte) 0x90
                //     || byte1 == (byte) 0xF4 && byte2 > (byte) 0x8F)
                    || (((byte1 << 28) + (byte2 - (byte) 0x90)) >> 30) != 0
                    // Third byte trailing-byte test
                    || bytes[index++] > (byte) 0xBF
                    // Fourth byte trailing-byte test
                    || bytes[index++] > (byte) 0xBF) {
                    return false;
                }
            }
        }
    }
}
