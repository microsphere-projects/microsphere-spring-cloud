[English](../openfeign.md) · [0.2.x 版本线](../../current/zh/openfeign.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-openfeign` — 用户指南（0.1.x）

当 `feign.client.config.*` 配置发生变化时，热重载 Spring Cloud OpenFeign 的按客户端
组件——`Decoder`、`Encoder`、`ErrorDecoder`、`Retryer`、`Contract`、`RequestInterceptor`
列表、默认请求头与默认查询参数——**无需重启、无需重建 Feign 客户端**。面向 0.1.x
整条版本线沿用的 Hoxton 时代属性命名空间构建。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release-1.x` |
| 版本线 | 0.1.x |
| 最新已发布版本 | **`0.1.26`**（分支当前开发版本为 `0.1.27-SNAPSHOT`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-openfeign` |
| 仓库 | Maven Central |
| Java 基线 | Java 8 |
| Spring Boot 范围 | 2.3.x – 2.6.x |
| Spring Cloud OpenFeign 范围 | 2.2.x – 3.1.x |
| 被监听的配置前缀 | **`feign.client.config.`**（不是 `spring.cloud.openfeign.client.config.`） |
| 自动配置注册 | `META-INF/spring.factories`（同时附带 `AutoConfiguration.imports`，但 Boot 2.3–2.6 忽略它） |
| 总开关 | `microsphere.spring.cloud.openfeign.enabled`（默认 `true`） |

## 用途与适用场景

Spring Cloud OpenFeign 只在创建某客户端子容器时读取一次 `feign.client.config.*`，
此后 `Decoder`/`Encoder`/`Retryer` 等实例即被冻结。本模块通过 Feign 的 `Capability`
机制把每个组件包装成可刷新的委托壳，当你修改配置并触发刷新后，**下一次调用**即使用
新配置的类。典型场景：故障处置期间临时更换 `Decoder`、轮转
`default-request-headers` / `default-query-parameters`、在线调整 `retryer` 策略。

## 依赖与启用

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    <version>0.1.26</version> <!-- 或通过 0.1.x BOM / 父 POM 统一管理 -->
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Spring Cloud OpenFeign 对本模块是 **optional** 的——由你自带
（2.2.x – 3.1.x，即 Hoxton.SR12 至 2021.0.x）。Hoxton 下即 Ribbon 时代技术栈；
请注意即便选择 `spring-cloud-2020`/`spring-cloud-2021` profile，0.1.x 版本线中本模块
的刷新机制仍固定使用 **`feign.client.config.`** 命名空间。

随后显式激活自动刷新：

```java
@SpringBootApplication
@EnableFeignClients
@EnableFeignAutoRefresh   // io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`@EnableFeignAutoRefresh` **没有任何属性**；它注册一个 `Marker` bean，供
`FeignClientAutoRefreshAutoConfiguration` 的 `@ConditionalOnBean` 判断。另一个自动配置
`FeignAutoConfiguration` 则是无条件生效的（只要 OpenFeign 在 classpath 上且总开关未关），
它为每个 Feign builder 预置一个 no-op `RequestInterceptor`，确保拦截器管线始终就位。

## 配置键（1.x 命名空间）

所有可刷新设置都位于 `feign.client.config.` 之下：

| 键（`feign.client.config.<client>.` 之后的后缀） | Feign 组件 | 可否刷新 |
|---|---|---|
| `decoder` | `feign.codec.Decoder` | 可 |
| `encoder` | `feign.codec.Encoder` | 可 |
| `error-decoder` | `feign.codec.ErrorDecoder` | 可 |
| `retryer` | `feign.Retryer` | 可 |
| `contract` | `feign.Contract` | 可 |
| `request-interceptors[n]` | `feign.RequestInterceptor`（列表） | 可 |
| `default-request-headers.<name>` | 由组合拦截器应用的请求头 | 可 |
| `default-query-parameters.<name>` | 由组合拦截器应用的查询参数 | 可 |
| `query-map-encoder` | `feign.QueryMapEncoder` | **0.1.x 不可**——见下文限制说明 |
| `feign.client.name`（不在 `config` 之下） | 在每个子容器内解析该客户端的 contextId | 构建期读取，不适用 |

规范形式为 kebab-case；camelCase（`errorDecoder`）与数组下标
（`requestInterceptors[0]`）也会被归一化。

```yaml
feign:
  client:
    name: default            # Feign 子容器内 contextId 的兜底值
    config:
      default:
        decoder: com.acme.feign.ADecoder
        encoder: com.acme.feign.AEncoder
        error-decoder: com.acme.feign.AErrorDecoder
        retryer: com.acme.feign.ARetry
        request-interceptors[0]: com.acme.feign.ARequestInterceptor
        default-request-headers:
          app: my-app
        default-query-parameters:
          sign: my-sign
      my-client:             # 按客户端覆盖，对 contextId "my-client" 优先生效
        decoder: com.acme.feign.BDecoder
```

```java
@FeignClient(contextId = "my-client", name = "my-client")
public interface MyClient {
    @GetMapping("echo")
    String echo(@RequestParam("msg") String msg, @RequestParam("v") String v);
}
```

## 刷新流程

监听器 `FeignClientConfigurationChangedListener` 响应的是 Spring Cloud Context 的
`EnvironmentChangeEvent`——即 `POST /actuator/refresh` 发布的事件。只有以
`feign.client.config.` 开头的键会被处理：前缀之后的第一段被视为客户端名。

1. 在外部化配置（Config Server、Kubernetes ConfigMap 等）中修改
   `feign.client.config.*` 的值。
2. 触发刷新：

   ```bash
   curl -X POST http://localhost:8080/actuator/refresh \
        -H 'Content-Type: application/json' \
        -d '["feign.client.config.my-client.decoder"]'
   ```

3. **下一次**调用 `my-client` 的方法时，委托 `Decoder` 即被重新解析为新的 `BDecoder`。
   无需重启，无需重建客户端。

变更键的作用语义：

- `feign.client.config.<client>.*` 变更 → 只刷新**该客户端**匹配的组件及其组合拦截器；
- `feign.client.config.default.*` 变更 → 对**所有**注册了该类型组件的客户端刷新对应
  组件（若变更涉及 `RequestInterceptor` 类型的键，则刷新全部组合拦截器）。

内部机制：借助 [commons](./commons.md) 的 `SpecificationCustomizer` 扩展点，
`AutoRefreshCapability` 配置类被注入每个客户端的子容器；该 Capability 把组件包装成
按 contextId 注册的 `Decorated*` 壳，并为每个客户端维护一个
`CompositedRequestInterceptor` 吸收拦截器、默认请求头与默认查询参数。`refresh()` 只是
丢弃当前委托，下次调用时再惰性重新解析。

## 自定义类的要求

配置键的值是**类的全限定名**。自定义类只需满足：

- `public` 类，实现对应的 Feign 接口
  （`Decoder`、`Encoder`、`ErrorDecoder`、`Retryer`、`Contract`、`RequestInterceptor`）；
- 拥有**默认（无参）构造器**。

它**不必**是 Spring bean——若容器中存在该类型的 bean 则优先使用，否则直接实例化类。
模块自身的测试正是这样用的：`ADecoder`/`BDecoder` 是普通类，仅通过
`@TestPropertySource(properties = "feign.client.config.default.decoder=…ADecoder")`
接线。

## `query-map-encoder` 限制（0.1.x）

在本版本线上，`DecoratedQueryMapEncoder` 的组件类型**硬编码为
`PageableSpringQueryMapEncoder.class`**。设置
`feign.client.config.<client>.query-map-encoder=…` 能影响客户端的初始构建，但之后的
修改 + 刷新**不会**把 `QueryMapEncoder` 委托换成所配置的类。0.2.x 版本线才会解析配置的
类（回退 `PageableSpringQueryMapEncoder`）——若需要可刷新的 `QueryMapEncoder`，只能
升级版本线或重启应用。

## 关闭方式

```properties
# 移除本模块的全部自动配置
microsphere.spring.cloud.openfeign.enabled=false
```

也可以不标注 `@EnableFeignAutoRefresh`：缺少其 `Marker` bean 时，
`FeignClientAutoRefreshAutoConfiguration` 整体退让，任何组件都不会被包装
（除非总开关为 `false`，`FeignAutoConfiguration` 的 no-op 拦截器预置仍会生效）。
当 classpath 上没有 `feign.Feign` / `feign.Capability` / `FeignClientFactoryBean` 时，
自动配置同样退让。

## 版本兼容性

| 运行环境 | 是否支持 |
|---|---|
| Java 8 / 11 / 17 | 支持 |
| Spring Boot 2.3.x – 2.6.x | 支持 |
| Spring Cloud Hoxton.SR12（Ribbon 时代）、2020.0.x、2021.0.x——Spring Cloud OpenFeign 2.2.x – 3.1.x | 支持 |
| `spring.cloud.openfeign.client.config.*` 属性命名空间 | **不支持——那是 0.2.x 的行为** |

## 与 0.2.x 的差异

| 方面 | 0.1.x（`release-1.x`） | 0.2.x（`release`） |
|---|---|---|
| 被监听的配置前缀 | `feign.client.config.` | `spring.cloud.openfeign.client.config.` |
| contextId 属性 | `feign.client.name` | `spring.cloud.openfeign.client.name` |
| 子容器工厂类型 | `org.springframework.cloud.openfeign.FeignContext` | `NamedContextFactory<FeignClientSpecification>` |
| `query-map-encoder` 刷新 | **不可刷新**（硬编码 `PageableSpringQueryMapEncoder`） | 解析配置类（先当前客户端、后默认配置），回退 `PageableSpringQueryMapEncoder` |
| `AutoRefreshCapabilityCustomizer` | 按类名解析 `FeignClientSpecification`、反射调用 `setConfiguration`（无编译期依赖） | 直接 `instanceof` + setter |
| 自动配置注册 | `spring.factories` 实际生效（附带但被忽略的 `.imports`） | 仅 `AutoConfiguration.imports` |
| 总开关键 | `microsphere.spring.cloud.openfeign.enabled` | 相同 |
| Hoxton 构建支持 | 模块 profile 追加 test 作用域的 `spring-cloud-starter-netflix-ribbon` | 彻底排除 Ribbon |
