package com.alipay.sofa.common.tracer;

/**
 * @author huzijie
 * @version SampleTracerIdAdapter.java, v 0.1 2020年11月11日 5:40 下午 huzijie Exp $
 */
public class SampleTracerIdAdapter implements TracerIdAdapter{

    @Override
    public String getTracerId(Thread thread) {
        return "sampleTraceId";
    }
}
