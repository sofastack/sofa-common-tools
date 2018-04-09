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

/**
 *
 * @author luoguimu123
 * @version $Id: StreamUtil.java, v 0.1 2017年08月01日 下午12:04 luoguimu123 Exp $
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class StreamUtil {

    public StreamUtil() {
    }

    public static void io(InputStream in, OutputStream out) throws IOException {
        io((InputStream) in, (OutputStream) out, -1);
    }

    public static void io(InputStream in, OutputStream out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = 8192;
        }

        byte[] buffer = new byte[bufferSize];

        int amount;
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }

    }

    public static void io(Reader in, Writer out) throws IOException {
        io((Reader) in, (Writer) out, -1);
    }

    public static void io(Reader in, Writer out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = 4096;
        }

        char[] buffer = new char[bufferSize];

        int amount;
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }

    }

    public static OutputStream synchronizedOutputStream(OutputStream out) {
        return new StreamUtil.SynchronizedOutputStream(out);
    }

    public static OutputStream synchronizedOutputStream(OutputStream out, Object lock) {
        return new StreamUtil.SynchronizedOutputStream(out, lock);
    }

    public static String readText(InputStream in) throws IOException {
        return readText(in, (String) null, -1);
    }

    public static String readText(InputStream in, String encoding) throws IOException {
        return readText(in, encoding, -1);
    }

    public static String readText(InputStream in, String encoding, int bufferSize)
                                                                                  throws IOException {
        Reader reader = encoding == null ? new InputStreamReader(in) : new InputStreamReader(in,
            encoding);
        return readText(reader, bufferSize);
    }

    public static String readText(Reader reader) throws IOException {
        return readText(reader, -1);
    }

    public static String readText(Reader reader, int bufferSize) throws IOException {
        StringWriter writer = new StringWriter();
        io((Reader) reader, (Writer) writer, bufferSize);
        return writer.toString();
    }

    private static class SynchronizedOutputStream extends OutputStream {
        private OutputStream out;
        private Object       lock;

        SynchronizedOutputStream(OutputStream out) {
            this(out, out);
        }

        SynchronizedOutputStream(OutputStream out, Object lock) {
            this.out = out;
            this.lock = lock;
        }

        public void write(int datum) throws IOException {
            synchronized (this.lock) {
                this.out.write(datum);
            }
        }

        public void write(byte[] data) throws IOException {
            synchronized (this.lock) {
                this.out.write(data);
            }
        }

        public void write(byte[] data, int offset, int length) throws IOException {
            synchronized (this.lock) {
                this.out.write(data, offset, length);
            }
        }

        public void flush() throws IOException {
            synchronized (this.lock) {
                this.out.flush();
            }
        }

        public void close() throws IOException {
            synchronized (this.lock) {
                this.out.close();
            }
        }
    }
}
