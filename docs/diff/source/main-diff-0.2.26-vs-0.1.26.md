# src/main 实现差异：0.2.26 vs 0.1.26

> 生成时间：2026-10-20
> 对比基线：tag `0.2.26`（新版，release 线，Java 17 + Spring Boot 3.x/4.x + Spring Cloud 2022+）
> 对比对象：tag `0.1.26`（旧版，release-1.x 线，Java 8 + Spring Boot 2.3–2.6 + Spring Cloud Hoxton–2021）
> 对比范围：各 Maven 模块 `src/main`（Java 源码与资源文件）；`src/test` 差异见[姊妹文档](./test-diff-0.2.26-vs-0.1.26.md)
> 英文版：[main-diff-0.2.26-vs-0.1.26-en.md](./main-diff-0.2.26-vs-0.1.26-en.md)

## 一、排除规则

以下差异不计入实现差异：

| 排除类别 | 说明 |
|---|---|
| Java EE → Jakarta EE API | `javax.servlet.*` → `jakarta.servlet.*` 等命名空间迁移 |
| Java 8 / Java 17 语法与 JDK API | 纯语言级语法差异 |
| 纯注释/文档差异 | 仅 Javadoc、`@see` 变更，无行为影响 |
| 格式等价重构 | 文件末尾换行符、静态 import 化等 |

## 二、模块匹配总览

| 0.2.26 模块 | 0.1.26 模块 | 匹配 | src/main/java | src/main/resources |
|---|---|---|---|---|
| microsphere-spring-cloud-parent | 同名 | ✅ | 无 Java 源码 | 无差异 |
| microsphere-spring-cloud-dependencies | 同名 | ✅ | 无 Java 源码 | 无差异 |
| microsphere-spring-cloud-commons | 同名 | ✅ | 61 个文件，3 个有差异（2 处实质差异） | `spring.factories` 瘦身（自动配置迁至 `AutoConfiguration.imports`，清单不变） |
| microsphere-spring-cloud-openfeign | 同名 | ✅ | 21 个文件，14 个有差异（6 处实质差异） | `spring.factories` 删除（迁至 `AutoConfiguration.imports`，清单不变） |

两 tag 的 Java 源文件清单一致；唯一文件级变化是 openfeign 的 `META-INF/spring.factories` 在 0.2.26 被删除。

## 三、src/main/java 差异

### 3.1 microsphere-spring-cloud-commons

| # | 文件 | 新版（0.2.26） | 旧版（0.1.26） | 差异原因 |
|---|---|---|---|---|
| M1 | `client/service/util/ServiceInstanceUtils.java` | `setProperties` 调用 `target.setSecure(source.isSecure())` | `target.setUri(source.getUri())`，secure 标志未传播 | **功能需要 / 行为修复**：显式传播安全标志，避免 HTTPS 实例被降级成 http URI |
| M2 | `fault/tolerance/tomcat/event/TomcatDynamicConfigurationListener.java` | 调用 `getMaxHttpRequestHeaderSize()` | 调用 `getMaxHttpHeaderSize()` | **API 兼容**：Spring Boot 3.x 中 `ServerProperties` 该访问器更名，动态刷新跟随新 getter |
| M3 | `client/service/registry/autoconfigure/WebMvcServiceRegistryAutoConfiguration.java` | `jakarta.servlet.Filter`；删除一条 `@see` | `javax.servlet.Filter` | **已排除**（Jakarta EE 迁移 + 纯注释） |

### 3.2 microsphere-spring-cloud-openfeign

| # | 文件 | 新版（0.2.26） | 旧版（0.1.26） | 差异原因 |
|---|---|---|---|---|
| M4 | `autorefresh/AutoRefreshCapability.java` | contextId 读取属性 `spring.cloud.openfeign.client.name` | 读取 `feign.client.name` | **API 兼容（配置键迁移）**：Spring Cloud 2022 起 OpenFeign 属性统一加 `spring.cloud.openfeign.` 前缀 |
| M5 | `autorefresh/FeignClientConfigurationChangedListener.java` | 监听前缀 `spring.cloud.openfeign.client.config.` | 监听前缀 `feign.client.config.` | **API 兼容（配置键迁移）**：决定配置变更后哪些客户端触发刷新 |
| M6 | `autorefresh/AutoRefreshCapabilityCustomizer.java` | `instanceof FeignClientSpecification` + 直接调用 `setConfiguration(...)` | `resolveClass` 动态解析类名 + `BeanUtils.findWriteMethod` 反射调用 setter | **API 兼容（实现简化）**：1.x 因 Specification 类名/包名跨版本不稳定而反射兜底；新版类编译期可用，改强类型直调，逻辑等价 |
| M7 | `components/DecoratedFeignComponent.java` | 依赖 `NamedContextFactory<FeignClientSpecification>`；`instantiate` 参数放宽为 `Class<? extends T>`；trace 日志加 `isTraceEnabled()` 包裹 | 依赖具体类 `FeignContext` | **API 兼容**：`FeignContext` 在新版 Spring Cloud 中废弃；日志包裹为防御性性能优化 |
| M8 | `components/DecoratedQueryMapEncoder.java` | 静态 `MethodHandle` 调用 `FeignClientConfiguration.getQueryMapEncoder()`，`componentType()` 按配置解析、未配置时回退 `PageableSpringQueryMapEncoder` | `componentType()` 固定返回 `PageableSpringQueryMapEncoder.class` | **功能需要 + API 兼容**：`getQueryMapEncoder` 属性仅新版存在；`MethodHandle` 找不到即返回 null，实现对旧版配置的优雅降级 |
| M9 | `components/DecoratedRetryer.java` | `componentType()` 未配置时回退 `Retryer.class`（接口） | 回退 `Retryer.Default.class` | **行为差异**：与 M8 统一为「按接口从上下文解析 Bean」，具体实现交由 `NamedContextFactory` 决定 |
| M10 | `DecoratedContract/Decoder/Encoder/ErrorDecoder.java`（4 个文件） | 构造器参数 `FeignContext` → `NamedContextFactory<FeignClientSpecification>` | `FeignContext` | **API 兼容**：随 M7 签名联动，无独立逻辑变化 |
| — | `CompositedRequestInterceptor.java`、`NoOpRequestInterceptor.java`、`ConditionalOnOpenFeignAvailable/Enabled.java`（4 个文件） | 仅 Javadoc/`@see` 变更 | — | **已排除**（纯注释） |

## 四、src/main/resources 差异

| # | 模块 | 文件 | 新版（0.2.26） | 旧版（0.1.26） | 差异原因 |
|---|---|---|---|---|---|
| R1 | commons | `META-INF/spring.factories` | 仅保留 `NacosServiceRegistry = NacosRegistration` 映射；10 个自动配置类迁至 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（两 tag 均存在该文件且内容一致） | 通过 `EnableAutoConfiguration=` 在 spring.factories 中注册 10 个自动配置类 | **API 兼容**：Spring Boot 3 移除 spring.factories 自动配置注册方式；0.1.26 上双通道重复注册，0.2.26 已清理 |
| R2 | openfeign | `META-INF/spring.factories` | 文件删除；`FeignAutoConfiguration`、`FeignClientAutoRefreshAutoConfiguration` 已在 `AutoConfiguration.imports` 注册（两 tag 内容一致） | 通过 spring.factories 与 imports 双通道注册上述 2 个自动配置类 | **API 兼容**：同 R1 |

## 五、差异原因归类

| 归类 | 涉及条目 | 说明 |
|---|---|---|
| Spring Cloud 配置键迁移（`feign.client.*` → `spring.cloud.openfeign.client.*`） | M4、M5 | **下游用户配置需同步迁移** |
| Spring Boot 3 自动配置注册机制迁移（spring.factories → AutoConfiguration.imports） | R1、R2 | Boot 3 强制要求，注册类清单本身未变 |
| Spring API 废弃/更名（`FeignContext`、`getMaxHttpHeaderSize`） | M2、M7、M10 | API 兼容适配 |
| 功能需要（secure 传播修复、QueryMapEncoder 可配置化） | M1、M8 | 真实行为变化 |
| 行为差异（默认回退 `Retryer.Default` → `Retryer`） | M9 | 语义统一为按接口解析 |
| 实现简化（反射兜底 → 强类型直调） | M6 | 逻辑等价重构 |
| 已排除（Jakarta EE、纯注释、格式） | M3 及 4 个纯注释文件 | 无行为影响 |

## 六、结论

1. src/main 实质差异共 **8 处**（commons 2 处 + openfeign 6 处），高度集中在 OpenFeign 4.x 升级适配：类型泛化、配置键迁移、新增可配置 QueryMapEncoder。
2. resources 层为注册机制迁移（spring.factories → AutoConfiguration.imports），自动配置内容完全一致，无功能增减。
3. 对使用者唯一的运行时行为变化点是 `ServiceInstanceUtils.setProperties` 的 secure 传播与 `DecoratedRetryer` 默认回退类型；其余均为框架升级必需适配。
