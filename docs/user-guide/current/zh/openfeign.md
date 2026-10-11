[English](../openfeign.md) · [1.x 版本线](../../1.x/zh/openfeign.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-openfeign` 使用指南（0.2.x）

在不重启的前提下**热替换 Feign 组件**：encoder、decoder、contract、retryer、error decoder、
query-map encoder、request interceptor，以及默认请求头与默认查询参数。模块把每个组件包装成委托代理，
只要 `spring.cloud.openfeign.client.config.*` 发生变化就会重新解析被委托对象，因此一次
`POST /actuator/refresh` 即可改变行为——既不重建 Feign 客户端，也不重启 JVM。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release` |
| 版本线 | 0.2.x |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-openfeign` |
| 制品类型 | `jar` |
| 最新已发布版本 | **`0.2.26`**（分支头部为 `0.2.27-SNAPSHOT`，未发布） |
| 仓库 | Maven Central |
| Java | **17+** |
| Spring Boot 范围 | 3.0.x – 4.0.x |
| Spring Cloud 范围 | 2022.0.x – 2025.1.x |
| 编译依赖 | `microsphere-spring-cloud-commons`（见 [commons.md](./commons.md)） |
| 需要你自行引入 | `spring-cloud-starter-openfeign`（本模块中声明为 `optional`） |
| 属性命名空间 | `spring.cloud.openfeign.client.config.*`（Boot 3 / Cloud 2022+ 命名空间；1.x 使用 `feign.client.config.*`） |

## 适用场景

- 需要在**运行中的节点**上更换序列化方式、重试策略、错误映射或鉴权 Header——例如故障处置期间、
  新 decoder 的灰度验证、或必须免发布落地的契约修正。
- 希望完全由外部化配置（配置中心、Kubernetes ConfigMap、`/actuator/env`）驱动按客户端的覆盖项。
- 如果你的 Feign 配置是静态的，则**不需要**本模块——完全可以不加 `@EnableFeignAutoRefresh`，
  此时模块的任何行为都不会生效。

## 安装

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    </dependency>
    <!-- 非传递依赖：本模块把该 starter 声明为 optional -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!-- 刷新触发器来自 Spring Cloud Context -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

`microsphere-spring-cloud-commons` 会随之传递引入（自动刷新能力正是通过它的
`SpecificationCustomizer` 扩展点注入的）。负载均衡方式由你决定，例如
`spring-cloud-starter-loadbalancer`。

## 版本兼容性

| Profile | Spring Boot | Spring Cloud | 所使用的 OpenFeign 属性命名空间 |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | `spring.cloud.openfeign.client.config.` |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | 同上 |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | 同上 |
| `spring-cloud-2025`**（默认）** | 3.5.15 | 2025.0.3 | 同上 |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | 同上 |

必须使用 Java 17+。模块使用的上下文工厂类型是
`NamedContextFactory<FeignClientSpecification>`（与 Spring Cloud OpenFeign 4.x 兼容的签名），
并且配置的 `query-map-encoder` 类在刷新时会被真正采用（通过 `MethodHandle` 解析，从而兼容访问方式
不同的 Spring Cloud 版本）。

## 开启：`@EnableFeignAutoRefresh`

```java
@SpringBootApplication
@EnableFeignClients(clients = MyClient.class)
@EnableFeignAutoRefresh          // 没有任何属性
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

该注解**不带任何属性**，连 `enabled` 也没有。它的机制只是 `@Import` 了内嵌标记类
`EnableFeignAutoRefresh.Marker`，而自动配置 `FeignClientAutoRefreshAutoConfiguration` 以
`@ConditionalOnBean(Marker.class)` 作为门槛。这也是模块自身的测试可以直接注册
`EnableFeignAutoRefresh.Marker` 而不使用注解就能激活特性的原因。要关闭本模块，请使用属性开关
（见“关闭方式”）。

## 可配置属性

所有配置都位于 `spring.cloud.openfeign.client.config.<client-name>.` 之下，其中
`<client-name>` 是 Feign 的 **contextId**（`contextId`，否则取 `name`），或者字面量 `default`：

| 键后缀 | 被替换的 Feign 组件 | 说明 |
|---|---|---|
| `retryer` | `feign.Retryer` | |
| `encoder` | `feign.codec.Encoder` | |
| `decoder` | `feign.codec.Decoder` | |
| `error-decoder` | `feign.codec.ErrorDecoder` | 兜底 `ErrorDecoder.Default` |
| `contract` | `feign.Contract` | |
| `query-map-encoder` | `feign.QueryMapEncoder` | 兜底 `PageableSpringQueryMapEncoder` |
| `request-interceptors[n]` | `feign.RequestInterceptor`（列表） | 解析组件类型时下标会被忽略 |
| `default-request-headers.<name>` | 由生成的 `RequestInterceptor` 注入 | 仅在请求头不存在时添加 |
| `default-query-parameters.<name>` | 由生成的 `RequestInterceptor` 注入 | 仅在参数不存在时添加 |

可接受的写法：短横线形式（`error-decoder`）与驼峰形式（`errorDecoder`）等价；组件注册表在映射到
Feign 类型之前会剥离数组下标（`request-interceptors[0]` → `request-interceptors`）。

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            encoder: com.example.feign.AEncoder
            decoder: com.example.feign.ADecoder
            error-decoder: com.example.feign.AErrorDecoder
            query-map-encoder: com.example.feign.AQueryMapEncoder
            retryer: com.example.feign.ARetry
            request-interceptors[0]: com.example.feign.ARequestInterceptor
            default-request-headers:
              app: my-app
            default-query-parameters:
              sign: my-sign
          my-client:            # 按客户端覆盖，优先于 default
            connect-timeout: 2000
            read-timeout: 5000
```

## 运行时更换组件

对应模块自身的测试用例（`DecoderChangedTest`）：把 `my-client` 的 decoder 从 `ADecoder` 换成
`BDecoder`。

```java
public class ADecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) { /* … */ return null; }
}

public class BDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) { /* 新行为 */ return null; }
}

@FeignClient(contextId = "my-client", name = "my-client")
public interface MyClient {
    @GetMapping("echo")
    String echo(@RequestBody String value, @RequestParam("version") String version);
}
```

```bash
# 1）让新值进入环境——配置中心、ConfigMap 或 /actuator/env 均可
# 2）触发刷新
curl -s -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' \
     -d '[{"spring.cloud.openfeign.client.config.my-client.decoder":"com.example.feign.BDecoder"}]'
```

执行链路：

```
/actuator/refresh  →  ContextRefresher  →  EnvironmentChangeEvent（spring-cloud-context）
        →  FeignClientConfigurationChangedListener（过滤 "spring.cloud.openfeign.client.config." 前缀）
        →  FeignComponentRegistry.refresh(clientName, changedKeys)
        →  DecoratedDecoder.refresh()   // delegate := null
        →  下一次 MyClient.echo(..) 调用时惰性解析出新 bean/新类
```

不会重建任何 Feign 客户端：委托包装器原地保留，在下一次调用时重新解析目标。
`EnvironmentManager.setProperty(...)`（模块测试所用的方式）与自己发布
`EnvironmentChangeEvent` 是等价的触发手段——监听器只关心事件本身。

> 由于 commons 的 `endpoints.properties`，`management.endpoint.refresh.enabled` 默认是 `false`；
> 使用上面的 curl 之前请先置为 `true` 并暴露 `refresh` 端点。

## `default.*` 与按客户端的语义差别

| 你修改的键 | 效果 |
|---|---|
| `spring.cloud.openfeign.client.config.my-client.decoder` | 只重新解析 `my-client` 的 decoder；若涉及拦截器变更，同时刷新它的拦截器组合体 |
| `spring.cloud.openfeign.client.config.default.decoder` | **所有**已注册且组件类型匹配 `feign.codec.Decoder` 的客户端都会被重新解析；只要存在 `RequestInterceptor` 变更，所有组合拦截器都会刷新 |

被装饰组件内部的解析顺序是**先看 default 配置，再看该客户端自己的配置**——因此按客户端的值会覆盖
default，而删除某个按客户端配置后会回落到 default。`default-request-headers` 与
`default-query-parameters` 采用同样的合并顺序（default 优先、`putIfAbsent`）。

## 自定义组件类的要求

写在上述属性里的类必须可被模块实例化，二选一：

- 要么是带 `public` 无参构造、实现相应 Feign SPI（`feign.codec.Decoder`、
  `feign.RequestInterceptor` 等）的 `public` 类——**不必**是 Spring bean；
- 要么是该类型的 bean，并且存在于该客户端的 **Feign 子上下文**中——注册表会先向
  `NamedContextFactory` 索取该类型的 provider，取不到时才直接实例化配置的类。

当组件需要注入依赖（ObjectMapper、HTTP 客户端、指标注册表）时，请走 bean 这条路：把它声明在
`@FeignClient` 的 `configuration = { … }` 中或 default 配置里，然后在属性中写出类名。

## 关闭方式

| 手段 | 效果 |
|---|---|
| `microsphere.spring.cloud.openfeign.enabled=false` | 关闭本模块**两个**自动配置（`ConditionalOnOpenFeignEnabled` 使用 `matchIfMissing=true`，因此除 `false` 之外的值都保持开启） |
| 不加 `@EnableFeignAutoRefresh`（也不注册 `Marker` bean） | 自动刷新不生效：`FeignClientAutoRefreshAutoConfiguration` 需要 `Marker` bean；`FeignAutoConfiguration` 仍会注册那颗种子拦截器 |
| 从 classpath 移除 `feign.Feign` / `feign.Capability` / `FeignClientFactoryBean` | 类存在性守卫使自动配置退避 |
| 保留依赖但不再使用 | 只要不改动 `spring.cloud.openfeign.client.config.*`，委托包装器的行为与原组件完全一致 |

## 工作原理：10 条事实

| # | 事实 |
|---|---|
| 1 | 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册两个自动配置：`FeignAutoConfiguration` 与 `FeignClientAutoRefreshAutoConfiguration`（本分支没有 `spring.factories`）。 |
| 2 | `FeignAutoConfiguration`（要求存在 `FeignBuilderCustomizer`，排在 OpenFeign 自身自动配置之后）贡献一个 `FeignBuilderCustomizer`，为每个 builder 添加 `NoOpRequestInterceptor.INSTANCE`，确保拦截器管道始终被激活。 |
| 3 | `FeignClientAutoRefreshAutoConfiguration` 要求 `feign.Capability` + `FeignClientFactoryBean` + 一个 `EnableFeignAutoRefresh.Marker` bean，并排在 commons 的 `SpecificationAutoConfiguration` 之后。 |
| 4 | 它定义两个 bean：`FeignComponentRegistry`（使用 `FeignClientProperties.getDefaultConfig()` 与 `BeanFactory` 构造）和 `AutoRefreshCapabilityCustomizer`，二者都是 `@ConditionalOnMissingBean`。 |
| 5 | 在 `ApplicationReadyEvent` 上它以**手工方式**（而非 bean）注册 `FeignClientConfigurationChangedListener`，从而保证排在 `ConfigurationPropertiesRebinder` 之后——`FeignClientProperties` 先完成重绑定，刷新逻辑才去读取环境。 |
| 6 | 该 Capability 是通过把类**前置**到以 `default.` 开头的 `FeignClientSpecification` 的 `configuration()` 数组中来进入 Feign 的；随后 Spring 为每个 Feign 子上下文创建一个 `AutoRefreshCapability`。 |
| 7 | `AutoRefreshCapability` 在子上下文中通过 `spring.cloud.openfeign.client.name` 解析所属客户端名，并把 `Retryer`、`Contract`、`Decoder`、`Encoder`、`ErrorDecoder`、`QueryMapEncoder` 包装成 `Decorated*` 代理登记到注册表；`RequestInterceptor` 被折叠进每客户端一个 `CompositedRequestInterceptor`（组合体只交给 builder 一次，后续拦截器降级为 no-op）。 |
| 8 | 任何 `DecoratedFeignComponent.refresh()` 做的事就是把 `volatile delegate` 置为 `null`；下一次调用时按当时生效的配置类重新解析 bean 或实例化。 |
| 9 | `CompositedRequestInterceptor.refresh()` 会用 default + 客户端配置重建有序集合：配置中的拦截器类、为 `default-request-headers.*` 生成的 Header 拦截器、为 `default-query-parameters.*` 生成的参数拦截器；`apply()` 在锁内遍历，因而刷新对请求是安全的。 |
| 10 | 只有以 `spring.cloud.openfeign.client.config.` 开头的键会触发行为；前缀之后的第一段路径决定客户端（`default` 会广播到所有同组件类型的客户端）。 |

## 限制与排查

| 现象 / 限制 | 说明 |
|---|---|
| 组件没有变化 | 键名不是以 `spring.cloud.openfeign.client.config.` 开头；客户端名既不是 contextId 也不是 `default`；或者根本没有发布 `EnvironmentChangeEvent` |
| `query-map-encoder` 相关的版本兼容噪音 | 该访问器通过 `MethodHandle` 解析；在缺少 `FeignClientConfiguration.getQueryMapEncoder()` 的 Spring Cloud 版本上会回落到 `PageableSpringQueryMapEncoder` |
| 拦截器执行了两次 | 只有当你**既**注册了自己的 `RequestInterceptor` bean **又**在配置里写了类名时才会如此；组合体会在单个客户端内按类去重，但保持插入顺序 |
| 需要依赖注入的组件构造失败 | 改为把它作为 bean 放进 Feign 子上下文，而不是只写类名 |
| `InvocationHandlerFactory`、`Client`、`Feign.Builder` | **不支持**热替换——只有上表七种组件类型以及默认 Header/参数会被装饰 |
| 超时/日志级别（`connect-timeout`、`read-timeout`、`logger-level`） | 由 Spring Cloud OpenFeign 自身处理，不属于本模块 |

## 参见

- [commons.md](./commons.md) — `SpecificationCustomizer`、刷新与 Actuator 端点默认值、特性目录
- [parent.md](./parent.md) · [dependencies.md](./dependencies.md)
- [1.x 版本线](../../1.x/zh/openfeign.md) — 同样行为，使用 `feign.client.config.` 命名空间
- [用户文档首页](../../README.md)
