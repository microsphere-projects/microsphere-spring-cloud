[中文](./zh/dependencies.md) · [0.2.x version line](../current/dependencies.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-dependencies` — User Guide (0.1.x)

The BOM (bill of materials) of the microsphere-spring-cloud 0.1.x version line. Import it
to pin the versions of `microsphere-spring-cloud-commons` and
`microsphere-spring-cloud-openfeign` without changing your application's own parent.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release-1.x` |
| Version line | 0.1.x |
| Latest published release | **`0.1.26`** (branch head is `0.1.27-SNAPSHOT`) |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies` |
| Repository | Maven Central |
| Java baseline | Java 8 |
| Spring Boot range | 2.3.x – 2.6.x |
| Spring Cloud range | Hoxton.SR12 – 2021.0.x |
| Artifact type | `pom` (`dependencyManagement` only) |

## Managed Artifacts

The BOM manages exactly the two runnable artifacts of this repository:

| GroupId | ArtifactId |
|---|---|
| `io.github.microsphere-projects` | `microsphere-spring-cloud-commons` |
| `io.github.microsphere-projects` | `microsphere-spring-cloud-openfeign` |

Both are pinned to the BOM version (`${revision}` at build time), so importing version
`0.1.26` gives you `0.1.26` of both modules.

## BOM Import Example

```xml
<project>
    <!-- your application keeps its own parent, e.g. spring-boot-starter-parent 2.6.15 -->
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
            <!-- no <version>: managed by the BOM -->
        </dependency>
    </dependencies>
</project>
```

> Note: importing the BOM does **not** add Spring Boot or Spring Cloud dependency
> management to your build — the published BOM only carries the two artifact entries
> above. The full set of third-party BOM imports
> (`spring-boot-dependencies`, `spring-cloud-dependencies`,
> `spring-cloud-alibaba-dependencies`, `microsphere-spring-boot-dependencies`,
> `junit-bom`, `testcontainers-bom`) lives in
> [`microsphere-spring-cloud-parent`](./parent.md), which is a POM *parent*, not an
> importable BOM for those.

## BOM vs Parent: Which One?

| Situation | Use |
|---|---|
| Your project already inherits `spring-boot-starter-parent` (or another parent) | **BOM** — `microsphere-spring-cloud-dependencies` via `<scope>import</scope>` |
| You are building a multi-module company platform on Microsphere Spring Cloud and want Boot/Cloud/Alibaba versions pinned too | **Parent** — `microsphere-spring-cloud-parent` 0.1.x (includes the same two artifact entries plus all imported BOMs and the Spring Cloud profiles) |
| You just need one artifact at a fixed version | Either — declare `io.github.microsphere-projects:<artifact>:0.1.26` directly |

Rule of thumb: **BOM for applications, parent for platforms/libraries that can spare
their `<parent>` slot.**

## Version Compatibility

| Runs with | Supported |
|---|---|
| Java 8 / 11 / 17 | yes |
| Spring Boot 2.3.x – 2.6.x with Spring Cloud Hoxton.SR12 – 2021.0.x | yes |
| Spring Cloud Alibaba 2.2.10 / 2021.1 / 2021.0.6.x | yes |
| Spring Boot 3.x / 4.x (`jakarta.*`) | **no — use the [0.2.x line](../current/dependencies.md)** |

Because the 0.1.x modules guard every auto-configuration with class-presence
conditions, the same BOM-managed jars work across all three Spring Cloud trains listed
in the parent profiles (Hoxton.SR12, 2020.0.x, 2021.0.x).

## Differences vs 0.2.x

| Aspect | 0.1.x (`release-1.x`) | 0.2.x (`release`) |
|---|---|---|
| BOM version to import | `0.1.26` | `0.2.26` |
| Managed artifacts | commons, openfeign | commons, openfeign (identical set) |
| Target platform of managed jars | Boot 2.3–2.6 / Java 8 / `javax.*` | Boot 3.0–4.0 / Java 17+ / `jakarta.*` |
| Companion parent profiles | hoxton / 2020 / 2021 | 2022 / 2023 / 2024 / 2025 / 2025.1 |
| Mixing the two BOMs | **not supported** — pin one version line only | — |
