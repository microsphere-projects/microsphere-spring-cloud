[English](../parent.md) · [1.x 版本线](../../1.x/zh/parent.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-parent` 使用指南（0.2.x）

microsphere-spring-cloud **0.2.x** 版本线（`release` 分支）的父 POM。它统一管理 Spring Boot /
Spring Cloud / Spring Cloud Alibaba / Microsphere Spring Boot 的依赖版本，聚合测试相关 BOM，
并为每一个受支持的 Spring Cloud 发布列车提供一个 Maven profile，使同一份源码可以在
Boot 3.0 到 Boot 4.0 之间构建和测试。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release` |
| 版本线 | 0.2.x |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-parent` |
| 制品类型 | `pom`（无代码、无运行时制品） |
| 最新已发布版本 | **`0.2.26`**（分支头部为 `0.2.27-SNAPSHOT`，未发布） |
| 仓库 | Maven Central |
| Java | **17+**（聚合 POM 中 `<java.version>17</java.version>`） |
| Servlet 命名空间 | `jakarta.*` |
| Spring Boot 范围 | 3.0.x – 4.0.x（由 profile 决定） |
| Spring Cloud 范围 | 2022.0.x – 2025.1.x（由 profile 决定） |
| 上游依赖 | `microsphere-spring-boot` **0.2.34** |
| 构建父 POM | `io.github.microsphere-projects:microsphere-build` **0.3.16** |
| 测试栈 | JUnit Jupiter **6.1.3**、Testcontainers **2.0.5** |

## 适用场景

以下情况建议使用本父 POM：

- 你的应用或内部库基于 0.2.x 版本线，希望把 Spring Boot / Spring Cloud / Spring Cloud Alibaba /
  Microsphere 的版本集中在**一处**声明；
- 你希望通过切换 Maven profile，而不是修改 POM，来验证同一份代码在 Spring Cloud 2022 → 2025.1
  上的表现；
- 你在构建 Microsphere Spring Cloud 的兄弟模块（例如公司内部依赖
  `microsphere-spring-cloud-commons` 的模块）。

如果你的工程必须保留现有父 POM（通常是 `spring-boot-starter-parent`），请改为导入
[依赖 BOM](./dependencies.md)。见下文“父 POM 还是 BOM？”。

## 继承父 POM

```xml
<project>
    <parent>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-parent</artifactId>
        <version>0.2.26</version>
        <relativePath/> <!-- 从仓库解析，而不是本地目录 -->
    </parent>

    <groupId>com.example</groupId>
    <artifactId>order-service</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <!-- 版本来自被导入的 BOM，无需写 <version> -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

由于父链一直追溯到 `microsphere-build`，使用者同时获得标准 Maven 插件管理（Java 17 编译、
source/javadoc、GPG 签名、`${revision}` 扁平化），即版本对齐之外还有一套可复现的构建配置。

## 可覆盖的属性

父 POM 声明了下列属性；只有在明确知道后果时才覆盖它们：

| 属性 | 0.2.26 中的值 | 设置位置 |
|---|---|---|
| `microsphere-spring-boot.version` | `0.2.34` | 父 POM 属性 |
| `junit-jupiter.version` | `6.1.3` | 父 POM 属性 |
| `testcontainers.version` | `2.0.5` | 父 POM 属性 |
| `spring-boot.version` | 随 profile | **Maven profile** |
| `spring-cloud.version` | 随 profile | **Maven profile** |
| `spring-cloud-alibaba.version` | 随 profile | **Maven profile** |
| `java.version` | `17` | 聚合 POM（`microsphere-spring-cloud`） |

## 聚合的 BOM

父 POM 的 `dependencyManagement` 按声明顺序导入：

| 被导入的 BOM | 版本属性 | 默认 profile 下的取值 |
|---|---|---|
| `org.junit:junit-bom` | `junit-jupiter.version` | 6.1.3 |
| `org.testcontainers:testcontainers-bom` | `testcontainers.version` | 2.0.5 |
| `org.springframework.boot:spring-boot-dependencies` | `spring-boot.version` | 3.5.15 |
| `org.springframework.cloud:spring-cloud-dependencies` | `spring-cloud.version` | 2025.0.3 |
| `com.alibaba.cloud:spring-cloud-alibaba-dependencies` | `spring-cloud-alibaba.version` | 2025.0.0.0 |
| `io.github.microsphere-projects:microsphere-spring-boot-dependencies` | `microsphere-spring-boot.version` | 0.2.34 |

顺序在 Maven 中是有意义的：**最先声明的依赖管理条目生效**。JUnit 与 Testcontainers 被放在最前面，
以避免被 Spring Boot 自带 BOM 悄悄降级。

子 BOM `microsphere-spring-cloud-dependencies` 继承以上全部内容，并额外管理本仓库的两个可运行制品
（`microsphere-spring-cloud-commons`、`microsphere-spring-cloud-openfeign`），版本为 `${revision}`。

## 版本兼容性（profile 矩阵）

共定义 5 个 Spring Cloud profile，**默认激活 `spring-cloud-2025`**。

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | 2022.0.0.2 |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | 2023.0.3.4 |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | 2023.0.3.4 |
| `spring-cloud-2025`**（默认）** | 3.5.15 | 2025.0.3 | 2025.0.0.0 |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | 2025.1.0.0 |

### 构建时切换 profile

```bash
# 默认构建 —— Spring Boot 3.5.15 / Spring Cloud 2025.0.3
mvn clean install

# 较早的发布列车
mvn clean install -Pspring-cloud-2022
mvn clean install -Pspring-cloud-2023
mvn clean install -Pspring-cloud-2024

# Spring Boot 4.0 / Spring Cloud 2025.1
mvn clean install -Pspring-cloud-2025.1

# 查看当前激活的 profile 以及最终版本
mvn help:active-profiles
mvn dependency:tree -Pspring-cloud-2023 | grep spring-cloud
```

需要注意的 Maven 语义：

- 只要命令行或其他激活条件触发了**任意** profile，`activeByDefault` 就会失效，因此
  `-Pspring-cloud-2022` 会完全替代默认 profile。
- 同时激活两个 Spring Cloud profile（`-Pspring-cloud-2022,spring-cloud-2024`）在语法上合法但效果
  不确定：最后声明 `spring-boot.version` 的 profile 生效。请只激活一个。

profile 只改变**被管理的版本**。已发布的 `0.2.26` jar 只构建一次，可在整个
3.0.x–4.0.x / 2022.0.x–2025.1.x 范围内运行，因为 [commons](./commons.md) 和
[openfeign](./openfeign.md) 中的每个自动配置都做了类存在性守卫
（`@ConditionalOnClass(name = …)`），当可选组件（Eureka、Nacos、Consul、Zookeeper、Kubernetes、
Feign、Tomcat）缺失时会优雅降级。

## `testcontainers` Profile

注册中心/服务发现的集成测试由系统属性把关，普通构建会被跳过：

```java
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
@EnabledIfDockerAvailable
class MultipleServiceRegistryIntegrationTest { … }
```

需要运行它们时（要求本机有 Docker）：

```bash
mvn clean test -Ptestcontainers
# 该 profile 为 surefire 设置 -Dtestcontainers.enabled=true
```

## 父 POM 还是 BOM？

| 场景 | 选择 |
|---|---|
| 新的服务/库，对 Maven 父 POM 没有特别要求 | `microsphere-spring-cloud-parent`（本模块） |
| 工程已继承 `spring-boot-starter-parent` 或公司统一父 POM | `microsphere-spring-cloud-dependencies`（import 作用域） |
| 既要固定 Spring Boot/Spring Cloud 版本，又要引入 Microsphere 制品 | 二者皆可——父 POM 导入的正是同一批 BOM，BOM 也继承了它们 |
| 多模块 reactor，自身已有依赖管理 | 用 BOM，且只在根 POM 中导入 |

BOM 只是本父 POM 的一个薄壳子模块，确切的导入片段与被管理制品清单见
[dependencies.md](./dependencies.md)。

## 注意事项

- **不要把父 POM 写进 `<dependencies>`。** 它是 `pom` 打包，只提供构建配置。
- **不要在一个应用里混用 0.2.x 与 0.1.x 制品。** 两者面向不同代际（`jakarta`/Boot 3+ 与
  `javax`/Boot 2），属性命名空间也不同
  （`spring.cloud.openfeign.client.config.` 与 `feign.client.config.`）。Java 8 / Boot 2 项目请使用
  [1.x 版本线](../../1.x/zh/parent.md)。
- 0.2.x 版本线要求 **Java 17** 字节码。Boot 4.0（profile `spring-cloud-2025.1`）同样以 Java 17 起步，
  但生产采用前请再确认 Spring 官方声明的基线。
- 版本属性插值使用 `${revision}` 配合 flatten 插件，消费者在已发布 POM 中看到的始终是具体版本
  （`0.2.26`）。

## 参见

- [dependencies.md](./dependencies.md) —— BOM 制品
- [commons.md](./commons.md) —— 核心模块能力与属性参考
- [openfeign.md](./openfeign.md) —— Feign 组件热刷新
- [用户文档首页](../../README.md) · [文档总索引](../../../README.md)
