package com.alipay.sofa.common.log.factory;

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.SpaceInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/30
 */
public class UpdateConsoleLoggerWhiteSetTest {
    private static final String spaceName = "com.alipay.sofa";

    @Test
    public void test() {
        SpaceId spaceId = new SpaceId(spaceName);
        SpaceInfo spaceInfo = new SpaceInfo();
        new LoggerSpaceFactory4LogbackBuilder(spaceId, spaceInfo).getSpaceLogConfigFileURL(this.getClass().getClassLoader(), "com.alipay.sofa");
        Assert.assertEquals(2, CommonLoggingConfigurations.getLoggerConsoleWhiteSet().size());
        Assert.assertTrue(CommonLoggingConfigurations.getLoggerConsoleWhiteSet().contains("testLoggerName1"));
        Assert.assertTrue(CommonLoggingConfigurations.getLoggerConsoleWhiteSet().contains("testLoggerName2"));
    }
}
