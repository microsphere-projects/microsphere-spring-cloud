[English](../dependencies.md) · [1.x 版本线](../../1.x/zh/dependencies.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-dependencies` 使用指南（0.2.x）

microsphere-spring-cloud **0.2.x** 版本线（`release` 分支）的 BOM（依赖清单）。它是一个 `pom`
制品，唯一职责是发布本仓库所有可运行模块的版本号，使使用者在声明 Microsphere Spring Cloud 依赖时
无需写 `<version>`。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release` |
| 版本线 | 0.2.x |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies` |
| 制品类型 | `pom`（以 `<scope>import</scope>` 方式导入） |
| 最新已发布版本 | **`0.2.26`**（分支头部为 `0.2.27-SNAPSHOT`，未发布） |
| 仓库 | Maven Central |
| 被管理制品的 Java 要求 | **17+** |
| 被管理制品的 Spring Boot 范围 | 3.0.x – 4.0.x |
| 被管理制品的 Spring Cloud 范围 | 2022.0.x – 2025.1.x |
| 父 POM | `microsphere-spring-cloud-parent` 0.2.x（见 [parent.md](./parent.md)） |
| 同时继承的上游 BOM | `microsphere-spring-boot-dependencies` **0.2.34** |

## 它做什么

`microsphere-spring-cloud-dependencies` 继承
[microsphere-spring-cloud-parent](./parent.md)，并在此之上为本仓库构建的模块补充
`dependencyManagement`。导入它可以获得：

1. **Microsphere Spring Cloud 制品的版本对齐** —— 即它自己声明的那两项。
2. **父 POM 中被继承的依赖管理** —— Maven 在导入 BOM 时会计算该 BOM 的*有效* POM，
   因此父 POM 聚合的六个 BOM（`junit-bom`、`testcontainers-bom`、`spring-boot-dependencies`、
   `spring-cloud-dependencies`、`spring-cloud-alibaba-dependencies`、
   `microsphere-spring-boot-dependencies`）同样一并生效。

## 被管理的制品

本 BOM 直接声明：

| 制品 | GroupId | 版本 |
|---|---|---|
| `microsphere-spring-cloud-commons` | `io.github.microsphere-projects` | `${revision}` → `0.2.26` |
| `microsphere-spring-cloud-openfeign` | `io.github.microsphere-projects` | `${revision}` → `0.2.26` |

从父链继承（被管理但非本 BOM 声明）：Spring Boot、Spring Cloud、Spring Cloud Alibaba、
Testcontainers、JUnit 以及 `microsphere-spring-boot-dependencies` 中的全部内容 —— 也包括
`microsphere-spring-cloud-commons` 以 compile 作用域强依赖的
`microsphere-spring-boot-core`、`microsphere-spring-boot-webmvc`、
`microsphere-spring-boot-webflux`、`microsphere-spring-boot-actuator`。

> 该版本线**没有** `microsphere-spring-cloud-starter-*` 制品。请直接依赖各模块，并自行补充第三方
> starter（`spring-cloud-starter-openfeign`、`spring-boot-starter-actuator`、注册中心客户端），
> 因为 Microsphere 模块把这些依赖都声明为 `optional`。

## 导入用法

```xml
<project>
    <!-- 你自己的父 POM 保持不变，例如 spring-boot-starter-parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>

    <properties>
        <microsphere-spring-cloud.version>0.2.26</microsphere-spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Microsphere Spring Cloud BOM -->
            <dependency>
                <groupId>io.github.microsphere-projects</groupId>
                <artifactId>microsphere-spring-cloud-dependencies</artifactId>
                <version>${microsphere-spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- 无需写 <version> -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

验证解析结果：

```bash
mvn -q dependency:tree -Dincludes=io.github.microsphere-projects
mvn -q help:effective-pom | grep -A2 "microsphere-spring-cloud"
```

### 导入顺序规则

Maven 在 `dependencyManagement` 中遵循“先声明者生效”，而 BOM 导入会在其声明位置被展开：

- 如果你要自己管理 Spring Boot / Spring Cloud 版本，请把**你的 BOM 写在
  `microsphere-spring-cloud-dependencies` 之前**，否则平台版本会被 Microsphere 父 POM 内置的
  `spring-boot.version` / `spring-cloud.version` 覆盖。
- 想把某个 Microsphere 制品单独锁定到别的版本，就在你自己的 `dependencyManagement` 中、
  导入语句之前显式声明它——直接声明的条目永远优先于导入的 BOM。

## 何时选 BOM，何时选父 POM

| 需求 | 选择 |
|---|---|
| 工程已继承 `spring-boot-starter-parent` 或公司统一父 POM | **BOM**（import 作用域）——一个 POM 只能有一个父 POM |
| 还想要 Microsphere 的构建插件配置（Java 17 编译、`${revision}` 扁平化、source/javadoc） | **父 POM**（[parent.md](./parent.md)） |
| 构建某个库时希望用 `-P…` profile 切换 Spring Boot/Spring Cloud 发布列车 | **父 POM**——profile 不会随导入的 BOM 进入你的构建 |
| 多模块 reactor：根 POM 统一锁版本，各子模块直接引用 | **BOM**，只在根 POM 导入 |
| 只有一个模块需要 `microsphere-spring-cloud-commons` | **BOM**，甚至可以直接写带版本的依赖 |

经验法则：**应用用 BOM；基于 Microsphere 的库用父 POM。**

## 版本兼容性

BOM 本身只是版本中立的元数据，但它管理的制品有如下要求：

| 要求 | 0.2.x 制品支持情况 |
|---|---|
| Java | 17+ |
| Spring Boot | 3.0.x – 4.0.x（实测矩阵：3.0.13 / 3.3.13 / 3.4.13 / 3.5.15 / 4.0.8） |
| Spring Cloud | 2022.0.x – 2025.1.x（2022.0.5 / 2023.0.6 / 2024.0.3 / 2025.0.3 / 2025.1.3） |
| Spring Cloud Alibaba | 2022.0.0.2 / 2023.0.3.4 / 2025.0.0.0 / 2025.1.0.0 |
| 上游 `microsphere-spring-boot` | 0.2.34（通过父 POM 间接管理） |
| Servlet 命名空间 | `jakarta.*` |

由于 BOM 继承而来的受管版本是按父 POM 的**默认** profile（`spring-cloud-2025` → Boot 3.5.15 /
Cloud 2025.0.3 / Alibaba 2025.0.0.0）计算的，把 BOM 导入一个 Boot 3.0.x 应用完全没问题——但请
**先**导入你自己版本的 `spring-boot-dependencies`，让平台停留在你需要的版本。若要以其他发布
列车构建本仓库，请使用父 POM 的 profile，见 [parent.md](./parent.md)。

## 注意事项

- **不要把 BOM 放进 `<dependencies>`。** 它必须出现在 `<dependencyManagement>` 中并使用
  `<scope>import</scope>`；作为编译依赖它只会解析出一个没有任何类的 POM。
- 导入 BOM **不会**在你的构建中激活父 POM 的 Maven profile。profile 只在构建本仓库时才有意义。
- 被管理的制品大量使用 `optional` 依赖：仅导入 BOM 并依赖
  `microsphere-spring-cloud-commons` 并**不会**把 Spring Cloud、注册中心客户端、Feign 或 Actuator
  带上 classpath，这些 starter 需要你自己声明。
- 一次只使用一条版本线：Spring Boot 2.x / Java 8 请使用
  [1.x BOM](../../1.x/zh/dependencies.md)（`0.1.26`）。

## 参见

- [parent.md](./parent.md) —— 本 BOM 继承的父 POM 与 profile 矩阵
- [commons.md](./commons.md) —— `microsphere-spring-cloud-commons` 的能力
- [openfeign.md](./openfeign.md) —— `microsphere-spring-cloud-openfeign` 的能力
- [用户文档首页](../../README.md)
