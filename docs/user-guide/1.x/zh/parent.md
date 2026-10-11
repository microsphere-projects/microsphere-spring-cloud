[English](../parent.md) · [0.2.x 版本线](../../current/zh/parent.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-parent` — 用户指南（0.1.x）

microsphere-spring-cloud 0.1.x 版本线的父 POM（parent POM）。它集中管理
Spring Boot / Spring Cloud / Spring Cloud Alibaba / Microsphere Spring Boot 各 BOM 的
依赖版本，并通过 Maven profile 在受支持的 Spring Cloud 发布轨道之间切换。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release-1.x` |
| 版本线 | 0.1.x |
| 最新已发布版本 | **`0.1.26`**（分支当前开发版本为 `0.1.27-SNAPSHOT`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-parent` |
| 仓库 | Maven Central |
| Java 基线 | **Java 8**（1.x 根 POM 未设置 `java.version`，由构建父工程 `microsphere-build` 0.3.14 提供 Java 8 基线） |
| Servlet 命名空间 | `javax.*` |
| Spring Boot 范围 | 2.3.x – 2.6.x（按 profile） |
| Spring Cloud 范围 | Hoxton.SR12 – 2021.0.x（按 profile） |
| 上游依赖 | `microsphere-spring-boot` **0.1.34** |
| 测试栈 | JUnit Jupiter 5.14.4、Testcontainers 1.21.4 |
| 构件类型 | `pom`（不含代码） |

## 适用场景

以下情况应使用本父 POM：

- 你的应用或库基于 0.1.x 版本线，希望在一处统一锁定 Spring Boot / Spring Cloud /
  Spring Cloud Alibaba 版本；
- 希望通过切换 Maven profile（而不是修改 POM）让同一份代码分别在 Hoxton、2020.x、
  2021.x 下构建与测试；
- 你在构建依赖 `microsphere-spring-cloud-commons` 的同级 Microsphere Spring Cloud
  模块（例如公司内部模块）。

如果你只需要为 Microsphere Spring Cloud 自身构件做版本管理，请改为导入
[dependencies BOM](./dependencies.md)——即使你的应用已经继承了
`spring-boot-starter-parent`，BOM 方式依然可用。

## 继承父 POM

```xml
<project>
    <parent>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-parent</artifactId>
        <version>0.1.26</version>
    </parent>

    <dependencies>
        <!-- 版本来自导入的 BOM，无需写 <version> -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
    </dependencies>
</project>
```

父 POM 的 `dependencyManagement` 按顺序导入以下 BOM：

| 导入的 BOM | 版本属性 |
|---|---|
| `org.junit:junit-bom` | `junit-jupiter.version` = 5.14.4 |
| `org.testcontainers:testcontainers-bom` | `testcontainers.version` = 1.21.4 |
| `org.springframework.boot:spring-boot-dependencies` | `spring-boot.version`（由 profile 设置） |
| `org.springframework.cloud:spring-cloud-dependencies` | `spring-cloud.version`（由 profile 设置） |
| `com.alibaba.cloud:spring-cloud-alibaba-dependencies` | `spring-cloud-alibaba.version`（由 profile 设置） |
| `io.github.microsphere-projects:microsphere-spring-boot-dependencies` | `microsphere-spring-boot.version` = 0.1.34 |

同时以 `${revision}` 管理本仓库两个可运行构件
（`microsphere-spring-cloud-commons`、`microsphere-spring-cloud-openfeign`）。

## Profile 切换

定义了三个 Spring Cloud profile，**`spring-cloud-2021` 默认激活**。

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-hoxton` | 2.3.12.RELEASE | Hoxton.SR12 | 2.2.10 |
| `spring-cloud-2020` | 2.4.13 | 2020.0.5 | 2021.1 |
| `spring-cloud-2021`（**默认**） | 2.6.15 | 2021.0.9 | 2021.0.6.1 |

构建时切换 profile：

```bash
mvn clean install -Pspring-cloud-hoxton
mvn clean install -Pspring-cloud-2020
mvn clean install                    # spring-cloud-2021（默认）
```

说明：

- Hoxton 属于 Ribbon 时代技术栈。在同级 `microsphere-spring-cloud-openfeign` 模块中，
  `spring-cloud-hoxton` profile 会额外引入**测试作用域**的
  `spring-cloud-starter-netflix-ribbon`，使测试运行在 Ribbon 时代的 Feign 技术栈上，
  对使用者无影响。
- profile 只改变受管理的依赖版本。已发布的 0.1.x 构件一次构建，即可在整个
  2.3.x–2.6.x / Hoxton.SR12–2021.0.x 范围内兼容使用；所有自动配置都带类存在条件，
  当可选类（Eureka、Nacos、Consul、Zookeeper、Feign 等）缺失时相应特性自动降级。
- 无论选择哪个 Spring Cloud profile，0.1.x 的 OpenFeign 模块始终监听
  `feign.client.config.` 属性前缀（Hoxton 时代的属性命名空间，在本版本线的
  2020.x/2021.x 下同样适用）。详见 [OpenFeign 指南](./openfeign.md)。

## Java 8 说明

0.1.x 版本线编译为 **Java 8 字节码**（`maven.compiler.source/target = 8`，继承自
`microsphere-build` 0.3.14），并使用 **`javax.*` Servlet 命名空间**。因此它可以在
Java 8、11、17 运行时上使用，但：

- 在 Spring Boot 2.x 下 Servlet API 仍是 `javax.servlet`，你应用中的
  filter/servlet 也必须是 `javax` 体系的；
- 0.1.x 的任何构件都不要求 Java 8 以上的语言特性——如果你在依赖中看到 record、
  sealed 类型或 `jakarta.*`，说明误引入了 0.2.x 构件。

## 注意事项

- **不要在同一应用中混用 0.1.x 与 0.2.x 构件。** 两个版本线面向不同的 Spring Boot
  代际（`javax`/Boot 2 对 `jakarta`/Boot 3+），属性命名空间也不同
  （如 `feign.client.config.` 对 `spring.cloud.openfeign.client.config.`）。
  请只锁定一个版本线，最好通过本父 POM 或同版本线的 BOM 统一管理。
- 0.1.x 的自动配置注册使用 `META-INF/spring.factories`（Spring Boot 2 机制）。
  jar 中同样附带 `AutoConfiguration.imports` 文件，但 Boot 2.3–2.6 会忽略它。
  这是库内部实现，使用者无需手动注册任何内容。
- `microsphere-spring-cloud-parent` 是构建/依赖层面的便利设施，不是运行期依赖，
  切勿写进 `<dependencies>`。

## 版本兼容性

| 运行环境 | 是否支持 |
|---|---|
| Java 8 / 11 / 17 JVM | 支持 |
| Spring Boot 2.3.x、2.4.x、2.5.x、2.6.x | 支持（见上方 profile） |
| Spring Cloud Hoxton.SR12、2020.0.x、2021.0.x | 支持 |
| Spring Cloud Alibaba 2.2.10、2021.1、2021.0.6.x | 支持 |
| Spring Boot 3.x / 4.x、`jakarta.*` | **不支持——请使用 [0.2.x 版本线](../../current/zh/parent.md)** |

## 与 0.2.x 的差异

| 方面 | 0.1.x（`release-1.x`） | 0.2.x（`release`） |
|---|---|---|
| Java 基线 | 8（经构建父工程 0.3.14） | 17（显式 `java.version`） |
| Servlet 命名空间 | `javax.*` | `jakarta.*` |
| 默认 profile | `spring-cloud-2021`（Boot 2.6.15 / Cloud 2021.0.9 / Alibaba 2021.0.6.1） | `spring-cloud-2025`（Boot 3.5.15 / Cloud 2025.0.3） |
| 其他 profile | `spring-cloud-hoxton`、`spring-cloud-2020` | 2022 / 2023 / 2024 / 2025.1 |
| 上游 `microsphere-spring-boot` | 0.1.34 | 0.2.34 |
| 自动配置注册（实际生效机制） | 以 `spring.factories` 为准（Boot 2） | 以 `AutoConfiguration.imports` 为准（Boot 3） |
| 最新发布版本 | 0.1.26 | 0.2.26 |
