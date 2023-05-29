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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * @author huzijie
 * @version LegacySpringBeanUtilTest.java, v 0.1 2023年05月26日 10:42 AM huzijie Exp $
 */
public class LegacySpringBeanUtilTest {

    @Test
    public void testCopyProperties() {
        TestBean tb = new TestBean();
        tb.setName("rod");
        tb.setAge(32);
        tb.setTouchy("touchy");
        TestBean tb2 = new TestBean();
        Assert.assertNull(tb2.getName());
        Assert.assertEquals(0, tb2.getAge());
        Assert.assertNull(tb2.getTouchy());
        BeanUtils.copyProperties(tb, tb2);
        Assert.assertEquals(tb2.getName(), tb.getName());
        Assert.assertEquals(tb2.getAge(), tb.getAge());
        Assert.assertEquals(tb2.getTouchy(), tb.getTouchy());
    }

    @Test
    public void testCopyPropertiesWithIgnore() {
        TestBean tb = new TestBean();
        Assert.assertNull(tb.getName());
        tb.setAge(32);
        tb.setTouchy("bla");
        TestBean tb2 = new TestBean();
        tb2.setName("rod");
        Assert.assertEquals(tb2.getAge(), 0);
        Assert.assertNull(tb2.getTouchy());

        // "spouse", "touchy", "age" should not be copied
        BeanUtils.copyProperties(tb, tb2, "spouse", "touchy", "age");
        Assert.assertNull(tb.getName());
        Assert.assertEquals(tb2.getAge(), 0);
        Assert.assertNull(tb2.getTouchy());
    }

    @Test
    public void testCopyPropertiesWithIgnoredNonExistingProperty() {
        NameAndSpecialProperty source = new NameAndSpecialProperty();
        source.setName("name");
        TestBean target = new TestBean();
        BeanUtils.copyProperties(source, target, "specialProperty");
        Assert.assertEquals("name", target.getName());
    }

    @Test
    public void testCopyPropertiesWithInvalidProperty() {
        InvalidProperty source = new InvalidProperty();
        source.setName("name");
        source.setFlag1(true);
        source.setFlag2(true);
        InvalidProperty target = new InvalidProperty();
        BeanUtils.copyProperties(source, target);
        Assert.assertEquals(target.getName(), "name");
        Assert.assertTrue(target.getFlag1());
        Assert.assertTrue(target.getFlag2());
    }

    private static class TestBean {

        private String name;

        private int    age;

        private String touchy;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getTouchy() {
            return touchy;
        }

        public void setTouchy(String touchy) {
            this.touchy = touchy;
        }
    }

    @SuppressWarnings("unused")
    private static class NameAndSpecialProperty {

        private String name;

        private int    specialProperty;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setSpecialProperty(int specialProperty) {
            this.specialProperty = specialProperty;
        }

        public int getSpecialProperty() {
            return specialProperty;
        }
    }

    @SuppressWarnings("unused")
    private static class InvalidProperty {

        private String  name;

        private String  value;

        private boolean flag1;

        private boolean flag2;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setValue(int value) {
            this.value = Integer.toString(value);
        }

        public String getValue() {
            return this.value;
        }

        public void setFlag1(boolean flag1) {
            this.flag1 = flag1;
        }

        public Boolean getFlag1() {
            return this.flag1;
        }

        public void setFlag2(Boolean flag2) {
            this.flag2 = flag2;
        }

        public boolean getFlag2() {
            return this.flag2;
        }
    }
}
