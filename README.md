## sofa-common-tools

![](https://travis-ci.org/alipay/sofa-common-tools.svg?branch=master) 
[![Coverage Status](https://coveralls.io/repos/github/alipay/sofa-common-tools/badge.svg?branch=master)](https://coveralls.io/github/sofastack/sofa-common-tools?branch=master) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
![maven](https://img.shields.io/badge/maven-v1.0.12-blue.svg)

`sofa-common-tools` 是 SOFAStack 中间件依赖的一个通用工具包，通过自动感知应用的日志实现，提供中间件与应用隔离的日志空间打印能力。

## 一、背景

在日常开发中，应用避免不了都会打印日志，可能采用的通用日志接口开发框架 SLF4J，而对于具体的日志实现最常用的是 Logback、Log4j2 或者 Log4j。假设应用依赖的二方包其要使用的日志实现是 Log4j2，而应用一直使用的日志实现是 Log4j，而当应用要集成这个二方包时会发现由于两边依赖的日志实现不同引入的冲突无法解决，从而导致集成失败。

为了解决上面描述的问题，常用的几种办法是：

1. 二方包修改日志实现依赖，将其改为只依赖 Log4j 的实现类；或者改为面向 SLF4J 的编程接口打印日志，然后在集成应用中配置相应的 `appender` 和 `logger` 以确定日志输出目录和文件。
2. 应用修改，修改为使用 Log4j2 的日志打印方式，并同时在集成应用中配置相应的 `appender` 和 `logger` 以确定日志输出目录和文件。

不管是两种方法中的哪一种，都需要修改相应的日志实现以统一到使用相同的日志实现去打印并同时需要配置日志文件的输出目录。那么有没有一种办法，在同样的一个 class path 下，都是由同一个 ClassLoader 加载的类，保证二方包或者引入的中间件其不用任何配置就可以完成**日志文件相对固定目录的输出**并能够**统一日志实现**？或者说引入的二方包遵循一定的标准进行配置后，能够感知到 Logback、Log4j2 或者 Log4j 并能够将日志文件输出在相对固定的目录？

答案是有的，开源的此 `sofa-common-tools` 就是在框架层面提供了解决方案，即二方包或者引入的中间件也只面向日志编程接口 SLF4J 去编程不直接使用具体日志实现的 API，具体的日志实现的选择权利交给应用开发者去选择，同时二方包或者中间件针对每一个日志实现提供了配置以输出日志到相对固定目录和文件。应用选择哪一个日志实现，这个框架就自动发现并选择应用开发者的日志实现进行打印并输出到相对固定目录和文件。

根据 `sofa-common-tools` 的规范对常用日志实现（Logback、Log4j2 和 Log4j）均进行配置（配置日志输出文件和格式），当应用引入二方包或者中间件时，根据应用中已有的日志实现并选择该日志实现能够正确解析的配置文件来初始化完成二方包或者中间件的日志配置来完成日志的输出，并同时不会和某一个具体日志实现绑定。从而完成在不用业务配置额外的 `appender` 和 `logger` 或者业务不用修改任何配置的情况下，完成日志空间打印的隔离能力。
 
*前提：需要统一日志编程接口到 [SLF4J](https://www.slf4j.org/index.html)*

## 二、使用场景和快速开始

### 2.1 使用场景和目标

期望达到日志打印隔离的目的：使用此日志打印隔离框架的中间件或者二方库的依赖中不直接使用具体日志实现的 API 或者说不会和某一个日志实现绑定，避免干扰将要接入的应用指定的日志实现，但中间件或者二方库能够自动发现应用环境（或者说 classpath）内引入的日志实现，Logback、Log4j2 或者 Log4j,并按照相应的优先级只使用其中一份配置进行日志输出。

### 2.2 快速开始

假设 RPC 接入，并定义的 `SpaceId` 的关键属性是 `com.alipay.sofa.rpc`。

首先，需要根据 `SpaceId` 和日志打印隔离框架的关键 API 自定义一个 LoggerFactory，如：

```java
public class RpcLoggerFactory {

    private static final String RPC_LOG_SPACE = "com.alipay.sofa.rpc";

    static {
        //SpaceId init properties
        Map spaceIdProperties = new HashMap<String, String>();
        MultiAppLoggerSpaceManager.init(RPC_LOG_SPACE, spaceIdProperties);
    }

    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }

    public static org.slf4j.Logger getLogger(String name) {
        //From "com/alipay/sofa/rpc/log" get the xml configuration and init,then get the logger object
        return MultiAppLoggerSpaceManager.getLoggerBySpace(name, RPC_LOG_SPACE);
    }
}
```

定义的 RpcLoggerFactory 主要定义自己的 `SpaceId` 并通过 `MultiAppLoggerSpaceManager.init` 完成初始化，同时抽象出一个 RpcLoggerFactory 方便代码中直接复用，初始化参数中可以针对当前的 `SpaceId` 设置一些属性参数用于在解析配置文件时完成对相应属性占位符的值替换。当然也提供了简化版的 API 即 `LoggerSpaceManager` 大家也可以使用。

其次，在指定的 `SpaceId` 资源路径下编写针对不同日志实现 Logback、Log4j2 和 Log4j 的日志配置文件，具体的文件路径为：

```
└── com
    └── alipay
        └── sofa
            └── rpc
                └── log
                    ├── log4j
                    │   └── log-conf.xml
                    ├── log4j2
                    │   └── log-conf.xml
                    └──  logback
                        └── log-conf.xml
```

需要注意的是在每一个日志实现目录标识下的配置文件都编写对应日志实现能够解析的配置，如 `logback` 目录下的配置 `log-conf.xml` 应该是 Logback 能够解析的配置文件，否则将会报错。 

最后，直接使用并测试验证

```java
public class LoggerSpaceManagerUsage {
    public static void main(String[] args) {
        Logger rpcLogger = RpcLoggerFactory.getLogger("com.alipay.foo");
        rpcLogger.debug("hello world");
    }
}
```

如果 classpath 中引用的是 Logback 依赖作为日志打印框架，并在 `logback/log-conf.xml` 日志打印文件配置内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder charset="UTF-8">
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.foo.Bar" level="${logging.level.com.alipay.sofa.rpc}" additivity="false">
        <appender-ref ref="stdout"/>
    </logger>

    <root level="DEBUG">
        <appender-ref ref="stdout"/>
    </root>
</configuration>
```

控制台中包含如下的日志打印内容：

```
Sofa-Middleware-Log SLF4J : Actual binding is of type [ com.alipay.sofa.rpc Logback ]
17:42:41.083 [main] DEBUG com.alipay.foo - hello world
```

## 三、功能特性

### 3.1 日志配置的系统属性

|变量名|在日志 xml 配置文件中使用样式|默认值|系统属性指定|
|---|---|---|---|
|logging.path|${logging.path}| ${user.home} |-Dlogging.path=/home/admin/logs|
|file.encoding|${file.encoding} |UTF-8 |-Dfile.encoding=UTF-8|
|logging.level.{spaceName} ，以 RPC 为例：logging.level.com.alipay.sofa.rpc|${logging.level.com.alipay.sofa.rpc}| INFO | -Dlogging.level.com.alipay.sofa.rpc=WARN|
|logging.path.{spaceName} ，以 RPC 为例：logging.path.com.alipay.sofa.rpc |${logging.path.com.alipay.sofa.rpc}|${logging.path.com.alipay.sofa.rpc}|-Dlogging.path.com.alipay.sofa.rpc=/home/admin/logs/appname|

### 3.2 关闭日志实现打印独立功能

1. 关闭日志实现独立打印能力:通过系统属性 `-Dsofa.middleware.log.disable=true`。也许你暂时不希望二方库或者中间件的日志打印到独立目录下，希望集中打印方便本地开发和跑测试用例时集中显示，那么可以借助上面开关，关闭日志独立打印能力,注意这个是全部关闭即禁用掉中间件的独立日志空间功能。

2. 关闭指定日志开关:作用是可以在大家完成编码,进行单元测试的时候,在所有的日志实现都引入 classpath后(当然都是 scope 为 test 的引入),可以采用这种方法自动化测试用例，如下面这种方式关闭掉 Logback 和 Log4j2，就可以单独测试在独立的日志空间下 Log4j 的正确使用:

```java
//禁用logback
System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
//禁用log4j
System.setProperty(Constants.LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
```
对应的几个日志实现独立打印能力的单独系统属性开关分别如下，设置相应的系统属性为 `true` 机会关闭对应的日志实现单独打印能力:

```java
//Class : com.alipay.sofa.common.log.Constants
//禁用log4j日志实现
String LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY = "log4j.middleware.log.disable";
//禁用log4j2日志实现
String LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY ="log4j2.middleware.log.disable";
//禁用logback日志实现
String LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY = "logback.middleware.log.disable";
```

### 3.3 提供动态改变日志级别的能力

考虑到主流的日志实现框架 Logback、Log4j2 和 Log4j 定义的日志级别并且没有统一起来，即没有定义一个统一的日志级别管控 API，这样就导致在 slf4j-api 这个接口层面无法提供统一的一个入口作为日志级别标准。但是在某种情形下需要去动态改变日志级别，所以在 `sofa-common-toos` 中提供了基于`SpaceId` 和 `LoggerName` 的日志级别改变能力。

`sofa-common-tools`日志级别定义为一个枚举类型，通过这个枚举类型并根据具体的日志实现，映射到具体的日志实现的级别上，通过此适配器方式来屏蔽不同日志实现所定义的级别差异：

具体提供的 API 为：

```java
com.alipay.sofa.common.log.LoggerSpaceManager#setLoggerLevel
```

而对应的日志枚举级别为：`com.alipay.sofa.common.log.adapter.level.AdapterLevel`，提供如下几种级别的适配：

```java
public enum AdapterLevel {

    /**
     * An error in the application, possibly recoverable.
     */
    ERROR("error"),

    /**
     * An event that might possible lead to an error.
     */
    WARN("warn"),

    /**
     * An event for informational purposes.
     */
    INFO("info"),

    /**
     * A general debugging event.
     */
    DEBUG("debug"),

    /**
     * A fine-grained debug message, typically capturing the flow through the application.
     */
    TRACE("trace");
}
```

## 四、LICENSE

[Apache 2.0](./LICENSE)

## 五、Contribution

[Contribution Guide](./CONTRIBUTING.md)





