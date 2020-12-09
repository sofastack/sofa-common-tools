# sofa-common-tools

![build](https://github.com/sofastack/sofa-common-tools/workflows/build/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/alipay/sofa-common-tools/badge.svg?branch=master)](https://coveralls.io/github/sofastack/sofa-common-tools?branch=master) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![maven](https://img.shields.io/github/release/alipay/sofa-common-tools.svg)](https://github.com/sofastack/sofa-common-tools/releases)

`sofa-common-tools` is a common dependency of SOFAStack middleware, it provides:
1. Separate log space for application and middleware
2. SOFA thread 
 
The audience of this library is middleware and SDK developer.

**Note:** Since version 1.2.0, sofa-common-tools don't support JDK 1.6 anymore. 

## Background

In daily developing, Java logging usually consists of choosing a log facade (e.g., JCL and SLF4j) and log implementation (e.g., Log4j2 and logback).
Say you are developing an application that uses a JAR which utilizes log4j2 for logging.
In such scenario, you cannot choose log implementation other than log4j2 (log implementation conflicts if you choose Logback).
Some solutions available:
1. The jar uses log facade instead log implementation but application developers still have to provide log configuration
2. The jar initialize loggers and appenders programmatically (This works well in Multi-ClassLoader environment where middleware/SDK developers handle many repeated work)
3. Application resort to same log implementation as the JAR and provide also log configuration

None of the above solutions is perfect, `sofa-common-tools` provides a Midas touch: middleware/SDK developers print logs using *only* facade and hand the right to select whichever log implementation to application developer.
At the mean time, middleware/SDK developers provide log configurations per log implementation.
`sofa-common-tools` detects automatically the log implementation and initializes appenders and loggers for middleware/SDK.
To differentiate SDKs/middlewares, each jar has its own log context and log space identifiable via `SpaceID` in `sofa-common-tools`. 

Some notes:
- `sofa-common-tools` only supports SLF4j facade currently

## Quick Start
Say you are developing an OCR SDK for downstream to integrate. First, you choose `com.alipay.sdk.ocr` as your log space.
Second, define a logger factory to retrieve all the loggers you need:

```java
import org.slf4j.Logger;
import com.alipay.sofa.common.log.LoggerSpaceManager;

public class AlipayOcrLoggerFactory {
    private static final String OCR_LOGGER_SPACE = "com.alipay.sdk.ocr";

    public static Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return LoggerSpaceManager.getLoggerBySpace(name, OCR_LOGGER_SPACE);
    }

    public static Logger getLogger(Class<?> klass) {
        if (klass == null) {
            return null;
        }

        return getLogger(klass.getCanonicalName());
    }
}
```

Third, create log configuration for your log space in classpath (space name `com.alipay.sdk.ocr` maps to `com/alipay/sdk/ocr/log/` ), for example
```
$ cd com/alipay/sdk/ocr/log && tree
.
├── log4j
│   └── log-conf.xml
├── log4j2
│   └── log-conf.xml
└── logback
    └── log-conf.xml
```

The directory name is quite self-evident. If application choose a log implementation you don't configure, error will be thrown.

A sample configuration for logback `logback/log-conf.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder charset="UTF-8">
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.alipay.foo" level="INFO" additivity="false">
        <appender-ref ref="stdout"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="stdout"/>
    </root>
</configuration>
```

Lastly, just logging
```java
public class Main {
    public static void main(String[] args) {
        Logger ocrLogger = AlipayOcrLoggerFactory.getLogger("com.alipay.foo");
        ocrLogger.info("hello world");
    }
}
```

In console, the following log will be printed:
```
17:42:41.083 [main] INFO com.alipay.foo - hello world
```

## Configuration
The configuration of corresponding logging implementation can be parameterized, that is to say, placeholders are allowed in XML file.
By default, `sofa-common-tools` provides following parameters with sensible default values:

|Parameter|Default value|
|---|---|
|logging.path| ${user.home} |
|file.encoding|UTF-8 |
|logging.level.{spaceName}| INFO |
|logging.path.{spaceName}|${logging.path}|

Application is able to override the value through JVM options, e.g., `-Dlogging.path=/home/admin`.

### Customized Parameter
Middlewares/SDKs can defined customized parameters for xml placeholders as well.
Those parameters must be initialized before using:
```java
import org.slf4j.Logger;
import com.alipay.sofa.common.log.LoggerSpaceManager;import java.util.HashMap;

public class AlipayOcrLoggerFactory {
    private static final String OCR_LOGGER_SPACE = "com.alipay.sdk.ocr";

    static {
        // Note: this step is important, as in Ark environment your SDK may be used in module dependency
        // and will be initialized multiple times.
        if (!MultiAppLoggerSpaceManager.isSpaceInitialized(OCR_LOGGER_SPACE)) {
            Map spaceIdProperties = new HashMap<String, String>();
            // Initialize your parameters here
            MultiAppLoggerSpaceManager.init(OCR_LOGGER_SPACE, spaceIdProperties);
        }
    }

    public static Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return LoggerSpaceManager.getLoggerBySpace(name, OCR_LOGGER_SPACE);
    }

    public static Logger getLogger(Class<?> klass) {
        if (klass == null) {
            return null;
        }

        return getLogger(klass.getCanonicalName());
    }
}
```

### Debugging
1. The logging ability can be disabled totally through `sofa.middleware.log.disable` JVM option (Of course for middleware/SDK jar using `sofa-common-tools`).
2. Debugging with specific log implementation, lock-down of other log implementation, e.g., `-Dlogback.middleware.log.disable=true` disables logback. All supported switch:
    - log4j.middleware.log.disable
    - log4j2.middleware.log.disable
    - logback.middleware.log.disable

### Miscellaneous
- sofa.middleware.log.disable, defaults to `false`
- logback.middleware.log.disable, defaults to `false`
- log4j2.middleware.log.disable, defaults to `false`
- log4j.middleware.log.disable, defaults to `false`

#### LogLog
`sofa-common-tools` uses internally `System.out` for logging, logging level can be set via JVM option `sofa.middleware.log.internal.level`.

#### Console logging
- Global configuration
    - Switch `sofa.middleware.log.console` toggles console logging for all middleware/SDK, defaults to `false`
    - `sofa.middleware.log.console.level` configures log level globally
- Independent middleware/SDK configuration
    + Switch `sofa.middleware.log.${spaceid}.console` toggles console logging for corresponding middleware/SDK, defaults to `false`
    + `sofa.middleware.log.{space id}.console.level` configures log level correspondingly, which overrides global log level  

##### Logging pattern
+ logback: `sofa.middleware.log.console.logback.pattern` defaults to `%d{yyyy-MM-dd HH:mm:ss.SSS} %5p ${PID:- } --- [%15.15t] %-40.40logger{39} : %m%n`
+ log4j2: `sofa.middleware.log.console.log4j2.pattern` defaults to `%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %X{PID} --- [%15.15t] %-40.40logger{39} : %m%n`

Console logging options can be passed through JVM option or Spring Boot `properties`.

## Compiling
Maven 3.2.5+, JDK Version 1.6+

## LICENSE

[Apache 2.0](./LICENSE)

## Contribution

[Contribution Guide](./CONTRIBUTING.md)





