package com.alipay.sofa.common.tracer;

/**
 * Adapter for different tracer system
 * @author huzijie
 * @version TracerAdpater.java, v 0.1 2020年11月11日 5:31 下午 huzijie Exp $
 */
public interface TracerIdAdapter {

    /**
     * get trace id from the thread
     * @param thread the thread
     * @return the trace Id
     */
    String getTracerId(Thread thread);
}
