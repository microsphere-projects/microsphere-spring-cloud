[English](../commons.md) · [0.2.x 版本线](../../current/zh/commons.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-commons` — 用户指南（0.1.x）

面向 Spring Boot 2.x 应用的 Spring Cloud Commons 增强：更完善的服务注册生命周期
（事件、定制器、多注册中心）、用于手动上下线的 Actuator 端点、union/响应式服务发现
组合、Web 映射元数据注入、`/actuator/features` 特性目录，以及可运行时调整的
内嵌 Tomcat——全部按需启用、全部带类存在条件保护。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release-1.x` |
| 版本线 | 0.1.x |
| 最新已发布版本 | **`0.1.26`**（分支当前开发版本为 `0.1.27-SNAPSHOT`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-commons` |
| 仓库 | Maven Central |
| Java 基线 | Java 8 |
| Servlet 命名空间 | `javax.*` |
| Spring Boot 范围 | 2.3.x – 2.6.x |
| Spring Cloud 范围 | Hoxton.SR12 – 2021.0.x |
| 自动配置注册 | `META-INF/spring.factories`（Boot 2 机制；jar 中同时附带 `AutoConfiguration.imports`，但 Boot 2.3–2.6 会忽略它） |

## 依赖引入

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-commons</artifactId>
    <version>0.1.26</version> <!-- 或通过 0.1.x BOM / 父 POM 统一管理 -->
</dependency>
```

本模块的所有第三方依赖（`spring-cloud-commons`、`spring-cloud-context`、各注册中心
starter、`spring-boot-starter-web`/`-webflux`/`-actuator` 等）均声明为 **optional**；
模块仅强依赖 Microsphere Spring Boot 0.1.34 各子模块与 `aspectjweaver`。你需要自带
Spring Boot / Spring Cloud / 注册中心客户端；每个特性只在其相关类位于你的 classpath
时才启用，否则静默降级。

---

## 1. 注册生命周期事件

**适用场景：** 在不接触注册中心专有代码的前提下，对**任意** Spring Cloud
`ServiceRegistry`（Eureka、Nacos、Consul、Zookeeper……）的每次注册/注销调用做审计、
日志或逻辑挂钩。

`ServiceRegistryAutoConfiguration` 始终导入 `EventPublishingRegistrationAspect`
（需要 AspectJ，由模块的强依赖 `aspectjweaver` 提供）。该切面拦截
`ServiceRegistry.register(..)` / `deregister(..)` 并发布：

| 事件 | 发布时机 | Type 枚举 |
|---|---|---|
| `RegistrationPreRegisteredEvent` | `register` 之前 | `PRE_REGISTERED` |
| `RegistrationRegisteredEvent` | `register` 之后 | `REGISTERED` |
| `RegistrationPreDeregisteredEvent` | `deregister` 之前 | `PRE_DEREGISTERED` |
| `RegistrationDeregisteredEvent` | `deregister` 之后 | `DEREGISTERED` |

均继承 `RegistrationEvent`，暴露 `getRegistration()`、`getRegistry()`，以及
`isPreRegistered()`、`isRegistered()`、`isPreDeregistered()`、`isDeregistered()`。

```java
@Component
public class RegistrationLogger {

    @EventListener
    public void onRegistered(RegistrationRegisteredEvent event) {
        Registration r = event.getRegistration();
        System.out.println("Registered " + r.getServiceId() + " at " + r.getHost() + ":" + r.getPort());
    }
}
```

### `RegistrationCustomizer`

`public interface RegistrationCustomizer { void customize(Registration registration); }`
会在每次注册调用**之前**（pre-registered 阶段）作用于注册对象。注册该类型的 bean 即可
打补丁元数据、实例 id 等：

```java
@Component
public class ZoneCustomizer implements RegistrationCustomizer {
    @Override
    public void customize(Registration registration) {
        registration.getMetadata().put("zone", "sh-a");
    }
}
```

事件由**各个具体**注册中心发布：当[多注册中心](#2-多注册中心聚合)启用时，切面会忽略
`MultipleServiceRegistry` 类型的 bean，避免重复发布。

另外，模块定义了 `ServiceInstancesChangedEvent`
（`getServiceName()`、`getServiceInstances()`、`processed()` / `isProcessed()`），
供其他模块发布消费；本模块自身不会自动发布它。

---

## 2. 多注册中心聚合

**适用场景：** 一个应用需要**同时注册到两个及以上注册中心**（如 Nacos + Zookeeper），
而 Spring Cloud 其余部分仍只看到一个 `Registration` / `ServiceRegistry`。

该特性**需要显式开启**：

```properties
microsphere.spring.cloud.multiple-registration.enabled=true
```

开启后（且至少存在一个 `Registration` bean 和一个 `ServiceRegistry` bean 时）安装三个
`@Primary` bean：

| Bean | 职责 |
|---|---|
| `MultipleRegistration` | 组合 `Registration`；id/service/host/port/secure/URI 委托给*默认*注册对象；通过 `special(NacosRegistration.class)` 可取出特定底层注册对象；`getMetadata()` 返回共享的 `RegistrationMetaData`，其每次 `put`/`remove` 都会传播到**所有**底层注册对象的 metadata |
| `MultipleServiceRegistry` | 把 `register`/`deregister`/`setStatus`/`close` 按注册对象类型配对后扇出到每个注册中心；`getStatus` 只读取默认注册中心 |
| `MultipleAutoServiceRegistration` | 让组合对象走标准 `AbstractAutoServiceRegistration` 生命周期 |

哪个注册对象是"默认"取决于 bean 的迭代顺序（**最后一个**胜出）。
`microsphere.spring.cloud.default-registration.type` 与
`microsphere.spring.cloud.default-service-registry.type`（声明 `Class` 值）就是为该
领域定义的显式指定键；当前接线方式请参考
[0.1.x 源码](https://github.com/microsphere-projects/microsphere-spring-cloud/tree/release-1.x)。
相关键 `microsphere.spring.cloud.composite-registration.enabled` 已声明但目前没有任何
自动配置消费它。

---

## 3. 服务注册/注销 Actuator 端点

**适用场景：** 发布前先把实例摘除（优雅摘流），或运维手动把实例重新挂上去，全部走 HTTP。

提供两个 Actuator 端点：

| Endpoint id | 操作 | HTTP 方法 | 生效 URL（模块默认） |
|---|---|---|---|
| `serviceRegistration` | `metadata()` — 状态快照 | GET | `/actuator/microsphere/service/registration` |
| `serviceRegistration` | `start()` — 未运行则注册 | POST | `/actuator/microsphere/service/registration` |
| `serviceDeregistration` | `stop()` — 运行中则注销 | POST | `/actuator/microsphere/service/deregistration` |

两个端点**默认关闭**，路径映射由模块自带的
`META-INF/config/default/endpoints.properties`（以最低优先级默认值加载）设定。
开启并暴露：

```yaml
management:
  endpoint:
    serviceRegistration:
      enabled: true
    serviceDeregistration:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,refresh,serviceRegistration,serviceDeregistration
```

然后：

```bash
# 查看注册状态
curl http://localhost:8080/actuator/microsphere/service/registration

# （重新）注册服务；返回的是*之前*的运行状态
curl -X POST http://localhost:8080/actuator/microsphere/service/registration

# 注销服务；返回的是注销*之前*的状态
curl -X POST http://localhost:8080/actuator/microsphere/service/deregistration
```

GET 响应包含 `application-name`、`registration`、`port`、`status`
（来自 `ServiceRegistry.getStatus`）、`running`、`enabled`、`phase`、`order`、
`managementRegistration`、`config`（可用时）。

> 注意：端点跟踪的 "running" 标志是**静态**字段，在同一 JVM 内跨两个端点实例共享——
> 只能作为粗略指标，注册中心本身才是权威状态。

---

## 4. 简单/内存注册（无需外部注册中心）

**适用场景：** Demo、集成测试，或没有部署真实注册中心、但仍希望拥有 `Registration`
bean、生命周期事件以及上述 Actuator 端点的环境。

```properties
microsphere.spring.cloud.service-registry.auto-registration.simple.enabled=true
```

需显式开启。开启后安装：

- `DefaultRegistration`：由 `spring.application.name`、`server.port` 与第一个非回环
  地址构造的 `Registration`，实例 id 为 `host:port`；
- `InMemoryServiceRegistry`：基于 `ConcurrentMap` 的 `ServiceRegistry`（仅当不存在
  其他 `ServiceRegistry` bean 时）——`register` 写入、`deregister` 删除、
  `setStatus`/`getStatus` 使用 metadata 键 `_status_`。

姊妹原语 `SimpleServiceRegistry` 则把实例写入 `SimpleDiscoveryProperties`（或其
reactive 对应物）的*配置文件实例*表中，使"被发现"的服务与注册内容天然一致且无网络
依赖。

---

## 5. Union 服务发现

**适用场景：** 多个 `DiscoveryClient` 实现并存（如 simple 配置式 + 真实注册中心），
你希望拿到某服务的**全部**实例，而不是组合客户端择一后的结果。

```properties
microsphere.spring.cloud.client.discovery.mode=union
```

安装 `UnionDiscoveryClient`（order = `HIGHEST_PRECEDENCE`），聚合除
`CompositeDiscoveryClient` 和自身以外的所有 `DiscoveryClient` bean。语义：
`getInstances(serviceId)` 顺序拼接所有非空结果（**保留重复**）；`getServices()` 返回
去重并集；`description()` 固定为 `"Union Discovery Client"`。

---

## 6. 响应式发现适配器

**适用场景：** 你的注册中心只提供**响应式**客户端（如 reactive 的 Kubernetes/Consul），
而非响应式代码（Ribbon 时代的负载均衡、0.1.x 版本线的阻塞式 Feign、各类库集成）仍在
调用 `DiscoveryClient`。

全自动、无开关属性。当存在 `ReactiveDiscoveryClient` bean 且 discovery 启用时，
`ReactiveDiscoveryClientAdapter` 会作为一个阻塞 `DiscoveryClient` 注册：订阅 `Flux`
并阻塞收集（在非阻塞线程上调用时走 `mono.toFuture().get()`，否则 `mono.block()`）。

配套工具 `DiscoveryUtils` 支持 `SimpleDiscoveryProperties` ⇄
`SimpleReactiveDiscoveryProperties` 双向转换，保持基于配置文件的服务实例同步。
（1.x 注：拷贝走 `DefaultServiceInstance.setUri(...)`，见
[与 0.2.x 的差异](#与-02x-的差异)。）

---

## 7. 注册元数据中的 Web 信息

**适用场景：** 希望服务发现的元数据消费方知道每个实例的 context path 与 URL 映射
（API 网关工具、契约检查、控制台面板）。

在 Servlet（`WebMvcServiceRegistryAutoConfiguration`）与 Reactive
(`WebFluxServiceRegistryAutoConfiguration`) 应用中，只要有 `Registration` bean 即
自动生效。收到 `WebEndpointMappingsReadyEvent` 后写入注册元数据：

| 元数据键 | 内容 |
|---|---|
| `web.context-path` | `${server.servlet.context-path:}`（WebFlux 下为空串） |
| `web.mappings` | URL 编码后的 `WebEndpointMapping` JSON 数组；内置 `org.springframework.*` filter 映射、DispatcherServlet 映射以及 Actuator 端点（位于 `${management.endpoints.web.base-path:/actuator}` 之下）会被排除 |

0.1.x 版本线的 Web MVC 代码基于 `javax.servlet`（检查的是 `javax.servlet.Filter`
注册项）。读取时使用 `ServiceInstanceUtils.getWebEndpointMappings(serviceInstance)`；
辅助方法 `getUri(instance)` / `getUriString(instance)` 解析
`http(s)://host:port`（端口 ≤ 0 时回退 443/80），`getMetadata` / `setMetadata` /
`removeMetadata` 提供类型化的元数据读写。

---

## 8. `/actuator/features` 特性目录

**适用场景：** 运行时检查哪些可选特性确实在你的 classpath 上生效。

`ConfigurationPropertyHasFeaturesAutoConfiguration` 读取自带的
`META-INF/config/default/features.yaml`（特性目录），并与你在以下前缀下自定义的属性
合并：

```yaml
microsphere:
  spring:
    cloud:
      features:
        abstract:      # 模块 -> 类名列表（"抽象"特性）
          my-module:
            - com.acme.MyFeature
        named:         # 模块 -> 特性名 -> 类
          my-module:
            my-feature: com.acme.MyFeature
```

每个模块键都会注册成一个名为 `<module>.features` 的 `HasFeatures` singleton，由
Spring Cloud 的 `/actuator/features` 端点渲染（class 缺失只报 absent 并打 WARN，
绝不失败）。commons 模块自带目录列出的特性：
`UnionDiscoveryClient`、`ReactiveDiscoveryClientAdapter`、`MultipleRegistration`、
`MultipleServiceRegistry`、`MultipleAutoServiceRegistration`、
`EventPublishingRegistrationAspect`、`SimpleServiceRegistry`、
`SimpleAutoServiceRegistration`、`InMemoryServiceRegistry`、
`ServiceRegistrationEndpoint`、`ServiceDeregistrationEndpoint`、
`SpecificationBeanPostProcessor`、`SpecificationCustomizer`、
`TomcatDynamicConfigurationListener`。

前提条件：`spring.cloud.features.enabled` 不为 `false`（默认开启）、classloader 中存在
`FeaturesEndpoint`、且 `features` 端点可用（`management.endpoint.features.enabled=true`，
模块默认属性已置为 true）。

---

## 9. `SpecificationCustomizer`（Named-Context 扩展点）

**适用场景：** 在 Feign（以及本版本线的 Ribbon）的按客户端
`NamedContextFactory.Specification` 定义启动子容器**之前**进行观察或修改——同门
OpenFeign 模块注入其刷新能力用的正是这个钩子。

当存在 `Specification` bean 时自动生效。注册定制器即可：

```java
@Component
public class LoggingSpecificationCustomizer implements SpecificationCustomizer {
    @Override
    public void customize(NamedContextFactory.Specification specification, String beanName) {
        System.out.println("Feign/Ribbon specification: " + specification.getName()
                + " <- " + beanName);
    }
}
```

`SpecificationBeanPostProcessor` 会按排序依次把每个 `SpecificationCustomizer` bean
应用到每个 specification。相关内容另见
[OpenFeign 0.1.x 指南](./openfeign.md)。

---

## 10. 容错（Fault Tolerance）

### 10.1 可运行时调整的内嵌 Tomcat

**适用场景：** `/actuator/refresh` 之后在线调整线程池、backlog、超时与
header/POST 大小，免去滚动重启。

默认开启（`microsphere.spring.cloud.fault-tolerance.tomcat.enabled`，默认 `true`），
前提为 classpath 上有内嵌 Tomcat 与 Spring Cloud Context，且是 Servlet Web 应用。
`WebServerInitializedEvent` 到达时模块注册
`TomcatDynamicConfigurationListener`；每次 `EnvironmentChangeEvent` 都对变更的
`server.*` 键重新绑定，并且只应用**发生变更、为正数、可转换**的值：

| 属性键（0.1.x） | Tomcat 设置 |
|---|---|
| `server.tomcat.threads.min-spare` | 核心线程数 |
| `server.tomcat.threads.max` | 最大线程数 |
| `server.tomcat.accept-count` | 网络 backlog |
| `server.tomcat.connection-timeout` | 连接超时（毫秒） |
| `server.tomcat.max-connections` | 最大连接数 |
| `server.max-http-header-size` | HTTP header 最大字节数 |
| `server.tomcat.max-swallow-size` | HTTP body 最大 swallow 字节数 |
| `server.tomcat.max-http-form-post-size` | 表单 POST 最大字节数 |

> **1.x 键名：** header 大小的属性是 **`server.max-http-header-size`**——即
> Spring Boot 2 的 `ServerProperties.getMaxHttpHeaderSize()`。（0.2.x / Boot 3 更名为
> `server.max-http-request-header-size`。）不可刷新的项：`server.port`、`server.ssl.*`、
> context path。

在外部化配置中修改值后：

```bash
curl -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' -d '["server.tomcat.threads.max"]'
```

无需重启、无需重建客户端——运行中的 Tomcat `Protocol`/`Connector` 会被直接更新。

### 10.2 预热权重原语

**适用场景：** 自研负载均衡器，需要让流量逐步爬坡到新实例。这些**只是库原语**——
本模块内没有任何代码消费 `warmup-time`/`weight` 属性，消费方是下游负载均衡实现。

| 属性 | 默认值 | 含义 |
|---|---|---|
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | 600000（毫秒，10 分钟） | 预热窗口 |
| `microsphere.spring.cloud.fault-tolerance.weight` | 100 | 目标满权重 |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | — | 为各负载均衡器保留的命名空间 |

`WeightedRoundRobin` 实现平滑加权轮询的累加器；
`LoadBalancerUtils.calculateWarmupWeight(uptime, warmup, weight)` 按二次曲线爬坡并
钳制在 `[1, weight]`。

---

## 条件注解参考

可用于你自己的 `@Configuration`、复刻本模块激活逻辑的元注解：

| 注解 | 检查内容 |
|---|---|
| `@ConditionalOnFeaturesEnabled` | `spring.cloud.features.enabled`（默认开） |
| `@ConditionalOnFeaturesAvailable` | 上述条件 **且** `FeaturesEndpoint` 类存在 **且** 端点可用 |
| `@ConditionalOnBlockingDiscoveryAvailable` | `DiscoveryClient` 类 + `spring.cloud.discovery.enabled` + 阻塞发现启用 |
| `@ConditionalOnReactiveDiscoveryAvailable` | `ReactiveDiscoveryClient` 类 + `spring.cloud.discovery.enabled` + 响应式发现启用 |
| `@ConditionalOnAutoServiceRegistrationEnabled` | `spring.cloud.service-registry.auto-registration.enabled`（默认开） |
| `@ConditionalOnAutoServiceRegistrationAvailable` | `Registration` 类存在 + 容器中有 `Registration` **bean** + 上述条件 |
| `@ConditionalOnMultipleRegistrationEnabled` | `microsphere.spring.cloud.multiple-registration.enabled` 显式存在且非 `false`（纯 opt-in） |
| `@ConditionalOnLoadBalancerEnabled` | `spring.cloud.loadbalancer.enabled`（默认开；供下游模块使用） |
| `@ConditionalOnUtilEnabled` | `spring.cloud.util.enabled`（默认开；供下游模块使用） |

（外部注解，继承自 microsphere-spring-boot 0.1.x：`@ConditionalOnWebMvcAvailable`、
`@ConditionalOnWebFluxAvailable`、`@ConditionalOnActuatorEndpointPresent`。）

## 属性参考

| 属性 | 类型 | 默认值 | 用途 |
|---|---|---|---|
| `spring.cloud.service-registry.auto-registration.enabled` | boolean | `true` | Spring Cloud 自动注册总开关 |
| `spring.cloud.features.enabled` | boolean | `true` | features 端点支持开关 |
| `spring.cloud.loadbalancer.enabled` | boolean | `true` | 由 `@ConditionalOnLoadBalancerEnabled` 守护 |
| `spring.cloud.util.enabled` | boolean | `true` | 由 `@ConditionalOnUtilEnabled` 守护 |
| `microsphere.spring.cloud.multiple-registration.enabled` | boolean | `false` | 启用多注册中心聚合 |
| `microsphere.spring.cloud.default-registration.type` | class | — | 组合对象的默认底层注册对象 |
| `microsphere.spring.cloud.default-service-registry.type` | class | — | 组合对象的默认底层注册中心 |
| `microsphere.spring.cloud.composite-registration.enabled` | boolean | `false` | 已声明，暂无自动配置消费 |
| `microsphere.spring.cloud.client.discovery.mode` | string | — | 设为 `union` 安装 `UnionDiscoveryClient` |
| `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | boolean | 关 | 启用简单/内存注册栈 |
| `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | boolean | `true` | 运行时可调 Tomcat 监听器 |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long | `600000` | 预热窗口（毫秒），供下游负载均衡器 |
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | 目标权重，供下游负载均衡器 |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | 前缀 | — | 各负载均衡器设置的保留前缀 |
| `microsphere.spring.cloud.features.abstract.*` | map | 自带目录 | 抽象特性声明 |
| `microsphere.spring.cloud.features.named.*` | map | 自带目录 | 命名特性声明 |
| `management.endpoint.serviceRegistration.enabled` | boolean | `false`（模块默认） | 启用注册端点 |
| `management.endpoint.serviceDeregistration.enabled` | boolean | `false`（模块默认） | 启用注销端点 |
| `management.endpoints.web.path-mapping.serviceRegistration` | string | `microsphere/service/registration` | 端点路径重映射 |
| `management.endpoints.web.path-mapping.serviceDeregistration` | string | `microsphere/service/deregistration` | 端点路径重映射 |

## 版本兼容性

适用于 Spring Boot **2.3.x – 2.6.x** 与 Spring Cloud **Hoxton.SR12 – 2021.0.x**
（即父 POM 的 `spring-cloud-hoxton`、`spring-cloud-2020`、`spring-cloud-2021` 三个
profile），运行于 Java **8+**，Servlet 命名空间为 `javax.*`。注册中心客户端（Eureka、
Nacos、Zookeeper、Consul）均为可选；存在时自动激活相应组合（事件、多注册中心、端点、
Web 元数据）。

## 与 0.2.x 的差异

| 方面 | 0.1.x（`release-1.x`） | 0.2.x（`release`） |
|---|---|---|
| 自动配置注册 | `META-INF/spring.factories` 实际生效（Boot 2 忽略同时附带的 `AutoConfiguration.imports`） | 以 `AutoConfiguration.imports` 为准（Boot 3+） |
| Web MVC Servlet 代码 | `javax.servlet.Filter` | `jakarta.servlet.Filter` |
| Tomcat 动态 header 属性 | `server.max-http-header-size`（`getMaxHttpHeaderSize`） | `server.max-http-request-header-size`（`getMaxHttpRequestHeaderSize`） |
| `ServiceInstanceUtils.setProperties` | 拷贝 `getUri()` 到 `DefaultServiceInstance`（0.2.x 的 Spring Cloud 已删除该 setter） | 拷贝 `secure` 标志 |
| Java 基线 / 语言级别 | Java 8 | 字节码 17（源码风格仍兼容 Java 8） |
| 上游 `microsphere-spring-boot` | 0.1.34 | 0.2.34 |
| 构建中的 Testcontainers 构件 | `org.testcontainers:junit-jupiter` | `org.testcontainers:testcontainers-junit-jupiter` |
| 最新发布版本 | 0.1.26 | 0.2.26 |
| 其余部分 | 相同的类、相同的属性键、相同的行为 | — |
