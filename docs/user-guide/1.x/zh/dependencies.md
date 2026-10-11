[English](../dependencies.md) · [0.2.x 版本线](../../current/zh/dependencies.md) · [用户文档首页](../../README.md)

# `microsphere-spring-cloud-dependencies` — 用户指南（0.1.x）

microsphere-spring-cloud 0.1.x 版本线的 BOM（物料清单）。导入它即可锁定
`microsphere-spring-cloud-commons` 与 `microsphere-spring-cloud-openfeign` 的版本，
而无需更换应用自己的父 POM。

## 版本信息

| 项目 | 值 |
|---|---|
| Git 分支 | `release-1.x` |
| 版本线 | 0.1.x |
| 最新已发布版本 | **`0.1.26`**（分支当前开发版本为 `0.1.27-SNAPSHOT`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies` |
| 仓库 | Maven Central |
| Java 基线 | Java 8 |
| Spring Boot 范围 | 2.3.x – 2.6.x |
| Spring Cloud 范围 | Hoxton.SR12 – 2021.0.x |
| 构件类型 | `pom`（仅含 `dependencyManagement`） |

## 受管理的构件

本 BOM 精确管理本仓库的两个可运行构件：

| GroupId | ArtifactId |
|---|---|
| `io.github.microsphere-projects` | `microsphere-spring-cloud-commons` |
| `io.github.microsphere-projects` | `microsphere-spring-cloud-openfeign` |

两者的版本都与 BOM 版本一致（构建时的 `${revision}`），因此导入版本 `0.1.26`
即可得到两个模块的 `0.1.26`。

## BOM 导入示例

```xml
<project>
    <!-- 应用保留自己的父工程，例如 spring-boot-starter-parent 2.6.15 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.15</version>
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.github.microsphere-projects</groupId>
                <artifactId>microsphere-spring-cloud-dependencies</artifactId>
                <version>0.1.26</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-openfeign</artifactId>
            <!-- 不写 <version>：由 BOM 管理 -->
        </dependency>
    </dependencies>
</project>
```

> 注意：导入 BOM **不会**为你的构建引入 Spring Boot / Spring Cloud 的依赖管理——
> 已发布的 BOM 只包含上表两个构件条目。完整的第三方 BOM 导入集合
> （`spring-boot-dependencies`、`spring-cloud-dependencies`、
> `spring-cloud-alibaba-dependencies`、`microsphere-spring-boot-dependencies`、
> `junit-bom`、`testcontainers-bom`）位于
> [`microsphere-spring-cloud-parent`](./parent.md)，它是 POM *父工程*，
> 而不是这些 BOM 的可导入替身。

## BOM 还是父 POM？

| 场景 | 选择 |
|---|---|
| 项目已继承 `spring-boot-starter-parent`（或其他父工程） | **BOM** —— 以 `<scope>import</scope>` 导入 `microsphere-spring-cloud-dependencies` |
| 你在构建基于 Microsphere Spring Cloud 的多模块公司平台，希望连 Boot/Cloud/Alibaba 版本也一并锁定 | **父 POM** —— `microsphere-spring-cloud-parent` 0.1.x（除两个构件条目外，还包含全部导入的 BOM 与 Spring Cloud profiles） |
| 只需要某一个构件的固定版本 | 两者皆可 —— 直接声明 `io.github.microsphere-projects:<artifact>:0.1.26` |

经验法则：**应用用 BOM，可以腾出 `<parent>` 位置的平台/库工程用父 POM。**

## 版本兼容性

| 运行环境 | 是否支持 |
|---|---|
| Java 8 / 11 / 17 | 支持 |
| Spring Boot 2.3.x – 2.6.x 配合 Spring Cloud Hoxton.SR12 – 2021.0.x | 支持 |
| Spring Cloud Alibaba 2.2.10 / 2021.1 / 2021.0.6.x | 支持 |
| Spring Boot 3.x / 4.x（`jakarta.*`） | **不支持——请使用 [0.2.x 版本线](../../current/zh/dependencies.md)** |

由于 0.1.x 各模块的所有自动配置都带类存在条件保护，同一组由 BOM 管理的 jar
可在父 POM profiles 列出的三个 Spring Cloud 轨道（Hoxton.SR12、2020.0.x、2021.0.x）
下通用。

## 与 0.2.x 的差异

| 方面 | 0.1.x（`release-1.x`） | 0.2.x（`release`） |
|---|---|---|
| 应导入的 BOM 版本 | `0.1.26` | `0.2.26` |
| 受管理构件 | commons、openfeign | commons、openfeign（集合相同） |
| 被管理 jar 的目标平台 | Boot 2.3–2.6 / Java 8 / `javax.*` | Boot 3.0–4.0 / Java 17+ / `jakarta.*` |
| 配套父 POM profiles | hoxton / 2020 / 2021 | 2022 / 2023 / 2024 / 2025 / 2025.1 |
| 混用两个版本线 | **不支持** —— 只锁定一个版本线 | — |
