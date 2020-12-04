package com.alipay.sofa.common.space;

import com.alipay.sofa.common.log.LogSpace;
import com.alipay.sofa.common.thread.space.ThreadPoolSpace;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class Space {
    private LogSpace logSpace;
    private ThreadPoolSpace threadPoolSpace;

    public LogSpace getLogSpace() {
        return logSpace;
    }

    public void setLogSpace(LogSpace logSpace) {
        this.logSpace = logSpace;
    }

    public ThreadPoolSpace getThreadPoolSpace() {
        return threadPoolSpace;
    }

    public void setThreadPoolSpace(ThreadPoolSpace threadPoolSpace) {
        this.threadPoolSpace = threadPoolSpace;
    }
}
