[English](../commons.md) · [1.x 版本线](../../1.x/zh/commons.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-commons` 使用指南（0.2.x）

microsphere-spring-cloud 的核心模块。它提供 Spring Cloud Commons 本身不具备的能力：
**一个服务实例同时注册到多个注册中心**、**可观测的注册生命周期事件**、
**通过 Actuator 手动上线/下线**、**自带一套无需外部服务的内存注册中心**、
**多 DiscoveryClient 的 union 聚合**、**响应式到阻塞式服务发现的桥接**、
**服务实例上的 Web 端点元数据**、**为 `/actuator/features` 提供特性目录**，
以及**运行期可调整的内置 Tomcat 参数**——全部按需开启，并做类存在性守卫。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release` |
| 版本线 | 0.2.x |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-commons` |
| 制品类型 | `jar` |
| 最新已发布版本 | **`0.2.26`**（分支头部为 `0.2.27-SNAPSHOT`，未发布） |
| 仓库 | Maven Central |
| Java | **17+** |
| Servlet 命名空间 | `jakarta.*` |
| Spring Boot 范围 | 3.0.x – 4.0.x |
| Spring Cloud 范围 | 2022.0.x – 2025.1.x |
| 编译期强依赖 | `microsphere-spring-boot-core`、`-webmvc`、`-webflux`、`-actuator`（0.2.34）、`org.aspectj:aspectjweaver` |
| 父 POM | `microsphere-spring-cloud-parent` — 见 [parent.md](./parent.md) |

## 能力总览

| 能力 | 开关 | 默认值 | 章节 |
|---|---|---|---|
| 多注册中心（同时注册 Nacos + Eureka 等） | `microsphere.spring.cloud.multiple-registration.enabled` | `false` | 多注册中心注册 |
| 注册生命周期事件 + 自定义器 | 无（只要 AspectJ 与 `Registration` 在 classpath 即生效） | 开启 | 注册生命周期事件 |
| 手动注册/反注册端点 | `management.endpoint.serviceRegistration.enabled`、`management.endpoint.serviceDeregistration.enabled` | `false` | Actuator 端点 |
| 简单/内存注册中心栈 | `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | 关闭（需显式开启） | 简单/内存注册中心栈 |
| 所有发现客户端的 union 聚合 | `microsphere.spring.cloud.client.discovery.mode=union` | 关闭 | Union Discovery Client |
| 响应式→阻塞式发现适配器 | 隐式（响应式与阻塞式发现同时启用） | 两者齐备即开启 | `ReactiveDiscoveryClientAdapter` |
| 实例元数据中的 Web 映射与上下文路径 | 隐式（可用自动服务注册时） | 开启 | Web 端点元数据 |
| `/actuator/features` 特性目录 | `spring.cloud.features.enabled`、`microsphere.spring.cloud.features.*` | `true` | 特性目录 |
| 定制 Feign/LoadBalancer 命名上下文 | 需要注册一个 `SpecificationCustomizer` bean | 存在 `Specification` bean 时开启 | `SpecificationCustomizer` |
| 内置 Tomcat 参数动态调整 | `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | `true` | 容错 |

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-commons</artifactId>
</dependency>
```

版本由 [BOM](./dependencies.md) 或 [父 POM](./parent.md) 提供。之后按你实际使用的功能补充依赖——
本模块所有第三方依赖都是 `optional`，除了 Microsphere Spring Boot 与 `aspectjweaver`
之外不会传递任何依赖：

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-commons</artifactId>
    </dependency>
    <!-- 你真正使用的注册中心客户端 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!-- 注册事件与 Actuator 端点所需 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## 版本兼容性

按父 POM 的 Spring Cloud profile 构建与测试
（见 [父 POM 指南](./parent.md)）：

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | 2022.0.0.2 |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | 2023.0.3.4 |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | 2023.0.3.4 |
| `spring-cloud-2025`**（默认）** | 3.5.15 | 2025.0.3 | 2025.0.0.0 |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | 2025.1.0.0 |

必须使用 Java 17+。自动配置通过
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3
机制）注册，并全部带有类存在性守卫，缺失的组件只会让对应功能不生效。

## 多注册中心注册（Multiple Service Registry）

**何时使用**：同一个应用实例需要同时出现在两个及以上注册中心——典型场景是从 Nacos 向
Eureka/Zookeeper 迁移，或者同时服务 Spring Cloud 体系与基于注册中心的传统体系。

开启后，模块会用三个 `@Primary` 组合 bean 替换单一的
`Registration` / `ServiceRegistry` / `AutoServiceRegistration`：

| Bean | 类型 | 构建来源 |
|---|---|---|
| `multipleRegistration` | `MultipleRegistration`（实现 `Registration`） | 所有 `Registration` bean |
| `multipleServiceRegistry` | `MultipleServiceRegistry`（实现 `ServiceRegistry<MultipleRegistration>`） | 所有 `ServiceRegistry` bean，以 bean 名为键 |
| `multipleAutoServiceRegistration` | `MultipleAutoServiceRegistration` | 上两个 bean + `AutoServiceRegistrationProperties` |

```yaml
spring:
  application:
    name: order-service
  cloud:
    service-registry:
      auto-registration:
        enabled: true          # 默认即为 true，此处仅为显式表达
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        namespace: f7ad23e0-f581-4516-9420-8c50aa6a7b89
        metadata:
          key: value
eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka/

microsphere:
  spring:
    cloud:
      multiple-registration:
        enabled: true
      # 已声明的类型提示（见下方说明）
      default-registration:
        type: com.alibaba.cloud.nacos.registry.NacosRegistration
      default-service-registry:
        type: com.alibaba.cloud.nacos.registry.NacosServiceRegistry
```

必须了解的行为：

- `register`、`deregister`、`setStatus`、`close` 会**遍历所有注册中心**。对每一个都会解析出对应的
  厂商专属 `Registration`，解析结果为 `null` 的直接跳过，即没有匹配注册的注册中心不参与。
- `getStatus(MultipleRegistration)` **只从默认注册中心**读取状态。
- **默认注册/注册中心取注入顺序中的最后一个**——这是组合 bean 依赖注入顺序的判定规则。上面两个
  `…type` 键是作为配置元数据发布的（本模块的多注册中心集成测试也这么写），它们表达你的意图，
  但并不会真正改变 bean 顺序。要锁定某个厂商，请控制 bean 的注入顺序，或显式使用 `special(...)`。
- `MultipleRegistration.getMetadata()` 返回共享的 `RegistrationMetaData`：对它的
  `put`、`remove`、`putAll`、`clear` 都会**同步传播到每个底层注册自身的 metadata**，因此一次写入
  即可覆盖所有注册中心。

访问厂商专属 API（例如 Eureka 的 `ApplicationInfoManager`）：

```java
@Autowired
private Registration registration; // 即 @Primary 的 MultipleRegistration

public InstanceInfo eurekaInstanceInfo() {
    EurekaRegistration eureka = this.registration.special(EurekaRegistration.class);
    if (eureka == null) {
        throw new IllegalStateException("当前 JVM 中没有 EurekaRegistration");
    }
    return eureka.getApplicationInfoManager().getInfo();
}
```

`special(Class<T>)` 返回最后注册的那个 `T`；若请求的类型就是 `Registration`，则返回组合对象本身。
`microsphere.spring.cloud.composite-registration.enabled` 只是一个为将来保留的常量，0.2.x 中没有
任何自动配置消费它。

## 注册生命周期事件

**何时使用**：你需要在注册进入注册中心的前后做出反应——同步厂商原生实例对象、补充构建元数据、
按自己的方式摘除流量。

只要 classpath 中同时存在 `org.aspectj.lang.annotation.Aspect` 与
`org.springframework.cloud.client.serviceregistry.Registration`，
`ServiceRegistryAutoConfiguration` 就会导入 `EventPublishingRegistrationAspect`——它**不受**
`microsphere.spring.cloud.multiple-registration.enabled` 控制。该切面环绕
`ServiceRegistry.register(..)` 与 `ServiceRegistry.deregister(..)`：

| 时机 | 发布的事件 | `RegistrationEvent.Type` |
|---|---|---|
| `register` 之前 | `RegistrationPreRegisteredEvent` | `PRE_REGISTERED` |
| `register` 之后 | `RegistrationRegisteredEvent` | `REGISTERED` |
| `deregister` 之前 | `RegistrationPreDeregisteredEvent` | `PRE_DEREGISTERED` |
| `deregister` 之后 | `RegistrationDeregisteredEvent` | `DEREGISTERED` |

每个事件都提供 `getRegistration()`、`getRegistry()`、`getSource()` 以及判定方法
`isPreRegistered()`、`isRegistered()`、`isPreDeregistered()`、`isDeregistered()`。

> 事件由**各个真实**注册中心触发：切面会忽略 `MultipleServiceRegistry` 的调用
> （`isIgnored(...)`），因此组合注册不会产生重复事件——每个真实厂商注册中心各一组事件。

```java
@Component
public class EurekaMetadataSyncListener implements ApplicationListener<RegistrationPreRegisteredEvent> {

    @Override
    public void onApplicationEvent(RegistrationPreRegisteredEvent event) {
        Registration registration = event.getRegistration();
        registration.getMetadata().put("my-key", "my-value");
        if (registration instanceof EurekaRegistration) {
            EurekaRegistration eurekaRegistration = (EurekaRegistration) registration;
            InstanceInfo instanceInfo = eurekaRegistration.getApplicationInfoManager().getInfo();
            // 在提交给 Eureka 之前，把 Registration 的 metadata 同步到 InstanceInfo
            instanceInfo.getMetadata().putAll(registration.getMetadata());
        }
    }
}
```

### `RegistrationCustomizer`

监听器负责**观察**，`RegistrationCustomizer` 负责**修改**：所有该类型的 bean（按序解析）会在
`RegistrationPreRegisteredEvent` 发布之后、同一注册前置步骤中依次作用于注册对象。

```java
@Component
public class BuildInfoRegistrationCustomizer implements RegistrationCustomizer {

    @Override
    public void customize(Registration registration) {
        Map<String, String> metadata = registration.getMetadata();
        metadata.put("build.sha", BuildProperties.gitSha());
        metadata.put("region", System.getenv().getOrDefault("REGION", "cn-hangzhou"));
    }
}
```

接口为 `io.microsphere.spring.cloud.client.service.registry.RegistrationCustomizer`
（唯一方法 `void customize(Registration registration)`），由于使用 `getSortedBeans` 解析，
可用 `Ordered`/`@Order` 控制顺序。

## Actuator 端点

**何时使用**：运维或发布流水线需要在不重启的前提下让实例上线/下线——例如热重启前摘流、
灰度发布时临时摘除。

提供两个端点：`ServiceRegistrationEndpoint`（`id = "serviceRegistration"`）与
`ServiceDeregistrationEndpoint`（`id = "serviceDeregistration"`）。**两者默认均为关闭**，
且都被重新映射到 Microsphere 专属路径。模块通过
`META-INF/config/default/endpoints.properties` 以最低优先级属性提供以下默认值：

```properties
management.endpoint.serviceRegistration.enabled = false
management.endpoint.serviceDeregistration.enabled = false
management.endpoints.web.path-mapping.serviceRegistration = microsphere/service/registration
management.endpoints.web.path-mapping.serviceDeregistration = microsphere/service/deregistration
```

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
        include: health,info,serviceRegistration,serviceDeregistration
```

| 实际 URL | 操作 | 效果 | 返回值 |
|---|---|---|---|
| `GET /actuator/microsphere/service/registration` | 读 | 实例状态快照 | JSON map |
| `POST /actuator/microsphere/service/registration` | 写（`start()`） | 未注册时执行注册 | **执行前**的 running 状态 |
| `POST /actuator/microsphere/service/deregistration` | 写（`stop()`） | 已注册时执行反注册 | **执行前**的状态 |

```bash
# 查看
curl -s http://localhost:8080/actuator/microsphere/service/registration | jq

# 把实例摘出可用列表
curl -s -X POST http://localhost:8080/actuator/microsphere/service/deregistration
# true  -> 此前是注册状态，现已反注册
# false -> 此前就已摘除（空调用，日志里会给出 warning）

# 重新挂回去
curl -s -X POST http://localhost:8080/actuator/microsphere/service/registration
```

`GET` 返回的键（按顺序）为：`application-name`、`registration`、`port`、`status`
（取自 `ServiceRegistry.getStatus(...)`）、`running`、`enabled`、`phase`、`order`、
`managementRegistration`、`config`。取不到的值就是缺失或 `null`；反射查找失败时返回 `null`
而不是抛异常。

实现上有两点需要知道：

- `POST` **返回的是执行前的状态，而不是结果**：`registration` 真正执行注册时返回 `false`
  （此前未 running），此前已注册而什么都不做时返回 `true`；`deregistration` 真正反注册时返回
  `true`，本来就没注册时返回 `false`。需要当前真实状态请读 `GET` 里的 `running`。
- running 标记位于 `AbstractServiceRegistrationEndpoint` 的 `protected static` 字段上，
  也就是说注册端点与反注册端点在**整个 JVM 内共享**该状态。一个 JVM 跑多个应用时，两个端点
  彼此是一致的，但并不按上下文区分。

## 简单/内存注册中心栈

**何时使用**：不依赖任何外部服务也需要一套 `Registration`/`ServiceRegistry`：示例工程、契约测试、
本地开发，以及 classpath 上没有真实注册中心客户端时的兜底。

```yaml
spring:
  application:
    name: test-service
server:
  port: 8080

microsphere:
  spring:
    cloud:
      service-registry:
        auto-registration:
          simple:
            enabled: true     # 需要显式开启，这就是开关
```

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`SimpleAutoServiceRegistrationAutoConfiguration` 由此提供：

| Bean | 类型 | 说明 |
|---|---|---|
| `registration` | `DefaultRegistration`（`extends DefaultServiceInstance implements Registration`） | `instanceId = host + ":" + port`，host 取 `InetUtils` 的第一个非回环 IP，port 取 `server.port`，serviceId 取 `spring.application.name`（缺省 `application`） |
| `serviceRegistry` | `InMemoryServiceRegistry` | `@ConditionalOnMissingBean`；以实例 id 为键的 `ConcurrentMap` |
| `SimpleAutoServiceRegistration` | 由 `@Import` 引入 | 用标准 `AbstractAutoServiceRegistration` 生命周期驱动内存注册中心 |

前提条件：`spring.cloud.service-registry.auto-registration.enabled` 不能为 `false`（默认 `true`），
并且 `Registration` 类必须在 classpath 上。该配置 `@AutoConfigureBefore` Spring Cloud 自己的
`AutoServiceRegistrationAutoConfiguration`，因此当厂商注册中心也存在时，厂商 bean 优先。

相关但不同的另一个类：`SimpleServiceRegistry` 把同样的思想绑定到**配置文件式**的服务发现模型
（`spring.cloud.discovery.client.simple.instances`），下文 union 客户端示例正是用这种方式构造实例。
两个简单注册中心都用元数据键 `_status_` 保存状态。

## Union Discovery Client

**何时使用**：同时存在多个 `DiscoveryClient` 实现（Nacos + Eureka + 配置文件实例），你需要的是一份
**保留各厂商重复项**的扁平视图，而不是 Spring Cloud `CompositeDiscoveryClient` 的先命中即返回策略。

```yaml
microsphere:
  spring:
    cloud:
      client:
        discovery:
          mode: union
      multiple-registration:
        enabled: true

spring:
  cloud:
    discovery:
      client:
        simple:
          order: -1
          instances:
            test:
              - instanceId: 1
                serviceId: test
                host: 127.0.0.1
                port: 8080
                metadata:
                  key-1: value-1
```

`UnionDiscoveryClient` bean 会被 Spring Cloud 的 `CompositeDiscoveryClient` 收集，并且由于
`Ordered.HIGHEST_PRECEDENCE`，它总是出现在 `getDiscoveryClients()` 的**第一位**——这也正是模块自身的
集成测试所断言的。

| 方法 | 行为 |
|---|---|
| `description()` | 返回 `"Union Discovery Client"` |
| `getInstances(serviceId)` | 依次拼接所有客户端的非空结果，**保留重复项** |
| `getServices()` | 各客户端并集，**去重**且保持插入顺序 |
| `getDiscoveryClients()` | 所有按序排列的 `DiscoveryClient` bean，排除 `CompositeDiscoveryClient` 与自身（首次惰性解析后缓存） |

## `ReactiveDiscoveryClientAdapter`

**何时使用**：可用的实现只有响应式发现（常见于 Kubernetes 或 Consul 响应式技术栈），
但第三方代码或你自己的代码仍在调用阻塞式 `DiscoveryClient` API。

无需开关：当 `ReactiveDiscoveryClient`、`spring.cloud.discovery.enabled`、
`spring.cloud.discovery.reactive.enabled` 都满足，**并且**阻塞式发现仍然启用
（`spring.cloud.discovery.blocking.enabled`，默认 `true`）时，模块会围绕可用的
`ReactiveDiscoveryClient` 创建一个 `ReactiveDiscoveryClientAdapter` bean。

```java
@Autowired
private DiscoveryClient discoveryClient; // 可能就是这个适配器

List<ServiceInstance> instances = discoveryClient.getInstances("order-service");
```

阻塞语义：内部用 `block()` 收集 `Flux`；若当前处于非阻塞线程
（`Schedulers.isInNonBlockingThread()`），改用 `toFuture().get()`，从而绕开 Reactor 的阻塞检测。
`description()`、`probe()`、`getOrder()` 直接委托给响应式客户端。

配套工具：`DiscoveryUtils` 可以在 `SimpleDiscoveryProperties` ⇄
`SimpleReactiveDiscoveryProperties` 之间双向转换，使配置文件实例在两套 API 下都可见。

## Web 端点元数据

**何时使用**：服务消费方需要知道某个实例**实际**提供哪些 URL（网关/流量工具、API 发现、契约测试）。

`WebMvcServiceRegistryAutoConfiguration`（Servlet）与
`WebFluxServiceRegistryAutoConfiguration`（Reactive）监听 `WebEndpointMappingsReadyEvent`，
并向 `Registration` 的 metadata 写入两项：

| 元数据键 | 常量 | 内容 |
|---|---|---|
| `web.mappings` | `InstanceConstants.WEB_MAPPINGS_METADATA_NAME` | URL 编码后的 `WebEndpointMapping` JSON 数组 |
| `web.context-path` | `InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME` | `server.servlet.context-path`（WebMvc）或 `""`（WebFlux） |

内置的 `org.springframework.*` filter/servlet 映射以及 Actuator 端点
（`management.endpoints.web.base-path`（默认 `/actuator`）之下的所有 pattern）会被**排除**。

```java
// 消费端，针对任意 ServiceInstance
Collection<WebEndpointMapping> mappings = ServiceInstanceUtils.getWebEndpointMappings(instance);
String baseUri = ServiceInstanceUtils.getUriString(instance); // http(s)://host:port，端口<=0 时回落 443/80
```

`ServiceInstanceUtils` 还提供 `getUri(ServiceInstance)`（比
`DefaultServiceInstance.getUri()` 更轻量）、`getMetadata/setMetadata/removeMetadata`
以及 `setProperties(source, target)`——0.2.x 中复制的是 **`secure`** 标记
（1.x 复制的是 URI；Boot 3 的 `DefaultServiceInstance` 由 `secure` 推导 URI）。

## 特性目录（Feature Catalogue）

**何时使用**：你希望 `/actuator/features` 直接报告当前进程中真正存在的 Microsphere 特性与可选类，
而不是靠翻 classpath 判断。

```yaml
spring:
  cloud:
    features:
      enabled: true            # 默认值；模块默认属性里该端点也是开启的

management:
  endpoints:
    web:
      exposure:
        include: health,info,features

microsphere:
  spring:
    cloud:
      features:
        abstract:
          rest: org.springframework.web.client.RestOperations
          redis: org.springframework.data.redis.connection.RedisConnectionFactory,
                 org.springframework.data.redis.core.RedisTemplate,
                 org.springframework.data.redis.core.StringRedisTemplate
        named:
          web:
            WebClient: org.springframework.web.reactive.function.client.WebClient
          rest:
            RestTemplate: org.springframework.web.client.RestTemplate
```

规则：

- **按模块键**注册一个 `HasFeatures` 单例，bean 名为 `<module>.features`
  （例如 `microsphere-spring-cloud-commons.features`）。
- **抽象特性**：若列出的类存在 Spring bean 类型，会被展开为每个 bean 类型一条**具名**特性；
  否则该类本身作为抽象特性记录。classpath 上不存在的类会让 `FeaturesEndpoint` 输出
  `"The following features are absent: …"`。
- **具名特性**的限定名为 `<module>:<FeatureName>`——上面这段配置产生 `web:WebClient`
  与 `rest:RestTemplate`。
- 找不到类时以 `WARN` 记录（消息中会给出确切的属性名）并跳过，应用照常启动。
- `META-INF/config/default/features.yaml` 内置了目录，已经登记 0.2.x 的 `commons` 与 `openfeign`
  模块（`UnionDiscoveryClient`、`ReactiveDiscoveryClientAdapter`、`MultipleRegistration`、
  `MultipleServiceRegistry`、`MultipleAutoServiceRegistration`、`EventPublishingRegistrationAspect`、
  `SimpleServiceRegistry`、`SimpleAutoServiceRegistration`、`InMemoryServiceRegistry`、
  `ServiceRegistrationEndpoint`、`ServiceDeregistrationEndpoint`、`SpecificationBeanPostProcessor`、
  `SpecificationCustomizer`、`TomcatDynamicConfigurationListener`）。
- 生效条件：`spring.cloud.features.enabled`（默认 `true`）**且**
  `org.springframework.cloud.client.actuator.FeaturesEndpoint` 类存在**且**该端点可用。
  （`ConditionalOnFeaturesAvailable` 的 Javadoc 写的是 `microsphere.spring.cloud.features.enabled`，
  实际绑定的键是 `spring.cloud.features.enabled`。）

## `SpecificationCustomizer`

**何时使用**：你需要观察或修改定义 Feign / LoadBalancer 每客户端上下文的
`NamedContextFactory.Specification` bean——OpenFeign 模块正是用它注入自动刷新能力的。

```java
@Component
public class AuditingSpecificationCustomizer implements SpecificationCustomizer {

    private static final Logger log = LoggerFactory.getLogger(AuditingSpecificationCustomizer.class);

    @Override
    public void customize(NamedContextFactory.Specification specification, String beanName) {
        log.info("named context '{}' -> specification '{}' with {} configuration class(es)",
                beanName, specification.getName(), specification.getConfiguration().length);
    }
}
```

`SpecificationAutoConfiguration` 会安装 `SpecificationBeanPostProcessor`（仅当存在
`Specification` bean，也就是 classpath 上有 `spring-cloud-context` 的 `NamedContextFactory` 时），
在初始化之后依次执行所有按序排列的 `SpecificationCustomizer`。OpenFeign 模块已经替你注册了一个
这样的自定义器——见 [openfeign.md](./openfeign.md)。

## 容错（Fault Tolerance）

### 动态内置 Tomcat

**何时使用**：需要在运行中的节点上调大线程池、连接数或 Header 限制，而不是滚动重启。

对使用内置 Tomcat 的 Servlet 应用默认开启（`microsphere.spring.cloud.fault-tolerance.tomcat.enabled=true`），
前提是 classpath 上有 `spring-cloud-context`。`WebServerInitializedEvent` 到来时会挂载
`TomcatDynamicConfigurationListener`；每次 `EnvironmentChangeEvent` 都重新读取 `server.*` 属性，
并把**已变化、为正数且可转换**的值应用下去：

| 属性 | 作用对象 | Tomcat 设置 |
|---|---|---|
| `server.tomcat.threads.min-spare` | `AbstractProtocol` | 核心（最小空闲）线程数 |
| `server.tomcat.threads.max` | `AbstractProtocol` | 最大线程数 |
| `server.tomcat.accept-count` | `AbstractProtocol` | 网络 backlog |
| `server.tomcat.connection-timeout` | `AbstractProtocol` | 连接超时（毫秒） |
| `server.tomcat.max-connections` | `AbstractProtocol` | 最大连接数 |
| `server.max-http-request-header-size` | `AbstractHttp11Protocol` | HTTP Header 最大字节数 |
| `server.tomcat.max-swallow-size` | `AbstractHttp11Protocol` | 最大 swallow 字节数 |
| `server.tomcat.max-http-form-post-size` | `Connector` | 表单 POST 最大字节数 |

```yaml
management:
  endpoint:
    refresh:
      enabled: true      # 模块默认属性把它设为 false
  endpoints:
    web:
      exposure:
        include: health,info,refresh,env
```

```bash
curl -s -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' \
     -d '[{"server.tomcat.threads.max":"400","server.tomcat.max-connections":"16384"}]'
```

无法热更的项：`server.port`、任何 `server.ssl.*`、以及上下文路径——这些仍需重启。
如果容器里没有 `ConfigurationPropertiesRebinder` bean，监听器会自己只重新绑定发生变化的 `server` 键。

### 加权轮询基础组件

**何时使用**：你要实现自己的负载均衡器，并希望与 Microsphere 技术栈采用相同的平滑加权轮询与
预热爬坡语义。这些只是**库级构件**：0.2.x 中没有任何自动配置装配它们，下面两个属性也是由下游
负载均衡器读取，而不是本模块。

```java
WeightedRoundRobin a = new WeightedRoundRobin("10.0.0.1:8080");
a.setWeight(3);                       // 重设权重会同时清零累加器
WeightedRoundRobin b = new WeightedRoundRobin("10.0.0.2:8080");
b.setWeight(1);

int total = a.getWeight() + b.getWeight();          // 4
long currentA = a.increaseCurrent();                // +3
a.sel(total);                                       // -1  -> 平滑加权轮询的一步
                                                     // 当前值最大的候选胜出

// 新启动的节点在预热期内按二次曲线爬坡
long warmupMillis = TimeUnit.MINUTES.toMillis(10);
long uptime = System.currentTimeMillis() - startTime;
int effectiveWeight = LoadBalancerUtils.calculateWarmupWeight(uptime, warmupMillis, 100); // 结果被夹在 [1, weight]
```

| 属性 | 类型 | 默认值 | 含义 |
|---|---|---|---|
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | 本实例的标称权重 |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long（毫秒） | `600000`（10 分钟） | 预热时长 |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | 前缀 | — | 预留给各负载均衡器的独立配置 |

### `ServiceInstancesChangedEvent`

模块提供/消费 `io.microsphere.spring.cloud.client.event.ServiceInstancesChangedEvent`
（`getServiceName()`、`getServiceInstances()`，以及用于幂等处理的 `processed()`/`isProcessed()`）。
实例列表**不允许为空**——用空列表构造事件会直接抛出异常。

```java
@EventListener
public void onInstancesChanged(ServiceInstancesChangedEvent event) {
    if (event.isProcessed()) {
        return;
    }
    cache.refresh(event.getServiceName(), event.getServiceInstances());
    event.processed();
}
```

## 条件注解参考

以下都是标记注解（无属性，因而也没有别名）。可以把它们用在自定义的
`@Configuration`/`@Bean` 上，以与库本身相同的条件做门控。

| 注解 | 包 | 判定内容 |
|---|---|---|
| `ConditionalOnFeaturesEnabled` | `client.condition` | `spring.cloud.features.enabled`（`matchIfMissing=true`） |
| `ConditionalOnFeaturesAvailable` | `client.condition` | 上述属性 **且** `FeaturesEndpoint` 类 **且** 该端点可用 |
| `ConditionalOnBlockingDiscoveryAvailable` | `client.discovery.condition` | `DiscoveryClient` 类 + `spring.cloud.discovery.enabled` + `spring.cloud.discovery.blocking.enabled` |
| `ConditionalOnReactiveDiscoveryAvailable` | `client.discovery.condition` | `ReactiveDiscoveryClient` 类 + `spring.cloud.discovery.enabled` + `spring.cloud.discovery.reactive.enabled` |
| `ConditionalOnAutoServiceRegistrationEnabled` | `client.service.registry.condition` | `spring.cloud.service-registry.auto-registration.enabled`（`matchIfMissing=true`） |
| `ConditionalOnAutoServiceRegistrationAvailable` | `client.service.registry.condition` | `Registration` 类 **且** 存在 `Registration` bean **且** 上述属性 |
| `ConditionalOnMultipleRegistrationEnabled` | `client.service.registry.condition` | `microsphere.spring.cloud.multiple-registration.enabled` 必须显式存在（严格 opt-in） |
| `ConditionalOnLoadBalancerEnabled` | `loadbalancer.condition` | `spring.cloud.loadbalancer.enabled`（为下游模块声明，本模块未使用） |
| `ConditionalOnUtilEnabled` | `commons.condition` | `spring.cloud.util.enabled`（为下游模块声明，本模块未使用） |

继承自 microsphere-spring-boot 并在本模块使用的：`ConditionalOnWebMvcAvailable`、
`ConditionalOnWebFluxAvailable`、`ConditionalOnActuatorEndpointPresent`。

## 自动配置参考

注册于 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`：

| # | 自动配置 | 生效条件 | 提供内容 |
|---|---|---|---|
| 1 | `SpecificationAutoConfiguration` | 存在 `NamedContextFactory`；存在 `Specification` bean | `SpecificationBeanPostProcessor` |
| 2 | `DiscoveryClientAutoConfiguration` | 阻塞式发现可用 | 内嵌 `UnionConfiguration`：mode 为 `union` 时提供 `UnionDiscoveryClient` |
| 3 | `ReactiveDiscoveryClientAutoConfiguration` | 响应式发现可用 | 内嵌 `BlockingConfiguration`：提供 `ReactiveDiscoveryClientAdapter` |
| 4 | `ServiceRegistryAutoConfiguration` | 自动服务注册启用 + AspectJ + `Registration` | `EventPublishingRegistrationAspect`；内嵌 `MultipleConfiguration` 提供三个 `@Primary` 组合 bean |
| 5 | `WebMvcServiceRegistryAutoConfiguration` | WebMvc + 自动服务注册可用 | 把 Web 映射与上下文路径写入 `Registration` metadata |
| 6 | `WebFluxServiceRegistryAutoConfiguration` | WebFlux + 自动服务注册可用 | 响应式应用同理（上下文路径为空，不排除任何映射） |
| 7 | `SimpleAutoServiceRegistrationAutoConfiguration` | 显式开启 simple 开关 | `DefaultRegistration`、`InMemoryServiceRegistry`、`SimpleAutoServiceRegistration` |
| 8 | `ServiceRegistrationEndpointAutoConfiguration` | discovery + 自动注册 + Actuator 端点可用 | `ServiceRegistrationEndpoint`、`ServiceDeregistrationEndpoint` |
| 9 | `TomcatFaultToleranceAutoConfiguration` | 默认开启 + Tomcat + Servlet 应用 | Web 服务器启动后挂载 `TomcatDynamicConfigurationListener` |
| 10 | `ConfigurationPropertyHasFeaturesAutoConfiguration` | 特性可用 | 依据 `microsphere.spring.cloud.features.*` 为每个模块注册一个 `HasFeatures` 单例 |

`WebServiceRegistryAutoConfiguration` 是第 5、6 行的抽象基类，**有意不注册**。

## 属性参考

| 属性 | 类型 | 默认值 | 作用 |
|---|---|---|---|
| `microsphere.spring.cloud.multiple-registration.enabled` | boolean | `false` | 开启组合注册/注册中心/自动注册 bean |
| `microsphere.spring.cloud.default-registration.type` | 类名 | — | 已声明的配置元数据：期望的默认 `Registration` 类型（代码实际使用注入顺序的最后一个） |
| `microsphere.spring.cloud.default-service-registry.type` | 类名 | — | 已声明的配置元数据：期望的默认 `ServiceRegistry` 类型 |
| `microsphere.spring.cloud.composite-registration.enabled` | boolean | `false` | 仅声明常量，0.2.x 中无消费者（预留） |
| `microsphere.spring.cloud.client.discovery.mode` | string | — | 设为 `union` 以启用 `UnionDiscoveryClient` |
| `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | boolean | 关闭（需显式开启） | 启用简单/内存注册中心栈 |
| `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | boolean | `true` | 在 `EnvironmentChangeEvent` 时动态调整 Tomcat |
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | 供下游负载均衡器使用的标称权重 |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long | `600000` | 预热时长（毫秒） |
| `microsphere.spring.cloud.features.abstract.<module>` | 类名列表 | — | 每个模块上报的抽象特性 |
| `microsphere.spring.cloud.features.named.<module>.<FeatureName>` | 类名 | — | 具名特性 `<module>:<FeatureName>` |
| `spring.cloud.service-registry.auto-registration.enabled` | boolean | `true` | Spring Cloud 原生开关，本模块多数功能的前置条件 |
| `spring.cloud.features.enabled` | boolean | `true` | Spring Cloud 原生特性目录开关 |
| `spring.cloud.loadbalancer.enabled` | boolean | `true` | 由 `ConditionalOnLoadBalancerEnabled` 守卫 |
| `spring.cloud.util.enabled` | boolean | `true` | 由 `ConditionalOnUtilEnabled` 守卫 |
| `management.endpoint.serviceRegistration.enabled` | boolean | `false`（模块默认值） | 启用注册端点 |
| `management.endpoint.serviceDeregistration.enabled` | boolean | `false`（模块默认值） | 启用反注册端点 |
| `management.endpoints.web.path-mapping.serviceRegistration` | string | `microsphere/service/registration` | 端点路径 |
| `management.endpoints.web.path-mapping.serviceDeregistration` | string | `microsphere/service/deregistration` | 端点路径 |
| `management.endpoint.features.enabled` | boolean | `true`（模块默认值） | `/actuator/features` |
| `management.endpoint.refresh.enabled` | boolean | `false`（模块默认值） | `/actuator/refresh`——Tomcat 动态特性依赖它 |
| `management.endpoints.web.base-path` | string | `/actuator` | Actuator 前缀；其下的映射不会写入 `web.mappings` |

模块自带的默认值（`META-INF/config/default/endpoints.properties`）还额外把
`management.endpoint.archaius/restart/pause/resume/env.post.enabled` 设为 `false`；这些是最低优先级
取值，你的任何显式声明都会覆盖它们。

## 故障排查

| 现象 | 原因 / 处理 |
|---|---|
| `registration` bean 是 `MultipleRegistration`，但只有一个注册中心真正生效 | 其余注册中心没有匹配的 `Registration` 类型；`special(...)` 返回 `null`，遍历时被跳过 |
| `/actuator/microsphere/service/registration` 返回 404 | 端点默认关闭，**并且**必须出现在 `management.endpoints.web.exposure.include` 中 |
| 完全收不到注册事件 | `aspectjweaver`/AOP 未生效，或 `spring.cloud.service-registry.auto-registration.enabled=false` |
| refresh 后 Tomcat 参数没变 | `management.endpoint.refresh.enabled` 仍为 `false`；新值不是正数；或属性名不以 `server` 开头 |
| 缺少 `web.mappings` | 自动服务注册不可用（没有 `Registration` bean）——该特性需要一个真实或简单的注册对象 |
| `getInstances` 出现重复服务 | `mode=union` 的预期行为；需要“先命中即返回”请使用默认的 `CompositeDiscoveryClient` |

## 参见

- [openfeign.md](./openfeign.md) — 基于 `SpecificationCustomizer` 的 Feign 组件热替换
- [parent.md](./parent.md) · [dependencies.md](./dependencies.md)
- [用户文档首页](../../README.md)
