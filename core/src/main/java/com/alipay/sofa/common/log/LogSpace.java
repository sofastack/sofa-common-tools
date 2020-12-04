package com.alipay.sofa.common.log;

import com.alipay.sofa.common.log.factory.AbstractLoggerSpaceFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class LogSpace {
    private AbstractLoggerSpaceFactory abstractLoggerSpaceFactory;
    private Properties properties;
    private ClassLoader spaceClassloader;

    public LogSpace() {
        properties = new Properties();
    }

    public LogSpace(Map<String, String> map, ClassLoader spaceClassloader) {
        this();
        this.spaceClassloader = spaceClassloader;
        putAll(map);
    }

    public ClassLoader getSpaceClassloader() {
        return spaceClassloader;
    }

    public void setSpaceClassloader(ClassLoader spaceClassloader) {
        this.spaceClassloader = spaceClassloader;
    }

    public AbstractLoggerSpaceFactory getAbstractLoggerSpaceFactory() {
        return abstractLoggerSpaceFactory;
    }

    public void setAbstractLoggerSpaceFactory(AbstractLoggerSpaceFactory abstractLoggerSpaceFactory) {
        this.abstractLoggerSpaceFactory = abstractLoggerSpaceFactory;
    }

    public Properties properties() {
        return properties;
    }

    public LogSpace putAll(Map<String, String> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public LogSpace setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
