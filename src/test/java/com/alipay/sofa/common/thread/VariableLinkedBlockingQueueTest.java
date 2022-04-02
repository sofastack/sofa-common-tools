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

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class VariableLinkedBlockingQueueTest {

    @Test
    public void commonTest() throws Exception {
        VariableLinkedBlockingQueue<String> queue = new VariableLinkedBlockingQueue<>(Arrays.asList("a"));
        Assert.assertEquals(1, queue.size());

        //confirm remaining capacity
        queue.setCapacity(3);
        Assert.assertEquals(2, queue.remainingCapacity());

        //write and read data to queue
        queue.put("b");
        queue.offer("c");
        Assert.assertEquals("a", queue.peek());
        Assert.assertEquals("a", queue.take());
        Assert.assertEquals("b", queue.poll());
        Assert.assertEquals("c", queue.poll(1, TimeUnit.MILLISECONDS));

        queue.offer("a");
        Assert.assertEquals(1, queue.toArray().length);
        Assert.assertEquals("a", queue.toArray(new String[0])[0]);

        List<String> anotherList = new ArrayList<>();
        queue.drainTo(anotherList);
        Assert.assertEquals("a", anotherList.get(0));
        Assert.assertEquals(0, queue.size());

        queue = new VariableLinkedBlockingQueue<>(1);
        queue.offer("a", 1, TimeUnit.MILLISECONDS);
        anotherList.clear();
        queue.drainTo(anotherList, 1);
        Assert.assertEquals("a", anotherList.get(0));
        Assert.assertEquals(0, queue.size());

        queue = new VariableLinkedBlockingQueue<>(1);
        queue.offer("a");
        Iterator<String> it = queue.iterator();
        while (it.hasNext()) {
            Assert.assertEquals("a", it.next());
            it.remove();
        }
        queue = new VariableLinkedBlockingQueue<>(1);
        queue.offer("a");
        queue.toString();
        Assert.assertTrue(queue.remove("a"));
        queue.clear();

        //cover timeout logic
        queue = new VariableLinkedBlockingQueue<>(1);
        queue.offer("a");
        //offer timeout
        queue.offer("a", 1, TimeUnit.MILLISECONDS);
        Thread.sleep(3);
        queue.poll();
        queue.poll();
        //poll timeout
        queue.poll(1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSerializable() throws Exception {
        BlockingQueue<String> queue = new VariableLinkedBlockingQueue<>(1);
        queue.offer("a");
        FileOutputStream fileOut = new FileOutputStream("/tmp/queue.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(queue);
        out.close();
        fileOut.close();

        FileInputStream fileIn = new FileInputStream("/tmp/queue.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        queue = (VariableLinkedBlockingQueue) in.readObject();
        in.close();
        fileIn.close();
        Assert.assertEquals("a", queue.poll());
    }
}
