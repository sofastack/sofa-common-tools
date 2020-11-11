package com.alipay.sofa.common.tracer;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author huzijie
 * @version TracerIdConverter.java, v 0.1 2020年11月11日 5:40 下午 huzijie Exp $
 */
public class TracerIdConverterTest {

    @Test
    public void testTraceIdConverter () throws NoSuchFieldException, IllegalAccessException {
        TracerIdConverter converter = TracerIdConverter.getInstance();
        String traceId = converter.traceIdSafari(Thread.currentThread());
        Assert.assertEquals("sampleTraceId", traceId);
        Field field = TracerIdConverter.class.getDeclaredField("tracerIdAdapter");
        field.setAccessible(true);
        field.set(converter, null);
        traceId = converter.traceIdSafari(Thread.currentThread());
        Assert.assertNull(traceId);
    }
}
