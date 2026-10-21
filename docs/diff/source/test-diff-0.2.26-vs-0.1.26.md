# src/test 实现差异：0.2.26 vs 0.1.26

> 生成时间：2026-10-20
> 对比基线：tag `0.2.26`（新版，release 线，Java 17 + Spring Boot 3.x/4.x + Spring Cloud 2022+）
> 对比对象：tag `0.1.26`（旧版，release-1.x 线，Java 8 + Spring Boot 2.3–2.6 + Spring Cloud Hoxton–2021）
> 对比范围：各 Maven 模块 `src/test`（Java 测试源码与测试资源）；`src/main` 差异见[姊妹文档](./main-diff-0.2.26-vs-0.1.26.md)
> 英文版：[test-diff-0.2.26-vs-0.1.26-en.md](./test-diff-0.2.26-vs-0.1.26-en.md)

## 一、排除规则

同 src/main 对比：排除 Java EE→Jakarta EE、Java 8/17 语法与 API、纯注释、格式等价重构（日志门面统一、静态 import、判空简化等无行为影响项在表中标注但不计为实现差异）。

## 二、模块匹配总览

| 0.2.26 模块 | 0.1.26 模块 | 匹配 | src/test 文件数 | 有差异文件 |
|---|---|---|---|---|
| microsphere-spring-cloud-parent | 同名 | ✅ | 0 | — |
| microsphere-spring-cloud-dependencies | 同名 | ✅ | 0 | — |
| microsphere-spring-cloud-commons | 同名 | ✅ | 54 | 5（Java 4 + resources 1） |
| microsphere-spring-cloud-openfeign | 同名 | ✅ | 42 | 25（全部 Java） |

两 tag 的测试文件清单完全一致，无新增/删除文件。

## 三、microsphere-spring-cloud-commons（src/test）

| # | 文件 | 新版（0.2.26） | 旧版（0.1.26） | 差异原因 |
|---|---|---|---|---|
| T1 | `client/service/registry/DefaultRegistrationTest.java` | `testIsSecure` 断言 `assertTrue`；测试夹具增加 `setSecure(true)`，URI 改为 https | 断言 `assertFalse` | **跟随主代码 M1（功能需要）**：验证 secure 传播后的新行为 |
| T2 | `client/service/util/ServiceInstanceUtilsTest.java` | 新增 `DefaultServiceInstance.getUri(...)` 一致性断言；夹具补 `setSecure(false)` | 无 | **跟随主代码 M1**：补强 URI/secure 等价性覆盖 |
| T3 | `fault/tolerance/tomcat/autoconfigure/TomcatFaultToleranceAutoConfigurationIntegrationTest.java` | 设置属性 `server.max-http-request-header-size` | 设置 `server.max-http-header-size` | **API 兼容（配置键迁移）**：与主代码 M2 联动 |
| T4 | `client/service/registry/endpoint/BaseServiceRegistrationEndpointTest.java` | import `org.springframework.boot.test.web.server.LocalServerPort` | import `org.springframework.boot.web.server.LocalServerPort` | **API 兼容**：Spring Boot 3.x 中该类迁移包位置 |
| T5 | `resources/META-INF/config/default/test.yaml` | profile 激活改用 `spring.config.activate.on-profile`（5 处） | 使用已废弃的 `spring.profiles` | **API 兼容**：Spring Boot 2.4+ 配置文件 profile 语法变更 |

## 四、microsphere-spring-cloud-openfeign（src/test）

| # | 文件（组） | 新版（0.2.26） | 旧版（0.1.26） | 差异原因 |
|---|---|---|---|---|
| T6 | `autorefresh/BaseTest.java` | `@TestPropertySource` 全部键使用 `spring.cloud.openfeign.client.config.*` | 使用 `feign.client.config.*` | **API 兼容（配置键迁移）**：与主代码 M4/M5 联动 |
| T7 | 5 个 `*ChangedTest.java`（encoder / errordecoder / querymapencoder / requestInterceptor / retryer） | `afterTestComponentConfigKey()` 返回 `spring.cloud.openfeign.client.config.my-client.*` | 返回 `feign.client.config.my-client.*` | **API 兼容（配置键迁移）**：同上 |
| T8 | `FeignComponentAssert.java` + 6 个 `*ComponentAssert.java` + `ObservableFeignInvocationHandler.java`（共 8 个文件） | 断言钩子签名改为 `expect(Object configuration, ResponseHandler responseHandler, ...)`；反射读取 MethodHandler 的 `methodHandlerConfiguration`/`responseHandler` 字段，并用 `ClassUtils.isPresent("feign.MethodHandlerConfiguration")` 做版本探测，旧版 Feign 回退到 MethodHandler 本身 | 直接从 `InvocationHandlerFactory.MethodHandler` 字段读取组件 | **API 兼容 + 功能需要**：新版 Feign 将 decoder/encoder 等依赖从 MethodHandler 迁移到 `ResponseHandler` 与 `MethodHandlerConfiguration`；探测逻辑保留跨版本兼容 |
| T9 | `autorefresh/AutoRefreshCapabilityTest.java`、`components/DecoratedFeignComponentTest.java` | 使用 `FeignClientFactory` / `NamedContextFactory<FeignClientSpecification>` | 使用 `FeignContext` | **API 兼容**：与主代码 M7 联动 |
| T10 | `autorefresh/AutoRefreshCapabilityCustomizerTest.java` | 直接 `new FeignClientSpecification(...)` | 反射查找构造器实例化 Specification | **API 兼容（实现简化）**：与主代码 M6 联动 |
| T11 | `components/DecoratedQueryMapEncoderTest.java` | 实现 `configureDelegateClass`（`setQueryMapEncoder`）、恢复父类两个 componentType 测试、新增 `testGetQueryMapEncoder`（null 配置 / 未找到 MethodHandle 用例） | 空实现并跳过 componentType 测试 | **功能需要（测试覆盖补强）**：配合主代码 M8 新功能的回归测试 |
| T12 | `components/DecoratedRetryerTest.java` | `continueOrPropagate` 断言改为 `assertThrows(RetryableException.class, ...)` | 直接调用、不校验异常 | **测试正确性修复**：`Retryer.Default` 超过重试上限必然抛异常，旧写法无法验证行为 |
| T13 | `constants/FeignConstantsTest.java` | 补测 `FEIGN_CAPABILITY_CLASS_NAME` 等 4 个常量 | 仅测 2 个常量 | **测试覆盖补强**：常量在主代码两版均存在，断言补齐 |
| T14 | `components/CompositedRequestInterceptorTest.java` | 删除 `getDefaultRequestHeaders`/`getDefaultQueryParameters` 的判空初始化分支；容器构造改用 `newLinkedList()` | 手动判空后 `HashMap`/`ArrayList` 初始化 | **API 兼容 + 等价重构**：新版 `FeignClientConfiguration` getter 返回非 null 默认集合，判空成为冗余 |
| T15 | `errordecoder/AErrorDecoder.java`、`querymapencoder/A/BQueryMapEncoder.java`、`retryer/ARetry.java` | 日志改用 `io.microsphere.logging` 门面；`MapUtils.newHashMap()` | 直接使用 `org.slf4j`、`new HashMap<>()` | **统一依赖 / 等价重构**：无行为差异 |

## 五、差异原因归类

| 归类 | 涉及条目 | 说明 |
|---|---|---|
| 配置键迁移（`feign.client.*` → `spring.cloud.openfeign.client.*`；`server.max-http-header-size` 更名） | T3、T6、T7 | 与主代码联动的 API 兼容适配 |
| Spring/Feign 类废弃、包位置与内部结构变化 | T4、T8、T9 | 其中 T8 的 `isPresent` 版本探测是有意的跨版本兼容设计 |
| 跟随主代码功能变化 | T1、T2、T11 | secure 传播、QueryMapEncoder 可配置化的回归测试 |
| 测试正确性修复 | T12 | 异常断言补全，属独立质量改进 |
| 测试覆盖补强 | T13 | 常量断言补齐 |
| 等价重构（已排除或仅标注） | T5、T10、T14、T15 | 语法/API 写法更新，无行为影响 |

## 六、结论

1. src/test 共 30 个文件存在 diff，但**绝大多数是 src/main 差异的机械联动**（配置键、类型替换、反射字段路径）。
2. 具有独立价值的改动有 3 类：`DecoratedRetryerTest` 异常断言修复（T12）、`FeignConstantsTest` 覆盖补全（T13）、`ObservableFeignInvocationHandler` 的 Feign 版本探测兼容设计（T8）。
3. 两 tag 测试文件清单一致，无测试新增或删除；测试行为预期随主代码 secure 传播修复同步调整（T1、T2）。
