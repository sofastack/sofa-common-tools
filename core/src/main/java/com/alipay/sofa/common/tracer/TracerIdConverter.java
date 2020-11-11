package com.alipay.sofa.common.tracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * use custom {@link TracerIdAdapter} to find trace id
 * @author huzijie
 * @version TracerAdapter.java, v 0.1 2020年11月11日 5:29 下午 huzijie Exp $
 */
public class TracerIdConverter {

    private static final Logger logger = LoggerFactory.getLogger(TracerIdConverter.class);

    private static final TracerIdConverter INSTANCE = new TracerIdConverter();

    private TracerIdAdapter tracerIdAdapter;

    private TracerIdConverter() {
        ServiceLoader<TracerIdAdapter> serviceLoader = ServiceLoader.load(TracerIdAdapter.class);
        Iterator<TracerIdAdapter> tracerIdAdapterIterator = serviceLoader.iterator();
        if (tracerIdAdapterIterator.hasNext()) {
            this.tracerIdAdapter = tracerIdAdapterIterator.next();
            logger.info("TracerIdConverter use tracerIdAdapter '{}'", tracerIdAdapter.getClass().getName());
        } else {
            logger.info("TracerIdConverter can not find any tracerIdAdapter");
        }
    }

    public static TracerIdConverter getInstance() {
        return INSTANCE;
    }

    public String traceIdSafari(Thread t) {
        if (tracerIdAdapter == null) {
            return null;
        }
        try {
            return tracerIdAdapter.getTracerId(t);
        } catch (Exception e) {
            //ignore
            return null;
        }
    }
}
