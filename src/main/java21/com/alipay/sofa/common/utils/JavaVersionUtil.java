package com.alipay.sofa.common.utils;

/**
 * @author huzijie
 * @version JavaVersionUtil.java, v 0.1 2023年11月20日 2:33 PM huzijie Exp $
 */
public class JavaVersionUtil {

    public static String version() {
        return Runtime.version().toString();
    }
}
